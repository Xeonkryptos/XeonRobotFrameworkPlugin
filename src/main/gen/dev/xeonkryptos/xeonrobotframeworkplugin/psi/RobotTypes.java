// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotDictVariableStubElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotImportArgumentStubElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotKeywordCallStubElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotListVariableStubElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotScalarVariableStubElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotTaskStatementStubElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotTestCaseStatementStubElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotUserKeywordStubElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotVariableDefinitionStubElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl.*;

public interface RobotTypes {

  IElementType BDD_STATEMENT = new RobotElementType("BDD_STATEMENT");
  IElementType COMMENTS_SECTION = new RobotElementType("COMMENTS_SECTION");
  IElementType CONDITIONAL_CONTENT = new RobotElementType("CONDITIONAL_CONTENT");
  IElementType CONDITIONAL_STRUCTURE = new RobotElementType("CONDITIONAL_STRUCTURE");
  IElementType DICT_VARIABLE = RobotDictVariableStubElement.create("DICT_VARIABLE");
  IElementType DOCUMENTATION_STATEMENT_GLOBAL_SETTING = new RobotElementType("DOCUMENTATION_STATEMENT_GLOBAL_SETTING");
  IElementType ELSE_IF_STRUCTURE = new RobotElementType("ELSE_IF_STRUCTURE");
  IElementType ELSE_STRUCTURE = new RobotElementType("ELSE_STRUCTURE");
  IElementType EMPTY_VARIABLE_STATEMENT = new RobotElementType("EMPTY_VARIABLE_STATEMENT");
  IElementType ENVIRONMENT_VARIABLE = new RobotElementType("ENVIRONMENT_VARIABLE");
  IElementType EXCEPT_STRUCTURE = new RobotElementType("EXCEPT_STRUCTURE");
  IElementType EXECUTABLE_STATEMENT = new RobotElementType("EXECUTABLE_STATEMENT");
  IElementType FINALLY_STRUCTURE = new RobotElementType("FINALLY_STRUCTURE");
  IElementType FOR_LOOP_STRUCTURE = new RobotElementType("FOR_LOOP_STRUCTURE");
  IElementType FOR_LOOP_STRUCTURE_FILL_PARAMETER = new RobotElementType("FOR_LOOP_STRUCTURE_FILL_PARAMETER");
  IElementType FOR_LOOP_STRUCTURE_MODE_PARAMETER = new RobotElementType("FOR_LOOP_STRUCTURE_MODE_PARAMETER");
  IElementType FOR_LOOP_STRUCTURE_PARAMETER = new RobotElementType("FOR_LOOP_STRUCTURE_PARAMETER");
  IElementType FOR_LOOP_STRUCTURE_START_PARAMETER = new RobotElementType("FOR_LOOP_STRUCTURE_START_PARAMETER");
  IElementType GLOBAL_SETTING_STATEMENT = new RobotElementType("GLOBAL_SETTING_STATEMENT");
  IElementType GROUP_STRUCTURE = new RobotElementType("GROUP_STRUCTURE");
  IElementType IF_STRUCTURE = new RobotElementType("IF_STRUCTURE");
  IElementType IF_VARIABLE_STATEMENT = new RobotElementType("IF_VARIABLE_STATEMENT");
  IElementType IMPORT_ARGUMENT = RobotImportArgumentStubElement.create("IMPORT_ARGUMENT");
  IElementType INLINE_ELSE_IF_STRUCTURE = new RobotElementType("INLINE_ELSE_IF_STRUCTURE");
  IElementType INLINE_IF_ELSE_STRUCTURE = new RobotElementType("INLINE_IF_ELSE_STRUCTURE");
  IElementType INLINE_IF_STRUCTURE = new RobotElementType("INLINE_IF_STRUCTURE");
  IElementType INLINE_VARIABLE_STATEMENT = new RobotElementType("INLINE_VARIABLE_STATEMENT");
  IElementType KEYWORDS_SECTION = new RobotElementType("KEYWORDS_SECTION");
  IElementType KEYWORD_CALL = RobotKeywordCallStubElement.create("KEYWORD_CALL");
  IElementType KEYWORD_CALL_LIBRARY = new RobotElementType("KEYWORD_CALL_LIBRARY");
  IElementType KEYWORD_CALL_LIBRARY_NAME = new RobotElementType("KEYWORD_CALL_LIBRARY_NAME");
  IElementType KEYWORD_CALL_NAME = new RobotElementType("KEYWORD_CALL_NAME");
  IElementType KEYWORD_VARIABLE_STATEMENT = new RobotElementType("KEYWORD_VARIABLE_STATEMENT");
  IElementType LIBRARY_IMPORT_GLOBAL_SETTING = new RobotElementType("LIBRARY_IMPORT_GLOBAL_SETTING");
  IElementType LIST_VARIABLE = RobotListVariableStubElement.create("LIST_VARIABLE");
  IElementType LITERAL_CONSTANT_VALUE = new RobotElementType("LITERAL_CONSTANT_VALUE");
  IElementType LOCAL_ARGUMENTS_SETTING = new RobotElementType("LOCAL_ARGUMENTS_SETTING");
  IElementType LOCAL_ARGUMENTS_SETTING_ID = new RobotElementType("LOCAL_ARGUMENTS_SETTING_ID");
  IElementType LOCAL_ARGUMENTS_SETTING_PARAMETER = new RobotElementType("LOCAL_ARGUMENTS_SETTING_PARAMETER");
  IElementType LOCAL_ARGUMENTS_SETTING_PARAMETER_MANDATORY = new RobotElementType("LOCAL_ARGUMENTS_SETTING_PARAMETER_MANDATORY");
  IElementType LOCAL_ARGUMENTS_SETTING_PARAMETER_OPTIONAL = new RobotElementType("LOCAL_ARGUMENTS_SETTING_PARAMETER_OPTIONAL");
  IElementType LOCAL_SETTING = new RobotElementType("LOCAL_SETTING");
  IElementType LOCAL_SETTING_ID = new RobotElementType("LOCAL_SETTING_ID");
  IElementType METADATA_STATEMENT_GLOBAL_SETTING = new RobotElementType("METADATA_STATEMENT_GLOBAL_SETTING");
  IElementType NEW_LIBRARY_NAME = new RobotElementType("NEW_LIBRARY_NAME");
  IElementType PARAMETER = new RobotElementType("PARAMETER");
  IElementType PARAMETER_ID = new RobotElementType("PARAMETER_ID");
  IElementType POSITIONAL_ARGUMENT = new RobotElementType("POSITIONAL_ARGUMENT");
  IElementType PYTHON_EXPRESSION = new RobotElementType("PYTHON_EXPRESSION");
  IElementType RESOURCE_IMPORT_GLOBAL_SETTING = new RobotElementType("RESOURCE_IMPORT_GLOBAL_SETTING");
  IElementType ROOT = new RobotElementType("ROOT");
  IElementType SCALAR_VARIABLE = RobotScalarVariableStubElement.create("SCALAR_VARIABLE");
  IElementType SECTION = new RobotElementType("SECTION");
  IElementType SETTINGS_SECTION = new RobotElementType("SETTINGS_SECTION");
  IElementType SETUP_TEARDOWN_STATEMENTS_GLOBAL_SETTING = new RobotElementType("SETUP_TEARDOWN_STATEMENTS_GLOBAL_SETTING");
  IElementType SINGLE_VARIABLE_STATEMENT = new RobotElementType("SINGLE_VARIABLE_STATEMENT");
  IElementType SUITE_NAME_STATEMENT_GLOBAL_SETTING = new RobotElementType("SUITE_NAME_STATEMENT_GLOBAL_SETTING");
  IElementType TAGS_STATEMENT_GLOBAL_SETTING = new RobotElementType("TAGS_STATEMENT_GLOBAL_SETTING");
  IElementType TASKS_SECTION = new RobotElementType("TASKS_SECTION");
  IElementType TASK_ID = new RobotElementType("TASK_ID");
  IElementType TASK_STATEMENT = RobotTaskStatementStubElement.create("TASK_STATEMENT");
  IElementType TEMPLATE_ARGUMENT = new RobotElementType("TEMPLATE_ARGUMENT");
  IElementType TEMPLATE_ARGUMENTS = new RobotElementType("TEMPLATE_ARGUMENTS");
  IElementType TEMPLATE_PARAMETER = new RobotElementType("TEMPLATE_PARAMETER");
  IElementType TEMPLATE_PARAMETER_ID = new RobotElementType("TEMPLATE_PARAMETER_ID");
  IElementType TEMPLATE_STATEMENTS_GLOBAL_SETTING = new RobotElementType("TEMPLATE_STATEMENTS_GLOBAL_SETTING");
  IElementType TEST_CASES_SECTION = new RobotElementType("TEST_CASES_SECTION");
  IElementType TEST_CASE_ID = new RobotElementType("TEST_CASE_ID");
  IElementType TEST_CASE_STATEMENT = RobotTestCaseStatementStubElement.create("TEST_CASE_STATEMENT");
  IElementType TIMEOUT_STATEMENTS_GLOBAL_SETTING = new RobotElementType("TIMEOUT_STATEMENTS_GLOBAL_SETTING");
  IElementType TRY_STRUCTURE = new RobotElementType("TRY_STRUCTURE");
  IElementType UNKNOWN_SETTING_STATEMENTS_GLOBAL_SETTING = new RobotElementType("UNKNOWN_SETTING_STATEMENTS_GLOBAL_SETTING");
  IElementType USER_KEYWORD_STATEMENT = RobotUserKeywordStubElement.create("USER_KEYWORD_STATEMENT");
  IElementType USER_KEYWORD_STATEMENT_ID = new RobotElementType("USER_KEYWORD_STATEMENT_ID");
  IElementType VARIABLE = new RobotElementType("VARIABLE");
  IElementType VARIABLES_IMPORT_GLOBAL_SETTING = new RobotElementType("VARIABLES_IMPORT_GLOBAL_SETTING");
  IElementType VARIABLES_SECTION = new RobotElementType("VARIABLES_SECTION");
  IElementType VARIABLE_BODY_ID = new RobotElementType("VARIABLE_BODY_ID");
  IElementType VARIABLE_CONTENT = new RobotElementType("VARIABLE_CONTENT");
  IElementType VARIABLE_DEFINITION = RobotVariableDefinitionStubElement.create("VARIABLE_DEFINITION");
  IElementType VARIABLE_INDEX_ACCESS_CONTENT = new RobotElementType("VARIABLE_INDEX_ACCESS_CONTENT");
  IElementType VARIABLE_NESTED_ACCESS_CONTENT = new RobotElementType("VARIABLE_NESTED_ACCESS_CONTENT");
  IElementType VARIABLE_SLICE_ACCESS_CONTENT = new RobotElementType("VARIABLE_SLICE_ACCESS_CONTENT");
  IElementType VARIABLE_VALUE = new RobotElementType("VARIABLE_VALUE");
  IElementType WHILE_LOOP_STRUCTURE = new RobotElementType("WHILE_LOOP_STRUCTURE");

