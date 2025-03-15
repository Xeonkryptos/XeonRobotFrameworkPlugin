package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.dto.ParameterDto;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.stub.element.PositionalArgumentStubElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

public class BracketSettingImpl extends RobotPsiElementBase implements BracketSetting {

    private static final String ARGUMENTS = "[Arguments]";
    private static final String TEARDOWN = "[Teardown]";

    private Collection<DefinedParameter> arguments;

    public BracketSettingImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public final boolean isArguments() {
        return ARGUMENTS.equalsIgnoreCase(this.getPresentableText());
    }

    @Override
    public Collection<DefinedParameter> getArguments() {
        Collection<DefinedParameter> localArguments = arguments;
        if (localArguments == null) {
            localArguments = PsiTreeUtil.getChildrenOfTypeAsList(this, VariableDefinition.class).stream().map(variableDefinition -> {
                assert variableDefinition.getName() != null;
                String defaultValue = PsiTreeUtil.getChildrenOfTypeAsList(variableDefinition, Argument.class)
                                                 .stream()
                                                 .map(RobotStatement::getPresentableText)
                                                 .collect(Collectors.joining("  "));
                if (defaultValue.isEmpty() && variableDefinition.getChildren().length > 0) {
                    // Special handling necessary to find the correct default value (argument) due to stubbing and what is cached ant what not.
                    PsiElement element = variableDefinition.getChildren()[0].getNextSibling();
                    if (element instanceof PsiWhiteSpace) {
                        do {
                            element = element.getNextSibling();
                        } while (element instanceof PsiWhiteSpace);
                    }
                    defaultValue = element instanceof LeafPsiElement leaf && leaf.getElementType() instanceof PositionalArgumentStubElement ?
                                   element.getText() :
                                   null;
                }
                if (defaultValue != null && defaultValue.isEmpty()) {
                    defaultValue = null;
                }
                return new ParameterDto(variableDefinition, variableDefinition.getName(), defaultValue);
            }).collect(Collectors.toCollection(LinkedHashSet::new));
            arguments = localArguments;
        }
        return localArguments;
    }

    @Override
    public final boolean isTeardown() {
        return TEARDOWN.equalsIgnoreCase(this.getPresentableText());
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();

        arguments = null;
    }
}
