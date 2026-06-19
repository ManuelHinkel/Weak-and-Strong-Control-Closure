package de.ControlClosure.DSCC;

import de.ControlClosure.Graph;
import de.ControlClosure.HashList;
import de.ControlClosure.SCC;
import de.ControlClosure.Vertex;

import java.util.List;
import java.util.Set;

public interface DecrementalSCC {
    void initialize(Graph<Vertex> G);

    void delete(Vertex u, Vertex v);

    void delete(Set<Vertex> Vp);

    int sccCount();

    HashList<SCC<Vertex>> SCCs();

    SCC<Vertex> scc(Vertex v);
}
