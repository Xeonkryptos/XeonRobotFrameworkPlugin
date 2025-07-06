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
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
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

    public RobotVariableReferenceSearcher(RobotVariable variable) {
        this.variableName = variable.getName();
        this.parents = collectParentsOf(variable);
    }

    private static Set<PsiElement> collectParentsOf(@NotNull RobotVariable variable) {
        Set<PsiElement> parents = new HashSet<>();
        PsiElement parent = variable.getParent();
        while (parent != null && !(parent instanceof RobotRoot)) {
            parents.add(parent);
            parent = parent.getParent();
        }
        return parents;
    }

    @Override
    public void visitPsiElement(@NotNull PsiElement o) {
        super.visitPsiElement(o);
        if (canVisitNextElements(o) && !rootReached) {
            PsiElement parent = o.getParent();
            if (parent != null) {
                parent.accept(this);
            }
        }
    }

    @Override
    public void visitRoot(@NotNull RobotRoot o) {
        visitElement(o);
        rootReached = true;
        o.acceptChildren(this);
    }

    @Override
    public void visitVariablesSection(@NotNull RobotVariablesSection o) {
        visitElement(o);
        if (canVisitNextElements(o)) {
            o.acceptChildren(this);
        }
    }

    @Override
    public void visitKeywordsSection(@NotNull RobotKeywordsSection o) {
        visitElement(o);
        if (canVisitNextElements(o)) {
            o.acceptChildren(this);
        }
    }

    @Override
    public void visitTestCasesSection(@NotNull RobotTestCasesSection o) {
        visitElement(o);
        if (canVisitNextElements(o)) {
            o.acceptChildren(this);
        }
    }

    @Override
    public void visitTestCaseStatement(@NotNull RobotTestCaseStatement o) {
        visitElement(o);
        if (canVisitNextElements(o)) {
            o.acceptChildren(this);
        }
    }

    @Override
    public void visitTasksSection(@NotNull RobotTasksSection o) {
        visitElement(o);
        if (canVisitNextElements(o)) {
            o.acceptChildren(this);
        }
    }

    @Override
    public void visitTaskStatement(@NotNull RobotTaskStatement o) {
        visitElement(o);
        if (canVisitNextElements(o)) {
            o.acceptChildren(this);
        }
    }

    @Override
    public void visitVariableStatement(@NotNull RobotVariableStatement o) {
        visitElement(o);
        if (canVisitNextElements(o)) {
            o.acceptChildren(this);
        }
    }

    @Override
    public void visitExecutableStatement(@NotNull RobotExecutableStatement o) {
        visitElement(o);
        if (!rootReached && canVisitNextElements(o)) {
            o.acceptChildren(this);
        }
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
    }

    @Override
    public void visitEnvironmentVariable(@NotNull RobotEnvironmentVariable o) {
        // Don't try to look for a variable definition with this kind. Environment variables are not defined in the same way as regular variables.
    }

    private boolean canVisitNextElements(@NotNull PsiElement sourceElement) {
        return isPartOfWalkingTree(sourceElement) && visitedElements.add(sourceElement) && foundElement == null;
    }

    private boolean isPartOfWalkingTree(@NotNull PsiElement element) {
        PsiElement parent = element.getParent();
        return parents.contains(element) || (parent != null && parents.contains(parent));
    }

    public PsiElement getFoundElement() {
        return foundElement;
    }
}
