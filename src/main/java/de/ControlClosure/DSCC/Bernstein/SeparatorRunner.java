package de.ControlClosure.DSCC.Bernstein;


import de.ControlClosure.*;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import java.util.function.BooleanSupplier;

public class SeparatorRunner<T extends Vertex>{

    private Semaphore outSepTurn;
    private Semaphore inSepTurn;

    private CompletableFuture<Tuple<Set<T>, Set<T>>> firstFinish;
    private CompletableFuture<Tuple<Set<T>, Set<T>>> secondFinish;

    private Thread outSepThread;
    private Thread inSepThread;

    public Tuple<Set<T>, Set<T>> startProcedures(T r, Graph<T> G, Separator<T> S, double d) {
        outSepTurn = new Semaphore(1);
        inSepTurn = new Semaphore(0);
        firstFinish = new CompletableFuture<>();
        secondFinish = new CompletableFuture<>();

        outSepThread = new Thread(() -> outSeparator(r,G,S,d));
        inSepThread = new Thread(() -> inSeparator(r,G,S,d));

        outSepThread.start();
        inSepThread.start();

        return firstFinish.join();
    }

    public Tuple<Set<T>, Set<T>> finishProcedure() {
        outSepTurn.release();
        inSepTurn.release();
        return secondFinish.join();
    }

    public void cleanup() {
        outSepThread.interrupt();
        inSepThread.interrupt();
    }

    private void outSeparator(T r, Graph<T> G, Separator<T> S, double d) {
        out(
                r,
                G,
                S,
                d,
                this::yieldOutSep,
                () -> {
                    try {
                        outSepTurn.acquire();
                    } catch (InterruptedException ignored) {}
                },
                firstFinish,
                secondFinish);
    }

    private void inSeparator(T r, Graph<T> G, Separator<T> S, double d) {
        in(
                r,
                G,
                S,
                d,
                this::yieldInSep,
                () -> {
                    try {
                        inSepTurn.acquire();
                    } catch (InterruptedException ignored) {}
                },
                firstFinish,
                secondFinish);
    }

    private boolean yieldOutSep() {
        //System.out.println("Yield Out");
        if (!firstFinish.isDone()) { // shouldn't block when calculating the second result
            inSepTurn.release();
            try {
                outSepTurn.acquire();
            } catch (InterruptedException e) {
                return false;
            }
        }
        return true;
    }

    private boolean yieldInSep() {
        //System.out.println("Yield In");
        if (!firstFinish.isDone()) {
            outSepTurn.release();
            try {
                inSepTurn.acquire();
            } catch (InterruptedException e) {
                return false;
            }
        }
        return true;
    }

    public static <T> Tuple<Set<T>, Set<T>> outSeparator(T r, Graph<T> G, Separator<T> S, double d){
        CompletableFuture<Tuple<Set<T>, Set<T>>> result = new CompletableFuture<>();
        out(
                r,
                G,
                S,
                d,
                () -> true,
                () -> {},
                result,
                new CompletableFuture<>());
        return result.join();
    }

    public static <T> Tuple<Set<T>, Set<T>> inSeparator(T r, Graph<T> G, Separator<T> S, double d){
        CompletableFuture<Tuple<Set<T>, Set<T>>> result = new CompletableFuture<>();
        in(
                r,
                G,
                S,
                d,
                () -> true,
                () -> {},
                result,
                new CompletableFuture<>());
        return result.join();
    }


