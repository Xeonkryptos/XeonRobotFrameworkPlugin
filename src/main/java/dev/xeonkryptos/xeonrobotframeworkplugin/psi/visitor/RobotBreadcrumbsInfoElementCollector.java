package dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotForLoopStructure;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotIfStructure;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTaskStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTryStructure;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotWhileLoopStructure;
import org.jetbrains.annotations.NotNull;

public final class RobotBreadcrumbsInfoElementCollector extends RobotVisitor {

    private boolean sticky;
    private boolean includeInBreadcrumbs;

    @Override
    public void visitSection(@NotNull RobotSection o) {
        sticky = true;
        includeInBreadcrumbs = true;
    }

    @Override
    public void visitUserKeywordStatement(@NotNull RobotUserKeywordStatement o) {
        sticky = true;
        includeInBreadcrumbs = true;
    }

    @Override
    public void visitTestCaseStatement(@NotNull RobotTestCaseStatement o) {
        sticky = true;
        includeInBreadcrumbs = true;
    }

    @Override
    public void visitTaskStatement(@NotNull RobotTaskStatement o) {
        sticky = true;
        includeInBreadcrumbs = true;
    }

    @Override
    public void visitKeywordCall(@NotNull RobotKeywordCall o) {
        sticky = true;
        includeInBreadcrumbs = true;
    }

    @Override
    public void visitVariableDefinition(@NotNull RobotVariableDefinition o) {
        sticky = true;
    }

    @Override
    public void visitVariableStatement(@NotNull RobotVariableStatement o) {
        sticky = true;
        includeInBreadcrumbs = true;
    }

    @Override
    public void visitForLoopStructure(@NotNull RobotForLoopStructure o) {
        sticky = true;
        includeInBreadcrumbs = true;
    }

    @Override
    public void visitWhileLoopStructure(@NotNull RobotWhileLoopStructure o) {
        sticky = true;
        includeInBreadcrumbs = true;
    }

    @Override
    public void visitTryStructure(@NotNull RobotTryStructure o) {
        sticky = true;
        includeInBreadcrumbs = true;
    }

    @Override
    public void visitIfStructure(@NotNull RobotIfStructure o) {
        sticky = true;
        includeInBreadcrumbs = true;
    }

    public boolean isSticky() {
        return sticky;
    }

    public boolean isIncludeInBreadcrumbs() {
        return includeInBreadcrumbs;
    }
}
