package dev.xeonkryptos.xeonrobotframeworkplugin.ide.usage;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.PatternUtil;
import com.intellij.find.findUsages.FindUsagesHandler;
import com.intellij.psi.PsiElement;
import com.jetbrains.python.psi.PyExpression;
import com.jetbrains.python.psi.PyFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

public class RobotPythonFindUsagesHandler extends FindUsagesHandler {

    public RobotPythonFindUsagesHandler(@NotNull PsiElement psiElement) {
        super(psiElement);
    }

    @Nullable
    @Override
    protected Collection<String> getStringsToSearch(@NotNull PsiElement element) {
        if (element instanceof PyFunction pyFunction) {
            Set<String> keywordNames = new LinkedHashSet<>();
            Collection<String> stringsToSearch = super.getStringsToSearch(element);
            if (stringsToSearch != null) {
                keywordNames.addAll(stringsToSearch);
            }
            addStringsToSearch(pyFunction, keywordNames);
            return keywordNames;
        }
        return super.getStringsToSearch(element);
    }

    @SuppressWarnings("UnstableApiUsage")
    private static void addStringsToSearch(@NotNull PyFunction pyFunction, @NotNull Set<String> keywordNames) {
        getDecoratorKeywordName(pyFunction).ifPresent(keywordNames::add);
        String pyFunctionName = pyFunction.getName();
        String convertedKeywordName = PatternUtil.functionToKeyword(pyFunctionName);
        if (convertedKeywordName != null) {
            keywordNames.add(convertedKeywordName);
        }
    }

    @NotNull
    @SuppressWarnings("UnstableApiUsage")
    private static Optional<String> getDecoratorKeywordName(PyFunction pyFunction) {
        return Optional.ofNullable(pyFunction.getDecoratorList())
                       .map(decorators -> decorators.findDecorator("keyword"))
                       .map(decorator -> decorator.getArgument(0, "keyword", PyExpression.class))
                       .map(PyExpression::getText);
    }

    @Override
    protected boolean isSearchForTextOccurrencesAvailable(@NotNull PsiElement psiElement, boolean isSingleFile) {
        return true;
    }
}
