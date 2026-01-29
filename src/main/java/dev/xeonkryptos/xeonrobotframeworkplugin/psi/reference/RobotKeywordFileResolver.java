package dev.xeonkryptos.xeonrobotframeworkplugin.psi.reference;

import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.util.Key;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider.Result;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiModificationTracker;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyDictLiteralExpression;
import com.jetbrains.python.psi.PyElementVisitor;
import com.jetbrains.python.psi.PyExpression;
import com.jetbrains.python.psi.PyFile;
import com.jetbrains.python.psi.PyImportElement;
import com.jetbrains.python.psi.PyListLiteralExpression;
import com.jetbrains.python.psi.PyParenthesizedExpression;
import com.jetbrains.python.psi.PyTargetExpression;
import com.jetbrains.python.psi.PyTupleExpression;
import com.jetbrains.python.psi.PyUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.VariableDto;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.VariableType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotPyUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.VariableScope;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("UnstableApiUsage")
class RobotKeywordFileResolver {

    private static final Key<CachedValue<Collection<DefinedVariable>>> MODULE_VARIABLES_CACHE_KEY = new Key<>("ROBOT_PYTHON_FILE_VARIABLES_CACHE");
    private static final Key<CachedValue<Collection<DefinedVariable>>> CLASS_VARIABLES_CACHE_KEY = new Key<>("ROBOT_PYTHON_CLASS_VARIABLES_CACHE");

    private RobotKeywordFileResolver() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    static Collection<DefinedVariable> findVariable(PyClass pythonClass, String variableName) {
        if (isNotLibraryDecorated(pythonClass)) {
            return resolveVariables(pythonClass).stream().filter(variable -> variable.matches(variableName)).collect(Collectors.toCollection(LinkedHashSet::new));
        }
        return List.of();
    }

    static Collection<DefinedVariable> resolveVariables(PyClass pythonClass) {
        if (isNotLibraryDecorated(pythonClass)) {
            return CachedValuesManager.getCachedValue(pythonClass, CLASS_VARIABLES_CACHE_KEY, () -> {
                ProgressManager.checkCanceled();
                Collection<DefinedVariable> foundVariables = Stream.concat(pythonClass.getClassAttributes().stream(), pythonClass.getInstanceAttributes().stream())
                                                                   .filter(expression -> expression.getName() != null && isNotReservedName(expression.getName()))
                                                                   .map(RobotKeywordFileResolver::createDefinedVariable)
                                                                   .collect(Collectors.toSet());
                return Result.createSingleDependency(foundVariables, PsiModificationTracker.MODIFICATION_COUNT);
            });
        }
        return List.of();
    }

    static Collection<DefinedVariable> findVariable(PyFile pythonFile, String variableName) {
        return resolveVariables(pythonFile).stream().filter(variable -> variable.matches(variableName)).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    static Collection<DefinedVariable> resolveVariables(PyFile pythonFile) {
        if (RobotPyUtil.isSystemLibrary(pythonFile)) {
            return List.of();
        }

        return CachedValuesManager.getCachedValue(pythonFile, MODULE_VARIABLES_CACHE_KEY, () -> {
            ProgressManager.checkCanceled();
            Set<DefinedVariable> definedVariables = new HashSet<>();
            addDefinedVariables(pythonFile, definedVariables);
            return Result.createSingleDependency(definedVariables, PsiModificationTracker.MODIFICATION_COUNT);
        });
    }

    private static void addDefinedVariables(@NotNull PyFile pyFile, @NotNull Collection<DefinedVariable> definedVariables) {
        Set<String> dunderAll = Optional.ofNullable(pyFile.getDunderAll()).map(HashSet::new).orElse(null);

        for (PyImportElement importTarget : pyFile.getImportTargets()) {
            String importName = importTarget.getAsName();
            if (importName != null && (dunderAll == null || dunderAll.contains(importName)) && isNotReservedName(importName)) {
                definedVariables.add(new VariableDto(importTarget, importName, VariableType.SCALAR, VariableScope.Global));
            }
        }
        for (PyTargetExpression attribute : pyFile.getTopLevelAttributes()) {
            String attributeName = attribute.getName();
            if (attributeName != null && (dunderAll == null || dunderAll.contains(attributeName)) && isNotReservedName(attributeName)) {
                definedVariables.add(createDefinedVariable(attribute));
            }
        }
        for (PyClass pyClass : pyFile.getTopLevelClasses()) {
            String className = pyClass.getName();
            if (className != null && (dunderAll == null || dunderAll.contains(className)) && isNotReservedName(className)) {
                definedVariables.add(new VariableDto(pyClass, className, VariableType.SCALAR, VariableScope.Global));
            }
        }
    }

    private static DefinedVariable createDefinedVariable(PyTargetExpression expression) {
        String expressionName = expression.getName();
        assert expressionName != null;
        VariableType variableType = VariableType.fromIndicator(expressionName);
        if (variableType == VariableType.SCALAR) {
            PyExpression assignedValue = expression.findAssignedValue();
            PythonAssignedValueIdentifier pythonAssignedValueIdentifier = new PythonAssignedValueIdentifier();
            if (assignedValue != null) {
                assignedValue.accept(pythonAssignedValueIdentifier);
            }
            variableType = pythonAssignedValueIdentifier.variableType;
        }
        return new VariableDto(expression, expressionName, variableType, VariableScope.Global);
    }

    private static boolean isNotReservedName(@NotNull String name) {
        return PyUtil.getInitialUnderscores(name) == 0 && !name.startsWith("ROBOT_LIBRARY_");
    }

    private static boolean isNotLibraryDecorated(PyClass pyClass) {
        return Optional.ofNullable(pyClass.getDecoratorList()).map(decoratorList -> decoratorList.findDecorator("library")).isEmpty();
    }

    private static final class PythonAssignedValueIdentifier extends PyElementVisitor {

        private VariableType variableType = VariableType.SCALAR;

        @Override
        public void visitPyParenthesizedExpression(@NotNull PyParenthesizedExpression node) {
            PyExpression containedExpression = node.getContainedExpression();
            if (containedExpression != null) {
                containedExpression.accept(this);
            }
        }

        @Override
        public void visitPyTupleExpression(@NotNull PyTupleExpression node) {
            variableType = VariableType.LIST;
        }

        @Override
        public void visitPyListLiteralExpression(@NotNull PyListLiteralExpression node) {
            variableType = VariableType.LIST;
        }

        @Override
        public void visitPyDictLiteralExpression(@NotNull PyDictLiteralExpression node) {
            variableType = VariableType.DICTIONARY;
        }
    }
}
