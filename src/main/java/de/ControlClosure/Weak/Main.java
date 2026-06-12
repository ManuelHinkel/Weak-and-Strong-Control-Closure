package de.ControlClosure.Weak;

import de.ControlClosure.DSCC.Bernstein.PolylogDSCC;
import de.ControlClosure.DSCC.TarjanDSCC;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        if (args.length != 2) throw new IllegalArgumentException("Expected [cubic|quadratic|polylog] and a file!");
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
        System.out.println(wcc.getClass() + " Content: " + content);
    }
}
