package de.ControlClosure.Weak;

import de.ControlClosure.Graph;
import de.ControlClosure.Vertex;

import java.util.Set;

public interface WeakControlClosure {
    Set<Vertex> wcc(Graph<Vertex> G, Set<Vertex> Vp);
}
