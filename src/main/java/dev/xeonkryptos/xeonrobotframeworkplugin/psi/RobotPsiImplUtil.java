package dev.xeonkryptos.xeonrobotframeworkplugin.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.icons.RobotIcons;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotDocumentationStatementGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotInlineVariableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallLibrary;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallLibraryName;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallName;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLanguageId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLibraryImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalSettingArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalSettingId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotMetadataStatementGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotParameterId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotResourceImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSetupTeardownStatementsGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSingleVariableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSuiteNameStatementGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTagsStatementGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTaskId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTaskStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTemplateParameterId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTemplateStatementsGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTimeoutStatementsGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUnknownSettingStatementsGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatementId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableBodyId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariablesImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl.RobotTestCaseExtension;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.RobotArgumentReference;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.RobotKeywordCallLibraryReference;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.RobotKeywordCallNameReference;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.RobotParameterReference;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.RobotVariableBodyReference;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.QualifiedNameBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;

public class RobotPsiImplUtil {

    @NotNull
    public static String getName(@NotNull RobotSection section) {
        PsiElement nameIdentifier = section.getNameIdentifier();
        assert nameIdentifier != null;
        return nameIdentifier.getText();
    }

    @NotNull
    public static String getName(@NotNull RobotLanguageId languageId) {
        return languageId.getText();
    }

    @NotNull
    public static String getName(@NotNull RobotDocumentationStatementGlobalSetting robotDocumentationStatementGlobalSetting) {
        PsiElement nameIdentifier = robotDocumentationStatementGlobalSetting.getNameIdentifier();
        return nameIdentifier.getText();
    }

    @NotNull
    public static String getName(@NotNull RobotLibraryImportGlobalSetting importGlobalSetting) {
        PsiElement nameIdentifier = importGlobalSetting.getNameIdentifier();
        return nameIdentifier.getText();
    }

    @NotNull
    public static String getName(@NotNull RobotVariablesImportGlobalSetting importGlobalSetting) {
        PsiElement nameIdentifier = importGlobalSetting.getNameIdentifier();
        return nameIdentifier.getText();
    }

    @NotNull
    public static String getName(@NotNull RobotResourceImportGlobalSetting importGlobalSetting) {
        PsiElement nameIdentifier = importGlobalSetting.getNameIdentifier();
        return nameIdentifier.getText();
    }

    @NotNull
    public static String getName(@NotNull RobotMetadataStatementGlobalSetting robotMetadataStatementGlobalSetting) {
        PsiElement nameIdentifier = robotMetadataStatementGlobalSetting.getNameIdentifier();
        return nameIdentifier.getText();
    }

    @NotNull
    public static String getName(@NotNull RobotSuiteNameStatementGlobalSetting robotSuiteNameStatementGlobalSetting) {
        PsiElement nameIdentifier = robotSuiteNameStatementGlobalSetting.getNameIdentifier();
        return nameIdentifier.getText();
    }

    @NotNull
    public static String getName(@NotNull RobotTagsStatementGlobalSetting robotTagsStatementGlobalSetting) {
        PsiElement nameIdentifier = robotTagsStatementGlobalSetting.getNameIdentifier();
        return nameIdentifier.getText();
    }

    @NotNull
    public static String getName(@NotNull RobotTemplateStatementsGlobalSetting robotTemplateStatementsGlobalSetting) {
        PsiElement nameIdentifier = robotTemplateStatementsGlobalSetting.getNameIdentifier();
        return nameIdentifier.getText();
    }

    @NotNull
    public static String getName(@NotNull RobotTimeoutStatementsGlobalSetting robotTimeoutStatementsGlobalSetting) {
        PsiElement nameIdentifier = robotTimeoutStatementsGlobalSetting.getNameIdentifier();
        return nameIdentifier.getText();
    }

    @NotNull
    public static String getName(@NotNull RobotUnknownSettingStatementsGlobalSetting robotUnknownSettingStatementsGlobalSetting) {
        PsiElement nameIdentifier = robotUnknownSettingStatementsGlobalSetting.getNameIdentifier();
        return nameIdentifier.getText();
    }

    @NotNull
    public static String getName(@NotNull RobotKeywordCall robotKeywordCall) {
        RobotKeywordCallName keywordCallName = robotKeywordCall.getKeywordCallName();
        return keywordCallName.getName();
    }

    @NotNull
    public static String getName(@NotNull RobotKeywordCallName keywordCallName) {
        return keywordCallName.getText();
    }

