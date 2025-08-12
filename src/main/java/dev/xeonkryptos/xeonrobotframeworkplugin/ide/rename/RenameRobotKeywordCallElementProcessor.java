package dev.xeonkryptos.xeonrobotframeworkplugin.ide.rename;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.refactoring.rename.RenamePsiElementProcessor;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.PyStringLiteralExpression;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class RenameRobotKeywordCallElementProcessor extends RenamePsiElementProcessor {

    @Override
    public boolean canProcessElement(@NotNull PsiElement element) {
        if (element instanceof RobotKeywordCall keywordCall) {
            PsiReference reference = keywordCall.getKeywordCallName().getReference();
            if (reference.resolve() instanceof PyFunction pyFunction) {
                return findCustomKeywordNameDecoratorExpression(pyFunction).isPresent();
            }
        }
        return false;
    }

    @NotNull
    @Unmodifiable
    @Override
    public Collection<PsiReference> findReferences(@NotNull PsiElement element, @NotNull SearchScope searchScope, boolean searchInCommentsAndStrings) {
        PyFunction pyFunction = element.getUserData(RobotMemberInplaceRenameHandler.REFERENCED_PY_FUNCTION_KEY);
        assert pyFunction != null : "Expected a PyFunction, but got: NULL";
        return ReferencesSearch.search(pyFunction, searchScope).findAll();
    }

    @Override
    public void prepareRenaming(@NotNull PsiElement element, @NotNull String newName, @NotNull Map<PsiElement, String> allRenames) {
        PyFunction pyFunction = element.getUserData(RobotMemberInplaceRenameHandler.REFERENCED_PY_FUNCTION_KEY);
        assert pyFunction != null : "Expected a PyFunction, but got: NULL";
        PyStringLiteralExpression pyStringLiteralExpression = findCustomKeywordNameDecoratorExpression(pyFunction).orElseThrow();
        allRenames.put(pyStringLiteralExpression, newName);
    }

    @SuppressWarnings("UnstableApiUsage")
    private Optional<PyStringLiteralExpression> findCustomKeywordNameDecoratorExpression(@NotNull PyFunction pyFunction) {
        return Optional.ofNullable(pyFunction.getDecoratorList())
                       .map(decoratorList -> decoratorList.findDecorator("keyword"))
                       .map(decorator -> decorator.getArgument(0, "name", PyStringLiteralExpression.class));
    }
}
