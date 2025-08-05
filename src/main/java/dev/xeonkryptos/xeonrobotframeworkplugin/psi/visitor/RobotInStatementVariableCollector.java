package dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor;

import com.intellij.psi.PsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.VariableDto;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotExecutableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotInlineVariableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordVariableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTaskStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.ReservedVariableScope;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class RobotInStatementVariableCollector extends RobotVisitor {

    private final Set<PsiElement> parents;
    private final Set<DefinedVariable> availableVariables = new LinkedHashSet<>();

    private final int definitionEndOffset;

    public RobotInStatementVariableCollector(PsiElement baseElement) {
        parents = collectParentsOf(baseElement);
        definitionEndOffset = baseElement.getTextRange().getStartOffset();
    }

    @Override
    public void visitTestCaseStatement(@NotNull RobotTestCaseStatement o) {
        o.acceptChildren(this);
    }

    @Override
    public void visitTaskStatement(@NotNull RobotTaskStatement o) {
        o.acceptChildren(this);
    }

    @Override
    public void visitExecutableStatement(@NotNull RobotExecutableStatement o) {
        if (parents.contains(o.getParent())) {
            o.acceptChildren(this);
        }
    }

    @Override
    public void visitKeywordVariableStatement(@NotNull RobotKeywordVariableStatement o) {
        o.acceptChildren(this);
    }

    @Override
    public void visitInlineVariableStatement(@NotNull RobotInlineVariableStatement o) {
        o.acceptChildren(this);
    }

    @Override
    public void visitVariableDefinition(@NotNull RobotVariableDefinition o) {
        if (o.getTextRange().getStartOffset() < definitionEndOffset) {
            RobotVariable variable = o.getVariable();
            String wrappedVariableName = variable.getText();
            String variableName = variable.getVariableName();
            if (variableName != null) {
                VariableDto variableDto = new VariableDto(o, wrappedVariableName, variableName, ReservedVariableScope.TestCase);
                availableVariables.add(variableDto);
            }
        }
    }

    private static Set<PsiElement> collectParentsOf(@NotNull PsiElement element) {
        Set<PsiElement> parents = new HashSet<>();
        PsiElement parent = element.getParent();
        while (parent != null && !(parent instanceof RobotTestCaseStatement) && !(parent instanceof RobotTaskStatement)) {
            parents.add(parent);
            parent = parent.getParent();
        }
        parents.add(parent);
        return parents;
    }

    public Set<DefinedVariable> getAvailableVariables() {
        return availableVariables;
    }
}
