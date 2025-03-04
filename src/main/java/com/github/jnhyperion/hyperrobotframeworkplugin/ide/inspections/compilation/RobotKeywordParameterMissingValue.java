package com.github.jnhyperion.hyperrobotframeworkplugin.ide.inspections.compilation;

import com.github.jnhyperion.hyperrobotframeworkplugin.RobotBundle;
import com.github.jnhyperion.hyperrobotframeworkplugin.ide.inspections.SimpleRobotInspection;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.PositionalArgument;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.Parameter;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.RobotStatement;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.Variable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class RobotKeywordParameterMissingValue extends SimpleRobotInspection {

    @NotNull
    @Override
    protected String getGroupNameKey() {
        return "INSP.GROUP.compilation";
    }

    @Override
    public boolean skip(PsiElement element) {
        if (element instanceof Parameter parameter) {
            RobotStatement argument = PsiTreeUtil.findChildOfAnyType(parameter, PositionalArgument.class, Variable.class);
            return argument != null;
        }
        return true;
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getDisplayName() {
        return RobotBundle.getMessage("INSP.Name.keyword.parameter.value.undefined");
    }

    @NotNull
    @Override
    public String getMessage() {
        return RobotBundle.getMessage("INSP.keyword.parameter.value.undefined");
    }
}
