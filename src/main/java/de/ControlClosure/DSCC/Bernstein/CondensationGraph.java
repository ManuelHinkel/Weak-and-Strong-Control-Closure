package de.ControlClosure.DSCC.Bernstein;


import de.ControlClosure.Graph;
import de.ControlClosure.GraphUtils;
import de.ControlClosure.SetUtils;
import de.ControlClosure.Vertex;

import java.util.*;

public class CondensationGraph extends Graph<Node> {

    public CondensationGraph(
            Graph<Vertex> underlyingGraph,
            Map<Vertex, Node> vertexNodeMap) {

        calcEdges(underlyingGraph, vertexNodeMap);

        assert GraphUtils.hasEdgeEntries(this);
    }


    private void calcEdges(
            Graph<Vertex> underlyingGraph,
            Map<Vertex, Node> vertexNodeMap) {
        for(Vertex v: underlyingGraph.adjacencyList.keySet()) {
            Node Xv = vertexNodeMap.get(v);

            if (!adjacencyList.containsKey(Xv)) {
                adjacencyList.put(Xv, new ArrayList<>());
            }

            for(Vertex w: underlyingGraph.adjacencyList.get(v)) {
                Node Xw = vertexNodeMap.get(w);
                adjacencyList.get(Xv).add(Xw);
            }
        }
        computeReversedAdjacencyList();
        computeNSL();
    }
}
