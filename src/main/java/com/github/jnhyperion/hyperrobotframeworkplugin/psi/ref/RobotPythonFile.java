package com.github.jnhyperion.hyperrobotframeworkplugin.psi.ref;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.dto.ImportType;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.dto.KeywordDto;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.dto.VariableDto;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.DefinedKeyword;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.DefinedVariable;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordFile;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.util.ReservedVariable;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.util.ReservedVariableScope;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyFile;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.PyTargetExpression;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

public class RobotPythonFile extends RobotPythonWrapper implements KeywordFile {

    private final String namespace;
    private final PyFile pythonFile;
    private final ImportType importType;
    private final String uniqueIdentifier;
    private final Project project;
    private final boolean isDifferentNamespace;

    public RobotPythonFile(@NotNull String namespace,
                           @NotNull PyFile pythonFile,
                           @NotNull ImportType importType,
                           @NotNull Project project,
                           boolean isDifferentNamespace) {
        this.namespace = namespace;
        this.pythonFile = pythonFile;
        this.importType = importType;
        this.uniqueIdentifier = pythonFile.getVirtualFile().getPath() + "#" + namespace;
        this.project = project;
        this.isDifferentNamespace = isDifferentNamespace;
    }

    @NotNull
    @Override
    public final synchronized Collection<DefinedKeyword> getDefinedKeywords() {
        Collection<DefinedKeyword> keywords;
        try {
            Map<String, Collection<DefinedKeyword>> cachedKeywords = ProjectFileCache.getCachedKeywords(this.project);
            keywords = cachedKeywords.get(this.uniqueIdentifier);
            if (keywords == null) {
                HashSet<DefinedKeyword> keywordSet = new HashSet<>();
                if (this.importType.equals(ImportType.LIBRARY)) {
                    try {
                        for (PyFunction function : this.pythonFile.getTopLevelFunctions()) {
                            String functionName = getValidName(function.getName());
                            if (functionName != null) {
                                String keywordName = getKeywordName(function);
                                if (keywordName != null) {
                                    functionName = keywordName;
                                }
                                keywordSet.add(new KeywordDto(function,
                                                              this.namespace,
                                                              functionName,
                                                              hasNonSelfParameter(function.getParameterList().getParameters()),
                                                              Arrays.asList(function.getParameterList().getParameters())));
                            }
                        }

                        for (PyTargetExpression attribute : this.pythonFile.getTopLevelAttributes()) {
                            String attributeName = getValidName(attribute.getName());
                            if (attributeName != null) {
                                keywordSet.add(new KeywordDto(attribute, this.namespace, attributeName));
                            }
                        }

                        for (PyClass pyClass : this.pythonFile.getTopLevelClasses()) {
                            if (pyClass.getName() != null && !pyClass.getName().startsWith("_")) {
                                String className = pyClass.getQualifiedName();
                                if (className == null) {
                                    className = "";
                                }
                                if (this.namespace.equals(pyClass.getName()) || this.isDifferentNamespace) {
                                    className = this.namespace;
                                }
                                addDefinedKeywords(pyClass, className, keywordSet);
                            }
                        }
                    } catch (Throwable t) {
                        keywordSet.clear();
                    }
                }

                if (!keywordSet.isEmpty()) {
                    cachedKeywords.put(this.uniqueIdentifier, keywordSet);
                }

                return keywordSet;
            }
        } catch (Throwable t) {
            return new HashSet<>();
        }
        return keywords;
    }

    @NotNull
    @Override
    public final synchronized Collection<DefinedVariable> getDefinedVariables() {
        Collection<DefinedVariable> variables;
        try {
            Map<String, Collection<DefinedVariable>> cachedVariables = ProjectFileCache.getCachedVariables(this.project);
            variables = cachedVariables.get(this.uniqueIdentifier);
            if (variables == null) {
                HashSet<DefinedVariable> variableSet = new HashSet<>();
                if (this.importType.equals(ImportType.VARIABLES)) {
                    try {
                        for (PyTargetExpression attribute : this.pythonFile.getTopLevelAttributes()) {
                            String attributeName = attribute.getName();
                            if (attributeName != null) {
                                variableSet.add(new VariableDto(attribute, ReservedVariable.wrapToScalar(attributeName), ReservedVariableScope.Global));
                            }
                        }

                        for (PyClass pyClass : this.pythonFile.getTopLevelClasses()) {
                            addDefinedVariables(pyClass, variableSet);
                        }
                    } catch (Throwable t) {
                        variableSet.clear();
                    }
                }

                if (!variableSet.isEmpty()) {
                    cachedVariables.put(this.uniqueIdentifier, variableSet);
                }

                return variableSet;
            }
        } catch (Throwable t) {
            return new HashSet<>();
        }
        return variables;
    }

    @NotNull
    @Override
    public final ImportType getImportType() {
        return this.importType;
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
        return this.namespace;
    }

    @Override
    public VirtualFile getVirtualFile() {
        return this.pythonFile.getVirtualFile();
    }

    @Override
    public final PsiFile getPsiFile() {
        return this.pythonFile;
    }

    @Override
    public final boolean isDifferentNamespace() {
        return this.isDifferentNamespace;
    }
}
