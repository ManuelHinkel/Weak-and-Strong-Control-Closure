package de.ControlClosure.Weak;

import de.ControlClosure.*;
import de.ControlClosure.DSCC.Bernstein.PolylogDSCC;
import de.ControlClosure.DSCC.TarjanDSCC;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        if (args.length != 2) throw new IllegalArgumentException("Expected [cubic|quadratic|polylog] [file]!");

        Path filePath = Path.of(args[1]);
        String fileName = filePath.getFileName().toString();

        String content;
        try {
            content = Files.readString(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Tuple<Graph<Vertex>, Set<Vertex>> parsed = GraphUtils.parseWCCInstance(content);
        Graph<Vertex> G = parsed.first;
        Set<Vertex> Vprime = parsed.second;


        String algorithm = args[0];
        WeakControlClosure wcc;
        Statistics statistics;
        switch (algorithm) {
            case "cubic" -> {
                wcc = new WCCCubic();
                statistics = new Statistics(fileName, "Cubic");
            }
            case "quadratic" -> {
                wcc = new WCCDecrementalSCC(new TarjanDSCC());
                statistics = new DSCCStatistics(fileName, "Quadratic");
            }
            case "polylog" -> {
                wcc = new WCCDecrementalSCC(new PolylogDSCC());
                statistics = new DSCCStatistics(fileName, "Polylog");
            }
            default -> throw new IllegalArgumentException("Expected [cubic|quadratic|polylog] !");
        }

        wcc.wcc(G,Vprime,statistics);
        System.out.println(statistics);
    }
}
