// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl.*;

public interface RobotTypes {

  IElementType ARGUMENT = new RobotElementType("ARGUMENT");
  IElementType BDD_STATEMENT = new RobotElementType("BDD_STATEMENT");
  IElementType BRACKET_SETTING = new RobotElementType("BRACKET_SETTING");
  IElementType BRACKET_SETTING_ID = new RobotElementType("BRACKET_SETTING_ID");
  IElementType COMMENTS_SECTION = new RobotElementType("COMMENTS_SECTION");
  IElementType CONSTANT_VALUE = new RobotElementType("CONSTANT_VALUE");
  IElementType DICT_VARIABLE = new RobotElementType("DICT_VARIABLE");
  IElementType DOCUMENTATION_STATEMENT = new RobotElementType("DOCUMENTATION_STATEMENT");
  IElementType ENVIRONMENT_VARIABLE = new RobotElementType("ENVIRONMENT_VARIABLE");
  IElementType EXTENDED_VARIABLE_INDEX_ACCESS = new RobotElementType("EXTENDED_VARIABLE_INDEX_ACCESS");
  IElementType EXTENDED_VARIABLE_KEY_ACCESS = new RobotElementType("EXTENDED_VARIABLE_KEY_ACCESS");
  IElementType EXTENDED_VARIABLE_NESTED_ACCESS = new RobotElementType("EXTENDED_VARIABLE_NESTED_ACCESS");
  IElementType EXTENDED_VARIABLE_SLICE_ACCESS = new RobotElementType("EXTENDED_VARIABLE_SLICE_ACCESS");
  IElementType FILE_2 = new RobotElementType("FILE_2");
  IElementType INLINE_VARIABLE_STATEMENT = new RobotElementType("INLINE_VARIABLE_STATEMENT");
  IElementType KEYWORDS_SECTION = new RobotElementType("KEYWORDS_SECTION");
  IElementType KEYWORD_CALL = new RobotElementType("KEYWORD_CALL");
  IElementType KEYWORD_CALL_ID = new RobotElementType("KEYWORD_CALL_ID");
  IElementType KEYWORD_STATEMENT = new RobotElementType("KEYWORD_STATEMENT");
  IElementType KEYWORD_STATEMENT_ID = new RobotElementType("KEYWORD_STATEMENT_ID");
  IElementType KEYWORD_VARIABLE_STATEMENT = new RobotElementType("KEYWORD_VARIABLE_STATEMENT");
  IElementType LANGUAGE = new RobotElementType("LANGUAGE");
  IElementType LANGUAGE_ID = new RobotElementType("LANGUAGE_ID");
  IElementType LIBRARY_IMPORT = new RobotElementType("LIBRARY_IMPORT");
  IElementType LIST_VARIABLE = new RobotElementType("LIST_VARIABLE");
  IElementType METADATA_STATEMENT = new RobotElementType("METADATA_STATEMENT");
  IElementType NEW_LIBRARY_NAME = new RobotElementType("NEW_LIBRARY_NAME");
  IElementType PARAMETER = new RobotElementType("PARAMETER");
  IElementType PARAMETER_ID = new RobotElementType("PARAMETER_ID");
  IElementType PYTHON_EXPRESSION = new RobotElementType("PYTHON_EXPRESSION");
  IElementType PYTHON_EXPRESSION_BODY = new RobotElementType("PYTHON_EXPRESSION_BODY");
  IElementType RESOURCE_IMPORT = new RobotElementType("RESOURCE_IMPORT");
  IElementType SCALAR_VARIABLE = new RobotElementType("SCALAR_VARIABLE");
  IElementType SECTION = new RobotElementType("SECTION");
  IElementType SETTINGS_SECTION = new RobotElementType("SETTINGS_SECTION");
  IElementType SETUP_TEARDOWN_STATEMENTS = new RobotElementType("SETUP_TEARDOWN_STATEMENTS");
  IElementType SINGLE_VARIABLE_STATEMENT = new RobotElementType("SINGLE_VARIABLE_STATEMENT");
  IElementType SUITE_NAME_STATEMENT = new RobotElementType("SUITE_NAME_STATEMENT");
  IElementType TAGS_STATEMENT = new RobotElementType("TAGS_STATEMENT");
  IElementType TASKS_SECTION = new RobotElementType("TASKS_SECTION");
  IElementType TASK_ID = new RobotElementType("TASK_ID");
  IElementType TASK_STATEMENT = new RobotElementType("TASK_STATEMENT");
  IElementType TEMPLATE_ARGUMENT = new RobotElementType("TEMPLATE_ARGUMENT");
  IElementType TEMPLATE_ARGUMENTS = new RobotElementType("TEMPLATE_ARGUMENTS");
  IElementType TEMPLATE_PARAMETER = new RobotElementType("TEMPLATE_PARAMETER");
  IElementType TEMPLATE_PARAMETER_ARGUMENT = new RobotElementType("TEMPLATE_PARAMETER_ARGUMENT");
  IElementType TEMPLATE_PARAMETER_ID = new RobotElementType("TEMPLATE_PARAMETER_ID");
  IElementType TEMPLATE_STATEMENTS = new RobotElementType("TEMPLATE_STATEMENTS");
  IElementType TEST_CASES_SECTION = new RobotElementType("TEST_CASES_SECTION");
  IElementType TEST_CASE_ID = new RobotElementType("TEST_CASE_ID");
  IElementType TEST_CASE_STATEMENT = new RobotElementType("TEST_CASE_STATEMENT");
  IElementType TIMEOUT_STATEMENTS = new RobotElementType("TIMEOUT_STATEMENTS");
  IElementType UNKNOWN_SETTING_STATEMENTS = new RobotElementType("UNKNOWN_SETTING_STATEMENTS");
  IElementType UNKNOWN_SETTING_STATEMENT_ID = new RobotElementType("UNKNOWN_SETTING_STATEMENT_ID");
  IElementType VARIABLE = new RobotElementType("VARIABLE");
  IElementType VARIABLES_IMPORT = new RobotElementType("VARIABLES_IMPORT");
  IElementType VARIABLES_SECTION = new RobotElementType("VARIABLES_SECTION");
  IElementType VARIABLE_ID = new RobotElementType("VARIABLE_ID");
  IElementType VARIABLE_VALUE = new RobotElementType("VARIABLE_VALUE");

