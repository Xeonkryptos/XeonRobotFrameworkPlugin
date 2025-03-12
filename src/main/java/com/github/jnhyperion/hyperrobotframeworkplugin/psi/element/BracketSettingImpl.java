package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.dto.ParameterDto;
import com.intellij.lang.ASTNode;
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
                if (defaultValue.isEmpty()) {
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
