package de.ControlClosure;

public class Statistics {
    protected String file;
    protected String algorithm;
    protected long runningTimeMS;
    protected int numVertices;
    protected int numEdges;

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

    @Override
    public String toString() {
        return file + ", " + algorithm + ", " + numVertices + ", " + numEdges + ", " + runningTimeMS;
    }
}
