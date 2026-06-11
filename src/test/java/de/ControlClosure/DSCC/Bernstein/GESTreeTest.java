package de.ControlClosure.DSCC.Bernstein;


import de.ControlClosure.Graph;
import de.ControlClosure.Vertex;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GESTreeTest {
    // v[6] unreachable, because no incoming each
    // v[9] unreachable, because too far away
    // parent of v[5] should be v[4], because v[3] has cost one
    @Test
    public void testShortestPathOutTree1() {
        Graph<Vertex> G = buildGraph();
        Separator<Vertex> S = new Separator<>(new HashSet<>(List.of(v[3], v[7], v[8])));

        GESTree<Vertex> gesTree = new GESTree<>(v[0], G, S, 2);

        assertTrue(gesTree.isOutTreeEdge(v[0], v[1]));

        assertEquals(0, gesTree.dist(v[0], v[1]));
        assertEquals(0, gesTree.dist(v[0], v[2]));
        assertEquals(0, gesTree.dist(v[0], v[3]));
        assertEquals(0, gesTree.dist(v[0], v[4]));
        assertEquals(0, gesTree.dist(v[0], v[5]));
        assertEquals(1, gesTree.dist(v[0], v[7]));
        assertEquals(2, gesTree.dist(v[0], v[8]));
        assertEquals(GESTree.INFINITY, gesTree.dist(v[0], v[6]));
        assertEquals(GESTree.INFINITY, gesTree.dist(v[0], v[9]));

        gesTree = new GESTree<>(v[0], G, S, 1);
        assertEquals(GESTree.INFINITY, gesTree.dist(v[0], v[8]));
        assertEquals(GESTree.INFINITY, gesTree.dist(v[0], v[6]));
        assertEquals(GESTree.INFINITY, gesTree.dist(v[0], v[9]));
    }

    @Test
    public void testShortestPathOutTreeDelete1() {
        Graph<Vertex> G = buildGraph();
        Separator<Vertex> S = new Separator<>(new HashSet<>(List.of(v[3], v[7], v[8])));

        GESTree<Vertex> gesTree = new GESTree<>(v[0], G, S, 2);
        assertTrue(gesTree.hasUnreachable());
        gesTree.delete(v[1], v[2]);

        assertEquals(0, gesTree.dist(v[0], v[1]));
        assertEquals(0, gesTree.dist(v[0], v[3]));
        assertEquals(1, gesTree.dist(v[0], v[5]));
        assertEquals(1, gesTree.dist(v[0], v[7]));
        assertEquals(2, gesTree.dist(v[0], v[8]));
        assertEquals(GESTree.INFINITY, gesTree.dist(v[0], v[2]));
        assertEquals(GESTree.INFINITY, gesTree.dist(v[0], v[4]));
        assertEquals(GESTree.INFINITY, gesTree.dist(v[0], v[6]));
        assertEquals(GESTree.INFINITY, gesTree.dist(v[0], v[9]));
    }

    @Test
    public void testShortestPathOutTreeDelete2() {
        Graph<Vertex> G = buildGraph();
        Separator<Vertex> S = new Separator<>(new HashSet<>(List.of(v[3], v[7], v[8])));

        GESTree<Vertex> gesTree = new GESTree<>(v[0], G, S, 2);
        assertTrue(gesTree.hasUnreachable());
        gesTree.delete(v[0], v[1]);

        Set<Vertex> GWihtoutRoot = new HashSet<>(G.vertices());
        GWihtoutRoot.remove(gesTree.root());
        assertEquals(GWihtoutRoot, gesTree.getAllUnreachable());
    }

    @Test
    public void testShortestPathInTree1() {
        Graph<Vertex> G = buildGraph();
        Separator<Vertex> S = new Separator<>(new HashSet<>(List.of(v[0], v[3], v[7], v[8], v[9])));

        GESTree<Vertex> gesTree = new GESTree<>(v[3], G, S, 3);

        assertTrue(gesTree.isInTreeEdge(v[3],v[1]));

        assertEquals(0, gesTree.dist(v[1], v[3]));
        assertEquals(1, gesTree.dist(v[0], v[3]));
        assertEquals(2, gesTree.dist(v[9], v[3]));
        assertEquals(3, gesTree.dist(v[8], v[3]));
        assertEquals(GESTree.INFINITY, gesTree.dist(v[7], v[3]));
        assertEquals(GESTree.INFINITY, gesTree.dist(v[5], v[3]));
        assertEquals(GESTree.INFINITY, gesTree.dist(v[4], v[3]));
        assertEquals(GESTree.INFINITY, gesTree.dist(v[2], v[3]));
    }

    @Test
    public void testShortestPathInTree2() {
        Graph<Vertex> G = buildGraph();
        Separator<Vertex> S = new Separator<>(new HashSet<>(List.of(v[0], v[3], v[7], v[8], v[9])));

        GESTree<Vertex> gesTree = new GESTree<>(v[3], G, S, 3);

        assertTrue(gesTree.isInTreeEdge(v[3],v[1]));

        gesTree.delete(v[1], v[3]);

        assertFalse(gesTree.isInTreeEdge(v[3], v[1]));
        assertEquals(GESTree.INFINITY, gesTree.dist(v[0], v[3]));
        assertEquals(GESTree.INFINITY, gesTree.dist(v[1], v[3]));
        assertEquals(GESTree.INFINITY, gesTree.dist(v[2], v[3]));
        assertEquals(GESTree.INFINITY, gesTree.dist(v[4], v[3]));
        assertEquals(GESTree.INFINITY, gesTree.dist(v[5], v[3]));
        assertEquals(GESTree.INFINITY, gesTree.dist(v[6], v[3]));
        assertEquals(GESTree.INFINITY, gesTree.dist(v[7], v[3]));
    }


    @Test
    public void testSCC() {
        Map<Vertex, List<Vertex>> G = new HashMap<>();
        G.put(v[0], List.of(v[1]));
        G.put(v[1], List.of(v[2]));
        G.put(v[2], List.of(v[0]));

        Separator<Vertex> S = new Separator<>(new HashSet<>(List.of(v[0])));

        GESTree<Vertex> gesTree = new GESTree<>(v[0], new Graph<>(G), S, 0);
        assertTrue(gesTree.hasUnreachable());
        assertEquals(new HashSet<>(List.of(v[1], v[2])), gesTree.getAllUnreachable());

        gesTree = new GESTree<>(v[0], new Graph<>(G), S, 1);
        assertFalse(gesTree.hasUnreachable());
        assertTrue(gesTree.getAllUnreachable().isEmpty());

        S.add(v[1]);
        gesTree = new GESTree<>(v[0], new Graph<>(G), S, 1);
        assertTrue(gesTree.hasUnreachable());
        assertEquals(new HashSet<>(List.of(v[2])), gesTree.getAllUnreachable());

        gesTree = new GESTree<>(v[0], new Graph<>(G), S, 2);
        assertFalse(gesTree.hasUnreachable());
        assertTrue(gesTree.getAllUnreachable().isEmpty());
    }

    @Test
    public void testAugment() {
        Graph<Vertex> G = buildGraph();
        Separator<Vertex> S = new Separator<>(new HashSet<>(List.of(v[3], v[7], v[8])));

        GESTree<Vertex> gesTree = new GESTree<>(v[0], G, S, 3);
        // same distance as in test "testShortestPathOutTree1" for most vertices
        assertEquals(3, gesTree.dist(v[0], v[9]));

        gesTree.augment(new HashSet<>(List.of(v[0])));
        // increase S-distance by 1
        assertEquals(1, gesTree.dist(v[0], v[1]));
        assertEquals(1, gesTree.dist(v[0], v[2]));
        assertEquals(1, gesTree.dist(v[0], v[3]));
        assertEquals(1, gesTree.dist(v[0], v[4]));
        assertEquals(1, gesTree.dist(v[0], v[5]));
        assertEquals(2, gesTree.dist(v[0], v[7]));
        assertEquals(3, gesTree.dist(v[0], v[8]));
        assertEquals(GESTree.INFINITY, gesTree.dist(v[0], v[9]));

        assertEquals(0, gesTree.dist(v[9], v[0]));
        assertEquals(3, gesTree.dist(v[3], v[0]));

        gesTree.augment(new HashSet<>(List.of(v[9])));
        assertEquals(1, gesTree.dist(v[9], v[0]));
        assertEquals(GESTree.INFINITY, gesTree.dist(v[3], v[0]));
    }

    private static final Vertex[] v = new Vertex[]{
            new Vertex(), //0
            new Vertex(), //1
            new Vertex(), //2
            new Vertex(), //3
            new Vertex(), //4
            new Vertex(), //5
            new Vertex(), //6
            new Vertex(), //7
            new Vertex(), //8
            new Vertex(), //9
    };

    private static Graph<Vertex> buildGraph() {
        Map<Vertex, List<Vertex>> G = new HashMap<>();
        G.put(v[0], List.of(v[1]));
        G.put(v[1], List.of(v[2], v[3]));
        G.put(v[2], List.of(v[4]));
        G.put(v[3], List.of(v[5], v[7]));
        G.put(v[4], List.of(v[5]));
        G.put(v[5], List.of());
        G.put(v[6], List.of(v[1]));
        G.put(v[7], List.of(v[8]));
        G.put(v[8], List.of(v[9]));
        G.put(v[9], List.of(v[0]));
        return new Graph<>(G);
    }
}