// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes.*;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.*;

public class RobotSettingsSectionImpl extends RobotSectionImpl implements RobotSettingsSection {

  public RobotSettingsSectionImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull RobotVisitor visitor) {
    visitor.visitSettingsSection(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof RobotVisitor) accept((RobotVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<RobotDocumentationStatement> getDocumentationStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotDocumentationStatement.class);
  }

  @Override
  @NotNull
  public List<RobotLibraryImport> getLibraryImportList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotLibraryImport.class);
  }

  @Override
  @NotNull
  public List<RobotMetadataStatement> getMetadataStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotMetadataStatement.class);
  }

  @Override
  @NotNull
  public List<RobotResourceImport> getResourceImportList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotResourceImport.class);
  }

  @Override
  @NotNull
  public List<RobotSetupTeardownStatements> getSetupTeardownStatementsList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotSetupTeardownStatements.class);
  }

  @Override
  @NotNull
  public List<RobotSuiteNameStatement> getSuiteNameStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotSuiteNameStatement.class);
  }

  @Override
  @NotNull
  public List<RobotTagsStatement> getTagsStatementList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotTagsStatement.class);
  }

  @Override
  @NotNull
  public List<RobotTemplateStatements> getTemplateStatementsList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotTemplateStatements.class);
  }

  @Override
  @NotNull
  public List<RobotTimeoutStatements> getTimeoutStatementsList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotTimeoutStatements.class);
  }

  @Override
  @NotNull
  public List<RobotUnknownSettingStatements> getUnknownSettingStatementsList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotUnknownSettingStatements.class);
  }

  @Override
  @NotNull
  public List<RobotVariablesImport> getVariablesImportList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, RobotVariablesImport.class);
  }

}
