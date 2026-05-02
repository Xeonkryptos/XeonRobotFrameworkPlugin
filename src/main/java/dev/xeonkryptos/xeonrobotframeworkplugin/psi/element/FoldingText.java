package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.psi.PsiElement;

import java.util.Collection;

public record FoldingText(String foldingText, Collection<PsiElement> dependants) {}
