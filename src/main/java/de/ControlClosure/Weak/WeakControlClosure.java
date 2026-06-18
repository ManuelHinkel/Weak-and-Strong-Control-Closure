package de.ControlClosure.Weak;

import de.ControlClosure.*;

import java.util.Set;

public interface WeakControlClosure {
    Set<Vertex> wcc(Graph<Vertex> G, Set<Vertex> Vp);

    Set<Vertex> wcc(Graph<Vertex> G, Set<Vertex> Vp, DSCCStatistics statistics);

    default Set<Vertex> measure(Graph<Vertex> G, Set<Vertex> Vp, Statistics statistics, boolean sccStats) {
        Set<Vertex> res;
        if (!sccStats) {
            long startTime = System.currentTimeMillis();
            res = wcc(G,Vp);
            long elapsed = System.currentTimeMillis() - startTime;
            statistics.setRunningTimeMS(elapsed);
            return res;
        } else {
            res = wcc(G,Vp, (DSCCStatistics) statistics);
        }
        return res;
    }
}
