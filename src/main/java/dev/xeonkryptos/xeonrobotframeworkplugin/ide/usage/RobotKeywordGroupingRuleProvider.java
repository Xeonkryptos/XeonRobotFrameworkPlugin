package dev.xeonkryptos.xeonrobotframeworkplugin.ide.usage;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.usages.PsiNamedElementUsageGroupBase;
import com.intellij.usages.Usage;
import com.intellij.usages.UsageGroup;
import com.intellij.usages.UsageTarget;
import com.intellij.usages.impl.FileStructureGroupRuleProvider;
import com.intellij.usages.rules.PsiElementUsage;
import com.intellij.usages.rules.SingleParentUsageGroupingRule;
import com.intellij.usages.rules.UsageGroupingRule;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotKeywordGroupingRuleProvider implements FileStructureGroupRuleProvider {

    @Nullable
    @Override
    public UsageGroupingRule getUsageGroupingRule(@NotNull Project project) {
        return new RobotKeywordGroupingRule();
    }

    private static class RobotKeywordGroupingRule extends SingleParentUsageGroupingRule {

        @Nullable
        @Override
        protected UsageGroup getParentGroupFor(@NotNull Usage usage, UsageTarget @NotNull [] targets) {
            if (usage instanceof PsiElementUsage psiElementUsage) {
                PsiElement psiElement = psiElementUsage.getElement();
                RobotUserKeywordStatement definition = PsiTreeUtil.getParentOfType(psiElement, RobotUserKeywordStatement.class, false);
                return definition == null ? null : new PsiNamedElementUsageGroupBase<>(definition);
            }
            return null;
        }
    }
}
