package com.github.jnhyperion.hyperrobotframeworkplugin.ide.inspections.complexity;

import com.github.jnhyperion.hyperrobotframeworkplugin.RobotBundle;
import com.github.jnhyperion.hyperrobotframeworkplugin.ide.inspections.SimpleRobotInspection;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RobotTokenTypes;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.Variable;
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
