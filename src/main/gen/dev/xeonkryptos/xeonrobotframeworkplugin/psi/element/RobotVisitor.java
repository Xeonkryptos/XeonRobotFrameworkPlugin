// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.navigation.NavigationItem;

public class RobotVisitor extends PsiElementVisitor {

  public void visitArgument(@NotNull RobotArgument o) {
    visitElement(o);
  }

  public void visitBddStatement(@NotNull RobotBddStatement o) {
    visitElement(o);
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
    // visitElement(o);
  }

  public void visitExtendedVariableIndexAccess(@NotNull RobotExtendedVariableIndexAccess o) {
    visitElement(o);
  }

  public void visitExtendedVariableKeyAccess(@NotNull RobotExtendedVariableKeyAccess o) {
    visitElement(o);
  }

  public void visitExtendedVariableNestedAccess(@NotNull RobotExtendedVariableNestedAccess o) {
    visitElement(o);
  }

  public void visitExtendedVariableSliceAccess(@NotNull RobotExtendedVariableSliceAccess o) {
    visitElement(o);
  }

  public void visitFinallyStructure(@NotNull RobotFinallyStructure o) {
    visitExecutableStatement(o);
    // visitBlockOpeningStructure(o);
  }

  public void visitFoldable(@NotNull RobotFoldable o) {
    visitElement(o);
  }

  public void visitForLoopStructure(@NotNull RobotForLoopStructure o) {
    visitExecutableStatement(o);
    // visitBlockOpeningStructure(o);
  }

  public void visitGlobalSettingStatement(@NotNull RobotGlobalSettingStatement o) {
    visitFoldable(o);
    // visitElement(o);
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
    // visitElement(o);
  }

  public void visitKeywordCallLibrary(@NotNull RobotKeywordCallLibrary o) {
    visitElement(o);
  }

  public void visitKeywordCallLibraryName(@NotNull RobotKeywordCallLibraryName o) {
    visitElement(o);
  }

  public void visitKeywordCallName(@NotNull RobotKeywordCallName o) {
    visitElement(o);
  }

  public void visitKeywordVariableStatement(@NotNull RobotKeywordVariableStatement o) {
    visitVariableStatement(o);
  }

  public void visitKeywordsSection(@NotNull RobotKeywordsSection o) {
    visitSection(o);
    // visitScopeOwner(o);
  }

  public void visitLanguage(@NotNull RobotLanguage o) {
    visitElement(o);
  }

  public void visitLanguageId(@NotNull RobotLanguageId o) {
    visitElement(o);
  }

  public void visitLibraryImportGlobalSetting(@NotNull RobotLibraryImportGlobalSetting o) {
    visitGlobalSettingStatement(o);
    // visitImportGlobalSettingExpression(o);
  }

  public void visitListVariable(@NotNull RobotListVariable o) {
    visitVariable(o);
  }

  public void visitLiteralConstantValue(@NotNull RobotLiteralConstantValue o) {
    visitElement(o);
  }

  public void visitLocalArgumentsSetting(@NotNull RobotLocalArgumentsSetting o) {
    visitFoldable(o);
    // visitElement(o);
  }

  public void visitLocalArgumentsSettingId(@NotNull RobotLocalArgumentsSettingId o) {
    visitElement(o);
  }

  public void visitLocalArgumentsSettingParameter(@NotNull RobotLocalArgumentsSettingParameter o) {
    visitElement(o);
  }

  public void visitLocalArgumentsSettingParameterMandatory(@NotNull RobotLocalArgumentsSettingParameterMandatory o) {
    visitElement(o);
  }

  public void visitLocalArgumentsSettingParameterOptional(@NotNull RobotLocalArgumentsSettingParameterOptional o) {
    visitElement(o);
  }

  public void visitLocalSetting(@NotNull RobotLocalSetting o) {
    visitFoldable(o);
    // visitElement(o);
  }

  public void visitLocalSettingId(@NotNull RobotLocalSettingId o) {
    visitElement(o);
  }

  public void visitMetadataStatementGlobalSetting(@NotNull RobotMetadataStatementGlobalSetting o) {
    visitGlobalSettingStatement(o);
  }

