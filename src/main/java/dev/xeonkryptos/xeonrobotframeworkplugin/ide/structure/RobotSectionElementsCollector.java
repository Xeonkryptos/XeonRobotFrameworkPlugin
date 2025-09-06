package dev.xeonkryptos.xeonrobotframeworkplugin.ide.structure;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotExecutableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotGlobalSettingStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordsSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotRoot;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTaskStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTasksSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCasesSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariablesSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedHashSet;

final class RobotSectionElementsCollector extends RobotVisitor {

    private final Collection<RobotSection> sections = new LinkedHashSet<>();
    private final Collection<RobotGlobalSettingStatement> metadataStatements = new LinkedHashSet<>();
    private final Collection<RobotTestCaseStatement> testCases = new LinkedHashSet<>();
    private final Collection<RobotTaskStatement> tasks = new LinkedHashSet<>();
    private final Collection<RobotVariableDefinition> variableDefinitions = new LinkedHashSet<>();
    private final Collection<RobotUserKeywordStatement> userKeywordStatements = new LinkedHashSet<>();
    private final Collection<RobotKeywordCall> keywordCalls = new LinkedHashSet<>();

    private boolean ignoreKeywordCalls;

    @Override
    public void visitRoot(@NotNull RobotRoot o) {
        super.visitRoot(o);
        o.acceptChildren(this);
    }

    @Override
    public void visitSection(@NotNull RobotSection o) {
        super.visitSection(o);
        sections.add(o);
    }

    @Override
    public void visitVariablesSection(@NotNull RobotVariablesSection o) {
        super.visitVariablesSection(o);
    }

    @Override
    public void visitTestCasesSection(@NotNull RobotTestCasesSection o) {
        super.visitTestCasesSection(o);
    }

    @Override
    public void visitTasksSection(@NotNull RobotTasksSection o) {
        super.visitTasksSection(o);
    }

    @Override
    public void visitKeywordsSection(@NotNull RobotKeywordsSection o) {
        super.visitKeywordsSection(o);
    }

    @Override
    public void visitGlobalSettingStatement(@NotNull RobotGlobalSettingStatement o) {
        super.visitGlobalSettingStatement(o);
        metadataStatements.add(o);
    }

    @Override
    public void visitTestCaseStatement(@NotNull RobotTestCaseStatement o) {
        super.visitTestCaseStatement(o);
        testCases.add(o);
    }

    @Override
    public void visitTaskStatement(@NotNull RobotTaskStatement o) {
        super.visitTaskStatement(o);
        tasks.add(o);
    }

    @Override
    public void visitExecutableStatement(@NotNull RobotExecutableStatement o) {
        super.visitExecutableStatement(o);
        o.acceptChildren(this);
    }

    @Override
    public void visitVariableStatement(@NotNull RobotVariableStatement o) {
        super.visitVariableStatement(o);
        ignoreKeywordCalls = true;
        o.acceptChildren(this);
        ignoreKeywordCalls = false;
    }

    @Override
    public void visitUserKeywordStatement(@NotNull RobotUserKeywordStatement o) {
        super.visitUserKeywordStatement(o);
        userKeywordStatements.add(o);
    }

    @Override
    public void visitVariableDefinition(@NotNull RobotVariableDefinition o) {
        super.visitVariableDefinition(o);
        variableDefinitions.add(o);
    }

    @Override
    public void visitKeywordCall(@NotNull RobotKeywordCall o) {
        super.visitKeywordCall(o);
        if (!ignoreKeywordCalls) {
            keywordCalls.add(o);
        }
    }

    public Collection<RobotSection> getSections() {
        return sections;
    }

    public Collection<RobotGlobalSettingStatement> getMetadataStatements() {
        return metadataStatements;
    }

    public Collection<RobotTestCaseStatement> getTestCases() {
        return testCases;
    }

    public Collection<RobotTaskStatement> getTasks() {
        return tasks;
    }

    public Collection<RobotUserKeywordStatement> getUserKeywordStatements() {
        return userKeywordStatements;
    }

    public Collection<RobotKeywordCall> getKeywordCalls() {
        return keywordCalls;
    }

    public Collection<RobotVariableDefinition> getVariableDefinitions() {
        return variableDefinitions;
    }
}
