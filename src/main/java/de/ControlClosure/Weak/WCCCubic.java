package de.ControlClosure.Weak;

import de.ControlClosure.*;
import de.ControlClosure.Statistics.DSCCStatistics;
import de.ControlClosure.Utils.GraphUtils;

import java.util.HashSet;
import java.util.Set;

public class WCCCubic implements WeakControlClosure{
    @Override
    public Set<Vertex> wcc(Graph<Vertex> G, Set<Vertex> Vp) {
        Set<Vertex> VpReachable = GraphUtils.reachableFrom(G, Vp);

        Set<Vertex> X = new HashSet<>(Vp);

        boolean changed = true;
        while (changed) {
            changed = false;

            for(Vertex p: G.vertices()) {
                if (!VpReachable.contains(p)) {
                    continue;
                }
                Set<Vertex> ThetaP = GraphUtils.Theta(G,X,p);

                for(Vertex r: G.outgoing(p)) {
                    Set<Vertex> ThetaR = GraphUtils.Theta(G,X,r);

                    if (ThetaR.size() == 1 && ThetaP.size() >= 2) {
                        changed = X.add(p);
                        break;
                    }
                }
                if (changed) break;
            }
        }

        return X;
    }

    @Override
    public Set<Vertex> wcc(Graph<Vertex> G, Set<Vertex> Vp, DSCCStatistics statistics) {
        throw new UnsupportedOperationException();
    }
}
