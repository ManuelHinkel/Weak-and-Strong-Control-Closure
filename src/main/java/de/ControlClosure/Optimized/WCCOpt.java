package de.ControlClosure.Optimized;

import de.ControlClosure.DataStructuresAndAlgorithms.Triple;

import java.util.*;

public class WCCOpt{

    private static TarjanList TARJAN = new TarjanList();

    public Set<Integer> wcc(GraphF G, Set<Integer> Vp) {
//        Map<SCC, Set<Integer>> ThetaHat = new HashMap<>();
//        Map<SCC, Set<Integer>> Boundary = new HashMap<>();

        Triple<GraphF, int[], int[]> res = GraphFUtils.onlyReachableAndNoOutgoingEdges(G,Vp);
        GraphF H = res.first;
        int[] GtoH = res.second;
        int[] HtoG = res.third;

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

//            if (ThetaHat.containsKey(scc) && ThetaHat.get(scc).size() >= 2) {
            if (scc.Theta != null && scc.Theta.size() >= 2) {
//                Set<Integer> B = Boundary.get(scc);
//                X.addAll(B);
//                H.deleteOut(B);
                X.addAll(scc.B);
                H.deleteOut(scc.B);

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
//                    ThetaHat.put(scc, set);
                    scc.Theta = set;
                }
                if (scc.Theta != null && !scc.Theta.isEmpty()) {
//                if (ThetaHat.containsKey(scc) && !ThetaHat.get(scc).isEmpty()) {
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
//                                if (!Boundary.containsKey(sccInc)) {
//                                    Boundary.put(sccInc, new HashSet<>());
//                                }
//                                Boundary.get(sccInc).add(u);
//
//                                if(!ThetaHat.containsKey(sccInc)) {
//                                    ThetaHat.put(sccInc, new HashSet<>());
//                                }
//                                ThetaHat.get(sccInc).addAll(ThetaHat.get(scc));
                            }
                        }
                    }
                }
            }
//            ThetaHat.remove(scc);
//            Boundary.remove(scc);
        }

        Set<Integer> actualX = new HashSet<>();
        for(int x: X) {
            actualX.add(HtoG[x]);
        }

        return actualX;
    }
}
