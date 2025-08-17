package dev.xeonkryptos.xeonrobotframeworkplugin.ide.misc;

import com.intellij.codeInsight.highlighting.ReadWriteAccessDetector;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

public class RobotReadWriteAccessDetector extends ReadWriteAccessDetector {

    private static final Collection<String> VARIABLE_SETTERS = Set.of("set variable",
                                                                      "set global variable",
                                                                      "set local variable",
                                                                      "set test variable",
                                                                      "set task variable",
                                                                      "set suite variable",
                                                                      "set variable if");

    public static boolean isVariableSetterKeyword(@NotNull String keywordName) {
        return VARIABLE_SETTERS.contains(keywordName.toLowerCase());
    }

    @Override
    public boolean isReadWriteAccessible(@NotNull PsiElement element) {
        return element instanceof RobotVariable || element instanceof RobotVariableDefinition;
    }

    @Override
    public boolean isDeclarationWriteAccess(@NotNull PsiElement element) {
        return element instanceof RobotVariable && element.getParent() instanceof RobotVariableDefinition || element instanceof RobotVariableDefinition;
    }

    @NotNull
    @Override
    public Access getReferenceAccess(@NotNull PsiElement referencedElement, @NotNull PsiReference reference) {
        PsiElement parentElement = PsiTreeUtil.getParentOfType(referencedElement, RobotVariable.class, RobotVariableDefinition.class);
        if (parentElement instanceof RobotVariable && parentElement.getParent() instanceof RobotVariableDefinition
            || parentElement instanceof RobotVariableDefinition) {
            return Access.Write;
        }
        return Access.Read;
    }

    @NotNull
    @Override
    public Access getExpressionAccess(@NotNull PsiElement expression) {
        if (expression instanceof RobotVariableDefinition) {
            return Access.Write;
        }

        if (expression instanceof RobotVariable) {
            if (expression.getParent() instanceof RobotVariableDefinition) {
                return Access.Write;
            }
            return Access.Read;
        }

        return Access.Read;
    }
}
