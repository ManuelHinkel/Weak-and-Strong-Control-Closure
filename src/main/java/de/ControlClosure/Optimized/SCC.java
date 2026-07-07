package de.ControlClosure.Optimized;


public class SCC {
    private static int nextId = 0;
    private final int id;

    public int[] vertices;

    public SCC(int[] vertices) {
        this.id = nextId++;
        this.vertices = vertices;
    }

    public int size() {
        return vertices.length;
    }

    public int first() {
        return vertices[0];
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        return this.id == ((SCC) obj).id;
    }

    @Override
    public String toString() {
        return vertices.toString();
    }
}
