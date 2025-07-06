package dev.xeonkryptos.xeonrobotframeworkplugin.ide.structure;

import com.intellij.psi.PsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotGlobalSettingStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedHashSet;

final class RobotSectionElementsCollector extends RobotVisitor {

    private final Collection<PsiElement> metadataStatements = new LinkedHashSet<>();
    private final Collection<RobotTestCaseStatement> testCases = new LinkedHashSet<>();
    private final Collection<RobotVariableStatement> variableStatements = new LinkedHashSet<>();
    private final Collection<RobotUserKeywordStatement> userKeywordStatements = new LinkedHashSet<>();

    @Override
    public void visitGlobalSettingStatement(@NotNull RobotGlobalSettingStatement o) {
        metadataStatements.add(o);
    }

    @Override
    public void visitTestCaseStatement(@NotNull RobotTestCaseStatement o) {
        testCases.add(o);
    }

    @Override
    public void visitVariableStatement(@NotNull RobotVariableStatement o) {
        variableStatements.add(o);
    }

    @Override
    public void visitUserKeywordStatement(@NotNull RobotUserKeywordStatement o) {
        userKeywordStatements.add(o);
    }

    public Collection<PsiElement> getMetadataStatements() {
        return metadataStatements;
    }

    public Collection<RobotTestCaseStatement> getTestCases() {
        return testCases;
    }

    public Collection<RobotVariableStatement> getVariableStatements() {
        return variableStatements;
    }

    public Collection<RobotUserKeywordStatement> getUserKeywordStatements() {
        return userKeywordStatements;
    }
}
