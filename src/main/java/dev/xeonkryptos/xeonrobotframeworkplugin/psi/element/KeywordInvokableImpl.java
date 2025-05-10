package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.RobotKeywordReference;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

public class KeywordInvokableImpl extends RobotPsiElementBase implements KeywordInvokable {

    public KeywordInvokableImpl(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public Collection<Parameter> getParameters() {
        PsiElement parent = getParent();
        if (parent instanceof KeywordStatement keywordStatement) {
            return keywordStatement.getParameters();
        }
        return Collections.emptySet();
    }

    @NotNull
    @Override
    public final Collection<PositionalArgument> getPositionalArguments() {
        PsiElement parent = getParent();
        if (parent instanceof KeywordStatement keywordStatement) {
            return keywordStatement.getPositionalArguments();
        }
        return Collections.emptySet();
    }

    @NotNull
    @Override
    public PsiReference getReference() {
        return new RobotKeywordReference(this);
    }
}
