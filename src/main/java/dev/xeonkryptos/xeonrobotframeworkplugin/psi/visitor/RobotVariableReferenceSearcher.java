package dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor;

import com.intellij.psi.PsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotExecutableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordsSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSettingArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotRoot;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTaskStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTasksSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCasesSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableBodyId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariablesSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class RobotVariableReferenceSearcher extends RobotVisitor {

    private final Set<PsiElement> parents;
    private final String variableName;

    private final Set<PsiElement> foundElements = new LinkedHashSet<>();

    private boolean inOvershadowingContext = false;
    private boolean overshadows = false;

    public RobotVariableReferenceSearcher(RobotVariableBodyId variableBodyId) {
        this.variableName = variableBodyId.getText().trim();
        this.parents = collectParentsOf(variableBodyId);
    }

    private static Set<PsiElement> collectParentsOf(@NotNull PsiElement element) {
        Set<PsiElement> parents = new HashSet<>();
        PsiElement parent = element.getParent();
        while (parent != null && !(parent instanceof RobotFile)) {
            parents.add(parent);
            parent = parent.getParent();
        }
        return parents;
    }

    @Override
    public void visitRoot(@NotNull RobotRoot o) {
        super.visitRoot(o);
        o.acceptChildren(this);
    }

    @Override
    public void visitVariablesSection(@NotNull RobotVariablesSection o) {
        super.visitVariablesSection(o);
        inOvershadowingContext = true;
        o.acceptChildren(this);
        inOvershadowingContext = false;
    }

    @Override
    public void visitKeywordsSection(@NotNull RobotKeywordsSection o) {
        super.visitKeywordsSection(o);
        if (parents.contains(o)) {
            o.acceptChildren(this);
        }
    }

    @Override
    public void visitTestCasesSection(@NotNull RobotTestCasesSection o) {
        super.visitTestCasesSection(o);
        if (parents.contains(o)) {
            o.acceptChildren(this);
        }
    }

    @Override
    public void visitTestCaseStatement(@NotNull RobotTestCaseStatement o) {
        super.visitTestCaseStatement(o);
        o.acceptChildren(this);
    }

    @Override
    public void visitTasksSection(@NotNull RobotTasksSection o) {
        super.visitTasksSection(o);
        if (parents.contains(o)) {
            o.acceptChildren(this);
        }
    }

    @Override
    public void visitTaskStatement(@NotNull RobotTaskStatement o) {
        super.visitTaskStatement(o);
        o.acceptChildren(this);
    }

    @Override
    public void visitUserKeywordStatement(@NotNull RobotUserKeywordStatement o) {
        super.visitUserKeywordStatement(o);
        if (parents.contains(o)) {
            o.acceptChildren(this);
        }
    }

    @Override
    public void visitLocalArgumentsSetting(@NotNull RobotLocalArgumentsSetting o) {
        super.visitLocalArgumentsSetting(o);
        inOvershadowingContext = true;
        o.acceptChildren(this);
        inOvershadowingContext = false;
    }

    @Override
    public void visitLocalArgumentsSettingArgument(@NotNull RobotLocalArgumentsSettingArgument o) {
        super.visitLocalArgumentsSettingArgument(o);
        RobotVariableDefinition variableDefinition = o.getVariableDefinition();
        String name = variableDefinition.getName();
        if (name != null && variableName.equalsIgnoreCase(name.trim())) {
            overshadows = true;
            foundElements.add(variableDefinition);
        }
    }

    @Override
    public void visitVariableStatement(@NotNull RobotVariableStatement o) {
        super.visitVariableStatement(o);
        o.acceptChildren(this);
    }

    @Override
    public void visitExecutableStatement(@NotNull RobotExecutableStatement o) {
        super.visitExecutableStatement(o);
        if (parents.contains(o) || parents.contains(o.getParent())) {
            o.acceptChildren(this);
        }
    }

    @Override
    public void visitVariableDefinition(@NotNull RobotVariableDefinition o) {
        super.visitVariableDefinition(o);
        String definedVariableName = o.getName();
        if (definedVariableName != null && variableName.equalsIgnoreCase(definedVariableName.trim())) {
            foundElements.add(o);
            overshadows |= inOvershadowingContext;
        }
    }

    @Override
    public void visitVariable(@NotNull RobotVariable o) {
        super.visitVariable(o);
        String variableName = o.getVariableName();
        if (variableName != null && this.variableName.equalsIgnoreCase(variableName.trim())) {
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
