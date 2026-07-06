package de.ControlClosure.Evaluation;

import de.ControlClosure.DSCC.TarjanDSCC;
import de.ControlClosure.Graph;
import de.ControlClosure.Statistics.Statistics;
import de.ControlClosure.Strong.SCCCubic;
import de.ControlClosure.Strong.SCCDecrementalSCC;
import de.ControlClosure.Utils.GraphUtils;
import de.ControlClosure.Vertex;
import de.ControlClosure.Weak.WCCCubic;
import de.ControlClosure.Weak.WCCDecrementalSCC;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;


public class ComparisonRunner {
    public static void main(String[] args) {
        assert false; // Test if Assertions are disabled
        if (args.length != 2) {
            throw new IllegalArgumentException("Expected [instance folder] [repetitions]!");
        }

        File dataFolder = new File(args[0]);

        for(File file: dataFolder.listFiles()) {
            String fileName = file.getName();
            String content;
            try {
                content = Files.readString(file.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            int repetitions = Integer.parseInt(args[1]);

            Vertex.resetID();

            Instance instance = Parser.parseInstance(content);
            Graph<Vertex> G = instance.G;
            Set<Vertex> Vprime = instance.Vprime;
            Set<Vertex> P = instance.P;
            Set<Vertex> Xweak = instance.weakControlClosure;
            Set<Vertex> Xstrong = instance.strongControlClosure;

//            List<Vertex> sortedVertices = new ArrayList<>(Vprime);
//            sortedVertices.sort(Comparator.comparingInt(v -> v.id));
//            System.out.println(sortedVertices);
//

            List<Statistics> lWeak = new ArrayList<>();
            List<Statistics> lStrong = new ArrayList<>();
            Statistics statistics = null;

            boolean notMatchingWeak = false;

            for(int i = 0; i < repetitions; i++) {
                statistics = new Statistics(fileName, "Quadratic");
                Set<Vertex> res = new WCCDecrementalSCC(new TarjanDSCC()).measure(G,Vprime, statistics, false);
                lWeak.add(statistics);
                if (!res.equals(Xweak)) {
                    notMatchingWeak = true;
                    break;
                }
            }
            Statistics averageWeak = statistics.average(lWeak);
            Statistics medianWeak = statistics.medianRunningTime(lWeak);
            Statistics longestWeak = statistics.longestRunningTime(lWeak);

            boolean notMatchingStrong = false;

            for(int i = 0; i < repetitions; i++) {
                statistics = new Statistics(fileName, "Quadratic");
                Set<Vertex> res = new SCCDecrementalSCC(new TarjanDSCC()).measure(G,Vprime,P, statistics, false);
                lStrong.add(statistics);
                if (!res.equals(Xstrong)) {
                    notMatchingStrong = true;
                    break;
                }
            }
            Statistics averageStrong = statistics.average(lStrong);
            Statistics medianStrong = statistics.medianRunningTime(lStrong);
            Statistics longestStrong = statistics.longestRunningTime(lStrong);

            System.out.println(fileName + ", "
                    + G.size() + ", "
                    + averageWeak.getRunningTimeMs()  + ", "
                    + averageStrong.getRunningTimeMs() + ", "
                    + (notMatchingWeak ? "EXCEPTION" : "")  + instance.wccTime + ", "
                    + (notMatchingStrong ? "EXCEPTION" : "") + instance.sccTime + ", "
                    + medianWeak.getRunningTimeMs() + ", "
                    + medianStrong.getRunningTimeMs() + ", "
                    + longestWeak.getRunningTimeMs() + ", "
                    + longestStrong.getRunningTimeMs() + ", "
                    + GraphUtils.numberOfNonTrivialSCCs(G) + ", "
                    + GraphUtils.isCFG(G));
        }
    }
}
