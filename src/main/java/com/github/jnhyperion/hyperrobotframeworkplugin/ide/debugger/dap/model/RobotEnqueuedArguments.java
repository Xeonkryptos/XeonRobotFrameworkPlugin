package com.github.jnhyperion.hyperrobotframeworkplugin.ide.debugger.dap.model;

import java.util.Arrays;
import java.util.Objects;

public record RobotEnqueuedArguments(String[] items) {

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RobotEnqueuedArguments that)) {return false;}
        return Objects.deepEquals(items, that.items);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(items);
    }
}
