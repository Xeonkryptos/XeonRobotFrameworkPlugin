package com.github.jnhyperion.hyperrobotframeworkplugin.psi.ref;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordStatement;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.Parameter;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotParameterReference extends PsiReferenceBase<Parameter> implements PsiReference {

    public RobotParameterReference(@NotNull Parameter parameter) {
        super(parameter, false);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        PsiElement result = null;

        Parameter parameter = getElement();
        PsiElement parent = parameter.getParent();
        if (parameter.getContainingFile().isValid()) {
            if (parent instanceof KeywordStatement) {
                result = ResolverUtils.findKeywordElement(parameter.getPresentableText(), parameter.getContainingFile());
                if (parameter.getPresentableText().contains("=")) {
                    int index = parameter.getPresentableText().indexOf('=');
                    String parameterName = parameter.getPresentableText().substring(0, index).trim();
                    result = ResolverUtils.findKeywordParameterElement(parameterName, (KeywordStatement) parent);
                }
            }
        }

        return result;
    }
}
