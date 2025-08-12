package dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref;

import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.CachedValueProvider.Result;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.ParameterizedCachedValue;
import com.jetbrains.python.psi.PyClass;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ImportType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedKeyword;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class RobotPythonClass implements KeywordFile {

    private static final Key<ParameterizedCachedValue<Collection<DefinedKeyword>, String>> KEYWORD_CACHE_KEY = new Key<>("ROBOT_PYTHON_CLASS_KEYWORDS_CACHE");

    private final String library;
    private final PyClass pythonClass;
    private final ImportType importType;

    public RobotPythonClass(@Nullable String library, @NotNull PyClass pythonClass, @NotNull ImportType importType) {
        this.library = library;
        this.pythonClass = pythonClass;
        this.importType = importType;
    }

    @NotNull
    @Override
    public final Collection<DefinedKeyword> getDefinedKeywords() {
        if (importType == ImportType.LIBRARY) {
            return CachedValuesManager.getManager(pythonClass.getProject()).getParameterizedCachedValue(pythonClass, KEYWORD_CACHE_KEY, libraryName -> {
                Set<DefinedKeyword> newKeywords = new LinkedHashSet<>();
                RobotKeywordFileResolver.addDefinedKeywords(pythonClass, libraryName, newKeywords);
                return new Result<>(newKeywords, new Object[] { pythonClass });
            }, false, library);
        }
        return Set.of();
    }

    @NotNull
    @Override
    public Collection<DefinedVariable> getDefinedVariables(Collection<KeywordFile> visitedFiles) {
        return getDefinedVariables();
    }

    @NotNull
    @Override
    public final Collection<DefinedVariable> getDefinedVariables() {
        if (importType == ImportType.VARIABLES) {
            return RobotKeywordFileResolver.resolveVariables(pythonClass);
        }
        return List.of();
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RobotPythonClass that = (RobotPythonClass) o;
        return Objects.equals(this.library, that.library) && this.pythonClass.equals(that.pythonClass);
    }

    @Override
    public int hashCode() {
        if (library != null) {
            int result = library.hashCode();
            return 31 * result + pythonClass.hashCode();
        }
        return pythonClass.hashCode();
    }

    @Override
    public String toString() {
        return pythonClass.toString();
    }

    @Override
    public VirtualFile getVirtualFile() {
        return getPsiFile().getVirtualFile();
    }

    @Override
    public final PsiFile getPsiFile() {
        return pythonClass.getContainingFile();
    }

    @Nullable
    @Override
    public String getLibraryName() {
        return library;
    }
}
