package de.ControlClosure.DSCC;

import de.ControlClosure.Graph;
import de.ControlClosure.SCC;
import de.ControlClosure.Tarjan;
import de.ControlClosure.Vertex;

import java.util.*;

public class TarjanDSCC implements DecrementalSCC {
    private static final Tarjan<Vertex> TARJAN = new Tarjan<>();

    private List<SCC<Vertex>> sccs;

    private Graph<Vertex> G;

    private Map<Vertex, SCC<Vertex>> sccMap;

    @Override
    public void initialize(Graph<Vertex> G) {
        this.G = G;
        sccMap = new HashMap<>();

        sccs = TARJAN.run(G);
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

        Graph<Vertex> Gi = G.induced(sccu.vertices());
        List<SCC<Vertex>> newSCCs = TARJAN.run(Gi);

        for(SCC<Vertex> scc: newSCCs) {
            for(Vertex x: scc.vertices()) {
                sccMap.replace(x, scc);
            }
        }

        int index = sccs.indexOf(sccu);
        sccs.remove(index);
        sccs.addAll(index, newSCCs);
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
    public List<SCC<Vertex>> SCCs() {
        return sccs;
    }

    @Override
    public SCC<Vertex> sigma(int index) {
        assert index >= 0 && index < sccCount();
        return sccs.get(index);
    }

    @Override
    public SCC<Vertex> scc(Vertex v) {
        assert sccMap.containsKey(v);
        return sccMap.get(v);
    }
}