  IElementType AND = new RobotTokenType("AND");
  IElementType ASSIGNMENT = new RobotTokenType("ASSIGNMENT");
  IElementType BREAK = new RobotTokenType("BREAK");
  IElementType BUT = new RobotTokenType("BUT");
  IElementType COMMENT = new RobotTokenType("COMMENT");
  IElementType COMMENTS_HEADER = new RobotTokenType("COMMENTS_HEADER");
  IElementType CONTINUE = new RobotTokenType("CONTINUE");
  IElementType DICT_VARIABLE_START = new RobotTokenType("DICT_VARIABLE_START");
  IElementType DOCUMENTATION_KEYWORD = new RobotTokenType("DOCUMENTATION_KEYWORD");
  IElementType ELSE = new RobotTokenType("ELSE");
  IElementType ELSE_IF = new RobotTokenType("ELSE_IF");
  IElementType END = new RobotTokenType("END");
  IElementType ENV_VARIABLE_START = new RobotTokenType("ENV_VARIABLE_START");
  IElementType EOL = new RobotTokenType("EOL");
  IElementType EOS = new RobotTokenType("EOS");
  IElementType EXCEPT = new RobotTokenType("EXCEPT");
  IElementType FINALLY = new RobotTokenType("FINALLY");
  IElementType FOR = new RobotTokenType("FOR");
  IElementType FOR_IN = new RobotTokenType("FOR_IN");
  IElementType GIVEN = new RobotTokenType("GIVEN");
  IElementType GROUP = new RobotTokenType("GROUP");
  IElementType IF = new RobotTokenType("IF");
  IElementType KEYWORD_LIBRARY_NAME = new RobotTokenType("KEYWORD_LIBRARY_NAME");
  IElementType KEYWORD_LIBRARY_SEPARATOR = new RobotTokenType("KEYWORD_LIBRARY_SEPARATOR");
  IElementType KEYWORD_NAME = new RobotTokenType("KEYWORD_NAME");
  IElementType LIBRARY_IMPORT_KEYWORD = new RobotTokenType("LIBRARY_IMPORT_KEYWORD");
  IElementType LIST_VARIABLE_START = new RobotTokenType("LIST_VARIABLE_START");
  IElementType LITERAL_CONSTANT = new RobotTokenType("LITERAL_CONSTANT");
  IElementType LOCAL_SETTING_END = new RobotTokenType("LOCAL_SETTING_END");
  IElementType LOCAL_SETTING_NAME = new RobotTokenType("LOCAL_SETTING_NAME");
  IElementType LOCAL_SETTING_START = new RobotTokenType("LOCAL_SETTING_START");
  IElementType METADATA_KEYWORD = new RobotTokenType("METADATA_KEYWORD");
  IElementType PARAMETER_NAME = new RobotTokenType("PARAMETER_NAME");
  IElementType PYTHON_EXPRESSION_CONTENT = new RobotTokenType("PYTHON_EXPRESSION_CONTENT");
  IElementType PYTHON_EXPRESSION_END = new RobotTokenType("PYTHON_EXPRESSION_END");
  IElementType PYTHON_EXPRESSION_START = new RobotTokenType("PYTHON_EXPRESSION_START");
  IElementType RESOURCE_IMPORT_KEYWORD = new RobotTokenType("RESOURCE_IMPORT_KEYWORD");
  IElementType RETURN = new RobotTokenType("RETURN");
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
  IElementType TRY = new RobotTokenType("TRY");
  IElementType UNKNOWN_SETTING_KEYWORD = new RobotTokenType("UNKNOWN_SETTING_KEYWORD");
  IElementType USER_KEYWORDS_HEADER = new RobotTokenType("USER_KEYWORDS_HEADER");
  IElementType USER_KEYWORD_NAME = new RobotTokenType("USER_KEYWORD_NAME");
  IElementType VAR = new RobotTokenType("VAR");
  IElementType VARIABLES_HEADER = new RobotTokenType("VARIABLES_HEADER");
  IElementType VARIABLES_IMPORT_KEYWORD = new RobotTokenType("VARIABLES_IMPORT_KEYWORD");
  IElementType VARIABLE_ACCESS_END = new RobotTokenType("VARIABLE_ACCESS_END");
  IElementType VARIABLE_ACCESS_START = new RobotTokenType("VARIABLE_ACCESS_START");
  IElementType VARIABLE_BODY = new RobotTokenType("VARIABLE_BODY");
  IElementType VARIABLE_END = new RobotTokenType("VARIABLE_END");
  IElementType VARIABLE_INDEX_ACCESS = new RobotTokenType("VARIABLE_INDEX_ACCESS");
  IElementType VARIABLE_SLICE_ACCESS = new RobotTokenType("VARIABLE_SLICE_ACCESS");
  IElementType WHEN = new RobotTokenType("WHEN");
  IElementType WHILE = new RobotTokenType("WHILE");
  IElementType WITH_NAME = new RobotTokenType("WITH_NAME");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == BDD_STATEMENT) {
        return new RobotBddStatementImpl(node);
      }
      else if (type == COMMENTS_SECTION) {
        return new RobotCommentsSectionImpl(node);
      }
      else if (type == CONDITIONAL_CONTENT) {
        return new RobotConditionalContentImpl(node);
      }
      else if (type == CONDITIONAL_STRUCTURE) {
        return new RobotConditionalStructureImpl(node);
      }
      else if (type == DICT_VARIABLE) {
        return new RobotDictVariableImpl(node);
      }
      else if (type == DOCUMENTATION_STATEMENT_GLOBAL_SETTING) {
        return new RobotDocumentationStatementGlobalSettingImpl(node);
      }
      else if (type == ELSE_IF_STRUCTURE) {
        return new RobotElseIfStructureImpl(node);
      }
      else if (type == ELSE_STRUCTURE) {
        return new RobotElseStructureImpl(node);
      }
      else if (type == EMPTY_VARIABLE_STATEMENT) {
        return new RobotEmptyVariableStatementImpl(node);
      }
      else if (type == ENVIRONMENT_VARIABLE) {
        return new RobotEnvironmentVariableImpl(node);
      }
      else if (type == EXCEPT_STRUCTURE) {
        return new RobotExceptStructureImpl(node);
      }
      else if (type == EXECUTABLE_STATEMENT) {
        return new RobotExecutableStatementImpl(node);
      }
      else if (type == FINALLY_STRUCTURE) {
        return new RobotFinallyStructureImpl(node);
      }
      else if (type == FOR_LOOP_STRUCTURE) {
        return new RobotForLoopStructureImpl(node);
      }
      else if (type == FOR_LOOP_STRUCTURE_FILL_PARAMETER) {
        return new RobotForLoopStructureFillParameterImpl(node);
      }
      else if (type == FOR_LOOP_STRUCTURE_MODE_PARAMETER) {
        return new RobotForLoopStructureModeParameterImpl(node);
      }
      else if (type == FOR_LOOP_STRUCTURE_PARAMETER) {
        return new RobotForLoopStructureParameterImpl(node);
      }
      else if (type == FOR_LOOP_STRUCTURE_START_PARAMETER) {
        return new RobotForLoopStructureStartParameterImpl(node);
      }
      else if (type == GLOBAL_SETTING_STATEMENT) {
        return new RobotGlobalSettingStatementImpl(node);
      }
      else if (type == GROUP_STRUCTURE) {
        return new RobotGroupStructureImpl(node);
      }
      else if (type == IF_STRUCTURE) {
        return new RobotIfStructureImpl(node);
      }
      else if (type == IF_VARIABLE_STATEMENT) {
        return new RobotIfVariableStatementImpl(node);
      }
      else if (type == IMPORT_ARGUMENT) {
        return new RobotImportArgumentImpl(node);
      }
      else if (type == INLINE_ELSE_IF_STRUCTURE) {
        return new RobotInlineElseIfStructureImpl(node);
      }
      else if (type == INLINE_IF_ELSE_STRUCTURE) {
        return new RobotInlineIfElseStructureImpl(node);
      }
      else if (type == INLINE_IF_STRUCTURE) {
        return new RobotInlineIfStructureImpl(node);
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
      else if (type == KEYWORD_CALL_LIBRARY) {
        return new RobotKeywordCallLibraryImpl(node);
      }
      else if (type == KEYWORD_CALL_LIBRARY_NAME) {
        return new RobotKeywordCallLibraryNameImpl(node);
      }
      else if (type == KEYWORD_CALL_NAME) {
        return new RobotKeywordCallNameImpl(node);
      }
      else if (type == KEYWORD_VARIABLE_STATEMENT) {
        return new RobotKeywordVariableStatementImpl(node);
      }
      else if (type == LIBRARY_IMPORT_GLOBAL_SETTING) {
        return new RobotLibraryImportGlobalSettingImpl(node);
      }
      else if (type == LIST_VARIABLE) {
        return new RobotListVariableImpl(node);
      }
      else if (type == LITERAL_CONSTANT_VALUE) {
        return new RobotLiteralConstantValueImpl(node);
      }
      else if (type == LOCAL_ARGUMENTS_SETTING) {
        return new RobotLocalArgumentsSettingImpl(node);
      }
      else if (type == LOCAL_ARGUMENTS_SETTING_ID) {
        return new RobotLocalArgumentsSettingIdImpl(node);
      }
      else if (type == LOCAL_ARGUMENTS_SETTING_PARAMETER) {
        return new RobotLocalArgumentsSettingParameterImpl(node);
      }
      else if (type == LOCAL_ARGUMENTS_SETTING_PARAMETER_MANDATORY) {
        return new RobotLocalArgumentsSettingParameterMandatoryImpl(node);
      }
      else if (type == LOCAL_ARGUMENTS_SETTING_PARAMETER_OPTIONAL) {
        return new RobotLocalArgumentsSettingParameterOptionalImpl(node);
      }
      else if (type == LOCAL_SETTING) {
        return new RobotLocalSettingImpl(node);
      }
      else if (type == LOCAL_SETTING_ID) {
        return new RobotLocalSettingIdImpl(node);
      }
      else if (type == METADATA_STATEMENT_GLOBAL_SETTING) {
        return new RobotMetadataStatementGlobalSettingImpl(node);
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
      else if (type == POSITIONAL_ARGUMENT) {
        return new RobotPositionalArgumentImpl(node);
      }
      else if (type == PYTHON_EXPRESSION) {
        return new RobotPythonExpressionImpl(node);
      }
      else if (type == RESOURCE_IMPORT_GLOBAL_SETTING) {
        return new RobotResourceImportGlobalSettingImpl(node);
      }
      else if (type == ROOT) {
        return new RobotRootImpl(node);
      }
      else if (type == SCALAR_VARIABLE) {
        return new RobotScalarVariableImpl(node);
      }
      else if (type == SETTINGS_SECTION) {
        return new RobotSettingsSectionImpl(node);
      }
      else if (type == SETUP_TEARDOWN_STATEMENTS_GLOBAL_SETTING) {
        return new RobotSetupTeardownStatementsGlobalSettingImpl(node);
      }
      else if (type == SINGLE_VARIABLE_STATEMENT) {
        return new RobotSingleVariableStatementImpl(node);
      }
      else if (type == SUITE_NAME_STATEMENT_GLOBAL_SETTING) {
        return new RobotSuiteNameStatementGlobalSettingImpl(node);
      }
      else if (type == TAGS_STATEMENT_GLOBAL_SETTING) {
        return new RobotTagsStatementGlobalSettingImpl(node);
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
      else if (type == TEMPLATE_PARAMETER_ID) {
        return new RobotTemplateParameterIdImpl(node);
      }
      else if (type == TEMPLATE_STATEMENTS_GLOBAL_SETTING) {
        return new RobotTemplateStatementsGlobalSettingImpl(node);
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
      else if (type == TIMEOUT_STATEMENTS_GLOBAL_SETTING) {
        return new RobotTimeoutStatementsGlobalSettingImpl(node);
      }
      else if (type == TRY_STRUCTURE) {
        return new RobotTryStructureImpl(node);
      }
      else if (type == UNKNOWN_SETTING_STATEMENTS_GLOBAL_SETTING) {
        return new RobotUnknownSettingStatementsGlobalSettingImpl(node);
      }
      else if (type == USER_KEYWORD_STATEMENT) {
        return new RobotUserKeywordStatementImpl(node);
      }
      else if (type == USER_KEYWORD_STATEMENT_ID) {
        return new RobotUserKeywordStatementIdImpl(node);
      }
      else if (type == VARIABLES_IMPORT_GLOBAL_SETTING) {
        return new RobotVariablesImportGlobalSettingImpl(node);
      }
      else if (type == VARIABLES_SECTION) {
        return new RobotVariablesSectionImpl(node);
      }
      else if (type == VARIABLE_BODY_ID) {
        return new RobotVariableBodyIdImpl(node);
      }
      else if (type == VARIABLE_CONTENT) {
        return new RobotVariableContentImpl(node);
      }
      else if (type == VARIABLE_DEFINITION) {
        return new RobotVariableDefinitionImpl(node);
      }
      else if (type == VARIABLE_INDEX_ACCESS_CONTENT) {
        return new RobotVariableIndexAccessContentImpl(node);
      }
      else if (type == VARIABLE_NESTED_ACCESS_CONTENT) {
        return new RobotVariableNestedAccessContentImpl(node);
      }
      else if (type == VARIABLE_SLICE_ACCESS_CONTENT) {
        return new RobotVariableSliceAccessContentImpl(node);
      }
      else if (type == VARIABLE_VALUE) {
        return new RobotVariableValueImpl(node);
      }
      else if (type == WHILE_LOOP_STRUCTURE) {
        return new RobotWhileLoopStructureImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
