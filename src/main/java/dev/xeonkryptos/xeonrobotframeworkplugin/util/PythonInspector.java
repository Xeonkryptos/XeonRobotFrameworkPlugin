package dev.xeonkryptos.xeonrobotframeworkplugin.util;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiModificationTracker;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.PyParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.MyLogger;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.config.RobotOptionsProvider;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ParameterDto;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedParameter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings("UnstableApiUsage")
public class PythonInspector {

    private static final Path DATA_DIR = PathManager.getPluginsDir().resolve("Xeon RobotFramework Support").resolve("data");
    private static final Path BUNDLED_DIR = DATA_DIR.resolve("bundled");
    private static final Path TOOL_DIR = BUNDLED_DIR.resolve("tool");
    private static final Path ROBOTCODE_DIR = TOOL_DIR.resolve("robotcode");
    private static final Path PYTHON_ARGUMENT_INSPECTOR_PY = ROBOTCODE_DIR.resolve("python_argument_inspector.py");

    private static final Pattern INSPECTOR_PARAMETER_PATTERN = Pattern.compile(
            "index:(?<index>\\d+);name:(?<name>.+?);default:(?<defaultValue>.+?)?;kind:(?<type>\\w+)");
    private static final Pattern INSPECTOR_PARAMETER_WITH_FUNCTION_ASSIGNMENT_PATTERN = Pattern.compile(
            "(?<functionName>\\w+)=(" + INSPECTOR_PARAMETER_PATTERN.pattern() + ")+");

    private static final AtomicInteger PYTHON_INSPECTOR_THREAD_COUNTER = new AtomicInteger();
    private static final Executor PYTHON_INSPECTOR_EXECUTOR = Executors.newCachedThreadPool(r -> {
        Thread thread = new Thread(r, "PythonInspector-" + PYTHON_INSPECTOR_THREAD_COUNTER.getAndIncrement());
        thread.setDaemon(true);
        return thread;
    });

    private PythonInspector() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static PythonInspectorParameter[] inspectPythonFunction(PyFunction function) {
        if (function == null) {
            return PythonInspectorParameter.EMPTY;
        }

        String namespace;
        String className = null;
        String functionName = function.getName();
        PsiElement sourceElement = function.getContainingFile();
        PyClass containingClass = function.getContainingClass();
        if (containingClass != null) {
            sourceElement = containingClass;
            className = containingClass.getName();
            namespace = containingClass.getQualifiedName();
            namespace = namespace.substring(0, namespace.length() - className.length() - 1);
        } else {
            namespace = function.getQualifiedName();
            namespace = namespace.substring(0, namespace.length() - functionName.length() - 1);
        }

        Map<String, PyFunction> elements = Map.of(functionName, function);
        Map<String, PythonInspectorParameter[]> result = inspectPythonFunctions(sourceElement, namespace, className, elements);
        return result.getOrDefault(functionName, PythonInspectorParameter.EMPTY);
    }