    private static <T> void out(T r, Graph<T> G, Separator<T> S, double d,
                                BooleanSupplier yield,
                                Runnable initialSemaphore,
                                CompletableFuture<Tuple<Set<T>, Set<T>>> firstFinish,
                                CompletableFuture<Tuple<Set<T>, Set<T>>> secondFinish) {
        initialSemaphore.run();

        double factor = (2.0f * Math.log(G.size()) /d);

        // L_i
        Set<T> currentLayer = new HashSet<>();
        // E_j<i L_j \intersect S
        int sum = 0;

        Deque<T> Q = new ArrayDeque<>();
        Q.add(r);

        Set<T> marked = new HashSet<>();
        marked.add(r);


        if (S.contains(r)) {// L_0 only contains r so we skip an iteration
            sum = 1;
        } else { // L_0 starts with r
            currentLayer.add(r);
        }

        if(!yield.getAsBoolean()) return; // Run Out init then run In Init

        while (true) {
            while(!Q.isEmpty()) { // O(L_i)?
                T v = Q.poll();
                for(T t: G.outgoing(v)) {
                    if(!yield.getAsBoolean()) return; // After each edge, switch thread
                    if (!marked.contains(t)) {
                        marked.add(t);
                        currentLayer.add(t);
                        if (!S.contains(t)) { // if t not in S, weight of outgoing edges are 0
                            Q.add(t);
                        }
                    }
                }
            }

            if(!yield.getAsBoolean()) return;

            // L_i \intersect S
            Set<T> layerSeparatorIntersection = new HashSet<>(); // O(L_i)
            for(T v: currentLayer) {
                if (S.contains(v)) {
                    layerSeparatorIntersection.add(v);
                }
            }

            // L_i \intersect S <= min{ E_j<i L_j \intersect S, other } * 2 * log_2 (V(G)) / d
            if (layerSeparatorIntersection.size() <= Math.min(sum, S.size() - sum) * factor) {
                Set<T> S_Sep = layerSeparatorIntersection; // S_Sep = L_i \intersect S
                marked.removeAll(S_Sep); // O(S_Sep)
                Set<T> V_Sep = marked; // V_Sep = U_j<=i L_i \ S_Sep

                if (!firstFinish.complete(new Tuple<>(S_Sep, V_Sep))) {
                    secondFinish.complete(new Tuple<>(S_Sep, V_Sep));
                }
                return;
            } else {
                sum += layerSeparatorIntersection.size();
                Q.addAll(layerSeparatorIntersection); // O(S_Sep)
                currentLayer.clear();
            }
        }
    }


    private static <T> void in(T r, Graph<T> G, Separator<T> S, double d,
                                BooleanSupplier yield,
                                Runnable initialSemaphore,
                                CompletableFuture<Tuple<Set<T>, Set<T>>> firstFinish,
                                CompletableFuture<Tuple<Set<T>, Set<T>>> secondFinish) {
        initialSemaphore.run();

        double factor = (2.0f * Math.log(G.size()) /d);

        // L_i
        Set<T> currentLayer = new HashSet<>();
        // E_j<i L_j \intersect S
        int sum = 0;

        Deque<T> Q = new ArrayDeque<>();
        Q.add(r);

        Set<T> marked = new HashSet<>();
        marked.add(r);


        if (S.contains(r)) {// L_0 only contains r so we skip an iteration
            sum = 1;
        } else { // L_0 starts with r
            currentLayer.add(r);
        }

        if(!yield.getAsBoolean()) return; // Run Out init then run In Init

        while (true) {
            while(!Q.isEmpty()) { // O(L_i)?
                T v = Q.poll();
                for(T s: G.incoming(v)) {
                    if(!yield.getAsBoolean()) return; // After each edge, switch thread
                    if (!marked.contains(s)) {
                        marked.add(s);
                        currentLayer.add(s);
                        if (!S.contains(s)) { // if t not in S, weight of outgoing edges are 0
                            Q.add(s);
                        }
                    }
                }
            }

            if(!yield.getAsBoolean()) return;

            // L_i \intersect S
            Set<T> layerSeparatorIntersection = new HashSet<>(); // O(L_i)
            for(T v: currentLayer) {
                if (S.contains(v)) {
                    layerSeparatorIntersection.add(v);
                }
            }

            // L_i \intersect S <= min{ E_j<i L_j \intersect S, other } * 2 * log_2 (V(G)) / d
            if (layerSeparatorIntersection.size() <= Math.min(sum, S.size() - sum) * factor) {
                Set<T> S_Sep = layerSeparatorIntersection; // S_Sep = L_i \intersect S
                marked.removeAll(S_Sep); // O(S_Sep)
                Set<T> V_Sep = marked; // V_Sep = U_j<=i L_i \ S_Sep

                if (!firstFinish.complete(new Tuple<>(S_Sep, V_Sep))) {
                    secondFinish.complete(new Tuple<>(S_Sep, V_Sep));
                }
                return;
            } else {
                sum += layerSeparatorIntersection.size();
                Q.addAll(layerSeparatorIntersection); // O(S_Sep)
                currentLayer.clear();
            }
        }
    }
}
