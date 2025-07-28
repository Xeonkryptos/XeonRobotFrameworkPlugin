// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiNamedElement;
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
  }

  public void visitCommentsSection(@NotNull RobotCommentsSection o) {
    visitSection(o);
  }

  public void visitDictVariable(@NotNull RobotDictVariable o) {
    visitVariable(o);
  }

  public void visitDocumentationStatementGlobalSetting(@NotNull RobotDocumentationStatementGlobalSetting o) {
    visitGlobalSettingStatement(o);
    // visitPsiNameIdentifierOwner(o);
  }

  public void visitEnvironmentVariable(@NotNull RobotEnvironmentVariable o) {
    visitVariable(o);
  }

  public void visitExecutableStatement(@NotNull RobotExecutableStatement o) {
    visitStatement(o);
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

  public void visitForLoopStructure(@NotNull RobotForLoopStructure o) {
    visitExecutableStatement(o);
  }

  public void visitGlobalSettingStatement(@NotNull RobotGlobalSettingStatement o) {
    visitStatement(o);
  }

  public void visitGroupStructure(@NotNull RobotGroupStructure o) {
    visitExecutableStatement(o);
  }

  public void visitIfStructure(@NotNull RobotIfStructure o) {
    visitExecutableStatement(o);
  }

  public void visitInlineVariableStatement(@NotNull RobotInlineVariableStatement o) {
    visitVariableStatement(o);
    // visitPsiNameIdentifierOwner(o);
  }

  public void visitKeywordCall(@NotNull RobotKeywordCall o) {
    visitNamedElementExpression(o);
    // visitKeywordCallExpression(o);
    // visitQualifiedNameOwner(o);
    // visitPsiNameIdentifierOwner(o);
    // visitNavigationItem(o);
    // visitStatement(o);
  }

  public void visitKeywordCallId(@NotNull RobotKeywordCallId o) {
    visitPsiNamedElement(o);
    // visitNamedElementExpression(o);
    // visitReferenceElementExpression(o);
    // visitStatement(o);
  }

  public void visitKeywordVariableStatement(@NotNull RobotKeywordVariableStatement o) {
    visitVariableStatement(o);
  }

  public void visitKeywordsSection(@NotNull RobotKeywordsSection o) {
    visitSection(o);
  }

  public void visitLanguage(@NotNull RobotLanguage o) {
    visitStatement(o);
    // visitPsiNameIdentifierOwner(o);
  }

  public void visitLanguageId(@NotNull RobotLanguageId o) {
    visitPsiNamedElement(o);
    // visitNamedElementExpression(o);
    // visitReferenceElementExpression(o);
    // visitStatement(o);
  }

  public void visitLibraryImportGlobalSetting(@NotNull RobotLibraryImportGlobalSetting o) {
    visitGlobalSettingStatement(o);
    // visitPsiNameIdentifierOwner(o);
    // visitImportGlobalSettingExpression(o);
  }

  public void visitListVariable(@NotNull RobotListVariable o) {
    visitVariable(o);
  }

  public void visitLiteralConstantValue(@NotNull RobotLiteralConstantValue o) {
    visitStatement(o);
  }

  public void visitLocalSetting(@NotNull RobotLocalSetting o) {
    visitNamedElementExpression(o);
    // visitPsiNameIdentifierOwner(o);
    // visitStatement(o);
  }

  public void visitLocalSettingArgument(@NotNull RobotLocalSettingArgument o) {
    visitPsiNameIdentifierOwner(o);
    // visitStatement(o);
  }

  public void visitLocalSettingId(@NotNull RobotLocalSettingId o) {
    visitPsiNamedElement(o);
    // visitNamedElementExpression(o);
    // visitReferenceElementExpression(o);
    // visitStatement(o);
  }

  public void visitMetadataStatementGlobalSetting(@NotNull RobotMetadataStatementGlobalSetting o) {
    visitGlobalSettingStatement(o);
    // visitPsiNameIdentifierOwner(o);
  }

  public void visitNewLibraryName(@NotNull RobotNewLibraryName o) {
    visitStatement(o);
  }

  public void visitParameter(@NotNull RobotParameter o) {
    visitPsiNameIdentifierOwner(o);
    // visitNamedElementExpression(o);
    // visitNavigationItem(o);
    // visitArgument(o);
    // visitStatement(o);
  }

  public void visitParameterId(@NotNull RobotParameterId o) {
    visitPsiNamedElement(o);
    // visitNamedElementExpression(o);
    // visitReferenceElementExpression(o);
    // visitStatement(o);
  }

  public void visitPositionalArgument(@NotNull RobotPositionalArgument o) {
    visitReferenceElementExpression(o);
    // visitNavigationItem(o);
    // visitArgument(o);
    // visitStatement(o);
  }

  public void visitPythonExpression(@NotNull RobotPythonExpression o) {
    visitStatement(o);
  }

  public void visitResourceImportGlobalSetting(@NotNull RobotResourceImportGlobalSetting o) {
    visitGlobalSettingStatement(o);
    // visitPsiNameIdentifierOwner(o);
    // visitImportGlobalSettingExpression(o);
  }

  public void visitRoot(@NotNull RobotRoot o) {
    visitStatement(o);
  }

  public void visitScalarVariable(@NotNull RobotScalarVariable o) {
    visitVariable(o);
  }

  public void visitSection(@NotNull RobotSection o) {
    visitStatement(o);
    // visitNamedElementExpression(o);
    // visitPsiNameIdentifierOwner(o);
  }

  public void visitSettingsSection(@NotNull RobotSettingsSection o) {
    visitSection(o);
  }

  public void visitSetupTeardownStatementsGlobalSetting(@NotNull RobotSetupTeardownStatementsGlobalSetting o) {
    visitGlobalSettingStatement(o);
    // visitNamedElementExpression(o);
    // visitPsiNameIdentifierOwner(o);
    // visitPsiNameIdentifierOwner(o);
  }

  public void visitSingleVariableStatement(@NotNull RobotSingleVariableStatement o) {
    visitVariableStatement(o);
    // visitPsiNameIdentifierOwner(o);
  }

  public void visitSuiteNameStatementGlobalSetting(@NotNull RobotSuiteNameStatementGlobalSetting o) {
    visitGlobalSettingStatement(o);
    // visitPsiNameIdentifierOwner(o);
  }

  public void visitTagsStatementGlobalSetting(@NotNull RobotTagsStatementGlobalSetting o) {
    visitGlobalSettingStatement(o);
    // visitPsiNameIdentifierOwner(o);
  }

  public void visitTaskId(@NotNull RobotTaskId o) {
    visitPsiNamedElement(o);
    // visitNamedElementExpression(o);
    // visitReferenceElementExpression(o);
    // visitStatement(o);
  }

  public void visitTaskStatement(@NotNull RobotTaskStatement o) {
    visitPsiNameIdentifierOwner(o);
    // visitQualifiedNameOwner(o);
    // visitNavigationItem(o);
    // visitStatement(o);
  }

  public void visitTasksSection(@NotNull RobotTasksSection o) {
    visitSection(o);
  }

  public void visitTemplateArgument(@NotNull RobotTemplateArgument o) {
    visitStatement(o);
  }

  public void visitTemplateArguments(@NotNull RobotTemplateArguments o) {
    visitStatement(o);
  }

  public void visitTemplateParameter(@NotNull RobotTemplateParameter o) {
    visitPsiNameIdentifierOwner(o);
    // visitStatement(o);
  }

  public void visitTemplateParameterArgument(@NotNull RobotTemplateParameterArgument o) {
    visitStatement(o);
  }

  public void visitTemplateParameterId(@NotNull RobotTemplateParameterId o) {
    visitPsiNamedElement(o);
    // visitNamedElementExpression(o);
    // visitReferenceElementExpression(o);
    // visitStatement(o);
  }

  public void visitTemplateStatementsGlobalSetting(@NotNull RobotTemplateStatementsGlobalSetting o) {
    visitGlobalSettingStatement(o);
    // visitPsiNameIdentifierOwner(o);
  }

  public void visitTestCaseId(@NotNull RobotTestCaseId o) {
    visitPsiNamedElement(o);
    // visitNamedElementExpression(o);
    // visitReferenceElementExpression(o);
    // visitStatement(o);
  }

  public void visitTestCaseStatement(@NotNull RobotTestCaseStatement o) {
    visitPsiNameIdentifierOwner(o);
    // visitQualifiedNameOwner(o);
    // visitNavigationItem(o);
    // visitStatement(o);
  }

  public void visitTestCasesSection(@NotNull RobotTestCasesSection o) {
    visitSection(o);
  }

  public void visitTimeoutStatementsGlobalSetting(@NotNull RobotTimeoutStatementsGlobalSetting o) {
    visitGlobalSettingStatement(o);
    // visitPsiNameIdentifierOwner(o);
  }

  public void visitTryStructure(@NotNull RobotTryStructure o) {
    visitExecutableStatement(o);
  }

  public void visitUnknownSettingStatementsGlobalSetting(@NotNull RobotUnknownSettingStatementsGlobalSetting o) {
    visitPsiNameIdentifierOwner(o);
    // visitStatement(o);
  }

  public void visitUserKeywordStatement(@NotNull RobotUserKeywordStatement o) {
    visitNamedElementExpression(o);
    // visitQualifiedNameOwner(o);
    // visitUserKeywordStatementExpression(o);
    // visitPsiNameIdentifierOwner(o);
    // visitNavigationItem(o);
    // visitStatement(o);
  }

  public void visitUserKeywordStatementId(@NotNull RobotUserKeywordStatementId o) {
    visitPsiNamedElement(o);
    // visitNamedElementExpression(o);
    // visitReferenceElementExpression(o);
    // visitStatement(o);
  }

  public void visitVariable(@NotNull RobotVariable o) {
    visitPsiNameIdentifierOwner(o);
    // visitReferenceElementExpression(o);
    // visitStatement(o);
  }

  public void visitVariableBodyId(@NotNull RobotVariableBodyId o) {
    visitReferenceElementExpression(o);
    // visitPsiNamedElement(o);
    // visitNamedElementExpression(o);
    // visitReferenceElementExpression(o);
    // visitStatement(o);
  }

  public void visitVariableContent(@NotNull RobotVariableContent o) {
    visitPsiNamedElement(o);
    // visitStatement(o);
  }

  public void visitVariableDefinition(@NotNull RobotVariableDefinition o) {
    visitPsiNameIdentifierOwner(o);
    // visitNavigationItem(o);
    // visitDefinedVariable(o);
    // visitQualifiedNameOwner(o);
    // visitStatement(o);
  }

  public void visitVariableStatement(@NotNull RobotVariableStatement o) {
    visitPsiNamedElement(o);
    // visitStatement(o);
  }

  public void visitVariableValue(@NotNull RobotVariableValue o) {
    visitStatement(o);
  }

  public void visitVariablesImportGlobalSetting(@NotNull RobotVariablesImportGlobalSetting o) {
    visitGlobalSettingStatement(o);
    // visitPsiNameIdentifierOwner(o);
    // visitImportGlobalSettingExpression(o);
  }

  public void visitVariablesSection(@NotNull RobotVariablesSection o) {
    visitSection(o);
  }

  public void visitWhileLoopStructure(@NotNull RobotWhileLoopStructure o) {
    visitExecutableStatement(o);
  }

  public void visitPsiNameIdentifierOwner(@NotNull PsiNameIdentifierOwner o) {
    visitElement(o);
  }

  public void visitPsiNamedElement(@NotNull PsiNamedElement o) {
    visitElement(o);
  }

  public void visitNamedElementExpression(@NotNull RobotNamedElementExpression o) {
    visitPsiElement(o);
  }

  public void visitReferenceElementExpression(@NotNull RobotReferenceElementExpression o) {
    visitPsiElement(o);
  }

  public void visitStatement(@NotNull RobotStatement o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
