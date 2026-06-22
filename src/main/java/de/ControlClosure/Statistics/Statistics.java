package de.ControlClosure.Statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Statistics {
    protected static double ONE_MILLION = 1_000_000;

    protected String file;
    protected String algorithm;
    protected long runningTimeNano;
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

    public void setRunningTimeNano(long runningTimeNano) {
        this.runningTimeNano = runningTimeNano;
    }

    public double getRunningTimeMs() {
        return runningTimeNano / ONE_MILLION;
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
                + getRunningTimeMs() + ", "
                + p + ", "
                + pPrime + ", "
                + pF;
    }

    public Statistics averageRunTime(List<Statistics> statisticsList) {
        String file = statisticsList.get(0).file;
        String algo = statisticsList.get(0).algorithm;

        long sumTime=0;

        for(Statistics s: statisticsList) {
            sumTime += s.runningTimeNano;
        }

        Statistics average = new Statistics(file,algo);
        average.runningTimeNano = sumTime / statisticsList.size();
        average.numVertices = statisticsList.get(0).numVertices;
        average.numEdges = statisticsList.get(0).numEdges;
        average.p = statisticsList.get(0).p;
        average.pPrime = statisticsList.get(0).pPrime;
        average.pF = statisticsList.get(0).pF;
        average.numRuns = statisticsList.size();

        return average;
    }

    public Statistics medianRunningTime(List<Statistics> statisticsList) {
        String file = statisticsList.get(0).file;
        String algo = statisticsList.get(0).algorithm;

        List<Long> times = new ArrayList<>();
        for (Statistics s : statisticsList) {
            times.add(s.runningTimeNano);
        }

        Collections.sort(times);

        long medianTime = times.get(times.size() / 2);

        Statistics average = new Statistics(file,algo);
        average.runningTimeNano = medianTime;
        average.numVertices = statisticsList.get(0).numVertices;
        average.numEdges = statisticsList.get(0).numEdges;
        average.p = statisticsList.get(0).p;
        average.pPrime = statisticsList.get(0).pPrime;
        average.pF = statisticsList.get(0).pF;
        average.numRuns = statisticsList.size();

        return average;
    }
}
