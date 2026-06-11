package de.ControlClosure.DSCC.Bernstein;

import de.ControlClosure.Graph;
import de.ControlClosure.GraphUtils;
import de.ControlClosure.SCC;
import de.ControlClosure.Vertex;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DSCCTest {
    @Test
    public void testDeletion1() {
        Graph<Vertex> G = G();
        PolylogDSCC dscc = new PolylogDSCC();
        dscc.initialize(G);
        dscc.delete(v[7],v[0]);
    }

    @Test
    public void testDeletion2() {
        Graph<Vertex> G = buildLarge();
        PolylogDSCC dscc = new PolylogDSCC();
        dscc.initialize(G);
        assertTrue(GraphUtils.areSCCs(dscc.sccs.stream().map(SCC::vertices).toList(), G, new HashSet<>()));

        dscc.delete(v[8],v[0]);
        assertTrue(GraphUtils.areSCCs(dscc.sccs.stream().map(SCC::vertices).toList(), G, new HashSet<>()));

        dscc.delete(v[9],v[0]);
        assertTrue(GraphUtils.areSCCs(dscc.sccs.stream().map(SCC::vertices).toList(), G, new HashSet<>()));

        dscc.delete(v[14],v[0]);
        assertTrue(GraphUtils.areSCCs(dscc.sccs.stream().map(SCC::vertices).toList(), G, new HashSet<>()));

        dscc.delete(v[17],v[1]);
        assertTrue(GraphUtils.areSCCs(dscc.sccs.stream().map(SCC::vertices).toList(), G, new HashSet<>()));
    }

    static Graph<Vertex> G() {
        Map<Vertex, List<Vertex>> G = new HashMap<>();
        G.put(v[0], List.of(v[1]));
        G.put(v[1], List.of(v[2], v[6]));
        G.put(v[2], List.of(v[3], v[5]));
        G.put(v[3], List.of(v[4]));
        G.put(v[4], List.of(v[7]));
        G.put(v[5], List.of(v[7]));
        G.put(v[6], List.of(v[7]));
        G.put(v[7], List.of(v[0]));
        return new Graph<>(G);
    }

    static Graph<Vertex> buildLarge() {
        Map<Vertex, List<Vertex>> G = new HashMap<>();
        G.put(v[0], List.of(v[1]));
        G.put(v[1], List.of(v[2],v[3]));
        G.put(v[2], List.of(v[4]));
        G.put(v[3], List.of(v[4],v[12]));
        G.put(v[4], List.of(v[1],v[5]));
        G.put(v[5], List.of(v[6],v[7]));
        G.put(v[6], List.of(v[4],v[10]));
        G.put(v[7],List.of(v[8], v[9]));
        G.put(v[8], List.of(v[0]));
        G.put(v[9],List.of(v[9],v[0]));
        G.put(v[10],List.of(v[11]));
        G.put(v[11],List.of(v[12],v[13]));
        G.put(v[12],List.of(v[10],v[15]));
        G.put(v[13],List.of(v[14]));
        G.put(v[14],List.of(v[0]));
        G.put(v[15],List.of(v[16]));
        G.put(v[16],List.of(v[15],v[17]));
        G.put(v[17],List.of(v[1]));
        return new Graph<>(G);
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
            new Vertex(), //16
            new Vertex(), //17
    };

}
