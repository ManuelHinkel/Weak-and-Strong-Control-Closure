package de.ControlClosure.Evaluation;

import de.ControlClosure.Vertex;
import org.junit.jupiter.api.Test;

import java.io.File;

public class MainTest {
    @Test
    public void testWeak() {
        File directory = new File("./evaluation/data_weak_dscc");

        for (File file : directory.listFiles()) {
            for(String algo: new String[]{"optimized"}) {
                Main.main(new String[]{algo, file.getAbsolutePath(), "5", "w", "", "i"});
                Vertex.resetID();
            }
        }
    }

    @Test
    public void testStrong() {
        File directory = new File("./evaluation/data_strong_dscc");

        for (File file : directory.listFiles()) {
            for(String algo: new String[]{"optimized"}) {
                Main.main(new String[]{algo, file.getAbsolutePath(), "1", "s", "", ""});
                Vertex.resetID();
            }
        }
    }

    @Test
    public void testWeakWorstCase() {
        File directory = new File("./evaluation/data_worst");

        for (File file : directory.listFiles()) {
            for(String algo: new String[]{"quadratic"}) {
                Main.main(new String[]{algo, file.getAbsolutePath(), "1", "w", ""});
                Vertex.resetID();
            }
        }
    }
}