  public void visitNewLibraryName(@NotNull RobotNewLibraryName o) {
    visitElement(o);
  }

  public void visitParameter(@NotNull RobotParameter o) {
    visitArgument(o);
    // visitElement(o);
  }

  public void visitParameterId(@NotNull RobotParameterId o) {
    visitElement(o);
  }

  public void visitPositionalArgument(@NotNull RobotPositionalArgument o) {
    visitArgument(o);
    // visitElement(o);
  }

  public void visitPythonExpression(@NotNull RobotPythonExpression o) {
    visitElement(o);
  }

  public void visitResourceImportGlobalSetting(@NotNull RobotResourceImportGlobalSetting o) {
    visitGlobalSettingStatement(o);
    // visitImportGlobalSettingExpression(o);
  }

  public void visitRoot(@NotNull RobotRoot o) {
    visitElement(o);
  }

  public void visitScalarVariable(@NotNull RobotScalarVariable o) {
    visitVariable(o);
  }

  public void visitScopeOwner(@NotNull RobotScopeOwner o) {
    visitElement(o);
  }

  public void visitSection(@NotNull RobotSection o) {
    visitFoldable(o);
    // visitElement(o);
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
    visitElement(o);
  }

  public void visitTaskStatement(@NotNull RobotTaskStatement o) {
    visitQualifiedNameOwner(o);
    // visitPsiNameIdentifierOwner(o);
    // visitNavigationItem(o);
    // visitScopeOwner(o);
    // visitFoldable(o);
    // visitElement(o);
  }

  public void visitTasksSection(@NotNull RobotTasksSection o) {
    visitSection(o);
    // visitScopeOwner(o);
  }

  public void visitTemplateArgument(@NotNull RobotTemplateArgument o) {
    visitArgument(o);
    // visitElement(o);
  }

  public void visitTemplateArguments(@NotNull RobotTemplateArguments o) {
    visitTemplateArgumentsExpression(o);
    // visitCallArgumentsContainer(o);
    // visitElement(o);
  }

  public void visitTemplateParameter(@NotNull RobotTemplateParameter o) {
    visitArgument(o);
    // visitElement(o);
  }

  public void visitTemplateParameterId(@NotNull RobotTemplateParameterId o) {
    visitElement(o);
  }

  public void visitTemplateStatementsGlobalSetting(@NotNull RobotTemplateStatementsGlobalSetting o) {
    visitGlobalSettingStatement(o);
  }

  public void visitTestCaseId(@NotNull RobotTestCaseId o) {
    visitElement(o);
  }

  public void visitTestCaseStatement(@NotNull RobotTestCaseStatement o) {
    visitQualifiedNameOwner(o);
    // visitPsiNameIdentifierOwner(o);
    // visitNavigationItem(o);
    // visitFoldable(o);
    // visitScopeOwner(o);
    // visitElement(o);
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
    // visitScopeOwner(o);
    // visitFoldable(o);
    // visitElement(o);
  }

  public void visitUserKeywordStatementId(@NotNull RobotUserKeywordStatementId o) {
    visitElement(o);
  }

  public void visitVariable(@NotNull RobotVariable o) {
    visitElement(o);
  }

  public void visitVariableBodyId(@NotNull RobotVariableBodyId o) {
    visitElement(o);
  }

  public void visitVariableContent(@NotNull RobotVariableContent o) {
    visitElement(o);
  }

  public void visitVariableDefinition(@NotNull RobotVariableDefinition o) {
    visitPsiNameIdentifierOwner(o);
    // visitNavigationItem(o);
    // visitDefinedVariable(o);
    // visitQualifiedNameOwner(o);
    // visitFoldable(o);
    // visitElement(o);
  }

  public void visitVariableStatement(@NotNull RobotVariableStatement o) {
    visitFoldable(o);
    // visitElement(o);
  }

  public void visitVariableValue(@NotNull RobotVariableValue o) {
    visitElement(o);
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

  public void visitElement(@NotNull RobotElement o) {
    visitPsiElement(o);
  }

  public void visitQualifiedNameOwner(@NotNull RobotQualifiedNameOwner o) {
    visitPsiElement(o);
  }

  public void visitTemplateArgumentsExpression(@NotNull RobotTemplateArgumentsExpression o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
