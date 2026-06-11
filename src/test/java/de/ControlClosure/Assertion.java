package de.ControlClosure;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class Assertion {
    @Test
    void assertionsDisabled() {
        boolean enabled = false;
        assert enabled = true;
        assertFalse(enabled);
    }
}
