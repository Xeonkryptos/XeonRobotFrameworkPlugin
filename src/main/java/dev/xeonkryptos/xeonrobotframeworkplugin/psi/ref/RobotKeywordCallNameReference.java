package dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.search.GlobalSearchScope;
import com.jetbrains.python.psi.PyFunction;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.completion.KeywordCompletionModification;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.index.PyRobotKeywordDefinitionIndex.Util;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ImportType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallLibrary;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallName;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.KeywordDefinitionNameIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

public class RobotKeywordCallNameReference extends PsiPolyVariantReferenceBase<RobotKeywordCallName> {

    public RobotKeywordCallNameReference(@NotNull RobotKeywordCallName keyword) {
        super(keyword, false);
    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        RobotKeywordCallName keywordCallName = getElement();
        ResolveCache resolveCache = ResolveCache.getInstance(keywordCallName.getProject());
        return resolveCache.resolveWithCaching(this, (robotKeywordReference, incompCode) -> {
            PsiFile containingFile = keywordCallName.getContainingFile();
            PsiElement[] keywordReferences = findKeywordReferences(keywordCallName, containingFile);
            return Arrays.stream(keywordReferences).map(PsiElementResolveResult::new).toArray(ResolveResult[]::new);
        }, false, false);
    }

    @NotNull
    private PsiElement[] findKeywordReferences(@NotNull RobotKeywordCallName keywordCallName, @Nullable PsiFile psiFile) {
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
    private PsiElement[] findKeywordReferences(@Nullable String libraryName, @NotNull String keyword, @Nullable PsiFile psiFile) {
        if (!(psiFile instanceof RobotFile robotFile)) {
            return null;
        }

        Collection<VirtualFile> importedFiles = collectImportedVirtualFilesOneselfIncluded(robotFile, libraryName);

        Project project = myElement.getProject();
        GlobalSearchScope searchScope = GlobalSearchScope.filesWithLibrariesScope(project, importedFiles);

        Collection<RobotUserKeywordStatement> userKeywordStatements = KeywordDefinitionNameIndex.getUserKeywordStatements(keyword, project, searchScope);
        Collection<PyFunction> pythonKeywordFunctions = Util.findKeywordFunctions(keyword, psiFile.getProject(), searchScope);
        Collection<PsiElement> keywordElements = new LinkedHashSet<>(userKeywordStatements.size() + pythonKeywordFunctions.size());
        keywordElements.addAll(userKeywordStatements);
        keywordElements.addAll(pythonKeywordFunctions);

        return keywordElements.toArray(PsiElement[]::new);
    }

    private static @NotNull Collection<VirtualFile> collectImportedVirtualFilesOneselfIncluded(RobotFile robotFile, String libraryName) {
        Collection<VirtualFile> importedFiles = libraryName != null ?
                                                robotFile.findImportedFilesWithLibraryName(libraryName) :
                                                robotFile.collectImportedFiles(true)
                                                         .stream()
                                                         .filter(keywordFile -> keywordFile.getImportType() != ImportType.VARIABLES)
                                                         .map(KeywordFile::getVirtualFile)
                                                         .collect(Collectors.toSet());
        importedFiles.add(robotFile.getViewProvider().getVirtualFile());
        return importedFiles;
    }
}
