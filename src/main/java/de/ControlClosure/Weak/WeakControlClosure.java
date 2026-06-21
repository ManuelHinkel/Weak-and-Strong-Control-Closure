package de.ControlClosure.Weak;

import de.ControlClosure.*;
import de.ControlClosure.Statistics.DSCCStatistics;
import de.ControlClosure.Statistics.Statistics;

import java.util.Set;

public interface WeakControlClosure {
    Set<Vertex> wcc(Graph<Vertex> G, Set<Vertex> Vp);

    Set<Vertex> wcc(Graph<Vertex> G, Set<Vertex> Vp, DSCCStatistics statistics);

    default Set<Vertex> measure(Graph<Vertex> G, Set<Vertex> Vp, Statistics statistics, boolean sccStats) {
        Set<Vertex> res;
        if (!sccStats) {
            long startTime = System.nanoTime();
            res = wcc(G,Vp);
            long elapsed = System.nanoTime() - startTime;
            statistics.setRunningTimeNano(elapsed);
            return res;
        } else {
            res = wcc(G,Vp, (DSCCStatistics) statistics);
        }
        return res;
    }
}
