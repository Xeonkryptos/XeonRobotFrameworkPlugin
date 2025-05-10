package dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.complexity;

import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.SimpleRobotInspection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTokenTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.Variable;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class RobotNestedVariable extends SimpleRobotInspection {
    @Nls
    @NotNull
    public String getDisplayName() {
        return RobotBundle.getMessage("INSP.NAME.variable.nested");
    }

    @Override
    public final boolean skip(PsiElement element) {
        PsiElement parentElement = element.getParent();
        return element.getNode().getElementType() != RobotTokenTypes.VARIABLE || !(parentElement instanceof Variable) || !((Variable) parentElement).isNested();
    }

    @Override
    public final String getMessage() {
        return RobotBundle.getMessage("INSP.variable.nested");
    }

    @NotNull
    @Override
    protected final String getGroupNameKey() {
        return "INSP.GROUP.complexity";
    }
}
