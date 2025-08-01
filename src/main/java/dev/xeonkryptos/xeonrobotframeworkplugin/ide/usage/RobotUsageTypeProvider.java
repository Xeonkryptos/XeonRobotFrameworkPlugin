package dev.xeonkryptos.xeonrobotframeworkplugin.ide.usage;

import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import com.intellij.psi.PsiElement;
import com.intellij.usages.UsageTarget;
import com.intellij.usages.impl.rules.UsageType;
import com.intellij.usages.impl.rules.UsageTypeProviderEx;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotUsageTypeProvider implements UsageTypeProviderEx {

    private static final UsageType KEYWORD_USAGE_TYPE = new UsageType(() -> RobotBundle.getMessage("usage.type.keyword"));

    @Nullable
    @Override
    public UsageType getUsageType(@NotNull PsiElement element) {
        return getUsageType(element, UsageTarget.EMPTY_ARRAY);
    }

    @Nullable
    @Override
    public UsageType getUsageType(PsiElement element, UsageTarget @NotNull [] targets) {
        if (element instanceof RobotKeywordCall || element instanceof RobotKeywordCallName) {
            return KEYWORD_USAGE_TYPE;
        }
        return null;
    }
}
