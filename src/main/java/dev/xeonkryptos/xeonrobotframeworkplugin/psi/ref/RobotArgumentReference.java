package dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.icons.AllIcons.Nodes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtilRt;
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
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.completion.CompletionKeys;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.completion.RobotLookupContext;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.completion.RobotLookupElementType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLibraryImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotResourceImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariablesImportGlobalSetting;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class RobotArgumentReference extends PsiPolyVariantReferenceBase<RobotPositionalArgument> {

    public RobotArgumentReference(@NotNull RobotPositionalArgument positionalArgument) {
        super(positionalArgument);
    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        return ResolveCache.getInstance(getElement().getProject())
                           .resolveWithCaching(this, (resolver, incompCode) -> multiResolve(resolver.getElement()), false, incompleteCode);
    }

    private static ResolveResult @NotNull [] multiResolve(RobotPositionalArgument positionalArgument) {
        Project project = positionalArgument.getProject();
        PsiElement parent = positionalArgument.getParent();
        String argumentValue = positionalArgument.getText();

        Set<ResolveResult> results = new LinkedHashSet<>();
        if (parent instanceof RobotResourceImportGlobalSetting resourceImport) {
            PsiElement result = RobotFileManager.findElement(argumentValue, project, resourceImport);
            if (result != null) {
                results.add(new PsiElementResolveResult(result));
            }
            VirtualFile virtualFile = resourceImport.getContainingFile().getVirtualFile();
            PsiFile file = RobotFileManager.findPsiFiles(argumentValue, virtualFile, project);
            if (file != null) {
                results.add(new PsiElementResolveResult(file));
            }
        } else if (parent instanceof RobotLibraryImportGlobalSetting || parent instanceof RobotVariablesImportGlobalSetting) {
            PsiElement result = RobotFileManager.findElementInContext(argumentValue, project, parent);
            if (result != null) {
                results.add(new PsiElementResolveResult(result));
            }
            if (argumentValue.endsWith(".py")) {
                VirtualFile virtualFile = parent.getContainingFile().getVirtualFile();
                PsiFile file = RobotFileManager.findPsiFiles(argumentValue, virtualFile, project);
                if (file != null) {
                    results.add(new PsiElementResolveResult(file));
                }
            }
        }
        return results.toArray(ResolveResult.EMPTY_ARRAY);
    }

    @Override
    public Object @NotNull [] getVariants() {
        RobotPositionalArgument currentPositionalArgument = getElement();
        RobotParameter parameter = PsiTreeUtil.getParentOfType(currentPositionalArgument, RobotParameter.class);
        if (parameter != null) {
            PsiElement resolvedElement = parameter.getParameterId().getReference().resolve();
            Optional<PyExpression> pyExpressionOpt = Optional.ofNullable(resolvedElement)
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
            return ((PyClass) resolvedExpression).getClassAttributes().stream().map(RobotArgumentReference::createEnumLookupElement);
        }
        PyFunctionalEnumElementVisitor pyElementVisitor = new PyFunctionalEnumElementVisitor();
        resolvedExpression.accept(pyElementVisitor);
        return pyElementVisitor.extractedEnumValues;
    }

    private static LookupElementBuilder createEnumLookupElement(PyTargetExpression targetExpression) {
        return createEnumLookupElement(LookupElementBuilder.create(targetExpression));
    }

    private static LookupElementBuilder createEnumLookupElement(String enumExpression) {
        return createEnumLookupElement(LookupElementBuilder.create(enumExpression));
    }

    private static LookupElementBuilder createEnumLookupElement(LookupElementBuilder enumElementBuilder) {
        LookupElementBuilder lookupElementBuilder = enumElementBuilder.withCaseSensitivity(true).withIcon(Nodes.Enum).withBoldness(true);
        lookupElementBuilder.putUserData(CompletionKeys.ROBOT_LOOKUP_CONTEXT, RobotLookupContext.WITHIN_KEYWORD_STATEMENT);
        lookupElementBuilder.putUserData(CompletionKeys.ROBOT_LOOKUP_ELEMENT_TYPE, RobotLookupElementType.ARGUMENT);
        return lookupElementBuilder;
    }

    private static boolean isEnumConstructor(PsiElement element) {
        return element instanceof PyClass pyClass && pyClass.isSubclass("enum.Enum", null);
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
            if (isEnumConstructor(resolved)) {
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
                Arrays.stream(partedEnumValues).map(RobotArgumentReference::createEnumLookupElement).forEach(extractedEnumValues::add);
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
