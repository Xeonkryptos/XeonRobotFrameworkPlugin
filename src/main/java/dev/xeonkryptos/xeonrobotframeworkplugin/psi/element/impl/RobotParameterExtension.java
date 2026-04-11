package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.CachedValueProvider.Result;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.ParameterizedCachedValue;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.KeywordUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotNames;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public abstract class RobotParameterExtension extends RobotPsiElementBase implements RobotParameter {

    private static final Key<ParameterizedCachedValue<Boolean, RobotKeywordCall>> IS_FAKE_PARAMETER_CACHE_KEY = Key.create("IS_FAKE_PARAMETER_CACHE_KEY");

    public RobotParameterExtension(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public boolean isFakeParameter() {
        RobotKeywordCall keywordCall = PsiTreeUtil.getParentOfType(this, RobotKeywordCall.class);
        if (keywordCall != null) {
            CachedValuesManager.getManager(getProject()).getParameterizedCachedValue(this, IS_FAKE_PARAMETER_CACHE_KEY, kc -> {
                PsiElement resolvedReference = kc.getNameIdentifier().getReference().resolve();
                Collection<DefinedParameter> availableParameters = kc.getAvailableParameters();
                String parameterName = getParameterName();

                boolean directMatchFound = availableParameters.stream().anyMatch(param -> param.matches(parameterName));
                boolean noKeywordContainerFound = availableParameters.stream().noneMatch(DefinedParameter::isKeywordContainer);

                boolean result = false;
                if (!directMatchFound && noKeywordContainerFound && kc.getStartOfKeywordsOnlyIndex().isEmpty()) {
                    String keywordCallName = kc.getName();
                    String normalizedKeywordCallName = KeywordUtil.normalizeKeywordName(keywordCallName);
                    // Create Dictionary keyword calls have special handling for parameters
                    result = !RobotNames.CREATE_DICTIONARY_NORMALIZED_KEYWORD_NAME.equalsIgnoreCase(normalizedKeywordCallName);
                }
                return Result.create(result, this, resolvedReference, kc);
            }, false, keywordCall);
        }
        return PsiTreeUtil.getParentOfType(this, RobotLocalSetting.class) != null;
    }
}
