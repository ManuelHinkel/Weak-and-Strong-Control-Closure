package de.ControlClosure;

import java.util.*;

public class GraphUtils {
    public static <T> Set<T> reachableFrom(
            Graph<T> G,
            Set<T> Vp
    ) {
        Set<T> visited = new HashSet<>();
        Deque<T> stack = new ArrayDeque<>(Vp);

        while (!stack.isEmpty()) {
            T x = stack.pop();
            if (visited.add(x)) {
                for (T y : G.outgoing(x)) {
                    stack.push(y);
                }
            }
        }

        return visited;
    }

    public static <T> Graph<T> onlyReachable(Graph<T> G, Set<T> Vp) {
        Set<T> reachable = reachableFrom(G, Vp);

        Map<T, List<T>> h = new HashMap<>();
        for(T r: reachable) {
            List<T> out = G.outgoing(r);

            List<T> newOut = new ArrayList<>();
            for(T t: out) {
                if (reachable.contains(t)) {
                    newOut.add(t);
                }
            }
            h.put(r,newOut);
        }
        return new Graph<>(h);
    }

    public static <T> Set<T> Theta(
            Graph<T> G,
            Set<T> X,
            T v
    ) {
        Map<T, List<T>> H = new HashMap<>();

        for (T x: G.vertices()) {
            List<T> successors = G.outgoing(x);

            List<T> filtered = new ArrayList<>();
            if (!X.contains(x)) {
                filtered.addAll(successors);
            }
            H.put(x, filtered);
        }

        Set<T> reachable = new HashSet<>();
        Deque<T> stack = new ArrayDeque<>();
        stack.push(v);

        while (!stack.isEmpty()) {
            T current = stack.pop();
            if (reachable.add(current)) {
                for (T next : H.getOrDefault(current, Collections.emptyList())) {
                    stack.push(next);
                }
            }
        }

        Set<T> result = new HashSet<>();
        for (T u: reachable) {
            if (X.contains(u)) {
                result.add(u);
            }
        }

        return result;
    }

    public static <T> Set<T> Gamma(
             Graph<T> G,
             Set<T> Vp,
             Set<T> P) {
        Set<T> predicates = new HashSet<>(P);
        Set<T> X = new HashSet<>(Vp);

        Graph<T> g = G.clone();

        boolean changed = true;
        while (changed) {
            changed = false;

            for(T u: g.vertices()) {
                Set<T> targets = new HashSet<>(g.outgoing(u));
                for(T v: targets) {
                    if (X.contains(v) && !X.contains(u)) {
                        g.delete(u,v);
                        changed = true;
                        if (!g.outgoing(u).isEmpty()) {
                            predicates.remove(u);
                            break;
                        } else if (!predicates.contains(u)) {
                            X.add(u);
                        }
                    }
                }
                if (changed) {
                    break;
                }
            }
        }

        Set<T> out = new HashSet<>(g.vertices());
        out.removeAll(X);
        return out;
    }

    public static <T> boolean isFinal(T v, Set<T> P, Graph<T> G) {
        if (P.contains(v)) { // predicate vertex
            return G.outgoing(v).size() < 2;
        } else { // non-predicate vertex
            return G.outgoing(v).isEmpty();
        }
    }

    public static <T> boolean hasSelfLoop(T v, Graph<T> G) {
        return G.outgoing(v).contains(v);
    }


    public static <T> boolean areSCCs(List<Set<T>> P, Graph<T> G, Set<T> S) {
        Graph<T> H = G.clone();
        for(T s: S) {
            for(T e: G.outgoing(s)) {
                H.delete(s,e);
            }
        }

        List<Set<T>> sccs = new Tarjan<T>().run(H).stream().map(SCC::vertices).toList();

        if (new HashSet<>(sccs).equals(new HashSet<>(P))) {
            return true;
        } else {
            System.out.println("\n ");
            System.out.println(G);
            System.out.println("Expected " + sccs);
            System.out.println("Actual   " + P);
            return false;
        }
    }

    public static <T> List<Tuple<T,T>> strongConnect(Graph<T> G){
        // Order vertices of G arbitrarily
        List<T> V = G.vertices().stream().toList();

        List<Tuple<T,T>> edgesAdded = new ArrayList<>();

        for(int i = 0; i < V.size(); i++) {
            T u = V.get(i);
            T v = V.get((i+1)%V.size());
            if (!G.outgoing(u).contains(v)) {
                G.adjacencyList.get(u).add(v);
                G.reversedAdjacencyList.get(v).add(u);
                edgesAdded.add(new Tuple<>(u,v));
            }
        }
        return edgesAdded;
    }

    public static <T> boolean hasEdgeEntries(Graph<T> G) {
        for(T v: G.vertices()) {
            if (!G.adjacencyList.containsKey(v) || !G.reversedAdjacencyList.containsKey(v)) {
                return false;
            }
        }
        return true;
    }

    public static <T> boolean areSCCsTopologicallyOrdered(Graph<T> G, List<SCC<T>> SCCs) {
        for(T u: G.vertices()) {
            for(T v: G.outgoing(u)) {
                SCC<T> SCCu = SCCs.stream().filter(scc -> scc.vertices().contains(u)).findFirst().get();
                SCC<T> SCCv = SCCs.stream().filter(scc -> scc.vertices().contains(v)).findFirst().get();
                int uIndex = SCCs.indexOf(SCCu);
                int vIndex = SCCs.indexOf(SCCv);

                if (uIndex > vIndex) {
                    System.out.println("Not ordered " + SCCs);
                    return false;
                }
            }
        }
        return true;
    }

    public static Tuple<Graph<Vertex>, Set<Vertex>> makeQuadraticDSCCGraph(int k) {
        Vertex[] V = new Vertex[2 * k];
        for(int i = 0; i < 2*k; i++) {
            V[i] = new Vertex();
        }

        Map<Vertex, List<Vertex>> G = new HashMap<>();
        for(int i = 0; i < 2*(k-1); i+=2) {
            G.put(V[i], List.of(V[1], V[i+2]));
            G.put(V[i+1], List.of(V[i], V[i+3]));
        }
        G.put(V[2*k-2],List.of(V[1]));
        G.put(V[2*k-1],List.of(V[2*k-2]));

        return new Tuple<>(new Graph<>(G), new HashSet<>(List.of(V[2*k-2],V[2*k-1])));
    }
}
