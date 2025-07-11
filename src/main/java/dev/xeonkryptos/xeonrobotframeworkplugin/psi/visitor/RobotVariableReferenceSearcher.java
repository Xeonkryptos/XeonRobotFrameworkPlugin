package dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor;

import com.intellij.psi.PsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotBlockOpeningStructure;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotEnvironmentVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotExecutableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordsSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotRoot;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTaskStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTasksSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCasesSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariablesSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class RobotVariableReferenceSearcher extends RobotVisitor {

    private final Set<PsiElement> visitedElements = new HashSet<>();
    private final Set<PsiElement> parents;

    private final String variableName;

    private PsiElement foundElement;

    private boolean rootReached = false;

    public RobotVariableReferenceSearcher(RobotVariableId variableId) {
        this.variableName = variableId.getName();
        this.parents = collectParentsOf(variableId);
    }

    private static Set<PsiElement> collectParentsOf(@NotNull PsiElement element) {
        Set<PsiElement> parents = new HashSet<>();
        PsiElement parent = element.getParent();
        while (parent != null && !(parent instanceof RobotTestCasesSection)) {
            parents.add(parent);
            parent = parent.getParent();
        }
        return parents;
    }

    @Override
    public void visitElement(@NotNull PsiElement o) {
        if (canVisitNextElements(o) && !rootReached) {
            PsiElement parent = o.getParent();
            if (parent != null) {
                parent.accept(this);
            }
        }
    }

    @Override
    public void visitRoot(@NotNull RobotRoot o) {
        rootReached = true;
        o.acceptChildren(this);
        visitedElements.add(o);
    }

    @Override
    public void visitVariablesSection(@NotNull RobotVariablesSection o) {
        visitElement(o);
        if (canVisitNextElements(o)) {
            o.acceptChildren(this);
        }
        visitedElements.add(o);
    }

    @Override
    public void visitKeywordsSection(@NotNull RobotKeywordsSection o) {
        visitElement(o);
        if (parents.contains(o) && canVisitNextElements(o)) {
            o.acceptChildren(this);
        }
        visitedElements.add(o);
    }

    @Override
    public void visitTestCasesSection(@NotNull RobotTestCasesSection o) {
        visitElement(o);
        if (parents.contains(o) && canVisitNextElements(o)) {
            o.acceptChildren(this);
        }
        visitedElements.add(o);
    }

    @Override
    public void visitTestCaseStatement(@NotNull RobotTestCaseStatement o) {
        visitElement(o);
        if (canVisitNextElements(o)) {
            o.acceptChildren(this);
        }
        visitedElements.add(o);
    }

    @Override
    public void visitTasksSection(@NotNull RobotTasksSection o) {
        visitElement(o);
        if (parents.contains(o) && canVisitNextElements(o)) {
            o.acceptChildren(this);
        }
        visitedElements.add(o);
    }

    @Override
    public void visitTaskStatement(@NotNull RobotTaskStatement o) {
        visitElement(o);
        if (canVisitNextElements(o)) {
            o.acceptChildren(this);
        }
        visitedElements.add(o);
    }

    @Override
    public void visitVariableStatement(@NotNull RobotVariableStatement o) {
        visitElement(o);
        if (canVisitNextElements(o)) {
            o.acceptChildren(this);
        }
        visitedElements.add(o);
    }

    @Override
    public void visitExecutableStatement(@NotNull RobotExecutableStatement o) {
        visitElement(o);
        if (canVisitNextElements(o)) {
            o.acceptChildren(this);
        }
        visitedElements.add(o);
    }

    @Override
    public void visitBlockOpeningStructure(@NotNull RobotBlockOpeningStructure o) {
        visitElement(o);
        // canVisitNextElements contains the check for if this element is part of the walking tree (being in a direct heritage of the variable), but it also
        // allows its parent to be part of it to still visit children. This doesn't work for block opening structures. They are opening a new scope block
        // we don't have any access to
        if (parents.contains(o) && canVisitNextElements(o)) {
            o.acceptChildren(this);
        }
        visitedElements.add(o);
    }

    @Override
    public void visitVariableDefinition(@NotNull RobotVariableDefinition o) {
        visitElement(o);
        if (canVisitNextElements(o)) {
            String definedVariableName = o.getName();
            if (variableName.equals(definedVariableName)) {
                foundElement = o;
            }
        }
        visitedElements.add(o);
    }

    @Override
    public void visitEnvironmentVariable(@NotNull RobotEnvironmentVariable o) {
        // Don't try to look for a variable definition with this kind. Environment variables are not defined in the same way as regular variables.
    }

    private boolean canVisitNextElements(@NotNull PsiElement sourceElement) {
        return !visitedElements.contains(sourceElement) && foundElement == null;
    }

    public PsiElement getFoundElement() {
        return foundElement;
    }
}
