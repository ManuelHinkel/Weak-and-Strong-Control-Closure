package de.ControlClosure.Weak;

import de.ControlClosure.Graph;
import de.ControlClosure.Vertex;

import java.util.*;

public class ExampleGraphs {
    private static final Vertex[] v = new Vertex[]{
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
    };

    public static Graph<Vertex>[] graphs = new Graph[]{
            buildG6a(),
            buildG6a(),
            buildG6a(),
            buildEx1(),
            buildEx1E(),
            buildExJungle(),
            buildExCycle(),
            buildExJungleCycle(),
            buildExDownward(),
            buildExNotReachable(),
            buildG10a(),
            buildLarge(),
    };

    private static final List<Vertex>[] startVertices = new List[] {
            List.of(v[0], v[7], v[4], v[6]),
            List.of(v[0], v[7], v[6]),
            List.of(v[0], v[7]),
            List.of(v[2],v[5],v[8],v[10],v[13]),
            List.of(v[2],v[13]),
            List.of(v[1],v[3],v[4],v[5],v[8]),
            List.of(v[1],v[5],v[8]),
            List.of(v[1],v[3],v[4],v[5],v[12]),
            List.of(v[1],v[4],v[5]),
            List.of(v[3],v[4]),
            List.of(v[0], v[2], v[7]),
            List.of(v[7], v[13], v[17]),
    };
    private static final List<Vertex>[] expectedResults = new List[] {
            List.of(v[0], v[7], v[4], v[6], v[1], v[2]),
            List.of(v[0], v[7], v[6], v[1]),
            List.of(v[0], v[7]),
            List.of(v[2],v[5],v[8],v[10],v[13],v[3]),
            List.of(v[2],v[5],v[8],v[10],v[13],v[3],v[4]),
            List.of(v[1],v[3],v[4],v[5],v[8],v[2],v[6]),
            List.of(v[1],v[5],v[8],v[4],v[3],v[2]),
            List.of(v[1],v[3],v[4],v[5],v[12],v[2],v[6],v[7],v[8]),
            List.of(v[1],v[4],v[5],v[2],v[3]),
            List.of(v[3],v[4]),
            List.of(v[0], v[2], v[7], v[1]),
            List.of(v[7], v[13], v[17], v[11],v[12],v[6],v[5],v[3],v[1],v[4]),
    };

    public static Set<Vertex> startVertices(int index) {
        return new HashSet<>(startVertices[index]);
    }

    public static Set<Vertex> expected(int index) {
        return new HashSet<>(expectedResults[index]);
    }

    static Graph<Vertex> buildG6a() {
        Map<Vertex, List<Vertex>> G = new HashMap<>();
        G.put(v[0], List.of(v[1]));
        G.put(v[1], List.of(v[2], v[6]));
        G.put(v[2], List.of(v[3], v[5]));
        G.put(v[3], List.of(v[4]));
        G.put(v[4], List.of(v[7]));
        G.put(v[5], List.of(v[7]));
        G.put(v[6], List.of(v[7]));
        G.put(v[7], List.of());
        return new Graph<>(G);
    }

    static Graph<Vertex> buildEx1() {
        Map<Vertex, List<Vertex>> G = new HashMap<>();
        G.put(v[0], List.of());
        G.put(v[1], List.of(v[2]));
        G.put(v[2], List.of(v[3]));
        G.put(v[3], List.of(v[4],v[5]));
        G.put(v[4], List.of(v[6]));
        G.put(v[5], List.of(v[4],v[7]));
        G.put(v[6], List.of(v[8]));
        G.put(v[7], List.of(v[10]));
        G.put(v[8], List.of(v[9],v[10]));
        G.put(v[9], List.of(v[11]));
        G.put(v[10], List.of(v[2], v[12]));
        G.put(v[11], List.of(v[13]));
        G.put(v[12], List.of(v[13]));
        G.put(v[13], List.of());
        return new Graph<>(G);
    }

