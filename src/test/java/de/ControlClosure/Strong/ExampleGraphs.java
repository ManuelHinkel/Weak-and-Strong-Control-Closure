package de.ControlClosure.Strong;

import de.ControlClosure.Graph;
import de.ControlClosure.Utils.GraphUtils;
import de.ControlClosure.Vertex;

import java.util.*;

public class ExampleGraphs {
    public static Vertex[] v = new Vertex[]{
            new Vertex(), //0
            new Vertex(), //1
            new Vertex(), //2
            new Vertex(), //3
            new Vertex(), //4
            new Vertex(), //5
            new Vertex(), //6
            new Vertex(), //7
            new Vertex(), //8
            new Vertex(), //9
            new Vertex(), //10
            new Vertex(), //11
            new Vertex(), //12
            new Vertex(), //13
            new Vertex(), //14
            new Vertex(), //15
            new Vertex(), //16
            new Vertex(), //17
            new Vertex(), //18
            new Vertex(), //19
            new Vertex(), //20
            new Vertex(), //21
    };

    public static Graph<Vertex>[] graphs = new Graph[]{
            buildG10a(),
            buildG10a(),
            buildLarge(),
            buildCompletePaths(),
            buildCompletePaths(),
            buildForIncompPredicate(),
            buildForSelfLoop(),
            buildLoopPropagation(),
            buildPerlbench(),
    };
    private static final List<Vertex>[] startVertices = new List[] {
            List.of(v[0], v[2], v[7]),
            List.of(v[0], v[7]),
            List.of(v[8], v[14], v[17]),
            List.of(v[0], v[7]),
            List.of(v[0], v[7], v[9]),
            List.of(v[0], v[7]),
            List.of(v[0], v[2]),
            List.of(v[0], v[4]),
            List.of(v[0], v[4], v[8], v[17], v[20], v[21])
    };

    private static final List<Vertex>[] predicateVertices = new List[] {
            List.of(v[0],v[1],v[3]),
            List.of(v[0],v[1],v[3]),
            List.of(v[1],v[3],v[4],v[5],v[6],v[7],v[11],v[12],v[13],v[16]),
            List.of(v[0],v[1],v[2],v[3]),
            List.of(v[0],v[1],v[2],v[3]),
            List.of(v[0],v[1],v[3]), // v[3] is incomplete predicate
            List.of(v[1]),
            List.of(v[1]),
            List.of(v[2], v[4], v[5], v[7], v[9], v[10], v[12], v[14], v[15], v[17], v[18], v[20]),
    };

    private static final List<Vertex>[]expectedResults = new List[] {
            // p0 can reach a marked vertex and is not weakly commiting -> two marked vertices reachable
            // p1, needs to be added bc one complete path doesnt enter V' and it is therefore not V' strongly committing
            List.of(v[0], v[2], v[7], v[1], v[3]),
            // p0 can reach a marked vertex and ist not weakly commiting -> final path to g -> complete path not entering V'
            // p1 - " -
            List.of(v[0], v[7], v[1], v[3]),
            // v[16] is on a complete path (cycle)
            List.of(v[8], v[14], v[17], v[13],v[11],v[16],v[12],v[7],v[6],v[5],v[4],v[3],v[1]),
            List.of(v[0], v[7], v[1], v[3]),
            List.of(v[0], v[7], v[9], v[1], v[3], v[2]),
            List.of(v[0], v[7], v[1], v[3]),
            List.of(v[0], v[2], v[1]),
            List.of(v[0], v[4], v[1]),
            List.of(v[0], v[4], v[5], v[8], v[9], v[10], /*v[15],*/ v[17], v[18], v[20], v[21]),
    };

    public static Set<Vertex> startVertices(int index) {
        return new HashSet<>(startVertices[index]);
    }

    public static Set<Vertex> predicateVertices(int index) {
        return new HashSet<>(predicateVertices[index]);
    }

    public static Set<Vertex> finalVertices(int index) {
        Set<Vertex> F = new HashSet<>();
        Set<Vertex> P = predicateVertices(index);
        Graph<Vertex> G = graphs[index];

        for(Vertex v: G.vertices()) {
            if (GraphUtils.isFinal(v,P,G)) {
                F.add(v);
            }
        }
        return F;
    }

    public static Set<Vertex> expected(int index) {
        return new HashSet<>(expectedResults[index]);
    }

