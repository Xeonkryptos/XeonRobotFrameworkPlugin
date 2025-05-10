package dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.Import;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.Parameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.PositionalArgument;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.icons.AllIcons.Nodes;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.QualifiedName;
import com.intellij.util.ArrayUtilRt;
import com.jetbrains.python.psi.PyAnnotation;
import com.jetbrains.python.psi.PyCallExpression;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyDictLiteralExpression;
import com.jetbrains.python.psi.PyElementVisitor;
import com.jetbrains.python.psi.PyExpression;
import com.jetbrains.python.psi.PyFile;
import com.jetbrains.python.psi.PyKeyValueExpression;
import com.jetbrains.python.psi.PyListLiteralExpression;
import com.jetbrains.python.psi.PyParameter;
import com.jetbrains.python.psi.PyParenthesizedExpression;
import com.jetbrains.python.psi.PyReferenceExpression;
import com.jetbrains.python.psi.PyStringLiteralExpression;
import com.jetbrains.python.psi.PyTargetExpression;
import com.jetbrains.python.psi.PyTupleExpression;
import com.jetbrains.python.psi.stubs.PyModuleNameIndex;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class RobotArgumentReference extends PsiPolyVariantReferenceBase<PositionalArgument> {

    public RobotArgumentReference(@NotNull PositionalArgument positionalArgument) {
        super(positionalArgument);
    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        return ResolveCache.getInstance(getElement().getProject())
                           .resolveWithCaching(this, (resolver, incompCode) -> multiResolve(resolver.getElement()), false, incompleteCode);
    }

    private static ResolveResult @NotNull [] multiResolve(PositionalArgument positionalArgument) {
        Project project = positionalArgument.getProject();
        PsiElement parent = positionalArgument.getParent();
        String argumentValue = positionalArgument.getContent();

        Set<ResolveResult> results = new LinkedHashSet<>();
        if (parent instanceof Import importElement) {
            if (importElement.isResource()) {
                for (PsiFile file : RobotFileManager.findPsiFiles(argumentValue, project)) {
                    results.add(new PsiElementResolveResult(file));
                }
            } else if ((importElement.isLibrary() || importElement.isVariables())) {
                if (argumentValue.endsWith(".py")) {
                    for (PsiFile file : RobotFileManager.findPsiFiles(argumentValue, project)) {
                        results.add(new PsiElementResolveResult(file));
                    }
                } else {
                    for (PyFile pyFile : PyModuleNameIndex.findByQualifiedName(QualifiedName.fromDottedString(argumentValue),
                                                                               project,
                                                                               GlobalSearchScope.allScope(project))) {
                        results.add(new PsiElementResolveResult(pyFile));
                    }
                }
            }
        }
        return results.toArray(ResolveResult.EMPTY_ARRAY);
    }

    @Override
    public Object @NotNull [] getVariants() {
        PositionalArgument currentPositionalArgument = getElement();
        PsiElement parent = currentPositionalArgument.getParent();
        if (parent instanceof Parameter parameter) {
            Optional<PyExpression> pyExpressionOpt = Optional.ofNullable(parameter.getNameIdentifier())
                                                             .map(PsiElement::getReference)
                                                             .map(PsiReference::resolve)
                                                             .filter(element -> element instanceof PyParameter)
                                                             .map(element -> PsiTreeUtil.findChildOfType(element, PyAnnotation.class))
                                                             .map(PyAnnotation::getValue);
            if (pyExpressionOpt.isPresent()) {
                PyExpression pyExpression = pyExpressionOpt.get();
                PyReferenceExpressionVisitor pyReferenceExpressionVisitor = new PyReferenceExpressionVisitor();
                pyExpression.accept(pyReferenceExpressionVisitor);
                pyExpression.acceptChildren(pyReferenceExpressionVisitor);
                return pyReferenceExpressionVisitor.resolvedExpressions.stream().flatMap(this::handleResolvedEnumExpression).toArray();
            }
        }
        return ArrayUtilRt.EMPTY_OBJECT_ARRAY;
    }

    private Stream<?> handleResolvedEnumExpression(PsiElement resolvedExpression) {
        if (isEnumConstructor(resolvedExpression)) {
            return ((PyClass) resolvedExpression).getClassAttributes().stream();
        }
        PyFunctionalEnumElementVisitor pyElementVisitor = new PyFunctionalEnumElementVisitor();
        resolvedExpression.accept(pyElementVisitor);
        return pyElementVisitor.extractedEnumValues;
    }

    private static boolean isEnumConstructor(PsiElement element) {
        return element instanceof PyClass pyClass && pyClass.isSubclass("enum.Enum", null);
    }

    private static class PyFunctionalEnumElementVisitor extends PyElementVisitor {

        private PyCallExpression pyCallExpression;
        private Stream<?> extractedEnumValues = Stream.empty();

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
            if (isEnumConstructor(resolved)) {
                extractedEnumValues = extractEnumValues(pyCallExpression);
            }
        }

        private Stream<?> extractEnumValues(PyCallExpression pyCallExpression) {
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
                Arrays.stream(partedEnumValues)
                      .map(value -> LookupElementBuilder.create(value).withCaseSensitivity(true).withIcon(Nodes.Enum))
                      .forEach(extractedEnumValues::add);
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
