package de.ControlClosure.Strong;

import de.ControlClosure.*;
import de.ControlClosure.DSCC.Bernstein.PolylogDSCC;
import de.ControlClosure.DSCC.TarjanDSCC;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        assert false; // Test if Assertions are disabled
        if (args.length != 2) throw new IllegalArgumentException("Expected [cubic|quadratic|polylog] [file]!");

        Path filePath = Path.of(args[1]);
        String fileName = filePath.getFileName().toString();

        String content;
        try {
            content = Files.readString(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Tuple<Graph<Vertex>, Tuple<Set<Vertex>, Set<Vertex>>> parsed = GraphUtils.parseInstance(content);
        Graph<Vertex> G = parsed.first;
        Set<Vertex> Vprime = parsed.second.first;
        Set<Vertex> P = parsed.second.second;


        String algorithm = args[0];
        StrongControlClosure scc;
        Statistics statistics;
        switch (algorithm) {
            case "cubic" -> {
                scc = new SCCCubic();
                statistics = new Statistics(fileName, "Cubic");
            }
            case "quadratic" -> {
                scc = new SCCDecrementalSCC(new TarjanDSCC());
                statistics = new DSCCStatistics(fileName, "Quadratic");
            }
            case "polylog" -> {
                scc = new SCCDecrementalSCC(new PolylogDSCC());
                statistics = new DSCCStatistics(fileName, "Polylog");
            }
            default -> throw new IllegalArgumentException("Expected [cubic|quadratic|polylog] !");
        }

        scc.scc(G,Vprime,P,statistics);
        System.out.println(statistics);
    }
}
