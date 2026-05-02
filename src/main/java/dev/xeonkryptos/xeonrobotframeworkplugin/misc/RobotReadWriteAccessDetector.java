package dev.xeonkryptos.xeonrobotframeworkplugin.misc;

import com.intellij.codeInsight.highlighting.ReadWriteAccessDetector;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.VariableScope;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.KeywordUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotNames;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class RobotReadWriteAccessDetector extends ReadWriteAccessDetector {

    private static final Map<String, VariableScope> VARIABLE_SETTERS = Map.ofEntries(Map.entry("setvariable", VariableScope.TestCase),
                                                                                     Map.entry("setglobalvariable", VariableScope.Global),
                                                                                     Map.entry("setlocalvariable", VariableScope.Local),
                                                                                     Map.entry("settestvariable", VariableScope.TestCase),
                                                                                     Map.entry("settaskvariable", VariableScope.TestCase),
                                                                                     Map.entry("setsuitevariable", VariableScope.TestSuite),
                                                                                     Map.entry("setvariableif", VariableScope.Local),
                                                                                     Map.entry(RobotNames.BUILTIN_NAMESPACE.toLowerCase() + ".setvariable", VariableScope.TestCase),
                                                                                     Map.entry(RobotNames.BUILTIN_NAMESPACE.toLowerCase() + ".setglobalvariable", VariableScope.Global),
                                                                                     Map.entry(RobotNames.BUILTIN_NAMESPACE.toLowerCase() + ".setlocalvariable", VariableScope.Local),
                                                                                     Map.entry(RobotNames.BUILTIN_NAMESPACE.toLowerCase() + ".settestvariable", VariableScope.TestCase),
                                                                                     Map.entry(RobotNames.BUILTIN_NAMESPACE.toLowerCase() + ".settaskvariable", VariableScope.TestCase),
                                                                                     Map.entry(RobotNames.BUILTIN_NAMESPACE.toLowerCase() + ".setsuitevariable", VariableScope.TestSuite),
                                                                                     Map.entry(RobotNames.BUILTIN_NAMESPACE.toLowerCase() + ".setvariableif", VariableScope.Local));

    public static boolean isVariableSetterKeyword(@NotNull String keywordName) {
        String normalizeKeywordName = KeywordUtil.normalizeKeywordName(keywordName);
        return VARIABLE_SETTERS.containsKey(normalizeKeywordName);
    }

    public static VariableScope getVariableSetterScope(@NotNull String keywordName) {
        String normalizeKeywordName = KeywordUtil.normalizeKeywordName(keywordName);
        return VARIABLE_SETTERS.getOrDefault(normalizeKeywordName, VariableScope.Local);
    }

    @Override
    public boolean isReadWriteAccessible(@NotNull PsiElement element) {
        return element instanceof RobotVariable || element instanceof RobotVariableDefinition;
    }

    @Override
    public boolean isDeclarationWriteAccess(@NotNull PsiElement element) {
        return element instanceof RobotVariableDefinition;
    }

    @NotNull
    @Override
    public Access getReferenceAccess(@NotNull PsiElement referencedElement, @NotNull PsiReference reference) {
        return getExpressionAccess(referencedElement);
    }

    @NotNull
    @Override
    public Access getExpressionAccess(@NotNull PsiElement expression) {
        return expression instanceof RobotVariableDefinition ? Access.Write : Access.Read;
    }
}
