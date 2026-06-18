package de.ControlClosure;

import java.util.Set;

public class SCC<T> extends Vertex {
    protected final Set<T> vertices;

    public SCC(Set<T> vertices) {
        super();
        assert !vertices.isEmpty();
        this.vertices = vertices;
    }

    public Set<T> vertices() {
        return vertices;
    }

    public int size() {
        return vertices.size();
    }

    public T first() {
        return vertices.iterator().next();
    }

    @Override
    public String toString() {
        return vertices().toString();
    }
}
