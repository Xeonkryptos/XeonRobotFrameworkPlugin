package dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotInlineVariableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordVariableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSettingsSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSingleVariableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatementId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import org.jetbrains.annotations.NotNull;

public class RobotStoppablePsiElement extends RobotVisitor {

    private boolean stoppable = false;

    @Override
    public void visitKeywordCall(@NotNull RobotKeywordCall o) {
        stoppable = true;
    }

    @Override
    public void visitInlineVariableStatement(@NotNull RobotInlineVariableStatement o) {
        stoppable = true;
    }

    @Override
    public void visitSingleVariableStatement(@NotNull RobotSingleVariableStatement o) {
        stoppable = true;
    }

    @Override
    public void visitKeywordVariableStatement(@NotNull RobotKeywordVariableStatement o) {
        stoppable = true;
    }

    @Override
    public void visitUserKeywordStatement(@NotNull RobotUserKeywordStatement o) {
        stoppable = true;
    }

    @Override
    public void visitUserKeywordStatementId(@NotNull RobotUserKeywordStatementId o) {
        stoppable = true;
    }

    @Override
    public void visitSettingsSection(@NotNull RobotSettingsSection o) {
        stoppable = true;
    }

    public boolean isStoppable() {
        return stoppable;
    }
}
