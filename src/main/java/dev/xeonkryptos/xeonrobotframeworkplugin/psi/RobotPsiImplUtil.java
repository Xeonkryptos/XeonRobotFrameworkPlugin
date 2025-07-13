package dev.xeonkryptos.xeonrobotframeworkplugin.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotDocumentationStatementGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLanguageId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLibraryImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalSettingId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotMetadataStatementGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotParameterId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotResourceImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSection;
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
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariablesImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.RobotKeywordReference;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.RobotParameterReference;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.RobotVariableReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        RobotKeywordCallId keywordCallId = robotKeywordCall.getKeywordCallId();
        return keywordCallId.getName();
    }

    @NotNull
    public static String getName(@NotNull RobotKeywordCallId keywordCallId) {
        return keywordCallId.getText();
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
        return taskId.getText();
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
    public static RobotVariableId getNameIdentifier(RobotVariable variable) {
        return PsiTreeUtil.findChildOfType(variable, RobotVariableId.class);
    }

    @Nullable
    public static String getName(RobotVariable variable) {
        RobotVariableId nameIdentifier = getNameIdentifier(variable);
        return nameIdentifier != null ? nameIdentifier.getName() : null;
    }

    @Nullable
    public static String getName(RobotVariableId variableId) {
        PsiElement contentElement = variableId.getContent();
        if (contentElement == null) {
            return null;
        }
        String nameContent = contentElement.getText();
        for (int i = 0; i < nameContent.length(); i++) {
            char c = nameContent.charAt(i);
            if (c == '.' || c == '[' || c == ':' || c == '+' || c == '-' || c == '*' || c == '/') {
                return nameContent.substring(0, i);
            }
        }
        return nameContent;
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

    @NotNull
    public static PsiReference getReference(RobotVariableId variableId) {
        return new RobotVariableReference(variableId);
    }

    @NotNull
    public static PsiReference getReference(RobotKeywordCallId keywordCallId) {
        return new RobotKeywordReference(keywordCallId);
    }

    @NotNull
    public static PsiReference getReference(RobotParameterId parameterId) {
        return new RobotParameterReference(parameterId);
    }
}
