package dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor;

import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import com.jetbrains.python.psi.PyElementVisitor;
import com.jetbrains.python.psi.PyExpression;
import com.jetbrains.python.psi.PyNamedParameter;
import com.jetbrains.python.psi.PyRecursiveElementVisitor;
import com.jetbrains.python.psi.PyReferenceExpression;
import com.jetbrains.python.psi.PySingleStarParameter;
import com.jetbrains.python.psi.PySlashParameter;
import com.jetbrains.python.psi.PyStringLiteralExpression;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ParameterDto;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotPyUtil;
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
                String defaultValueText = computeDefaultValueFor(node);
                definedParameters.add(ParameterDto.builder(node, node.getRepr(false)).defaultValue(defaultValueText).build());
            } else {
                definedParameters.add(ParameterDto.builder(node, node.getRepr(false)).positionalContainer(positionalContainer).keywordContainer(keywordContainer).build());
            }
        }
    }

    private static String computeDefaultValueFor(@NotNull PyNamedParameter node) {
        Ref<String> defaultValueTextRef = new Ref<>(null);
        PyElementVisitor visitor = new PyElementVisitor() {
            @Override
            public void visitPyStringLiteralExpression(@NotNull PyStringLiteralExpression node) {
                String stringValue = node.getStringValue();
                defaultValueTextRef.set(stringValue);
            }

            @Override
            public void visitPyReferenceExpression(@NotNull PyReferenceExpression node) {
                PsiElement firstChild = node.getFirstChild();
                if (firstChild != null && firstChild.getReference() != null) {
                    PsiElement target = firstChild.getReference().resolve();
                    if (RobotPyUtil.isPythonEnumElement(target)) {
                        PsiElement lastChild = node.getLastChild();
                        if (lastChild != null) {
                            String enumValue = lastChild.getText();
                            defaultValueTextRef.set(enumValue);
                        }
                    }
                }
            }
        };
        PyExpression defaultValueExpression = node.getDefaultValue();
        if (defaultValueExpression != null) {
            defaultValueExpression.accept(visitor);
            if (defaultValueTextRef.isNull()) {
                return node.getDefaultValueText();
            }
        }
        return defaultValueTextRef.get();
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
