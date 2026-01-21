package dev.xeonkryptos.xeonrobotframeworkplugin.execution.dap.model;

import java.util.Arrays;
import java.util.Objects;

public record RobotExecutionAttributes(String id,
                                       String parentId,
                                       String longname,
                                       String template,
                                       String status,
                                       String message,
                                       Integer elapsedtime,
                                       String source,
                                       Integer lineno,
                                       String starttime,
                                       String endtime,
                                       String[] tags) {
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RobotExecutionAttributes(String id1,
                                                    String parentId1,
                                                    String longname1,
                                                    String template1,
                                                    String status1,
                                                    String message1,
                                                    Integer elapsedtime1,
                                                    String source1,
                                                    Integer lineno1,
                                                    String starttime1,
                                                    String endtime1,
                                                    String[] tags1))) {
            return false;
        }
        return Objects.equals(id, id1) && Objects.equals(status, status1) && Objects.equals(source, source1) && Objects.deepEquals(tags, tags1)
               && Objects.equals(message, message1) && Objects.equals(lineno, lineno1) && Objects.equals(endtime, endtime1) && Objects.equals(parentId,
                                                                                                                                              parentId1)
               && Objects.equals(longname, longname1) && Objects.equals(template, template1) && Objects.equals(starttime, starttime1) && Objects.equals(
                elapsedtime,
                elapsedtime1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, parentId, longname, template, status, message, elapsedtime, source, lineno, starttime, endtime, Arrays.hashCode(tags));
    }
}
