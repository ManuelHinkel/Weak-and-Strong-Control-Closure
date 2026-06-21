package de.ControlClosure.Evaluation;

import de.ControlClosure.Vertex;
import org.junit.jupiter.api.Test;

import java.io.File;

public class ComparisonRunnerTest {
    @Test
    public void test() {
        File directory = new File("./comparison/instances/gcc");

        for (File file : directory.listFiles()) {
            ComparisonRunner.main(new String[]{file.getAbsolutePath(), "100"});
            Vertex.resetID();
        }
    }
}
