package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.github.jnhyperion.hyperrobotframeworkplugin.ide.icons.RobotIcons;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.dto.ImportType;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.dto.VariableDto;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.ref.PythonResolver;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.ref.ResolverUtils;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.ref.RobotFileManager;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.ref.RobotPythonClass;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.ref.RobotPythonFile;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.util.ReservedVariableScope;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.MultiMap;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

public class HeadingImpl extends RobotPsiElementBase implements Heading {

    private static final String ROBOT_BUILT_IN = "robot.libraries.BuiltIn";

    private static final String WITH_NAME = "WITH NAME";
    private static final String AS = "AS";

    private Collection<KeywordInvokable> invokedKeywords;
    private MultiMap<String, KeywordInvokable> invokableReferences;
    private Collection<Variable> usedVariables;
    private Collection<DefinedKeyword> definedKeywords;
    private Collection<DefinedKeyword> testCases;
    private Collection<KeywordFile> keywordFiles;
    private Collection<PsiFile> referencedFiles;
    private Collection<DefinedVariable> declaredVariables;
    private Collection<VariableDefinition> variableDefinitions;
    private Collection<RobotStatement> metadataStatements;

    public HeadingImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public final boolean isSettings() {
        return this.getPresentableText().startsWith("*** Setting");
    }

    private boolean containsVariables() {
        return this.getPresentableText().startsWith("*** Variable");
    }

    @Override
    public final boolean containsTestCases() {
        return this.getPresentableText().startsWith("*** Test Case");
    }

    @Override
    public final boolean containsTasks() {
        return this.getPresentableText().equals("*** Tasks ***");
    }

    @Override
    public final boolean containsKeywordDefinitions() {
        String text = this.getPresentableText();
        return text.startsWith("*** Keyword") || text.startsWith("*** User Keyword");
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();
        try {
            if (isSettings()) {
                PsiFile file = getContainingFile();
                if (file instanceof RobotFile robotFile) {
                    robotFile.reset();
                    robotFile.importsChanged();
                }
            }
        } catch (Throwable t) {
            // ignored
        }
        this.importsChanged();
    }

    @Override
    public final void importsChanged() {
        this.definedKeywords = null;
        this.testCases = null;
        this.keywordFiles = null;
        this.invokedKeywords = null;
        this.invokableReferences = null;
        this.usedVariables = null;
        this.referencedFiles = null;
        this.declaredVariables = null;
        this.metadataStatements = null;
        this.variableDefinitions = null;
    }

    @NotNull
    @Override
    public final Collection<DefinedVariable> getDefinedVariables() {
        Collection<DefinedVariable> results = this.declaredVariables;
        if (results == null) {
            try {
                results = new LinkedHashSet<>(RobotFileManager.getGlobalVariables(getProject()));
                if (containsVariables()) {
                    for (PsiElement child : getChildren()) {
                        if (child instanceof DefinedVariable) {
                            if (child instanceof VariableDefinitionImpl) {
                                results.add(new VariableDto(child, ((VariableDefinitionImpl) child).getName(), ReservedVariableScope.Global));
                            } else {
                                results.add((DefinedVariable) child);
                            }
                            results.add((DefinedVariable) child);
                        }
                    }
                } else if (isSettings()) {
                    PsiElement[] children = getChildren();
                    for (int i = 0; i < children.length; i++) {
                        PsiElement child = children[i];
                        if (child instanceof Setting setting) {
                            if (setting.getText().equalsIgnoreCase("Suite Setup")
                                || setting.getText().equalsIgnoreCase("Test Setup") && i + 1 < children.length && children[i + 1] instanceof KeywordStatement) {
                                List<DefinedVariable> resolvedVariablesInKeywords = ResolverUtils.walkKeyword((KeywordStatement) children[i + 1]);
                                results.addAll(resolvedVariablesInKeywords);
                            }
                        }
                    }
                }
            } catch (Throwable t) {
                return Collections.emptySet();
            }
            this.declaredVariables = results;
        }
        return results;
    }

    @NotNull
    @Override
    public final Collection<DefinedKeyword> collectDefinedKeywords() {
        Collection<DefinedKeyword> results = this.definedKeywords;
        if (results == null) {
            try {
                if (!this.containsKeywordDefinitions()) {
                    results = Collections.emptySet();
                } else {
                    results = new LinkedHashSet<>();
                    for (PsiElement child : getChildren()) {
                        if (child instanceof DefinedKeyword) {
                            results.add((DefinedKeyword) child);
                        }
                    }
                }
            } catch (Throwable e) {
                return Collections.emptySet();
            }
            this.definedKeywords = results;
        }
        return results;
    }