    static Graph<Vertex> buildEx1E() {
        Map<Vertex, List<Vertex>> G = new HashMap<>();
        G.put(v[0], List.of());
        G.put(v[1], List.of(v[2]));
        G.put(v[2], List.of(v[3]));
        G.put(v[3], List.of(v[4],v[5]));
        G.put(v[4], List.of(v[6]));
        G.put(v[5], List.of(v[4],v[7]));
        G.put(v[6], List.of(v[8]));
        G.put(v[7], List.of(v[10]));
        G.put(v[8], List.of(v[9],v[10]));
        G.put(v[9], List.of(v[11]));
        G.put(v[10], List.of(v[2], v[12]));
        G.put(v[11], List.of(v[13]));
        G.put(v[12], List.of(v[13]));
        G.put(v[13], List.of());
        G.put(v[4], List.of(v[2],v[6]));
        return new Graph<>(G);
    }

    static Graph<Vertex> buildExJungle() {
        Map<Vertex, List<Vertex>> G = new HashMap<>();
        G.put(v[0], List.of());
        G.put(v[1], List.of(v[2]));
        G.put(v[2], List.of(v[3],v[4]));
        G.put(v[3], List.of());
        G.put(v[4], List.of());
        G.put(v[5], List.of(v[6]));
        G.put(v[6], List.of(v[7],v[8]));
        G.put(v[7], List.of(v[4]));
        G.put(v[8], List.of());
        return new Graph<>(G);
    }

    static Graph<Vertex> buildExCycle() {
        Map<Vertex, List<Vertex>> G = new HashMap<>();
        G.put(v[0], List.of());
        G.put(v[1], List.of(v[2]));
        G.put(v[2], List.of(v[3],v[4]));
        G.put(v[3], List.of(v[5],v[6]));
        G.put(v[4], List.of(v[7],v[8]));
        G.put(v[5], List.of());
        G.put(v[6], List.of(v[2]));
        G.put(v[7], List.of(v[6]));
        G.put(v[8], List.of());
        return new Graph<>(G);
    }

    static Graph<Vertex> buildExJungleCycle() {
        Map<Vertex, List<Vertex>> G = new HashMap<>();
        G.put(v[0], List.of());
        G.put(v[1], List.of(v[2]));
        G.put(v[2], List.of(v[3],v[4]));
        G.put(v[3], List.of());
        G.put(v[4], List.of());
        G.put(v[5], List.of(v[6]));
        G.put(v[6], List.of(v[7],v[8]));
        G.put(v[7], List.of(v[9],v[10]));
        G.put(v[8], List.of(v[11],v[12]));
        G.put(v[9], List.of(v[4]));
        G.put(v[10], List.of(v[6]));
        G.put(v[11], List.of(v[10]));
        G.put(v[12], List.of());
        return new Graph<>(G);
    }

    static Graph<Vertex> buildExDownward() {
        Map<Vertex, List<Vertex>> G = new HashMap<>();
        G.put(v[0], List.of(v[0]));
        G.put(v[1], List.of(v[2]));
        G.put(v[2], List.of(v[3],v[4]));
        G.put(v[3], List.of(v[4],v[5]));
        G.put(v[4], List.of());
        G.put(v[5], List.of());
        return new Graph<>(G);
    }

    static Graph<Vertex> buildExNotReachable() {
        Map<Vertex, List<Vertex>> G = new HashMap<>();
        G.put(v[0], List.of(v[1]));
        G.put(v[1], List.of(v[2]));
        G.put(v[2], List.of(v[3],v[4]));
        G.put(v[3], List.of());
        G.put(v[4], List.of());
        return new Graph<>(G);
    }

    static Graph<Vertex> buildG10a() {
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

    static Graph<Vertex> buildLarge() {
        Map<Vertex, List<Vertex>> G = new HashMap<>();
        G.put(v[0], List.of(v[1]));
        G.put(v[1], List.of(v[2],v[3]));
        G.put(v[2], List.of(v[4]));
        G.put(v[3], List.of(v[4],v[12]));
        G.put(v[4], List.of(v[1],v[5]));
        G.put(v[5], List.of(v[6],v[7]));
        G.put(v[6], List.of(v[4],v[8]));
        G.put(v[7],List.of());
        G.put(v[8], List.of(v[9]));
        G.put(v[9],List.of(v[10]));
        G.put(v[10],List.of(v[11]));
        G.put(v[11],List.of(v[12],v[13]));
        G.put(v[12],List.of(v[8],v[14]));
        G.put(v[13],List.of());
        G.put(v[14],List.of(v[15]));
        G.put(v[15],List.of(v[16]));
        G.put(v[16],List.of(v[14],v[17]));
        G.put(v[17],List.of(v[1]));
        return new Graph<>(G);
    }
}

