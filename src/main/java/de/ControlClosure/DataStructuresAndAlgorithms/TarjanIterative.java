package de.ControlClosure.DataStructuresAndAlgorithms;

import de.ControlClosure.Graph;
import de.ControlClosure.SCC;

import java.util.*;

public class TarjanIterative<T> extends Tarjan<T> {

    @Override
    public List<SCC<T>> run(Graph<T> G) {
        currentIndex = 0;
        number = new HashMap<>();
        lowLink = new HashMap<>();
        stack = new ArrayDeque<>();
        onStack = new HashMap<>();
        stronglyConnectedComponents = new LinkedList<>();
        graph = G;

        for (T v : graph.vertices()) {
            if (!number.containsKey(v)) {
                strongConnect(v);
            }
        }

        return stronglyConnectedComponents;
    }


    private void strongConnect(T start) {
        class Frame {
            T vertex;
            Iterator<T> outgoingIterator;

            Frame(T vertex, Iterator<T> iterator) {
                this.vertex = vertex;
                this.outgoingIterator = iterator;
            }
        }

        Deque<Frame> dfsStack = new ArrayDeque<>();

        number.put(start, currentIndex);
        lowLink.put(start, currentIndex);
        currentIndex++;

        stack.push(start);
        onStack.put(start, true);

        dfsStack.push(new Frame(start, graph.outgoing(start).iterator()));

        while (!dfsStack.isEmpty()) {
            Frame currentFrame = dfsStack.peek();
            T v = currentFrame.vertex;

            if (currentFrame.outgoingIterator.hasNext()) {
                T w = currentFrame.outgoingIterator.next();

                if (!number.containsKey(w)) { // Branch that recursively calls strongConnect on children
                    number.put(w, currentIndex);
                    lowLink.put(w, currentIndex);
                    currentIndex++;

                    stack.push(w);
                    onStack.put(w, true);

                    dfsStack.push(new Frame(w, graph.outgoing(w).iterator()));
                } else if (number.get(w) < number.get(v)) { // Same as in recursive version
                    if (onStack.getOrDefault(w, false)) {
                        lowLink.put(v, Math.min(lowLink.get(v), number.get(w)));
                    }
                }
            } else { // All children processed
                dfsStack.pop();

                // Rest of branch that recursively calls strongConnect on children
                if (!dfsStack.isEmpty()) {
                    Frame parentFrame = dfsStack.peek();
                    T parent = parentFrame.vertex;
                    lowLink.put(parent, Math.min(lowLink.get(parent), lowLink.get(v)));
                }

                // Root of SCC
                if (lowLink.get(v).equals(number.get(v))) {
                    Set<T> scc = new HashSet<>();

                    while (!stack.isEmpty() && number.get(stack.peek()) >= number.get(v)) {
                        T w = stack.pop();
                        onStack.put(w, false);
                        scc.add(w);
                    }

                    stronglyConnectedComponents.add(0, new SCC<>(scc));
                }
            }
        }
    }
}