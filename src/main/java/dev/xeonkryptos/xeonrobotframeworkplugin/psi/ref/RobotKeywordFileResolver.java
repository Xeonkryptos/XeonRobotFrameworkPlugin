package dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref;

import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.util.Key;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider.Result;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiModificationTracker;
import com.jetbrains.python.psi.Property;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyFile;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.PyTargetExpression;
import com.jetbrains.python.psi.types.TypeEvalContext;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.VariableDto;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotPyUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.VariableScope;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
class RobotKeywordFileResolver {

    private static final Key<CachedValue<Collection<DefinedVariable>>> VARIABLES_CACHE_KEY = new Key<>("ROBOT_PYTHON_FILE_VARIABLES_CACHE");
    private static final Key<CachedValue<Collection<DefinedVariable>>> FILE_VARIABLES_CACHE_KEY = new Key<>("ROBOT_PYTHON_FILE_VARIABLES_CACHE");
    private static final Key<CachedValue<Collection<DefinedVariable>>> CLASS_VARIABLES_CACHE_KEY = new Key<>("ROBOT_PYTHON_CLASS_VARIABLES_CACHE");

    private RobotKeywordFileResolver() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    static Collection<DefinedVariable> resolveVariables(PyClass pythonClass) {
        if (!isLibraryDecorated(pythonClass)) {
            PyFile containingFile = (PyFile) pythonClass.getContainingFile();
            return resolveVariables(containingFile);
        }
        return List.of();
    }

    static Collection<DefinedVariable> resolveVariables(PyFile pythonFile) {
        if (RobotPyUtil.isSystemLibrary(pythonFile)) {
            return List.of();
        }

        return CachedValuesManager.getCachedValue(pythonFile, VARIABLES_CACHE_KEY, () -> {
            Set<PyFile> pyFiles = new HashSet<>();
            pyFiles.add(pythonFile);

            Set<DefinedVariable> definedVariables = new HashSet<>();
            for (PyFile pyFile : pyFiles) {
                ProgressManager.checkCanceled();
                Collection<DefinedVariable> foundVariables = resolveDefinedVariables(pyFile);
                definedVariables.addAll(foundVariables);
            }
            return Result.create(definedVariables, PsiModificationTracker.MODIFICATION_COUNT);
        });
    }

    static Collection<DefinedVariable> resolveDefinedVariables(PyClass pythonClass) {
        return CachedValuesManager.getCachedValue(pythonClass, CLASS_VARIABLES_CACHE_KEY, () -> {
            Set<DefinedVariable> newVariables = new HashSet<>();
            addDefinedVariables(pythonClass, newVariables, true);
            return new Result<>(newVariables, PsiModificationTracker.MODIFICATION_COUNT);
        });
    }

    static Collection<DefinedVariable> resolveDefinedVariables(PyFile pythonFile) {
        return CachedValuesManager.getCachedValue(pythonFile, FILE_VARIABLES_CACHE_KEY, () -> {
            Set<DefinedVariable> variables = new HashSet<>();
            addDefinedVariables(pythonFile, variables);
            return new Result<>(variables, PsiModificationTracker.MODIFICATION_COUNT);
        });
    }

    private static void addDefinedVariables(@NotNull PyFile pyFile, @NotNull Collection<DefinedVariable> definedVariables) {
        for (PyTargetExpression attribute : pyFile.getTopLevelAttributes()) {
            String attributeName = attribute.getName();
            if (attributeName != null) {
                definedVariables.add(new VariableDto(attribute, attributeName, VariableScope.Global));
            }
        }
        for (PyClass pyClass : pyFile.getTopLevelClasses()) {
            Collection<DefinedVariable> foundVariables = resolveDefinedVariables(pyClass);
            definedVariables.addAll(foundVariables);
        }
    }

    private static void addDefinedVariables(@NotNull PyClass pyClass, @NotNull Collection<DefinedVariable> definedVariables, boolean instanceVariables) {
        TypeEvalContext context = TypeEvalContext.deepCodeInsight(pyClass.getProject());
        List<PyTargetExpression> attributes = new ArrayList<>(pyClass.getClassAttributes());
        if (instanceVariables) {
            attributes.addAll(pyClass.getInstanceAttributes());
        }

        for (PyTargetExpression attribute : attributes) {
            String attributeName = attribute.getName();
            if (attributeName != null && isNotReservedName(attributeName)) {
                definedVariables.add(new VariableDto(attribute, attributeName, VariableScope.Global));
            }
        }

        for (Entry<String, Property> entry : pyClass.getProperties().entrySet()) {
            String propertyName = entry.getKey();
            PyFunction propertyFunction = pyClass.findMethodByName(propertyName, false, context);
            if (propertyName != null && isNotReservedName(propertyName) && propertyFunction != null) {
                definedVariables.add(new VariableDto(propertyFunction, propertyName, VariableScope.Global));
            }
        }

        for (PyClass superClass : pyClass.getSuperClasses(context)) {
            String superClassName = superClass.getName();
            if (superClassName != null && !superClassName.equals("object")) {
                addDefinedVariables(superClass, definedVariables, instanceVariables);
            }
        }
    }

    private static boolean isNotReservedName(@NotNull String name) {
        return !name.startsWith("_") && !name.startsWith("ROBOT_LIBRARY_");
    }

    private static boolean isLibraryDecorated(PyClass pyClass) {
        return Optional.ofNullable(pyClass.getDecoratorList()).map(decoratorList -> decoratorList.findDecorator("library")).isPresent();
    }
}
