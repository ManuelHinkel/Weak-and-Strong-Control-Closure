package de.ControlClosure.DSCC.Bernstein;

import de.ControlClosure.SCC;
import de.ControlClosure.SetUtils;
import de.ControlClosure.Vertex;

import java.util.HashSet;
import java.util.Set;

public class Node extends SCC<Vertex> {
    protected Node(Set<Vertex> vertices) {
        super(vertices);
    }

//    @Override
//    public String toString() {
//        return id + ": " + vertices().toString();
//    }
//    @Override
//    public boolean equals(Object obj) {
//        if (this == obj) return true;
//        if (obj == null || getClass() != obj.getClass()) return false;
//        Node other = (Node) obj;
//        if (this.size() == 1 && other.size() == 1) {
//            return this.first().equals(other.first());
//        } else {
//            return this.id == other.id;
//        }
//    }
}
