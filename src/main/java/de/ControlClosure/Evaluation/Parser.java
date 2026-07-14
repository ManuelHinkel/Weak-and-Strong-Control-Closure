package de.ControlClosure.Evaluation;

import de.ControlClosure.DataStructuresAndAlgorithms.Tuple;
import de.ControlClosure.Graph;
import de.ControlClosure.Utils.GraphUtils;
import de.ControlClosure.Utils.IOUtils;
import de.ControlClosure.Vertex;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Stream;
/*
 * Given the output of Masud's algorithm, produces wcc and scc instances.
 * Also parses instance files.
 */
public class Parser {

    public static final String V_PRIME_ID = "V'";
    public static final String P_ID = "P";
    public static final String WCC_ID = "Xw";
    public static final String SCC_ID = "Xs";
    public static final String WCC_TIME_ID = "T_Xw";
    public static final String SCC_TIME_ID = "T_Xs";

    public static Instance parseInstance(String content) {
        Instance instance = new Instance();
        Map<Vertex, List<Vertex>> G = new HashMap<>();

        List<String> lines = Arrays.stream(content.split("\n")).toList();
        List<String> adj = new ArrayList<>();

        List<Integer> Vprime = List.of();
        List<Integer> P = List.of();
        List<Integer> WCC = List.of();
        List<Integer> SCC = List.of();
        for(String line: lines) {
            if(line.startsWith(V_PRIME_ID)) {
                Vprime = parseVertices(line.split(":",2)[1]);
            } else if (line.startsWith(P_ID)) {
                P = parseVertices(line.split(":",2)[1]);
            } else if (line.startsWith(WCC_ID)) {
                WCC = parseVertices(line.split(":",2)[1]);
            } else if (line.startsWith(SCC_ID)) {
                SCC = parseVertices(line.split(":",2)[1]);
            } else if (line.startsWith(WCC_TIME_ID)) {
                instance.wccTime = Long.parseLong(line.split(":",2)[1].trim());
            } else if (line.startsWith(SCC_TIME_ID)) {
                instance.sccTime = Long.parseLong(line.split(":",2)[1].trim());
            } else {
                adj.add(line);
            }
        }

        Vertex[] V = new Vertex[adj.size()];
        for(int i = 0; i < V.length; i++) {
            V[i] = new Vertex();
        }

        for(String adjacencyList: adj) {
            String[] split = adjacencyList.split(":",2);
            String vertexId = split[0];
            String adjacencyString = split[1];

            Vertex v = V[Integer.parseInt(vertexId.trim())];
            List<Vertex> endpoints = mapToVertex(parseVertices(adjacencyString), V);
            G.put(v, endpoints);
        }

        instance.G = new Graph<>(G);
        instance.Vprime = new HashSet<>(mapToVertex(Vprime,V));
        instance.P = new HashSet<>(mapToVertex(P,V));
        instance.weakControlClosure = new HashSet<>(mapToVertex(WCC,V));
        instance.strongControlClosure = new HashSet<>(mapToVertex(SCC,V));

        return instance;
    }

    private static List<Integer> parseVertices(String vertices) {
        List<Integer> adjacencyList = new ArrayList<>();
        String[] vertex = vertices.split(",");

        for(String v: vertex) {
            if (!v.isBlank()) {
                adjacencyList.add(Integer.parseInt(v.trim()));
            }
        }
        return adjacencyList;
    }

    private static List<Vertex> mapToVertex(List<Integer> list, Vertex[] V) {
        return list.stream().map(s -> V[s]).toList();
    }

    private static final String METHOD_SEPARATOR = "--------------------";

    public static List<Instance> parseFromCResults(String cResults) {
        String[] methodResults = cResults.split(METHOD_SEPARATOR);
        String header = methodResults[0];

        if(!header.contains("Error")) {
            throw new RuntimeException(header);
        }

        String containsCFileName = header.split("\\.c",2)[0];
        String[] split = containsCFileName.split("/");
        String cFileName = split[split.length-1] + ".c";

        List<Instance> instances = new ArrayList<>();
        // Exclude error message at the start
        for(int i = 1; i < methodResults.length; i++) {
            instances.add(parseMethod(methodResults[i].trim(), cFileName));
        }

        return instances;
    }

