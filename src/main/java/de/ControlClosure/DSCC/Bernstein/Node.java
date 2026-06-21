package de.ControlClosure.DSCC.Bernstein;

import de.ControlClosure.SCC;
import de.ControlClosure.Vertex;

import java.util.Set;

public class Node extends SCC<Vertex> {
    protected Node(Set<Vertex> vertices) {
        super(vertices);
    }
}
