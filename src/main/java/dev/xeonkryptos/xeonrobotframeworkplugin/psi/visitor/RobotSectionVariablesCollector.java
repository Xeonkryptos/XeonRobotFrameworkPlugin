package dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotCommentsSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallName;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSetupTeardownStatementsGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTemplateArguments;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatementExpression;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotNames;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class RobotSectionVariablesCollector extends RecursiveRobotVisitor {

    private final Map<String, RobotKeywordCall> potentialKeywordCalls = new ConcurrentHashMap<>();

    private final Set<DefinedVariable> variables = new LinkedHashSet<>();
    private final Set<DefinedVariable> userKeywordVariables = ConcurrentHashMap.newKeySet();

    @Override
    public void visitCommentsSection(@NotNull RobotCommentsSection o) {
        // Do not visit comments section
    }

    @Override
    public void visitTemplateArguments(@NotNull RobotTemplateArguments o) {
        // Do not visit template arguments
    }

    @Override
    public void visitLocalSetting(@NotNull RobotLocalSetting o) {
        // Do not visit local settings
    }

    @Override
    public void visitSetupTeardownStatementsGlobalSetting(@NotNull RobotSetupTeardownStatementsGlobalSetting o) {
        String settingName = o.getNameElement().getText();
        if (RobotNames.SUITE_SETUP_GLOBAL_SETTING_NAME.equalsIgnoreCase(settingName) || RobotNames.TEST_SETUP_GLOBAL_SETTING_NAME.equalsIgnoreCase(settingName)) {
            o.acceptChildren(this);
        }
    }

    @Override
    public void visitVariableDefinition(@NotNull RobotVariableDefinition o) {
        variables.add(o);
    }

    @Override
    public void visitKeywordCall(@NotNull RobotKeywordCall o) {
        RobotKeywordCallName keywordCallName = o.getKeywordCallName();
        String keywordCallNameString = keywordCallName.getText();
        potentialKeywordCalls.putIfAbsent(keywordCallNameString, o);
    }

    public Collection<DefinedVariable> computeUserKeywordVariables() {
        potentialKeywordCalls.values()
                             .stream()
                             .map(keywordCall -> keywordCall.getKeywordCallName().getReference().resolve())
                             .filter(referenceCall -> referenceCall instanceof RobotUserKeywordStatement)
                             .map(referenceCall -> (RobotUserKeywordStatement) referenceCall)
                             .distinct()
                             .map(RobotUserKeywordStatementExpression::getGlobalVariables)
                             .forEach(userKeywordVariables::addAll);
        return userKeywordVariables;
    }

    public Collection<DefinedVariable> getVariables() {
        return variables;
    }
}
