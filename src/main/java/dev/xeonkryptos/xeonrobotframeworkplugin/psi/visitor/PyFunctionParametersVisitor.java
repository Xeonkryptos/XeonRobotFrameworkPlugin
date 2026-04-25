package dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor;

import com.jetbrains.python.psi.PyNamedParameter;
import com.jetbrains.python.psi.PyRecursiveElementVisitor;
import com.jetbrains.python.psi.PySingleStarParameter;
import com.jetbrains.python.psi.PySlashParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ParameterDto;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedParameter;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@SuppressWarnings("UnstableApiUsage")
public class PyFunctionParametersVisitor extends PyRecursiveElementVisitor {

    private int currentIndex = 0;
    private int positionalArgumentsOnlyEndIndex = -1;
    private int parametersOnlyStartIndex = -1;

    private boolean positionalArgumentsContainerFound = false;
    private boolean parametersOnlyContainerFound = false;

    private final Set<DefinedParameter> definedParameters = new LinkedHashSet<>();

    @Override
    public void visitPyNamedParameter(@NotNull PyNamedParameter node) {
        if (!node.isSelf()) {
            currentIndex++;

            boolean positionalContainer = node.isPositionalContainer();
            boolean keywordContainer = node.isKeywordContainer();

            positionalArgumentsContainerFound |= positionalContainer;
            parametersOnlyContainerFound |= keywordContainer;

            if (!positionalContainer && !keywordContainer) {
                String defaultValueText = node.getDefaultValueText();
                definedParameters.add(ParameterDto.builder(node, node.getRepr(false)).defaultValue(defaultValueText).build());
            } else {
                definedParameters.add(ParameterDto.builder(node, node.getRepr(false)).positionalContainer(positionalContainer).keywordContainer(keywordContainer).build());
            }
        }
    }

    @Override
    public void visitPySlashParameter(@NotNull PySlashParameter node) {
        definedParameters.forEach(DefinedParameter::markAsPositionalOnly);
        positionalArgumentsOnlyEndIndex = currentIndex;
    }

    @Override
    public void visitPySingleStarParameter(@NotNull PySingleStarParameter node) {
        parametersOnlyStartIndex = currentIndex;
    }
}