    @NotNull
    @Override
    public final Collection<DefinedKeyword> getTestCases() {
        Collection<DefinedKeyword> results = this.testCases;
        if (results == null) {
            try {
                if (!this.containsTestCases() && !this.containsTasks()) {
                    results = Collections.emptySet();
                } else {
                    results = new LinkedHashSet<>();
                    for (PsiElement child : getChildren()) {
                        if (child instanceof DefinedKeyword) {
                            results.add((DefinedKeyword) child);
                        }
                    }
                }
            } catch (Throwable var6) {
                return Collections.emptySet();
            }
            this.testCases = results;
        }
        return results;
    }

    @NotNull
    @Override
    public final Collection<PsiFile> getFilesFromInvokedKeywordsAndVariables() {
        Collection<PsiFile> results = this.referencedFiles;
        if (this.referencedFiles == null) {
            try {
                results = new HashSet<>();
                for (KeywordInvokable invokedKeyword : getInvokedKeywords()) {
                    PsiReference reference = invokedKeyword.getReference();
                    if (reference != null) {
                        PsiElement resolvedReference = reference.resolve();
                        if (resolvedReference != null) {
                            results.add(resolvedReference.getContainingFile());
                        }
                    }
                    addReferencedArguments(results, invokedKeyword);
                }

                Collection<Variable> variables = getUsedVariables();
                for (Variable variable : variables) {
                    PsiReference reference = variable.getReference();
                    if (reference != null) {
                        PsiElement element = reference.resolve();
                        if (element != null) {
                            results.add(element.getContainingFile());
                        }
                    }
                }
            } catch (Throwable var6) {
                return Collections.emptySet();
            }
            this.referencedFiles = results;
        }
        return results;
    }

    private Collection<Variable> getUsedVariables() {
        Collection<Variable> results = this.usedVariables;
        if (this.usedVariables == null) {
            results = PsiTreeUtil.findChildrenOfType(this, Variable.class);
            this.usedVariables = results;
        }
        return results;
    }

    private static void addReferencedArguments(@NotNull Collection<PsiFile> results, @NotNull KeywordInvokable keyword) {
        for (Argument argument : keyword.getArguments()) {
            PsiReference reference = argument.getReference();
            if (reference != null) {
                PsiElement resolvedReference = reference.resolve();
                if (resolvedReference != null) {
                    results.add(resolvedReference.getContainingFile());
                }
            }
        }
    }

    @NotNull
    private Collection<KeywordInvokable> getInvokedKeywords() {
        Collection<KeywordInvokable> results = this.invokedKeywords;
        if (this.invokedKeywords == null) {
            try {
                results = PsiTreeUtil.findChildrenOfType(this, KeywordInvokable.class);
            } catch (Throwable var2) {
                return Collections.emptySet();
            }
            this.invokedKeywords = results;
        }
        return results;
    }

    @NotNull
    @Override
    public final Collection<KeywordInvokable> getInvokableKeywords(@Nullable KeywordDefinition definition) {
        MultiMap<String, KeywordInvokable> results = this.invokableReferences;
        if (this.invokableReferences == null) {
            try {
                results = new MultiMap<>();
                for (KeywordInvokable invokedKeyword : getInvokedKeywords()) {
                    PsiReference reference = invokedKeyword.getReference();
                    if (reference != null) {
                        PsiElement resolvedReference = reference.resolve();
                        if (resolvedReference instanceof KeywordDefinition) {
                            results.putValue(((KeywordDefinition) resolvedReference).getPresentableText(), invokedKeyword);
                        }
                    }
                }
            } catch (Throwable t) {
                return Collections.emptySet();
            }
            this.invokableReferences = results;
        }
        return definition == null ? Collections.emptySet() : results.get(definition.getPresentableText());
    }

    @NotNull
    @Override
    public final Collection<VariableDefinition> getVariableDefinitions() {
        Collection<VariableDefinition> variableDefinitions = this.variableDefinitions;
        if (this.variableDefinitions == null) {
            if (this.containsVariables()) {
                try {
                    variableDefinitions = PsiTreeUtil.findChildrenOfType(this, VariableDefinition.class);
                } catch (Throwable t) {
                    return Collections.emptySet();
                }
            } else {
                variableDefinitions = Collections.emptySet();
            }
            this.variableDefinitions = variableDefinitions;
        }
        return variableDefinitions;
    }

