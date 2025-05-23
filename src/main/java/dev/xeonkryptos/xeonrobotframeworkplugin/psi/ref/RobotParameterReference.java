package dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.ParameterId;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.impl.source.resolve.ResolveCache;
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
        ResolveCache resolveCache = ResolveCache.getInstance(parameterId.getProject());
        return resolveCache.resolveWithCaching(this, (robotParameterReference, incompleteCode) -> {
            String parameterName = parameterId.getName();
            KeywordStatement keywordStatement = PsiTreeUtil.getParentOfType(parameterId, KeywordStatement.class);
            PsiElement reference = null;
            if (keywordStatement != null && parameterName != null) {
                reference = keywordStatement.getAvailableParameters()
                                            .stream()
                                            .filter(param -> parameterName.equals(param.getLookup()) || param.isKeywordContainer())
                                            .min(Comparator.comparing(DefinedParameter::isKeywordContainer, (kc1, kc2) -> kc1 == kc2 ? 0 : kc1 ? 1 : -1))
                                            .map(DefinedParameter::reference)
                                            .orElse(null);
                if (reference == null) {
                    // Fall back to PyFunction element. The parameter itself couldn't be found
                    String keywordStatementName = keywordStatement.getName();
                    PsiFile containingFile = keywordStatement.getContainingFile();
                    reference = ResolverUtils.findKeywordReference(keywordStatementName, containingFile);
                }
            }
            return reference;
        }, false, false);
    }
}
