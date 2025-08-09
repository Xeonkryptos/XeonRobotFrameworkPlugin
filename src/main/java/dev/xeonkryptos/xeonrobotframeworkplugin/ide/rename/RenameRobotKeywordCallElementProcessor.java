package dev.xeonkryptos.xeonrobotframeworkplugin.ide.rename;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.SearchScope;
import com.intellij.refactoring.rename.RenamePsiElementProcessor;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.PyStringLiteralExpression;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class RenameRobotKeywordCallElementProcessor extends RenamePsiElementProcessor {

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public boolean canProcessElement(@NotNull PsiElement element) {
        if (element instanceof RobotKeywordCall keywordCall) {
            PsiReference reference = keywordCall.getKeywordCallName().getReference();
            if (reference.resolve() instanceof PyFunction pyFunction) {
                return Optional.ofNullable(pyFunction.getDecoratorList())
                               .map(decoratorList -> decoratorList.findDecorator("keyword"))
                               .map(decorator -> decorator.getArgument(0, "name", PyStringLiteralExpression.class))
                               .isPresent();
            }
        }
        return false;
    }

    @NotNull
    @Unmodifiable
    @Override
    public Collection<PsiReference> findReferences(@NotNull PsiElement element, @NotNull SearchScope searchScope, boolean searchInCommentsAndStrings) {
        return List.of();
    }
}
