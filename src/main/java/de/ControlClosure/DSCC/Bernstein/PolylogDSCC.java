package de.ControlClosure.DSCC.Bernstein;

import de.ControlClosure.*;
import de.ControlClosure.DSCC.DecrementalSCC;

import java.util.*;

import static de.ControlClosure.DSCC.Bernstein.GESTree.INFINITY;

public class PolylogDSCC implements DecrementalSCC {
    private Graph<Vertex> G;

    public HashList<SCC<Vertex>> sccs;

    private List<Map<Vertex, Node>> vertexNodeMaps;
    private List<CondensationGraph> graphs;
    private List<NodeSeparator> separators;
    private Map<Node, GESTree<Node>> gesTreeM;

    private int alpha;
    private double delta;

    @Override
    public void initialize(Graph<Vertex> G) {
        this.G = G;
        sccs = new HashList<>();
        alpha = MathUtil.lg2f(G.size()) + 1;
        delta = 128.0 * Math.pow(MathUtil.lg2c(G.size()), 2.0);

        vertexNodeMaps = new ArrayList<>();
        graphs = new ArrayList<>();
        separators = new ArrayList<>();
        gesTreeM = new HashMap<>();

        List<Tuple<Vertex,Vertex>> edgesAdded = GraphUtils.strongConnect(G);
        preprocessing(delta);
        for(Tuple<Vertex,Vertex> e: edgesAdded) {
            delete(e.first,e.second);
        }

        assert GraphUtils.areSCCs(sccs.toList().stream().map(SCC::vertices).toList(), G, new HashSet<>());
    }

    private void preprocessing(double delta) {
        for(int i = 0; i <= alpha; i++) {
            vertexNodeMaps.add(new HashMap<>());
            separators.add(new NodeSeparator());
        }

        // S_0
        NodeSeparator S0 = separators.get(0);
        Map<Vertex, Node> SCC0 = vertexNodeMaps.get(0);
        for(Vertex v: G.vertices()) {
            Node n = new Node(new LinkedHashSet<>(List.of(v)));
            S0.add(n);
            SCC0.put(v, n);
        }
        graphs.add(new CondensationGraph(G, SCC0));

        for(int i = 0; i < alpha; i++) {
            CondensationGraph Gi = graphs.get(i);
            NodeSeparator Si = separators.get(i);

            Tuple<Set<Node>, List<Set<Node>>> splitResult = split(Gi, Si, delta/2.0f);
            Set<Node> SSplit = splitResult.first;
            List<Set<Node>> P = splitResult.second;

            assert GraphUtils.areSCCs(P, Gi, SSplit);

            // S_i+1 = S_Split
            separators.get(i+1).addAll(SSplit);

            Map<Vertex, Node> SCCip1 = vertexNodeMaps.get(i+1);

            for(Set<Node> scc: P) {
                Set<Vertex> vertexSet = new LinkedHashSet<>();
                // merge vertices of all nodes to single node
                for(Node n: scc) {
                    vertexSet.addAll(n.vertices());
                }

                Node nodeForSCC = new Node(vertexSet);
                for(Vertex v: nodeForSCC.vertices()) {
                    SCCip1.put(v, nodeForSCC);
                }

                Vertex r = SetUtils.pickRandom(vertexSet);
                Node R = vertexNodeMaps.get(i).get(r);

                GESTree<Node> gesTree = new GESTree<>(R, Gi.induced(scc), Si, delta);
                gesTreeM.put(nodeForSCC, gesTree);
            }
            CondensationGraph Gip1 = new CondensationGraph(G, SCCip1);
            graphs.add(Gip1);
        }

        assert graphs.get(alpha).size() == 1;
        sccs.addLast((SCC<Vertex>) graphs.get(alpha).vertices().toArray()[0]);
    }

