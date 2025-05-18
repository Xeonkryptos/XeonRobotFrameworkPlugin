package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import dev.xeonkryptos.xeonrobotframeworkplugin.ide.icons.RobotIcons;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ImportType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.KeywordDto;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.PythonResolver;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.ResolverUtils;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.RobotFileManager;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.RobotPythonClass;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.RobotPythonFile;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyFile;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class HeadingImpl extends RobotPsiElementBase implements Heading {

    private static final String ROBOT_BUILT_IN = "robot.libraries.BuiltIn";

    private static final String WITH_NAME = "WITH NAME";
    private static final String AS = "AS";

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
    }

    @Override
    public final boolean isSettings() {
        return getPresentableText().startsWith("*** Setting");
    }

    @Override
    public boolean containsVariables() {
        return getPresentableText().startsWith("*** Variable");
    }

    @Override
    public final boolean containsTestCases() {
        return getPresentableText().startsWith("*** Test Case");
    }

    @Override
    public final boolean containsTasks() {
        return getPresentableText().equals("*** Tasks ***");
    }

    @Override
    public final boolean containsKeywordDefinitions() {
        String text = getPresentableText();
        return text.startsWith("*** Keyword") || text.startsWith("*** User Keyword");
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();
        PsiFile file = getContainingFile();
        if (file instanceof RobotFile robotFile) {
            robotFile.reset();
            robotFile.importsChanged();
        }
        importsChanged();
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
    }

    @NotNull
    @Override
    public final Collection<DefinedVariable> getDefinedVariables() {
        Collection<DefinedVariable> results = declaredVariables;
        if (results == null) {
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
                        if (setting.getText().equalsIgnoreCase("Suite Setup")
                            || setting.getText().equalsIgnoreCase("Test Setup") && i + 1 < children.length && children[i + 1] instanceof KeywordStatement) {
                            List<DefinedVariable> resolvedVariablesInKeywords = ResolverUtils.walkKeyword((KeywordStatement) children[i + 1]);
                            results.addAll(resolvedVariablesInKeywords);
                        }
                    }
                }
            }
            declaredVariables = results;
        }
        return results;
    }

    @NotNull
    @Override
    public final Collection<DefinedKeyword> collectDefinedKeywords() {
        Collection<DefinedKeyword> results = definedKeywords;
        if (results == null) {
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
            definedKeywords = results;
        }
        return results;
    }

    @NotNull
    @Override
    public final Collection<KeywordDefinition> getTestCases() {
        Collection<KeywordDefinition> results = testCases;
        if (results == null) {
            if (!containsTestCases() && !containsTasks()) {
                results = Collections.emptySet();
            } else {
                List<KeywordDefinition> keywordDefinitions = PsiTreeUtil.getChildrenOfTypeAsList(this, KeywordDefinition.class);
                results = new LinkedHashSet<>(keywordDefinitions);
            }
            testCases = results;
        }
        return results;
    }

    @NotNull
    @Override
    public final Collection<PsiFile> getFilesFromInvokedKeywordsAndVariables() {
        if (referencedFiles == null) {
            Collection<PsiFile> results = new LinkedHashSet<>();
            for (KeywordInvokable invokedKeyword : getInvokedKeywords()) {
                Optional.ofNullable(invokedKeyword.getReference()).map(PsiReference::resolve).map(PsiElement::getContainingFile).ifPresent(results::add);
                addReferencedArguments(results, invokedKeyword);
            }

            Collection<Variable> variables = getUsedVariables();
            for (Variable variable : variables) {
                PsiElement resolvedElement = variable.getReference().resolve();
                Optional.ofNullable(resolvedElement).map(PsiElement::getContainingFile).ifPresent(results::add);
            }
            referencedFiles = results;
        }
        return referencedFiles;
    }

    private Collection<Variable> getUsedVariables() {
        Collection<Variable> results = this.usedVariables;
        if (usedVariables == null) {
            results = PsiTreeUtil.findChildrenOfType(this, Variable.class);
            usedVariables = results;
        }
        return results;
    }

    private static void addReferencedArguments(@NotNull Collection<PsiFile> results, @NotNull KeywordInvokable keyword) {
        for (PositionalArgument positionalArgument : keyword.getPositionalArguments()) {
            PsiElement resolvedElement = positionalArgument.getReference().resolve();
            Optional.ofNullable(resolvedElement).map(PsiElement::getContainingFile).ifPresent(results::add);
        }
    }

    @NotNull
    private Collection<KeywordInvokable> getInvokedKeywords() {
        Collection<KeywordInvokable> results = invokedKeywords;
        if (invokedKeywords == null) {
            results = PsiTreeUtil.findChildrenOfType(this, KeywordInvokable.class);
            invokedKeywords = results;
        }
        return results;
    }

    @NotNull
    @Override
    public final Collection<VariableDefinition> getVariableDefinitions() {
        Collection<VariableDefinition> variableDefinitions = this.variableDefinitions;
        if (variableDefinitions == null) {
            if (containsVariables()) {
                variableDefinitions = PsiTreeUtil.findChildrenOfType(this, VariableDefinition.class);
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
        Collection<RobotStatement> statement = metadataStatements;
        if (metadataStatements == null) {
            statement = new LinkedHashSet<>(PsiTreeUtil.findChildrenOfAnyType(this, Import.class, Setting.class));
            metadataStatements = statement;
        }
        return statement;
    }

    @NotNull
    @Override
    public final Collection<KeywordFile> collectImportFiles() {
        if (!isValid()) {
            return List.of();
        }
        Collection<KeywordFile> files = keywordFiles;
        if (keywordFiles == null) {
            files = new LinkedHashSet<>();
            addBuiltInImports(files);
            if (isSettings()) {
                for (Import importElement : PsiTreeUtil.findChildrenOfType(this, Import.class)) {
                    PositionalArgument positionalArgument = PsiTreeUtil.findChildOfType(importElement, PositionalArgument.class);
                    if (positionalArgument != null) {
                        if (importElement.isResource()) {
                            PsiElement resolution = positionalArgument.getReference().resolve();
                            if (resolution instanceof KeywordFile keywordFile) {
                                files.add(keywordFile);
                            }
                        } else if (importElement.isLibrary() || importElement.isVariables()) {
                            PsiElement resolved = positionalArgument.getReference().resolve();
                            if (resolved instanceof PyClass pyClass) {
                                String namespace = getNamespace(importElement, positionalArgument);
                                boolean isDifferentNamespace = !positionalArgument.getContent().equals(namespace);
                                files.add(new RobotPythonClass(namespace, pyClass, importElement.getImportType(), isDifferentNamespace));
                            } else if (resolved instanceof PyFile file) {
                                String namespace = getNamespace(importElement, positionalArgument);
                                boolean isDifferentNamespace = !positionalArgument.getContent().equals(namespace);
                                files.add(new RobotPythonFile(namespace, file, importElement.getImportType(), isDifferentNamespace));
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

            keywordFiles = files;
        }
        return files;
    }

    private void addBuiltInImports(@NotNull Collection<KeywordFile> files) {
        PyClass builtIn = PythonResolver.findClass(ROBOT_BUILT_IN, getProject());
        if (builtIn != null) {
            files.add(new RobotPythonClass(ROBOT_BUILT_IN, builtIn, ImportType.LIBRARY, false));
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
            } else if (secondStatement instanceof KeywordInvokable invokable && AS.equals(invokable.getName())
                       && firstStatement instanceof VariableDefinition variable) {
                results = variable.getName();
            }
        }

        return results;
    }

    @NotNull
    public Icon getIcon(int flags) {
        return RobotIcons.MODELS;
    }
}
