package de.ControlClosure.DSCC.Bernstein;


import de.ControlClosure.Graph;
import de.ControlClosure.Tuple;
import de.ControlClosure.Vertex;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SeparatorRunnerTest {
    @Test
    public void testParallel1(){
        SeparatorRunner<Vertex> sr = new SeparatorRunner<>();
        Graph<Vertex> G = buildGraph();
        // In Separator should finish first, because v1 doesn't have any incoming edges
        Tuple<Set<Vertex>, Set<Vertex>> res = sr.startProcedures(
                v[1],
                G,
                new Separator<>(new HashSet<>(List.of(v[2], v[3]))),
                1);
        assertTrue(res.first.isEmpty());
        assertEquals(new HashSet<>(List.of(v[1])), res.second);

        // Finish outSeparator
        res = sr.finishProcedure();
        assertTrue(res.first.isEmpty());
        assertEquals(new HashSet<>(List.of(v[1], v[2], v[3], v[4], v[5], v[6], v[7])), res.second);
    }

    @Test
    public void testParallelCleanup(){
        SeparatorRunner<Vertex> sr = new SeparatorRunner<>();
        Graph<Vertex> G = buildGraph();
        // In Separator should finish first, because v1 doesn't have any incoming edges
        Tuple<Set<Vertex>, Set<Vertex>> res = sr.startProcedures(
                v[1],
                G,
                new Separator<>(new HashSet<>(List.of(v[2], v[3]))),
                1);
        assertTrue(res.first.isEmpty());
        assertEquals(new HashSet<>(List.of(v[1])), res.second);

        // Finish outSeparator
        sr.cleanup();
    }

    @Test
    public void testOutSeparatorNoEdge() {
        Tuple<Set<Vertex>, Set<Vertex>> res = SeparatorRunner.outSeparator(v[0], buildGraph(),  new Separator<>(), 1);
        assertTrue(res.first.isEmpty());
        assertEquals(new HashSet<>(List.of(v[0])), res.second);
    }

    @Test
    public void testInSeparatorNoEdge() {
        Tuple<Set<Vertex>, Set<Vertex>> res = SeparatorRunner.inSeparator(v[0], buildGraph(),  new Separator<>(), 1);
        assertTrue(res.first.isEmpty());
        assertEquals(new HashSet<>(List.of(v[0])), res.second);
    }

    // First layer contains v1-v3, second layer contains v4 -> S_Sep: empty, V_Sep: v1-v4
    @Test
    public void testOutSeparator1() {
        Graph<Vertex> G = buildGraph();
        Tuple<Set<Vertex>, Set<Vertex>> res = SeparatorRunner.outSeparator(
                v[1],
                G,
                new Separator<>(new HashSet<>(List.of(v[2], v[3]))),
                1);
        assertTrue(res.first.isEmpty());
        assertEquals(new HashSet<>(List.of(v[1], v[2], v[3], v[4], v[5], v[6], v[7])), res.second);
    }

    // First layer contains v1, second layer contains v2,v3 -> S_Sep: v2,v3, V_Sep: v1
    @Test
    public void testOutSeparator2() {
        Graph<Vertex> G = buildGraph();
        Tuple<Set<Vertex>, Set<Vertex>> res = SeparatorRunner.outSeparator(
                v[1],
                G,
                new Separator<>(new HashSet<>(List.of(v[1], v[2], v[3]))),
                1);
        assertEquals(new HashSet<>(List.of(v[2], v[3])), res.first);
        assertEquals(new HashSet<>(List.of(v[1])), res.second);
    }

    // Test, if second layer is not separator bc d is too large
    @Test
    public void testOutSeparatorMoreLevels() {
        Graph<Vertex> G = buildGraph();
        Tuple<Set<Vertex>, Set<Vertex>> res = SeparatorRunner.outSeparator(
                v[1],
                G,
                new Separator<>(new HashSet<>(List.of(v[1], v[2], v[3]))),
                100);
        assertTrue(res.first.isEmpty());
        assertEquals(new HashSet<>(List.of(v[1], v[2], v[3], v[4], v[5], v[6], v[7])), res.second);
    }

    @Test
    public void testOutSeparator3() {
        Graph<Vertex> G = buildGraph();
        Tuple<Set<Vertex>, Set<Vertex>> res = SeparatorRunner.outSeparator(
                v[1],
                G,
                new Separator<>(new HashSet<>(List.of(v[2], v[3], v[5]))),
                1);
        assertEquals(new HashSet<>(List.of(v[5])), res.first);
        assertEquals(new HashSet<>(List.of(v[1], v[2], v[3], v[4])), res.second);
    }

    @Test
    public void testInSeparator1() {
        Graph<Vertex> G = buildGraph();
        Tuple<Set<Vertex>, Set<Vertex>> res = SeparatorRunner.inSeparator(
                v[7],
                G,
                new Separator<>(new HashSet<>(List.of(v[2], v[5], v[7]))),
                1);
        // L0 = v[7], L1 = v[6] and v[5]; note that this differs from S-distances
        assertEquals(new HashSet<>(List.of(v[5])), res.first);
        assertEquals(new HashSet<>(List.of(v[6],v[7])), res.second);
    }

    @Test
    public void testOutSepNotSatisfiable() {
        Graph<Vertex> G = buildNotSatisfiable();
        Separator<Vertex> S
                = new Separator<>(new HashSet<>(List.of(v[1], v[2], v[3], v[4], v[5], v[6], v[7], v[8], v[9], v[10], v[11])));
        Tuple<Set<Vertex>, Set<Vertex>> res = SeparatorRunner.outSeparator(
                v[0],
                G,
                S,
                1);
        assertEquals(new HashSet<>(List.of(v[11])), res.first);
        // v[11] has S-distance 2 > 1; so it doesnt work
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
            new Vertex(), //10
            new Vertex(), //11
            new Vertex(), //12
            new Vertex(), //13
            new Vertex(), //14
            new Vertex(), //15
    };

    private static Graph<Vertex> buildGraph() {
        Map<Vertex, List<Vertex>> G = new HashMap<>();
        G.put(v[0], List.of());
        G.put(v[1], List.of(v[2], v[3]));
        G.put(v[2], List.of(v[4]));
        G.put(v[3], List.of());
        G.put(v[4], List.of(v[5]));
        G.put(v[5], List.of(v[6]));
        G.put(v[6], List.of(v[7]));
        G.put(v[7], List.of());
        return new Graph<>(G);
    }

    private static Graph<Vertex> buildNotSatisfiable() {
        Map<Vertex, List<Vertex>> G = new HashMap<>();
        G.put(v[0], List.of(v[1]));
        G.put(v[1], List.of(v[2], v[3], v[4], v[5], v[6], v[7], v[8], v[9], v[10])); //9 vertices
        G.put(v[2], List.of(v[11]));
        G.put(v[3], List.of());
        G.put(v[4], List.of());
        G.put(v[5], List.of());
        G.put(v[6], List.of());
        G.put(v[7], List.of());
        G.put(v[8], List.of());
        G.put(v[9], List.of());
        G.put(v[10], List.of());
        G.put(v[11], List.of(v[0],v[2], v[3], v[4], v[5], v[6], v[7], v[8], v[9], v[10], v[12], v[13], v[14], v[15]));
        G.put(v[12], List.of(v[11]));
        G.put(v[13], List.of(v[11]));
        G.put(v[14], List.of(v[11]));
        G.put(v[15], List.of(v[11]));
        return new Graph<>(G);
    }


    @Test
    public void testOutSepNotSatisfiable2() {
        Graph<Vertex> G = buildNotSatisfiable2();
        Separator<Vertex> S =
                new Separator<>(new HashSet<>(List.of(v[1], v[2], v[3], v[4], v[5], v[6])));
        Tuple<Set<Vertex>, Set<Vertex>> res = SeparatorRunner.outSeparator(
                v[0],
                G,
                S,
                1);
        assertEquals(new HashSet<>(List.of(v[6])), res.first);
        // v[4] has S-distance 2 > 1; so it doesnt work
    }
    private static Graph<Vertex> buildNotSatisfiable2() {
        Map<Vertex, List<Vertex>> G = new HashMap<>();
        G.put(v[0], List.of(v[1]));
        G.put(v[1], List.of(v[2],v[3],v[4],v[5])); //9 vertices
        G.put(v[2], List.of(v[6]));
        G.put(v[3], List.of(v[6]));
        G.put(v[4], List.of(v[6]));
        G.put(v[5], List.of(v[6]));
        G.put(v[6], List.of(v[2], v[3], v[4], v[5], v[0]));
        return new Graph<>(G);
    }
}