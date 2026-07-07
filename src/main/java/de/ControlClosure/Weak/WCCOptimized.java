package de.ControlClosure.Weak;

import de.ControlClosure.DataStructuresAndAlgorithms.Tuple;
import de.ControlClosure.Graph;
import de.ControlClosure.Optimized.GraphF;
import de.ControlClosure.Optimized.GraphFUtils;
import de.ControlClosure.Optimized.WCCOpt;
import de.ControlClosure.Statistics.DSCCStatistics;
import de.ControlClosure.Statistics.Statistics;
import de.ControlClosure.Vertex;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// Only for Runtime Measurement of Optimized Implementation
public class WCCOptimized implements WeakControlClosure{
    @Override
    public Set<Vertex> wcc(Graph<Vertex> G, Set<Vertex> Vp) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Vertex> wcc(Graph<Vertex> G, Set<Vertex> Vp, DSCCStatistics statistics) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Vertex> measure(Graph<Vertex> G, Set<Vertex> Vp, Statistics statistics, boolean sccStats) {
        Tuple<GraphF, Map<Vertex, Integer>> converted = GraphFUtils.convertG(G);
        Set<Integer> VpI = GraphFUtils.convertVertexSet(Vp, converted.second);

        WCCOpt wcc = new WCCOpt();

        long startTime = System.nanoTime();
        Set<Integer> res = wcc.wcc(converted.first,VpI);
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
