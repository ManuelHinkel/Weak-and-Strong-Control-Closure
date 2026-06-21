package de.ControlClosure.Strong;

import de.ControlClosure.Graph;
import de.ControlClosure.Statistics.DSCCStatistics;
import de.ControlClosure.Statistics.Statistics;
import de.ControlClosure.Vertex;

import java.util.Set;

public interface StrongControlClosure {
    Set<Vertex> scc(Graph<Vertex> G, Set<Vertex> Vp, Set<Vertex> P);

    Set<Vertex> scc(Graph<Vertex> G, Set<Vertex> Vp, Set<Vertex> P, DSCCStatistics statistics);

    default Set<Vertex> measure(Graph<Vertex> G, Set<Vertex> Vp, Set<Vertex> P, Statistics statistics, boolean sccStats) {
        Set<Vertex> res;
        if (!sccStats) {
            long startTime = System.nanoTime();
            res = scc(G,Vp,P);
            long elapsed = System.nanoTime() - startTime;
            statistics.setRunningTimeNano(elapsed);
            return res;
        } else {
            res = scc(G,Vp,P, (DSCCStatistics) statistics);
        }
        return res;
    }
}


