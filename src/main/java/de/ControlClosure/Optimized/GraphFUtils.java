package de.ControlClosure.Optimized;

import de.ControlClosure.DataStructuresAndAlgorithms.Triple;
import de.ControlClosure.DataStructuresAndAlgorithms.Tuple;
import de.ControlClosure.Graph;
import de.ControlClosure.Vertex;

import java.util.*;

public class GraphFUtils {


    public static boolean[] reachableFrom(
            GraphF G,
            Set<Integer> Vp
    ) {
        boolean[] visited = new boolean[G.size()];
        Deque<Integer> stack = new ArrayDeque<>(Vp);

        while (!stack.isEmpty()) {
            Integer x = stack.pop();
            if (!visited[x]) {
                visited[x] = true;
                for (Integer y : G.outgoing(x)) {
                    stack.push(y);
                }
            }
        }

        return visited;
    }

    public static Triple<GraphF, int[], int[]> onlyReachableAndNoOutgoingEdges(GraphF G, Set<Integer> Vp) {
        boolean[] reachable = reachableFrom(G, Vp);

        int n = G.size();

        int[] newId = new int[n];
        int next = 0;
        for(int v = 0; v < n; v++) {
            if(reachable[v]) {
                newId[v] = next++;
            }
        }

        int[] newIdR = new int[next];
        for(int v = 0; v < n; v++) {
            if (reachable[v]) {
                newIdR[newId[v]] = v;
            }
        }

        List<Integer>[] adj = new List[next];
        for(int i = 0; i < next; i++) {
            adj[i] = new ArrayList<>(2);
        }

        for(int v = 0; v < n; v++) {
            if (!reachable[v] ||Vp.contains(v)) continue;

            for(int w: G.outgoing(v)) {
                if (reachable[w]) {
                    adj[newId[v]].add(newId[w]);
                }
            }
        }

        return new Triple<>(new GraphF(adj), newId, newIdR);
    }

    public static Tuple<GraphF, Map<Vertex, Integer>> convertG(Graph<Vertex> G) {
        Map<Vertex, Integer> map = new HashMap<>();
        int i = 0;
        for(Vertex v: G.vertices()) {
            map.put(v, i++);
        }

        List<Integer>[] adj = new List[G.size()];
        for(Vertex u: G.vertices()) {
            int ui = map.get(u);
            adj[ui] = new ArrayList<>(2);
            for(Vertex v: G.outgoing(u)) {
                adj[ui].add(map.get(v));
            }
        }

        return new Tuple<>(new GraphF(adj), map);

    }

    public static Set<Integer> convertVertexSet(Set<Vertex> Vp, Map<Vertex, Integer> map) {
        Set<Integer> VpI = new HashSet<>();
        for(Vertex v: Vp) {
            VpI.add(map.get(v));
        }
        return VpI;
    }
}
