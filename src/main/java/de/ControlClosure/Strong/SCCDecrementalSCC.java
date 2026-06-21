package de.ControlClosure.Strong;

import de.ControlClosure.*;
import de.ControlClosure.DSCC.DecrementalSCC;
import de.ControlClosure.Statistics.DSCCStatistics;
import de.ControlClosure.Utils.GraphUtils;
import de.ControlClosure.Utils.SetUtils;

import java.util.*;

public class SCCDecrementalSCC implements StrongControlClosure{
    private final DecrementalSCC dscc;

    public SCCDecrementalSCC(DecrementalSCC dscc) {
        this.dscc = dscc;
    }


    @Override
    public Set<Vertex> scc(Graph<Vertex> G, Set<Vertex> Vp, Set<Vertex> P) {
        Map<SCC<Vertex>, Set<Vertex>> ThetaHat = new HashMap<>();
        Map<SCC<Vertex>, Set<Vertex>> Boundary = new HashMap<>();
        Map<SCC<Vertex>, Set<Vertex>> CompletePath = new HashMap<>();

        Set<Vertex> X = new HashSet<>(Vp);

        Graph<Vertex> H = GraphUtils.onlyReachableAndNoOutgoingEdges(G, Vp);
        dscc.initialize(H); // E^+(Vp) already removed

        SCC<Vertex> lastMoved = null;
        SCC<Vertex> scc;
        while ((scc = dscc.SCCs().prev(lastMoved)) != null){
            Set<Vertex> ThetaHatSCC = ThetaHat.getOrDefault(scc,new HashSet<>());

            // Currently ThetaHat(scc) stores \hat{\Theta}(R,X \cap R,scc), which means for a single-vertex SCC that
            // contains a vertex in X it is empty, which means we do not need to test and exclude this case to avoid
            // adding a single vertex multiple times to X
            if (ThetaHatSCC.size() >= 2
                    || (ThetaHatSCC.size() == 1 && !CompletePath.getOrDefault(scc,new HashSet<>()).isEmpty()) // direct complete paths
                    || (ThetaHatSCC.size() == 1 && scc.size() > 1) // complete path, bc on cycle
                    || (ThetaHatSCC.size() == 1 && GraphUtils.isFinal(SetUtils.getFirst(Boundary.get(scc)), P, G)) // incomplete predicate
                    || (ThetaHatSCC.size() == 1 && GraphUtils.hasSelfLoop(SetUtils.getFirst(Boundary.get(scc)), G)) ) // complete path, bc incomplete predicate
            { // O(1)
                Set<Vertex> B = Boundary.get(scc); // always exists O(1)
                Set<Vertex> C = CompletePath.getOrDefault(scc,new HashSet<>()); // O(1)

                Set<Vertex> union = new HashSet<>(B);
                union.addAll(C);

                assert Collections.disjoint(X,union);

                X.addAll(union);    // O(X) in total
                dscc.delete(union); // O(T(n)) in total

                // Ensure that it is empty because DSCC might reuse same SCC object
                ThetaHat.put(scc, new HashSet<>());
                Boundary.put(scc, new HashSet<>());
                CompletePath.put(scc, new HashSet<>());
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

                if (scc.size() > 1
                        || !CompletePath.getOrDefault(scc,new HashSet<>()).isEmpty()
                        || (GraphUtils.hasSelfLoop(scc.first(),H)) // In H, vertices in X cannot have selfloops
                        || (!X.contains(scc.first()) && GraphUtils.isFinal(scc.first(), P, G))) {
                    for(Vertex v: scc.vertices()) {
                        for(Vertex u: H.incoming(v)) {
                            SCC<Vertex> sccInc = dscc.scc(u); // O(1)
                            if (!scc.equals(sccInc)) { // O(1)
                                // O(1)
                                if (!CompletePath.containsKey(sccInc)) {
                                    CompletePath.put(sccInc, new LinkedHashSet<>());
                                }
                                CompletePath.get(sccInc).add(u);
                            }
                        }
                    }
                }
                lastMoved=scc;
            }
        }
        return X;
    }

