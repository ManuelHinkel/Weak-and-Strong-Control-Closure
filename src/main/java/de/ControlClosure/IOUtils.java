package de.ControlClosure;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class IOUtils {
    public static void main(String[] args) {
        if (args.length != 2) throw new IllegalArgumentException("Expected <path to individual results> <folder for combined results> and a file!");

        String sourceFolder = args[0];
        String outputFolder = args[1];

        System.out.println(args[0]);
        System.out.println(args[1]);

        combineCSVFiles(sourceFolder, outputFolder);
    }

    public static void combineCSVFiles(String sourceFolder, String outputFolder) {
        try {
            Path sourcePath = Paths.get(sourceFolder);
            List<Path> csvFiles = Files.list(sourcePath).toList();

            StringBuilder mergedContent = new StringBuilder();

            for (Path csvFile : csvFiles) {
                List<String> lines = Files.readAllLines(csvFile);
                for (String line : lines) {
                    mergedContent.append(line).append(System.lineSeparator());
                }

                Files.delete(csvFile);
            }

            writeToFile(outputFolder, sourcePath.getFileName() + "_results.csv", mergedContent.toString());
        } catch (IOException e) {
            throw new RuntimeException("Failed to combine result files!");
        }
    }

    public static void writeToFile(String folder, String fileName, String content) {
        try {
            Path folderPath = Path.of(folder);
            Files.createDirectories(folderPath);

            Path outputFile = folderPath.resolve(fileName);
            Files.writeString(outputFile, content);
        } catch (IOException e) {
            System.out.println(e);
            throw new RuntimeException("Failed to write to file!");
        }
    }
}
