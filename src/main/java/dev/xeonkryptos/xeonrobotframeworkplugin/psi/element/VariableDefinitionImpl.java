package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.lang.ASTNode;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.icons.RobotIcons;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotStubPsiElementBase;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.element.VariableDefinitionStub;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.PatternUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.QualifiedNameBuilder;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.ReservedVariableScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import java.util.Objects;
import java.util.regex.Pattern;

public class VariableDefinitionImpl extends RobotStubPsiElementBase<VariableDefinitionStub, VariableDefinition> implements VariableDefinition {

    private Pattern pattern;

    public VariableDefinitionImpl(@NotNull ASTNode node) {
        super(node);
    }

    public VariableDefinitionImpl(@NotNull VariableDefinitionStub stub, @NotNull IStubElementType<VariableDefinitionStub, VariableDefinition> nodeType) {
        super(stub, nodeType);
    }

    @NotNull
    @Override
    public String getName() {
        VariableDefinitionStub stub = getStub();
        if (stub != null) {
            return stub.getName();
        }
        VariableDefinitionId variableId = getNameIdentifier();
        InjectedLanguageManager injectedLanguageManager = InjectedLanguageManager.getInstance(getProject());
        PsiElement sourceElement = Objects.requireNonNullElse(variableId, this);
        return injectedLanguageManager.getUnescapedText(sourceElement);
    }

    @NotNull
    @Override
    public String getUnwrappedName() {
        VariableDefinitionStub stub = getStub();
        if (stub != null) {
            return stub.getUnwrappedName();
        }
        return VariableDefinition.super.getUnwrappedName();
    }

    @NotNull
    @Override
    public SearchScope getUseScope() {
        KeywordDefinition keywordDefinition = PsiTreeUtil.getParentOfType(this, KeywordDefinition.class);
        if (keywordDefinition != null) {
            return keywordDefinition.getUseScope();
        }
        return super.getUseScope();
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();

        this.pattern = null;
    }

    @Override
    public final boolean matches(String text) {
        if (text == null) {
            return false;
        }
        String myText = getName();
        Pattern pattern = this.pattern;
        if (pattern == null && !isEmpty()) {
            pattern = Pattern.compile(PatternUtil.getVariablePattern(myText), Pattern.CASE_INSENSITIVE);
            this.pattern = pattern;
        }
        return pattern != null && pattern.matcher(text).matches();
    }

    @Override
    public boolean isEmpty() {
        // A variable contains always $, @ or % and at enclosing brackets. Meaning, a variable consists of at least 3 characters even when no name is defined
        return getName().length() <= 3;
    }

    @Override
    public final boolean isInScope(@NotNull PsiElement position) {
        KeywordDefinition keywordDefinition = PsiTreeUtil.getParentOfType(this, KeywordDefinition.class);
        if (keywordDefinition != null) {
            return ReservedVariableScope.TestCase.isInScope(this, position);
        }
        Heading heading = PsiTreeUtil.getParentOfType(this, Heading.class);
        if (heading == null || !heading.isGlobalVariablesProvider()) {
            return getContainingFile() == position.getContainingFile();
        }
        return true;
    }

    @Override
    public final @NotNull PsiElement reference() {
        return this;
    }

    @NotNull
    @Override
    public final String getLookup() {
        return getName();
    }

    @Override
    public final boolean isNested() {
        String text = getName();
        return StringUtil.getOccurrenceCount(text, "}") > 1
               && StringUtil.getOccurrenceCount(text, "${") + StringUtil.getOccurrenceCount(text, "@{") + StringUtil.getOccurrenceCount(text, "%{") > 1;
    }

    @Override
    public @NotNull String getQualifiedName() {
        return QualifiedNameBuilder.computeQualifiedName(this);
    }

    @NotNull
    @Override
    public Icon getIcon(int flags) {
        return RobotIcons.VARIABLE;
    }

    @Nullable
    @Override
    public VariableDefinitionId getNameIdentifier() {
        VariableDefinitionStub stub = getStub();
        if (stub != null) {
            return PsiTreeUtil.findChildOfType(stub.getPsi(), VariableDefinitionId.class);
        }
        return PsiTreeUtil.findChildOfType(this, VariableDefinitionId.class);
    }
}
