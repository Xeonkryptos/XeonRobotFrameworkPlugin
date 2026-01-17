package dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.jetbrains.python.psi.PyClass;
import dev.xeonkryptos.xeonrobotframeworkplugin.completion.KeywordCompletionModification;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ImportType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallLibraryName;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RobotLibraryNamesCollector;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotNames;
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
            PsiElement[] keywordLibraryReference = findKeywordLibraryReference(libraryName, containingFile);
            return Arrays.stream(keywordLibraryReference).map(PsiElementResolveResult::new).toArray(ResolveResult[]::new);
        }, false, false);
    }

    @NotNull
    private PsiElement @NotNull [] findKeywordLibraryReference(@NotNull String libraryName, @Nullable PsiFile psiFile) {
        if (!(psiFile instanceof RobotFile robotFile)) {
            return PsiElement.EMPTY_ARRAY;
        }
        if (libraryName.equalsIgnoreCase(RobotNames.BUILTIN_NAMESPACE)) {
            PyClass builtInImportClass = PythonResolver.getBuiltInClass(psiFile);
            return builtInImportClass != null ? new PsiElement[] { builtInImportClass } : PsiElement.EMPTY_ARRAY;
        }
        return robotFile.collectImportedFiles(true, ImportType.RESOURCE).stream().flatMap(keywordFile -> {
            RobotLibraryNamesCollector libraryNamesCollector = new RobotLibraryNamesCollector();
            keywordFile.getPsiFile().acceptChildren(libraryNamesCollector);
            return libraryNamesCollector.getRenamedLibraries().entrySet().stream();
        }).filter(entry -> libraryName.equals(entry.getKey())).map(Entry::getValue).toArray(PsiElement[]::new);
    }
}
