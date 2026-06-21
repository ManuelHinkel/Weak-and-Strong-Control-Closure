package de.ControlClosure.Evaluation;

import de.ControlClosure.Utils.IOUtils;
import de.ControlClosure.Vertex;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Summation {

    public static void main(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Expected [c Subdirectory]!");
        }

        File cSubDir = new File("./instance_results/" + args[0]);

        double sumTimeWeakMs = 0.0;
        double sumTimeStrongMs = 0.0;

        // TODO: is it milliseconds or microseconds
        long sumTimeOtherWeakMs = 0;
        long sumTimeOtherStrongMs = 0;

        for (File file : cSubDir.listFiles()) {
            try {
                String content = Files.readString(file.toPath());

                String[] fields = content.split(",");

                sumTimeWeakMs += Double.parseDouble(fields[1]);
                sumTimeStrongMs += Double.parseDouble(fields[2]);
                sumTimeOtherWeakMs += Long.parseLong(fields[3]);
                sumTimeOtherStrongMs += Long.parseLong(fields[4]);

                String out = args[0] + ", "
                        + sumTimeWeakMs + ", "
                        + sumTimeStrongMs + ", "
                        + sumTimeOtherWeakMs + ", "
                        + sumTimeOtherStrongMs;

                IOUtils.writeToFile("./summed_results/", args[0] + ".csv", out);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
