package de.ControlClosure.Optimized;

import de.ControlClosure.DataStructuresAndAlgorithms.Triple;

import java.util.*;

/*
 * Optimized implementation of SCCDecrementalSCC using Tarjan's algorithm.
 */
public class SCCOpt {
    private static TarjanList TARJAN = new TarjanList();

    public Set<Integer> scc(GraphF G, Set<Integer> Vp, Set<Integer> F) {
        Triple<GraphF, int[], int[]> res = GraphFUtils.onlyReachableAndNoOutgoingEdges(G,Vp);
        GraphF H = res.first;
        int[] GtoH = res.second;
        int[] HtoG = res.third;

        boolean[] isFinal = new boolean[H.size()];
        for(int v: H.vertices) {
            if (F.contains(HtoG[v])) {
                isFinal[v] = true;
            }
        }

        Set<Integer> X = new HashSet<>();
        for(int vp: Vp) {
            X.add(GtoH[vp]);
        }

        List<SCC> SCCs = new ArrayList<>(H.size() / 2);
        SCC[] sccMap = new SCC[H.size()];

        SCCs.addAll(TARJAN.run(H, H.vertices));
        for(SCC scc: SCCs) {
            for(int v: scc.vertices) {
                sccMap[v] = scc;
            }
        }

        while (!SCCs.isEmpty()) {
            SCC scc = SCCs.remove(SCCs.size()-1);

            Set<Integer> Theta = scc.Theta;
            if (Theta != null && (
                    Theta.size() >= 2
                    || (Theta.size() == 1 && scc.C != null && !scc.C.isEmpty())
                    || (Theta.size() == 1 && scc.size() > 1)
                    || (Theta.size() == 1 && isFinal[scc.first()])
                    || (Theta.size() == 1 && H.outgoing(scc.first()).contains(scc.first()))
            )) {
                X.addAll(scc.B);
                H.deleteOut(scc.B);

                if (scc.C != null) {
                    X.addAll(scc.C);
                    H.deleteOut(scc.C);
                }


                List<SCC> newSCCs = TARJAN.run(H, scc.vertices);
                SCCs.addAll(newSCCs);

                for(SCC newSCC: newSCCs) {
                    for(int v: newSCC.vertices) {
                        sccMap[v] = newSCC;
                    }
                }
            } else {
                if (scc.size() == 1 && X.contains(scc.first())) {
                    Set<Integer> set = new HashSet<>();
                    set.add(scc.first());
                    scc.Theta = set;
                }
                if (scc.Theta != null && !scc.Theta.isEmpty()) {
                    for(int v: scc.vertices) {
                        for(int u: H.incoming(v)) {
                            SCC sccInc = sccMap[u];
                            if (!scc.equals(sccInc)) {
                                if (sccInc.B == null) {
                                    sccInc.B = new HashSet<>();
                                }
                                sccInc.B.add(u);

                                if (sccInc.Theta == null) {
                                    sccInc.Theta = new HashSet<>();
                                }
                                sccInc.Theta.addAll(scc.Theta);
                            }
                        }
                    }
                }

                if (scc.size() > 1
                        || (scc.C != null && !scc.C.isEmpty())
                        || H.outgoing(scc.first()).contains(scc.first())
                        || (!X.contains(scc.first()) && isFinal[scc.first()])) {
                    for(int v: scc.vertices) {
                        for(int u: H.incoming(v)) {
                            SCC sccInc = sccMap[u];
                            if (!scc.equals(sccInc)) {
                                if (sccInc.C == null) {
                                    sccInc.C = new HashSet<>();
                                }
                                sccInc.C.add(u);
                            }
                        }
                    }
                }
            }
        }

        Set<Integer> actualX = new HashSet<>();
        for(int x: X) {
            actualX.add(HtoG[x]);
        }

        return actualX;
    }
}
