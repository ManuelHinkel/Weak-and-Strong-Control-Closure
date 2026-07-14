package de.ControlClosure.Evaluation;

import de.ControlClosure.Graph;
import de.ControlClosure.Vertex;

import java.util.*;

public class Instance {
    public Graph<Vertex> G;
    public Set<Vertex> Vprime;
    public Set<Vertex> P;

    // only used to compare with Masud's algorithm
    public String name;
    public Set<Vertex> weakControlClosure;
    public Set<Vertex> strongControlClosure;
    public long wccTime = -1;
    public long sccTime = -1;

    @Override
    public String toString() {
        StringBuilder content = new StringBuilder();
        content.append(graphToString(G))
            .append(Parser.V_PRIME_ID).append(": ").append(setToString(Vprime));

        if(P != null) {
            content.append(Parser.P_ID).append(": ").append(setToString(P));
        }
        if (weakControlClosure != null) {
            content.append(Parser.WCC_ID).append(": ").append(setToString(weakControlClosure));
        }
        if (strongControlClosure != null) {
            content.append(Parser.SCC_ID).append(": ").append(setToString(strongControlClosure));
        }
        if (wccTime >= 0) {
            content.append(Parser.WCC_TIME_ID).append(": ").append(wccTime).append(System.lineSeparator());
        }
        if (sccTime >= 0) {
            content.append(Parser.SCC_TIME_ID).append(": ").append(sccTime).append(System.lineSeparator());
        }

        return content.toString();
    }

    private String graphToString(Graph<Vertex> G) {
        List<Vertex> V = new ArrayList<>(G.vertices());
        V.sort(Comparator.comparingInt(Object::hashCode));

        StringBuilder content = new StringBuilder();
        for(Vertex v: V) {
            StringBuilder line = new StringBuilder();
            line.append(v.hashCode()).append(": ");

            List<Vertex> adj = G.outgoing(v).toList();
            adj.sort(Comparator.comparingInt(Object::hashCode));

            for(Vertex t: adj) {
                line.append(t.hashCode()).append(", ");
            }

            int i = line.lastIndexOf(", ");
            if (i != -1) {
                line.replace(i,i+2, "");
            }
            line.append(System.lineSeparator());
            content.append(line);
        }
        return content.toString();
    }

    private String setToString(Set<Vertex> S) {
        StringBuilder line = new StringBuilder();
        for(Vertex v: S) {
            line.append(v.hashCode()).append(", ");
        }
        int i = line.lastIndexOf(", ");
        if (i != -1) {
            line.replace(i,i+2, "");
        }
        line.append(System.lineSeparator());
        return line.toString();
    }
}
