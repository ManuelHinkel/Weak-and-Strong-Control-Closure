package de.ControlClosure.DSCC;

import de.ControlClosure.*;
import de.ControlClosure.DataStructuresAndAlgorithms.HashList;
import de.ControlClosure.DataStructuresAndAlgorithms.Tarjan;
import de.ControlClosure.DataStructuresAndAlgorithms.TarjanIterative;

import java.util.*;

public class TarjanDSCC implements DecrementalSCC {
    private static final Tarjan<Vertex> TARJAN = new Tarjan<>();

    private HashList<SCC<Vertex>> sccs;

    private Graph<Vertex> G;

    private Map<Vertex, SCC<Vertex>> sccMap;

    @Override
    public void initialize(Graph<Vertex> G) {
        this.G = G;
        sccMap = new HashMap<>();

        sccs = new HashList<>();
        sccs.addAllLast(TARJAN.run(G, G.vertices()));
        for(SCC<Vertex> scc: sccs) {
            for(Vertex v: scc.vertices()) {
                sccMap.put(v, scc);
            }
        }
    }

    @Override
    public void delete(Vertex u, Vertex v) {
        G.delete(u,v);

        SCC<Vertex> sccu = scc(u);
        SCC<Vertex> sccv = scc(v);

        if (!sccu.equals(sccv)) return;

        List<SCC<Vertex>> newSCCs = TARJAN.run(G, sccu.vertices());

        for(SCC<Vertex> scc: newSCCs) {
            for(Vertex x: scc.vertices()) {
                sccMap.replace(x, scc);
            }
        }

        sccs.replace(sccu, newSCCs);
    }

    @Override
    public void delete(Set<Vertex> Vp) {
        for(Vertex u: Vp) {
            List<Vertex> out = new ArrayList<>(G.outgoing(u));
            for (Vertex v: out) {
                delete(u,v);
            }
        }
    }

    @Override
    public int sccCount() {
        return sccs.size();
    }

    @Override
    public HashList<SCC<Vertex>> SCCs() {
        return sccs;
    }

    @Override
    public SCC<Vertex> scc(Vertex v) {
        assert sccMap.containsKey(v);
        return sccMap.get(v);
    }
}
