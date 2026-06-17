package de.ControlClosure.Strong;

import de.ControlClosure.Graph;
import de.ControlClosure.Statistics;
import de.ControlClosure.Vertex;

import java.util.Set;

public interface StrongControlClosure {
    Set<Vertex> scc(Graph<Vertex> G, Set<Vertex> Vp, Set<Vertex> P);

    default Set<Vertex> scc(Graph<Vertex> G, Set<Vertex> Vp,  Set<Vertex> P, Statistics statistics) {
        statistics.setNumVertices(G.size());
        statistics.setNumEdges(G.m());

        long startTime = System.currentTimeMillis();
        Set<Vertex> res = scc(G,Vp,P);
        long elapsed = System.currentTimeMillis() - startTime;
        statistics.setRunningTimeMS(elapsed);

        return res;
    }
}


