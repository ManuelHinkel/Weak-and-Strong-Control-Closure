package de.ControlClosure.Strong;

import de.ControlClosure.Graph;
import de.ControlClosure.Statistics.DSCCStatistics;
import de.ControlClosure.Utils.GraphUtils;
import de.ControlClosure.Vertex;

import java.util.HashSet;
import java.util.Set;

public class SCCCubic implements StrongControlClosureNoPredicate{
    @Override
    public Set<Vertex> scc(Graph<Vertex> G, Set<Vertex> Vp, Set<Vertex> P) {
        Set<Vertex> VpReachable = GraphUtils.reachableFrom(G, Vp);

        Set<Vertex> X = new HashSet<>(Vp);

        boolean changed = true;
        while (changed) {
            changed = false;

            Set<Vertex> Gamma = GraphUtils.Gamma(G,X,P);

            for(Vertex p: G.vertices()) {
                if (!VpReachable.contains(p)) {
                    continue;
                }
                Set<Vertex> ThetaP = GraphUtils.Theta(G,X,p);

                for(Vertex r: G.outgoing(p)) {
                    Set<Vertex> ThetaR = GraphUtils.Theta(G,X,r);

                    if (ThetaR.size() == 1
                            && !Gamma.contains(r)
                            && (ThetaP.size() >= 2 || Gamma.contains(p) )) {
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
    public Set<Vertex> scc(Graph<Vertex> G, Set<Vertex> Vp, Set<Vertex> P, DSCCStatistics statistics) {
        throw new UnsupportedOperationException();
    }
}
