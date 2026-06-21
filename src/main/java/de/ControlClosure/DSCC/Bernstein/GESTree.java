package de.ControlClosure.DSCC.Bernstein;

import de.ControlClosure.Graph;
import de.ControlClosure.DataStructuresAndAlgorithms.Tuple;
import de.ControlClosure.Vertex;

import java.util.*;

public class GESTree <T extends Vertex>{
    public static final int INFINITY = Integer.MAX_VALUE;

    protected final T r;
    protected final Graph<T> G;
    protected final Separator<T> S;
    protected final double delta;

    public final HashMap<T, Integer> outDist = new HashMap<>();
    public final HashMap<T, Integer> inDist = new HashMap<>();

    public final HashMap<T, T> shortestPathOutTreeParents = new HashMap<>();
    public final HashMap<T, T> shortestPathInTreeParents = new HashMap<>();
    public final HashMap<T, List<T>> shortestPathOutTreeChildren = new HashMap<>();
    public final HashMap<T, List<T>> shortestPathInTreeChildren = new HashMap<>();

    protected final Queue<T> unreachableVerticesOutTree = new ArrayDeque<>();
    protected final Queue<T> unreachableVerticesInTree = new ArrayDeque<>();

    protected final Deque<Tuple<T, Integer>> Q_out = new ArrayDeque<>();
    protected final Deque<Tuple<T, Integer>> Q_in = new ArrayDeque<>();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Root " + r  + " V: ")
                .append(G.vertices().toString())
                .append("\nParents:\n")
                .append(shortestPathOutTreeParents)
                .append("\n")
                .append(shortestPathInTreeParents)
                .append("\nChildren:\n")
                .append(shortestPathOutTreeChildren)
                .append("\n")
                .append(shortestPathInTreeChildren)
                .append("\nDists:\n")
                .append(outDist)
                .append("\n")
                .append(inDist)
                .append("\nUnreachable:\n")
                .append(unreachableVerticesOutTree)
                .append(unreachableVerticesInTree)
                .append("\n Q: \n")
                .append(Q_out)
                .append(Q_in);
        return sb.toString();
    }

    public GESTree(T r, Graph<T> G, Separator<T> S, double delta){
        this.r = r;
        this.G = G;
        this.S = S;
        this.delta = delta;

        buildShortestPathOutTree();
        buildShortestPathInTree();
        assert Q_out.isEmpty();
        assert Q_in.isEmpty();
    }

    public T root() {
        return r;
    }

    public Graph<T> graph() {
        return G;
    }

    public Integer dist(T a, T b) {
        if (r.equals(a)) {
            return lOut(b);
        } else if(r.equals(b)){
            return lIn(a);
        } else {
            throw new IllegalArgumentException("Non of the arguments is the root node.");
        }
    }

    public void delete(T u, T v) {
        G.delete(u,v);

        remove(u,v);
    }

    public void remove(T u, T v) {
        if (isOutTreeEdge(u, v)) {
            shortestPathOutTreeChildren.get(u).remove(v);
            shortestPathOutTreeParents.put(v, null);
            assert !containsVertex(Q_out,v);
            Q_out.add(new Tuple<>(v, lOut(v)));
            fixOutTree();
        }
        if (isInTreeEdge(v, u)) { // (v,u) is a tree edge in T_in
            shortestPathInTreeChildren.get(v).remove(u);
            shortestPathInTreeParents.put(u, null);
            assert !containsVertex(Q_in,u);
            Q_in.add(new Tuple<>(u, lIn(u)));
            fixInTree();
        }
    }

    public void delete(Set<T> vertices) {
        assert !vertices.contains(root());
        for (T v: vertices) {
            for (T w: new ArrayList<>(G.outgoing(v))) {
                delete(v, w);
            }
            for (T u: new ArrayList<>(G.incoming(v))) {
                delete(u, v);
            }
        }

        G.deleteFromMaps(vertices);
        unreachableVerticesOutTree.removeAll(vertices);
        unreachableVerticesInTree.removeAll(vertices);

        for(T v: vertices) {
            shortestPathOutTreeParents.remove(v);
            shortestPathInTreeParents.remove(v);
            shortestPathOutTreeChildren.remove(v);
            shortestPathInTreeChildren.remove(v);
            outDist.remove(v);
            inDist.remove(v);
        }
    }

    public boolean hasUnreachable() {
        return !(unreachableVerticesOutTree.isEmpty() && unreachableVerticesInTree.isEmpty());
    }

    public T getUnreachable() {
        assert hasUnreachable();
        if (!unreachableVerticesOutTree.isEmpty()) {
            return unreachableVerticesOutTree.peek();
        } else {
            return unreachableVerticesInTree.peek();
        }
    }

    public Set<T> getAllUnreachable() {
        Set<T> unreachable = new HashSet<>(unreachableVerticesOutTree);
        unreachable.addAll(unreachableVerticesInTree);
        assert !unreachable.contains(r);
        return unreachable;
    }

    public Set<T> getAll() {
        return G.vertices();
    }

    protected int lOut(T v) {
        assert outDist.containsKey(v);
        return outDist.get(v);
    }

    protected int lIn(T v) {
        assert inDist.containsKey(v);
        return inDist.get(v);
    }

    protected int w(T u, T v) {
        assert G.outgoing(u).contains(v);
        return S.contains(u) ? 1 : 0;
    }

    private void buildShortestPathOutTree() {
        for(T v: G.vertices()) {
            outDist.put(v, INFINITY);
            shortestPathOutTreeChildren.put(v,new ArrayList<>());
        }

        Deque<T> dequeue = new ArrayDeque<>();
        dequeue.add(r);

        outDist.put(r, 0);

        while (!dequeue.isEmpty()) {
            T u = dequeue.poll();

            for(T v: G.outgoing(u)) {
                int w = w(u,v);
                int dist = lOut(u) + w;
                if (dist <= delta && dist < lOut(v)) {
                    outDist.put(v, dist);
                    shortestPathOutTreeParents.put(v,u);

                    if (w == 0) {
                        dequeue.addFirst(v);
                    } else {
                        dequeue.addLast(v);
                    }
                }
            }
        }

        for(T v: G.vertices()) {
            if (shortestPathOutTreeParents.get(v) != null) {
                shortestPathOutTreeChildren.get(shortestPathOutTreeParents.get(v)).add(v);
            }
            if(lOut(v) == INFINITY) {
                unreachableVerticesOutTree.add(v);
            }
        }
    }

    private void buildShortestPathInTree() {
        for(T v: G.vertices()) {
            inDist.put(v, INFINITY);
            shortestPathInTreeChildren.put(v,new ArrayList<>());
        }

        Deque<T> dequeue = new ArrayDeque<>();
        dequeue.add(r);

        inDist.put(r, 0);

        while (!dequeue.isEmpty()) {
            T v = dequeue.poll();

            for(T u: G.incoming(v)) {
                int w = w(u, v);
                int dist = lIn(v) + w;
                if (dist <= delta && dist < lIn(u)) {
                    inDist.put(u, dist);
                    shortestPathInTreeParents.put(u,v);

                    if (w == 0) {
                        dequeue.addFirst(u);
                    } else {
                        dequeue.addLast(u);
                    }
                }
            }
        }

        for(T v: G.vertices()) {
            if (shortestPathInTreeParents.get(v) != null) {
                shortestPathInTreeChildren.get(shortestPathInTreeParents.get(v)).add(v);
            }
            if(lIn(v) == INFINITY) {
                unreachableVerticesInTree.add(v);
            }
        }
    }


    public boolean isOutTreeEdge(T u, T v) {
        return u.equals(shortestPathOutTreeParents.get(v));
    }

    public boolean isInTreeEdge(T u, T v) {
        return u.equals(shortestPathInTreeParents.get(v));
    }

    protected void fixOutTree() {
        while (!Q_out.isEmpty() && Q_out.peek().second < delta + 1) {
            Tuple<T, Integer> head = Q_out.poll();
            T v = head.first;
            int l = head.second;

            if (r.equals(v)) {
                continue;
            }


            boolean reconnected = false;

            for(T y: G.incomingNSL(v)) {
                if (!v.equals(y) && l == lOut(y) + w(y,v)) {
                    shortestPathOutTreeParents.put(v,y);
                    assert !shortestPathOutTreeChildren.get(y).contains(v);
                    shortestPathOutTreeChildren.get(y).add(v);
                    reconnected = true;
                    break;
                }
            }

            if (!reconnected) { // increase dist by one and retry
                // Add all child nodes in the tree to Q_out and remove parent child relation
                disconnectOutChildren(v);

                outDist.put(v, l + 1);
                assert !containsVertex(Q_out,v);
                Q_out.add(new Tuple<>(v, l + 1));
            }

            assert QisSorted(Q_out);
        }

        while (!Q_out.isEmpty()) {
            T v = Q_out.poll().first;
            outDist.put(v, INFINITY);
            assert  !unreachableVerticesOutTree.contains(v); // || unreachableVerticesInTree.contains(v));
            unreachableVerticesOutTree.add(v);
        }
    }


    protected void fixInTree() {
        while (!Q_in.isEmpty() && Q_in.peek().second < delta + 1) {
            Tuple<T, Integer> head = Q_in.poll();
            T v = head.first;
            int l = head.second;

            if (r.equals(v)) {
                continue;
            }

            boolean reconnected = false;

            for(T y: G.outgoingNSL(v)) {
                if (!v.equals(y) && l == lIn(y) + w(v, y)) {
                    shortestPathInTreeParents.put(v, y);
                    shortestPathInTreeChildren.get(y).add(v);
                    reconnected = true;
                    break;
                }
            }

            if (!reconnected) { // increase dist by one and retry
                // Add all child nodes in the tree to Q_in and remove parent child relation
                disconnectInChildren(v);

                inDist.put(v, l + 1);
                assert !containsVertex(Q_in,v);
                Q_in.addLast(new Tuple<>(v, l + 1));
            }
            assert QisSorted(Q_in);
        }

        while (!Q_in.isEmpty()) {
            T v = Q_in.poll().first;
            inDist.put(v, INFINITY);
            assert !unreachableVerticesInTree.contains(v);
            unreachableVerticesInTree.add(v);
        }
    }

    public void disconnectOutChildren(T v) {
        for (T z: shortestPathOutTreeChildren.get(v)) {
            shortestPathOutTreeParents.put(z, null);
            if (S.contains(v)) {
                assert !containsVertex(Q_out,z);
                Q_out.addLast(new Tuple<>(z, lOut(z)));
            } else {
                assert !containsVertex(Q_out,z);
                Q_out.addFirst(new Tuple<>(z, lOut(z)));
            }
        }
        shortestPathOutTreeChildren.get(v).clear();
        assert QisSorted(Q_out);
    }

    public void disconnectInChildren(T v) {
        for (T z: shortestPathInTreeChildren.get(v)) {
            shortestPathInTreeParents.put(z, null);
            if (S.contains(z)) {
                assert !containsVertex(Q_in,z);
                Q_in.addLast(new Tuple<>(z, lIn(z)));
            } else {
                assert !containsVertex(Q_in,z);
                Q_in.addFirst(new Tuple<>(z, lIn(z)));
            }
        }
        shortestPathInTreeChildren.get(v).clear();
        assert QisSorted(Q_in);
    }

    public void augment(Set<T> S) {

        for(T n: S) {
            if (this.S.contains(n)) continue;
            this.S.add(n);
            if (!G.vertices().contains(n)) {
                System.out.println("Should not happen");
            }
            for(T v: G.outgoing(n)) {
                if (isOutTreeEdge(n,v)) {
                    disconnectOut(v);
                }
                if (isInTreeEdge(v,n)) {
                    disconnectIn(n);
                }
            }
        }

        fixOutTree();
        fixInTree();
    }

    public void disconnectOut(T v) {
        // Adds the children of v to Q_out in order
        disconnectOutChildren(v);
        T parent = shortestPathOutTreeParents.get(v);
        if (parent != null) {
            shortestPathOutTreeChildren.get(parent).remove(v);
            shortestPathOutTreeParents.put(v, null);
        }
        // Add this in front because some children may be reattached
        Q_out.addFirst(new Tuple<>(v, lOut(v)));
    }

    public void disconnectIn(T v) {
        // Adds the children of v to Q_in in order
        disconnectInChildren(v);
        T parent = shortestPathInTreeParents.get(v);
        if (parent != null) {
            shortestPathInTreeChildren.get(parent).remove(v);
            shortestPathInTreeParents.put(v, null);
        }
        // Add this in front because some children may be reattached
        Q_in.addFirst(new Tuple<>(v, lIn(v)));
    }

    private static <T> boolean QisSorted(Queue<Tuple<T, Integer>> Q) {
        Integer prev = null;

        for (Tuple<T, Integer> pair : Q) {
            int current = pair.second; // second element

            if (prev != null && current < prev) {
                return false;
            }
            prev = current;
        }

        return true;
    }

    private static <T> boolean containsVertex(Queue<Tuple<T, Integer>> Q, T v) {
        for (Tuple<T, Integer> pair : Q) {
            if (pair.first.equals(v)) {
                return true;
            }
        }
        return false;
    }
}