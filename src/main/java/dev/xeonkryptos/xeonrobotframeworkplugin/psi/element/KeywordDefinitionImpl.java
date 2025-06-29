package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.lang.ASTNode;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.icons.RobotIcons;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.VariableDto;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotStubPsiElementBase;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.element.KeywordDefinitionStub;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.QualifiedNameBuilder;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.ReservedVariableScope;
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

    @NotNull
    @Override
    public String getName() {
        KeywordDefinitionStub stub = getStub();
        if (stub != null) {
            return stub.getName();
        }
        PsiElement nameIdentifier = getNameIdentifier();
        if (nameIdentifier != null) {
            return InjectedLanguageManager.getInstance(getProject()).getUnescapedText(nameIdentifier);
        }
        return "";
    }

    @Override
    public Collection<DefinedParameter> getParameters() {
        Collection<DefinedParameter> results = parameters;
        if (results == null || results.stream().map(DefinedParameter::reference).anyMatch(element -> !element.isValid())) {
            for (BracketSetting bracketSetting : PsiTreeUtil.getChildrenOfTypeAsList(this, BracketSetting.class)) {
                if (bracketSetting.isArguments()) {
                    results = bracketSetting.getArguments();
                    break;
                }
            }
            parameters = results != null ? results : List.of();
        }
        return results;
    }

    @NotNull
    @Override
    public final List<KeywordInvokable> getInvokedKeywords() {
        List<KeywordInvokable> results = invokedKeywords;
        if (invokedKeywords == null || results.stream().anyMatch(element -> !element.isValid())) {
            results = new ArrayList<>();
            for (PsiElement statement : getChildren()) {
                if (statement instanceof KeywordStatement || statement instanceof BracketSetting) {
                    results.addAll(PsiTreeUtil.collectElementsOfType(statement, KeywordInvokable.class));
                }
            }
            invokedKeywords = results;
        }
        return results;
    }

    @NotNull
    @Override
    public final Collection<DefinedVariable> getDeclaredVariables() {
        Set<DefinedVariable> results = new LinkedHashSet<>();
        results.addAll(getDefinedVariables());
        results.addAll(getInlineVariables());
        Collection<DefinedVariable> localTestCaseVariables = testCaseVariables;
        if (testCaseVariables == null || results.stream().map(DefinedVariable::reference).anyMatch(element -> !element.isValid())) {
            localTestCaseVariables = getTestCaseVariables();
            testCaseVariables = localTestCaseVariables;
        }
        results.addAll(localTestCaseVariables);
        return results;
    }

    @NotNull
    private Collection<DefinedVariable> getInlineVariables() {
        Collection<DefinedVariable> results = inlineVariables;
        if (inlineVariables == null || results.stream().map(DefinedVariable::reference).anyMatch(element -> !element.isValid())) {
            results = collectInlineVariables();
            inlineVariables = results;
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
        Collection<DefinedVariable> results = definedVariables;
        if (definedVariables == null || results.stream().map(DefinedVariable::reference).anyMatch(element -> !element.isValid())) {
            results = collectDefinedVariables();
            definedVariables = results;
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
            String name = variableDefinition.getName();
            results.add(new VariableDto(variableDefinition, name, ReservedVariableScope.TestCase));
        }
        for (VariableDefinitionGroup variableDefinitionGroup : PsiTreeUtil.getChildrenOfTypeAsList(this, VariableDefinitionGroup.class)) {
            Collection<DefinedVariable> definedVariables = variableDefinitionGroup.getDefinedVariables();
            results.addAll(definedVariables);
        }
        return results;
    }

    @Override
    public @NotNull String getQualifiedName() {
        return QualifiedNameBuilder.computeQualifiedName(this);
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();
        parameters = null;
        definedVariables = null;
        inlineVariables = null;
        testCaseVariables = null;
        invokedKeywords = null;
    }

    @Override
    public final String getKeywordName() {
        return getName();
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
