// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl.*;

public interface RobotTypes {

  IElementType BRACKET_SETTING_NAME = new RobotElementType("BRACKET_SETTING_NAME");
  IElementType BRACKET_SETTING_STATEMENT = new RobotElementType("BRACKET_SETTING_STATEMENT");
  IElementType BREAK_STATEMENT = new RobotElementType("BREAK_STATEMENT");
  IElementType CELL = new RobotElementType("CELL");
  IElementType COMMENTS_HEADER = new RobotElementType("COMMENTS_HEADER");
  IElementType COMMENTS_SECTION = new RobotElementType("COMMENTS_SECTION");
  IElementType COMMENT_LINE = new RobotElementType("COMMENT_LINE");
  IElementType CONDITION = new RobotElementType("CONDITION");
  IElementType CONTINUE_STATEMENT = new RobotElementType("CONTINUE_STATEMENT");
  IElementType DICT_VARIABLE = new RobotElementType("DICT_VARIABLE");
  IElementType EMPTY_LINE = new RobotElementType("EMPTY_LINE");
  IElementType ENVIRONMENT_VARIABLE = new RobotElementType("ENVIRONMENT_VARIABLE");
  IElementType EXTENDED_FOR_SYNTAX = new RobotElementType("EXTENDED_FOR_SYNTAX");
  IElementType EXTENDED_IF_SYNTAX = new RobotElementType("EXTENDED_IF_SYNTAX");
  IElementType EXTENDED_TRY_SYNTAX = new RobotElementType("EXTENDED_TRY_SYNTAX");
  IElementType EXTENDED_WHILE_SYNTAX = new RobotElementType("EXTENDED_WHILE_SYNTAX");
  IElementType HEADER_STATEMENT = new RobotElementType("HEADER_STATEMENT");
  IElementType KEYWORDS_HEADER = new RobotElementType("KEYWORDS_HEADER");
  IElementType KEYWORDS_SECTION = new RobotElementType("KEYWORDS_SECTION");
  IElementType KEYWORD_STATEMENT = new RobotElementType("KEYWORD_STATEMENT");
  IElementType KEYWORD_STATEMENT_NAME = new RobotElementType("KEYWORD_STATEMENT_NAME");
  IElementType KEYWORD_STEP = new RobotElementType("KEYWORD_STEP");
  IElementType LIBRARY_IMPORT = new RobotElementType("LIBRARY_IMPORT");
  IElementType LINE_COMMENT = new RobotElementType("LINE_COMMENT");
  IElementType LIST_VARIABLE = new RobotElementType("LIST_VARIABLE");
  IElementType LITERAL_VALUE = new RobotElementType("LITERAL_VALUE");
  IElementType PIPE_SEPARATED_STATEMENT = new RobotElementType("PIPE_SEPARATED_STATEMENT");
  IElementType PIPE_STATEMENT = new RobotElementType("PIPE_STATEMENT");
  IElementType QUOTED_CELL = new RobotElementType("QUOTED_CELL");
  IElementType RETURN_STATEMENT = new RobotElementType("RETURN_STATEMENT");
  IElementType SCALAR_VARIABLE = new RobotElementType("SCALAR_VARIABLE");
  IElementType SECTION = new RobotElementType("SECTION");
  IElementType SETTINGS_HEADER = new RobotElementType("SETTINGS_HEADER");
  IElementType SETTINGS_SECTION = new RobotElementType("SETTINGS_SECTION");
  IElementType SETTING_STATEMENT = new RobotElementType("SETTING_STATEMENT");
  IElementType SETTING_VALUE = new RobotElementType("SETTING_VALUE");
  IElementType SIMPLE_SETTING_NAME = new RobotElementType("SIMPLE_SETTING_NAME");
  IElementType SIMPLE_SETTING_STATEMENT = new RobotElementType("SIMPLE_SETTING_STATEMENT");
  IElementType SPACE_OR_TAB = new RobotElementType("SPACE_OR_TAB");
  IElementType SPACE_SEPARATED_STATEMENT = new RobotElementType("SPACE_SEPARATED_STATEMENT");
  IElementType STEP = new RobotElementType("STEP");
  IElementType TASKS_HEADER = new RobotElementType("TASKS_HEADER");
  IElementType TASKS_SECTION = new RobotElementType("TASKS_SECTION");
  IElementType TEST_CASES_HEADER = new RobotElementType("TEST_CASES_HEADER");
  IElementType TEST_CASES_SECTION = new RobotElementType("TEST_CASES_SECTION");
  IElementType TEST_CASE_NAME = new RobotElementType("TEST_CASE_NAME");
  IElementType TEST_CASE_STATEMENT = new RobotElementType("TEST_CASE_STATEMENT");
  IElementType TEST_CASE_STEP = new RobotElementType("TEST_CASE_STEP");
  IElementType VALUE = new RobotElementType("VALUE");
  IElementType VARIABLES_HEADER = new RobotElementType("VARIABLES_HEADER");
  IElementType VARIABLES_SECTION = new RobotElementType("VARIABLES_SECTION");
  IElementType VARIABLE_DEFINITION = new RobotElementType("VARIABLE_DEFINITION");
  IElementType VARIABLE_NAME = new RobotElementType("VARIABLE_NAME");
  IElementType VARIABLE_STATEMENT = new RobotElementType("VARIABLE_STATEMENT");
  IElementType VARIABLE_VALUE = new RobotElementType("VARIABLE_VALUE");
  IElementType VAR_STATEMENT = new RobotElementType("VAR_STATEMENT");
  IElementType WHITESPACE = new RobotElementType("WHITESPACE");

