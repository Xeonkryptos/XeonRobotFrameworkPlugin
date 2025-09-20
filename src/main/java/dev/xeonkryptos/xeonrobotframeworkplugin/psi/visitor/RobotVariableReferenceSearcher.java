package dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor;

import com.intellij.psi.PsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotExecutableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordsSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotRoot;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTaskStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTasksSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCasesSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableBodyId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariablesSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.VariableNameUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class RobotVariableReferenceSearcher extends RecursiveRobotVisitor {

    private final Set<PsiElement> parents;
    private final String variableName;
    private final Set<String> variableNameVariants;

    private final Set<PsiElement> foundElements = new LinkedHashSet<>();

    private boolean inOvershadowingContext = false;
    private boolean overshadows = false;

    public RobotVariableReferenceSearcher(RobotVariableBodyId variableBodyId) {
        this.variableName = variableBodyId.getText();
        this.variableNameVariants = VariableNameUtil.INSTANCE.computeVariableNameVariants(variableName);
        this.parents = collectParentsOf(variableBodyId);
    }

    private static Set<PsiElement> collectParentsOf(@NotNull PsiElement element) {
        Set<PsiElement> parents = new HashSet<>();
        PsiElement parent = element.getParent();
        while (parent != null && !(parent instanceof RobotRoot)) {
            parents.add(parent);
            parent = parent.getParent();
        }
        return parents;
    }

    @Override
    public void visitVariablesSection(@NotNull RobotVariablesSection o) {
        inOvershadowingContext = true;
        super.visitVariablesSection(o);
        inOvershadowingContext = false;
    }

    @Override
    public void visitKeywordsSection(@NotNull RobotKeywordsSection o) {
        if (parents.contains(o)) {
            super.visitKeywordsSection(o);
        }
    }

    @Override
    public void visitTestCasesSection(@NotNull RobotTestCasesSection o) {
        if (parents.contains(o)) {
            super.visitTestCasesSection(o);
        }
    }

    @Override
    public void visitTasksSection(@NotNull RobotTasksSection o) {
        if (parents.contains(o)) {
            super.visitTasksSection(o);
        }
    }

    @Override
    public void visitUserKeywordStatement(@NotNull RobotUserKeywordStatement o) {
        if (parents.contains(o)) {
            super.visitUserKeywordStatement(o);
        }
    }

    @Override
    public void visitTestCaseStatement(@NotNull RobotTestCaseStatement o) {
        if (parents.contains(o)) {
            super.visitTestCaseStatement(o);
        }
    }

    @Override
    public void visitTaskStatement(@NotNull RobotTaskStatement o) {
        if (parents.contains(o)) {
            super.visitTaskStatement(o);
        }
    }

    @Override
    public void visitLocalArgumentsSetting(@NotNull RobotLocalArgumentsSetting o) {
        inOvershadowingContext = true;
        super.visitLocalArgumentsSetting(o);
        inOvershadowingContext = false;
    }

    @Override
    public void visitExecutableStatement(@NotNull RobotExecutableStatement o) {
        if (parents.contains(o) || parents.contains(o.getParent())) {
            super.visitExecutableStatement(o);
        }
    }

    @Override
    public void visitVariableDefinition(@NotNull RobotVariableDefinition o) {
        if (o.matches(variableName)) {
            foundElements.add(o);
            overshadows |= inOvershadowingContext;
        }
    }

    @Override
    public void visitVariable(@NotNull RobotVariable o) {
        String variableName = o.getVariableName();
        if (VariableNameUtil.INSTANCE.matchesVariableName(variableName, variableNameVariants)) {
            foundElements.add(o);
        }
    }

    public Collection<PsiElement> getFoundElements() {
        return foundElements;
    }

    public boolean isOvershadowing() {
        return overshadows;
    }
}
