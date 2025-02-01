package com.github.jnhyperion.hyperrobotframeworkplugin.psi.ref;

import com.github.jnhyperion.hyperrobotframeworkplugin.ide.config.RobotOptionsProvider;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.dto.ImportType;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.DefinedKeyword;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.DefinedVariable;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordDefinition;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordFile;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordInvokable;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordStatement;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.RobotFile;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.RobotFileImpl;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResolverUtils {
    private ResolverUtils() {
    }

    @Nullable
    public static PsiElement findKeywordElement(@Nullable String keyword, @Nullable PsiFile psiFile) {
        if (keyword == null || !(psiFile instanceof RobotFile)) {
            return null;
        }

        RobotFile robotFile = (RobotFile) psiFile;
        for (DefinedKeyword definedKeyword : robotFile.getDefinedKeywords()) {
            if (definedKeyword.matches(keyword)) {
                return definedKeyword.reference();
            }
        }

        boolean includeTransitive = RobotOptionsProvider.getInstance(psiFile.getProject()).allowTransitiveImports();
        for (KeywordFile keywordFile : robotFile.getImportedFiles(includeTransitive)) {
            for (DefinedKeyword definedKeyword : keywordFile.getDefinedKeywords()) {
                if (definedKeyword.matches(keyword)) {
                    return definedKeyword.reference();
                }
            }
        }

        return null;
    }

    @Nullable
    public static PsiElement findVariableElement(@Nullable String variableName, @Nullable PsiFile psiFile) {
        if (variableName == null || !(psiFile instanceof RobotFile)) {
            return null;
        }

        try {
            RobotFile robotFile = (RobotFile) psiFile;

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

            if (robotFile instanceof RobotFileImpl) {
                for (DefinedVariable definedVariable : robotFile.getDefinedVariables()) {
                    if (definedVariable.matches(variableName)) {
                        return definedVariable.reference();
                    }
                }
            }
        } catch (Throwable ignored) {
        }

        return null;
    }

    @Nullable
    public static PsiElement findVariableInKeyword(@Nullable String variableName, @Nullable PsiElement element, boolean searchInKeywordStatements) {
        if (variableName == null || element == null) {
            return null;
        }
        try {
            PsiElement currentElement = element;
            while ((currentElement = currentElement.getParent()) != null) {
                if (currentElement instanceof KeywordDefinition) {
                    KeywordDefinition keywordDefinition = (KeywordDefinition) currentElement;
                    PsiElement[] children = keywordDefinition.getChildren();
                    boolean foundElement = false;
                    for (int i = children.length - 1; i >= 0; i--) {
                        PsiElement child = children[i];
                        if (child == element) {
                            foundElement = true;
                        } else if (foundElement) {
                            if (child instanceof DefinedVariable && ((DefinedVariable) child).matches(variableName)) {
                                return child;
                            } else if (child instanceof KeywordStatement) {
                                PsiElement result = searchInKeywordStatements ? findVariableInStatement((KeywordStatement) child, variableName) : null;
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
                    break;
                }
            }
        } catch (Throwable ignored) {
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
        try {
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
        } catch (Throwable ignored) {
        }
        return Collections.emptyList();
    }
}
