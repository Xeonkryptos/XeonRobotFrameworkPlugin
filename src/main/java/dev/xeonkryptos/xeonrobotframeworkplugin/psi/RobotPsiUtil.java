package dev.xeonkryptos.xeonrobotframeworkplugin.psi;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.util.indexing.DumbModeAccessType;
import com.jetbrains.python.psi.PyBoolLiteralExpression;
import com.jetbrains.python.psi.PyDictLiteralExpression;
import com.jetbrains.python.psi.PyElementVisitor;
import com.jetbrains.python.psi.PyExpression;
import com.jetbrains.python.psi.PyKeyValueExpression;
import com.jetbrains.python.psi.PyNoneLiteralExpression;
import com.jetbrains.python.psi.PyNumericLiteralExpression;
import com.jetbrains.python.psi.PySequenceExpression;
import com.jetbrains.python.psi.PyStringLiteralExpression;
import com.jetbrains.python.psi.PyTargetExpression;
import com.jetbrains.python.psi.PyTupleExpression;
import dev.xeonkryptos.xeonrobotframeworkplugin.icons.RobotIcons;
import dev.xeonkryptos.xeonrobotframeworkplugin.misc.RobotReadWriteAccessDetector;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.FoldingText;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotCommentsSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotDictVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotImportArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotInlineVariableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallLibraryName;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallName;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordVariableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordsSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotListVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSettingId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalSettingId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotParameterId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotScalarVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSettingsSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTaskId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTaskStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTasksSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTemplateParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTemplateParameterId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCasesSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatementId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableBodyId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableContent;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariablesSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl.RobotTestCaseExtension;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.folding.RobotFoldingComputationUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.reference.RobotImportArgumentReference;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.reference.RobotKeywordCallLibraryReference;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.reference.RobotKeywordCallNameReference;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.reference.RobotParameterReference;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.reference.RobotPositionalArgumentReference;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.reference.RobotTemplateParameterReference;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.reference.RobotVariableBodyReference;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotDictVariableStub;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotKeywordCallStub;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotListVariableStub;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotScalarVariableStub;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotTaskStatementStub;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotTestCaseStatementStub;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotUserKeywordStub;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotVariableDefinitionStub;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.QualifiedNameBuilder;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.VariableScope;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.GlobalConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
public class RobotPsiUtil {

    @NotNull
    public static String getName(@NotNull RobotKeywordCall robotKeywordCall) {
        RobotKeywordCallStub stub = robotKeywordCall.getStub();
        if (stub != null) {
            return stub.getName();
        }
        RobotKeywordCallName keywordCallName = getNameIdentifier(robotKeywordCall);
        return keywordCallName.getText();
    }

    @NotNull
    public static String getName(@NotNull RobotUserKeywordStatement userKeywordStatement) {
        RobotUserKeywordStub stub = userKeywordStatement.getStub();
        if (stub != null) {
            return stub.getName();
        }
        RobotUserKeywordStatementId userKeywordStatementId = getNameIdentifier(userKeywordStatement);
        return userKeywordStatementId.getText();
    }

    @Nullable
    public static String getName(@NotNull RobotVariableDefinition variableDefinition) {
        RobotVariableDefinitionStub stub = variableDefinition.getStub();
        if (stub != null) {
            return stub.getName();
        }
        RobotVariableBodyId variableBodyId = getVariableBodyId(variableDefinition);
        return variableBodyId != null ? variableBodyId.getText() : null;
    }

    @NotNull
    public static String getName(@NotNull RobotTestCaseStatement testCaseStatement) {
        RobotTestCaseStatementStub stub = testCaseStatement.getStub();
        if (stub != null) {
            return stub.getName();
        }
        RobotTestCaseId testCaseId = testCaseStatement.getTestCaseId();
        return testCaseId.getText();
    }

    @NotNull
    public static String getName(@NotNull RobotTaskStatement taskStatement) {
        RobotTaskStatementStub stub = taskStatement.getStub();
        if (stub != null) {
            return stub.getName();
        }
        RobotTaskId taskId = taskStatement.getTaskId();
        return taskId.getText();
    }

