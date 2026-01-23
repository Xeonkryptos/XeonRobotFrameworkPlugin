package dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor;

import com.intellij.psi.PsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotExecutableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTaskStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class RobotInStatementVariableCollector extends RecursiveRobotVisitor {

    private final Set<PsiElement> parents;
    private final Set<DefinedVariable> availableVariables = new LinkedHashSet<>();

    private final int definitionEndOffset;

    public RobotInStatementVariableCollector(PsiElement baseElement) {
        parents = collectParentsOf(baseElement);
        definitionEndOffset = baseElement.getTextRange().getStartOffset();
    }

    @Override
    public void visitExecutableStatement(@NotNull RobotExecutableStatement o) {
        if (parents.contains(o.getParent())) {
            o.acceptChildren(this);
        }
    }

    @Override
    public void visitVariableDefinition(@NotNull RobotVariableDefinition o) {
        if (o.getTextRange().getStartOffset() < definitionEndOffset) {
            String variableName = o.getName();
            if (variableName != null) {
                availableVariables.add(o);
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
