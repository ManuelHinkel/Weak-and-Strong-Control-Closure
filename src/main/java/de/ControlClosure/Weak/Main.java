package de.ControlClosure.Weak;

import de.ControlClosure.*;
import de.ControlClosure.DSCC.Bernstein.PolylogDSCC;
import de.ControlClosure.DSCC.TarjanDSCC;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
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
        Tuple<Graph<Vertex>, Tuple<Set<Vertex>,Set<Vertex>>> parsed = GraphUtils.parseInstance(content);
        Graph<Vertex> G = parsed.first;
        Set<Vertex> Vprime = parsed.second.first;

        String algorithm = args[0];
        List<Statistics> l = new ArrayList<>();
        Statistics statistics = null;
        for(int i = 0; i < 10; i++) {
            WeakControlClosure wcc;
            switch (algorithm) {
                case "cubic" -> {
                    wcc = new WCCCubic();
                    statistics = new Statistics(fileName, "Cubic");
                    wcc.wcc(G,Vprime, statistics);
                    l.add(statistics);
                }
                case "quadratic" -> {
                    wcc = new WCCDecrementalSCC(new TarjanDSCC());
                    statistics = new DSCCStatistics(fileName, "Quadratic");
                    wcc.wcc(G,Vprime, statistics);
                    l.add(statistics);
                }
                case "polylog" -> {
                    wcc = new WCCDecrementalSCC(new PolylogDSCC());
                    statistics = new DSCCStatistics(fileName, "Polylog");
                    wcc.wcc(G,Vprime, statistics);
                    l.add(statistics);
                }
                default -> throw new IllegalArgumentException("Expected [cubic|quadratic|polylog] !");
            }
        }
        System.out.println(statistics.average(l));
    }
}
