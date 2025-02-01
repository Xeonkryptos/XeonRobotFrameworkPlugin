package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.github.jnhyperion.hyperrobotframeworkplugin.ide.icons.RobotIcons;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.dto.VariableDto;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.util.ReservedVariableScope;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class KeywordDefinitionImpl extends RobotPsiElementBase implements DefinedKeyword, KeywordDefinition, PsiNameIdentifierOwner {

    private static final Pattern PATTERN = Pattern.compile("(.*?)(\\$\\{.*?})(.*)");
    private static final String ANY = ".*?";
    private static final String DOT = ".";

    private Pattern pattern;
    private List<KeywordInvokable> invokedKeywords;
    private Collection<DefinedVariable> inlineVariables;
    private Collection<DefinedVariable> definedArguments;
    private Collection<DefinedVariable> testCaseVariables;

    public KeywordDefinitionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public final List<KeywordInvokable> getInvokedKeywords() {
        List<KeywordInvokable> results = this.invokedKeywords;
        if (this.invokedKeywords == null) {
            results = new ArrayList<>();
            for (PsiElement statement : getChildren()) {
                if (statement instanceof KeywordStatement || statement instanceof BracketSetting) {
                    results.addAll(PsiTreeUtil.collectElementsOfType(statement, KeywordInvokable.class));
                }
            }
            this.invokedKeywords = results;
        }
        return results;
    }

    @NotNull
    @Override
    public final Collection<DefinedVariable> getDeclaredVariables() {
        Set<DefinedVariable> results = new LinkedHashSet<>();
        results.addAll(getDefinedArguments());
        results.addAll(getInlineVariables());
        Collection<DefinedVariable> localTestCaseVariables = this.testCaseVariables;
        if (this.testCaseVariables == null) {
            localTestCaseVariables = getTestCaseVariables();
            this.testCaseVariables = localTestCaseVariables;
        }
        results.addAll(localTestCaseVariables);
        return results;
    }

    @Override
    public final boolean hasInlineVariables() {
        return !getInlineVariables().isEmpty();
    }

    @NotNull
    private Collection<DefinedVariable> getInlineVariables() {
        Collection<DefinedVariable> results = this.inlineVariables;
        if (this.inlineVariables == null) {
            results = this.collectInlineVariables();
            this.inlineVariables = results;
        }
        return results;
    }

    @NotNull
    private Collection<DefinedVariable> collectInlineVariables() {
        Set<DefinedVariable> results = new LinkedHashSet<>();
        for (PsiElement child : getChildren()) {
            if (child instanceof KeywordDefinitionId) {
                for (PsiElement keywordChild : child.getChildren()) {
                    if (keywordChild instanceof DefinedVariable) {
                        results.add((DefinedVariable) keywordChild);
                    }
                }
            }
        }
        return results;
    }

    @NotNull
    public final Collection<DefinedVariable> getDefinedArguments() {
        Collection<DefinedVariable> results = this.definedArguments;
        if (this.definedArguments == null) {
            results = collectDefinedArguments();
            this.definedArguments = results;
        }
        return results;
    }

    @NotNull
    private Collection<DefinedVariable> collectDefinedArguments() {
        return Arrays.stream(getChildren())
                     .filter(child -> child instanceof BracketSetting)
                     .map(child -> (BracketSetting) child)
                     .filter(BracketSetting::isArguments)
                     .flatMap(bracketSetting -> Arrays.stream(bracketSetting.getChildren()))
                     .filter(argument -> argument instanceof Argument)
                     .flatMap(argument -> Arrays.stream(argument.getChildren()))
                     .filter(argumentChild -> argumentChild instanceof DefinedVariable)
                     .map(argumentChild -> (DefinedVariable) argumentChild)
                     .collect(Collectors.toSet());
    }

    @NotNull
    private Collection<DefinedVariable> getTestCaseVariables() {
        Set<DefinedVariable> results = new LinkedHashSet<>();
        for (PsiElement child : getChildren()) {
            if (child instanceof VariableDefinition) {
                for (PsiElement element : child.getChildren()) {
                    if (element instanceof VariableDefinitionId) {
                        VariableDefinitionId id = (VariableDefinitionId) element;
                        results.add(new VariableDto(child, id.getText(), ReservedVariableScope.TestCase));
                    }
                }
            }
        }
        return results;
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();
        this.definedArguments = null;
        this.inlineVariables = null;
        this.testCaseVariables = null;
        this.pattern = null;
        this.invokedKeywords = null;
    }

    @Override
    public final boolean matches(String text) {
        if (text == null) {
            return false;
        } else {
            try {
                String myText = this.getPresentableText();
                Pattern namePattern = this.pattern;
                if (namePattern == null) {
                    PsiFile psiFile = getContainingFile();
                    String myNamespace = getNamespace(psiFile);
                    namePattern = Pattern.compile(buildPattern(myNamespace, myText.trim()), Pattern.CASE_INSENSITIVE);
                    this.pattern = namePattern;
                }

                return namePattern.matcher(text.trim()).matches();
            } catch (Throwable var5) {
                return false;
            }
        }
    }

    private String getNamespace(@NotNull PsiFile file) {
        VirtualFile virtualFile = file.getVirtualFile();
        if (virtualFile == null) {
            return null;
        }
        String name = virtualFile.getName();
        // remove the extension
        int index = name.lastIndexOf(DOT);
        if (index > 0) {
            name = name.substring(0, index);
        }
        return name;
    }

    @Override
    public final PsiElement reference() {
        return this;
    }

    private String buildPattern(String namespace, String text) {
        Matcher matcher = PATTERN.matcher(text);
        String result = "";
        if (matcher.matches()) {
            text = matcher.group(1);
            String end = this.buildPattern(null, matcher.group(3));
            if (!text.isEmpty()) {
                result = Pattern.quote(text);
            }

            result = result + ANY;
            if (!end.isEmpty()) {
                result = result + end;
            }
        } else {
            result = !text.isEmpty() ? Pattern.quote(text) : text;
        }
        if (namespace != null && !namespace.isEmpty()) {
            result = "(" + Pattern.quote(namespace + DOT) + ")?" + result;
        }
        return result;
    }

    @Override
    public final String getKeywordName() {
        return this.getPresentableText();
    }

    @Override
    public final boolean hasArguments() {
        return !this.getDefinedArguments().isEmpty();
    }

    @Nullable
    public PsiElement getNameIdentifier() {
        return PsiTreeUtil.findChildOfType(this, KeywordDefinitionId.class);
    }

    @NotNull
    public Icon getIcon(int flags) {
        Heading heading = PsiTreeUtil.getParentOfType(this, Heading.class);
        if (heading != null && heading.containsTestCases()) {
            return RobotIcons.JUNIT;
        } else {
            return RobotIcons.FUNCTION;
        }
    }
}
