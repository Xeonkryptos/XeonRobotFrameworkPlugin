package com.github.jnhyperion.hyperrobotframeworkplugin.ide.inspections.compilation;

import com.github.jnhyperion.hyperrobotframeworkplugin.RobotBundle;
import com.github.jnhyperion.hyperrobotframeworkplugin.ide.inspections.SimpleRobotInspection;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.DefinedParameter;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordStatement;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.Parameter;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

public class RobotKeywordParameterNotFound extends SimpleRobotInspection {

    @NotNull
    @Override
    protected String getGroupNameKey() {
        return "INSP.GROUP.compilation";
    }

    @Override
    public boolean skip(PsiElement element) {
        if (element instanceof Parameter parameter) {
            KeywordStatement keywordStatement = PsiTreeUtil.getParentOfType(parameter, KeywordStatement.class);
            if (keywordStatement == null) {
                return true;
            }
            Set<String> parameterNames = keywordStatement.getAvailableParameters().stream().map(DefinedParameter::getLookup).collect(Collectors.toSet());
            String parameterName = parameter.getParameterName();
            return parameterNames.contains(parameterName);
        }
        return true;
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getDisplayName() {
        return RobotBundle.getMessage("INSP.Name.keyword.parameter.undefined");
    }

    @NotNull
    @Override
    public String getMessage() {
        return RobotBundle.getMessage("INSP.keyword.parameter.undefined");
    }
}
