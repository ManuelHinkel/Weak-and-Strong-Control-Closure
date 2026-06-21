package de.ControlClosure.Weak;

import de.ControlClosure.*;
import de.ControlClosure.DSCC.DecrementalSCC;
import de.ControlClosure.Statistics.DSCCStatistics;
import de.ControlClosure.Utils.GraphUtils;

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

        Graph<Vertex> H = GraphUtils.onlyReachableAndNoOutgoingEdges(G, Vp);
        dscc.initialize(H); // E^+(Vp) already removed

        SCC<Vertex> lastMoved = null;
        SCC<Vertex> scc;
        while ((scc = dscc.SCCs().prev(lastMoved)) != null) {
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
                lastMoved = scc;
            }
        }

        return X;
    }

    @Override
    public Set<Vertex> wcc(Graph<Vertex> G, Set<Vertex> Vp, DSCCStatistics statistics) {
        // Statistics
        List<Integer> sccSizes = new ArrayList<>();
        List<Integer> newSCCCounts = new ArrayList<>();
        List<Double> largestNewSCCToCurrentRatios = new ArrayList<>();

        Map<SCC<Vertex>, Set<Vertex>> ThetaHat = new HashMap<>();
        Map<SCC<Vertex>, Set<Vertex>> Boundary = new HashMap<>();

        Set<Vertex> X = new LinkedHashSet<>(Vp);

        Graph<Vertex> H = GraphUtils.onlyReachableAndNoOutgoingEdges(G, Vp);
        dscc.initialize(H); // E^+(Vp) already removed

        SCC<Vertex> lastMoved = null;
        SCC<Vertex> scc;
        while ((scc = dscc.SCCs().prev(lastMoved)) != null){
            sccSizes.add(scc.size());                                           // Statistics

            if (ThetaHat.getOrDefault(scc, new HashSet<>()).size() >= 2) { // O(1)
                Set<Vertex> B = Boundary.getOrDefault(scc, new HashSet<>()); // O(1)
                assert Collections.disjoint(X,B);
                X.addAll(B);    // O(X) in total

                int sccCountBefore = dscc.sccCount();                           // Statistics
                int sccSize = scc.size();                                       // Statistics (needed bc PolylogDSCC reuses SCC objects -> scc.size() may change in deletion)

                dscc.delete(B); // O(T(n)) in total

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
                lastMoved = scc;
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
