package dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.completion.KeywordCompletionModification;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.config.RobotOptionsProvider;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ImportType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedKeyword;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallLibrary;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallName;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RobotLibraryNamesCollector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map.Entry;

public class ResolverUtils {

    private ResolverUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    @NotNull
    public static PsiElement[] findKeywordReferences(@NotNull RobotKeywordCallName keywordCallName, @Nullable PsiFile psiFile) {
        RobotKeywordCallLibrary keywordCallLibrary = keywordCallName.getKeywordCallLibrary();
        String libraryName = keywordCallLibrary != null ? keywordCallLibrary.getText() : null;
        String keywordName = keywordCallName.getText();
        if (KeywordCompletionModification.isKeywordStartsWithModifier(libraryName)) {
            libraryName = libraryName.substring(1);
        } else if (libraryName == null && KeywordCompletionModification.isKeywordStartsWithModifier(keywordName)) {
            keywordName = keywordName.substring(1);
        }
        if (libraryName != null) {
            int libraryNameLength = libraryName.length();
            keywordName = keywordName.substring(libraryNameLength + 1);
        }
        return findKeywordReferences(libraryName, keywordName, psiFile);
    }

    @NotNull
    public static PsiElement[] findKeywordReferences(@Nullable String libraryName, @NotNull String keyword, @Nullable PsiFile psiFile) {
        if (!(psiFile instanceof RobotFile robotFile)) {
            return null;
        }

        Collection<PsiElement> keywordElements = new LinkedHashSet<>();
        for (DefinedKeyword definedKeyword : robotFile.getDefinedKeywords()) {
            if (definedKeyword.matches(keyword)) {
                keywordElements.add(definedKeyword.reference());
            }
        }

        Collection<KeywordFile> importedFiles;
        if (libraryName != null) {
            importedFiles = robotFile.findImportedFilesWithLibraryName(libraryName);
        } else {
            boolean includeTransitive = RobotOptionsProvider.getInstance(psiFile.getProject()).allowTransitiveImports();
            importedFiles = robotFile.collectImportedFiles(includeTransitive);
        }

        for (KeywordFile keywordFile : importedFiles) {
            for (DefinedKeyword definedKeyword : keywordFile.getDefinedKeywords()) {
                if (definedKeyword.matches(keyword)) {
                    keywordElements.add(definedKeyword.reference());
                }
            }
        }
        return keywordElements.toArray(PsiElement[]::new);
    }

    @NotNull
    public static PsiElement @NotNull [] findKeywordLibraryReference(@NotNull String libraryName, @Nullable PsiFile psiFile) {
        if (!(psiFile instanceof RobotFile robotFile)) {
            return PsiElement.EMPTY_ARRAY;
        }
        boolean includeTransitive = RobotOptionsProvider.getInstance(psiFile.getProject()).allowTransitiveImports();
        return robotFile.collectImportedFiles(includeTransitive)
                        .stream()
                        .filter(keywordFile -> keywordFile.getImportType() == ImportType.RESOURCE)
                        .flatMap(keywordFile -> {
                            RobotLibraryNamesCollector libraryNamesCollector = new RobotLibraryNamesCollector();
                            keywordFile.getPsiFile().acceptChildren(libraryNamesCollector);
                            return libraryNamesCollector.getRenamedLibraries().entrySet().stream();
                        })
                        .filter(entry -> libraryName.equals(entry.getKey()))
                        .map(Entry::getValue)
                        .toArray(PsiElement[]::new);
    }

    @Nullable
    public static PsiElement findVariableElement(RobotVariable variable, String variableName, @Nullable PsiFile psiFile) {
        if (!(psiFile instanceof RobotFile robotFile)) {
            return null;
        }
        for (DefinedVariable definedVariable : robotFile.getDefinedVariables()) {
            if (definedVariable.matches(variableName) && definedVariable.isInScope(variable)) {
                return definedVariable.reference();
            }
        }

        boolean includeTransitive = RobotOptionsProvider.getInstance(psiFile.getProject()).allowTransitiveImports();
        for (KeywordFile keywordFile : robotFile.collectImportedFiles(includeTransitive)) {
            for (DefinedVariable definedVariable : keywordFile.getDefinedVariables()) {
                if (definedVariable.matches(variableName) && definedVariable.isInScope(variable)) {
                    return definedVariable.reference();
                }
            }
        }
        return null;
    }
}
