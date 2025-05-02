package com.github.jnhyperion.hyperrobotframeworkplugin.psi.ref;

import com.github.jnhyperion.hyperrobotframeworkplugin.ide.config.RobotOptionsProvider;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.dto.ImportType;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.dto.KeywordFileWithDependentsWrapper;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.DefinedKeyword;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.DefinedVariable;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordDefinition;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordFile;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordInvokable;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordStatement;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.RobotFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ResolverUtils {

    private ResolverUtils() {
    }

    public static PsiReferenceResultWithImportPath findKeywordReference(@Nullable String keyword, @Nullable PsiFile psiFile) {
        if (keyword == null || !(psiFile instanceof RobotFile robotFile)) {
            return null;
        }

        for (DefinedKeyword definedKeyword : robotFile.getDefinedKeywords()) {
            if (definedKeyword.matches(keyword)) {
                return new PsiReferenceResultWithImportPath(definedKeyword.reference(), Collections.emptyList());
            }
        }

        boolean includeTransitive = RobotOptionsProvider.getInstance(psiFile.getProject()).allowTransitiveImports();
        Collection<KeywordFileWithDependentsWrapper> importedFilesWithDependents = robotFile.getImportedFilesWithDependents(includeTransitive);
        for (KeywordFileWithDependentsWrapper wrapper : importedFilesWithDependents) {
            for (DefinedKeyword definedKeyword : wrapper.keywordFile().getDefinedKeywords()) {
                if (definedKeyword.matches(keyword)) {
                    PsiElement reference = definedKeyword.reference();
                    Collection<KeywordFile> dependents = wrapper.dependents();
                    return new PsiReferenceResultWithImportPath(reference, dependents);
                }
            }
        }

        return null;
    }

    @Nullable
    public static PsiElement findVariableElement(@Nullable String variableName, @Nullable PsiFile psiFile) {
        if (variableName == null || !(psiFile instanceof RobotFile robotFile)) {
            return null;
        }
        for (DefinedVariable definedVariable : robotFile.getDefinedVariables()) {
            if (definedVariable.matches(variableName)) {
                return definedVariable.reference();
            }
        }

        boolean includeTransitive = RobotOptionsProvider.getInstance(psiFile.getProject()).allowTransitiveImports();
        for (KeywordFile keywordFile : robotFile.getImportedFiles(includeTransitive)) {
            if (keywordFile.getImportType() != ImportType.LIBRARY) {
                for (DefinedVariable definedVariable : keywordFile.getDefinedVariables()) {
                    if (definedVariable.matches(variableName)) {
                        return definedVariable.reference();
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    public static PsiElement findVariableInKeyword(@Nullable String variableName, @Nullable PsiElement element, boolean searchInKeywordStatements) {
        if (variableName == null || element == null) {
            return null;
        }
        KeywordDefinition keywordDefinition = PsiTreeUtil.getParentOfType(element, KeywordDefinition.class);
        if (keywordDefinition != null) {
            PsiElement[] children = keywordDefinition.getChildren();
            boolean foundElement = false;
            for (int i = children.length - 1; i >= 0; i--) {
                PsiElement child = children[i];
                if (child == element) {
                    foundElement = true;
                } else if (foundElement) {
                    if (child instanceof DefinedVariable definedVariable && definedVariable.matches(variableName)) {
                        return child;
                    } else if (child instanceof KeywordStatement keywordStatement) {
                        PsiElement result = searchInKeywordStatements ? findVariableInStatement(keywordStatement, variableName) : null;
                        if (result != null) {
                            return result;
                        }
                    }
                }
            }
            for (DefinedVariable definedVariable : keywordDefinition.getDeclaredVariables()) {
                if (definedVariable.matches(variableName)) {
                    return definedVariable.reference();
                }
            }
        }
        return null;
    }

    @Nullable
    private static PsiElement findVariableInStatement(@Nullable KeywordStatement keywordStatement, String variableName) {
        if (keywordStatement == null) {
            return null;
        }

        DefinedVariable definedVariable = keywordStatement.getGlobalVariable();
        if (definedVariable != null && definedVariable.matches(variableName)) {
            return definedVariable.reference();
        }

        KeywordInvokable keywordInvokable = keywordStatement.getInvokable();
        if (keywordInvokable != null) {
            PsiReference reference = keywordInvokable.getReference();
            if (reference != null) {
                PsiElement resolvedElement = reference.resolve();
                if (resolvedElement instanceof KeywordDefinition) {
                    List<KeywordInvokable> invokedKeywords = ((KeywordDefinition) resolvedElement).getInvokedKeywords();
                    Collections.reverse(invokedKeywords);
                    for (KeywordInvokable invokedKeyword : invokedKeywords) {
                        PsiElement parent = invokedKeyword.getParent();
                        if (parent instanceof KeywordStatement) {
                            PsiElement result = findVariableInStatement((KeywordStatement) parent, variableName);
                            if (result != null) {
                                return result;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    @NotNull
    public static List<DefinedVariable> walkKeyword(@Nullable KeywordStatement keywordStatement) {
        if (keywordStatement == null) {
            return Collections.emptyList();
        }

        // set test variable  ${x}  ${y}
        DefinedVariable globalVariable = keywordStatement.getGlobalVariable();
        if (globalVariable != null) {
            return Collections.singletonList(globalVariable);
        }

        List<DefinedVariable> variables = new ArrayList<>();
        KeywordInvokable invokable = keywordStatement.getInvokable();
        if (invokable != null) {
            PsiReference reference = invokable.getReference();
            if (reference != null) {
                PsiElement resolvedElement = reference.resolve();
                if (resolvedElement instanceof KeywordDefinition) {
                    for (KeywordInvokable invokedKeyword : ((KeywordDefinition) resolvedElement).getInvokedKeywords()) {
                        PsiElement parent = invokedKeyword.getParent();
                        if (parent instanceof KeywordStatement) {
                            List<DefinedVariable> results = walkKeyword((KeywordStatement) parent);
                            variables.addAll(results);
                        }
                    }
                }
            }
        }
        return variables;
    }
}
