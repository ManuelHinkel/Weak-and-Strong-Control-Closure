package de.ControlClosure.Optimized;

import java.util.*;
/*
 * Minimal, optimized implementation of a graph.
 */
public class GraphF {
    public List<Integer>[] adjacencyList;
    public List<Integer>[] reversedAdjacencyList;

    public final int[] vertices;

    public GraphF(List<Integer>[] adjacencyList) {
        this.adjacencyList = adjacencyList;
        computeReversedAdjacencyList();

        vertices = new int[adjacencyList.length];
        for(int i = 0; i < adjacencyList.length; i++) {
            vertices[i] = i;
        }
    }

    public int size() {
        return adjacencyList.length;
    }

    public List<Integer> outgoing(Integer v) {
        return adjacencyList[v];
    }

    public List<Integer> incoming(Integer v) {
        return reversedAdjacencyList[v];
    }


    public void delete(Integer u, Integer v) {
        adjacencyList[u].remove(v);
        reversedAdjacencyList[v].remove(u);
    }

    public void deleteOut(Set<Integer> V) {
        for(Integer v: V) {
            adjacencyList[v].clear();
        }
    }

    protected void computeReversedAdjacencyList() {
        reversedAdjacencyList = new List[adjacencyList.length];
        for(int i = 0; i < adjacencyList.length; i++) {
            reversedAdjacencyList[i] = new ArrayList<>();
        }
        for (int from = 0; from < adjacencyList.length; from++) {
            for(Integer to: adjacencyList[from]) {
                reversedAdjacencyList[to].add(from);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("V: ").append(vertices).append("\n");
        for(Integer v: vertices) {
            s.append(v + ": " + adjacencyList[v] + " " + reversedAdjacencyList[v]).append("\n");
        }
        return s.toString();
    }
}