    private static Instance parseMethod(String method, String cFileName) {
        Instance instance = new Instance();

        String[] lines = method.split("\\r?\\n|\\r");

        // G
        List<String> graphLines = new ArrayList<>();
        for(String line: lines) {
            if (line.startsWith("digraph")) continue; // Exclude first line
            if (line.startsWith("}")) break; // Graph String ends with this line
            graphLines.add(line);
        }

        Tuple<Vertex[], Map<Vertex, List<Vertex>>> G = parseGraph(graphLines);
        Vertex[] V = G.first;
        instance.G = new Graph<>(G.second);

        // V'
        List<Vertex> Vprime = mapToVertex(
            parseVertices(
                Arrays.stream(lines).filter(l -> l.contains("Nodes")).findFirst().get()
                    .split(": :")[1]
            ),
            V);
        instance.Vprime = new HashSet<>(Vprime);

        // P (only vertices with outDegree 2, therefore pF = 0.0)
        instance.P = GraphGenerator.computeP(instance.G, 0.0, new Random());

        // WCC
        List<Vertex> WCC = mapToVertex(
            parseVertices(
                    Arrays.stream(lines).filter(l -> l.contains("WCC (")).findFirst().get()
                            .split(": :")[1]
            ),
            V);
        instance.weakControlClosure = new HashSet<>(WCC);

        // SCC
        List<Vertex> SCC = mapToVertex(
                parseVertices(
                        Arrays.stream(lines).filter(l -> l.contains("SCC (")).findFirst().get()
                                .split(": :")[1]
                ),
                V);
        instance.strongControlClosure = new HashSet<>(SCC);

        // Function
        String func = Arrays.stream(lines).filter(l -> l.contains("Func")).findFirst().get();
        String[] data = func.split(",");

        String fileName = data[0].split(":",2)[1].trim();
        instance.name = cFileName + "-" + fileName;

        // Computation time
        String execWCC = data[3].split(":",2)[1].trim();
        String execSCC = data[5].split(":",2)[1].trim();

        instance.wccTime = Long.parseLong(execWCC);
        instance.sccTime = Long.parseLong(execSCC);

        return instance;
    }

    private static Tuple<Vertex[], Map<Vertex, List<Vertex>>> parseGraph( List<String> lines) {
        List<Tuple<Integer,Integer>> edges = new ArrayList<>();

        int numV = 0;
        for(String line: lines) {
            String[] e = line.split("->");
            Integer source = Integer.parseInt(e[0].trim());
            Integer target = Integer.parseInt(e[1].replace(";", "").trim());
            if (source > numV) {
                numV = source;
            }
            if (target > numV) {
                numV = target;
            }
            edges.add(new Tuple<>(source,target));
        }

        Vertex[] V = new Vertex[numV+1]; // highest index + 1 for vertex v_0
        Map<Vertex, List<Vertex>> G = new HashMap<>();
        for(int i = 0; i < V.length; i++) {
            V[i] = new Vertex();
            G.put(V[i], new ArrayList<>());
        }

        for(Tuple<Integer,Integer> e: edges) {
            Vertex source = V[e.first];
            Vertex target = V[e.second];
            G.get(source).add(target);
        }

        Vertex.resetID();
        return new Tuple<>(V,G);
    }

    // Iterates over the results of Masud's algorithm and translates them to Instances
    public static void main(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("Expected arguments: [result folder] [output folder]!");
        }

        File dataFolder = new File(args[0]);

        List<Instance> all = new ArrayList<>();

        for(File file: dataFolder.listFiles()) {
            try {
                String cResult = Files.readString(file.toPath());
                all.addAll(parseFromCResults(cResult));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        for(Instance i: all) {
            IOUtils.writeToFile(args[1], i.name + ".txt", i.toString());
        }
    }
}
