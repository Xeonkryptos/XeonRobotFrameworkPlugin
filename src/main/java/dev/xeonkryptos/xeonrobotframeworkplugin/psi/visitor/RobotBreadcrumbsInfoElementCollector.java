package dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatementId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
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
    public void visitUserKeywordStatementId(@NotNull RobotUserKeywordStatementId o) {
        sticky = true;
        includeInBreadcrumbs = true;
    }

    @Override
    public void visitKeywordCall(@NotNull RobotKeywordCall o) {
        sticky = true;
        includeInBreadcrumbs = true;
    }

    @Override
    public void visitKeywordCallId(@NotNull RobotKeywordCallId o) {
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

    public boolean isSticky() {
        return sticky;
    }

    public boolean isIncludeInBreadcrumbs() {
        return includeInBreadcrumbs;
    }
}
