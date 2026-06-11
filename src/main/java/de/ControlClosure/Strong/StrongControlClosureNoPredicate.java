package de.ControlClosure.Strong;

import de.ControlClosure.Graph;
import de.ControlClosure.Vertex;

import java.util.Set;

public interface StrongControlClosureNoPredicate extends StrongControlClosure{
    Set<Vertex> scc(Graph<Vertex> G, Set<Vertex> Vp, Set<Vertex> F);
}


