package de.ControlClosure;


import de.ControlClosure.DSCC.Bernstein.Node;

import java.util.*;

public class Graph<T> {
    public Map<T, List<T>> adjacencyList = new HashMap<>();
    public Map<T, List<T>> reversedAdjacencyList = new HashMap<>();
    public Map<T, List<T>> adjacencyListNSL = new HashMap<>();
    public Map<T, List<T>> reversedAdjacencyListNSL = new HashMap<>();

    // To avoid using super
    public Graph() {}

    // O(V + E)
    public Graph(Map<T, List<T>> adjacencyList) {
        cloneAdjacencyList(adjacencyList);
        computeReversedAdjacencyList();
        computeNSL();
    }

    public Set<T> vertices() {
        return adjacencyList.keySet();
    }

    public int size() {
        return adjacencyList.size();
    }

    public boolean isEmpty() {
        return adjacencyList.isEmpty();
    }

    public List<T> outgoing(T v) {
        assert adjacencyList.containsKey(v);
        return adjacencyList.get(v);
    }

    public List<T> incoming(T v) {
        assert reversedAdjacencyList.containsKey(v);
        return reversedAdjacencyList.get(v);
    }

    public List<T> outgoingNSL(T v) {
        assert adjacencyList.containsKey(v);
        assert adjacencyListNSL.containsKey(v);
        return adjacencyListNSL.get(v);
    }

    public List<T> incomingNSL(T v) {
        assert reversedAdjacencyList.containsKey(v);
        assert reversedAdjacencyListNSL.containsKey(v);
        return reversedAdjacencyListNSL.get(v);
    }


    public void delete(T u, T v) {
        assert adjacencyList.containsKey(u);
        assert reversedAdjacencyList.containsKey(v);

        adjacencyList.get(u).remove(v);
        reversedAdjacencyList.get(v).remove(u);

        adjacencyListNSL.get(u).remove(v);
        reversedAdjacencyListNSL.get(v).remove(u);
    }

    public void deleteFromMaps(Set<T> V) { // Only for actually removing the entries in the maps
        for(T v: V) {
            adjacencyList.remove(v);
            reversedAdjacencyList.remove(v);
            adjacencyListNSL.remove(v);
            reversedAdjacencyListNSL.remove(v);
        }
    }

    public void addVertex(T v) {
        adjacencyList.put(v, new ArrayList<>());
        reversedAdjacencyList.put(v, new ArrayList<>());
        adjacencyListNSL.put(v, new ArrayList<>());
        reversedAdjacencyListNSL.put(v, new ArrayList<>());
    }

    public void redirectOut(T origin, T target, T newOrigin) {
        assert adjacencyList.containsKey(origin);
        assert adjacencyList.get(origin).contains(target);
        assert reversedAdjacencyList.containsKey(target);
        assert reversedAdjacencyList.get(target).contains(origin);

        adjacencyList.get(origin).remove(target);
        adjacencyList.get(newOrigin).add(target);

        reversedAdjacencyList.get(target).remove(origin);
        reversedAdjacencyList.get(target).add(newOrigin);

        adjacencyListNSL.get(origin).remove(target);
        reversedAdjacencyListNSL.get(target).remove(origin);
        if (!newOrigin.equals(target)) {
            adjacencyListNSL.get(newOrigin).add(target);
            reversedAdjacencyListNSL.get(target).add(newOrigin);
        }
    }


    public void redirectIn(T origin, T target, T newTarget) {
        assert adjacencyList.containsKey(origin);
        assert adjacencyList.get(origin).contains(target);
        assert reversedAdjacencyList.containsKey(target);
        assert reversedAdjacencyList.get(target).contains(origin);

        adjacencyList.get(origin).remove(target);
        adjacencyList.get(origin).add(newTarget);

        reversedAdjacencyList.get(target).remove(origin);
        reversedAdjacencyList.get(newTarget).add(origin);

        adjacencyListNSL.get(origin).remove(target);
        reversedAdjacencyListNSL.get(target).remove(origin);
        if (!newTarget.equals(origin)){
            adjacencyListNSL.get(origin).add(newTarget);
            reversedAdjacencyListNSL.get(newTarget).add(origin);
        }
    }

    public void redirectSelfLoop(T origin, T target) {
        adjacencyList.get(origin).remove(origin);
        reversedAdjacencyList.get(origin).remove(origin);
        adjacencyList.get(target).add(target);
        reversedAdjacencyList.get(target).add(target);
    }


    public Graph<T> induced(Set<T> restrictedTo) {
        Graph<T> induced = new Graph<>();

        for (T v : restrictedTo) {
            // Forward edges
            List<T> inducedNeighbors =
                    adjacencyList.getOrDefault(v, new ArrayList<>())
                            .stream()
                            .filter(restrictedTo::contains)
                            .toList();

            induced.adjacencyList.put(v, new ArrayList<>(inducedNeighbors));
        }
        induced.computeReversedAdjacencyList();
        induced.computeNSL();

        return induced;
    }

    public Graph<T> clone() {
        return new Graph<>(this.adjacencyList);
    }

    private void cloneAdjacencyList(Map<T, List<T>> adjacencyList) {
        for(T u: adjacencyList.keySet()) {
            this.adjacencyList.put(u, new ArrayList<>(adjacencyList.get(u)));
        }
    }

    protected void computeReversedAdjacencyList() {
        for (T v : adjacencyList.keySet()) {
            reversedAdjacencyList.put(v, new ArrayList<>());
        }

        for (T from : adjacencyList.keySet()) {
            for (T to : adjacencyList.get(from)) {
                reversedAdjacencyList.get(to).add(from);
            }
        }
    }

    protected void computeNSL() {
        for(T v: adjacencyList.keySet()) {
            List<T> targetNSL = new ArrayList<>();
            for(T t: adjacencyList.get(v)) {
                if (!v.equals(t)) {
                    targetNSL.add(t);
                }
            }
            adjacencyListNSL.put(v, targetNSL);
        }

        for(T v: reversedAdjacencyList.keySet()) {
            List<T> originNSL = new ArrayList<>();
            for(T o: reversedAdjacencyList.get(v)) {
                if (!v.equals(o)) {
                    originNSL.add(o);
                }
            }
            reversedAdjacencyListNSL.put(v, originNSL);
        }
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("V: ").append(vertices()).append("\n");
        for(T v: vertices()) {
            s.append(v + ": " + adjacencyList.get(v)).append("\n");
        }
        return s.toString();
    }
}