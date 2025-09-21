// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.navigation.NavigationItem;

public class RobotVisitor extends PsiElementVisitor {

  public void visitArgument(@NotNull RobotArgument o) {
    visitStatement(o);
  }

  public void visitBddStatement(@NotNull RobotBddStatement o) {
    visitStatement(o);
  }

  public void visitBlockOpeningStructure(@NotNull RobotBlockOpeningStructure o) {
    visitExecutableStatement(o);
    // visitFoldable(o);
    // visitScopeOwner(o);
  }

  public void visitCommentsSection(@NotNull RobotCommentsSection o) {
    visitSection(o);
  }

  public void visitDictVariable(@NotNull RobotDictVariable o) {
    visitVariable(o);
  }

  public void visitDocumentationStatementGlobalSetting(@NotNull RobotDocumentationStatementGlobalSetting o) {
    visitGlobalSettingStatement(o);
  }

  public void visitElseIfStructure(@NotNull RobotElseIfStructure o) {
    visitExecutableStatement(o);
    // visitBlockOpeningStructure(o);
  }

  public void visitEmptyVariableStatement(@NotNull RobotEmptyVariableStatement o) {
    visitVariableStatement(o);
  }

  public void visitEnvironmentVariable(@NotNull RobotEnvironmentVariable o) {
    visitVariable(o);
  }

  public void visitExceptStructure(@NotNull RobotExceptStructure o) {
    visitExecutableStatement(o);
    // visitBlockOpeningStructure(o);
  }

  public void visitExecutableStatement(@NotNull RobotExecutableStatement o) {
    visitFoldable(o);
    // visitStatement(o);
  }

  public void visitExtendedVariableIndexAccess(@NotNull RobotExtendedVariableIndexAccess o) {
    visitStatement(o);
  }

  public void visitExtendedVariableKeyAccess(@NotNull RobotExtendedVariableKeyAccess o) {
    visitStatement(o);
  }

  public void visitExtendedVariableNestedAccess(@NotNull RobotExtendedVariableNestedAccess o) {
    visitStatement(o);
  }

  public void visitExtendedVariableSliceAccess(@NotNull RobotExtendedVariableSliceAccess o) {
    visitStatement(o);
  }

  public void visitFinallyStructure(@NotNull RobotFinallyStructure o) {
    visitExecutableStatement(o);
    // visitBlockOpeningStructure(o);
  }

  public void visitFoldable(@NotNull RobotFoldable o) {
    visitStatement(o);
  }

  public void visitForLoopStructure(@NotNull RobotForLoopStructure o) {
    visitExecutableStatement(o);
    // visitBlockOpeningStructure(o);
  }

  public void visitGlobalSettingStatement(@NotNull RobotGlobalSettingStatement o) {
    visitFoldable(o);
    // visitStatement(o);
  }

  public void visitGroupStructure(@NotNull RobotGroupStructure o) {
    visitExecutableStatement(o);
    // visitBlockOpeningStructure(o);
  }

  public void visitIfElseStructure(@NotNull RobotIfElseStructure o) {
    visitExecutableStatement(o);
    // visitBlockOpeningStructure(o);
  }

  public void visitIfStructure(@NotNull RobotIfStructure o) {
    visitExecutableStatement(o);
    // visitBlockOpeningStructure(o);
  }

  public void visitInlineVariableStatement(@NotNull RobotInlineVariableStatement o) {
    visitVariableStatement(o);
  }

  public void visitKeywordCall(@NotNull RobotKeywordCall o) {
    visitQualifiedNameOwner(o);
    // visitKeywordCallExpression(o);
    // visitPsiNameIdentifierOwner(o);
    // visitNavigationItem(o);
    // visitCallArgumentsContainer(o);
    // visitFoldable(o);
    // visitStatement(o);
  }

  public void visitKeywordCallLibrary(@NotNull RobotKeywordCallLibrary o) {
    visitStatement(o);
  }

  public void visitKeywordCallLibraryName(@NotNull RobotKeywordCallLibraryName o) {
    visitStatement(o);
  }

  public void visitKeywordCallName(@NotNull RobotKeywordCallName o) {
    visitStatement(o);
  }

  public void visitKeywordVariableStatement(@NotNull RobotKeywordVariableStatement o) {
    visitVariableStatement(o);
  }