    public static <T extends Vertex> Tuple<Set<T>, List<Set<T>>> split(Graph<T> G, Separator<T> S, double d) {
        Set<T> SSplit = new LinkedHashSet<>();
        List<Set<T>> P = new ArrayList<>();
        Graph<T> GPrime = G.clone();

        while (!GPrime.isEmpty()) {
            T r = SetUtils.pickRandom(GPrime.vertices());

            SeparatorRunner<T> sr = new SeparatorRunner<>();
            Tuple<Set<T>, Set<T>> separatorParallelResult
                    = sr.startProcedures(r, GPrime, SetUtils.intersection(S,  GPrime.vertices()), d/16.0);

            if(separatorParallelResult.second.size() <= 2.0f/3.0f * G.size()) {
                sr.cleanup();
            } else {
                separatorParallelResult = sr.finishProcedure();
            }

            if (separatorParallelResult.second.size() <= 2.0f/3.0f * G.size()){
                Tuple<Set<T>, List<Set<T>>> splitRecursion = split(
                        GPrime.induced(separatorParallelResult.second),
                        SetUtils.intersection(S, separatorParallelResult.second),
                        d);

                SSplit.addAll(separatorParallelResult.first);
                for(T separator: separatorParallelResult.first) {
                    P.add(new LinkedHashSet<>(List.of(separator)));
                }

                SSplit.addAll(splitRecursion.first);
                P.addAll(splitRecursion.second);

                Set<T> remaining = new LinkedHashSet<>(GPrime.vertices());
                remaining.removeAll(separatorParallelResult.first);
                remaining.removeAll(separatorParallelResult.second);
                GPrime = GPrime.induced(remaining);
            } else {
                GESTree<T> E = new GESTree<>(r, GPrime, S, d / 2.0);

                while (E.hasUnreachable()) {
                    T v = E.getUnreachable();

                    Tuple<Set<T>, Set<T>> separatorResult;
                    if (E.dist(r, v) > d / 2.0) {
                        separatorResult = SeparatorRunner.inSeparator(v, GPrime, S, d / 4.0);
                    } else { // E.dist(v,r) < d/2
                        separatorResult = SeparatorRunner.outSeparator(v, GPrime, S, d / 4.0);
                    }
                    Set<T> SSep = separatorResult.first;
                    Set<T> VSep = separatorResult.second;

                    assert !VSep.contains(r);
                    assert VSep.contains(v);

                    E.delete(SSep);
                    E.delete(VSep);
                    assert !E.getAllUnreachable().contains(v);

                    SSplit.addAll(SSep);
                    for (T separator : SSep) {
                        P.add(new LinkedHashSet<>(List.of(separator)));
                    }

                    Tuple<Set<T>, List<Set<T>>> splitRecursion
                            = split(G.induced(VSep), SetUtils.intersection(S, VSep), d);

                    SSplit.addAll(splitRecursion.first);
                    P.addAll(splitRecursion.second);
                }
                P.add(E.getAll());
                GPrime = new Graph<>();
            }
        }
        assert GraphUtils.areSCCs(P, G, SSplit);
        return new Tuple<>(SSplit, P);
    }

