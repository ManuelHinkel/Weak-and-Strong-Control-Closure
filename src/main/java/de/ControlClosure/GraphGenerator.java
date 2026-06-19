package de.ControlClosure;

import java.util.*;

public class GraphGenerator {
    public static void main(String[] args) {
        if (args.length != 5 && args.length != 2) {
            throw new IllegalArgumentException("Expected arguments: [folder] [n] [p] [p'] [pF] or [folder] [k]!");
        }

        String dataFolder = args[0];

        Graph<Vertex> G;
        Set<Vertex> Vprime;
        Set<Vertex> P = null;
        String fileName;
        String content;
        if (args.length == 5) {
            StringBuilder contentSB = new StringBuilder();
            int n = Integer.parseInt(args[1]);
            double p = Double.parseDouble(args[2]);
            double pPrime = Double.parseDouble(args[3]);
            double pF = Double.parseDouble(args[4]); // probability for a vertex with deg+ < 2 to become final

            fileName = "Random n=" + n + " p=" + p + " p'=" + pPrime + " pF=" + pF + ".txt";
            contentSB
                    .append("seed=").append(new Random().nextInt(Integer.MAX_VALUE)).append(System.lineSeparator())
                    .append("n=").append(n).append(System.lineSeparator())
                    .append("p=").append(p).append(System.lineSeparator())
                    .append("p'=").append(pPrime).append(System.lineSeparator())
                    .append("pF=").append(pF);

            content = contentSB.toString();
        } else {
            int k = Integer.parseInt(args[1]);

            Tuple<Graph<Vertex>, Set<Vertex>> res = makeQuadraticDSCCGraph(k);
            G = res.first;
            Vprime = res.second;
            fileName = "WorstCase n=" + (2*k) + ".txt";
            content = makeString(G,Vprime,P);
        }
        IOUtils.writeToFile(dataFolder, fileName, content);
    }


    public static Graph<Vertex> randomCFG(int n, double pEdge, Random r) {
        Vertex[] V = new Vertex[n];
        for(int i = 0; i < n; i++) {
            V[i] = new Vertex();
        }

        Map<Vertex, List<Vertex>> G = new HashMap<>();
        for(int i = 0; i < n; i++) {
            List<Vertex> adj = new ArrayList<>();

            if (r.nextDouble() < pEdge) {
                adj.add(V[r.nextInt(n)]);
            }

            if (r.nextDouble() < pEdge) {
                Vertex t;
                do {
                    t = V[r.nextInt(n)];
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

    public static Set<Vertex> chooseVprime(Set<Vertex> V, double pPrime, Random r) {
        Set<Vertex> Vprime = new HashSet<>();
        for(Vertex v: V) {
            if (r.nextDouble() < pPrime) {
                Vprime.add(v);
            }
        }
        return Vprime;
    }

    public static Set<Vertex> computeP(Graph<Vertex> G, double pF, Random r) {
        Set<Vertex> P = new HashSet<>();

        for(Vertex v: G.vertices()) {
            if (G.outgoing(v).size() == 2 || r.nextDouble() < pF) {
                P.add(v);
            }
        }
        return P;
    }

    private static String makeString(Graph<Vertex> G, Set<Vertex> Vprime, Set<Vertex> P) {
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
        VprimeLine.append(System.lineSeparator());
        content.append(VprimeLine);

        if (P != null) {
            StringBuilder PLine = new StringBuilder();
            PLine.append("P: ");
            for(Vertex v: P) {
                PLine.append(v.hashCode()).append(", ");
            }
            i = PLine.lastIndexOf(", ");
            if (i != -1) {
                PLine.replace(i,i+2, "");
            }
            content.append(PLine);
        }

        return content.toString();
    }
}
