package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.dto.VariableDto;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.util.PatternUtil;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class KeywordStatementImpl extends RobotPsiElementBase implements KeywordStatement {

    private List<Argument> arguments;
    private DefinedVariable variable;
    private KeywordInvokable invokable;

    public KeywordStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    @Override
    public final KeywordInvokable getInvokable() {
        KeywordInvokable result = this.invokable;
        if (this.invokable == null) {
            for (PsiElement child : getChildren()) {
                if (child instanceof KeywordInvokable) {
                    result = (KeywordInvokable) child;
                    break;
                }
            }
            this.invokable = result;
        }
        return result;
    }

    @NotNull
    @Override
    public final List<Argument> getArguments() {
        List<Argument> results = this.arguments;
        if (this.arguments == null) {
            results = new ArrayList<>();
            for (PsiElement element : getChildren()) {
                if (element instanceof Argument) {
                    results.add((Argument) element);
                }
            }
            this.arguments = results;
        }
        return results;
    }

    @Nullable
    @Override
    public final DefinedVariable getGlobalVariable() {
        DefinedVariable result = this.variable;
        if (result == null) {
            KeywordInvokable invokable = getInvokable();
            if (invokable != null) {
                String text = invokable.getPresentableText();
                if (PatternUtil.isVariableSettingKeyword(text)) {
                    List<Argument> arguments = getArguments();
                    if (!arguments.isEmpty()) {
                        Argument variable = arguments.get(0);
                        // already formatted ${X}
                        result = new VariableDto(variable, variable.getPresentableText(), null);
                    }
                }
            }
            this.variable = result;
        }
        return result;
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();
        this.arguments = null;
        this.invokable = null;
        this.variable = null;
    }
}