    @Override
    public Set<Vertex> scc(Graph<Vertex> G, Set<Vertex> Vp, Set<Vertex> P, DSCCStatistics statistics) {
        // Statistics
        List<Integer> sccSizes = new ArrayList<>();
        List<Integer> newSCCCounts = new ArrayList<>();
        List<Double> largestNewSCCToCurrentRatios = new ArrayList<>();

        Map<SCC<Vertex>, Set<Vertex>> ThetaHat = new HashMap<>();
        Map<SCC<Vertex>, Set<Vertex>> Boundary = new HashMap<>();
        Map<SCC<Vertex>, Set<Vertex>> CompletePath = new HashMap<>();

        Set<Vertex> X = new HashSet<>(Vp);

        Graph<Vertex> H = GraphUtils.onlyReachableAndNoOutgoingEdges(G, Vp);
        dscc.initialize(H); // E^+(Vp) already removed

        SCC<Vertex> lastMoved = null;
        SCC<Vertex> scc;
        while ((scc = dscc.SCCs().prev(lastMoved)) != null){
            sccSizes.add(scc.size());                                           // Statistics

            Set<Vertex> ThetaHatSCC = ThetaHat.getOrDefault(scc,new HashSet<>());

            // Currently ThetaHat(scc) stores \hat{\Theta}(R,X \cap R,scc), which means for a single-vertex SCC that
            // contains a vertex in X it is empty, which means we do not need to test and exclude this case to avoid
            // adding a single vertex multiple times to X
            if (ThetaHatSCC.size() >= 2
                    || (ThetaHatSCC.size() == 1 && !CompletePath.getOrDefault(scc,new HashSet<>()).isEmpty()) // direct complete paths
                    || (ThetaHatSCC.size() == 1 && scc.size() > 1) // complete path, bc on cycle
                    || (ThetaHatSCC.size() == 1 && GraphUtils.isFinal(SetUtils.getFirst(Boundary.get(scc)), P, G)) // incomplete predicate
                    || (ThetaHatSCC.size() == 1 && GraphUtils.hasSelfLoop(SetUtils.getFirst(Boundary.get(scc)), G)) ) // complete path, bc incomplete predicate
            { // O(1)
                Set<Vertex> B = Boundary.get(scc); // always exists O(1)
                Set<Vertex> C = CompletePath.getOrDefault(scc,new HashSet<>()); // O(1)

                Set<Vertex> union = new HashSet<>(B);
                union.addAll(C);

                assert Collections.disjoint(X,union);

                X.addAll(union);    // O(X) in total

                int sccCountBefore = dscc.sccCount();                           // Statistics
                int sccSize = scc.size();                                       // Statistics (needed bc PolylogDSCC reuses SCC objects -> scc.size() may change in deletion)

                dscc.delete(union); // O(T(n)) in total

                int sccCountAfter = dscc.sccCount();                            // Statistics
                int sccCountDiff = sccCountAfter - sccCountBefore;              // Statistics
                newSCCCounts.add(sccCountDiff);                                 // Statistics
                // Statistics: Determine largest size of newly created SCC
                if (sccCountDiff > 0) {
                    int largest = 0;
                    SCC<Vertex> current = dscc.SCCs().prev(lastMoved);
                    for(int j = 0; j < sccCountDiff+1; j++) { // If one SCC splits into two, then sccCountDiff = 1
                        if (current.size() > largest) {
                            largest = current.size();
                        }
                        current = dscc.SCCs().prev(current);
                    }
                    double ratio = largest / (double) sccSize;
                    largestNewSCCToCurrentRatios.add(ratio);
                }

                // Ensure that it is empty because DSCC might reuse same SCC object
                ThetaHat.put(scc, new HashSet<>());
                Boundary.put(scc, new HashSet<>());
                CompletePath.put(scc, new HashSet<>());
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

                if (scc.size() > 1
                        || !CompletePath.getOrDefault(scc,new HashSet<>()).isEmpty()
                        || (GraphUtils.hasSelfLoop(scc.first(),H)) // In H, vertices in X cannot have selfloops
                        || (!X.contains(scc.first()) && GraphUtils.isFinal(scc.first(), P, G))) {
                    for(Vertex v: scc.vertices()) {
                        for(Vertex u: H.incoming(v)) {
                            SCC<Vertex> sccInc = dscc.scc(u); // O(1)
                            if (!scc.equals(sccInc)) { // O(1)
                                // O(1)
                                if (!CompletePath.containsKey(sccInc)) {
                                    CompletePath.put(sccInc, new LinkedHashSet<>());
                                }
                                CompletePath.get(sccInc).add(u);
                            }
                        }
                    }
                }
                lastMoved=scc;
            }
        }
        // Statistics
        if (statistics != null) {
            statistics.setAvgSCCSize(sccSizes
                    .stream()
                    .mapToInt(Integer::intValue)
                    .average()
                    .orElse(0));
            statistics.setAvgNewSCCCount(newSCCCounts
                    .stream()
                    .mapToInt(Integer::intValue)
                    .average()
                    .orElse(0));
            statistics.setAvgRatioLargestNewToCurrent(largestNewSCCToCurrentRatios
                    .stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0));
        }

        return X;
    }
}
