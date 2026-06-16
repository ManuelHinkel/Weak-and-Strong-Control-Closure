package de.ControlClosure;

public class DSCCStatistics extends Statistics{
    protected double avgNewSCCCount;
    protected double avgRatio;

    public DSCCStatistics(String file, String algorithm) {
        super(file, algorithm);
    }

    public void setAvgNewSCCCount(double avgNewSCCCount) {
        this.avgNewSCCCount = avgNewSCCCount;
    }

    public void setAvgRatioLargestNewToCurrent(double avgRatio) {
        this.avgRatio = avgRatio;
    }

    @Override
    public String toString() {
        return super.toString() + ", " + avgNewSCCCount + ", " + avgRatio;
    }
}