  IElementType ARG = new RobotTokenType("ARG");
  IElementType BREAK = new RobotTokenType("BREAK");
  IElementType CELL_CONTENT = new RobotTokenType("CELL_CONTENT");
  IElementType COMMENTS_WORDS = new RobotTokenType("COMMENTS_WORDS");
  IElementType CONTINUE = new RobotTokenType("CONTINUE");
  IElementType DICT_VARIABLE_START = new RobotTokenType("DICT_VARIABLE_START");
  IElementType DOT = new RobotTokenType("DOT");
  IElementType ELSE = new RobotTokenType("ELSE");
  IElementType ELSE_IF = new RobotTokenType("ELSE_IF");
  IElementType END = new RobotTokenType("END");
  IElementType ENV_VARIABLE_START = new RobotTokenType("ENV_VARIABLE_START");
  IElementType EOL = new RobotTokenType("EOL");
  IElementType EQUALS = new RobotTokenType("EQUALS");
  IElementType EXCEPT = new RobotTokenType("EXCEPT");
  IElementType FINALLY = new RobotTokenType("FINALLY");
  IElementType FOR = new RobotTokenType("FOR");
  IElementType HASH = new RobotTokenType("HASH");
  IElementType IF = new RobotTokenType("IF");
  IElementType IN = new RobotTokenType("IN");
  IElementType KEYWORDS_WORDS = new RobotTokenType("KEYWORDS_WORDS");
  IElementType LBRACE = new RobotTokenType("LBRACE");
  IElementType LBRACKET = new RobotTokenType("LBRACKET");
  IElementType LIBRARY_WORDS = new RobotTokenType("LIBRARY_WORDS");
  IElementType LIST_VARIABLE_START = new RobotTokenType("LIST_VARIABLE_START");
  IElementType NAME = new RobotTokenType("NAME");
  IElementType NON_EOL = new RobotTokenType("NON_EOL");
  IElementType PIPE = new RobotTokenType("PIPE");
  IElementType QUOTED_STRING = new RobotTokenType("QUOTED_STRING");
  IElementType RBRACE = new RobotTokenType("RBRACE");
  IElementType RBRACKET = new RobotTokenType("RBRACKET");
  IElementType RETURN = new RobotTokenType("RETURN");
  IElementType SCALAR_VARIABLE_START = new RobotTokenType("SCALAR_VARIABLE_START");
  IElementType SETTINGS_WORDS = new RobotTokenType("SETTINGS_WORDS");
  IElementType SETTING_NAME_CONTENT = new RobotTokenType("SETTING_NAME_CONTENT");
  IElementType SIMPLE_NAME = new RobotTokenType("SIMPLE_NAME");
  IElementType SPACE = new RobotTokenType("SPACE");
  IElementType STAR = new RobotTokenType("STAR");
  IElementType STARS = new RobotTokenType("STARS");
  IElementType TAB = new RobotTokenType("TAB");
  IElementType TASKS_WORDS = new RobotTokenType("TASKS_WORDS");
  IElementType TESTCASES_WORDS = new RobotTokenType("TESTCASES_WORDS");
  IElementType TRY = new RobotTokenType("TRY");
  IElementType UNQUOTED_STRING = new RobotTokenType("UNQUOTED_STRING");
  IElementType VAR = new RobotTokenType("VAR");
  IElementType VARIABLES_WORDS = new RobotTokenType("VARIABLES_WORDS");
  IElementType WHILE = new RobotTokenType("WHILE");
  IElementType WITH_NAME_WORDS = new RobotTokenType("WITH_NAME_WORDS");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == BRACKET_SETTING_NAME) {
        return new RobotBracketSettingNameImpl(node);
      }
      else if (type == BRACKET_SETTING_STATEMENT) {
        return new RobotBracketSettingStatementImpl(node);
      }
      else if (type == BREAK_STATEMENT) {
        return new RobotBreakStatementImpl(node);
      }
      else if (type == CELL) {
        return new RobotCellImpl(node);
      }
      else if (type == COMMENTS_HEADER) {
        return new RobotCommentsHeaderImpl(node);
      }
      else if (type == COMMENTS_SECTION) {
        return new RobotCommentsSectionImpl(node);
      }
      else if (type == COMMENT_LINE) {
        return new RobotCommentLineImpl(node);
      }
      else if (type == CONDITION) {
        return new RobotConditionImpl(node);
      }
      else if (type == CONTINUE_STATEMENT) {
        return new RobotContinueStatementImpl(node);
      }
      else if (type == DICT_VARIABLE) {
        return new RobotDictVariableImpl(node);
      }
      else if (type == EMPTY_LINE) {
        return new RobotEmptyLineImpl(node);
      }
      else if (type == ENVIRONMENT_VARIABLE) {
        return new RobotEnvironmentVariableImpl(node);
      }
      else if (type == EXTENDED_FOR_SYNTAX) {
        return new RobotExtendedForSyntaxImpl(node);
      }
      else if (type == EXTENDED_IF_SYNTAX) {
        return new RobotExtendedIfSyntaxImpl(node);
      }
      else if (type == EXTENDED_TRY_SYNTAX) {
        return new RobotExtendedTrySyntaxImpl(node);
      }
      else if (type == EXTENDED_WHILE_SYNTAX) {
        return new RobotExtendedWhileSyntaxImpl(node);
      }
      else if (type == HEADER_STATEMENT) {
        return new RobotHeaderStatementImpl(node);
      }
      else if (type == KEYWORDS_HEADER) {
        return new RobotKeywordsHeaderImpl(node);
      }
      else if (type == KEYWORDS_SECTION) {
        return new RobotKeywordsSectionImpl(node);
      }
      else if (type == KEYWORD_STATEMENT) {
        return new RobotKeywordStatementImpl(node);
      }
      else if (type == KEYWORD_STATEMENT_NAME) {
        return new RobotKeywordStatementNameImpl(node);
      }
      else if (type == KEYWORD_STEP) {
        return new RobotKeywordStepImpl(node);
      }
      else if (type == LIBRARY_IMPORT) {
        return new RobotLibraryImportImpl(node);
      }
      else if (type == LINE_COMMENT) {
        return new RobotLineCommentImpl(node);
      }
      else if (type == LIST_VARIABLE) {
        return new RobotListVariableImpl(node);
      }
      else if (type == LITERAL_VALUE) {
        return new RobotLiteralValueImpl(node);
      }
      else if (type == PIPE_SEPARATED_STATEMENT) {
        return new RobotPipeSeparatedStatementImpl(node);
      }
      else if (type == PIPE_STATEMENT) {
        return new RobotPipeStatementImpl(node);
      }
      else if (type == QUOTED_CELL) {
        return new RobotQuotedCellImpl(node);
      }
      else if (type == RETURN_STATEMENT) {
        return new RobotReturnStatementImpl(node);
      }
      else if (type == SCALAR_VARIABLE) {
        return new RobotScalarVariableImpl(node);
      }
      else if (type == SECTION) {
        return new RobotSectionImpl(node);
      }
      else if (type == SETTINGS_HEADER) {
        return new RobotSettingsHeaderImpl(node);
      }
      else if (type == SETTINGS_SECTION) {
        return new RobotSettingsSectionImpl(node);
      }
      else if (type == SETTING_STATEMENT) {
        return new RobotSettingStatementImpl(node);
      }
      else if (type == SETTING_VALUE) {
        return new RobotSettingValueImpl(node);
      }
      else if (type == SIMPLE_SETTING_NAME) {
        return new RobotSimpleSettingNameImpl(node);
      }
      else if (type == SIMPLE_SETTING_STATEMENT) {
        return new RobotSimpleSettingStatementImpl(node);
      }
      else if (type == SPACE_OR_TAB) {
        return new RobotSpaceOrTabImpl(node);
      }
      else if (type == SPACE_SEPARATED_STATEMENT) {
        return new RobotSpaceSeparatedStatementImpl(node);
      }
      else if (type == STEP) {
        return new RobotStepImpl(node);
      }
      else if (type == TASKS_HEADER) {
        return new RobotTasksHeaderImpl(node);
      }
      else if (type == TASKS_SECTION) {
        return new RobotTasksSectionImpl(node);
      }
      else if (type == TEST_CASES_HEADER) {
        return new RobotTestCasesHeaderImpl(node);
      }
      else if (type == TEST_CASES_SECTION) {
        return new RobotTestCasesSectionImpl(node);
      }
      else if (type == TEST_CASE_NAME) {
        return new RobotTestCaseNameImpl(node);
      }
      else if (type == TEST_CASE_STATEMENT) {
        return new RobotTestCaseStatementImpl(node);
      }
      else if (type == TEST_CASE_STEP) {
        return new RobotTestCaseStepImpl(node);
      }
      else if (type == VALUE) {
        return new RobotValueImpl(node);
      }
      else if (type == VARIABLES_HEADER) {
        return new RobotVariablesHeaderImpl(node);
      }
      else if (type == VARIABLES_SECTION) {
        return new RobotVariablesSectionImpl(node);
      }
      else if (type == VARIABLE_DEFINITION) {
        return new RobotVariableDefinitionImpl(node);
      }
      else if (type == VARIABLE_NAME) {
        return new RobotVariableNameImpl(node);
      }
      else if (type == VARIABLE_STATEMENT) {
        return new RobotVariableStatementImpl(node);
      }
      else if (type == VARIABLE_VALUE) {
        return new RobotVariableValueImpl(node);
      }
      else if (type == VAR_STATEMENT) {
        return new RobotVarStatementImpl(node);
      }
      else if (type == WHITESPACE) {
        return new RobotWhitespaceImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
