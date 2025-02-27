package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.github.jnhyperion.hyperrobotframeworkplugin.ide.icons.RobotIcons;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.dto.VariableDto;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.util.ReservedVariableScope;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class KeywordDefinitionImpl extends RobotPsiElementBase implements KeywordDefinition, PsiNameIdentifierOwner {

    private List<KeywordInvokable> invokedKeywords;
    private Collection<DefinedVariable> inlineVariables;
    private Collection<DefinedVariable> definedVariables;
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
        results.addAll(getDefinedVariables());
        results.addAll(getInlineVariables());
        Collection<DefinedVariable> localTestCaseVariables = this.testCaseVariables;
        if (this.testCaseVariables == null) {
            localTestCaseVariables = getTestCaseVariables();
            this.testCaseVariables = localTestCaseVariables;
        }
        results.addAll(localTestCaseVariables);
        return results;
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
    public final Collection<DefinedVariable> getDefinedVariables() {
        Collection<DefinedVariable> results = this.definedVariables;
        if (this.definedVariables == null) {
            results = collectDefinedVariables();
            this.definedVariables = results;
        }
        return results;
    }

    @NotNull
    private Collection<DefinedVariable> collectDefinedVariables() {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, BracketSetting.class)
                          .stream()
                          .filter(BracketSetting::isArguments)
                          .flatMap(bracketSetting -> PsiTreeUtil.getChildrenOfTypeAsList(bracketSetting, VariableDefinition.class).stream())
                          .collect(Collectors.toSet());
    }

    @NotNull
    private Collection<DefinedVariable> getTestCaseVariables() {
        Set<DefinedVariable> results = new LinkedHashSet<>();
        for (VariableDefinition variableDefinition : PsiTreeUtil.getChildrenOfTypeAsList(this, VariableDefinition.class)) {
            for (VariableDefinitionId variableDefinitionId : PsiTreeUtil.getChildrenOfTypeAsList(variableDefinition, VariableDefinitionId.class)) {
                results.add(new VariableDto(variableDefinition, variableDefinitionId.getText(), ReservedVariableScope.TestCase));
            }
        }
        return results;
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();
        this.definedVariables = null;
        this.inlineVariables = null;
        this.testCaseVariables = null;
        this.invokedKeywords = null;
    }

    @Override
    public final String getKeywordName() {
        return this.getPresentableText();
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return PsiTreeUtil.findChildOfType(this, KeywordDefinitionId.class);
    }

    @NotNull
    @Override
    public Icon getIcon(int flags) {
        Heading heading = PsiTreeUtil.getParentOfType(this, Heading.class);
        if (heading != null && heading.containsTestCases()) {
            return RobotIcons.JUNIT;
        } else {
            return RobotIcons.FUNCTION;
        }
    }
}
