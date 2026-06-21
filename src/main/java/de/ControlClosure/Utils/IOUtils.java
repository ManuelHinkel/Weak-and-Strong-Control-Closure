package de.ControlClosure.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class IOUtils {
    public static void writeToFile(String folder, String fileName, String content) {
        try {
            Path folderPath = Path.of(folder);
            Files.createDirectories(folderPath);

            Path outputFile = folderPath.resolve(fileName);
            Files.writeString(outputFile, content);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to file!");
        }
    }
}
