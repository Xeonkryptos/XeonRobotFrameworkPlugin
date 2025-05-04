package com.github.jnhyperion.hyperrobotframeworkplugin.psi.ref;

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
        Variable variable = getElement();
        if (variable.isEmpty()) { // e.g. ${}, thus empty representation of a variable. There can be no reference.
            return null;
        }
        String variableName = variable.getPresentableText();
        PsiElement parentElement = variable.getParent();
        PsiElement resolvedElement = ResolverUtils.findVariableInKeyword(variableName, parentElement);
        if (resolvedElement == null) {
            PsiFile containingFile = variable.getContainingFile();
            resolvedElement = ResolverUtils.findVariableElement(variableName, containingFile);
        }
        return resolvedElement;
    }
}
