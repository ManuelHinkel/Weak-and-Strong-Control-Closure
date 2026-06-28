package de.ControlClosure.DataStructuresAndAlgorithms;

import de.ControlClosure.Graph;
import de.ControlClosure.SCC;

import java.util.*;

public class Tarjan<T> {
    protected int currentIndex = 0;

    protected Map<T, Integer> number;
    protected Map<T, Integer> lowLink;
    protected Deque<T> stack ;
    protected Map<T, Boolean> onStack;
    protected List<SCC<T>> stronglyConnectedComponents;

    protected Graph<T> graph;

    public List<SCC<T>> run(Graph<T> G, Set<T> restrictedTo) {
        currentIndex = 0;
        number = new HashMap<>();
        lowLink = new HashMap<>();
        stack = new ArrayDeque<>();
        onStack = new HashMap<>();
        stronglyConnectedComponents = new LinkedList<>();

        graph = G;
        for (T v : restrictedTo) {
            if (!number.containsKey(v)) {
                strongConnect(v, restrictedTo);
            }
            stack.clear();
            onStack.clear();
        }

        return stronglyConnectedComponents;
    }

    private void strongConnect(T v, Set<T> restrictedTo) {
        number.put(v, currentIndex);
        lowLink.put(v, currentIndex);
        currentIndex++;

        stack.push(v);
        onStack.put(v, true);

        for (T w : graph.outgoing(v)) {
            if (!restrictedTo.contains(w)) continue;
            if (!number.containsKey(w)) {
                strongConnect(w, restrictedTo);
                lowLink.put(v, Math.min(lowLink.get(v), lowLink.get(w)));
            } else if (number.get(w) < number.get(v)) {
                if (onStack.getOrDefault(w, false)) {
                    lowLink.put(v, Math.min(lowLink.get(v), number.get(w)));
                }
            }
        }

        // Root of SCC
        if (lowLink.get(v).equals(number.get(v))) {
            Set<T> scc = new HashSet<>();

            while (!stack.isEmpty() && number.get(stack.peek()) >= number.get(v)) {
                T w = stack.pop();

                onStack.put(w, false);
                scc.add(w);
            }

            stronglyConnectedComponents.add(0,new SCC<>(scc));
        }
    }
}
