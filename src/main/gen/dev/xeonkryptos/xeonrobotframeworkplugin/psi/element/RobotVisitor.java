// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiNameIdentifierOwner;

public class RobotVisitor extends PsiElementVisitor {

  public void visitArgument(@NotNull RobotArgument o) {
    visitPsiElement(o);
  }

  public void visitBracketSetting(@NotNull RobotBracketSetting o) {
    visitPsiNameIdentifierOwner(o);
  }

  public void visitBracketSettingId(@NotNull RobotBracketSettingId o) {
    visitPsiNamedElement(o);
  }

  public void visitCommentsSection(@NotNull RobotCommentsSection o) {
    visitSection(o);
  }

  public void visitConstantValue(@NotNull RobotConstantValue o) {
    visitPsiElement(o);
  }

  public void visitDictVariable(@NotNull RobotDictVariable o) {
    visitVariable(o);
  }

  public void visitDocumentationStatement(@NotNull RobotDocumentationStatement o) {
    visitPsiElement(o);
  }

  public void visitEnvironmentVariable(@NotNull RobotEnvironmentVariable o) {
    visitVariable(o);
  }

  public void visitExtendedVariableIndexAccess(@NotNull RobotExtendedVariableIndexAccess o) {
    visitPsiElement(o);
  }

  public void visitExtendedVariableKeyAccess(@NotNull RobotExtendedVariableKeyAccess o) {
    visitPsiElement(o);
  }

  public void visitExtendedVariableNestedAccess(@NotNull RobotExtendedVariableNestedAccess o) {
    visitPsiElement(o);
  }

  public void visitExtendedVariableSliceAccess(@NotNull RobotExtendedVariableSliceAccess o) {
    visitPsiElement(o);
  }

  public void visitFile2(@NotNull RobotFile2 o) {
    visitPsiElement(o);
  }

  public void visitKeywordCall(@NotNull RobotKeywordCall o) {
    visitPsiNameIdentifierOwner(o);
  }

  public void visitKeywordCallId(@NotNull RobotKeywordCallId o) {
    visitPsiNamedElement(o);
  }

  public void visitKeywordStatement(@NotNull RobotKeywordStatement o) {
    visitPsiElement(o);
  }

  public void visitKeywordStatementId(@NotNull RobotKeywordStatementId o) {
    visitPsiNamedElement(o);
  }

  public void visitKeywordVariableStatement(@NotNull RobotKeywordVariableStatement o) {
    visitVariableStatement(o);
  }

  public void visitKeywordsSection(@NotNull RobotKeywordsSection o) {
    visitSection(o);
  }

  public void visitLanguage(@NotNull RobotLanguage o) {
    visitPsiNameIdentifierOwner(o);
  }

  public void visitLanguageId(@NotNull RobotLanguageId o) {
    visitPsiNamedElement(o);
  }

  public void visitLibraryImport(@NotNull RobotLibraryImport o) {
    visitPsiElement(o);
  }

  public void visitListVariable(@NotNull RobotListVariable o) {
    visitVariable(o);
  }

  public void visitMetadataStatement(@NotNull RobotMetadataStatement o) {
    visitPsiElement(o);
  }

  public void visitNewLibraryName(@NotNull RobotNewLibraryName o) {
    visitPsiElement(o);
  }

  public void visitParameter(@NotNull RobotParameter o) {
    visitPsiNameIdentifierOwner(o);
  }

  public void visitParameterId(@NotNull RobotParameterId o) {
    visitPsiNamedElement(o);
  }

  public void visitPythonExpression(@NotNull RobotPythonExpression o) {
    visitPsiElement(o);
  }

  public void visitPythonExpressionBody(@NotNull RobotPythonExpressionBody o) {
    visitPsiElement(o);
  }

  public void visitResourceImport(@NotNull RobotResourceImport o) {
    visitPsiElement(o);
  }

  public void visitScalarVariable(@NotNull RobotScalarVariable o) {
    visitVariable(o);
  }

  public void visitSection(@NotNull RobotSection o) {
    visitPsiElement(o);
  }

  public void visitSettingsSection(@NotNull RobotSettingsSection o) {
    visitSection(o);
  }

  public void visitSetupTeardownStatements(@NotNull RobotSetupTeardownStatements o) {
    visitPsiElement(o);
  }

  public void visitSingleVariableStatement(@NotNull RobotSingleVariableStatement o) {
    visitVariableStatement(o);
  }

  public void visitSuiteNameStatement(@NotNull RobotSuiteNameStatement o) {
    visitPsiElement(o);
  }

  public void visitTagsStatement(@NotNull RobotTagsStatement o) {
    visitPsiElement(o);
  }

  public void visitTaskId(@NotNull RobotTaskId o) {
    visitPsiNamedElement(o);
  }

  public void visitTaskStatement(@NotNull RobotTaskStatement o) {
    visitPsiNameIdentifierOwner(o);
  }

  public void visitTasksSection(@NotNull RobotTasksSection o) {
    visitSection(o);
  }

  public void visitTemplateArgument(@NotNull RobotTemplateArgument o) {
    visitPsiElement(o);
  }

  public void visitTemplateArguments(@NotNull RobotTemplateArguments o) {
    visitPsiElement(o);
  }

  public void visitTemplateParameter(@NotNull RobotTemplateParameter o) {
    visitPsiNameIdentifierOwner(o);
  }

  public void visitTemplateParameterArgument(@NotNull RobotTemplateParameterArgument o) {
    visitPsiElement(o);
  }

  public void visitTemplateParameterId(@NotNull RobotTemplateParameterId o) {
    visitPsiNamedElement(o);
  }

  public void visitTemplateStatements(@NotNull RobotTemplateStatements o) {
    visitPsiElement(o);
  }

  public void visitTestCaseId(@NotNull RobotTestCaseId o) {
    visitPsiNamedElement(o);
  }

  public void visitTestCaseStatement(@NotNull RobotTestCaseStatement o) {
    visitPsiNameIdentifierOwner(o);
  }

  public void visitTestCasesSection(@NotNull RobotTestCasesSection o) {
    visitSection(o);
  }

  public void visitTimeoutStatements(@NotNull RobotTimeoutStatements o) {
    visitPsiElement(o);
  }

  public void visitUnknownSettingStatementId(@NotNull RobotUnknownSettingStatementId o) {
    visitPsiNamedElement(o);
  }

  public void visitUnknownSettingStatements(@NotNull RobotUnknownSettingStatements o) {
    visitPsiNameIdentifierOwner(o);
  }

  public void visitVariable(@NotNull RobotVariable o) {
    visitPsiElement(o);
  }

  public void visitVariableId(@NotNull RobotVariableId o) {
    visitPsiNamedElement(o);
  }

  public void visitVariableStatement(@NotNull RobotVariableStatement o) {
    visitPsiElement(o);
  }

  public void visitVariableValue(@NotNull RobotVariableValue o) {
    visitPsiElement(o);
  }

  public void visitVariablesImport(@NotNull RobotVariablesImport o) {
    visitPsiElement(o);
  }

  public void visitVariablesSection(@NotNull RobotVariablesSection o) {
    visitSection(o);
  }

  public void visitPsiNameIdentifierOwner(@NotNull PsiNameIdentifierOwner o) {
    visitElement(o);
  }

  public void visitPsiNamedElement(@NotNull PsiNamedElement o) {
    visitElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
