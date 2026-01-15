package dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref;

import com.intellij.openapi.project.Project;
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
        RobotTemplateParameterId parameterId = getElement();
        Project project = parameterId.getProject();
        ResolveCache resolveCache = ResolveCache.getInstance(project);
        return resolveCache.resolveWithCaching(this, (robotParameterReference, incompleteCode) -> {
            String parameterName = parameterId.getText();
            RobotKeywordCall keywordCall = KeywordUtil.findTemplateKeywordCall(parameterId);
            return keywordCall != null ? keywordCall.findParameterReference(parameterName) : null;
        }, false, false);
    }
}
