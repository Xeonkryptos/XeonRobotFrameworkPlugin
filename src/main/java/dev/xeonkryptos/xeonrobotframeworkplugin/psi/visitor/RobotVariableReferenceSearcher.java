package dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor;

import com.intellij.psi.PsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotExecutableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordsSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalSettingArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotRoot;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTaskStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTasksSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCasesSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariablesSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.ResolverUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class RobotVariableReferenceSearcher extends RobotVisitor {

    private final Set<PsiElement> parents;
    private final String variableName;

    private final Set<PsiElement> foundElements = new LinkedHashSet<>();

    public RobotVariableReferenceSearcher(RobotVariableId variableId) {
        this.variableName = variableId.getName();
        this.parents = collectParentsOf(variableId);
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
        o.acceptChildren(this);
    }

    @Override
    public void visitVariablesSection(@NotNull RobotVariablesSection o) {
        o.acceptChildren(this);
    }

    @Override
    public void visitKeywordsSection(@NotNull RobotKeywordsSection o) {
        if (parents.contains(o)) {
            o.acceptChildren(this);
        }
    }

    @Override
    public void visitTestCasesSection(@NotNull RobotTestCasesSection o) {
        if (parents.contains(o)) {
            o.acceptChildren(this);
        }
    }

    @Override
    public void visitTestCaseStatement(@NotNull RobotTestCaseStatement o) {
        o.acceptChildren(this);
    }

    @Override
    public void visitTasksSection(@NotNull RobotTasksSection o) {
        if (parents.contains(o)) {
            o.acceptChildren(this);
        }
    }

    @Override
    public void visitTaskStatement(@NotNull RobotTaskStatement o) {
        o.acceptChildren(this);
    }

    @Override
    public void visitUserKeywordStatement(@NotNull RobotUserKeywordStatement o) {
        if (parents.contains(o)) {
            o.acceptChildren(this);
        }
    }

    @Override
    public void visitLocalSetting(@NotNull RobotLocalSetting o) {
        for (RobotLocalSettingArgument localSettingArgument : o.getLocalSettingArgumentList()) {
            visitLocalSettingArgument(localSettingArgument);
        }
        for (RobotPositionalArgument positionalArgument : o.getPositionalArgumentList()) {
            positionalArgument.acceptChildren(this);
        }
    }

    @Override
    public void visitLocalSettingArgument(@NotNull RobotLocalSettingArgument o) {
        RobotVariable variable = o.getVariable();
        String definedVariableName = variable.getName();
        if (variableName.equalsIgnoreCase(definedVariableName)) {
            foundElements.add(o);
        }
    }

    @Override
    public void visitVariableStatement(@NotNull RobotVariableStatement o) {
        o.acceptChildren(this);
    }

    @Override
    public void visitExecutableStatement(@NotNull RobotExecutableStatement o) {
        if (parents.contains(o) || parents.contains(o.getParent())) {
            o.acceptChildren(this);
        }
    }

    @Override
    public void visitVariableDefinition(@NotNull RobotVariableDefinition o) {
        String definedVariableName = o.getName();
        if (variableName.equalsIgnoreCase(definedVariableName)) {
            foundElements.add(o);
        }
    }

    @Override
    public void visitKeywordCall(@NotNull RobotKeywordCall o) {
        List<DefinedVariable> definedVariables = ResolverUtils.walkKeyword(o);
        for (DefinedVariable definedVariable : definedVariables) {
            if (definedVariable.matches(variableName)) {
                foundElements.add(definedVariable.reference());
            }
        }
    }

    public Collection<PsiElement> getFoundElements() {
        return foundElements;
    }
}
