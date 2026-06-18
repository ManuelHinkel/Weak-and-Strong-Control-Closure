package de.ControlClosure.DSCC.Bernstein;

import de.ControlClosure.Vertex;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class NodeSeparator extends Separator<Node>{
    private Set<Vertex> verticesInNodes = new HashSet<>();

    public NodeSeparator() {
        super(new LinkedHashSet<>());
    }

    @Override
    public void add(Node s) {
        assert s.size() == 1;
        verticesInNodes.add(s.first());
        S.add(s);
    }

    @Override
    public void addAll(Set<Node> Sp) {
        for(Node n: Sp) {
            assert n.size() == 1;
            verticesInNodes.add(n.first());
        }
        S.addAll(Sp);
    }

    @Override
    public boolean contains(Node s) {
        if (s.size() > 1) return false;
        return verticesInNodes.contains(s.first());
    }


}