    public static Map<String, PythonInspectorParameter[]> inspectPythonFunctions(PsiElement sourceElement,
                                                                                 String namespace,
                                                                                 String className,
                                                                                 Map<String, PyFunction> elements) {
        if (elements == null || elements.isEmpty()) {
            return Map.of();
        }

        Sdk sdk = RobotLocalProcessExecutor.findPythonSdk(sourceElement);
        if (sdk == null) {
            return Map.of();
        }

        final Sdk finalSdk = sdk;
        return CachedValuesManager.getCachedValue(sourceElement, () -> {
            try {
                List<String> processArguments = createInspectionProcessArguments(finalSdk, sourceElement, namespace, className, elements);

                ProcessBuilder processBuilder = new ProcessBuilder(processArguments);

                Module module = ModuleUtilCore.findModuleForPsiElement(sourceElement);
                RobotLocalProcessExecutor.setupPythonPathForModule(processBuilder, module);

                Process process = processBuilder.start();
                try {
                    return CompletableFuture.supplyAsync(() -> {
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                            Map<String, PythonInspectorParameter[]> functions = extractFunctionParameters(elements, reader);
                            if (!process.isAlive() && process.exitValue() != 0) {
                                byte[] errorStreamBytes = process.getErrorStream().readAllBytes();
                                String errorStream = new String(errorStreamBytes);
                                MyLogger.logger.error(errorStream);
                                throw new RuntimeException("Python process exited with code " + process.exitValue());
                            }
                            return new CachedValueProvider.Result<>(functions, PsiModificationTracker.MODIFICATION_COUNT);
                        } catch (Exception e) {
                            throw new RuntimeException("Error while reading Python process output", e);
                        }
                    }, PYTHON_INSPECTOR_EXECUTOR).get(10L, TimeUnit.SECONDS);
                } catch (TimeoutException e) {
                    process.destroyForcibly();
                    throw e;
                }
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException("Error while inspecting Python function", e);
            }
        });
    }

    @NotNull
    private static List<String> createInspectionProcessArguments(Sdk finalSdk,
                                                                 PsiElement sourceElement,
                                                                 String namespace,
                                                                 @Nullable String className,
                                                                 Map<String, PyFunction> elements) {
        RobotOptionsProvider robotOptionsProvider = RobotOptionsProvider.getInstance(sourceElement.getProject());
        String additionalArguments = robotOptionsProvider.getPythonLiveInspectionAdditionalArguments();

        List<String> processArguments = new ArrayList<>(9);
        processArguments.add(finalSdk.getHomePath());
        processArguments.add(PYTHON_ARGUMENT_INSPECTOR_PY.toString());
        processArguments.add(additionalArguments);
        processArguments.add("--namespace");
        processArguments.add(namespace);
        if (className != null && !className.isBlank()) {
            processArguments.add("--classname");
            processArguments.add(className);
        }
        for (String function : elements.keySet()) {
            processArguments.add("--functions");
            processArguments.add(function);
        }
        return processArguments;
    }

    @NotNull
    private static Map<String, PythonInspectorParameter[]> extractFunctionParameters(Map<String, PyFunction> elements, BufferedReader reader) throws
                                                                                                                                              IOException {
        String input;
        Map<String, PythonInspectorParameter[]> functions = new LinkedHashMap<>(elements.size());
        Matcher functionMatcher = null;
        Matcher parameterInspectionMatcher = null;
        while ((input = reader.readLine()) != null) {
            if (functionMatcher == null) {
                functionMatcher = INSPECTOR_PARAMETER_WITH_FUNCTION_ASSIGNMENT_PATTERN.matcher(input);
            } else {
                functionMatcher.reset(input);
            }
            if (functionMatcher.matches()) {
                String functionName = functionMatcher.group("functionName");

                if (parameterInspectionMatcher == null) {
                    parameterInspectionMatcher = INSPECTOR_PARAMETER_PATTERN.matcher(input);
                } else {
                    parameterInspectionMatcher.reset(input);
                }
                List<PythonInspectorParameter> parameters = extractPythonInspectorParameters(parameterInspectionMatcher);
                functions.put(functionName, parameters.toArray(PythonInspectorParameter[]::new));
            }
        }
        return functions;
    }

    private static @NotNull List<PythonInspectorParameter> extractPythonInspectorParameters(Matcher matcher) {
        List<PythonInspectorParameter> parameters = new ArrayList<>();
        while (matcher.find()) {
            int index = Integer.parseInt(matcher.group("index"));
            String name = matcher.group("name");
            String defaultValue = matcher.group("defaultValue");
            String typeString = matcher.group("type");
            ParameterType type = ParameterType.valueOf(typeString);
            parameters.add(new PythonInspectorParameter(index, name, defaultValue, type));
        }
        return parameters;
    }

    public static Collection<DefinedParameter> convertPyParameters(PythonInspector.PythonInspectorParameter[] parameters,
                                                                   PyParameter[] pyParameters,
                                                                   boolean instanceFunction) {
        if (parameters == null || parameters.length == 0) {
            return Collections.emptyList();
        }

        record Tuple(PythonInspector.PythonInspectorParameter parameter, PyParameter pyParameter) {}

        int indexCorrection = 0;
        PythonInspector.ParameterType lastSeenType = null;
        List<Tuple> matchedParameters = new ArrayList<>();
        for (PythonInspector.PythonInspectorParameter parameter : parameters) {
            if (lastSeenType != null && lastSeenType != parameter.type() && (
                    lastSeenType == ParameterType.POSITIONAL_ONLY && parameter.type() != ParameterType.VAR_POSITIONAL
                    || lastSeenType == ParameterType.POSITIONAL_OR_KEYWORD && parameter.type() == ParameterType.KEYWORD_ONLY)) {
                indexCorrection++;
            }
            lastSeenType = parameter.type();
            int index = parameter.index() + indexCorrection;
            if (index >= pyParameters.length) {
                break;
            }
            Tuple tuple = new Tuple(parameter, pyParameters[index]);
            matchedParameters.add(tuple);
        }
        return matchedParameters.stream()
                                .filter(parameter -> !instanceFunction || !"self".equals(parameter.parameter.name()))
                                .filter(parameter -> parameter.parameter.type() != PythonInspector.ParameterType.VAR_POSITIONAL)
                                .map(parameter -> {
                                    String defaultValue = parameter.parameter.defaultValue();
                                    return new ParameterDto(parameter.pyParameter,
                                                            parameter.parameter.name(),
                                                            defaultValue,
                                                            parameter.parameter.type() == ParameterType.VAR_KEYWORD);
                                })
                                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public record PythonInspectorParameter(int index, String name, String defaultValue, ParameterType type) {

        public static final PythonInspectorParameter[] EMPTY = new PythonInspectorParameter[0];
    }

    public enum ParameterType {
        POSITIONAL_ONLY, POSITIONAL_OR_KEYWORD, KEYWORD_ONLY, VAR_POSITIONAL, VAR_KEYWORD
    }
}
