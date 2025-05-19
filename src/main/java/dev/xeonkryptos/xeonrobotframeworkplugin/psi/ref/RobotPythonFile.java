package dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ImportType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.KeywordDto;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.VariableDto;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedKeyword;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.ReservedVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.ReservedVariableScope;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.CachedValueProvider.Result;
import com.intellij.psi.util.CachedValuesManager;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyFile;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.PyTargetExpression;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class RobotPythonFile extends RobotPythonWrapper implements KeywordFile {

    private final String namespace;
    private final PyFile pythonFile;
    private final ImportType importType;
    private final boolean isDifferentNamespace;

    public RobotPythonFile(@NotNull String namespace, @NotNull PyFile pythonFile, @NotNull ImportType importType, boolean isDifferentNamespace) {
        this.namespace = namespace;
        this.pythonFile = pythonFile;
        this.importType = importType;
        this.isDifferentNamespace = isDifferentNamespace;
    }

    @NotNull
    @Override
    @SuppressWarnings("UnstableApiUsage")
    public final Collection<DefinedKeyword> getDefinedKeywords() {
        if (importType == ImportType.LIBRARY) {
            return CachedValuesManager.getCachedValue(pythonFile, () -> {
                Set<DefinedKeyword> keywordSet = new HashSet<>();
                Map<String, PyFunction> functions = new LinkedHashMap<>();
                for (PyFunction function : pythonFile.getTopLevelFunctions()) {
                    String functionName = getValidName(function.getName());
                    if (functionName != null) {
                        functions.put(functionName, function);
                    }
                }
                addDefinedKeywords(pythonFile, namespace, keywordSet, functions);

                for (PyTargetExpression attribute : pythonFile.getTopLevelAttributes()) {
                    String attributeName = getValidName(attribute.getName());
                    if (attributeName != null) {
                        keywordSet.add(new KeywordDto(attribute, namespace, attributeName));
                    }
                }

                for (PyClass pyClass : pythonFile.getTopLevelClasses()) {
                    String className = pyClass.getName();
                    if (className != null && !className.startsWith("_")) {
                        className = pyClass.getQualifiedName();
                        if (className == null) {
                            className = "";
                        }
                        if (namespace.equals(className) || isDifferentNamespace) {
                            className = namespace;
                        }
                        addDefinedKeywords(pyClass, className, keywordSet);
                    }
                }
                Object[] dependents = Stream.concat(Stream.of(pythonFile), keywordSet.stream().map(DefinedKeyword::reference)).toArray();
                return new Result<>(keywordSet, dependents);
            });
        }
        return Set.of();
    }

    @NotNull
    @Override
    @SuppressWarnings("UnstableApiUsage")
    public final synchronized Collection<DefinedVariable> getDefinedVariables() {
        if (importType == ImportType.VARIABLES) {
            return CachedValuesManager.getCachedValue(pythonFile, () -> {
                Set<DefinedVariable> variables = new HashSet<>();
                for (PyTargetExpression attribute : pythonFile.getTopLevelAttributes()) {
                    String attributeName = attribute.getName();
                    if (attributeName != null) {
                        variables.add(new VariableDto(attribute, ReservedVariable.wrapToScalar(attributeName), ReservedVariableScope.Global));
                    }
                }
                for (PyClass pyClass : pythonFile.getTopLevelClasses()) {
                    addDefinedVariables(pyClass, variables);
                }
                Object[] dependents = Stream.concat(Stream.of(pythonFile), variables.stream().map(DefinedVariable::reference)).toArray();
                return new Result<>(variables, dependents);
            });
        }
        return Set.of();
    }

    @NotNull
    @Override
    public final ImportType getImportType() {
        return importType;
    }

    @NotNull
    @Override
    public final Collection<KeywordFile> getImportedFiles(boolean includeTransitive) {
        return Collections.emptyList();
    }

    @NotNull
    @Override
    public final Collection<VirtualFile> getVirtualFiles(boolean includeTransitive) {
        return Collections.emptyList();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        RobotPythonFile that = (RobotPythonFile) obj;
        return namespace.equals(that.namespace) && pythonFile.equals(that.pythonFile);
    }

    @Override
    public int hashCode() {
        return 31 * namespace.hashCode() + pythonFile.hashCode();
    }

    @Override
    public String toString() {
        return namespace;
    }

    @Override
    public VirtualFile getVirtualFile() {
        return pythonFile.getVirtualFile();
    }

    @Override
    public final PsiFile getPsiFile() {
        return pythonFile;
    }

    @Override
    public final boolean isDifferentNamespace() {
        return isDifferentNamespace;
    }

    @Override
    public boolean isValid() {
        return pythonFile.isValid();
    }
}
