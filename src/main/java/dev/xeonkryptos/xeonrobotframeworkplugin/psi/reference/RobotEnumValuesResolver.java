package dev.xeonkryptos.xeonrobotframeworkplugin.psi.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.icons.AllIcons.Nodes;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.python.psi.PyAnnotation;
import com.jetbrains.python.psi.PyCallExpression;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyDictLiteralExpression;
import com.jetbrains.python.psi.PyElementVisitor;
import com.jetbrains.python.psi.PyExpression;
import com.jetbrains.python.psi.PyKeyValueExpression;
import com.jetbrains.python.psi.PyListLiteralExpression;
import com.jetbrains.python.psi.PyParameter;
import com.jetbrains.python.psi.PyParenthesizedExpression;
import com.jetbrains.python.psi.PyReferenceExpression;
import com.jetbrains.python.psi.PyStringLiteralExpression;
import com.jetbrains.python.psi.PyTargetExpression;
import com.jetbrains.python.psi.PyTupleExpression;
import dev.xeonkryptos.xeonrobotframeworkplugin.completion.CompletionKeys;
import dev.xeonkryptos.xeonrobotframeworkplugin.completion.RobotLookupContext;
import dev.xeonkryptos.xeonrobotframeworkplugin.completion.RobotLookupElementType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotPyUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class RobotEnumValuesResolver {

    public static LookupElement[] findPossibleEnumValuesFor(@NotNull RobotPositionalArgument positionalArgument) {
        RobotParameter parameter = PsiTreeUtil.getParentOfType(positionalArgument, RobotParameter.class);
        if (parameter != null) {
            PsiElement resolvedElement = parameter.getParameterId().getReference().resolve();
            Optional<PyExpression> pyExpressionOpt = Optional.ofNullable(resolvedElement)
                                                             .filter(PyParameter.class::isInstance)
                                                             .map(element -> PsiTreeUtil.findChildOfType(element, PyAnnotation.class))
                                                             .map(PyAnnotation::getValue);
            if (pyExpressionOpt.isPresent()) {
                PyExpression pyExpression = pyExpressionOpt.get();
                PyReferenceExpressionVisitor pyReferenceExpressionVisitor = new PyReferenceExpressionVisitor();
                pyExpression.accept(pyReferenceExpressionVisitor);
                pyExpression.acceptChildren(pyReferenceExpressionVisitor);
                return pyReferenceExpressionVisitor.resolvedExpressions.stream()
                                                                       .filter(PsiElement::isValid)
                                                                       .flatMap(RobotEnumValuesResolver::handleResolvedEnumExpression)
                                                                       .toArray(LookupElement[]::new);
            }
        }
        return LookupElement.EMPTY_ARRAY;
    }

    private static Stream<LookupElement> handleResolvedEnumExpression(PsiElement resolvedExpression) {
        if (RobotPyUtil.isPythonEnumElement(resolvedExpression)) {
            return ((PyClass) resolvedExpression).getClassAttributes().stream().map(targetExpression -> createEnumLookupElement(targetExpression, (PyClass) resolvedExpression));
        }
        PyFunctionalEnumElementVisitor pyElementVisitor = new PyFunctionalEnumElementVisitor();
        resolvedExpression.accept(pyElementVisitor);
        return pyElementVisitor.extractedEnumValues;
    }

    @SuppressWarnings("UnstableApiUsage")
    private static LookupElementBuilder createEnumLookupElement(PyTargetExpression targetExpression, PyClass pyClass) {
        LookupElementBuilder lookupElementBuilder = createEnumLookupElement(LookupElementBuilder.createWithSmartPointer(targetExpression.getText(), targetExpression)).withPsiElement(targetExpression);
        ItemPresentation presentation = pyClass.getPresentation();
        if (presentation != null) {
            lookupElementBuilder = lookupElementBuilder.withTypeText(pyClass.getName(), presentation.getIcon(false), true);
        }
        return lookupElementBuilder;
    }

    private static LookupElementBuilder createEnumLookupElement(String enumExpression, PsiElement pointerElement) {
        LookupElementBuilder lookupElementBuilder = createEnumLookupElement(LookupElementBuilder.createWithSmartPointer(enumExpression, pointerElement)).withPsiElement(pointerElement);
        PsiFile containingFile = pointerElement.getContainingFile();
        ItemPresentation presentation = containingFile.getPresentation();
        if (presentation != null) {
            String fileName = containingFile.getName();
            lookupElementBuilder = lookupElementBuilder.withTypeText(fileName, presentation.getIcon(false), true);
        }
        return lookupElementBuilder;
    }

    private static LookupElementBuilder createEnumLookupElement(LookupElementBuilder enumElementBuilder) {
        LookupElementBuilder lookupElementBuilder = enumElementBuilder.withCaseSensitivity(true).withIcon(Nodes.Enum).withBoldness(true);
        lookupElementBuilder.putUserData(CompletionKeys.ROBOT_LOOKUP_CONTEXT, RobotLookupContext.WITHIN_KEYWORD_STATEMENT);
        lookupElementBuilder.putUserData(CompletionKeys.ROBOT_LOOKUP_ELEMENT_TYPE, RobotLookupElementType.ARGUMENT);
        return lookupElementBuilder;
    }

    private static class PyFunctionalEnumElementVisitor extends PyElementVisitor {

        private PyCallExpression pyCallExpression;
        private Stream<LookupElement> extractedEnumValues = Stream.empty();

        @Override
        public void visitPyTargetExpression(@NotNull PyTargetExpression node) {
            PyExpression assignedValue = node.findAssignedValue();
            if (assignedValue != null) {
                assignedValue.accept(this);
            }
        }

        @Override
        public void visitPyCallExpression(@NotNull PyCallExpression node) {
            pyCallExpression = node;
            PyExpression callee = node.getCallee();
            if (callee != null) {
                callee.accept(this);
            }
        }

        @Override
        public void visitPyReferenceExpression(@NotNull PyReferenceExpression node) {
            PsiElement resolved = node.getReference().resolve();
            if (RobotPyUtil.isPythonEnumElement(resolved)) {
                extractedEnumValues = extractEnumValues(pyCallExpression);
            }
        }

        private Stream<LookupElement> extractEnumValues(PyCallExpression pyCallExpression) {
            PyExpression[] arguments = pyCallExpression.getArguments();
            if (arguments.length >= 2) {
                PyExpression enumValuesArg = arguments[1];
                PyFunctionalEnumValueVisitor visitor = new PyFunctionalEnumValueVisitor();
                enumValuesArg.accept(visitor);
                return visitor.extractedEnumValues.stream();
            }
            return Stream.empty();
        }
    }

    private static class PyFunctionalEnumValueVisitor extends PyElementVisitor {

        private final List<LookupElement> extractedEnumValues = new ArrayList<>();

        @Override
        public void visitPyListLiteralExpression(@NotNull PyListLiteralExpression node) {
            node.acceptChildren(this);
        }

        @Override
        public void visitPyDictLiteralExpression(@NotNull PyDictLiteralExpression node) {
            node.acceptChildren(this);
        }

        @Override
        public void visitPyKeyValueExpression(@NotNull PyKeyValueExpression node) {
            node.getKey().accept(this);
        }

        @Override
        public void visitPyTupleExpression(@NotNull PyTupleExpression node) {
            PyStringLiteralExpression enumValueTupleChild = PsiTreeUtil.findChildOfType(node, PyStringLiteralExpression.class);
            if (enumValueTupleChild != null) {
                enumValueTupleChild.accept(this);
            }
        }

        @Override
        public void visitPyParenthesizedExpression(@NotNull PyParenthesizedExpression node) {
            PyExpression containedExpression = node.getContainedExpression();
            if (containedExpression != null) {
                containedExpression.accept(this);
            }
        }

        @Override
        @SuppressWarnings("UnstableApiUsage")
        public void visitPyStringLiteralExpression(@NotNull PyStringLiteralExpression node) {
            String enumValue = node.getStringValue();
            if (!enumValue.isBlank()) {
                String[] partedEnumValues = enumValue.split("[,\\s]+");
                Arrays.stream(partedEnumValues).map(enumEntry -> createEnumLookupElement(enumEntry, node)).forEach(extractedEnumValues::add);
            }
        }
    }

    private static class PyReferenceExpressionVisitor extends PyElementVisitor {

        private final Set<PsiElement> resolvedExpressions = new LinkedHashSet<>();

        @Override
        public void visitPyReferenceExpression(@NotNull PyReferenceExpression node) {
            PsiElement resolvedExpression = node.getReference().resolve();
            if (resolvedExpression != null) {
                resolvedExpressions.add(resolvedExpression);
            }
        }

        @Override
        public void visitPyExpression(@NotNull PyExpression node) {
            node.acceptChildren(this);
        }
    }
}
