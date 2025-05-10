package dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.complexity;

import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.SimpleRobotInspection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotStubTokenTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.VariableDefinition;
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
