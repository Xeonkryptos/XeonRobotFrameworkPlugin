package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import org.jetbrains.annotations.Nullable;

public interface RobotAssignedVariable {

    @Nullable FoldingText getAssignedValues();
}
