package dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
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
import com.intellij.psi.util.ParameterizedCachedValue;
import com.intellij.psi.util.QualifiedName;
import com.jetbrains.python.psi.Property;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyDecorator;
import com.jetbrains.python.psi.PyDecoratorList;
import com.jetbrains.python.psi.PyExpression;
import com.jetbrains.python.psi.PyFile;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.PyImportElement;
import com.jetbrains.python.psi.PyImportStatementBase;
import com.jetbrains.python.psi.PyTargetExpression;
import com.jetbrains.python.psi.resolve.RatedResolveResult;
import com.jetbrains.python.psi.types.TypeEvalContext;
import com.jetbrains.python.sdk.PythonSdkUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.MyLogger;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.config.RobotOptionsProvider;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.KeywordDto;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.VariableDto;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedKeyword;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.ReservedVariableScope;
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
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@SuppressWarnings("UnstableApiUsage")
class RobotKeywordFileResolver {

    private static final Key<ParameterizedCachedValue<Collection<DefinedVariable>, Boolean>> TRANSITIVE_VARIABLES_CACHE_KEY = new Key<>(
            "ROBOT_PYTHON_TRANSITIVE_VARIABLES_CACHE");
    private static final Key<CachedValue<Collection<DefinedVariable>>> FILE_VARIABLES_CACHE_KEY = new Key<>("ROBOT_PYTHON_FILE_VARIABLES_CACHE");
    private static final Key<CachedValue<Collection<DefinedVariable>>> CLASS_VARIABLES_CACHE_KEY = new Key<>("ROBOT_PYTHON_CLASS_VARIABLES_CACHE");
    private static final Key<CachedValue<Boolean>> SYSTEM_PSI_FILE_KEY = new Key<>("ROBOT_PYTHON_SYSTEM_FILE_CACHE");

    private RobotKeywordFileResolver() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    static Collection<DefinedVariable> resolveVariables(PyClass pythonClass) {
        PyFile containingFile = (PyFile) pythonClass.getContainingFile();
        return resolveVariables(containingFile);
    }

    static Collection<DefinedVariable> resolveVariables(PyFile pythonFile) {
        if (isSystemLibrary(pythonFile)) {
            return List.of();
        }

        Project project = pythonFile.getProject();
        boolean transitiveImports = RobotOptionsProvider.getInstance(project).allowTransitiveImports();
        CachedValuesManager cachedValuesManager = CachedValuesManager.getManager(project);
        return cachedValuesManager.getParameterizedCachedValue(pythonFile, TRANSITIVE_VARIABLES_CACHE_KEY, transitive -> {
            Set<PyFile> pyFiles = new HashSet<>();
            Set<PyClass> pyClasses = new HashSet<>();
            if (transitive) {
                collectScannableElementsFromImports(pythonFile, pyFiles, pyClasses);
            }
            pyFiles.add(pythonFile);

            Set<DefinedVariable> definedVariables = new HashSet<>();
            for (PyFile pyFile : pyFiles) {
                ProgressManager.checkCanceled();
                Collection<DefinedVariable> foundVariables = resolveDefinedVariables(pyFile);
                definedVariables.addAll(foundVariables);
            }
            for (PyClass pyClass : pyClasses) {
                ProgressManager.checkCanceled();
                Collection<DefinedVariable> foundVariables = resolveDefinedVariables(pyClass);
                definedVariables.addAll(foundVariables);
            }

            Object[] dependents = Stream.concat(Stream.of(pythonFile),
                                                definedVariables.stream()
                                                                .map(DefinedVariable::reference)
                                                                .map(PsiElement::getContainingFile)
                                                                .filter(Objects::nonNull)).distinct().toArray();
            return Result.create(definedVariables, dependents);
        }, false, transitiveImports);
    }

    static Collection<DefinedVariable> resolveDefinedVariables(PyClass pythonClass) {
        return CachedValuesManager.getCachedValue(pythonClass, CLASS_VARIABLES_CACHE_KEY, () -> {
            Set<DefinedVariable> newVariables = new HashSet<>();
            addDefinedVariables(pythonClass, newVariables, true);
            Object[] dependents = Stream.concat(newVariables.stream().map(DefinedVariable::reference), Stream.of(pythonClass))
                                        .map(PsiElement::getContainingFile)
                                        .filter(Objects::nonNull)
                                        .distinct()
                                        .toArray();
            return new Result<>(newVariables, dependents);
        });
    }

    static Collection<DefinedVariable> resolveDefinedVariables(PyFile pythonFile) {
        return CachedValuesManager.getCachedValue(pythonFile, FILE_VARIABLES_CACHE_KEY, () -> {
            Set<DefinedVariable> variables = new HashSet<>();
            addDefinedVariables(pythonFile, variables);
            Object[] dependents = Stream.concat(variables.stream().map(DefinedVariable::reference), Stream.of(pythonFile))
                                        .map(PsiElement::getContainingFile)
                                        .filter(Objects::nonNull)
                                        .distinct()
                                        .toArray();
            return new Result<>(variables, dependents);
        });
    }

