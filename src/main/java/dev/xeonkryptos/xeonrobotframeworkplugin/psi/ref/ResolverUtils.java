package dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.completion.KeywordCompletionModification;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.config.RobotOptionsProvider;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.misc.RobotReadWriteAccessDetector;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ImportType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.VariableDto;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedKeyword;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallLibrary;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallName;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RobotLibraryNamesCollector;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RobotSectionVariablesCollector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
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
    public static PsiElement findVariableElement(@Nullable String variableName, @Nullable PsiFile psiFile) {
        if (variableName == null || !(psiFile instanceof RobotFile robotFile)) {
            return null;
        }
        for (DefinedVariable definedVariable : robotFile.getDefinedVariables()) {
            if (definedVariable.matches(variableName)) {
                return definedVariable.reference();
            }
        }

        boolean includeTransitive = RobotOptionsProvider.getInstance(psiFile.getProject()).allowTransitiveImports();
        for (KeywordFile keywordFile : robotFile.collectImportedFiles(includeTransitive)) {
            for (DefinedVariable definedVariable : keywordFile.getDefinedVariables()) {
                if (definedVariable.matches(variableName)) {
                    return definedVariable.reference();
                }
            }
        }
        return null;
    }

    @NotNull
    public static List<DefinedVariable> walkKeyword(@Nullable RobotKeywordCall keywordCall) {
        if (keywordCall == null) {
            return Collections.emptyList();
        }

        String keywordName = keywordCall.getName();
        if (RobotReadWriteAccessDetector.isVariableSetterKeyword(keywordName)) {
            List<RobotPositionalArgument> positionalArgumentList = keywordCall.getPositionalArgumentList();
            if (!positionalArgumentList.isEmpty() && positionalArgumentList.getFirst().getFirstChild() instanceof RobotVariable variable) {
                String variableName = variable.getVariableName();
                if (variableName != null) {
                    DefinedVariable definedVariable = new VariableDto(variable, variableName, null);
                    return Collections.singletonList(definedVariable);
                }
            }
            return Collections.emptyList();
        }

        List<DefinedVariable> variables = new ArrayList<>();
        PsiElement resolvedElement = keywordCall.getKeywordCallName().getReference().resolve();
        if (resolvedElement instanceof RobotUserKeywordStatement userKeywordStatement) {
            RobotSectionVariablesCollector variablesCollector = new RobotSectionVariablesCollector();
            userKeywordStatement.accept(variablesCollector);
            Collection<DefinedVariable> collectedVariables = variablesCollector.getVariables();
            variables.addAll(collectedVariables);
        }
        return variables;
    }
}