  public void visitKeywordsSection(@NotNull RobotKeywordsSection o) {
    visitSection(o);
    // visitScopeOwner(o);
  }

  public void visitLanguage(@NotNull RobotLanguage o) {
    visitStatement(o);
  }

  public void visitLanguageId(@NotNull RobotLanguageId o) {
    visitStatement(o);
  }

  public void visitLibraryImportGlobalSetting(@NotNull RobotLibraryImportGlobalSetting o) {
    visitGlobalSettingStatement(o);
    // visitImportGlobalSettingExpression(o);
  }

  public void visitListVariable(@NotNull RobotListVariable o) {
    visitVariable(o);
  }

  public void visitLiteralConstantValue(@NotNull RobotLiteralConstantValue o) {
    visitStatement(o);
  }

  public void visitLocalArgumentsSetting(@NotNull RobotLocalArgumentsSetting o) {
    visitFoldable(o);
    // visitStatement(o);
  }

  public void visitLocalArgumentsSettingId(@NotNull RobotLocalArgumentsSettingId o) {
    visitStatement(o);
  }

  public void visitLocalArgumentsSettingParameter(@NotNull RobotLocalArgumentsSettingParameter o) {
    visitStatement(o);
  }

  public void visitLocalArgumentsSettingParameterMandatory(@NotNull RobotLocalArgumentsSettingParameterMandatory o) {
    visitStatement(o);
  }

  public void visitLocalArgumentsSettingParameterOptional(@NotNull RobotLocalArgumentsSettingParameterOptional o) {
    visitStatement(o);
  }

  public void visitLocalSetting(@NotNull RobotLocalSetting o) {
    visitFoldable(o);
    // visitStatement(o);
  }

  public void visitLocalSettingId(@NotNull RobotLocalSettingId o) {
    visitStatement(o);
  }

  public void visitMetadataStatementGlobalSetting(@NotNull RobotMetadataStatementGlobalSetting o) {
    visitGlobalSettingStatement(o);
  }

  public void visitNewLibraryName(@NotNull RobotNewLibraryName o) {
    visitStatement(o);
  }

  public void visitParameter(@NotNull RobotParameter o) {
    visitArgument(o);
    // visitStatement(o);
  }

  public void visitParameterId(@NotNull RobotParameterId o) {
    visitStatement(o);
  }

  public void visitPositionalArgument(@NotNull RobotPositionalArgument o) {
    visitArgument(o);
    // visitStatement(o);
  }

  public void visitPythonExpression(@NotNull RobotPythonExpression o) {
    visitStatement(o);
  }

  public void visitResourceImportGlobalSetting(@NotNull RobotResourceImportGlobalSetting o) {
    visitGlobalSettingStatement(o);
    // visitImportGlobalSettingExpression(o);
  }

  public void visitRoot(@NotNull RobotRoot o) {
    visitStatement(o);
  }

  public void visitScalarVariable(@NotNull RobotScalarVariable o) {
    visitVariable(o);
  }

  public void visitScopeOwner(@NotNull RobotScopeOwner o) {
    visitStatement(o);
  }

  public void visitSection(@NotNull RobotSection o) {
    visitFoldable(o);
    // visitStatement(o);
  }

  public void visitSettingsSection(@NotNull RobotSettingsSection o) {
    visitSection(o);
  }

  public void visitSetupTeardownStatementsGlobalSetting(@NotNull RobotSetupTeardownStatementsGlobalSetting o) {
    visitGlobalSettingStatement(o);
  }

  public void visitSingleVariableStatement(@NotNull RobotSingleVariableStatement o) {
    visitVariableStatement(o);
  }

  public void visitSuiteNameStatementGlobalSetting(@NotNull RobotSuiteNameStatementGlobalSetting o) {
    visitGlobalSettingStatement(o);
  }

  public void visitTagsStatementGlobalSetting(@NotNull RobotTagsStatementGlobalSetting o) {
    visitGlobalSettingStatement(o);
  }

  public void visitTaskId(@NotNull RobotTaskId o) {
    visitStatement(o);
  }

  public void visitTaskStatement(@NotNull RobotTaskStatement o) {
    visitQualifiedNameOwner(o);
    // visitPsiNameIdentifierOwner(o);
    // visitNavigationItem(o);
    // visitFoldable(o);
    // visitStatement(o);
  }

  public void visitTasksSection(@NotNull RobotTasksSection o) {
    visitSection(o);
    // visitScopeOwner(o);
  }

