package de.ControlClosure.Evaluation;

import de.ControlClosure.DSCC.TarjanDSCC;
import de.ControlClosure.Graph;
import de.ControlClosure.Statistics.Statistics;
import de.ControlClosure.Strong.SCCDecrementalSCC;
import de.ControlClosure.Vertex;
import de.ControlClosure.Weak.WCCCubic;
import de.ControlClosure.Weak.WCCDecrementalSCC;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class ComparisonRunner {
    public static void main(String[] args) {
        assert false; // Test if Assertions are disabled
        if (args.length != 2) {
            throw new IllegalArgumentException("Expected [file] [repetitions]!");
        }

        Path filePath = Path.of(args[0]);
        String fileName = filePath.getFileName().toString();
        String content;
        try {
            content = Files.readString(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int repetitions = Integer.parseInt(args[1]);

        Instance instance = Parser.parseInstance(content);
        Graph<Vertex> G = instance.G;
        Set<Vertex> Vprime = instance.Vprime;
        Set<Vertex> P = instance.P;

        Set<Vertex> Xweak = instance.weakControlClosure;
        Set<Vertex> Xstrong = instance.strongControlClosure;

        List<Statistics> lWeak = new ArrayList<>();
        List<Statistics> lStrong = new ArrayList<>();
        Statistics statistics = null;

        for(int i = 0; i < repetitions; i++) {
            statistics = new Statistics(fileName, "Quadratic");
            Set<Vertex> res = new WCCDecrementalSCC(new TarjanDSCC()).measure(G,Vprime, statistics, false);
            if (!res.equals(Xweak)) throw new RuntimeException("Result is not the weak control closure");
            lWeak.add(statistics);
        }
        Statistics averageWeak = statistics.average(lWeak);
        Statistics medianWeak = statistics.medianRunningTime(lWeak);
        Statistics longestWeak = statistics.longestRunningTime(lWeak);

        for(int i = 0; i < repetitions; i++) {
            statistics = new Statistics(fileName, "Quadratic");
            Set<Vertex> res = new SCCDecrementalSCC(new TarjanDSCC()).measure(G,Vprime,P, statistics, false);
            if (!res.equals(Xstrong)) throw new RuntimeException("Result is not the strong control closure");
            lStrong.add(statistics);
        }
        Statistics averageStrong = statistics.average(lStrong);
        Statistics medianStrong = statistics.medianRunningTime(lStrong);
        Statistics longestStrong = statistics.longestRunningTime(lStrong);

        System.out.println(fileName + ", "
                + G.size() + ", "
                + averageWeak.getRunningTimeMs()  + ", "
                + averageStrong.getRunningTimeMs() + ", "
                + instance.wccTime + ", "
                + instance.sccTime + ", "
                + medianWeak.getRunningTimeMs() + ", "
                + medianStrong.getRunningTimeMs() + ", "
                + longestWeak.getRunningTimeMs() + ", "
                + longestStrong.getRunningTimeMs());
    }
}
