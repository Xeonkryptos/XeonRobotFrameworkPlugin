package dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.completion.KeywordCompletionModification;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.config.RobotOptionsProvider;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ImportType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallLibraryName;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RobotLibraryNamesCollector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Map.Entry;

public class RobotKeywordCallLibraryReference extends PsiPolyVariantReferenceBase<RobotKeywordCallLibraryName> {

    public RobotKeywordCallLibraryReference(@NotNull RobotKeywordCallLibraryName keyword) {
        super(keyword, false);
    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        RobotKeywordCallLibraryName keywordCallLibraryId = getElement();
        ResolveCache resolveCache = ResolveCache.getInstance(keywordCallLibraryId.getProject());
        return resolveCache.resolveWithCaching(this, (robotKeywordReference, incompCode) -> {
            String libraryName = keywordCallLibraryId.getText();
            if (KeywordCompletionModification.isKeywordStartsWithModifier(libraryName)) {
                libraryName = libraryName.substring(1);
            }
            PsiFile containingFile = keywordCallLibraryId.getContainingFile();
            return Arrays.stream(findKeywordLibraryReference(libraryName, containingFile)).map(PsiElementResolveResult::new).toArray(ResolveResult[]::new);
        }, false, false);
    }

    @NotNull
    private PsiElement @NotNull [] findKeywordLibraryReference(@NotNull String libraryName, @Nullable PsiFile psiFile) {
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
}
