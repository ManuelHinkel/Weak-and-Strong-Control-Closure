package de.ControlClosure;

public class Vertex {
    private static int nextId = 0;
    public final int id;

    public Vertex() {
        this.id = nextId++;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vertex other = (Vertex) obj;
        return this.id == other.id;
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }

    public static void resetID() {
        nextId = 0;
    }
}
