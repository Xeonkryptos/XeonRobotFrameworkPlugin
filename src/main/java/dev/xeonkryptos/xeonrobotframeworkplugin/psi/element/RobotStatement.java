package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public interface RobotStatement extends PsiElement {

   @NotNull
   String getPresentableText();
}
