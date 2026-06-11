package de.ControlClosure.Weak;

import de.ControlClosure.DSCC.DecrementalSCC;
import de.ControlClosure.Graph;
import de.ControlClosure.GraphUtils;
import de.ControlClosure.SCC;
import de.ControlClosure.Vertex;

import java.util.*;

public class WCCDecrementalSCC implements WeakControlClosure{
    private final DecrementalSCC dscc;

    public WCCDecrementalSCC(DecrementalSCC dscc) {
        this.dscc = dscc;
    }

    @Override
    public Set<Vertex> wcc(Graph<Vertex> G, Set<Vertex> Vp) {
        Map<SCC<Vertex>, Set<Vertex>> ThetaHat = new HashMap<>();
        Map<SCC<Vertex>, Set<Vertex>> Boundary = new HashMap<>();

        Set<Vertex> X = new LinkedHashSet<>(Vp);

        Graph<Vertex> H = GraphUtils.onlyReachable(G, Vp);

        dscc.initialize(H);
//        System.out.println(H);
//        System.out.println("SCCs " + dscc.SCCs());
        assert GraphUtils.areSCCsTopologicallyOrdered(H, dscc.SCCs());

        dscc.delete(Vp);

        int i = 0;
        while (i < dscc.sccCount()) {
            SCC<Vertex> scc = dscc.sigmaRev(i); // O(1)

            if (ThetaHat.getOrDefault(scc, new HashSet<>()).size() >= 2) { // O(1)
                Set<Vertex> B = Boundary.getOrDefault(scc, new HashSet<>()); // O(1)
                assert Collections.disjoint(X,B);
                X.addAll(B);    // O(X) in total
                dscc.delete(B); // O(T(n)) in total

                // Ensure that it is empty because DSCC might reuse same SCC object
                ThetaHat.put(scc, new HashSet<>());
                Boundary.put(scc, new HashSet<>());
            } else { // Propagate (move from L to R)
                // Currently, ThetaHat(scc) stores \hat{\Theta}(R,X \cap R,scc)
                if (scc.size() == 1 && X.contains(scc.first())) {
                    ThetaHat.put(scc, new LinkedHashSet<>(List.of(scc.first())));
                }
                // Now it stores \hat{\Theta}(R,X,scc), which equals \hat{\Theta}(X',scc), and more importantly,
                // needs to be propagated
                if (!ThetaHat.getOrDefault(scc, new HashSet<>()).isEmpty()) { // O(1)
                    // O(E^in(\sigma(i)))
                    for(Vertex v: scc.vertices()) {
                        for(Vertex u: H.incoming(v)) {
                            SCC<Vertex> sccInc = dscc.scc(u); // O(1)
                            if (!scc.equals(sccInc)) { // O(1)
                                // O(1)
                                if (!Boundary.containsKey(sccInc)) {
                                    Boundary.put(sccInc, new LinkedHashSet<>());
                                }
                                Boundary.get(sccInc).add(u);

                                // O(1)
                                if(!ThetaHat.containsKey(sccInc)) {
                                    ThetaHat.put(sccInc, new LinkedHashSet<>());
                                }
                                ThetaHat.get(sccInc).addAll(ThetaHat.get(scc)); // In practice only adds one
                            }
                        }
                    }
                }
                i++;
            }
        }

        return X;
    }
}
