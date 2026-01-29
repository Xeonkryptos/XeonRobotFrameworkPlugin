package dev.xeonkryptos.xeonrobotframeworkplugin.misc;

import com.intellij.codeInsight.highlighting.ReadWriteAccessDetector;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.VariableScope;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class RobotReadWriteAccessDetector extends ReadWriteAccessDetector {

    private static final Map<String, VariableScope> VARIABLE_SETTERS = Map.of("set variable",
                                                                              VariableScope.TestCase,
                                                                              "set global variable",
                                                                              VariableScope.Global,
                                                                              "set local variable",
                                                                              VariableScope.Local,
                                                                              "set test variable",
                                                                              VariableScope.TestCase,
                                                                              "set task variable",
                                                                              VariableScope.TestCase,
                                                                              "set suite variable",
                                                                              VariableScope.TestSuite,
                                                                              "set variable if",
                                                                              VariableScope.Local);

    public static boolean isVariableSetterKeyword(@NotNull String keywordName) {
        return VARIABLE_SETTERS.containsKey(keywordName.toLowerCase());
    }

    public static VariableScope getVariableSetterScope(@NotNull String keywordName) {
        return VARIABLE_SETTERS.getOrDefault(keywordName.toLowerCase(), VariableScope.Local);
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
