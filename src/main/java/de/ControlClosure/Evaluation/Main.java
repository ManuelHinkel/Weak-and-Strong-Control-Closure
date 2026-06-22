package de.ControlClosure.Evaluation;

import de.ControlClosure.DSCC.Bernstein.PolylogDSCC;
import de.ControlClosure.DSCC.TarjanDSCC;
import de.ControlClosure.Graph;
import de.ControlClosure.Statistics.DSCCStatistics;
import de.ControlClosure.Statistics.Statistics;
import de.ControlClosure.Strong.SCCCubic;
import de.ControlClosure.Strong.SCCDecrementalSCC;
import de.ControlClosure.Vertex;
import de.ControlClosure.Weak.WCCCubic;
import de.ControlClosure.Weak.WCCDecrementalSCC;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        assert false; // Test if Assertions are disabled
        if (args.length != 5) {
            throw new IllegalArgumentException("Expected [cubic|quadratic|polylog] [file] [repetitions] [w|s] [scc]!");
        }

        Path filePath = Path.of(args[1]);
        String fileName = filePath.getFileName().toString();
        String content;
        try {
            content = Files.readString(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String algorithm = args[0];
        int repetitions = Integer.parseInt(args[2]);
        boolean weak = args[3].equals("w");
        boolean sccStats = args[4].equals("scc");

        if (fileName.startsWith("Random")) { // Generate Random Graph
            String[] lines = content.split(System.lineSeparator());
            int seed = Integer.parseInt(lines[0].split("=",2)[1]);
            int n = Integer.parseInt(lines[1].split("=",2)[1]);
            double p = Double.parseDouble(lines[2].split("=",2)[1]);
            double pPrime = Double.parseDouble(lines[3].split("=",2)[1]);
            double pF = Double.parseDouble(lines[4].split("=",2)[1]);

            runRandomGraph(fileName,seed,n,p,pPrime,pF,repetitions,algorithm,weak,sccStats);
        } else { // Parse Graph
            Instance instance = Parser.parseInstance(content);
            Graph<Vertex> G = instance.G;
            Set<Vertex> Vprime = instance.Vprime;
            Set<Vertex> P = instance.P;

            List<Statistics> l = new ArrayList<>();
            Statistics statistics = null;

            for(int i = 0; i < repetitions; i++) {
                statistics = getStatistics(fileName, algorithm, G, sccStats);
                run(G, Vprime, P, algorithm, weak, statistics, sccStats);
                l.add(statistics);
            }
            System.out.println(statistics.average(l));
        }
    }

    private static Statistics getStatistics(String fileName, String algorithm, Graph<Vertex> g, boolean sccStats) {
        Statistics statistics;
        statistics = switch (algorithm) {
            case "cubic" -> new Statistics(fileName, "Cubic");
            case "quadratic" -> sccStats ? new DSCCStatistics(fileName, "Quadratic") : new Statistics(fileName, "Quadratic");
            default ->  sccStats ? new DSCCStatistics(fileName, "Polylog") : new Statistics(fileName, "Polylog");
        };
        statistics.setNumVertices(g.size());
        statistics.setNumEdges(g.m());
        return statistics;
    }

    private static void runRandomGraph(String fileName, int seed, int n, double p, double pPrime, double pF, int reps, String algorithm, boolean weak, boolean sccStats) {
        List<Statistics> l = new ArrayList<>();
        Statistics statistics = null;

        Random r = new Random(seed);
        for(int i = 0; i < reps; i++) {
            Vertex.resetID();
            Graph<Vertex> G = GraphGenerator.randomCFG(n,p,r);
            Set<Vertex> Vprime = GraphGenerator.chooseVprime(G.vertices(), pPrime, r);
            Set<Vertex> P = GraphGenerator.computeP(G, pF, r);

            statistics = getStatistics(fileName, algorithm, G, sccStats);
            statistics.setP(p);
            statistics.setPPrime(pPrime);
            statistics.setPF(pF);

            run(G,Vprime,P,algorithm,weak,statistics, sccStats);
            l.add(statistics);
        }
        System.out.println(statistics.average(l));
    }

    public static String curfile;

    private static void run(Graph<Vertex> G, Set<Vertex> Vprime, Set<Vertex> P, String algorithm, boolean weak, Statistics statistics, boolean sccStats) {
        switch (algorithm) {
            case "cubic" -> {
                if (weak) {
                    new WCCCubic().measure(G,Vprime, statistics, sccStats);
                } else {
                    new SCCCubic().measure(G,Vprime,P,statistics, sccStats);
                }
                }
            case "quadratic" -> {
                if (weak) {
                    new WCCDecrementalSCC(new TarjanDSCC()).measure(G,Vprime, statistics, sccStats);
                } else {
                    new SCCDecrementalSCC(new TarjanDSCC()).measure(G,Vprime,P,statistics, sccStats);
                }
            }
            case "polylog" -> {
                if (weak) {
                    new WCCDecrementalSCC(new PolylogDSCC()).measure(G,Vprime, statistics, sccStats);
                } else {
                    new SCCDecrementalSCC(new PolylogDSCC()).measure(G,Vprime,P,statistics, sccStats);
                }
            }
        }
    }
}