    @NotNull
    public static RobotUserKeywordStatementId getNameIdentifier(@NotNull RobotUserKeywordStatement userKeywordStatement) {
        return userKeywordStatement.getUserKeywordStatementId();
    }

    @NotNull
    public static RobotKeywordCallName getNameIdentifier(@NotNull RobotKeywordCall keywordCall) {
        return keywordCall.getKeywordCallName();
    }

    @NotNull
    public static RobotTaskId getNameIdentifier(@NotNull RobotTaskStatement taskStatement) {
        return taskStatement.getTaskId();
    }

    @NotNull
    public static RobotTestCaseId getNameIdentifier(@NotNull RobotTestCaseStatement testCaseStatement) {
        return testCaseStatement.getTestCaseId();
    }

    @Nullable
    public static String getVariableName(@NotNull RobotScalarVariable variable) {
        RobotScalarVariableStub stub = variable.getStub();
        if (stub != null) {
            return stub.getVariableName();
        }
        RobotVariableBodyId variableBodyId = getVariableBodyId(variable);
        return variableBodyId != null ? variableBodyId.getText() : null;
    }

    @Nullable
    public static String getVariableName(@NotNull RobotListVariable variable) {
        RobotListVariableStub stub = variable.getStub();
        if (stub != null) {
            return stub.getVariableName();
        }
        RobotVariableBodyId variableBodyId = getVariableBodyId(variable);
        return variableBodyId != null ? variableBodyId.getText() : null;
    }

    @Nullable
    public static String getVariableName(@NotNull RobotDictVariable variable) {
        RobotDictVariableStub stub = variable.getStub();
        if (stub != null) {
            return stub.getVariableName();
        }
        RobotVariableBodyId variableBodyId = getVariableBodyId(variable);
        return variableBodyId != null ? variableBodyId.getText() : null;
    }

    @Nullable
    public static String getVariableName(@NotNull RobotVariable variable) {
        return switch (variable) {
            case RobotScalarVariable robotScalarVariable -> getVariableName(robotScalarVariable);
            case RobotListVariable robotListVariable -> getVariableName(robotListVariable);
            case RobotDictVariable robotDictVariable -> getVariableName(robotDictVariable);
            default -> {
                RobotVariableBodyId variableBodyId = getVariableBodyId(variable);
                yield variableBodyId != null ? variableBodyId.getText() : null;
            }
        };
    }

    @NotNull
    public static String getParameterName(@NotNull RobotParameter parameter) {
        RobotParameterId parameterId = parameter.getParameterId();
        return parameterId.getText();
    }

    @NotNull
    public static String getParameterName(@NotNull RobotTemplateParameter parameter) {
        RobotTemplateParameterId parameterId = parameter.getTemplateParameterId();
        return parameterId.getText();
    }

    @NotNull
    public static String getSettingName(@NotNull RobotLocalSetting localSetting) {
        RobotLocalSettingId localSettingId = localSetting.getLocalSettingId();
        return localSettingId.getSettingName().getText();
    }

    @NotNull
    public static String getSettingName(@NotNull RobotLocalArgumentsSetting localArgumentsSetting) {
        RobotLocalArgumentsSettingId localSettingId = localArgumentsSetting.getLocalArgumentsSettingId();
        return getSettingName(localSettingId);
    }

    @NotNull
    public static String getSettingName(@NotNull RobotLocalArgumentsSettingId localArgumentsSettingId) {
        PsiElement[] children = localArgumentsSettingId.getChildren();
        for (PsiElement child : children) {
            if (PsiUtilCore.getElementType(child) == RobotTypes.LOCAL_SETTING_NAME) {
                return child.getText();
            }
        }
        throw new IllegalArgumentException("LocalArgumentsSettingId has no LOCAL_SETTING_NAME child");
    }

    @NotNull
    public static PsiReference getReference(RobotVariableBodyId variableBodyId) {
        return new RobotVariableBodyReference(variableBodyId);
    }

