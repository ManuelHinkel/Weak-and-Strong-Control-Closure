package de.ControlClosure.DSCC;

import de.ControlClosure.Graph;
import de.ControlClosure.SCC;
import de.ControlClosure.Vertex;

import java.util.List;
import java.util.Set;

public interface DecrementalSCC {
    void initialize(Graph<Vertex> G);

    void delete(Vertex u, Vertex v);

    void delete(Set<Vertex> Vp);

    int sccCount();

    List<SCC<Vertex>> SCCs();

    SCC<Vertex> sigma(int index);

    default SCC<Vertex> sigmaRev(int index) {
        assert index >= 0 && index < sccCount();
        return sigma((sccCount() - 1) - index);
    }

    SCC<Vertex> scc(Vertex v);
}
