package de.ControlClosure.Weak;

import de.ControlClosure.*;
import de.ControlClosure.DSCC.Bernstein.PolylogDSCC;
import de.ControlClosure.DSCC.TarjanDSCC;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        if (args.length != 3) throw new IllegalArgumentException("Expected [cubic|quadratic|polylog] [file] [out]!");
        String algorithm = args[0];
        WeakControlClosure wcc;
        switch (algorithm) {
            case "cubic" -> {wcc = new WCCCubic();}
            case "quadratic" -> {wcc = new WCCDecrementalSCC(new TarjanDSCC());}
            case "polylog" -> {wcc = new WCCDecrementalSCC(new PolylogDSCC());}
            default -> throw new IllegalArgumentException("Expected [cubic|quadratic|polylog] !");
        }

        Path filePath = Path.of(args[1]);
        String content;
        try {
            content = Files.readString(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Tuple<Graph<Vertex>, Set<Vertex>> parsed = GraphUtils.parseWCCInstance(content);
        Set<Vertex> res = wcc.wcc(parsed.first,parsed.second);

        String resS = res.toString();
        IOUtils.writeToFile(args[2],filePath.getFileName().toString().replace("txt", "csv"),resS);
    }
}
