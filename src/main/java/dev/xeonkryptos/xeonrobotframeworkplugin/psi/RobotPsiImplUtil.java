package dev.xeonkryptos.xeonrobotframeworkplugin.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.icons.RobotIcons;
import dev.xeonkryptos.xeonrobotframeworkplugin.misc.RobotReadWriteAccessDetector;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotCommentsSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotImportArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotInlineVariableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallLibraryName;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallName;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordVariableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordsSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSettingId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalSettingId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotParameterId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument;
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
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.RobotImportArgumentReference;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.RobotKeywordCallLibraryReference;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.RobotKeywordCallNameReference;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.RobotParameterReference;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.RobotPositionalArgumentReference;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.RobotTemplateParameterReference;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.RobotVariableBodyReference;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.QualifiedNameBuilder;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.VariableScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import java.util.List;
import java.util.Optional;

public class RobotPsiImplUtil {

    @NotNull
    public static String getName(@NotNull RobotKeywordCall robotKeywordCall) {
        RobotKeywordCallName keywordCallName = getNameIdentifier(robotKeywordCall);
        return keywordCallName.getText();
    }

    @NotNull
    public static String getName(@NotNull RobotUserKeywordStatement userKeywordStatement) {
        RobotUserKeywordStatementId userKeywordStatementId = getNameIdentifier(userKeywordStatement);
        return userKeywordStatementId.getText();
    }

    @Nullable
    public static String getName(@NotNull RobotVariableDefinition variableDefinition) {
        RobotVariable variable = getNameIdentifier(variableDefinition);
        return variable.getVariableName();
    }

    @NotNull
    public static String getName(@NotNull RobotTestCaseStatement testCaseStatement) {
        RobotTestCaseId testCaseId = testCaseStatement.getTestCaseId();
        return testCaseId.getText();
    }

    @NotNull
    public static String getName(@NotNull RobotTaskStatement taskStatement) {
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

    @NotNull
    public static RobotVariable getNameIdentifier(@NotNull RobotVariableDefinition variableDefinition) {
        return variableDefinition.getVariable();
    }

    @Nullable
    public static String getVariableName(@NotNull RobotVariable variable) {
        RobotVariableBodyId variableBodyId = getVariableBodyId(variable);
        return variableBodyId != null ? variableBodyId.getText() : null;
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
            if (child.getNode().getElementType() == RobotTypes.LOCAL_SETTING_NAME) {
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

    @NotNull
    public static VariableScope getScope(@NotNull RobotVariableDefinition variableDefinition) {
        RobotInlineVariableStatement inlineVariableStatement = PsiTreeUtil.getParentOfType(variableDefinition, RobotInlineVariableStatement.class, true);
        Optional<VariableScope> variableScopeOpt = Optional.ofNullable(inlineVariableStatement)
                                                           .map(RobotInlineVariableStatement::getParameter)
                                                           .map(RobotParameter::getPositionalArgument)
                                                           .map(PsiElement::getText)
                                                           .map(scope -> switch (scope.toUpperCase()) {
                                                               case "LOCAL" -> VariableScope.Local;
                                                               case "TEST", "TASK" -> VariableScope.TestCase;
                                                               case "SUITE", "SUITES" -> VariableScope.TestSuite;
                                                               case "GLOBAL" -> VariableScope.Global;
                                                               default -> null;
                                                           })
                                                           .or(() -> {
                                                               RobotVariablesSection variablesSection = PsiTreeUtil.getParentOfType(variableDefinition,
                                                                                                                                    RobotVariablesSection.class,
                                                                                                                                    true);
                                                               return Optional.ofNullable(variablesSection).map(ignored -> VariableScope.TestSuite);
                                                           })
                                                           .or(() -> {
                                                               RobotKeywordVariableStatement keywordVariableStatement = PsiTreeUtil.getParentOfType(
                                                                       variableDefinition,
                                                                       RobotKeywordVariableStatement.class,
                                                                       true);
                                                               return Optional.ofNullable(keywordVariableStatement)
                                                                              .map(RobotKeywordVariableStatement::getKeywordCall)
                                                                              .map(RobotKeywordCall::getKeywordCallName)
                                                                              .map(PsiElement::getText)
                                                                              .filter(RobotReadWriteAccessDetector::isVariableSetterKeyword)
                                                                              .map(RobotReadWriteAccessDetector::getVariableSetterScope);
                                                           });
        return variableScopeOpt.orElse(VariableScope.Local);
    }
}