    @NotNull
    public static PsiReference getReference(RobotKeywordCallLibraryName keywordCallLibraryId) {
        return new RobotKeywordCallLibraryReference(keywordCallLibraryId);
    }

    @NotNull
    public static PsiReference getReference(RobotKeywordCallName keywordCallName) {
        return new RobotKeywordCallNameReference(keywordCallName);
    }

    @NotNull
    public static PsiReference getReference(RobotParameterId parameterId) {
        return new RobotParameterReference(parameterId);
    }

    @NotNull
    public static PsiReference getReference(RobotPositionalArgument positionalArgument) {
        return new RobotPositionalArgumentReference(positionalArgument);
    }

    @NotNull
    public static PsiReference getReference(RobotImportArgument importArgument) {
        return new RobotImportArgumentReference(importArgument);
    }

    @NotNull
    public static PsiReference getReference(RobotTemplateParameterId templateParameterId) {
        return new RobotTemplateParameterReference(templateParameterId);
    }

    @NotNull
    @SuppressWarnings("unused")
    public static Icon getIcon(RobotTaskStatement taskStatement, int flags) {
        return RobotIcons.JUNIT;
    }

    @NotNull
    @SuppressWarnings("unused")
    public static Icon getIcon(RobotTestCaseExtension testCaseExtension, int flags) {
        return RobotIcons.JUNIT;
    }

    @NotNull
    @SuppressWarnings("unused")
    public static Icon getIcon(RobotVariableDefinition variableDefinition, int flags) {
        return RobotIcons.VARIABLE;
    }

    @NotNull
    @SuppressWarnings("unused")
    public static Icon getIcon(RobotVariableStatement variableStatement, int flags) {
        return RobotIcons.VARIABLE;
    }

    @NotNull
    public static String getQualifiedName(RobotTaskStatement taskStatement) {
        return QualifiedNameBuilder.computeQualifiedName(taskStatement);
    }

    @NotNull
    public static String getQualifiedName(RobotTestCaseExtension testCaseExtension) {
        return QualifiedNameBuilder.computeQualifiedName(testCaseExtension);
    }

    @NotNull
    public static String getQualifiedName(RobotUserKeywordStatement userKeywordStatement) {
        return QualifiedNameBuilder.computeQualifiedName(userKeywordStatement);
    }

    @NotNull
    public static String getQualifiedName(RobotVariableDefinition variableDefinition) {
        return QualifiedNameBuilder.computeQualifiedName(variableDefinition);
    }

    @Nullable
    public static RobotVariableBodyId getVariableBodyId(RobotVariable variable) {
        RobotVariableContent variableContent = PsiTreeUtil.getChildOfType(variable, RobotVariableContent.class);
        if (variableContent != null) {
            return PsiTreeUtil.getChildOfType(variableContent, RobotVariableBodyId.class);
        }
        return null;
    }

    @Nullable
    public static RobotVariableBodyId getVariableBodyId(RobotVariableDefinition variableDefinition) {
        RobotVariableContent variableContent = PsiTreeUtil.getChildOfType(variableDefinition, RobotVariableContent.class);
        if (variableContent != null) {
            return PsiTreeUtil.getChildOfType(variableContent, RobotVariableBodyId.class);
        }
        return null;
    }

    @NotNull
    public static List<RobotLocalSetting> getLocalSettings(@NotNull RobotTestCaseStatement element) {
        return PsiTreeUtil.getChildrenOfTypeAsList(element, RobotLocalSetting.class);
    }

    @NotNull
    public static List<RobotLocalSetting> getLocalSettings(@NotNull RobotTaskStatement element) {
        return PsiTreeUtil.getChildrenOfTypeAsList(element, RobotLocalSetting.class);
    }

    @NotNull
    @SuppressWarnings("unused")
    public static String getSectionName(@NotNull RobotSection section) {
        return "Unknown";
    }

