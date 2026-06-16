package de.ControlClosure;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class GraphGenerator {
    private static final Random RANDOM = new Random();

    public static void main(String[] args) {
        if (args.length != 4 && args.length != 2) {
            throw new IllegalArgumentException("Expected arguments: [folder] [n] [p] [p'] or [folder] [k]!");
        }

        String dataFolder = args[0];

        Graph<Vertex> G;
        Set<Vertex> Vprime;
        String fileName;
        if (args.length == 4) {
            int n = Integer.parseInt(args[1]);
            double p = Double.parseDouble(args[2]);
            double pPrime = Double.parseDouble(args[3]);

            G = randomCFG(n, p);
            Vprime = chooseVprime(G.vertices(), pPrime);

            fileName = "Random n=" + n + " p=" + p + " p'=" + pPrime + ".txt";
        } else {
            int k = Integer.parseInt(args[1]);

            Tuple<Graph<Vertex>, Set<Vertex>> res = makeQuadraticDSCCGraph(k);
            G = res.first;
            Vprime = res.second;
            fileName = "WorstCase n=" + (2*k) + ".txt";
        }
        String content = makeString(G,Vprime);
        IOUtils.writeToFile(dataFolder, fileName, content);
    }


    private static Graph<Vertex> randomCFG(int n, double pEdge) {
        Vertex[] V = new Vertex[n];
        for(int i = 0; i < n; i++) {
            V[i] = new Vertex();
        }

        Map<Vertex, List<Vertex>> G = new HashMap<>();
        for(int i = 0; i < n; i++) {
            List<Vertex> adj = new ArrayList<>();

            if (RANDOM.nextDouble() < pEdge) {
                adj.add(V[RANDOM.nextInt(n)]);
            }

            if (RANDOM.nextDouble() < pEdge) {
                Vertex t;
                do {
                    t = V[RANDOM.nextInt(n)];
                } while (adj.contains(t));
                adj.add(t);
            }
            G.put(V[i], adj);
        }

        return new Graph<>(G);
    }

    public static Tuple<Graph<Vertex>, Set<Vertex>> makeQuadraticDSCCGraph(int k) {
        Vertex[] V = new Vertex[2 * k];
        for(int i = 0; i < 2*k; i++) {
            V[i] = new Vertex();
        }

        Map<Vertex, List<Vertex>> G = new HashMap<>();
        for(int i = 0; i < 2*(k-1); i+=2) {
            G.put(V[i], List.of(V[1], V[i+2]));
            G.put(V[i+1], List.of(V[i], V[i+3]));
        }
        G.put(V[2*k-2],List.of(V[1]));
        G.put(V[2*k-1],List.of(V[2*k-2]));

        return new Tuple<>(new Graph<>(G), new HashSet<>(List.of(V[2*k-2],V[2*k-1])));
    }

    private static Set<Vertex> chooseVprime(Set<Vertex> V, double p) {
        Set<Vertex> Vprime = new HashSet<>();
        for(Vertex v: V) {
            if (RANDOM.nextDouble() < p) {
                Vprime.add(v);
            }
        }
        return Vprime;
    }

    private static String makeString(Graph<Vertex> G, Set<Vertex> Vprime) {
        List<Vertex> V = new ArrayList<>(G.vertices());
        V.sort(Comparator.comparingInt(Object::hashCode));

        StringBuilder content = new StringBuilder();

        for(Vertex v: V) {
            StringBuilder line = new StringBuilder();
            line.append(v.hashCode()).append(": ");

            List<Vertex> adj = new ArrayList<>(G.outgoing(v));
            adj.sort(Comparator.comparingInt(Object::hashCode));

            for(Vertex t: adj) {
                line.append(t.hashCode()).append(", ");
            }

            int i = line.lastIndexOf(", ");
            if (i != -1) {
                line.replace(i,i+2, "");
            }
            line.append(System.lineSeparator());
            content.append(line);
        }

        StringBuilder VprimeLine = new StringBuilder();
        VprimeLine.append("V': ");
        for(Vertex v: Vprime) {
            VprimeLine.append(v.hashCode()).append(", ");
        }
        int i = VprimeLine.lastIndexOf(", ");
        if (i != -1) {
            VprimeLine.replace(i,i+2, "");
        }
        content.append(VprimeLine);

        return content.toString();
    }

    private static void writeToFolder(String dataFolder, Graph<Vertex> G, Set<Vertex> Vprime, Set<Vertex> P) {

    }
}
