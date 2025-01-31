package com.github.jnhyperion.hyperrobotframeworkplugin.ide.usage;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordDefinitionImpl;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.usages.PsiNamedElementUsageGroupBase;
import com.intellij.usages.Usage;
import com.intellij.usages.UsageGroup;
import com.intellij.usages.impl.FileStructureGroupRuleProvider;
import com.intellij.usages.rules.PsiElementUsage;
import com.intellij.usages.rules.UsageGroupingRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotKeywordGroupingRuleProvider implements FileStructureGroupRuleProvider {

    @Nullable
    @Override
    public UsageGroupingRule getUsageGroupingRule(@NotNull Project project) {
        return new RobotKeywordGroupingRule();
    }

    private static class RobotKeywordGroupingRule implements UsageGroupingRule {

        @Override
        public UsageGroup groupUsage(@NotNull Usage usage) {
            if (!(usage instanceof PsiElementUsage)) {
                return null;
            } else {
                PsiElement psiElement = ((PsiElementUsage) usage).getElement();
                KeywordDefinitionImpl definition = PsiTreeUtil.getParentOfType(psiElement, KeywordDefinitionImpl.class, false);
                return definition == null ? null : new PsiNamedElementUsageGroupBase<>(definition);
            }
        }
    }
}
