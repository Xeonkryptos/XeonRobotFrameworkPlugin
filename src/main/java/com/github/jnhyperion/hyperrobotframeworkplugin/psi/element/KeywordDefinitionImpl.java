package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.github.jnhyperion.hyperrobotframeworkplugin.ide.icons.RobotIcons;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.dto.VariableDto;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.stub.element.KeywordDefinitionStub;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.util.ReservedVariableScope;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.stubs.IStubElementType;
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

public class KeywordDefinitionImpl extends RobotStubPsiElementBase<KeywordDefinitionStub, KeywordDefinition> implements KeywordDefinition {

    private Collection<DefinedParameter> parameters;
    private List<KeywordInvokable> invokedKeywords;
    private Collection<DefinedVariable> inlineVariables;
    private Collection<DefinedVariable> definedVariables;
    private Collection<DefinedVariable> testCaseVariables;

    public KeywordDefinitionImpl(@NotNull ASTNode node) {
        super(node);
    }

    public KeywordDefinitionImpl(@NotNull KeywordDefinitionStub stub, @NotNull IStubElementType<KeywordDefinitionStub, KeywordDefinition> nodeType) {
        super(stub, nodeType);
    }

    @Nullable
    @Override
    public String getName() {
        KeywordDefinitionStub stub = getStub();
        if (stub != null) {
            return stub.getName();
        }
        PsiElement nameIdentifier = getNameIdentifier();
        if (nameIdentifier != null) {
            return nameIdentifier.getText();
        }
        return null;
    }

    @Override
    public Collection<DefinedParameter> getParameters() {
        Collection<DefinedParameter> results = this.parameters;
        if (results == null) {
            for (BracketSetting bracketSetting : PsiTreeUtil.getChildrenOfTypeAsList(this, BracketSetting.class)) {
                if (bracketSetting.isArguments()) {
                    results = bracketSetting.getArguments();
                    break;
                }
            }
            this.parameters = results != null ? results : List.of();
        }
        return results;
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
            PsiNamedElement identifyingElement = (PsiNamedElement) variableDefinition.getIdentifyingElement();
            if (identifyingElement != null) {
                String name = identifyingElement.getName();
                assert name != null;
                results.add(new VariableDto(variableDefinition, name, ReservedVariableScope.TestCase));
            }
        }
        return results;
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();
        this.parameters = null;
        this.definedVariables = null;
        this.inlineVariables = null;
        this.testCaseVariables = null;
        this.invokedKeywords = null;
    }

    @Override
    public final String getKeywordName() {
        return getPresentableText();
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        KeywordDefinitionStub stub = getStub();
        if (stub != null) {
            return PsiTreeUtil.findChildOfType(stub.getPsi(), KeywordDefinitionId.class);
        }
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
