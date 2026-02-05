package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.util.IncorrectOperationException;
import dev.xeonkryptos.xeonrobotframeworkplugin.misc.RobotReadWriteAccessDetector;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotPsiUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.VariableType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.FoldingText;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotIfVariableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSettingParameterOptional;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableBodyId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableValue;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.folding.RobotFoldingComputationUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotStubPsiElementBase;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotVariableDefinitionStub;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotElementGenerator;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.VariableScope;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.GlobalConstants;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.KeywordUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotNames;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.VariableNameUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public abstract class RobotVariableDefinitionExtension extends RobotStubPsiElementBase<RobotVariableDefinitionStub, RobotVariableDefinition> implements RobotVariableDefinition {

    private static final Set<String> CREATE_LIST_KEYWORD_NAMES = Set.of(RobotNames.CREATE_LIST_KEYWORD_NAME, RobotNames.BUILTIN_NAMESPACE + "." + RobotNames.CREATE_LIST_KEYWORD_NAME);
    private static final Set<String> CREATE_DICTIONARY_KEYWORD_NAMES = Set.of(RobotNames.CREATE_DICTIONARY_KEYWORD_NAME,
                                                                              RobotNames.BUILTIN_NAMESPACE + "." + RobotNames.CREATE_DICTIONARY_KEYWORD_NAME);

    private Set<String> variableNameVariants;
    private VariableScope scope;

    public RobotVariableDefinitionExtension(@NotNull ASTNode node) {
        super(node);
    }

    public RobotVariableDefinitionExtension(RobotVariableDefinitionStub stub, IStubElementType<RobotVariableDefinitionStub, RobotVariableDefinition> nodeType) {
        super(stub, nodeType);
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();

        variableNameVariants = null;
        scope = null;
    }

    @Override
    public boolean isInScope(@NotNull PsiElement element) {
        return getScope().isInScope(this, element);
    }

    @Override
    public boolean matches(@Nullable String text) {
        if (text == null) {
            return false;
        }
        if (variableNameVariants == null) {
            String variableName = getName();
            variableNameVariants = VariableNameUtil.INSTANCE.computeVariableNameVariants(variableName);
        }
        return VariableNameUtil.INSTANCE.matchesVariableName(text, variableNameVariants);
    }

    @Override
    public int getTextOffset() {
        return super.getTextOffset() + 2; // to skip the ${, @{, &{ or %{ at the beginning of the variable
    }

    @Override
    public PsiElement setName(@NotNull String newName) throws IncorrectOperationException {
        RobotVariableBodyId newVariableBodyId = RobotElementGenerator.getInstance(getProject()).createNewVariableBodyId(newName);
        RobotVariableBodyId variableBodyId = RobotPsiUtil.getVariableBodyId(this);
        if (variableBodyId != null && newVariableBodyId != null) {
            variableBodyId.replace(newVariableBodyId);
        }
        return this;
    }

    @Override
    public @NotNull VariableType getVariableType() {
        RobotVariableDefinitionStub stub = getStub();
        if (stub != null) {
            return stub.getVariableType();
        }
        PsiElement firstChild = getFirstChild();
        if (firstChild == null) {
            return VariableType.SCALAR;
        }
        IElementType elementType = PsiUtilCore.getElementType(firstChild);
        if (elementType == RobotTypes.LIST_VARIABLE_START) {
            return VariableType.LIST;
        } else if (elementType == RobotTypes.DICT_VARIABLE_START) {
            return VariableType.DICTIONARY;
        }
        return VariableType.SCALAR;
    }

    @Override
    public String getLookup() {
        return getName();
    }

    @Override
    public String getPresentableText() {
        String name = getName();
        if (name == null) {
            return getText();
        }
        return getVariableType().prefixed(name);
    }

    @Override
    public String[] getLookupWords() {
        String name = getName();
        String text = getText();
        if (name == null) {
            return new String[] { text };
        }

        VariableType variableType = getVariableType();
        return new String[] { name, variableType.prefixed(name), text };
    }

    @NotNull
    @Override
    public VariableScope getScope() {
        if (scope == null) {
            RobotVariableDefinitionStub stub = getStub();
            if (stub != null) {
                scope = stub.getScope();
            }
            if (scope == null) {
                scope = RobotPsiUtil.getScope(this);
            }
        }
        return scope;
    }

    public @Nullable FoldingText getAssignedValues() {
        PsiElement parent = getParent();
        StringBuilder builder = new StringBuilder();
        List<PsiElement> dependants = new ArrayList<>();
        RobotVisitor visitor = new RobotVisitor() {
            @Override
            public void visitLocalArgumentsSettingParameterOptional(@NotNull RobotLocalArgumentsSettingParameterOptional o) {
                String text = o.getPositionalArgument().getText().trim();
                builder.append(text);
                dependants.add(o.getPositionalArgument());
            }

            @Override
            public void visitVariableStatement(@NotNull RobotVariableStatement o) {
                Collection<RobotElement> children = PsiTreeUtil.findChildrenOfAnyType(o, true, RobotVariableValue.class, RobotKeywordCall.class);
                for (RobotElement child : children) {
                    child.accept(this);
                }
            }

            @Override
            public void visitVariableValue(@NotNull RobotVariableValue o) {
                appendFoldableText(o);
            }

            @Override
            public void visitKeywordCall(@NotNull RobotKeywordCall o) {
                String keywordName = o.getName();
                String normalizeKeywordName = KeywordUtil.normalizeKeywordName(keywordName);
                if (RobotReadWriteAccessDetector.isVariableSetterKeyword(keywordName)) {
                    Collection<RobotArgument> allCallArguments = o.getAllCallArguments();
                    for (RobotArgument argument : allCallArguments) {
                        appendFoldableText(argument);
                    }
                } else if (CREATE_LIST_KEYWORD_NAMES.contains(normalizeKeywordName)) {
                    builder.append("[");
                    Collection<RobotArgument> allCallArguments = o.getAllCallArguments();
                    for (RobotArgument argument : allCallArguments) {
                        appendFoldableText(argument);
                    }
                    if (builder.length() - 1 >= RobotFoldingComputationUtil.MAX_VARIABLE_FOLDING_LENGTH) {
                        String text = builder.deleteCharAt(0).toString();
                        text = StringUtil.shortenTextWithEllipsis(text, RobotFoldingComputationUtil.MAX_VARIABLE_FOLDING_LENGTH, 0);
                        builder.replace(1, builder.length(), text);
                    }
                    builder.append("]");
                } else if (CREATE_DICTIONARY_KEYWORD_NAMES.contains(normalizeKeywordName)) {
                    builder.append("{");
                    Collection<RobotArgument> allCallArguments = o.getAllCallArguments();
                    for (RobotArgument argument : allCallArguments) {
                        appendFoldableText(argument);
                    }
                    if (builder.length() - 1 >= RobotFoldingComputationUtil.MAX_VARIABLE_FOLDING_LENGTH) {
                        String text = builder.deleteCharAt(0).toString();
                        text = StringUtil.shortenTextWithEllipsis(text, RobotFoldingComputationUtil.MAX_VARIABLE_FOLDING_LENGTH, 0);
                        builder.replace(1, builder.length(), text);
                    }
                    builder.append("}");
                }
            }

            private void appendFoldableText(RobotElement element) {
                String text = element.getText().trim();
                if (!builder.isEmpty()) {
                    builder.append(GlobalConstants.SUPER_SPACE);
                }
                builder.append(text);
                dependants.add(element);
            }

            @Override
            public void visitIfVariableStatement(@NotNull RobotIfVariableStatement o) {
                // Not supporting this case
            }
        };
        parent.accept(visitor);
        return !builder.isEmpty() ? new FoldingText(builder.toString(), dependants) : null;
    }

    @NotNull
    @Override
    public abstract Icon getIcon(int flags);

    @Override
    public PsiElement reference() {
        return this;
    }
}