    @Override
    public void delete(Vertex u, Vertex v) {
        for (int i = 0; i < alpha; i++) {
            Map<Vertex, Node> SCCi = vertexNodeMaps.get(i);
            Node Xu = SCCi.get(u);
            Node Xv = SCCi.get(v);

            Map<Vertex, Node> SCCip1 = vertexNodeMaps.get(i+1);
            Node X = SCCip1.get(u);
            if (X.equals(SCCip1.get(v))) {
                GESTree<Node> E = gesTreeM.get(X);
                gesTreeM.get(X).delete(Xu, Xv);
            }

            graphs.get(i).delete(Xu,Xv);

            Queue<Node> nodesPotentiallyUnreachable = new ArrayDeque<>();
            nodesPotentiallyUnreachable.add(X);

            while (!nodesPotentiallyUnreachable.isEmpty()) {
                X = nodesPotentiallyUnreachable.poll();
                GESTree<Node> E = gesTreeM.get(X);

                assert E != null;
                if (!E.hasUnreachable()) continue;
                Node Y = E.getUnreachable();

                Tuple<Set<Node>, Set<Node>> separatorParallelResult;
                if (E.dist(E.root(), Y) > delta) {
                    separatorParallelResult = SeparatorRunner.inSeparator(
                            Y,
                            E.graph(),
                            SetUtils.intersection(separators.get(i), E.graph().vertices()),
                            delta/2.0);
                } else { // E.dist(Y, E.root()) > delta
                    separatorParallelResult = SeparatorRunner.outSeparator(
                            Y,
                            E.graph(),
                            SetUtils.intersection(separators.get(i), E.graph().vertices()),
                            delta/2.0);
                }

                Set<Node> SSep = separatorParallelResult.first;
                Set<Node> VSep = separatorParallelResult.second;
                assert !VSep.contains(E.root());

                Set<Node> SAug;
                List<Set<Node>> PNew;

                int vertsInVSep = VSep
                        .stream()
                        .mapToInt(Node::size)
                        .sum();

                if (vertsInVSep <= 2.0f/3.0f * X.size()) {
                    E.delete(SSep);
                    E.delete(VSep);

                    Tuple<Set<Node>, List<Set<Node>>> splitResult = split(
                            graphs.get(i).induced(VSep),
                            SetUtils.intersection(separators.get(i), VSep),
                            delta/2.0);

                    SAug = new LinkedHashSet<>(SSep);
                    PNew = new ArrayList<>();
                    for(Node separator: SSep) {
                        PNew.add(new LinkedHashSet<>(List.of(separator)));
                    }
                    SAug.addAll(splitResult.first);
                    PNew.addAll(splitResult.second);

                    nodesPotentiallyUnreachable.add(X);
                } else {
                    nodesPotentiallyUnreachable.clear();
                    gesTreeM.remove(X);

                    Tuple<Set<Node>, List<Set<Node>>> splitResult = split(
                            E.graph(),
                            SetUtils.intersection(separators.get(i), E.graph().vertices()),
                            delta/2.0);
                    SAug = splitResult.first;
                    PNew = splitResult.second;
                }

                for(Set<Node> scc: PNew) {
                    // create a new node
                    Set<Vertex> vertexSet = new LinkedHashSet<>();
                    for(Node n: scc) {
                        vertexSet.addAll(n.vertices());
                    }
                    Node Z = new Node(vertexSet);

                    Node Zprime = splitNode(i+1,Z, u,v);

                    Vertex r = SetUtils.pickRandom(vertexSet);
                    Node R = SCCi.get(r);
                    GESTree<Node> gesTree = new GESTree<>(
                            R,
                            graphs.get(i).induced(scc),
                            separators.get(i),
                            delta);

                    // Normally means Z = X (or what is still in X after previous SplitNode calls)
                    if (Zprime == null) {
                        // Necessary because else-branch deletes E -> X no longer has GES-Tree
                        // Can't just use X as argument as SplitNode(Y\Z) may switch GES-Trees
                        Node W = SCCip1.get(Z.first());
                        gesTreeM.put(W,gesTree);
                    } else if (Zprime.equals(Z)) {
                        gesTreeM.put(Z,gesTree);
                    } else { // Handles Case where SplitNode recursed on Y\Z; switches GES-Trees
                        gesTreeM.put(Zprime,E);
                        gesTreeM.put(X,gesTree);

                        nodesPotentiallyUnreachable.add(Zprime);
                    }
                }
                E.augment(SAug);
                separators.get(i+1).addAll(SAug);
            }

            assert mappingsCorrect(i+1);
        }
        // In G_alpha, the edge was not yet deleted
        Map<Vertex, Node> SCCalpha = vertexNodeMaps.get(alpha);
        graphs.get(alpha).delete(SCCalpha.get(u),SCCalpha.get(v));

        // Finally, also delete it from G
        G.delete(u,v);

        assert GraphUtils.areSCCs(sccs.toList().stream().map(SCC::vertices).toList(),G,new HashSet<>());
        assert GraphUtils.areSCCsTopologicallyOrdered(G,sccs.toList());
    }

