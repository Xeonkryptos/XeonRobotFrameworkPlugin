package dev.xeonkryptos.xeonrobotframeworkplugin.psi.reference;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTemplateParameterId;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.KeywordUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotTemplateParameterReference extends PsiReferenceBase<RobotTemplateParameterId> implements PsiReference {

    public RobotTemplateParameterReference(@NotNull RobotTemplateParameterId parameter) {
        super(parameter, false);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveCache resolveCache = ResolveCache.getInstance(getElement().getProject());
        return resolveCache.resolveWithCaching(this, (robotParameterReference, incompleteCode) -> {
            RobotTemplateParameterId parameterId = robotParameterReference.getElement();
            String parameterName = parameterId.getText();
            RobotKeywordCall keywordCall = KeywordUtil.findTemplateKeywordCall(parameterId);
            return keywordCall != null ? keywordCall.findParameterReference(parameterName) : null;
        }, false, false);
    }
}