  public void visitTemplateArgument(@NotNull RobotTemplateArgument o) {
    visitArgument(o);
    // visitStatement(o);
  }

  public void visitTemplateArguments(@NotNull RobotTemplateArguments o) {
    visitTemplateArgumentsExpression(o);
    // visitCallArgumentsContainer(o);
    // visitStatement(o);
  }

  public void visitTemplateParameter(@NotNull RobotTemplateParameter o) {
    visitArgument(o);
    // visitStatement(o);
  }

  public void visitTemplateParameterId(@NotNull RobotTemplateParameterId o) {
    visitStatement(o);
  }

  public void visitTemplateStatementsGlobalSetting(@NotNull RobotTemplateStatementsGlobalSetting o) {
    visitGlobalSettingStatement(o);
  }

  public void visitTestCaseId(@NotNull RobotTestCaseId o) {
    visitStatement(o);
  }

  public void visitTestCaseStatement(@NotNull RobotTestCaseStatement o) {
    visitQualifiedNameOwner(o);
    // visitPsiNameIdentifierOwner(o);
    // visitNavigationItem(o);
    // visitFoldable(o);
    // visitStatement(o);
  }

  public void visitTestCasesSection(@NotNull RobotTestCasesSection o) {
    visitSection(o);
    // visitScopeOwner(o);
  }

  public void visitTimeoutStatementsGlobalSetting(@NotNull RobotTimeoutStatementsGlobalSetting o) {
    visitGlobalSettingStatement(o);
  }

  public void visitTryElseStructure(@NotNull RobotTryElseStructure o) {
    visitExecutableStatement(o);
    // visitBlockOpeningStructure(o);
  }

  public void visitTryStructure(@NotNull RobotTryStructure o) {
    visitExecutableStatement(o);
    // visitBlockOpeningStructure(o);
  }

  public void visitUnknownSettingStatementsGlobalSetting(@NotNull RobotUnknownSettingStatementsGlobalSetting o) {
    visitGlobalSettingStatement(o);
  }

  public void visitUserKeywordStatement(@NotNull RobotUserKeywordStatement o) {
    visitQualifiedNameOwner(o);
    // visitUserKeywordStatementExpression(o);
    // visitPsiNameIdentifierOwner(o);
    // visitNavigationItem(o);
    // visitFoldable(o);
    // visitStatement(o);
  }

  public void visitUserKeywordStatementId(@NotNull RobotUserKeywordStatementId o) {
    visitStatement(o);
  }

  public void visitVariable(@NotNull RobotVariable o) {
    visitStatement(o);
  }

  public void visitVariableBodyId(@NotNull RobotVariableBodyId o) {
    visitStatement(o);
  }

  public void visitVariableContent(@NotNull RobotVariableContent o) {
    visitStatement(o);
  }

  public void visitVariableDefinition(@NotNull RobotVariableDefinition o) {
    visitPsiNameIdentifierOwner(o);
    // visitNavigationItem(o);
    // visitDefinedVariable(o);
    // visitQualifiedNameOwner(o);
    // visitFoldable(o);
    // visitStatement(o);
  }

  public void visitVariableStatement(@NotNull RobotVariableStatement o) {
    visitFoldable(o);
    // visitStatement(o);
  }

  public void visitVariableValue(@NotNull RobotVariableValue o) {
    visitStatement(o);
  }

  public void visitVariablesImportGlobalSetting(@NotNull RobotVariablesImportGlobalSetting o) {
    visitGlobalSettingStatement(o);
    // visitImportGlobalSettingExpression(o);
  }

  public void visitVariablesSection(@NotNull RobotVariablesSection o) {
    visitSection(o);
  }

  public void visitWhileLoopStructure(@NotNull RobotWhileLoopStructure o) {
    visitExecutableStatement(o);
    // visitBlockOpeningStructure(o);
  }

  public void visitPsiNameIdentifierOwner(@NotNull PsiNameIdentifierOwner o) {
    visitElement(o);
  }

  public void visitQualifiedNameOwner(@NotNull RobotQualifiedNameOwner o) {
    visitPsiElement(o);
  }

  public void visitStatement(@NotNull RobotStatement o) {
    visitPsiElement(o);
  }

  public void visitTemplateArgumentsExpression(@NotNull RobotTemplateArgumentsExpression o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