    @NotNull
    @Override
    public final Collection<RobotStatement> getMetadataStatements() {
        Collection<RobotStatement> statement = this.metadataStatements;
        if (this.metadataStatements == null) {
            try {
                statement = new LinkedHashSet<>();
                statement.addAll(PsiTreeUtil.findChildrenOfType(this, Import.class));
                statement.addAll(PsiTreeUtil.findChildrenOfType(this, Setting.class));
            } catch (Throwable var2) {
                return Collections.emptySet();
            }
            this.metadataStatements = statement;
        }
        return statement;
    }

    @NotNull
    @Override
    public final Collection<KeywordFile> collectImportFiles() {
        Collection<KeywordFile> files = this.keywordFiles;
        if (this.keywordFiles == null) {
            try {
                files = new LinkedHashSet<>();
                addBuiltInImports(files);
                if (isSettings()) {
                    for (Import importElement : PsiTreeUtil.findChildrenOfType(this, Import.class)) {
                        Argument argument = PsiTreeUtil.findChildOfType(importElement, Argument.class);
                        if (argument != null) {
                            if (importElement.isResource()) {
                                PsiElement resolution = resolveImport(argument);
                                if (resolution instanceof KeywordFile) {
                                    files.add((KeywordFile) resolution);
                                }
                            } else if (importElement.isLibrary() || importElement.isVariables()) {
                                PsiElement resolved = resolveImport(argument);
                                PyClass resolution = PythonResolver.castClass(resolved);
                                if (resolution != null) {
                                    String namespace = getNamespace(importElement, argument);
                                    boolean isDifferentNamespace = !argument.getPresentableText().equals(namespace);
                                    files.add(new RobotPythonClass(namespace,
                                                                   resolution,
                                                                   ImportType.getType(importElement.getPresentableText()),
                                                                   this.getProject(),
                                                                   isDifferentNamespace));
                                }
                                PyFile file = PythonResolver.castFile(resolved);
                                if (file != null) {
                                    String namespace = getNamespace(importElement, argument);
                                    boolean isDifferentNamespace = !argument.getPresentableText().equals(namespace);
                                    files.add(new RobotPythonFile(namespace,
                                                                  file,
                                                                  ImportType.getType(importElement.getPresentableText()),
                                                                  this.getProject(),
                                                                  isDifferentNamespace));
                                }
                            }
                        }
                    }
                }
                if (files.isEmpty()) {
                    return files;
                }

                Collection<Import> imports = PsiTreeUtil.findChildrenOfType(this, Import.class);
                if (files.size() < imports.size() + 1) {
                    return files;
                }
            } catch (Throwable t) {
                return Collections.emptySet();
            }

            this.keywordFiles = files;
        }
        return files;
    }

    private void addBuiltInImports(@NotNull Collection<KeywordFile> files) {
        PyClass builtIn = PythonResolver.findClass(ROBOT_BUILT_IN, getProject());
        if (builtIn != null) {
            files.add(new RobotPythonClass(ROBOT_BUILT_IN, builtIn, ImportType.LIBRARY, this.getProject(), false));
        }
    }

    /**
     * Gets the namespace of the current import. This looks for the 'WITH NAME' or 'AS' tag else returns the first argument.
     *
     * @param imp     the import statement to get the namespace of.
     * @param library the first argument; aka the default namespace
     *
     * @return the namespace of the import.
     */
    private static String getNamespace(Import imp, Argument library) {
        List<RobotStatement> args = PsiTreeUtil.getChildrenOfAnyType(imp, RobotStatement.class);
        String results = library.getPresentableText();
        if (!args.isEmpty()) {
            results = args.get(0).getPresentableText();
        }

        if (args.size() >= 3) {
            RobotStatement firstStatement = args.get(args.size() - 1);
            RobotStatement secondStatement = args.get(args.size() - 2);
            if (secondStatement instanceof Argument && WITH_NAME.equals(secondStatement.getPresentableText())) {
                results = firstStatement.getPresentableText();
            } else if (secondStatement instanceof KeywordInvokable && AS.equals(secondStatement.getPresentableText())
                       && firstStatement instanceof VariableDefinition) {
                results = firstStatement.getPresentableText();
            }
        }

        return results;
    }

    @Nullable
    private static PsiElement resolveImport(@NotNull Argument argument) {
        PsiReference reference = argument.getReference();
        if (reference != null) {
            return reference.resolve();
        }
        return null;
    }

    @NotNull
    public Icon getIcon(int flags) {
        return RobotIcons.MODELS;
    }
}
