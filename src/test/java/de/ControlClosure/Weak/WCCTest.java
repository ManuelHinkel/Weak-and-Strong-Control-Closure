package de.ControlClosure.Weak;

import de.ControlClosure.*;
import de.ControlClosure.DSCC.Bernstein.PolylogDSCC;
import de.ControlClosure.DSCC.TarjanDSCC;
import de.ControlClosure.DataStructuresAndAlgorithms.Tuple;
import de.ControlClosure.Evaluation.GraphGenerator;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WCCTest {
    private static  final  WeakControlClosure[] WCC = {
            new WCCCubic(),
            new WCCDecrementalSCC(new TarjanDSCC()),
            new WCCDecrementalSCC(new PolylogDSCC()),
    };

    @Test
    public void test(){
        for (WeakControlClosure wcc : WCC) {
            System.out.println("Running Implementation: " + wcc.getClass().getSimpleName());
            for (int j = 0; j < ExampleGraphs.graphs.length; j++) {
                Graph<Vertex> G = ExampleGraphs.graphs[j];
                Set<Vertex> Vp = ExampleGraphs.startVertices(j);

                Set<Vertex> result = wcc.wcc(G, Vp);

                assertEquals(ExampleGraphs.expected(j), result);
            }
        }
    }

    @Test
    public void testQuadraticTime(){
        Tuple<Graph<Vertex>,Set<Vertex>> res = GraphGenerator.makeQuadraticDSCCGraph(1000);

        Graph<Vertex> G = res.first;
        Set<Vertex> Vp = res.second;

        Set<Vertex> result = new WCCDecrementalSCC(new TarjanDSCC()).wcc(G,Vp);
        assertEquals(G.vertices(), result);
    }
}

