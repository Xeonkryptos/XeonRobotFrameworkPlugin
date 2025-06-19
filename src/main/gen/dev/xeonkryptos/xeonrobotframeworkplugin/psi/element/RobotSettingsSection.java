// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RobotSettingsSection extends RobotSection {

  @NotNull
  List<RobotDocumentationStatement> getDocumentationStatementList();

  @NotNull
  List<RobotLibraryImport> getLibraryImportList();

  @NotNull
  List<RobotMetadataStatement> getMetadataStatementList();

  @NotNull
  List<RobotResourceImport> getResourceImportList();

  @NotNull
  List<RobotSetupTeardownStatements> getSetupTeardownStatementsList();

  @NotNull
  List<RobotSuiteNameStatement> getSuiteNameStatementList();

  @NotNull
  List<RobotTagsStatement> getTagsStatementList();

  @NotNull
  List<RobotTemplateStatements> getTemplateStatementsList();

  @NotNull
  List<RobotTimeoutStatements> getTimeoutStatementsList();

  @NotNull
  List<RobotUnknownSettingStatements> getUnknownSettingStatementsList();

  @NotNull
  List<RobotVariablesImport> getVariablesImportList();

}