    @NotNull
    public static String getSectionName(@NotNull RobotSettingsSection section) {
        return section.getNameIdentifier().getText();
    }

    @NotNull
    public static String getSectionName(@NotNull RobotCommentsSection section) {
        return section.getNameIdentifier().getText();
    }

    @NotNull
    public static String getSectionName(@NotNull RobotVariablesSection section) {
        return section.getNameIdentifier().getText();
    }

    @NotNull
    public static String getSectionName(@NotNull RobotTestCasesSection section) {
        return section.getNameIdentifier().getText();
    }

    @NotNull
    public static String getSectionName(@NotNull RobotTasksSection section) {
        return section.getNameIdentifier().getText();
    }

    @NotNull
    public static String getSectionName(@NotNull RobotKeywordsSection section) {
        return section.getNameIdentifier().getText();
    }

    public static FoldingText getAssignedValues(@NotNull RobotVariable variable) {
        RobotVariableBodyId variableBodyId = RobotPsiUtil.getVariableBodyId(variable);
        Optional<PsiElement> resolvedElementOpt = Optional.ofNullable(variableBodyId).map(RobotVariableBodyId::getReference).map(PsiReference::resolve);
        if (resolvedElementOpt.isPresent()) {
            PsiElement psiElement = resolvedElementOpt.get();
            if (psiElement instanceof RobotVariableDefinition variableDefinition) {
                return variableDefinition.getAssignedValues();
            }
            if (psiElement instanceof PyTargetExpression targetExpression) {
                PyExpression assignedValue = targetExpression.findAssignedValue();
                if (assignedValue != null) {
                    StringBuilder builder = new StringBuilder();
                    PyElementVisitor visitor = new PyElementVisitor() {
                        @Override
                        public void visitPyStringLiteralExpression(@NotNull PyStringLiteralExpression node) {
                            String stringLiteral = node.getStringValue();
                            builder.append(stringLiteral);
                        }

                        @Override
                        public void visitPyBoolLiteralExpression(@NotNull PyBoolLiteralExpression node) {
                            boolean value = node.getValue();
                            builder.append(value ? "True" : "False");
                        }

                        @Override
                        public void visitPyNumericLiteralExpression(@NotNull PyNumericLiteralExpression node) {
                            String numberLiteral = node.getBigDecimalValue().toPlainString();
                            builder.append(numberLiteral);
                        }

                        @Override
                        public void visitPyNoneLiteralExpression(@NotNull PyNoneLiteralExpression node) {
                            builder.append("None");
                        }

                        @Override
                        public void visitPyDictLiteralExpression(@NotNull PyDictLiteralExpression node) {
                            builder.append("{");
                            int startLength = builder.length();
                            PyKeyValueExpression[] elements = node.getElements();
                            for (PyKeyValueExpression element : elements) {
                                if (builder.length() > startLength) {
                                    builder.append(GlobalConstants.SUPER_SPACE);
                                }
                                PyExpression keyExpression = element.getKey();
                                keyExpression.accept(this);

                                builder.append("=");
                                PyExpression valueExpression = element.getValue();
                                if (valueExpression != null) {
                                    valueExpression.accept(this);
                                }
                            }
                            builder.append("}");
                        }

                        @Override
                        public void visitPySequenceExpression(@NotNull PySequenceExpression node) {
                            builder.append("[");
                            int startLength = builder.length();
                            PyExpression[] elements = node.getElements();
                            for (PyExpression element : elements) {
                                if (builder.length() > startLength) {
                                    builder.append(GlobalConstants.SUPER_SPACE);
                                }
                                element.accept(this);
                            }
                            builder.append("]");
                        }

                        @Override
                        public void visitPyTupleExpression(@NotNull PyTupleExpression node) {
                            node.acceptChildren(this);
                        }
                    };
                    assignedValue.accept(visitor);
                    return new FoldingText(builder.toString(), Set.of(assignedValue));
                }
            }
        }
        return null;
    }

