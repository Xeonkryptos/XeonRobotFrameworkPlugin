package com.github.jnhyperion.hyperrobotframeworkplugin.psi.ref;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordFile;
import com.intellij.psi.PsiElement;

import java.util.Collection;

public record PsiReferenceResultWithImportPath(PsiElement reference, Collection<KeywordFile> importFilePaths) {}
