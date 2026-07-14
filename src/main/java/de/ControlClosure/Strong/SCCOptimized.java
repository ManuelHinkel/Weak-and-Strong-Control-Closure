package de.ControlClosure.Strong;

import de.ControlClosure.DataStructuresAndAlgorithms.Tuple;
import de.ControlClosure.Graph;
import de.ControlClosure.Optimized.GraphF;
import de.ControlClosure.Optimized.GraphFUtils;
import de.ControlClosure.Optimized.SCCOpt;
import de.ControlClosure.Statistics.DSCCStatistics;
import de.ControlClosure.Statistics.Statistics;
import de.ControlClosure.Utils.GraphUtils;
import de.ControlClosure.Vertex;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/*
 * Wrapper of the SCCOpt algorithm used in Main.java
 */
public class SCCOptimized implements StrongControlClosure {
    @Override
    public Set<Vertex> scc(Graph<Vertex> G, Set<Vertex> Vp, Set<Vertex> P) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Vertex> scc(Graph<Vertex> G, Set<Vertex> Vp, Set<Vertex> P, DSCCStatistics statistics) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Vertex> measure(Graph<Vertex> G, Set<Vertex> Vp, Set<Vertex> P, Statistics statistics, boolean sccStats) {
        Set<Vertex> F = GraphUtils.finalVertices(P,G);

        Tuple<GraphF, Map<Vertex, Integer>> converted = GraphFUtils.convertG(G);
        Set<Integer> VpI = GraphFUtils.convertVertexSet(Vp, converted.second);
        Set<Integer> FI = GraphFUtils.convertVertexSet(F, converted.second);

        SCCOpt scc = new SCCOpt();

        long startTime = System.nanoTime();
        Set<Integer> res = scc.scc(converted.first,VpI,FI);
        long elapsed = System.nanoTime() - startTime;
        statistics.setRunningTimeNano(elapsed);

        // Map back from Integer to Vertex for interoperability with other implementations
        Map<Vertex, Integer> map = converted.second;
        Map<Integer, Vertex> mapRev = new HashMap<>();
        for(Vertex v: map.keySet()) {
            mapRev.put(map.get(v), v);
        }

        Set<Vertex> returnRes = new HashSet<>();
        for(Integer x: res) {
            returnRes.add(mapRev.get(x));
        }

        return returnRes;
    }
}
