package com.github.jnhyperion.hyperrobotframeworkplugin.ide.debugger.dap.model;

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
        if (!(o instanceof RobotExecutionAttributes that)) {return false;}
        return Objects.equals(id, that.id) &&
               Objects.equals(status, that.status) &&
               Objects.equals(source, that.source) &&
               Objects.deepEquals(tags, that.tags) &&
               Objects.equals(message, that.message) &&
               Objects.equals(lineno, that.lineno) &&
               Objects.equals(endtime, that.endtime) &&
               Objects.equals(parentId, that.parentId) &&
               Objects.equals(longname, that.longname) &&
               Objects.equals(template, that.template) &&
               Objects.equals(starttime, that.starttime) &&
               Objects.equals(elapsedtime, that.elapsedtime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, parentId, longname, template, status, message, elapsedtime, source, lineno, starttime, endtime, Arrays.hashCode(tags));
    }
}
