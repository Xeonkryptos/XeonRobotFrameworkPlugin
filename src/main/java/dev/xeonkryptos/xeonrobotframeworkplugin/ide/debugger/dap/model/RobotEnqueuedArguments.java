package dev.xeonkryptos.xeonrobotframeworkplugin.ide.debugger.dap.model;

import java.util.Arrays;
import java.util.Objects;

public record RobotEnqueuedArguments(String[] items) {

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RobotEnqueuedArguments(String[] items1))) {
            return false;
        }
        return Objects.deepEquals(items, items1);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(items);
    }
}
