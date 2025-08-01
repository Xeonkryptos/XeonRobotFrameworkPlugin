package dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref;

import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider.Result;
import com.intellij.psi.util.CachedValuesManager;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyFile;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.PyTargetExpression;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ImportType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.KeywordDto;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedKeyword;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class RobotPythonFile implements KeywordFile {

    private static final Key<CachedValue<Collection<DefinedKeyword>>> KEYWORDS_CACHE_KEY = new Key<>("ROBOT_PYTHON_FILE_KEYWORDS_CACHE");

    private final String library;
    private final PyFile pythonFile;
    private final ImportType importType;

    public RobotPythonFile(@NotNull String library, @NotNull PyFile pythonFile, @NotNull ImportType importType) {
        this.library = library;
        this.pythonFile = pythonFile;
        this.importType = importType;
    }

    @NotNull
    @Override
    @SuppressWarnings("UnstableApiUsage")
    public final Collection<DefinedKeyword> getDefinedKeywords() {
        if (importType == ImportType.LIBRARY) {
            return CachedValuesManager.getCachedValue(pythonFile, KEYWORDS_CACHE_KEY, () -> {
                Set<DefinedKeyword> keywordSet = new HashSet<>();
                Map<String, PyFunction> functions = new LinkedHashMap<>();
                for (PyFunction function : pythonFile.getTopLevelFunctions()) {
                    String functionName = RobotKeywordFileResolver.getValidName(function.getName());
                    if (functionName != null) {
                        functions.put(functionName, function);
                    }
                }
                RobotKeywordFileResolver.addDefinedKeywords(pythonFile, library, keywordSet, functions);

                for (PyTargetExpression attribute : pythonFile.getTopLevelAttributes()) {
                    String attributeName = RobotKeywordFileResolver.getValidName(attribute.getName());
                    if (attributeName != null) {
                        keywordSet.add(new KeywordDto(attribute, attributeName));
                    }
                }

                for (PyClass pyClass : pythonFile.getTopLevelClasses()) {
                    String className = pyClass.getName();
                    if (className != null && !className.startsWith("_")) {
                        RobotKeywordFileResolver.addDefinedKeywords(pyClass, keywordSet);
                    }
                }
                return new Result<>(keywordSet, new Object[] { pythonFile });
            });
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
        return RobotKeywordFileResolver.resolveVariables(pythonFile);
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
        return library.equals(that.library) && pythonFile.equals(that.pythonFile);
    }

    @Override
    public int hashCode() {
        return 31 * library.hashCode() + pythonFile.hashCode();
    }

    @Override
    public String toString() {
        return library;
    }

    @Override
    public VirtualFile getVirtualFile() {
        return pythonFile.getVirtualFile();
    }

    @Override
    public final PsiFile getPsiFile() {
        return pythonFile;
    }

    @Nullable
    @Override
    public String getLibraryName() {
        return library;
    }
}