    protected static void addDefinedVariables(@NotNull PyFile pyFile, @NotNull Collection<DefinedVariable> definedVariables) {
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

    static void collectScannableElementsFromImports(PyFile pyFile, Collection<PyFile> pyFiles, Collection<PyClass> pyClasses) {
        collectScannableElementsFromImports(pyFile, pyFiles, pyClasses, new HashSet<>());
    }

    static void collectScannableElementsFromImports(PyFile pyFile,
                                                    Collection<PyFile> pyFiles,
                                                    Collection<PyClass> pyClasses,
                                                    Collection<QualifiedName> visitedImports) {
        Set<PyFile> scanImportsFiles = new HashSet<>();
        for (PyImportStatementBase importStatement : pyFile.getImportBlock()) {
            for (PyImportElement importElement : importStatement.getImportElements()) {
                QualifiedName importedQName = importElement.getImportedQName();
                if (importedQName == null || !visitedImports.add(importedQName)) {
                    continue;
                }

                ProgressManager.checkCanceled();
                List<RatedResolveResult> ratedResolveResults = importElement.multiResolve();
                for (RatedResolveResult ratedResolveResult : ratedResolveResults) {
                    PsiElement element = ratedResolveResult.getElement();
                    if (element instanceof PyFile resolvedPyFile && pyFile != resolvedPyFile && pyFiles.add(resolvedPyFile)) {
                        scanImportsFiles.add(resolvedPyFile);
                    } else if (element instanceof PyClass resolvedPyClass) {
                        PyFile containingFile = (PyFile) resolvedPyClass.getContainingFile();
                        if (pyClasses.add(resolvedPyClass) && !pyFiles.contains(containingFile)) {
                            scanImportsFiles.add(containingFile);
                        }
                    }
                }
            }
        }
        for (PyFile scanImportsFile : scanImportsFiles) {
            ProgressManager.checkCanceled();
            collectScannableElementsFromImports(scanImportsFile, pyFiles, pyClasses, visitedImports);
        }
    }

    static String getValidName(@Nullable String name) {
        return name != null && isNotReservedName(name) ? name : null;
    }

    static boolean isNotReservedName(@NotNull String name) {
        return !name.startsWith("_") && !name.startsWith("ROBOT_LIBRARY_");
    }

    @Nullable
    private static String getKeywordName(@NotNull PyFunction function) {
        String keywordName = null;
        PyDecoratorList decoratorList = function.getDecoratorList();
        if (decoratorList != null) {
            PyDecorator keywordDecorator = decoratorList.findDecorator("keyword");
            if (keywordDecorator != null && keywordDecorator.hasArgumentList()) {
                PyExpression nameArgument = keywordDecorator.getKeywordArgument("name");
                if (nameArgument != null) {
                    keywordName = nameArgument.getText().replaceAll("^\"|\"|'|'$", "");
                } else {
                    PyExpression[] arguments = keywordDecorator.getArguments();
                    if (arguments.length > 0 && arguments[0].getName() == null) {
                        keywordName = arguments[0].getText().replaceAll("^\"|\"|'|'$", "");
                    }
                }
            }
        }
        return keywordName;
    }

    static void addDefinedKeywords(@NotNull PyClass pyClass, @NotNull String namespace, @NotNull Collection<DefinedKeyword> keywords) {
        Map<String, PyFunction> methods = new LinkedHashMap<>();
        String className = pyClass.getName();
        pyClass.visitMethods(method -> {
            boolean propertyDecorated = Optional.ofNullable(method.getDecoratorList())
                                                .map(decoratorList -> decoratorList.findDecorator("property"))
                                                .isPresent();
            String methodName = method.getName();
            if (propertyDecorated) {
                String attributeName = getValidName(methodName);
                if (attributeName != null) {
                    keywords.add(new KeywordDto(method, namespace, attributeName));
                }
                return true;
            }
            methodName = getValidName(methodName);
            if (methodName != null) {
                methods.put(methodName, method);
            }
            return true;
        }, true, null);
        addDefinedKeywords(pyClass, namespace, keywords, className, methods);

        pyClass.visitClassAttributes(attribute -> {
            String attributeName = getValidName(attribute.getName());
            if (attributeName != null) {
                keywords.add(new KeywordDto(attribute, namespace, attributeName));
            }
            return true;
        }, true, null);
    }

    static void addDefinedKeywords(@NotNull PsiElement sourceElement,
                                   @NotNull String namespace,
                                   @NotNull Collection<DefinedKeyword> keywords,
                                   Map<String, PyFunction> methods) {
        addDefinedKeywords(sourceElement, namespace, keywords, null, methods);
    }

    private static void addDefinedKeywords(@NotNull PsiElement sourceElement,
                                           @NotNull String namespace,
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
                                                namespace,
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
            keywords.add(new KeywordDto(method, namespace, methodName, Arrays.asList(method.getParameterList().getParameters())));
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
            return Result.createSingleDependency(false, psiFile);
        });
    }
}
