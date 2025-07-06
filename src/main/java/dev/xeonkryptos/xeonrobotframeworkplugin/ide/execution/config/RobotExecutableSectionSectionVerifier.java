package dev.xeonkryptos.xeonrobotframeworkplugin.ide.execution.config;

import com.intellij.psi.PsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordsSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotRoot;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTasksSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCasesSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import org.jetbrains.annotations.NotNull;

class RobotExecutableSectionSectionVerifier extends RobotVisitor {

    private boolean testCasesSection = false;
    private boolean tasksSection = false;
    private boolean userKeywordSection = false;

    public void reset() {
        testCasesSection = false;
        tasksSection = false;
        userKeywordSection = false;
    }

    @Override
    public void visitPsiElement(@NotNull PsiElement o) {
        PsiElement parent = o.getParent();
        if (parent != null) {
            parent.accept(this);
        }
    }

    @Override
    public void visitRoot(@NotNull RobotRoot o) {
        for (RobotSection robotSection : o.getSectionList()) {
            robotSection.accept(this);
        }
    }

    @Override
    public void visitTestCasesSection(@NotNull RobotTestCasesSection o) {
        testCasesSection = true;
    }

    @Override
    public void visitTasksSection(@NotNull RobotTasksSection o) {
        tasksSection = true;
    }

    @Override
    public void visitKeywordsSection(@NotNull RobotKeywordsSection o) {
        userKeywordSection = true;
    }

    public boolean hasTestCasesSection() {
        return testCasesSection;
    }

    public boolean hasTasksSection() {
        return tasksSection;
    }

    public boolean hasOnlyTasksSection() {
        return tasksSection && !testCasesSection;
    }

    public boolean hasOnlyTestCasesSection() {
        return testCasesSection && !tasksSection;
    }

    public boolean isExecutable() {
        return testCasesSection || tasksSection || userKeywordSection;
    }
}
