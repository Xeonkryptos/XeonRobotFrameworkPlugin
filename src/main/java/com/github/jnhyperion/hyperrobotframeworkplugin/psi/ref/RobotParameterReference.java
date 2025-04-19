package com.github.jnhyperion.hyperrobotframeworkplugin.psi.ref;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.DefinedParameter;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordStatement;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.ParameterId;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

public class RobotParameterReference extends PsiReferenceBase<ParameterId> implements PsiReference {

    public RobotParameterReference(@NotNull ParameterId parameter) {
        super(parameter, false);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        ParameterId parameterId = getElement();
        String parameterName = parameterId.getName();
        KeywordStatement keywordStatement = PsiTreeUtil.getParentOfType(parameterId, KeywordStatement.class);
        if (keywordStatement != null && parameterName != null) {
            PsiElement reference = keywordStatement.getAvailableParameters()
                                                   .stream()
                                                   .filter(param -> parameterName.equals(param.getLookup()) || param.isKeywordContainer())
                                                   .min(Comparator.comparing(DefinedParameter::isKeywordContainer,
                                                                             (kc1, kc2) -> kc1 == kc2 ? 0 : kc1 ? 1 : -1))
                                                   .map(DefinedParameter::reference)
                                                   .orElse(null);
            if (reference != null) {
                return reference;
            }
            // Fall back to PyFunction element. The parameter itself couldn't be found
            PsiReferenceResultWithImportPath wrapper = ResolverUtils.findKeywordReference(keywordStatement.getName(), keywordStatement.getContainingFile());
            if (wrapper != null) {
                return wrapper.reference();
            }
        }
        return null;
    }
}
