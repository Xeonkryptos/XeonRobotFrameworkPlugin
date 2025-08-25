package dev.xeonkryptos.xeonrobotframeworkplugin.ide.rename;

import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.refactoring.rename.RenamePyElementProcessor;
import com.jetbrains.python.refactoring.rename.RenamePyFunctionProcessor;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotPyUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Map;

public class RenameRobotPyFunctionProcessor extends RenamePyElementProcessor {

    private final RenamePyFunctionProcessor renamePyFunctionProcessor = new RenamePyFunctionProcessor();

    @Override
    public boolean canProcessElement(@NotNull PsiElement element) {
        return renamePyFunctionProcessor.canProcessElement(element);
    }

    @Override
    public boolean isToSearchInComments(@NotNull PsiElement element) {
        return renamePyFunctionProcessor.isToSearchInComments(element);
    }

    @Override
    public void setToSearchInComments(@NotNull PsiElement element, boolean enabled) {
        renamePyFunctionProcessor.setToSearchInComments(element, enabled);
    }

    @Override
    public boolean isToSearchForTextOccurrences(@NotNull PsiElement element) {
        return renamePyFunctionProcessor.isToSearchForTextOccurrences(element);
    }

    @Override
    public void setToSearchForTextOccurrences(@NotNull PsiElement element, boolean enabled) {
        renamePyFunctionProcessor.setToSearchForTextOccurrences(element, enabled);
    }

    @Override
    public PsiElement substituteElementToRename(@NotNull PsiElement element, @Nullable Editor editor) {
        return renamePyFunctionProcessor.substituteElementToRename(element, editor);
    }

    @Override
    public void prepareRenaming(@NotNull PsiElement element, @NotNull String newName, @NotNull Map<PsiElement, String> allRenames) {
        renamePyFunctionProcessor.prepareRenaming(element, newName, allRenames);
    }

    @NotNull
    @Override
    @Unmodifiable
    @SuppressWarnings("UnstableApiUsage")
    public Collection<PsiReference> findReferences(@NotNull PsiElement element, @NotNull SearchScope searchScope, boolean searchInCommentsAndStrings) {
        boolean customKeywordName = RobotPyUtil.findCustomKeywordNameDecoratorExpression((PyFunction) element).isPresent();
        if (customKeywordName) {
            return ReferencesSearch.search(element, searchScope).filtering(reference -> !(reference.getElement() instanceof RobotStatement)).findAll();
        }
        return super.findReferences(element, searchScope, searchInCommentsAndStrings);
    }
}
