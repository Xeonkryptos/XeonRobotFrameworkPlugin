package com.github.jnhyperion.hyperrobotframeworkplugin.psi.ref;

import com.github.jnhyperion.hyperrobotframeworkplugin.ide.config.RobotOptionsProvider;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.Variable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReferenceBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotVariableReference extends PsiReferenceBase<Variable> {

    public RobotVariableReference(@NotNull Variable element) {
        super(element, false);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        Variable variable = this.getElement();
        String variableName = variable.getPresentableText();
        PsiElement parentElement = variable.getParent();

        PsiElement resolvedElement = ResolverUtils.findVariableInKeyword(
            variableName,
            parentElement,
            RobotOptionsProvider.getInstance(variable.getProject()).allowGlobalVariables()
        );

        if (resolvedElement != null) {
            return resolvedElement;
        } else {
            PsiFile containingFile = variable.getContainingFile();
            return ResolverUtils.findVariableElement(variableName, containingFile);
        }
    }

    @NotNull
    @Override
    public Object @NotNull [] getVariants() {
        return EMPTY_ARRAY;
    }
}
