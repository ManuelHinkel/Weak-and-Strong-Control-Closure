package de.ControlClosure.Strong;

import de.ControlClosure.Vertex;
import org.junit.jupiter.api.Test;

import java.io.File;

public class MainTest {
    @Test
    public void test() {
        File directory = new File("./evaluation/data_strong");

        for (File file : directory.listFiles()) {
            for(String algo: new String[]{"cubic", "quadratic", "polylog"}) {
                Main.main(new String[]{algo, file.getAbsolutePath()});
                Vertex.resetID();
            }
        }
    }
}
