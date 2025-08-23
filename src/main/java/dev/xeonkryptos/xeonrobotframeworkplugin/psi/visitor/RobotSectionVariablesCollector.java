package dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor;

import com.intellij.psi.PsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotExecutableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotGlobalSettingStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotRoot;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSettingsSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSetupTeardownStatementsGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSingleVariableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariablesSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public final class RobotSectionVariablesCollector extends RobotVisitor {

    private final Set<DefinedVariable> variables = new LinkedHashSet<>();

    @Override
    public void visitRoot(@NotNull RobotRoot o) {
        o.acceptChildren(this);
    }

    @Override
    public void visitVariablesSection(@NotNull RobotVariablesSection o) {
        o.acceptChildren(this);
    }

    @Override
    public void visitSettingsSection(@NotNull RobotSettingsSection o) {
        o.acceptChildren(this);
    }

    @Override
    public void visitSingleVariableStatement(@NotNull RobotSingleVariableStatement o) {
        o.acceptChildren(this);
    }

    @Override
    public void visitVariableDefinition(@NotNull RobotVariableDefinition o) {
        variables.add(o);
    }

    @Override
    public void visitGlobalSettingStatement(@NotNull RobotGlobalSettingStatement o) {
        o.acceptChildren(this);
    }

    @Override
    public void visitSetupTeardownStatementsGlobalSetting(@NotNull RobotSetupTeardownStatementsGlobalSetting o) {
        String settingName = o.getNameElement().getText();
        if ("Suite Setup".equalsIgnoreCase(settingName) || "Test Setup".equalsIgnoreCase(settingName)) {
            o.acceptChildren(this);
        }
    }

    @Override
    public void visitUserKeywordStatement(@NotNull RobotUserKeywordStatement o) {
        o.acceptChildren(this);
    }

    @Override
    public void visitExecutableStatement(@NotNull RobotExecutableStatement o) {
        o.acceptChildren(this);
    }

    @Override
    public void visitVariableStatement(@NotNull RobotVariableStatement o) {
        o.acceptChildren(this);
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
