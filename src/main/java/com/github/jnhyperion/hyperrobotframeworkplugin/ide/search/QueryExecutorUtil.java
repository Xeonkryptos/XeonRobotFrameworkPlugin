package com.github.jnhyperion.hyperrobotframeworkplugin.ide.search;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.SearchScope;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

final class QueryExecutorUtil {

    private QueryExecutorUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    static GlobalSearchScope convertToGlobalSearchScope(@NotNull SearchScope scope, @NotNull Project project) {
        if (scope instanceof GlobalSearchScope) {
            return (GlobalSearchScope) scope;
        } else if (scope instanceof LocalSearchScope localScope) {
            // If the local scope is empty, return empty global scope
            if (localScope == LocalSearchScope.EMPTY) {
                return GlobalSearchScope.EMPTY_SCOPE;
            }

            // Convert LocalSearchScope to GlobalSearchScope
            // This creates a global scope that contains all files in the local scope
            return GlobalSearchScope.filesScope(project,
                                                Arrays.stream(localScope.getScope())
                                                      .map(PsiElement::getContainingFile)
                                                      .filter(Objects::nonNull)
                                                      .map(PsiFile::getVirtualFile)
                                                      .filter(Objects::nonNull)
                                                      .collect(Collectors.toList()));
        }

        // Fallback to project scope if the type is unknown
        return GlobalSearchScope.projectScope(project);
    }
}
