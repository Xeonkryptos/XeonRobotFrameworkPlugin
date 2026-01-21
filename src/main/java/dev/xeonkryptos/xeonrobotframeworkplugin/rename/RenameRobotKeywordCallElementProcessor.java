package dev.xeonkryptos.xeonrobotframeworkplugin.rename;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.refactoring.listeners.RefactoringElementListener;
import com.intellij.refactoring.rename.RenamePsiElementProcessor;
import com.intellij.usageView.UsageInfo;
import com.intellij.util.IncorrectOperationException;
import com.jetbrains.python.psi.PyFunction;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotPyUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;

public class RenameRobotKeywordCallElementProcessor extends RenamePsiElementProcessor {

    @Override
    public boolean canProcessElement(@NotNull PsiElement element) {
        if (element instanceof RobotKeywordCall keywordCall) {
            PsiReference reference = keywordCall.getKeywordCallName().getReference();
            return reference.resolve() instanceof PyFunction;
        }
        return false;
    }

    @NotNull
    @Unmodifiable
    @Override
    public Collection<PsiReference> findReferences(@NotNull PsiElement element, @NotNull SearchScope searchScope, boolean searchInCommentsAndStrings) {
        PyFunction pyFunction = (PyFunction) ((RobotKeywordCall) element).getKeywordCallName().getReference().resolve();
        assert pyFunction != null : "Expected a PyFunction, but got: NULL";
        return ReferencesSearch.search(pyFunction, searchScope).findAll();
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public void renameElement(@NotNull PsiElement element,
                              @NotNull String newName,
                              UsageInfo @NotNull [] usages,
                              @Nullable RefactoringElementListener listener) throws IncorrectOperationException {
        PyFunction pyFunction = (PyFunction) ((RobotKeywordCall) element).getKeywordCallName().getReference().resolve();
        assert pyFunction != null : "Expected a PyFunction, but got: NULL";

        super.renameElement(element, newName, usages, listener);

        RobotPyUtil.findCustomKeywordNameDecoratorExpression(pyFunction).ifPresent(expression -> expression.updateText(newName));
    }
}