    @NotNull
    public static String getName(@NotNull RobotKeywordCallLibrary keywordCallLibrary) {
        return keywordCallLibrary.getNameIdentifier().getText();
    }

    @NotNull
    public static String getName(@NotNull RobotLocalSettingId localSettingId) {
        return localSettingId.getText();
    }

    @NotNull
    public static String getName(@NotNull RobotUserKeywordStatement userKeywordStatement) {
        RobotUserKeywordStatementId userKeywordStatementId = userKeywordStatement.getNameIdentifier();
        return userKeywordStatementId.getText();
    }

    @NotNull
    public static String getName(@NotNull RobotUserKeywordStatementId userKeywordStatementId) {
        return userKeywordStatementId.getText();
    }

    @NotNull
    public static String getName(@NotNull RobotTestCaseStatement robotTestCaseStatement) {
        RobotTestCaseId testCaseId = robotTestCaseStatement.getNameIdentifier();
        return testCaseId.getText();
    }

    @NotNull
    public static String getName(@NotNull RobotTestCaseId testCaseId) {
        return testCaseId.getText();
    }

    @NotNull
    public static String getName(@NotNull RobotTaskStatement robotTaskStatement) {
        RobotTaskId taskId = robotTaskStatement.getNameIdentifier();
        return taskId.getName();
    }

    @NotNull
    public static String getName(@NotNull RobotTaskId taskId) {
        return taskId.getText();
    }

    @NotNull
    public static String getName(@NotNull RobotTemplateParameterId templateParameterId) {
        return templateParameterId.getText();
    }

    @Nullable
    public static String getName(RobotVariable variable) {
        RobotVariableBodyId nameIdentifier = getNameIdentifier(variable);
        return nameIdentifier != null ? nameIdentifier.getName() : null;
    }

    @NotNull
    public static String getName(RobotVariableBodyId variableBodyId) {
        return variableBodyId.getText();
    }

    @NotNull
    public static String getName(RobotParameter parameter) {
        RobotParameterId nameIdentifier = parameter.getNameIdentifier();
        return nameIdentifier.getName();
    }

    @NotNull
    public static String getName(RobotParameterId parameterId) {
        return parameterId.getText();
    }

    @Nullable
    public static String getName(RobotInlineVariableStatement inlineVariableStatement) {
        return inlineVariableStatement.getNameIdentifier().getName();
    }

    @NotNull
    public static String getName(@NotNull RobotLocalSetting localSetting) {
        return localSetting.getNameIdentifier().getName();
    }

    @NotNull
    public static String getName(@NotNull RobotSetupTeardownStatementsGlobalSetting setupTeardownStatementsGlobalSetting) {
        return setupTeardownStatementsGlobalSetting.getNameIdentifier().getText();
    }

    @Nullable
    public static String getName(@NotNull RobotVariableDefinition variableDefinition) {
        RobotVariable nameIdentifier = variableDefinition.getNameIdentifier();
        return nameIdentifier.getName();
    }

    @Nullable
    public static RobotVariableBodyId getNameIdentifier(RobotVariable variable) {
        return PsiTreeUtil.findChildOfType(variable, RobotVariableBodyId.class);
    }

    @NotNull
    public static RobotVariable getNameIdentifier(@NotNull RobotInlineVariableStatement inlineVariableStatement) {
        return inlineVariableStatement.getVariableDefinition().getVariable();
    }

    @NotNull
    public static RobotKeywordCallName getNameIdentifier(@NotNull RobotKeywordCall keywordCall) {
        return keywordCall.getKeywordCallName();
    }

    @Nullable
    public static RobotVariableBodyId getNameIdentifier(@NotNull RobotLocalSettingArgument localSettingArgument) {
        RobotVariable variable = localSettingArgument.getVariable();
        return PsiTreeUtil.getChildOfType(variable, RobotVariableBodyId.class);
    }

    @NotNull
    public static RobotVariable getNameIdentifier(@NotNull RobotSingleVariableStatement singleVariableStatement) {
        return singleVariableStatement.getVariableDefinition().getVariable();
    }

    @NotNull
    public static RobotVariable getNameIdentifier(@NotNull RobotVariableDefinition variableDefinition) {
        return variableDefinition.getVariable();
    }

    @NotNull
    public static RobotKeywordCallLibraryName getNameIdentifier(@NotNull RobotKeywordCallLibrary keywordCallLibrary) {
        return keywordCallLibrary.getKeywordCallLibraryName();
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
        return new RobotArgumentReference(positionalArgument);
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
}
