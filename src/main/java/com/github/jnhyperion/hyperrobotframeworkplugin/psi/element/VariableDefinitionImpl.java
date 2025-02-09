package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.github.jnhyperion.hyperrobotframeworkplugin.ide.icons.RobotIcons;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.util.PatternUtil;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import java.util.regex.Pattern;

public class VariableDefinitionImpl extends RobotPsiElementBase implements VariableDefinition, PsiNameIdentifierOwner {

    private Pattern pattern;

    public VariableDefinitionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();
        this.pattern = null;
    }

    @Override
    public String getVariableName() {
        VariableDefinitionId variableId = PsiTreeUtil.getChildOfType(this, VariableDefinitionId.class);
        return variableId != null ? variableId.getText() : getName();
    }

    @Override
    public final boolean matches(String text) {
        if (text == null) {
            return false;
        }
        String myText = this.getPresentableText();
        Pattern pattern = this.pattern;
        if (this.pattern == null) {
            pattern = Pattern.compile(PatternUtil.getVariablePattern(myText), Pattern.CASE_INSENSITIVE);
            this.pattern = pattern;
        }
        return pattern.matcher(text).matches();
    }

    @Override
    public final boolean isInScope(@Nullable PsiElement position) {
        return true;
    }

    @Override
    public final PsiElement reference() {
        return this;
    }

    @Nullable
    @Override
    public final String getLookup() {
        return getVariableName();
    }

    @Override
    public final boolean isNested() {
        String text = this.getPresentableText();
        return StringUtil.getOccurrenceCount(text, "}") > 1 &&
               StringUtil.getOccurrenceCount(text, "${") + StringUtil.getOccurrenceCount(text, "@{") + StringUtil.getOccurrenceCount(text, "%{") > 1;
    }

    @NotNull
    @Override
    public Icon getIcon(int flags) {
        return RobotIcons.VARIABLE;
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return PsiTreeUtil.findChildOfType(this, VariableDefinitionId.class);
    }
}
