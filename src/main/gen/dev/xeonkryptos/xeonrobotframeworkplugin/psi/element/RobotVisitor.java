// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.navigation.NavigationItem;

public class RobotVisitor extends PsiElementVisitor {

  public void visitBddStatement(@NotNull RobotBddStatement o) {
    visitElement(o);
  }

  public void visitCommentsSection(@NotNull RobotCommentsSection o) {
    visitSection(o);
    // visitNameIdentifierHolder(o);
  }

  public void visitConditionalContent(@NotNull RobotConditionalContent o) {
    visitElement(o);
  }

  public void visitConditionalStructure(@NotNull RobotConditionalStructure o) {
    visitExecutableStatement(o);
    // visitFoldable(o);
  }

  public void visitDictVariable(@NotNull RobotDictVariable o) {
    visitVariable(o);
  }

  public void visitDocumentationStatementGlobalSetting(@NotNull RobotDocumentationStatementGlobalSetting o) {
    visitGlobalSettingStatement(o);
    // visitGlobalSettingStatementExpression(o);
  }

  public void visitElseIfStructure(@NotNull RobotElseIfStructure o) {
    visitExecutableStatement(o);
    // visitBlockOpeningStructure(o);
  }

  public void visitElseStructure(@NotNull RobotElseStructure o) {
    visitExecutableStatement(o);
    // visitBlockOpeningStructure(o);
    // visitFoldable(o);
  }

  public void visitEmptyVariableStatement(@NotNull RobotEmptyVariableStatement o) {
    visitVariableStatement(o);
  }

  public void visitEnvironmentVariable(@NotNull RobotEnvironmentVariable o) {
    visitVariable(o);
  }

  public void visitExceptHeader(@NotNull RobotExceptHeader o) {
    visitElement(o);
  }

  public void visitExceptStructure(@NotNull RobotExceptStructure o) {
    visitExecutableStatement(o);
    // visitBlockOpeningStructure(o);
    // visitFoldable(o);
  }

  public void visitExceptionHandlingStructure(@NotNull RobotExceptionHandlingStructure o) {
    visitExecutableStatement(o);
  }

  public void visitExecutableStatement(@NotNull RobotExecutableStatement o) {
    visitElement(o);
  }

  public void visitFinallyStructure(@NotNull RobotFinallyStructure o) {
    visitExecutableStatement(o);
    // visitBlockOpeningStructure(o);
    // visitFoldable(o);
  }

  public void visitForLoopHeader(@NotNull RobotForLoopHeader o) {
    visitElement(o);
  }

  public void visitForLoopStructure(@NotNull RobotForLoopStructure o) {
    visitExecutableStatement(o);
    // visitBlockOpeningStructure(o);
    // visitFoldable(o);
  }

  public void visitForLoopStructureFillParameter(@NotNull RobotForLoopStructureFillParameter o) {
    visitElement(o);
  }

  public void visitForLoopStructureModeParameter(@NotNull RobotForLoopStructureModeParameter o) {
    visitElement(o);
  }

  public void visitForLoopStructureParameter(@NotNull RobotForLoopStructureParameter o) {
    visitElement(o);
  }

  public void visitForLoopStructureStartParameter(@NotNull RobotForLoopStructureStartParameter o) {
    visitElement(o);
  }

  public void visitGlobalSettingStatement(@NotNull RobotGlobalSettingStatement o) {
    visitFoldable(o);
    // visitElement(o);
  }

  public void visitGroupHeader(@NotNull RobotGroupHeader o) {
    visitElement(o);
  }

  public void visitGroupStructure(@NotNull RobotGroupStructure o) {
    visitExecutableStatement(o);
    // visitBlockOpeningStructure(o);
    // visitFoldable(o);
  }

  public void visitIfStructure(@NotNull RobotIfStructure o) {
    visitExecutableStatement(o);
    // visitBlockOpeningStructure(o);
  }

  public void visitIfVariableStatement(@NotNull RobotIfVariableStatement o) {
    visitVariableStatement(o);
  }

  public void visitImportArgument(@NotNull RobotImportArgument o) {
    visitArgument(o);
    // visitElement(o);
  }

  public void visitInlineElseIfStructure(@NotNull RobotInlineElseIfStructure o) {
    visitExecutableStatement(o);
    // visitBlockOpeningStructure(o);
  }

  public void visitInlineElseStructure(@NotNull RobotInlineElseStructure o) {
    visitExecutableStatement(o);
    // visitBlockOpeningStructure(o);
  }