    private Node splitNode(int ip1, Node Z, Vertex x, Vertex y) {
        CondensationGraph Gip1 = graphs.get(ip1);
        Map<Vertex, Node> SCCip1 = vertexNodeMaps.get(ip1);

        Vertex v = Z.first();
        Node Y = SCCip1.get(v);
        assert SetUtils.isSubset(Z.vertices(), Y.vertices());

        // Y = Z, should do nothing
        if (Z.size() == Y.size()) {
            return null;
        }

        Set<Vertex> YwOutZVertices = new LinkedHashSet<>(Y.vertices());
        YwOutZVertices.removeAll(Z.vertices());
        if (Z.size() > YwOutZVertices.size()) {
            Node YwOutZ = new Node(YwOutZVertices);
            return splitNode(ip1,YwOutZ,x,y);
        } else {
            Gip1.addVertex(Z);
            if (ip1 < alpha) {
                Map<Vertex, Node> SCCip2 = vertexNodeMaps.get(ip1+1);
                Node above = SCCip2.get(v);
                GESTree<Node> Eip2 = gesTreeM.get(above);

                Eip2.shortestPathOutTreeChildren.put(Z, new ArrayList<>());
                Eip2.shortestPathInTreeChildren.put(Z, new ArrayList<>());
                Eip2.outDist.put(Z, Eip2.lOut(Y));
                Eip2.inDist.put(Z, Eip2.lIn(Y));

                Graph<Node> Gip1E = Eip2.graph();
                Gip1E.addVertex(Z);

                // Redirect Edges and remove from Y
                for(Vertex z: Z.vertices()) {
                    SCCip1.put(z,Z);
                    Y.vertices().remove(z);

                    for(Vertex targetV: G.outgoing(z)) {
                        if (targetV.equals(z)) {
                            Gip1.redirectSelfLoop(Y,Z);
                            Gip1E.redirectSelfLoop(Y,Z);
                        } else {
                            Node targetN = SCCip1.get(targetV);
                            Gip1.redirectOut(Y, targetN, Z);

                            // check needed because GES-tree works on induced graphs
                            if (Gip1E.vertices().contains(targetN)) {
                                Gip1E.redirectOut(Y,targetN,Z);
                                // redirect GES-tree
                                if (Eip2.isOutTreeEdge(Y,targetN)) {
                                    Eip2.shortestPathOutTreeChildren.get(Y).remove(targetN);
                                    Eip2.shortestPathOutTreeChildren.get(Z).add(targetN);
                                    Eip2.shortestPathOutTreeParents.replace(targetN,Z);
                                }
                                if (Eip2.isInTreeEdge(targetN,Y)) {
                                    Eip2.shortestPathInTreeChildren.get(targetN).remove(Y);
                                    Eip2.shortestPathInTreeChildren.get(targetN).add(Z);
                                    Eip2.shortestPathInTreeParents.replace(Y,null);
                                    Eip2.shortestPathInTreeParents.put(Z,targetN);
                                }
                            }
                        }
                    }

                    for(Vertex originV: G.incoming(z)) {
                        if (originV.equals(z)) continue;

                        Node originN = SCCip1.get(originV);
                        Gip1.redirectIn(originN, Y, Z);

                        // check needed because GES-tree works on induced graphs
                        if (Gip1E.vertices().contains(originN)) {
                            Gip1E.redirectIn(originN,Y,Z);
                            // redirect GES-tree
                            if (Eip2.isInTreeEdge(Y,originN)) {
                                Eip2.shortestPathInTreeChildren.get(Y).remove(originN);
                                Eip2.shortestPathInTreeChildren.get(Z).add(originN);
                                Eip2.shortestPathInTreeParents.replace(originN,Z);
                            }
                            if (Eip2.isOutTreeEdge(originN,Y)) {
                                Eip2.shortestPathOutTreeChildren.get(originN).remove(Y);
                                Eip2.shortestPathOutTreeChildren.get(originN).add(Z);
                                Eip2.shortestPathOutTreeParents.replace(Y,null);
                                Eip2.shortestPathOutTreeParents.put(Z,originN);
                            }
                        }
                    }
                }


                if(Eip2.lOut(Y) == INFINITY) { // Y was already disconnected in T_Out
                    Eip2.unreachableVerticesOutTree.add(Z);
                } else {
                    if (Eip2.shortestPathOutTreeParents.get(Y) == null) {
                        Eip2.Q_out.addFirst(new Tuple<>(Y, Eip2.lOut(Y)));
                    }
                    if (Eip2.shortestPathOutTreeParents.get(Z) == null) {
                        Eip2.Q_out.addFirst(new Tuple<>(Z, Eip2.lOut(Z)));
                    }
                    Eip2.fixOutTree();
                }

                if (Eip2.lIn(Y) == INFINITY) {// Y was already disconnected in T_In
                    Eip2.unreachableVerticesInTree.add(Z);
                } else {
                    if (Eip2.shortestPathInTreeParents.get(Y) == null) {
                        Eip2.Q_in.addFirst(new Tuple<>(Y, Eip2.lIn(Y)));
                    }
                    if (Eip2.shortestPathInTreeParents.get(Z) == null) {
                        Eip2.Q_in.addFirst(new Tuple<>(Z, Eip2.lIn(Z)));
                    }
                    Eip2.fixInTree();
                }

            } else { // Maintain topological ordering of SCCs
                boolean hasEdgeToY = false;
                for(Vertex z: Z.vertices()) {
                    SCCip1.put(z,Z);
                    Y.vertices().remove(z);

                    for(Vertex targetV: G.outgoing(z)) {
                        if (targetV.equals(z)) {
                            Gip1.redirectSelfLoop(Y,Z);
                        } else {
                            Node targetN = SCCip1.get(targetV);

                            if (targetN.equals(Y)
                                    // Edge is only deleted afterward in G_i+1, so we need to exclude it here
                                    && (!z.equals(x) && !targetV.equals(y))
                                    // Hidden Self Loops must also be excluded
                                    && !Z.vertices().contains(targetV)) {
                                hasEdgeToY = true;
                            }
                            Gip1.redirectOut(Y, targetN, Z);

                        }
                    }

                    for(Vertex originV: G.incoming(z)) {
                        if (originV.equals(z)) continue;
                        Node originN = SCCip1.get(originV);
                        Gip1.redirectIn(originN, Y, Z);
                    }
                }

                if (hasEdgeToY) {
                    // Add Z before Y
                    sccs.insertBefore(Y,Z);
//                    int i = sccs.indexOf(Y);
//                    sccs.add(i, Z);
                } else {
                    // Add Z after Y
                    sccs.insertAfter(Y,Z);
//                    int i = sccs.indexOf(Y);
//                    sccs.add(i+1, Z);
                }
            }
            return Z;
        }
    }

    @Override
    public void delete(Set<Vertex> Vp) {
        for(Vertex u: Vp) {
            List<Vertex> out = new ArrayList<>(G.outgoing(u));
            for (Vertex v: out) {
                delete(u,v);
            }
        }
    }

    @Override
    public int sccCount() {
        return sccs.size();
    }

    @Override
    public HashList<SCC<Vertex>> SCCs() {
        return sccs;
    }

//    @Override
//    public SCC<Vertex> sigma(int index) {
//        assert index >= 0 && index < sccCount();
//        return sccs.get(index);
//    }

    @Override
    public SCC<Vertex> scc(Vertex v) {
        assert vertexNodeMaps.get(alpha).containsKey(v);
        return vertexNodeMaps.get(alpha).get(v);
    }

    private boolean mappingsCorrect(int level) {
        for(Node n: graphs.get(level).vertices()) {
            for(Vertex v: n.vertices()) {
                if (!vertexNodeMaps.get(level).get(v).equals(n)) {
                    return false;
                }
            }
        }
        return true;
    }
}
