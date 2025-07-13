package dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordsSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTasksSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCasesSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import org.jetbrains.annotations.NotNull;

public class RobotKeywordCallHolderSection extends RobotVisitor {

    private boolean keywordCallHolderSection = false;

    @Override
    public void visitTestCasesSection(@NotNull RobotTestCasesSection o) {
        keywordCallHolderSection = true;
    }

    @Override
    public void visitTasksSection(@NotNull RobotTasksSection o) {
        keywordCallHolderSection = true;
    }

    @Override
    public void visitKeywordsSection(@NotNull RobotKeywordsSection o) {
        keywordCallHolderSection = true;
    }

    public boolean isKeywordCallHolderSection() {
        return keywordCallHolderSection;
    }
}
