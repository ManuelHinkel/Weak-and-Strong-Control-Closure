package de.ControlClosure.Strong;

import de.ControlClosure.DSCCStatistics;
import de.ControlClosure.Graph;
import de.ControlClosure.Statistics;
import de.ControlClosure.Vertex;

import java.util.Set;

public interface StrongControlClosure {
    Set<Vertex> scc(Graph<Vertex> G, Set<Vertex> Vp, Set<Vertex> P);

    Set<Vertex> scc(Graph<Vertex> G, Set<Vertex> Vp, Set<Vertex> P, DSCCStatistics statistics);

    default Set<Vertex> measure(Graph<Vertex> G, Set<Vertex> Vp, Set<Vertex> P, Statistics statistics, boolean sccStats) {
        Set<Vertex> res;
        if (!sccStats) {
            long startTime = System.currentTimeMillis();
            res = scc(G,Vp,P);
            long elapsed = System.currentTimeMillis() - startTime;
            statistics.setRunningTimeMS(elapsed);
            return res;
        } else {
            res = scc(G,Vp,P, (DSCCStatistics) statistics);
        }
        return res;
    }
}


