package de.ControlClosure;

import java.util.List;

public class DSCCStatistics extends Statistics{
    protected double avgSCCSize;
    protected double avgNewSCCCount;
    protected double avgRatio;

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

    @Override
    public String toString() {
        return super.toString() + ", " + avgSCCSize + ", " + avgNewSCCCount + ", " + avgRatio;
    }

    @Override
    public Statistics average(List<Statistics> statisticsList) {
        String file = statisticsList.get(0).file;
        String algo = statisticsList.get(0).algorithm;

        int sumV=0;
        int sumE=0;
        long sumTime=0;
        double sumSize=0;
        double sumNew=0;
        double sumRatio=0;

        for(Statistics s: statisticsList) {
            DSCCStatistics ds = (DSCCStatistics)s;
            sumV += s.numVertices;
            sumE += s.numEdges;
            sumTime += s.runningTimeMS;
            sumSize += ds.avgSCCSize;
            sumNew += ds.avgNewSCCCount;
            sumRatio += ds.avgRatio;
        }

        DSCCStatistics average = new DSCCStatistics(file,algo);
        average.numVertices = sumV / statisticsList.size();
        average.numEdges = sumE / statisticsList.size();
        average.runningTimeMS = sumTime / statisticsList.size();
        average.avgSCCSize = sumSize / statisticsList.size();
        average.avgNewSCCCount = sumNew / statisticsList.size();
        average.avgRatio = sumRatio / statisticsList.size();

        return average;
    }
}
