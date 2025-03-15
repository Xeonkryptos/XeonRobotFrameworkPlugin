package com.github.jnhyperion.hyperrobotframeworkplugin.psi.ref;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordStatement;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.ParameterId;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotParameterReference extends PsiReferenceBase<ParameterId> implements PsiReference {

    public RobotParameterReference(@NotNull ParameterId parameter) {
        super(parameter, false);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        PsiElement result = null;

        ParameterId parameterId = getElement();
        KeywordStatement keywordStatement = PsiTreeUtil.getParentOfType(parameterId, KeywordStatement.class);
        if (keywordStatement != null) {
                String parameterName = parameterId.getName();
                PsiReferenceResultWithImportPath wrapper = ResolverUtils.findKeywordParameterElement(parameterName, keywordStatement);
                if (wrapper == null) {
                    // Fall back to PyFunction element. The parameter itself couldn't be found
                    wrapper = ResolverUtils.findKeywordReference(keywordStatement.getName(), keywordStatement.getContainingFile());
                } else {
                    return wrapper.reference();
                }
                if (wrapper == null) {
                    return null;
                }
                return wrapper.reference();
        }

        return result;
    }
}
