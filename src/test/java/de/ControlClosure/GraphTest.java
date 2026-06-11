package de.ControlClosure;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GraphTest {
    private static final Vertex[] v = new Vertex[]{
            new Vertex(), //0
            new Vertex(), //1
            new Vertex(), //2
            new Vertex(), //3
    };

    @Test
    public void testClone() {
        Graph<Vertex> G = G();
        Graph<Vertex> C = G.clone();

        C.delete(v[0],v[1]);
        assertFalse(C.outgoing(v[0]).contains(v[1]));
        assertTrue(G.outgoing(v[0]).contains(v[1]));
    }

    @Test // TODO: find bug
    public void testInduced() {
        Graph<Vertex> G = G();
        Graph<Vertex> I = G.induced(new HashSet<>(List.of(v[0],v[1],v[2])));

        I.delete(v[0],v[1]);
        assertFalse(I.outgoing(v[0]).contains(v[1]));
        assertTrue(G.outgoing(v[0]).contains(v[1]));
    }

    static Graph<Vertex> G() {
        Map<Vertex, List<Vertex>> G = new HashMap<>();
        G.put(v[0], List.of(v[1]));
        G.put(v[1], List.of(v[2] ));
        G.put(v[2], List.of(v[3]));
        G.put(v[3], List.of(v[2]));
        return new Graph<>(G);
    }
}
