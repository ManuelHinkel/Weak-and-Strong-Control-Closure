package de.ControlClosure.Statistics;

import java.util.List;

public class DSCCStatistics extends Statistics {
    protected double avgSCCSize;
    protected double avgNewSCCCount;
    protected double avgRatio;
    protected double percentageOfOrigGraph;

    public DSCCStatistics(String file, String algorithm) {
        super(file, algorithm);
    }

    public void setAvgSCCSize(double avgSCCSize) {
        this.avgSCCSize = avgSCCSize;
    }

    public void setAvgNewSCCCount(double avgNewSCCCount) {
        this.avgNewSCCCount = avgNewSCCCount;
    }

    public void setAvgRatioLargestNewToCurrent(double avgRatio) {
        this.avgRatio = avgRatio;
    }

    public void setPercentageOfOrigGraph(double percentageOfOrigGraph) {
        this.percentageOfOrigGraph = percentageOfOrigGraph;
    }

    @Override
    public String toString() {
        return super.toString() + ", "
                + avgSCCSize + ", "
                + avgNewSCCCount + ", "
                + avgRatio + ", "
                + percentageOfOrigGraph;
    }

    @Override
    public Statistics average(List<Statistics> statisticsList) {
        String file = statisticsList.get(0).file;
        String algo = statisticsList.get(0).algorithm;

        long sumTime=0;
        double sumSize=0;
        double sumNew=0;
        double sumRatio=0;
        double sumPercentage=0;

        for(Statistics s: statisticsList) {
            DSCCStatistics ds = (DSCCStatistics)s;
            sumTime += s.runningTimeNano;
            sumSize += ds.avgSCCSize;
            sumNew += ds.avgNewSCCCount;
            sumRatio += ds.avgRatio;
            sumPercentage += ds.percentageOfOrigGraph;
        }

        DSCCStatistics average = new DSCCStatistics(file,algo);
        average.numVertices = statisticsList.get(0).numVertices;
        average.numEdges = statisticsList.get(0).numEdges;
        average.p = statisticsList.get(0).p;
        average.pPrime = statisticsList.get(0).pPrime;
        average.pF = statisticsList.get(0).pF;
        average.runningTimeNano = sumTime / statisticsList.size();
        average.avgSCCSize = sumSize / statisticsList.size();
        average.avgNewSCCCount = sumNew / statisticsList.size();
        average.avgRatio = sumRatio / statisticsList.size();
        average.percentageOfOrigGraph = sumPercentage / statisticsList.size();
        average.numRuns = statisticsList.size();

        return average;
    }
}
