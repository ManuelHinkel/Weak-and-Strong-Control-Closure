package de.ControlClosure.Optimized;

import de.ControlClosure.DataStructuresAndAlgorithms.Tuple;
import de.ControlClosure.Evaluation.GraphGenerator;
import de.ControlClosure.Graph;
import de.ControlClosure.Utils.GraphUtils;
import de.ControlClosure.Vertex;
import de.ControlClosure.Strong.ExampleGraphs;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SCCOptTest {
    @Test
    public void test(){
        SCCOpt scc = new SCCOpt();
        for (int j = 0; j < ExampleGraphs.graphs.length; j++) {
            Graph<Vertex> G = ExampleGraphs.graphs[j];
            Set<Vertex> Vp = ExampleGraphs.startVertices(j);
            Set<Vertex> F = GraphUtils.finalVertices(ExampleGraphs.predicateVertices(j),G);

            Tuple<GraphF, Map<Vertex, Integer>> converted = GraphFUtils.convertG(G);
            Set<Integer> VpI = GraphFUtils.convertVertexSet(Vp, converted.second);
            Set<Integer> FI = GraphFUtils.convertVertexSet(F,converted.second);

            Set<Integer> result = scc.scc(converted.first, VpI, FI);
            Set<Integer> expected = GraphFUtils.convertVertexSet(ExampleGraphs.expected(j), converted.second);

            assertEquals(expected, result);
        }
    }
}
