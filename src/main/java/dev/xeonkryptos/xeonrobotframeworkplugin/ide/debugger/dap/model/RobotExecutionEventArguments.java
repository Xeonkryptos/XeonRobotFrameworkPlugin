package dev.xeonkryptos.xeonrobotframeworkplugin.ide.debugger.dap.model;

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
        if (!(o instanceof RobotExecutionEventArguments(String type1,
                                                        String id1,
                                                        String name1,
                                                        String parentId1,
                                                        RobotExecutionAttributes attributes1,
                                                        RobotExecutionAttributes[] keywords))) {
            return false;
        }
        return Objects.equals(id, id1) && Objects.equals(type, type1) && Objects.equals(name, name1) && Objects.equals(parentId, parentId1) && Objects.equals(
                attributes,
                attributes1) && Objects.deepEquals(failedKeywords, keywords);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, id, name, parentId, attributes, Arrays.hashCode(failedKeywords));
    }
}
