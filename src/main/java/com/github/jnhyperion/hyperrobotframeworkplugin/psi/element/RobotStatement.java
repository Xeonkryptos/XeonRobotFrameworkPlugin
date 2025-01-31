package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public interface RobotStatement extends PsiElement {

   @NotNull
   String getPresentableText();
}
