package dev.xeonkryptos.xeonrobotframeworkplugin.execution.config;

import com.intellij.psi.PsiFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotRoot;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTasksSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCasesSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import org.jetbrains.annotations.NotNull;

class RobotExecutableSectionSectionVerifier extends RobotVisitor {

    private boolean testCasesSection = false;
    private boolean tasksSection = false;

    @Override
    public void visitFile(@NotNull PsiFile file) {
        super.visitFile(file);
        file.acceptChildren(this);
    }

    @Override
    public void visitRoot(@NotNull RobotRoot o) {
        o.acceptChildren(this);
    }

    @Override
    public void visitTestCasesSection(@NotNull RobotTestCasesSection o) {
        testCasesSection = true;
    }

    @Override
    public void visitTasksSection(@NotNull RobotTasksSection o) {
        tasksSection = true;
    }

    public boolean isExecutable() {
        return testCasesSection || tasksSection;
    }
}
