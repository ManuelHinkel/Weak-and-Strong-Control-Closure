package de.ControlClosure.Optimized;

import de.ControlClosure.DataStructuresAndAlgorithms.Tuple;
import de.ControlClosure.Evaluation.GraphGenerator;
import de.ControlClosure.Graph;
import de.ControlClosure.Vertex;
import de.ControlClosure.Weak.ExampleGraphs;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WCCOptTest {
    @Test
    public void test(){
        WCCOpt wcc = new WCCOpt();
        for (int j = 0; j < ExampleGraphs.graphs.length; j++) {
            Graph<Vertex> G = ExampleGraphs.graphs[j];
            Set<Vertex> Vp = ExampleGraphs.startVertices(j);

            Tuple<GraphF, Map<Vertex, Integer>> converted = GraphFUtils.convertG(G);
            Set<Integer> VpI = GraphFUtils.convertVertexSet(Vp, converted.second);

            Set<Integer> result = wcc.wcc(converted.first, VpI);
            Set<Integer> expected = GraphFUtils.convertVertexSet(ExampleGraphs.expected(j), converted.second);

            assertEquals(expected, result);
        }

    }

    @Test
    public void testLarge(){
        Random r = new Random();

        WCCOpt wcc = new WCCOpt();

        int rep = 3;

        for(int i = 100_000; i <= 200_000; i+= 100_000) {
            long sum = 0;
            for(int j = 0; j < rep; j++) {
                Graph<Vertex> G = GraphGenerator.randomCFG(i,0.75, r);
                Set<Vertex> Vp = GraphGenerator.chooseVprime(G.vertices(), 0.8, r);

                Tuple<GraphF, Map<Vertex, Integer>> converted = GraphFUtils.convertG(G);
                Set<Integer> VpI = GraphFUtils.convertVertexSet(Vp, converted.second);

                long startTime = System.nanoTime();
                Set<Integer> result = wcc.wcc(converted.first, VpI);
                long elapsed = System.nanoTime() - startTime;
                sum += elapsed;
                System.out.println(elapsed / 1_000_000);
            }
            sum /= rep;
            System.out.println("Avg " + sum / 1_000_000 );
        }
    }
}
