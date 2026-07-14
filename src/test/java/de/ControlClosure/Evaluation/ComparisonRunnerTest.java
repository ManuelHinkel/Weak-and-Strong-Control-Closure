package de.ControlClosure.Evaluation;

import org.junit.jupiter.api.Test;

/*
 * Tests if the output of ComparisonRunner has the correct format
 */
public class ComparisonRunnerTest {
    @Test
    public void test() {
        ComparisonRunner.main(new String[]{"./comparison/instances/gcc", "20"});
    }
}
