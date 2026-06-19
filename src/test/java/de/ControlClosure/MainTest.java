package de.ControlClosure;

import org.junit.jupiter.api.Test;

import java.io.File;

public class MainTest {
    @Test
    public void testWeak() {
        File directory = new File("./evaluation/data_weak");

        for (File file : directory.listFiles()) {
            for(String algo: new String[]{"cubic", "quadratic", "polylog"}) {
                Main.main(new String[]{algo, file.getAbsolutePath(), "1", "w", ""});
                Vertex.resetID();
            }
        }
    }

    @Test
    public void testStrong() {
        File directory = new File("./evaluation/data_strong_all");

        for (File file : directory.listFiles()) {
            for(String algo: new String[]{/*"cubic",*/ "quadratic", "polylog"}) {
                Main.main(new String[]{algo, file.getAbsolutePath(), "1", "s", ""});
                Vertex.resetID();
            }
        }
    }

    @Test
    public void testWeakWorstCase() {
        File directory = new File("./evaluation/data_worstcase");

        for (File file : directory.listFiles()) {
            for(String algo: new String[]{"quadratic","polylog"}) {
                Main.main(new String[]{algo, file.getAbsolutePath(), "1", "w", ""});
                Vertex.resetID();
            }
        }
    }
}
