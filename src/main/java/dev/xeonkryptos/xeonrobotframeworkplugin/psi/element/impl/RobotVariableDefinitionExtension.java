package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.icons.RobotIcons;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotResourceFileType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariablesSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotStubPsiElementBase;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotVariableDefinitionStub;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.QualifiedNameBuilder;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.ReservedVariableScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;

public abstract class RobotVariableDefinitionExtension extends RobotStubPsiElementBase<RobotVariableDefinitionStub, RobotVariableDefinition>
        implements RobotVariableDefinition {

    public RobotVariableDefinitionExtension(@NotNull ASTNode node) {
        super(node);
    }

    public RobotVariableDefinitionExtension(RobotVariableDefinitionStub stub, IStubElementType<RobotVariableDefinitionStub, RobotVariableDefinition> nodeType) {
        super(stub, nodeType);
    }

    @Override
    public String getName() {
        RobotVariable nameIdentifier = getVariable();
        return nameIdentifier.getName();
    }

    @Override
    public RobotVariable getNameIdentifier() {
        return getVariable();
    }

    @Override
    public boolean isInScope(@NotNull PsiElement element) {
        RobotVariableStatement variableStatement = PsiTreeUtil.getParentOfType(this, RobotVariableStatement.class);
        if (variableStatement != null) {
            return ReservedVariableScope.TestCase.isInScope(this, element);
        }
        RobotVariablesSection variablesSection = PsiTreeUtil.getParentOfType(this, RobotVariablesSection.class);
        if (variablesSection == null || variablesSection.getContainingFile().getFileType() != RobotResourceFileType.getInstance()) {
            return getContainingFile() == element.getContainingFile();
        }
        return true;
    }

    @Override
    public boolean matches(@Nullable String text) {
        if (text == null) {
            return false;
        }
        String variableName = getName();
        return text.equalsIgnoreCase(variableName);
    }

    @NotNull
    @Override
    public Icon getIcon(int flags) {
        return RobotIcons.VARIABLE;
    }

    @Override
    public String getLookup() {
        return getText();
    }

    @Override
    public String[] getLookupWords() {
        return new String[] { getName(), getText() };
    }

    @Override
    public PsiElement reference() {
        return this;
    }

    @NotNull
    @Override
    public String getQualifiedName() {
        return QualifiedNameBuilder.computeQualifiedName(this);
    }
}
