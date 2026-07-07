package de.ControlClosure.Optimized;


import java.util.Set;

public class SCC {
    public int[] vertices;

    public Set<Integer> Theta;
    public Set<Integer> B;

    public SCC(int[] vertices) {
        this.vertices = vertices;
    }

    public int size() {
        return vertices.length;
    }

    public int first() {
        return vertices[0];
    }

    @Override
    public String toString() {
        return vertices.toString();
    }
}
