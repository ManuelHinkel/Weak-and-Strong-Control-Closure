package de.ControlClosure.Evaluation;

import org.junit.jupiter.api.Test;

public class ParserTest {
    @Test
    public void test() {
        Parser.main(new String[]{"./comparison/results/gcc", "./comparison/instances/gcc"});
    }
}