  IElementType AND = new RobotTokenType("AND");
  IElementType ARGUMENT_VALUE = new RobotTokenType("ARGUMENT_VALUE");
  IElementType ASSIGNMENT = new RobotTokenType("ASSIGNMENT");
  IElementType BRACKET_SETTING_NAME = new RobotTokenType("BRACKET_SETTING_NAME");
  IElementType BUT = new RobotTokenType("BUT");
  IElementType COMMENT = new RobotTokenType("COMMENT");
  IElementType COMMENTS_HEADER = new RobotTokenType("COMMENTS_HEADER");
  IElementType DICT_VARIABLE_START = new RobotTokenType("DICT_VARIABLE_START");
  IElementType DOCUMENTATION_KEYWORD = new RobotTokenType("DOCUMENTATION_KEYWORD");
  IElementType ENV_VARIABLE_START = new RobotTokenType("ENV_VARIABLE_START");
  IElementType EOL = new RobotTokenType("EOL");
  IElementType GIVEN = new RobotTokenType("GIVEN");
  IElementType KEYWORDS_HEADER = new RobotTokenType("KEYWORDS_HEADER");
  IElementType KEYWORD_NAME = new RobotTokenType("KEYWORD_NAME");
  IElementType KEYWORD_STATEMENT_NAME = new RobotTokenType("KEYWORD_STATEMENT_NAME");
  IElementType LANGUAGE_KEYWORD = new RobotTokenType("LANGUAGE_KEYWORD");
  IElementType LANGUAGE_NAME = new RobotTokenType("LANGUAGE_NAME");
  IElementType LIBRARY_IMPORT_KEYWORD = new RobotTokenType("LIBRARY_IMPORT_KEYWORD");
  IElementType LIST_VARIABLE_START = new RobotTokenType("LIST_VARIABLE_START");
  IElementType METADATA_KEYWORD = new RobotTokenType("METADATA_KEYWORD");
  IElementType PARAMETER_NAME = new RobotTokenType("PARAMETER_NAME");
  IElementType PYTHON_EXPRESSION_CONTENT = new RobotTokenType("PYTHON_EXPRESSION_CONTENT");
  IElementType PYTHON_EXPRESSION_END = new RobotTokenType("PYTHON_EXPRESSION_END");
  IElementType PYTHON_EXPRESSION_START = new RobotTokenType("PYTHON_EXPRESSION_START");
  IElementType RESOURCE_IMPORT_KEYWORD = new RobotTokenType("RESOURCE_IMPORT_KEYWORD");
  IElementType SCALAR_VARIABLE_START = new RobotTokenType("SCALAR_VARIABLE_START");
  IElementType SETTINGS_HEADER = new RobotTokenType("SETTINGS_HEADER");
  IElementType SETUP_TEARDOWN_STATEMENT_KEYWORDS = new RobotTokenType("SETUP_TEARDOWN_STATEMENT_KEYWORDS");
  IElementType SUITE_NAME_KEYWORD = new RobotTokenType("SUITE_NAME_KEYWORD");
  IElementType TAGS_KEYWORDS = new RobotTokenType("TAGS_KEYWORDS");
  IElementType TASKS_HEADER = new RobotTokenType("TASKS_HEADER");
  IElementType TASK_NAME = new RobotTokenType("TASK_NAME");
  IElementType TEMPLATE_ARGUMENT_VALUE = new RobotTokenType("TEMPLATE_ARGUMENT_VALUE");
  IElementType TEMPLATE_KEYWORDS = new RobotTokenType("TEMPLATE_KEYWORDS");
  IElementType TEMPLATE_PARAMETER_NAME = new RobotTokenType("TEMPLATE_PARAMETER_NAME");
  IElementType TEST_CASES_HEADER = new RobotTokenType("TEST_CASES_HEADER");
  IElementType TEST_CASE_NAME = new RobotTokenType("TEST_CASE_NAME");
  IElementType THEN = new RobotTokenType("THEN");
  IElementType TIMEOUT_KEYWORDS = new RobotTokenType("TIMEOUT_KEYWORDS");
  IElementType UNKNOWN_SETTING_KEYWORD = new RobotTokenType("UNKNOWN_SETTING_KEYWORD");
  IElementType VAR = new RobotTokenType("VAR");
  IElementType VARIABLES_HEADER = new RobotTokenType("VARIABLES_HEADER");
  IElementType VARIABLES_IMPORT_KEYWORD = new RobotTokenType("VARIABLES_IMPORT_KEYWORD");
  IElementType VARIABLE_ACCESS_END = new RobotTokenType("VARIABLE_ACCESS_END");
  IElementType VARIABLE_ACCESS_START = new RobotTokenType("VARIABLE_ACCESS_START");
  IElementType VARIABLE_END = new RobotTokenType("VARIABLE_END");
  IElementType VARIABLE_INDEX_ACCESS = new RobotTokenType("VARIABLE_INDEX_ACCESS");
  IElementType VARIABLE_KEY_ACCESS = new RobotTokenType("VARIABLE_KEY_ACCESS");
  IElementType VARIABLE_SLICE_ACCESS = new RobotTokenType("VARIABLE_SLICE_ACCESS");
  IElementType WHEN = new RobotTokenType("WHEN");
  IElementType WITH_NAME_KEYWORD = new RobotTokenType("WITH_NAME_KEYWORD");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == ARGUMENT) {
        return new RobotArgumentImpl(node);
      }
      else if (type == BDD_STATEMENT) {
        return new RobotBddStatementImpl(node);
      }
      else if (type == BRACKET_SETTING) {
        return new RobotBracketSettingImpl(node);
      }
      else if (type == BRACKET_SETTING_ID) {
        return new RobotBracketSettingIdImpl(node);
      }
      else if (type == COMMENTS_SECTION) {
        return new RobotCommentsSectionImpl(node);
      }
      else if (type == CONSTANT_VALUE) {
        return new RobotConstantValueImpl(node);
      }
      else if (type == DICT_VARIABLE) {
        return new RobotDictVariableImpl(node);
      }
      else if (type == DOCUMENTATION_STATEMENT) {
        return new RobotDocumentationStatementImpl(node);
      }
      else if (type == ENVIRONMENT_VARIABLE) {
        return new RobotEnvironmentVariableImpl(node);
      }
      else if (type == EXTENDED_VARIABLE_INDEX_ACCESS) {
        return new RobotExtendedVariableIndexAccessImpl(node);
      }
      else if (type == EXTENDED_VARIABLE_KEY_ACCESS) {
        return new RobotExtendedVariableKeyAccessImpl(node);
      }
      else if (type == EXTENDED_VARIABLE_NESTED_ACCESS) {
        return new RobotExtendedVariableNestedAccessImpl(node);
      }
      else if (type == EXTENDED_VARIABLE_SLICE_ACCESS) {
        return new RobotExtendedVariableSliceAccessImpl(node);
      }
      else if (type == FILE_2) {
        return new RobotFile2Impl(node);
      }
      else if (type == INLINE_VARIABLE_STATEMENT) {
        return new RobotInlineVariableStatementImpl(node);
      }
      else if (type == KEYWORDS_SECTION) {
        return new RobotKeywordsSectionImpl(node);
      }
      else if (type == KEYWORD_CALL) {
        return new RobotKeywordCallImpl(node);
      }
      else if (type == KEYWORD_CALL_ID) {
        return new RobotKeywordCallIdImpl(node);
      }
      else if (type == KEYWORD_STATEMENT) {
        return new RobotKeywordStatementImpl(node);
      }
      else if (type == KEYWORD_STATEMENT_ID) {
        return new RobotKeywordStatementIdImpl(node);
      }
      else if (type == KEYWORD_VARIABLE_STATEMENT) {
        return new RobotKeywordVariableStatementImpl(node);
      }
      else if (type == LANGUAGE) {
        return new RobotLanguageImpl(node);
      }
      else if (type == LANGUAGE_ID) {
        return new RobotLanguageIdImpl(node);
      }
      else if (type == LIBRARY_IMPORT) {
        return new RobotLibraryImportImpl(node);
      }
      else if (type == LIST_VARIABLE) {
        return new RobotListVariableImpl(node);
      }
      else if (type == METADATA_STATEMENT) {
        return new RobotMetadataStatementImpl(node);
      }
      else if (type == NEW_LIBRARY_NAME) {
        return new RobotNewLibraryNameImpl(node);
      }
      else if (type == PARAMETER) {
        return new RobotParameterImpl(node);
      }
      else if (type == PARAMETER_ID) {
        return new RobotParameterIdImpl(node);
      }
      else if (type == PYTHON_EXPRESSION) {
        return new RobotPythonExpressionImpl(node);
      }
      else if (type == PYTHON_EXPRESSION_BODY) {
        return new RobotPythonExpressionBodyImpl(node);
      }
      else if (type == RESOURCE_IMPORT) {
        return new RobotResourceImportImpl(node);
      }
      else if (type == SCALAR_VARIABLE) {
        return new RobotScalarVariableImpl(node);
      }
      else if (type == SETTINGS_SECTION) {
        return new RobotSettingsSectionImpl(node);
      }
      else if (type == SETUP_TEARDOWN_STATEMENTS) {
        return new RobotSetupTeardownStatementsImpl(node);
      }
      else if (type == SINGLE_VARIABLE_STATEMENT) {
        return new RobotSingleVariableStatementImpl(node);
      }
      else if (type == SUITE_NAME_STATEMENT) {
        return new RobotSuiteNameStatementImpl(node);
      }
      else if (type == TAGS_STATEMENT) {
        return new RobotTagsStatementImpl(node);
      }
      else if (type == TASKS_SECTION) {
        return new RobotTasksSectionImpl(node);
      }
      else if (type == TASK_ID) {
        return new RobotTaskIdImpl(node);
      }
      else if (type == TASK_STATEMENT) {
        return new RobotTaskStatementImpl(node);
      }
      else if (type == TEMPLATE_ARGUMENT) {
        return new RobotTemplateArgumentImpl(node);
      }
      else if (type == TEMPLATE_ARGUMENTS) {
        return new RobotTemplateArgumentsImpl(node);
      }
      else if (type == TEMPLATE_PARAMETER) {
        return new RobotTemplateParameterImpl(node);
      }
      else if (type == TEMPLATE_PARAMETER_ARGUMENT) {
        return new RobotTemplateParameterArgumentImpl(node);
      }
      else if (type == TEMPLATE_PARAMETER_ID) {
        return new RobotTemplateParameterIdImpl(node);
      }
      else if (type == TEMPLATE_STATEMENTS) {
        return new RobotTemplateStatementsImpl(node);
      }
      else if (type == TEST_CASES_SECTION) {
        return new RobotTestCasesSectionImpl(node);
      }
      else if (type == TEST_CASE_ID) {
        return new RobotTestCaseIdImpl(node);
      }
      else if (type == TEST_CASE_STATEMENT) {
        return new RobotTestCaseStatementImpl(node);
      }
      else if (type == TIMEOUT_STATEMENTS) {
        return new RobotTimeoutStatementsImpl(node);
      }
      else if (type == UNKNOWN_SETTING_STATEMENTS) {
        return new RobotUnknownSettingStatementsImpl(node);
      }
      else if (type == UNKNOWN_SETTING_STATEMENT_ID) {
        return new RobotUnknownSettingStatementIdImpl(node);
      }
      else if (type == VARIABLES_IMPORT) {
        return new RobotVariablesImportImpl(node);
      }
      else if (type == VARIABLES_SECTION) {
        return new RobotVariablesSectionImpl(node);
      }
      else if (type == VARIABLE_ID) {
        return new RobotVariableIdImpl(node);
      }
      else if (type == VARIABLE_VALUE) {
        return new RobotVariableValueImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
