package dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref;

import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.completion.KeywordCompletionModification;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallLibraryName;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

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
            return Arrays.stream(ResolverUtils.findKeywordLibraryReference(libraryName, containingFile))
                         .map(PsiElementResolveResult::new)
                         .toArray(ResolveResult[]::new);
        }, false, false);
    }
}
