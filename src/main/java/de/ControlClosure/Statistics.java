package de.ControlClosure;

import java.util.List;

public class Statistics {
    protected String file;
    protected String algorithm;
    protected long runningTimeMS;
    protected int numVertices;
    protected int numEdges;

    protected int numRuns = 1;

    protected double p;
    protected double pPrime;
    protected double pF;

    public Statistics(String file, String algorithm) {
        this.file = file;
        this.algorithm = algorithm;
    }

    public void setNumVertices(int numVertices) {
        this.numVertices = numVertices;
    }

    public void setNumEdges(int numEdges) {
        this.numEdges = numEdges;
    }

    public void setRunningTimeMS(long runningTimeMS) {
        this.runningTimeMS = runningTimeMS;
    }

    public void setP(double p) {
        this.p = p;
    }

    public void setPPrime(double pPrime) {
        this.pPrime = pPrime;
    }

    public void setPF(double pF) {
        this.pF = pF;
    }

    @Override
    public String toString() {
        return file + ", "
                + algorithm + ", "
                + numRuns + ", "
                + numVertices + ", "
                + numEdges + ", "
                + runningTimeMS + ", "
                + p + ", "
                + pPrime + ", "
                + pF;
    }

    public Statistics averageRunTime(List<Statistics> statisticsList) {
        String file = statisticsList.get(0).file;
        String algo = statisticsList.get(0).algorithm;

        long sumTime=0;

        for(Statistics s: statisticsList) {
            sumTime += s.runningTimeMS;
        }

        Statistics average = new Statistics(file,algo);
        average.runningTimeMS = sumTime / statisticsList.size();
        average.numVertices = statisticsList.get(0).numVertices;
        average.numEdges = statisticsList.get(0).numEdges;
        average.p = statisticsList.get(0).p;
        average.pPrime = statisticsList.get(0).pPrime;
        average.pF = statisticsList.get(0).pF;
        average.numRuns = statisticsList.size();

        return average;
    }
}
