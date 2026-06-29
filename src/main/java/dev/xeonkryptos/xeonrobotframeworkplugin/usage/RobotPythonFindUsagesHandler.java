package dev.xeonkryptos.xeonrobotframeworkplugin.usage;

import com.intellij.find.findUsages.FindUsagesHandler;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.jetbrains.python.psi.PyExpression;
import com.jetbrains.python.psi.PyFunction;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotPyUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.KeywordUtil;
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
            addStringsToSearch(pyFunction, keywordNames, element.getContainingFile());
            return keywordNames;
        }
        return super.getStringsToSearch(element);
    }

    @SuppressWarnings("UnstableApiUsage")
    private static void addStringsToSearch(@NotNull PyFunction pyFunction, @NotNull Set<String> keywordNames, PsiFile file) {
        getDecoratorKeywordName(pyFunction).ifPresent(keywordNames::add);
        String pyFunctionName = pyFunction.getName();
        if (pyFunctionName != null) {
            String convertedKeywordName = KeywordUtil.getInstance().functionToKeyword(pyFunctionName, file);
            keywordNames.add(convertedKeywordName);
        }
    }

    @NotNull
    @SuppressWarnings("UnstableApiUsage")
    private static Optional<String> getDecoratorKeywordName(PyFunction pyFunction) {
        return RobotPyUtil.findCustomKeywordDecorator(pyFunction)
                          .map(decorator -> decorator.getArgument(0, "keyword", PyExpression.class))
                          .map(PyExpression::getText);
    }

    @Override
    protected boolean isSearchForTextOccurrencesAvailable(@NotNull PsiElement psiElement, boolean isSingleFile) {
        return true;
    }
}
