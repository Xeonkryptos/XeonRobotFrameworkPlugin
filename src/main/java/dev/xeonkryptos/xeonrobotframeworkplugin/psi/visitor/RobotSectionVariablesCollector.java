package dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor;

import com.intellij.psi.PsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSetupTeardownStatementsGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public final class RobotSectionVariablesCollector extends RecursiveRobotVisitor {

    private final Set<DefinedVariable> variables = new LinkedHashSet<>();

    @Override
    public void visitVariableDefinition(@NotNull RobotVariableDefinition o) {
        variables.add(o);
    }

    @Override
    public void visitSetupTeardownStatementsGlobalSetting(@NotNull RobotSetupTeardownStatementsGlobalSetting o) {
        String settingName = o.getNameElement().getText();
        if ("Suite Setup".equalsIgnoreCase(settingName) || "Test Setup".equalsIgnoreCase(settingName)) {
            o.acceptChildren(this);
        }
    }

    @Override
    public void visitKeywordCall(@NotNull RobotKeywordCall o) {
        PsiElement resolvedElement = o.getKeywordCallName().getReference().resolve();
        if (resolvedElement instanceof RobotUserKeywordStatement userKeywordStatement) {
            RobotSectionVariablesCollector variablesCollector = new RobotSectionVariablesCollector();
            userKeywordStatement.accept(variablesCollector);
            Collection<DefinedVariable> collectedVariables = variablesCollector.getVariables();
            variables.addAll(collectedVariables);
        }
    }

    public Collection<DefinedVariable> getVariables() {
        return variables;
    }
}
