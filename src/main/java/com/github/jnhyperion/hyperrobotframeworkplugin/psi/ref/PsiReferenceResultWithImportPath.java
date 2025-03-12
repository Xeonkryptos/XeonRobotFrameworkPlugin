package com.github.jnhyperion.hyperrobotframeworkplugin.psi.ref;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordFile;
import com.intellij.psi.PsiElement;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

public record PsiReferenceResultWithImportPath(PsiElement reference, Collection<KeywordFile> importFilePaths) {

    public Object[] combineWithOtherDependents(Object... dependents) {
        return Stream.concat(Arrays.stream(dependents), importFilePaths.stream()).distinct().toArray(Object[]::new);
    }
}
