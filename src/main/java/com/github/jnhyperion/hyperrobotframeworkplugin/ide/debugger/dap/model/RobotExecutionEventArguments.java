package com.github.jnhyperion.hyperrobotframeworkplugin.ide.debugger.dap.model;

import java.util.Arrays;
import java.util.Objects;

public record RobotExecutionEventArguments(String type,
                                           String id,
                                           String name,
                                           String parentId,
                                           RobotExecutionAttributes attributes,
                                           RobotExecutionAttributes[] failedKeywords) {

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RobotExecutionEventArguments that)) {return false;}
        return Objects.equals(id, that.id) &&
               Objects.equals(type, that.type) &&
               Objects.equals(name, that.name) &&
               Objects.equals(parentId, that.parentId) &&
               Objects.equals(attributes, that.attributes) &&
               Objects.deepEquals(failedKeywords, that.failedKeywords);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, id, name, parentId, attributes, Arrays.hashCode(failedKeywords));
    }
}
