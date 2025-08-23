package dev.xeonkryptos.xeonrobotframeworkplugin.psi;

import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.icons.RobotIcons;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallLibraryName;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallName;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSettingId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalSettingId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotParameterId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTaskId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTaskStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatementId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableBodyId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableContent;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableStatement;
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
    public static String getSettingName(@NotNull RobotLocalSetting localSetting) {
        RobotLocalSettingId localSettingId = localSetting.getLocalSettingId();
        return localSettingId.getText();
    }

    @NotNull
    public static String getSettingName(@NotNull RobotLocalArgumentsSetting localArgumentsSetting) {
        RobotLocalArgumentsSettingId localSettingId = localArgumentsSetting.getLocalArgumentsSettingId();
        return localSettingId.getText();
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

    @Nullable
    public static RobotVariableBodyId getVariableBodyId(RobotVariable variable) {
        RobotVariableContent variableContent = PsiTreeUtil.getChildOfType(variable, RobotVariableContent.class);
        if (variableContent != null) {
            return PsiTreeUtil.getChildOfType(variableContent, RobotVariableBodyId.class);
        }
        return null;
    }
}
