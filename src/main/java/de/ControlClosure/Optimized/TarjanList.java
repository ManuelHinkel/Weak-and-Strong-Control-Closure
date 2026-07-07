package de.ControlClosure.Optimized;


import java.util.*;

public class TarjanList {
    private int currentIndex = 0;

    private int[] number;
    private int[] lowLink;
    private Deque<Integer> stack = new ArrayDeque<>();
    private boolean[] onStack;
    private List<SCC> stronglyConnectedComponents = new ArrayList<>();

    private GraphF graph;
    private boolean[] restrictedTo;


    public List<SCC> run(GraphF G, int[] restrictedTo) {
        currentIndex = 1;
        number = new int[G.size()];
        lowLink = new int[G.size()];
        stack.clear();
        onStack = new boolean[G.size()];
        stronglyConnectedComponents.clear();


        graph = G;
        this.restrictedTo = new boolean[G.size()];
        for(int v: restrictedTo) {
            this.restrictedTo[v] = true;
        }

        for (int v : restrictedTo) {
            if (number[v] == 0) {
                strongConnect(v);
            }
        }

        Collections.reverse(stronglyConnectedComponents);
        return stronglyConnectedComponents;
    }

    private void strongConnect(int v) {
        number[v] = currentIndex;
        lowLink[v] = currentIndex;
        currentIndex++;

        stack.push(v);
        onStack[v] = true;

        for (int w : graph.outgoing(v)) {
            if (!restrictedTo[w]) continue;
            if (number[w] == 0) {
                strongConnect(w);
                lowLink[v] = Math.min(lowLink[v], lowLink[w]);
            } else if (number[w] < number[v]) {
                if (onStack[w]) {
                    lowLink[v] = Math.min(lowLink[v], number[w]);
                }
            }
        }

        if (lowLink[v] == number[v]) {
            List<Integer> scc = new ArrayList<>();

            while (!stack.isEmpty() && number[stack.peek()] >= number[v]) {
                int w = stack.pop();

                onStack[w] = false;
                scc.add(w);
            }

            // Add at end of Arraylist, later reverse
            stronglyConnectedComponents.add(new SCC(scc.stream().mapToInt(Integer::intValue).toArray()));
        }
    }
}
