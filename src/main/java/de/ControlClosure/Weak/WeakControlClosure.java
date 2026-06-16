package de.ControlClosure.Weak;

import de.ControlClosure.Graph;
import de.ControlClosure.Statistics;
import de.ControlClosure.Tuple;
import de.ControlClosure.Vertex;

import java.util.Set;

public interface WeakControlClosure {
    Set<Vertex> wcc(Graph<Vertex> G, Set<Vertex> Vp);

    default Set<Vertex> wcc(Graph<Vertex> G, Set<Vertex> Vp, Statistics statistics) {
        statistics.setNumVertices(G.size());
        statistics.setNumEdges(G.m());

        long startTime = System.currentTimeMillis();
        Set<Vertex> res = wcc(G,Vp);
        long elapsed = System.currentTimeMillis() - startTime;
        statistics.setRunningTimeMS(elapsed);

        return res;
    }
}
