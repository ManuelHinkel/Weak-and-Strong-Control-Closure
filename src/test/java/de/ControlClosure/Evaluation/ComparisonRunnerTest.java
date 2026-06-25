package de.ControlClosure.Evaluation;

import org.junit.jupiter.api.Test;


public class ComparisonRunnerTest {
    @Test
    public void test() {
        ComparisonRunner.main(new String[]{"./comparison/instances/gcc", "20"});
    }
}
