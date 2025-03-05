package com.github.jnhyperion.hyperrobotframeworkplugin.ide.inspections.complexity;

import com.github.jnhyperion.hyperrobotframeworkplugin.RobotBundle;
import com.github.jnhyperion.hyperrobotframeworkplugin.ide.inspections.SimpleRobotInspection;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RobotStubTokenTypes;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.VariableDefinition;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class RobotNestedVariableDefinition extends SimpleRobotInspection {
    @Nls
    @NotNull
    public String getDisplayName() {
        return RobotBundle.getMessage("INSP.NAME.variableDefinition.nested");
    }

    @Override
    public final boolean skip(PsiElement element) {
        PsiElement parentElement = element.getParent();
        return element.getNode().getElementType() != RobotStubTokenTypes.VARIABLE_DEFINITION || !(parentElement instanceof VariableDefinition)
               || !((VariableDefinition) parentElement).isNested();
    }

    @Override
    public final String getMessage() {
        return RobotBundle.getMessage("INSP.variableDefinition.nested");
    }

    @NotNull
    @Override
    protected final String getGroupNameKey() {
        return "INSP.GROUP.complexity";
    }
}
