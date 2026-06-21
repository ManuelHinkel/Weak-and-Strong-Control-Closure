package de.ControlClosure.Evaluation;

import de.ControlClosure.Graph;
import de.ControlClosure.Utils.IOUtils;
import de.ControlClosure.Vertex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class CFileGenerator {
    public static void main(String[] args) {
        if (args.length != 3) {
            throw new IllegalArgumentException("Expected arguments: [out] [n] [p]!");
        }

        int n = Integer.parseInt(args[1]);
        double p = Double.parseDouble(args[2]);

        Vertex.resetID();
        Graph<Vertex> G = GraphGenerator.randomCFG(n,p, new Random());

        String fileName = "n=" + n + " p=" + p + ".c";
        String content = emitC(G);
        IOUtils.writeToFile(args[0], fileName,content);
    }

    private static String emitC(
            Graph<Vertex> G) {
        StringBuilder sb = new StringBuilder();

        sb.append("void generated_cfg(").append(System.lineSeparator());

        Iterator<Vertex> it = G.vertices().iterator();

        while (it.hasNext()) {
            Vertex v = it.next();

            sb.append("    int cond_").append(v.id);

            if (it.hasNext())
                sb.append(",");
            else
                sb.append(")").append(System.lineSeparator());
        }

        sb.append("{").append(System.lineSeparator());

        for (Vertex v : G.vertices()) {

            sb.append("B").append(v.id).append(":").append(System.lineSeparator());

            List<Vertex> successors = G.outgoing(v);
            if (successors.isEmpty()) {

                sb.append("    return;").append(System.lineSeparator());

            } else if (successors.size() == 1) {

                sb.append("    goto B").append(successors.get(0)).append(";").append(System.lineSeparator());

            } else if (successors.size() == 2) {

                sb.append("    if(cond_").append(v.id).append(")").append(System.lineSeparator());
                sb.append("        goto B").append(successors.get(0)).append(";").append(System.lineSeparator());
                sb.append(
                        "    else").append(System.lineSeparator());
                sb.append("        goto B").append(successors.get(1)).append(";").append(System.lineSeparator());

            } else {

                throw new IllegalStateException(
                        "Node " + v.id +
                                " has more than 2 successors");
            }

            sb.append(System.lineSeparator());
        }

        sb.append("}").append(System.lineSeparator());
        return sb.toString();
    }
}