  public void visitInlineIfStructure(@NotNull RobotInlineIfStructure o) {
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
    // visitNameIdentifierHolder(o);
  }

  public void visitLibraryImportGlobalSetting(@NotNull RobotLibraryImportGlobalSetting o) {
    visitGlobalSettingStatement(o);
    // visitGlobalSettingStatementExpression(o);
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
    // visitGlobalSettingStatementExpression(o);
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
    // visitGlobalSettingStatementExpression(o);
    // visitImportGlobalSettingExpression(o);
  }

  public void visitReturnStructure(@NotNull RobotReturnStructure o) {
    visitExecutableStatement(o);
  }

  public void visitRoot(@NotNull RobotRoot o) {
    visitElement(o);
  }

  public void visitScalarVariable(@NotNull RobotScalarVariable o) {
    visitVariable(o);
  }

  public void visitSection(@NotNull RobotSection o) {
    visitFoldable(o);
    // visitElement(o);
  }

  public void visitSettingsSection(@NotNull RobotSettingsSection o) {
    visitSection(o);
    // visitNameIdentifierHolder(o);
  }

  public void visitSetupTeardownStatementsGlobalSetting(@NotNull RobotSetupTeardownStatementsGlobalSetting o) {
    visitGlobalSettingStatement(o);
    // visitGlobalSettingStatementExpression(o);
  }

  public void visitSingleVariableStatement(@NotNull RobotSingleVariableStatement o) {
    visitVariableStatement(o);
  }

  public void visitSuiteNameStatementGlobalSetting(@NotNull RobotSuiteNameStatementGlobalSetting o) {
    visitGlobalSettingStatement(o);
    // visitGlobalSettingStatementExpression(o);
  }

  public void visitTagsStatementGlobalSetting(@NotNull RobotTagsStatementGlobalSetting o) {
    visitGlobalSettingStatement(o);
    // visitGlobalSettingStatementExpression(o);
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
    // visitNameIdentifierHolder(o);
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
    // visitGlobalSettingStatementExpression(o);
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
    // visitNameIdentifierHolder(o);
  }

  public void visitTimeoutStatementsGlobalSetting(@NotNull RobotTimeoutStatementsGlobalSetting o) {
    visitGlobalSettingStatement(o);
    // visitGlobalSettingStatementExpression(o);
  }

  public void visitTryStructure(@NotNull RobotTryStructure o) {
    visitExecutableStatement(o);
    // visitBlockOpeningStructure(o);
    // visitFoldable(o);
  }

  public void visitUnknownSettingStatementsGlobalSetting(@NotNull RobotUnknownSettingStatementsGlobalSetting o) {
    visitGlobalSettingStatement(o);
    // visitGlobalSettingStatementExpression(o);
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
    // visitElement(o);
  }

  public void visitVariableIndexAccessContent(@NotNull RobotVariableIndexAccessContent o) {
    visitElement(o);
  }

  public void visitVariableNestedAccessContent(@NotNull RobotVariableNestedAccessContent o) {
    visitElement(o);
  }

  public void visitVariableSliceAccessContent(@NotNull RobotVariableSliceAccessContent o) {
    visitElement(o);
  }

  public void visitVariableStatement(@NotNull RobotVariableStatement o) {
    visitElement(o);
  }

  public void visitVariableValue(@NotNull RobotVariableValue o) {
    visitElement(o);
  }

  public void visitVariablesImportGlobalSetting(@NotNull RobotVariablesImportGlobalSetting o) {
    visitGlobalSettingStatement(o);
    // visitGlobalSettingStatementExpression(o);
    // visitImportGlobalSettingExpression(o);
  }

  public void visitVariablesSection(@NotNull RobotVariablesSection o) {
    visitSection(o);
    // visitNameIdentifierHolder(o);
  }

  public void visitWhileLoopHeader(@NotNull RobotWhileLoopHeader o) {
    visitElement(o);
  }

  public void visitWhileLoopStructure(@NotNull RobotWhileLoopStructure o) {
    visitExecutableStatement(o);
    // visitBlockOpeningStructure(o);
    // visitFoldable(o);
  }

  public void visitPsiNameIdentifierOwner(@NotNull PsiNameIdentifierOwner o) {
    visitElement(o);
  }

  public void visitArgument(@NotNull RobotArgument o) {
    visitPsiElement(o);
  }

  public void visitElement(@NotNull RobotElement o) {
    visitPsiElement(o);
  }

  public void visitFoldable(@NotNull RobotFoldable o) {
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
