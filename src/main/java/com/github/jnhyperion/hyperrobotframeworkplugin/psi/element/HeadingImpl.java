package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.github.jnhyperion.hyperrobotframeworkplugin.ide.icons.RobotIcons;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.dto.ImportType;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.dto.KeywordDto;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.ref.PythonResolver;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.ref.ResolverUtils;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.ref.RobotFileManager;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.ref.RobotPythonClass;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.ref.RobotPythonFile;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
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
import java.util.stream.Collectors;

public class HeadingImpl extends RobotPsiElementBase implements Heading {

    private static final String ROBOT_BUILT_IN = "robot.libraries.BuiltIn";

    private static final String WITH_NAME = "WITH NAME";
    private static final String AS = "AS";

    private String headerText;

    private Collection<KeywordInvokable> invokedKeywords;
    private Collection<Variable> usedVariables;
    private Collection<DefinedKeyword> definedKeywords;
    private Collection<KeywordDefinition> testCases;
    private Collection<KeywordFile> keywordFiles;
    private Collection<PsiFile> referencedFiles;
    private Collection<DefinedVariable> declaredVariables;
    private Collection<VariableDefinition> variableDefinitions;
    private Collection<RobotStatement> metadataStatements;

    public HeadingImpl(@NotNull ASTNode node) {
        super(node);

        headerText = getPresentableText();
    }

    @Override
    public final boolean isSettings() {
        return headerText.startsWith("*** Setting");
    }

    private boolean containsVariables() {
        return headerText.startsWith("*** Variable");
    }

    @Override
    public final boolean containsTestCases() {
        return headerText.startsWith("*** Test Case");
    }

    @Override
    public final boolean containsTasks() {
        return headerText.equals("*** Tasks ***");
    }

    @Override
    public final boolean containsKeywordDefinitions() {
        String text = headerText;
        return text.startsWith("*** Keyword") || text.startsWith("*** User Keyword");
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();
        if (isSettings()) {
            PsiFile file = getContainingFile();
            if (file instanceof RobotFile robotFile) {
                robotFile.reset();
                robotFile.importsChanged();
            }
        }
        this.importsChanged();
    }

    @Override
    public final void importsChanged() {
        this.definedKeywords = null;
        this.testCases = null;
        this.keywordFiles = null;
        this.invokedKeywords = null;
        this.usedVariables = null;
        this.referencedFiles = null;
        this.declaredVariables = null;
        this.metadataStatements = null;
        this.variableDefinitions = null;
        headerText = getPresentableText();
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
                        if (child instanceof DefinedVariable definedVariable) {
                            results.add(definedVariable);
                        }
                    }
                } else if (isSettings()) {
                    PsiElement[] children = getChildren();
                    for (int i = 0; i < children.length; i++) {
                        PsiElement child = children[i];
                        if (child instanceof Setting setting) {
                            if (setting.getText().equalsIgnoreCase("Suite Setup") ||
                                setting.getText().equalsIgnoreCase("Test Setup") && i + 1 < children.length && children[i + 1] instanceof KeywordStatement) {
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
                if (containsKeywordDefinitions()) {
                    results = PsiTreeUtil.getChildrenOfTypeAsList(this, KeywordDefinition.class).stream().map(keywordDefinition -> {
                        Collection<DefinedParameter> arguments = PsiTreeUtil.getChildrenOfTypeAsList(keywordDefinition, BracketSetting.class)
                                                                            .stream()
                                                                            .filter(BracketSetting::isArguments)
                                                                            .map(BracketSetting::getArguments)
                                                                            .flatMap(Collection::stream)
                                                                            .collect(Collectors.toCollection(LinkedHashSet::new));
                        return new KeywordDto(keywordDefinition, keywordDefinition.getKeywordName(), arguments);
                    }).collect(Collectors.toCollection(LinkedHashSet::new));
                } else {
                    results = Collections.emptySet();
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
    public final Collection<KeywordDefinition> getTestCases() {
        Collection<KeywordDefinition> results = this.testCases;
        if (results == null) {
            try {
                if (!containsTestCases() && !containsTasks()) {
                    results = Collections.emptySet();
                } else {
                    List<KeywordDefinition> keywordDefinitions = PsiTreeUtil.getChildrenOfTypeAsList(this, KeywordDefinition.class);
                    results = new LinkedHashSet<>(keywordDefinitions);
                }
            } catch (Throwable t) {
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
        for (PositionalArgument positionalArgument : keyword.getPositionalArguments()) {
            PsiReference reference = positionalArgument.getReference();
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
                        PositionalArgument positionalArgument = PsiTreeUtil.findChildOfType(importElement, PositionalArgument.class);
                        if (positionalArgument != null) {
                            if (importElement.isResource()) {
                                PsiElement resolution = resolveImport(positionalArgument);
                                if (resolution instanceof KeywordFile keywordFile) {
                                    files.add(keywordFile);
                                }
                            } else if (importElement.isLibrary() || importElement.isVariables()) {
                                PsiElement resolved = resolveImport(positionalArgument);
                                PyClass resolution = PythonResolver.castClass(resolved);
                                if (resolution != null) {
                                    String namespace = getNamespace(importElement, positionalArgument);
                                    boolean isDifferentNamespace = !positionalArgument.getContent().equals(namespace);
                                    files.add(new RobotPythonClass(namespace,
                                                                   resolution,
                                                                   ImportType.getType(importElement.getPresentableText()),
                                                                   this.getProject(),
                                                                   isDifferentNamespace));
                                }
                                PyFile file = PythonResolver.castFile(resolved);
                                if (file != null) {
                                    String namespace = getNamespace(importElement, positionalArgument);
                                    boolean isDifferentNamespace = !positionalArgument.getContent().equals(namespace);
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
     * @return the namespace of the import.
     */
    private static String getNamespace(Import imp, PositionalArgument library) {
        List<RobotStatement> args = PsiTreeUtil.getChildrenOfAnyType(imp, RobotStatement.class);
        String results = library.getContent();
        if (!args.isEmpty()) {
            results = args.getFirst().getPresentableText();
        }

        if (args.size() >= 3) {
            RobotStatement firstStatement = args.getLast();
            RobotStatement secondStatement = args.get(args.size() - 2);
            if (secondStatement instanceof PositionalArgument argument && WITH_NAME.equals(argument.getContent())) {
                results = firstStatement.getPresentableText();
            } else if (secondStatement instanceof KeywordInvokable invokable &&
                       AS.equals(invokable.getName()) &&
                       firstStatement instanceof VariableDefinition variable) {
                results = variable.getName();
            }
        }

        return results;
    }

    @Nullable
    private static PsiElement resolveImport(@NotNull PositionalArgument positionalArgument) {
        PsiReference reference = positionalArgument.getReference();
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
