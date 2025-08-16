package dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider.Result;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiModificationTracker;
import com.jetbrains.python.psi.Property;
import com.jetbrains.python.psi.PyBoolLiteralExpression;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyDecorator;
import com.jetbrains.python.psi.PyFile;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.PyTargetExpression;
import com.jetbrains.python.psi.types.TypeEvalContext;
import com.jetbrains.python.sdk.PythonSdkUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.MyLogger;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.config.RobotOptionsProvider;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.KeywordDto;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.VariableDto;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedKeyword;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.ReservedVariableScope;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotPyUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.PythonInspector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
class RobotKeywordFileResolver {

    private static final Key<CachedValue<Collection<DefinedVariable>>> VARIABLES_CACHE_KEY = new Key<>("ROBOT_PYTHON_FILE_VARIABLES_CACHE");
    private static final Key<CachedValue<Collection<DefinedVariable>>> FILE_VARIABLES_CACHE_KEY = new Key<>("ROBOT_PYTHON_FILE_VARIABLES_CACHE");
    private static final Key<CachedValue<Collection<DefinedVariable>>> CLASS_VARIABLES_CACHE_KEY = new Key<>("ROBOT_PYTHON_CLASS_VARIABLES_CACHE");
    private static final Key<CachedValue<Boolean>> SYSTEM_PSI_FILE_KEY = new Key<>("ROBOT_PYTHON_SYSTEM_FILE_CACHE");

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
        if (isSystemLibrary(pythonFile)) {
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
                definedVariables.add(new VariableDto(attribute, attributeName, ReservedVariableScope.Global));
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
                definedVariables.add(new VariableDto(attribute, attributeName, ReservedVariableScope.Global));
            }
        }

        for (Entry<String, Property> entry : pyClass.getProperties().entrySet()) {
            String propertyName = entry.getKey();
            PyFunction propertyFunction = pyClass.findMethodByName(propertyName, false, context);
            if (propertyName != null && isNotReservedName(propertyName) && propertyFunction != null) {
                definedVariables.add(new VariableDto(propertyFunction, propertyName, ReservedVariableScope.Global));
            }
        }

        for (PyClass superClass : pyClass.getSuperClasses(context)) {
            String superClassName = superClass.getName();
            if (superClassName != null && !superClassName.equals("object")) {
                addDefinedVariables(superClass, definedVariables, instanceVariables);
            }
        }
    }

    static String getValidName(@Nullable String name) {
        return name != null && isNotReservedName(name) ? name : null;
    }

    private static boolean isNotReservedName(@NotNull String name) {
        return !name.startsWith("_") && !name.startsWith("ROBOT_LIBRARY_");
    }

    @Nullable
    private static String getKeywordName(@NotNull PyFunction function) {
        String keywordName = null;
        Optional<String> normalizedKeywordNameOpt = RobotPyUtil.findCustomKeywordNameDecoratorExpression(function)
                                                               .map(expression -> expression.getText().replaceAll("^\"|\"|'|'$", ""));
        if (normalizedKeywordNameOpt.isPresent()) {
            keywordName = normalizedKeywordNameOpt.get();
        }
        return keywordName;
    }

    static void addDefinedKeywords(@NotNull PyClass pyClass, @Nullable String libraryName, @NotNull Collection<DefinedKeyword> keywords) {
        Map<String, PyFunction> methods = new LinkedHashMap<>();
        String className = pyClass.getName();
        boolean shouldImportOnlyDecoratedMethods = shouldImportOnlyDecoratedMethods(pyClass);
        boolean libraryDecorated = isLibraryDecorated(pyClass);
        pyClass.visitMethods(method -> {
            boolean propertyDecorated = Optional.ofNullable(method.getDecoratorList())
                                                .map(decoratorList -> decoratorList.findDecorator("property"))
                                                .isPresent();
            String methodName = method.getName();
            if (propertyDecorated && !libraryDecorated) {
                String attributeName = getValidName(methodName);
                if (attributeName != null) {
                    keywords.add(new KeywordDto(method, libraryName, attributeName));
                }
                return true;
            }
            methodName = getValidName(methodName);
            if (methodName != null && (!shouldImportOnlyDecoratedMethods || isMethodKeywordDecorated(method))) {
                methods.put(methodName, method);
            }
            return true;
        }, true, null);
        String namespace = pyClass.getQualifiedName();
        if (namespace == null && className != null) {
            namespace = className;
        } else {
            namespace = "";
        }
        addDefinedKeywords(pyClass, namespace, libraryName, keywords, className, methods);

        pyClass.visitClassAttributes(attribute -> {
            String attributeName = getValidName(attribute.getName());
            if (attributeName != null && !libraryDecorated) {
                keywords.add(new KeywordDto(attribute, libraryName, attributeName));
            }
            return true;
        }, true, null);
    }

    static void addDefinedKeywords(@NotNull PsiElement sourceElement,
                                   @Nullable String libraryName,
                                   @NotNull Collection<DefinedKeyword> keywords,
                                   Map<String, PyFunction> methods) {
        addDefinedKeywords(sourceElement, "", libraryName, keywords, null, methods);
    }

    private static void addDefinedKeywords(@NotNull PsiElement sourceElement,
                                           @NotNull String namespace,
                                           @Nullable String libraryName,
                                           @NotNull Collection<DefinedKeyword> keywords,
                                           @Nullable String className,
                                           Map<String, PyFunction> methods) {
        RobotOptionsProvider robotOptionsProvider = RobotOptionsProvider.getInstance(sourceElement.getProject());
        Map<String, PyFunction> methodsToLiveInspect = new LinkedHashMap<>();
        Map<String, PyFunction> methodsToStaticallyInspect = new LinkedHashMap<>();
        methods.forEach((methodName, pyFunction) -> {
            if (robotOptionsProvider.analyzeViaPythonLiveInspection(pyFunction)) {
                methodsToLiveInspect.put(methodName, pyFunction);
            } else {
                methodsToStaticallyInspect.put(methodName, pyFunction);
            }
        });
        if (robotOptionsProvider.pythonLiveInspection()) {
            String inspectionNamespace = namespace;
            if (className != null && namespace.endsWith("." + className)) {
                inspectionNamespace = namespace.substring(0, namespace.length() - className.length() - 1);
            }
            try {
                Map<String, PythonInspector.PythonInspectorParameter[]> analyzedFunctions = PythonInspector.inspectPythonFunctions(sourceElement,
                                                                                                                                   inspectionNamespace,
                                                                                                                                   className,
                                                                                                                                   methodsToLiveInspect);
                for (Entry<String, PythonInspector.PythonInspectorParameter[]> entry : analyzedFunctions.entrySet()) {
                    String methodName = entry.getKey();
                    PythonInspector.PythonInspectorParameter[] parameters = entry.getValue();

                    PyFunction method = methods.get(methodName);
                    String keywordName = getKeywordName(method);
                    if (keywordName != null) {
                        methodName = keywordName;
                    }
                    keywords.add(new KeywordDto(method,
                                                libraryName,
                                                methodName,
                                                PythonInspector.convertPyParameters(parameters, method.getParameterList().getParameters(), true)));
                }
            } catch (ProcessCanceledException e) {
                throw e;
            } catch (Exception e) {
                MyLogger.logger.warn("Error while inspecting Python functions. Falling back to static analysis.", e);
                methodsToStaticallyInspect.putAll(methodsToLiveInspect);
            }
        }
        for (Map.Entry<String, PyFunction> entry : methodsToStaticallyInspect.entrySet()) {
            String methodName = entry.getKey();
            PyFunction method = entry.getValue();
            String keywordName = getKeywordName(method);
            if (keywordName != null) {
                methodName = keywordName;
            }
            keywords.add(new KeywordDto(method, libraryName, methodName, Arrays.asList(method.getParameterList().getParameters())));
        }
    }

    private static boolean isSystemLibrary(PsiFile psiFile) {
        return CachedValuesManager.getCachedValue(psiFile, SYSTEM_PSI_FILE_KEY, () -> {
            Module module = ModuleUtilCore.findModuleForPsiElement(psiFile);
            if (module != null) {
                Sdk sdk = PythonSdkUtil.findPythonSdk(module);
                if (sdk != null) {
                    VirtualFile[] roots = sdk.getRootProvider().getFiles(OrderRootType.CLASSES);
                    VirtualFile fileVirtual = psiFile.getVirtualFile();
                    boolean result = Arrays.stream(roots).anyMatch(root -> VfsUtilCore.isAncestor(root, fileVirtual, false));
                    return Result.createSingleDependency(result, psiFile);
                }
            }
            return Result.createSingleDependency(false, PsiModificationTracker.MODIFICATION_COUNT);
        });
    }

    private static boolean isLibraryDecorated(PyClass pyClass) {
        return Optional.ofNullable(pyClass.getDecoratorList()).map(decoratorList -> decoratorList.findDecorator("library")).isPresent();
    }

    private static boolean shouldImportOnlyDecoratedMethods(PyClass pyClass) {
        Optional<@Nullable PyDecorator> libraryDecoratorOpt = Optional.ofNullable(pyClass.getDecoratorList())
                                                                      .map(decoratorList -> decoratorList.findDecorator("library"));
        if (libraryDecoratorOpt.isPresent()) {
            PyDecorator libraryDecorator = libraryDecoratorOpt.get();
            PyBoolLiteralExpression autoKeywords = libraryDecorator.getArgument(5, "auto_keywords", PyBoolLiteralExpression.class);
            if (autoKeywords != null && autoKeywords.getValue()) {
                return autoKeywords.getValue();
            } else {
                return true;
            }
        }
        return false;
    }

    private static boolean isMethodKeywordDecorated(@NotNull PyFunction method) {
        return Optional.ofNullable(method.getDecoratorList()).map(decoratorList -> decoratorList.findDecorator("keyword")).isPresent();
    }
}