    public static FoldingDescriptor[] fold(RobotVariable variable, @NotNull Document ignoredDocument, boolean quick) {
        if (quick) {
            return FoldingDescriptor.EMPTY_ARRAY;
        }
        FoldingText assignedValues = DumbModeAccessType.RELIABLE_DATA_ONLY.ignoreDumbMode(() -> getAssignedValues(variable));
        if (assignedValues == null) {
            return FoldingDescriptor.EMPTY_ARRAY;
        }
        ASTNode variableEndNode = variable.getNode().findChildByType(RobotTypes.VARIABLE_END);
        if (variableEndNode == null) {
            return FoldingDescriptor.EMPTY_ARRAY;
        }

        String foldingText = StringUtil.shortenTextWithEllipsis(assignedValues.foldingText().trim(), RobotFoldingComputationUtil.MAX_VARIABLE_FOLDING_LENGTH, 0);

        int endOffset = variableEndNode.getTextRange().getEndOffset();
        TextRange textRange = variable.getTextRange();
        textRange = new TextRange(textRange.getStartOffset(), endOffset);
        return new FoldingDescriptor[] { new FoldingDescriptor(variable.getNode(), textRange, null, foldingText, false, Set.copyOf(assignedValues.dependants())) };
    }

    public static boolean areElementsEquivalent(PsiElement current, PsiElement another) {
        String qualifiedName;
        if (another instanceof PsiNamedElement namedElement) {
            qualifiedName = QualifiedNameBuilder.computeQualifiedName(namedElement);
        } else {
            qualifiedName = QualifiedNameBuilder.computeQualifiedPath(another) + "." + another.getText();
        }
        String thisQualifiedName;
        if (current instanceof PsiNamedElement namedElement) {
            thisQualifiedName = QualifiedNameBuilder.computeQualifiedName(namedElement);
        } else {
            thisQualifiedName = QualifiedNameBuilder.computeQualifiedPath(current) + "." + current.getText();
        }
        return qualifiedName.equals(thisQualifiedName);
    }

    @NotNull
    public static VariableScope getScope(@NotNull RobotVariableDefinition variableDefinition) {
        RobotVariableDefinitionStub stub = variableDefinition.getStub();
        if (stub != null) {
            return stub.getScope();
        }
        RobotInlineVariableStatement inlineVariableStatement = PsiTreeUtil.getParentOfType(variableDefinition, RobotInlineVariableStatement.class, true);
        Optional<VariableScope> variableScopeOpt = Optional.ofNullable(inlineVariableStatement)
                                                           .map(RobotInlineVariableStatement::getParameter)
                                                           .map(RobotParameter::getPositionalArgument)
                                                           .map(PsiElement::getText)
                                                           .map(scope -> switch (scope.toUpperCase()) {
                                                               case "LOCAL" -> VariableScope.Local;
                                                               case "TEST", "TASK" -> VariableScope.TestCase;
                                                               case "SUITE" -> VariableScope.TestSuite;
                                                               case "GLOBAL", "SUITES" -> VariableScope.Global;
                                                               default -> null;
                                                           })
                                                           .or(() -> {
                                                               RobotVariablesSection variablesSection = PsiTreeUtil.getParentOfType(variableDefinition, RobotVariablesSection.class, true);
                                                               return Optional.ofNullable(variablesSection).map(ignored -> VariableScope.TestSuite);
                                                           })
                                                           .or(() -> {
                                                               RobotKeywordVariableStatement keywordVariableStatement = PsiTreeUtil.getParentOfType(variableDefinition,
                                                                                                                                                    RobotKeywordVariableStatement.class,
                                                                                                                                                    true);
                                                               return Optional.ofNullable(keywordVariableStatement)
                                                                              .map(RobotKeywordVariableStatement::getKeywordCall)
                                                                              .map(RobotKeywordCall::getName)
                                                                              .filter(RobotReadWriteAccessDetector::isVariableSetterKeyword)
                                                                              .map(RobotReadWriteAccessDetector::getVariableSetterScope);
                                                           });
        return variableScopeOpt.orElse(VariableScope.Local);
    }
}
