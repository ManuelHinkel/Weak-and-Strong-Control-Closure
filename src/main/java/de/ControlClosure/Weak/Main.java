package de.ControlClosure.Weak;

import de.ControlClosure.DSCC.Bernstein.PolylogDSCC;
import de.ControlClosure.DSCC.DecrementalSCC;
import de.ControlClosure.DSCC.TarjanDSCC;

import java.util.Arrays;

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
        System.out.println(wcc.getClass() + " File: " + args[1]);
    }
}
