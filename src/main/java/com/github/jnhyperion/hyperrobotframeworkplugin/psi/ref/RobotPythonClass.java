package com.github.jnhyperion.hyperrobotframeworkplugin.psi.ref;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.dto.ImportType;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.DefinedKeyword;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.DefinedVariable;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordFile;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.CachedValueProvider.Result;
import com.intellij.psi.util.CachedValuesManager;
import com.jetbrains.python.psi.PyClass;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class RobotPythonClass extends RobotPythonWrapper implements KeywordFile {

    private final String library;
    private final PyClass pythonClass;
    private final ImportType importType;
    private final boolean isDifferentNamespace;

    public RobotPythonClass(@NotNull String library, @NotNull PyClass pythonClass, @NotNull ImportType importType, boolean isDifferentNamespace) {
        this.library = library;
        this.pythonClass = pythonClass;
        this.importType = importType;
        this.isDifferentNamespace = isDifferentNamespace;
    }

    @NotNull
    @Override
    public final Collection<DefinedKeyword> getDefinedKeywords() {
        if (importType == ImportType.LIBRARY) {
            return CachedValuesManager.getCachedValue(pythonClass, () -> {
                Set<DefinedKeyword> newKeywords = new HashSet<>();
                addDefinedKeywords(pythonClass, library, newKeywords);
                Object[] dependents = Stream.concat(Stream.of(pythonClass), newKeywords.stream().map(DefinedKeyword::reference)).toArray();
                return new Result<>(newKeywords, dependents);
            });
        }
        return Set.of();
    }

    @NotNull
    @Override
    public final Collection<DefinedVariable> getDefinedVariables() {
        if (importType == ImportType.VARIABLES) {
            return CachedValuesManager.getCachedValue(pythonClass, () -> {
                Set<DefinedVariable> newVariables = new HashSet<>();
                addDefinedVariables(pythonClass, newVariables);
                Object[] dependents = Stream.concat(Stream.of(pythonClass), newVariables.stream().map(DefinedVariable::reference)).toArray();
                return new Result<>(newVariables, dependents);
            });
        }
        return Set.of();
    }

    @NotNull
    @Override
    public final ImportType getImportType() {
        return importType;
    }

    @Override
    public final @NotNull Collection<KeywordFile> getImportedFiles(boolean includeTransitive) {
        return Collections.emptyList();
    }

    @NotNull
    @Override
    public final Collection<VirtualFile> getVirtualFiles(boolean includeTransitive) {
        return Collections.emptyList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RobotPythonClass that = (RobotPythonClass) o;
        return this.library.equals(that.library) && this.pythonClass.equals(that.pythonClass);
    }

    @Override
    public int hashCode() {
        int result = this.library.hashCode();
        result = 31 * result + this.pythonClass.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return this.library;
    }

    @Override
    public VirtualFile getVirtualFile() {
        return this.getPsiFile().getVirtualFile();
    }

    @Override
    public final PsiFile getPsiFile() {
        return this.pythonClass.getContainingFile();
    }

    @Override
    public final boolean isDifferentNamespace() {
        return this.isDifferentNamespace;
    }
}