    // Tests self loop on v[4]
    public static Graph<Vertex> buildG10a() {
        Map<Vertex, List<Vertex>> G = new HashMap<>();
        G.put(v[0], List.of(v[1], v[2])); //start
        G.put(v[1], List.of(v[2], v[3])); //p0
        G.put(v[2], List.of()); // g
        G.put(v[3], List.of(v[4], v[5])); // p1
        G.put(v[4], List.of(v[4])); // k
        G.put(v[5], List.of(v[6])); // h
        G.put(v[6], List.of(v[7])); // m
        G.put(v[7], List.of()); // end

        return new Graph<>(G);
    }
    public static Graph<Vertex> buildLarge() {
        Map<Vertex, List<Vertex>> G = new HashMap<>();
        G.put(v[0], List.of(v[1]));
        G.put(v[1], List.of(v[2],v[3]));
        G.put(v[2], List.of(v[4]));
        G.put(v[3], List.of(v[4],v[12]));
        G.put(v[4], List.of(v[1],v[5]));
        G.put(v[5], List.of(v[6],v[7]));
        G.put(v[6], List.of(v[4],v[10]));
        G.put(v[7],List.of(v[8], v[9]));
        G.put(v[8], List.of());
        G.put(v[9],List.of(v[9]));
        G.put(v[10],List.of(v[11]));
        G.put(v[11],List.of(v[12],v[13]));
        G.put(v[12],List.of(v[10],v[15]));
        G.put(v[13],List.of(v[14]));
        G.put(v[14],List.of());
        G.put(v[15],List.of(v[16]));
        G.put(v[16],List.of(v[15],v[17]));
        G.put(v[17],List.of(v[1]));
        return new Graph<>(G);
    }

    public static Graph<Vertex> buildCompletePaths() {
        Map<Vertex, List<Vertex>> G = new HashMap<>();
        G.put(v[0], List.of(v[1], v[2]));
        G.put(v[1], List.of(v[2], v[3]));
        G.put(v[2], List.of(v[8], v[9]));
        G.put(v[3], List.of(v[4], v[5]));
        G.put(v[4], List.of(v[4]));
        G.put(v[5], List.of(v[6]));
        G.put(v[6], List.of(v[7]));
        G.put(v[7], List.of());
        G.put(v[8], List.of());
        G.put(v[9], List.of());

        return new Graph<>(G);
    }

    public static Graph<Vertex> buildForIncompPredicate() {
        Map<Vertex, List<Vertex>> G = new HashMap<>();
        G.put(v[0], List.of(v[1], v[2]));
        G.put(v[1], List.of(v[2], v[3]));
        G.put(v[2], List.of());
        G.put(v[3], List.of(v[5]));
        G.put(v[4], List.of(v[4]));
        G.put(v[5], List.of(v[6]));
        G.put(v[6], List.of(v[7]));
        G.put(v[7], List.of());

        return new Graph<>(G);
    }

    public static Graph<Vertex> buildForSelfLoop() {
        Map<Vertex, List<Vertex>> G = new HashMap<>();
        G.put(v[0], List.of(v[1]));
        G.put(v[1], List.of(v[2], v[1]));
        G.put(v[2], List.of());

        return new Graph<>(G);
    }

    public static Graph<Vertex> buildLoopPropagation() {
        Map<Vertex, List<Vertex>> G = new HashMap<>();
        G.put(v[0], List.of(v[1]));
        G.put(v[1], List.of(v[2],v[4]));
        G.put(v[2], List.of(v[3]));
        G.put(v[3], List.of(v[2]));
        G.put(v[4], List.of(v[4]));

        return new Graph<>(G);
    }

    public static Graph<Vertex> buildPerlbench() {
        Map<Vertex, List<Vertex>> G = new HashMap<>();
        G.put(v[0], List.of());
        G.put(v[1], List.of(v[0]));
        G.put(v[2], List.of(v[0], v[1]));
        G.put(v[3], List.of(v[2]));
        G.put(v[4], List.of(v[2],v[3]));
        G.put(v[5], List.of(v[0],v[4]));
        G.put(v[6], List.of(v[5]));
        G.put(v[7],List.of(v[5], v[6]));
        G.put(v[8], List.of(v[7]));
        G.put(v[9],List.of(v[7], v[8]));
        G.put(v[10],List.of(v[0], v[9]));
        G.put(v[11],List.of(v[10]));
        G.put(v[12],List.of(v[10], v[11]));
        G.put(v[13],List.of(v[12]));
        G.put(v[14],List.of(v[12], v[13]));
        G.put(v[15],List.of(v[10], v[14]));
        G.put(v[16],List.of(v[10]));
        G.put(v[17],List.of(v[15], v[16]));
        G.put(v[18],List.of(v[10], v[17]));
        G.put(v[19],List.of(v[0]));
        G.put(v[20],List.of(v[18], v[19]));
        G.put(v[21],List.of(v[20]));

        return new Graph<>(G);
    }
}
