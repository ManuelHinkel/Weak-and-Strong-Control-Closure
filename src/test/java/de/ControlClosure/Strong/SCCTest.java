package de.ControlClosure.Strong;

import de.ControlClosure.DSCC.Bernstein.PolylogDSCC;
import de.ControlClosure.DSCC.TarjanDSCC;
import de.ControlClosure.Graph;
import de.ControlClosure.Utils.GraphUtils;
import de.ControlClosure.Vertex;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SCCTest {
    private static  final StrongControlClosure[] SCC = {
            new SCCCubic(),
            new SCCDecrementalSCC(new TarjanDSCC()),
            new SCCDecrementalSCC(new PolylogDSCC()),
    };

    @Test
    public void test(){
        for (StrongControlClosure scc : SCC) {
            System.out.println("Running Implementation: " + scc.getClass().getSimpleName());
            for (int j = ExampleGraphs.graphs.length-1; j < ExampleGraphs.graphs.length; j++) {
                Graph<Vertex> G = ExampleGraphs.graphs[j];
                Set<Vertex> Vp = ExampleGraphs.startVertices(j);
                Set<Vertex> P = ExampleGraphs.predicateVertices(j);

                Set<Vertex> result = scc.scc(G, Vp, P);
                assertEquals(ExampleGraphs.expected(j), result);
            }
        }
    }

    private static  final StrongControlClosureNoPredicate[] SCC_NP = {
            new SCCNoPredicateDecrementalSCC(new TarjanDSCC()),
            new SCCNoPredicateDecrementalSCC(new PolylogDSCC()),
    };

    @Test
    public void testNoPredicate() {
        for (StrongControlClosureNoPredicate scc : SCC_NP) {
            System.out.println("Running Implementation: " + scc.getClass().getSimpleName());
            for (int j = 0; j < ExampleGraphs.graphs.length; j++) {
                Graph<Vertex> G = ExampleGraphs.graphs[j];
                Set<Vertex> Vp = ExampleGraphs.startVertices(j);
                Set<Vertex> F = GraphUtils.finalVertices(ExampleGraphs.predicateVertices(j),G);

                Set<Vertex> result = scc.scc(G, Vp, F);
                assertEquals(ExampleGraphs.expected(j), result);
            }
        }
    }

    @Test
    public void testNoCFG() {
        StrongControlClosureNoPredicate scc = new SCCNoPredicateDecrementalSCC(new TarjanDSCC());
        Graph<Vertex> G = ExampleGraphs.buildNonCFG();
        Set<Vertex> Vp = ExampleGraphs.nonCFGVp();
        Set<Vertex> F = ExampleGraphs.nonCFGF();

        Set<Vertex> result = scc.scc(G, Vp, F);
        Set<Vertex> expected = new HashSet<>(Vp);
        expected.addAll(F);
        assertEquals(expected, result);
    }
}
