package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ParameterDto;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.element.PositionalArgumentStubElement;
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
    private static final String DOCUMENTATION = "[Documentation]";

    private BracketSettingType bracketSettingType;
    private Collection<DefinedParameter> arguments;

    public BracketSettingImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public final boolean isArguments() {
        return getBracketSettingType() == BracketSettingType.ARGUMENTS;
    }

    @Override
    public final boolean isTeardown() {
        return getBracketSettingType() == BracketSettingType.TEARDOWN;
    }

    @Override
    public boolean isDocumentation() {
        return getBracketSettingType() == BracketSettingType.DOCUMENTATION;
    }

    @Override
    public Collection<DefinedParameter> getArguments() {
        Collection<DefinedParameter> localArguments = arguments;
        if (localArguments == null || localArguments.stream().map(DefinedParameter::reference).anyMatch(element -> !element.isValid())) {
            localArguments = PsiTreeUtil.getChildrenOfTypeAsList(this, VariableDefinition.class).stream().map(variableDefinition -> {
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
                return new ParameterDto(variableDefinition, variableDefinition.getUnwrappedName(), defaultValue);
            }).collect(Collectors.toCollection(LinkedHashSet::new));
            arguments = localArguments;
        }
        return localArguments;
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();

        arguments = null;
        bracketSettingType = null;
    }

    private BracketSettingType getBracketSettingType() {
        if (bracketSettingType == null) {
            String presentableText = getPresentableText();
            if (ARGUMENTS.equalsIgnoreCase(presentableText)) {
                bracketSettingType = BracketSettingType.ARGUMENTS;
            } else if (TEARDOWN.equalsIgnoreCase(presentableText)) {
                bracketSettingType = BracketSettingType.TEARDOWN;
            } else if (DOCUMENTATION.equalsIgnoreCase(presentableText)) {
                bracketSettingType = BracketSettingType.DOCUMENTATION;
            }
        }
        return bracketSettingType;
    }

    private enum BracketSettingType {
        ARGUMENTS, TEARDOWN, DOCUMENTATION
    }
}
