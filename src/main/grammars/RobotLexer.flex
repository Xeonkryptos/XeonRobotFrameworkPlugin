package dev.xeonkryptos.xeonrobotframeworkplugin.psi;

import com.intellij.psi.tree.IElementType;

import java.util.Stack;

import static com.intellij.psi.TokenType.*;
import static dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes.*;
import static dev.xeonkryptos.xeonrobotframeworkplugin.psi.ExtendedRobotTypes.*;
%%

%{
  protected boolean globalTemplateEnabled = false;
  protected boolean localTemplateEnabled = false;
  protected boolean templateKeywordFound = false;

  protected int currentIndex = -1;

  protected final int[] previousStates = new int[20];

  public RobotLexer() {
      this((java.io.Reader)null);
  }

  protected void enterNewState(int newState) {
      int previousState = yystate();
      ++currentIndex;
      previousStates[currentIndex] = previousState;
      yybegin(newState);
  }

  protected void leaveState() {
      if (currentIndex >= 0) {
          int previousState = previousStates[currentIndex];
          --currentIndex;
          yybegin(previousState);
      } else {
          yybegin(YYINITIAL);
      }
  }

  private void resetInternalState() {
      currentIndex = -1;
      localTemplateEnabled = globalTemplateEnabled;
  }

  /**
   * Resests the complete lexer including the additional internal states besides the lexer states from JFlex. You need to call this method when you want to
   * reset the lexer to the initial state completely, e.g. when starting to lex a new file.
   */
  protected void resetLexer() {
      currentIndex = -1;
      localTemplateEnabled = false;
      templateKeywordFound = false;
      globalTemplateEnabled = false;
  }

  protected void handleStateChangeOnMultiLineDetection() {
      int currentState = yystate();
      if (shouldLeaveStateOnMultilineDetection(currentState)) {
          leaveState();
      } else if (currentState == SINGLE_LITERAL_CONSTANT_START) {
          yybegin(SINGLE_LITERAL_CONSTANT);
      } else if (currentState == PYTHON_EVALUATED_CONTROL_STRUCTURE_START) {
          yybegin(PYTHON_EXECUTED_CONDITION);
      }
  }

  protected boolean shouldLeaveStateOnMultilineDetection(int currentState) {
      return currentState == SINGLE_LITERAL_CONSTANT
            || currentState == PARAMETER_VALUE
            || currentState == TEMPLATE_PARAMETER_VALUE
            || currentState == PYTHON_EXECUTED_CONDITION;
  }
%}

%public
%buffer 65536
%class RobotLexer
%extends AbstractRobotLexer
%function advance
%type IElementType
%unicode

// \u00A0 => NBSP (non-breaking space)
Space = " " | \u00A0
Tab = \t
Star = "*"
EqualSign = "="

Continuation = "..."
LineCommentSign = "#"

EOL = (\r) | (\n) | (\r\n)
NON_EOL = [^\r\n]

EscapeChar = \\
EmptyValue = {EscapeChar} {Space}

NonNewlineWhitespace = [^\S\r\n]
WhitespaceIncludingNewline = \s

SpaceBasedEndMarker = {Space}{2} | {Tab}
ExtendedSpaceBasedEndMarker = {SpaceBasedEndMarker} {WhitespaceIncludingNewline}*

KeywordFinishedMarker = {SpaceBasedEndMarker} | {EOL}
ExtendedKeywordFinishedMarker = {KeywordFinishedMarker} {WhitespaceIncludingNewline}*

MultiLineContinuation = {Continuation} {SpaceBasedEndMarker}

WithNameKeyword = "WITH NAME" | "AS"

SectionSettingsWords = "Settings" | "Setting"
SectionVariablesWords = "Variables" | "Variable"
SectionTestcasesWords = "Test Cases" | "Test Case"
SectionTasksWords = "Tasks" | "Task"
SectionKeywordsWords = "Keywords" | "Keyword"
SectionCommentsWords = "Comments" | "Comment"

SectionIdentifierParts = ({Star} | {Space})*
CommentSectionIdentifier = {Star} {SectionIdentifierParts} {SectionCommentsWords} {NON_EOL}*
SettingsSectionIdentifier = {Star} {SectionIdentifierParts} {SectionSettingsWords} {NON_EOL}*
TestcaseSectionIdentifier = {Star} {SectionIdentifierParts} {SectionTestcasesWords} {NON_EOL}*
TasksSectionIdentifier = {Star} {SectionIdentifierParts} {SectionTasksWords} {NON_EOL}*
KeywordsSectionIdentifier = {Star} {SectionIdentifierParts} {SectionKeywordsWords} {NON_EOL}*
VariablesSectionIdentifier = {Star} {SectionIdentifierParts} {SectionVariablesWords} {NON_EOL}*

OpeningVariable = "{"
ClosingVariable = "}"

ScalarVariableStart = "$" {OpeningVariable}
ListVariableStart = "@" {OpeningVariable}
DictVariableStart = "&" {OpeningVariable}
EnvVariableStart = "%" {OpeningVariable}

VariableStart = ("$" | "@" | "&" | "%") {OpeningVariable}

LibraryImportKeyword = "Library"
ResourceImportKeyword = "Resource"
VariablesImportKeyword = "Variables"
NameKeyword = "Name"
DocumentationKeyword = "Documentation"
MetadataKeyword = "Metadata"
SetupTeardownKeywords = "Suite Setup" | "Suite Teardown" | "Test Setup" | "Test Teardown" | "Task Setup" | "Task Teardown"
TagsKeywords = "Test Tags" | "Force Tags" | "Default Tags" | "Keyword Tags"
TemplateKeywords = "Test Template" | "Task Template"
TimeoutKeywords = "Test Timeout" | "Task Timeout"
GenericSettingsKeyword = [\w_-]+([ ][\w_-]+)*

VariableCharNotAllowed = [^\s$@%&]
ExceptionForAllowedVariableChar = [$@%&] [^{] | {EscapeChar}{1} [\s$@%&]
AllowedEverythingButVariableChar = {VariableCharNotAllowed} | {ExceptionForAllowedVariableChar}
AllowedEverythingButVariableSeq = {AllowedEverythingButVariableChar}+

AllowedExtendedVariableAccessChar = [^\s\[\]$@%&] | {EscapeChar}{1} "[" | {EscapeChar}{1} "]" | {ExceptionForAllowedVariableChar}
AllowedExtendedVariableAccessSeq = {AllowedExtendedVariableAccessChar}+

VariableLiteralValue =   ([^}$@&%] | {ExceptionForAllowedVariableChar} | {OpeningVariable})+
LiteralValue =           [^\s]+([ ][^\s]+)*[ ]?
VariableFreeLiteralValue = {AllowedEverythingButVariableSeq} ({Space} {AllowedEverythingButVariableSeq})*
KeywordLibraryNameLiteralValue = [/*]? {VariableFreeLiteralValue} "."
EverythingButVariableValue = {AllowedEverythingButVariableSeq} ({Space} {AllowedEverythingButVariableSeq})*
ExtendedVariableAccessValue = {AllowedExtendedVariableAccessSeq}

LocalSettingKeywordStartWhitespaceFree = "["
LocalSettingKeywordEndWhitespaceFree = "]"

LocalSettingKeywordStart = {LocalSettingKeywordStartWhitespaceFree} {NonNewlineWhitespace}*
LocalSettingKeywordEnd = {NonNewlineWhitespace}* {LocalSettingKeywordEndWhitespaceFree}
LocalTemplateKeyword = {LocalSettingKeywordStart} "Template" {LocalSettingKeywordEnd}
LocalSetupTeardownKeywords = {LocalSettingKeywordStart} ("Setup" | "Teardown") {LocalSettingKeywordEnd}
LocalArgumentsKeyword = {LocalSettingKeywordStart} "Arguments" {LocalSettingKeywordEnd}
LocalSettingKeyword = {LocalSettingKeywordStart} {GenericSettingsKeyword} {LocalSettingKeywordEnd}

BuiltInNamespace = "BuiltIn."
IntraKeywordSeparator = {Space} | "_"+ ({Space} "_"+)*

// Builtin keywords accepting a keyword as an argument and every parameter after that is passed to the called keyword.
AndContinueOnFailure = "Continue" {IntraKeywordSeparator}? "On" {IntraKeywordSeparator}? "Failure"
AndIgnoreError = "Ignore" {IntraKeywordSeparator}? "Error"
AndReturnStatus = "Return" {IntraKeywordSeparator} ?"Status"
AndWarnOnFailure = "Warn" {IntraKeywordSeparator}? "On" {IntraKeywordSeparator}? "Failure"
RunKeywordCall = "Run" {IntraKeywordSeparator}? "Keyword" ({IntraKeywordSeparator}? And {IntraKeywordSeparator}? ({AndContinueOnFailure} | {AndIgnoreError} | "Return" | {AndReturnStatus} | {AndWarnOnFailure}))?

// Builtin keywords accepting a condition to decide whether to run the keyword or not. After that, working like the builtin keywords above, expecting a keyword
// to execute and its parameters
AllTestsPassed = "All" {IntraKeywordSeparator}? "Tests" {IntraKeywordSeparator}? "Passed"
AnyTestsFailed = "Any" {IntraKeywordSeparator}? "Tests" {IntraKeywordSeparator}? "Failed"
TestFailed = "Test" {IntraKeywordSeparator}? "Failed"
TestPassed = "Test" {IntraKeywordSeparator}? "Passed"
TimeoutOccurred = "Timeout" {IntraKeywordSeparator}? "Occurred"
ConditionalRunKeywordCall = "Run" {IntraKeywordSeparator}? "Keyword" {IntraKeywordSeparator}? "If" ({IntraKeywordSeparator}? ({AllTestsPassed} | {AnyTestsFailed} | {TestFailed} | {TestPassed} | {TimeoutOccurred}))?
    | "Run" {IntraKeywordSeparator}? "Keyword" {IntraKeywordSeparator}? "Unless"
    | "Run" {IntraKeywordSeparator}? "Keyword" {IntraKeywordSeparator}? "And" {IntraKeywordSeparator}? "Return" {IntraKeywordSeparator}? "If"

AssertRunKeywordCall = "Run" {IntraKeywordSeparator}? "Keyword" {IntraKeywordSeparator}? "And" {IntraKeywordSeparator}? "Expect" {IntraKeywordSeparator}? "Error"

MultiLine = {EOL}+ {NonNewlineWhitespace}* {MultiLineContinuation}

LineComment = {LineCommentSign} {NON_EOL}*

%state SETTINGS_SECTION, VARIABLES_SECTION
%state TESTCASE_NAME_DEFINITION, TESTCASE_DEFINITION, TASK_NAME_DEFINITION, TASK_DEFINITION
%state USER_KEYWORD_NAME_DEFINITION, USER_KEYWORD_DEFINITION, USER_KEYWORD_RETURN_STATEMENT
%state SETTING, SETTING_TEMPLATE_START, LOCAL_TEMPLATE_DEFINITION_START, INTERMEDIATE_TEMPLATE_CONFIGURATION, TEMPLATE_DEFINITION
%state KEYWORD_CALL, KEYWORD_ARGUMENTS, SINGLE_LITERAL_CONSTANT_START, SINGLE_LITERAL_CONSTANT
%state INLINE_VARIABLE_DEFINITION, VARIABLE_DEFINITION, VARIABLE_DEFINITION_ARGUMENTS, VARIABLE_USAGE, EXTENDED_VARIABLE_ACCESS, PYTHON_EXPRESSION
%state PARAMETER_VALUE, TEMPLATE_PARAMETER_VALUE
%state FOR_STRUCTURE, SIMPLE_CONTROL_STRUCTURE_START, FOR_STRUCTURE_LOOP_START, SIMPLE_CONTROL_STRUCTURE, FOR_STRUCTURE_LOOP
%state PYTHON_EXECUTED_CONDITION, PYTHON_EVALUATED_CONTROL_STRUCTURE_START

%xstate COMMENTS_SECTION, LITERAL_CONSTANT_ONLY, LOCAL_SETTING_DEFINITION
%xstate PARAMETER_ASSIGNMENT, TEMPLATE_PARAMETER_ASSIGNMENT
%xstate KEYWORD_LIBRARY_NAME_SEPARATOR, KEYWORD_CALL_NAME,

%%

// Define a comment when it is the only thing on the line
^ {LineComment}                                    { pushBackTrailingWhitespace(); return COMMENT; }
{LineComment}                                      { pushBackTrailingWhitespace(); return COMMENT; }

{EOL} {WhitespaceIncludingNewline}* {LineCommentSign}    { handleStateChangeOnMultiLineDetection(); yypushback(1); return WHITE_SPACE; }
{MultiLine} {WhitespaceIncludingNewline}*                { handleStateChangeOnMultiLineDetection(); return WHITE_SPACE; }

{ExtendedKeywordFinishedMarker} {LineCommentSign}  { yypushback(1); return WHITE_SPACE; }

^ {SettingsSectionIdentifier}   { resetInternalState(); yybegin(SETTINGS_SECTION); return SETTINGS_HEADER; }
^ {VariablesSectionIdentifier}  { resetInternalState(); yybegin(VARIABLES_SECTION); return VARIABLES_HEADER; }
^ {KeywordsSectionIdentifier}   { resetInternalState(); yybegin(USER_KEYWORD_NAME_DEFINITION); return USER_KEYWORDS_HEADER; }
^ {TestcaseSectionIdentifier}   { resetInternalState(); yybegin(TESTCASE_NAME_DEFINITION); return TEST_CASES_HEADER; }
^ {TasksSectionIdentifier}      { resetInternalState(); yybegin(TASK_NAME_DEFINITION); return TASKS_HEADER; }
^ {CommentSectionIdentifier}    { resetInternalState(); yybegin(COMMENTS_SECTION); return COMMENTS_HEADER; }

<VARIABLES_SECTION> {
    {ScalarVariableStart}       { enterNewState(VARIABLE_DEFINITION); return SCALAR_VARIABLE_START; }
    {ListVariableStart}         { enterNewState(VARIABLE_DEFINITION); return LIST_VARIABLE_START; }
    {DictVariableStart}         { enterNewState(VARIABLE_DEFINITION); return DICT_VARIABLE_START; }
    {EnvVariableStart}          { enterNewState(VARIABLE_DEFINITION); return ENV_VARIABLE_START; }
}

<INLINE_VARIABLE_DEFINITION> {
    {ScalarVariableStart}       { yybegin(VARIABLE_DEFINITION); return SCALAR_VARIABLE_START; }
    {ListVariableStart}         { yybegin(VARIABLE_DEFINITION); return LIST_VARIABLE_START; }
    {DictVariableStart}         { yybegin(VARIABLE_DEFINITION); return DICT_VARIABLE_START; }
    {EnvVariableStart}          { yybegin(VARIABLE_DEFINITION); return ENV_VARIABLE_START; }
}

<VARIABLE_DEFINITION> {
    {ClosingVariable}                                        { yybegin(VARIABLE_DEFINITION_ARGUMENTS); return VARIABLE_END; }
    {ClosingVariable} {NonNewlineWhitespace}* {EqualSign}    { yybegin(VARIABLE_DEFINITION_ARGUMENTS); yypushback(yylength() - 1); return VARIABLE_END; }
    {ClosingVariable} "["                                    { enterNewState(EXTENDED_VARIABLE_ACCESS); yypushback(1); return VARIABLE_END; }
    {ClosingVariable} "]"                                    { yybegin(VARIABLE_DEFINITION_ARGUMENTS); yypushback(1); return VARIABLE_END; }
    {EqualSign}                                              { yybegin(VARIABLE_DEFINITION_ARGUMENTS); return ASSIGNMENT; }
}

<VARIABLE_DEFINITION_ARGUMENTS> {
    {EqualSign} !{SpaceBasedEndMarker}              { yypushback(yylength() - 1); return ASSIGNMENT; }
    "scope" {EqualSign} !{KeywordFinishedMarker}    { yypushback(yylength() - "scope".length()); enterNewState(PARAMETER_ASSIGNMENT); return PARAMETER_NAME; }
    {VariableFreeLiteralValue}                        { return LITERAL_CONSTANT; }
}

<VARIABLE_USAGE> {
    {ClosingVariable} "["                         { leaveState(); enterNewState(EXTENDED_VARIABLE_ACCESS); yypushback(1); return VARIABLE_END; }
    {ClosingVariable} "]"                         { leaveState(); yypushback(1); return VARIABLE_END; }
    {ClosingVariable}                             { leaveState(); return VARIABLE_END; }
    {OpeningVariable} (!{ClosingVariable}{2})+    { enterNewState(PYTHON_EXPRESSION); yypushback(yylength() - 1); return PYTHON_EXPRESSION_START; }
}

<VARIABLE_DEFINITION, VARIABLE_USAGE> {
    {VariableLiteralValue} {ClosingVariable}                 { yypushback(1); return VARIABLE_BODY; }
    {VariableLiteralValue} {ClosingVariable} {EqualSign}     { yypushback(2); return VARIABLE_BODY; }
    {VariableLiteralValue} {VariableStart}                   { yypushback(2); return VARIABLE_BODY; }
}

<EXTENDED_VARIABLE_ACCESS> {
    "["                                             { return VARIABLE_ACCESS_START; }
    "]"                                             { return VARIABLE_ACCESS_END; }
    "]" ({WhitespaceIncludingNewline}+ | [^\[]{1})  {
          leaveState();
          yypushback(yylength() - 1);
          return VARIABLE_ACCESS_END;
      }
    {ExtendedVariableAccessValue}                   { return EXTENDED_VARIABLE_ACCESS_BODY; }
}

<PYTHON_EXPRESSION> {
    {ClosingVariable}{2}         { leaveState(); yypushback(1); return PYTHON_EXPRESSION_END; }
    ( [^}] | }[^}] )+            { return PYTHON_EXPRESSION_CONTENT; }
}

<SETTINGS_SECTION> {
    ^ {LibraryImportKeyword} {ExtendedKeywordFinishedMarker}             { enterNewState(SETTING); pushBackTrailingWhitespace(); return LIBRARY_IMPORT_KEYWORD; }
    ^ {ResourceImportKeyword} {ExtendedKeywordFinishedMarker}            { enterNewState(LITERAL_CONSTANT_ONLY); pushBackTrailingWhitespace(); return RESOURCE_IMPORT_KEYWORD; }
    ^ {VariablesImportKeyword} {ExtendedKeywordFinishedMarker}           { enterNewState(SETTING); pushBackTrailingWhitespace(); return VARIABLES_IMPORT_KEYWORD; }
    ^ {NameKeyword} {ExtendedKeywordFinishedMarker}                      { enterNewState(LITERAL_CONSTANT_ONLY); pushBackTrailingWhitespace(); return SUITE_NAME_KEYWORD; }
    ^ {DocumentationKeyword} {ExtendedKeywordFinishedMarker}             { enterNewState(LITERAL_CONSTANT_ONLY); pushBackTrailingWhitespace(); return DOCUMENTATION_KEYWORD; }
    ^ {MetadataKeyword} {ExtendedKeywordFinishedMarker}                  { enterNewState(SETTING); pushBackTrailingWhitespace(); return METADATA_KEYWORD; }
    ^ {SetupTeardownKeywords} {ExtendedKeywordFinishedMarker}            { enterNewState(KEYWORD_CALL); pushBackTrailingWhitespace(); return SETUP_TEARDOWN_STATEMENT_KEYWORDS; }
    ^ {TagsKeywords} {ExtendedKeywordFinishedMarker}                     { enterNewState(LITERAL_CONSTANT_ONLY); pushBackTrailingWhitespace(); return TAGS_KEYWORDS; }
    ^ {TemplateKeywords} {ExtendedKeywordFinishedMarker}                 {
          enterNewState(KEYWORD_CALL);
          pushBackTrailingWhitespace();
          globalTemplateEnabled = true;
          localTemplateEnabled = true;
          templateKeywordFound = true;
          return TEMPLATE_KEYWORDS;
      }
    ^ {TimeoutKeywords} {ExtendedKeywordFinishedMarker}                  { enterNewState(LITERAL_CONSTANT_ONLY); pushBackTrailingWhitespace(); return TIMEOUT_KEYWORDS; }
    ^ {GenericSettingsKeyword} {ExtendedKeywordFinishedMarker}           { enterNewState(LITERAL_CONSTANT_ONLY); pushBackTrailingWhitespace(); return UNKNOWN_SETTING_KEYWORD; }
}

<TESTCASE_NAME_DEFINITION>     ^ {LiteralValue}    { enterNewState(TESTCASE_DEFINITION); pushBackTrailingWhitespace(); return TEST_CASE_NAME; }
<TASK_NAME_DEFINITION>         ^ {LiteralValue}    { enterNewState(TASK_DEFINITION); pushBackTrailingWhitespace(); return TASK_NAME; }
<USER_KEYWORD_NAME_DEFINITION> ^ {LiteralValue}    { enterNewState(USER_KEYWORD_DEFINITION); pushBackTrailingWhitespace(); return USER_KEYWORD_NAME; }

<TESTCASE_DEFINITION>          ^ {LiteralValue}    { localTemplateEnabled = globalTemplateEnabled; pushBackTrailingWhitespace(); return TEST_CASE_NAME; }
<TASK_DEFINITION>              ^ {LiteralValue}    { localTemplateEnabled = globalTemplateEnabled; pushBackTrailingWhitespace(); return TASK_NAME; }
<USER_KEYWORD_DEFINITION>      ^ {LiteralValue}    { pushBackTrailingWhitespace(); return USER_KEYWORD_NAME; }

<KEYWORD_ARGUMENTS, TESTCASE_DEFINITION, TASK_DEFINITION, USER_KEYWORD_DEFINITION, VARIABLE_DEFINITION, SETTINGS_SECTION> {
    <SETTING, FOR_STRUCTURE> {
        {VariableFreeLiteralValue} {EqualSign} {
              yypushback(1);
              enterNewState(PARAMETER_ASSIGNMENT);
              return PARAMETER_NAME;
        }
        {VariableFreeLiteralValue} {EqualSign} {VariableFreeLiteralValue} {
              int assignmentPos = indexOf('=');
              yypushback(yylength() - assignmentPos);
              enterNewState(PARAMETER_ASSIGNMENT);
              return PARAMETER_NAME;
        }
    }
    {EqualSign} {KeywordFinishedMarker}           { pushBackTrailingWhitespace(); return ASSIGNMENT; }
}
<INTERMEDIATE_TEMPLATE_CONFIGURATION> {
    ({ExtendedSpaceBasedEndMarker} | ({NonNewlineWhitespace}* {MultiLine})) "NONE" {ExtendedKeywordFinishedMarker}  {
          pushBackTrailingWhitespace();
          yypushback("NONE".length());
          return WHITE_SPACE;
    }
    "NONE" {ExtendedKeywordFinishedMarker}  {
          yypushback(yylength() - "NONE".length());
          yybegin(LITERAL_CONSTANT_ONLY);
          return LITERAL_CONSTANT;
    }
}
<TEMPLATE_DEFINITION> {
    <TESTCASE_DEFINITION, TASK_DEFINITION> {
        {LocalTemplateKeyword} ({ExtendedSpaceBasedEndMarker} | ({NonNewlineWhitespace}* {MultiLine})) "NONE" {ExtendedKeywordFinishedMarker}   {
              yypushback(yylength() - 1);
              enterNewState(INTERMEDIATE_TEMPLATE_CONFIGURATION);
              enterNewState(LOCAL_SETTING_DEFINITION);
              localTemplateEnabled = false;
              return LOCAL_SETTING_START;
        }
        {LocalTemplateKeyword} {NonNewlineWhitespace}* {EOL} {WhitespaceIncludingNewline}* !{MultiLineContinuation}  {
              yypushback(yylength() - 1);
              enterNewState(LITERAL_CONSTANT_ONLY);
              enterNewState(LOCAL_SETTING_DEFINITION);
              localTemplateEnabled = false;
              return LOCAL_SETTING_START;
        }
        {LocalTemplateKeyword} ({ExtendedSpaceBasedEndMarker} | {MultiLine})   {
              yypushback(yylength() - 1);
              enterNewState(TEMPLATE_DEFINITION);
              enterNewState(SETTING_TEMPLATE_START);
              enterNewState(LOCAL_SETTING_DEFINITION);
              localTemplateEnabled = true;
              return LOCAL_SETTING_START;
        }
    }
    ^ {LiteralValue}    {
        localTemplateEnabled = globalTemplateEnabled;
        leaveState();
        pushBackTrailingWhitespace();
        return yystate() == TESTCASE_DEFINITION ? TEST_CASE_NAME : TASK_NAME;
    }
    {VariableFreeLiteralValue} {EqualSign}       {
          yypushback(1);
          enterNewState(TEMPLATE_PARAMETER_ASSIGNMENT);
          return TEMPLATE_PARAMETER_NAME;
    }
    {VariableFreeLiteralValue} {EqualSign} {VariableFreeLiteralValue}      {
          int assignmentPos = indexOf('=');
          yypushback(yylength() - assignmentPos);
          enterNewState(TEMPLATE_PARAMETER_ASSIGNMENT);
          return TEMPLATE_PARAMETER_NAME;
    }
    <TEMPLATE_PARAMETER_ASSIGNMENT>  {EqualSign}       { yybegin(TEMPLATE_PARAMETER_VALUE); return ASSIGNMENT; }
    {VariableFreeLiteralValue}                         { return TEMPLATE_ARGUMENT_VALUE; }
}
<TEMPLATE_PARAMETER_VALUE>  {VariableFreeLiteralValue} { return TEMPLATE_ARGUMENT_VALUE; }

<USER_KEYWORD_DEFINITION> {
    "RETURN" {ExtendedKeywordFinishedMarker}     {
          yypushback(yylength() - "RETURN".length());
          enterNewState(USER_KEYWORD_RETURN_STATEMENT);
          return RETURN;
    }
    {VariableFreeLiteralValue}   { enterNewState(KEYWORD_CALL); yypushback(yylength()); break; }
}
<LOCAL_SETTING_DEFINITION> {
    {LocalSettingKeywordStart}                                      { pushBackTrailingWhitespace(); return LOCAL_SETTING_START; }
    {GenericSettingsKeyword}                                        { return LOCAL_SETTING_NAME; }
    {NonNewlineWhitespace}+ {LocalSettingKeywordEndWhitespaceFree}  { yypushback(1); return WHITE_SPACE; }
    {NonNewlineWhitespace}+                                         { return WHITE_SPACE; }
    {LocalSettingKeywordEndWhitespaceFree}                          { leaveState(); return LOCAL_SETTING_END; }
}
<USER_KEYWORD_DEFINITION> {
    {LocalArgumentsKeyword} {ExtendedKeywordFinishedMarker}       {
        yypushback(yylength() - 1);
        enterNewState(INLINE_VARIABLE_DEFINITION);
        enterNewState(LOCAL_SETTING_DEFINITION);
        return LOCAL_SETTING_START;
    }
}
<TESTCASE_DEFINITION, TASK_DEFINITION> {
    <USER_KEYWORD_DEFINITION> {
        <TEMPLATE_DEFINITION> {
            {LocalSetupTeardownKeywords} {ExtendedKeywordFinishedMarker}  {
                yypushback(yylength() - 1);
                enterNewState(KEYWORD_CALL);
                enterNewState(LOCAL_SETTING_DEFINITION);
                return LOCAL_SETTING_START;
            }
            {LocalSettingKeyword} {ExtendedKeywordFinishedMarker}         {
                yypushback(yylength() - 1);
                enterNewState(LITERAL_CONSTANT_ONLY);
                enterNewState(LOCAL_SETTING_DEFINITION);
                return LOCAL_SETTING_START;
            }
        }

        "FOR" {ExtendedSpaceBasedEndMarker}         { yypushback(yylength() - "FOR".length()); enterNewState(FOR_STRUCTURE); return FOR; }
        "WHILE" {ExtendedSpaceBasedEndMarker}       { yypushback(yylength() - "WHILE".length()); enterNewState(PYTHON_EVALUATED_CONTROL_STRUCTURE_START); return WHILE; }
        "IF" {ExtendedSpaceBasedEndMarker}          { yypushback(yylength() - "IF".length()); enterNewState(PYTHON_EVALUATED_CONTROL_STRUCTURE_START); return IF; }
        "ELSE IF" {ExtendedSpaceBasedEndMarker}     { yypushback(yylength() - "ELSE IF".length()); enterNewState(PYTHON_EVALUATED_CONTROL_STRUCTURE_START); return ELSE_IF; }
        "ELSE" {ExtendedKeywordFinishedMarker}      { yypushback(yylength() - "ELSE".length()); return ELSE; }
        "TRY" {EOL}                                 { yypushback(1); return TRY; }
        "EXCEPT" {ExtendedSpaceBasedEndMarker}      { yypushback(yylength() - "EXCEPT".length()); enterNewState(SIMPLE_CONTROL_STRUCTURE_START); return EXCEPT; }
        "FINALLY" {EOL}                             { yypushback(1); return FINALLY; }
        "BREAK" {ExtendedKeywordFinishedMarker}     { yypushback(yylength() - "BREAK".length()); return BREAK; }
        "CONTINUE" {ExtendedKeywordFinishedMarker}  { yypushback(yylength() - "CONTINUE".length()); return CONTINUE; }
        "GROUP" {ExtendedKeywordFinishedMarker}     { yypushback(yylength() - "GROUP".length()); enterNewState(SIMPLE_CONTROL_STRUCTURE_START); return GROUP; }
        "END" {EOL}                                 { yypushback(1); return END; }
    }

    "GIVEN" {ExtendedSpaceBasedEndMarker}   {
          yypushback(yylength() - "GIVEN".length());
          enterNewState(KEYWORD_CALL);
          return GIVEN;
      }
    "WHEN" {ExtendedSpaceBasedEndMarker}    {
         yypushback(yylength() - "WHEN".length());
         enterNewState(KEYWORD_CALL);
         return WHEN;
     }
    "THEN" {ExtendedSpaceBasedEndMarker}    {
         yypushback(yylength() - "THEN".length());
         enterNewState(KEYWORD_CALL);
         return THEN;
     }
    "AND" {ExtendedSpaceBasedEndMarker}     {
         yypushback(yylength() - "AND".length());
         enterNewState(KEYWORD_CALL);
         return AND;
     }
    "BUT" {ExtendedSpaceBasedEndMarker}     {
         yypushback(yylength() - "BUT".length());
         enterNewState(KEYWORD_CALL);
         return BUT;
     }

    "VAR" {ExtendedSpaceBasedEndMarker}     {
          yypushback(yylength() - "VAR".length());
          enterNewState(INLINE_VARIABLE_DEFINITION);
          return VAR;
      }

    {VariableFreeLiteralValue}                {
          int nextState = localTemplateEnabled && templateKeywordFound ? TEMPLATE_DEFINITION : KEYWORD_CALL;
          enterNewState(nextState);
          yypushback(yylength());
          break;
      }
}

<FOR_STRUCTURE>  {
    "IN" {ExtendedSpaceBasedEndMarker}            { yypushback(yylength() - "IN".length()); yybegin(FOR_STRUCTURE_LOOP_START); return FOR_IN; }
    "IN ENUMERATE" {ExtendedSpaceBasedEndMarker}  { yypushback(yylength() - "IN ENUMERATE".length()); yybegin(FOR_STRUCTURE_LOOP_START); return FOR_IN; }
    "IN RANGE" {ExtendedSpaceBasedEndMarker}      { yypushback(yylength() - "IN RANGE".length()); yybegin(FOR_STRUCTURE_LOOP_START); return FOR_IN; }
    "IN ZIP" {ExtendedSpaceBasedEndMarker}        { yypushback(yylength() - "IN ZIP".length()); yybegin(FOR_STRUCTURE_LOOP_START); return FOR_IN; }
    "END" {EOL}                                   { yypushback(1); leaveState(); return END; }
}

<SIMPLE_CONTROL_STRUCTURE_START>            {ExtendedSpaceBasedEndMarker}      { yybegin(SIMPLE_CONTROL_STRUCTURE); return WHITE_SPACE; }
<FOR_STRUCTURE_LOOP_START>                  {ExtendedSpaceBasedEndMarker}      { yybegin(FOR_STRUCTURE_LOOP); return WHITE_SPACE; }
<PYTHON_EVALUATED_CONTROL_STRUCTURE_START>  {
    {ExtendedSpaceBasedEndMarker}      { yybegin(PYTHON_EXECUTED_CONDITION); return WHITE_SPACE; }
}
<PYTHON_EXECUTED_CONDITION>  {
    {EverythingButVariableValue}            { return PYTHON_EXPRESSION_CONTENT; }
    {ExtendedSpaceBasedEndMarker}           { leaveState(); return EOS; }
}

<SIMPLE_CONTROL_STRUCTURE> {
    <FOR_STRUCTURE_LOOP> {VariableFreeLiteralValue}          { return LITERAL_CONSTANT; }
    {ExtendedSpaceBasedEndMarker}                            { leaveState(); return EOL; }
}
<SETTING> {WithNameKeyword} {ExtendedSpaceBasedEndMarker}    { pushBackTrailingWhitespace(); return WITH_NAME; }

<SETTING, KEYWORD_CALL, KEYWORD_ARGUMENTS, VARIABLE_DEFINITION, USER_KEYWORD_RETURN_STATEMENT, FOR_STRUCTURE_LOOP, PYTHON_EXECUTED_CONDITION, LITERAL_CONSTANT_ONLY, VARIABLE_DEFINITION_ARGUMENTS, SETTING_TEMPLATE_START> {EOL}+   { leaveState(); return EOL; }
<TESTCASE_DEFINITION, TASK_DEFINITION, USER_KEYWORD_DEFINITION, VARIABLE_DEFINITION, TEMPLATE_DEFINITION> {EOL}+   { return EOL; }

<PARAMETER_ASSIGNMENT>  {EqualSign}       { yybegin(PARAMETER_VALUE); return ASSIGNMENT; }
<PARAMETER_VALUE>       {
    {VariableFreeLiteralValue}            { return LITERAL_CONSTANT; }
    <TEMPLATE_PARAMETER_VALUE> {
        {ExtendedKeywordFinishedMarker}   { leaveState(); yypushback(yylength()); break; }
    }
}

<SETTING_TEMPLATE_START>  {
    {KeywordLibraryNameLiteralValue} {VariableFreeLiteralValue}    {
          templateKeywordFound = true;
          int libraryNameSeparatorStart = indexOf('.');
          yypushback(yylength() - libraryNameSeparatorStart);
          yybegin(KEYWORD_LIBRARY_NAME_SEPARATOR);
          return KEYWORD_LIBRARY_NAME;
    }
    {VariableFreeLiteralValue}                                     { templateKeywordFound = true; yybegin(KEYWORD_ARGUMENTS); return KEYWORD_NAME; }
}

<KEYWORD_CALL>  {
    {RunKeywordCall}                                               { return KEYWORD_NAME; }
    {ConditionalRunKeywordCall}                                    { yybegin(PYTHON_EVALUATED_CONTROL_STRUCTURE_START); return KEYWORD_NAME; }
    {AssertRunKeywordCall}                                         { enterNewState(SINGLE_LITERAL_CONSTANT_START); return KEYWORD_NAME; }

    {BuiltInNamespace} ({RunKeywordCall} | {ConditionalRunKeywordCall} | {AssertRunKeywordCall}) {
          yypushback(yylength() - "BuiltIn".length());
          enterNewState(KEYWORD_LIBRARY_NAME_SEPARATOR);
          return KEYWORD_LIBRARY_NAME;
    }

    {KeywordLibraryNameLiteralValue} {VariableFreeLiteralValue}    {
          int libraryNameSeparatorStart = indexOf('.');
          yypushback(yylength() - libraryNameSeparatorStart);
          yybegin(KEYWORD_LIBRARY_NAME_SEPARATOR);
          return KEYWORD_LIBRARY_NAME;
    }
    {VariableFreeLiteralValue}                                     { yybegin(KEYWORD_ARGUMENTS); return KEYWORD_NAME; }
}
<KEYWORD_LIBRARY_NAME_SEPARATOR> "."                               { yybegin(KEYWORD_CALL_NAME); return KEYWORD_LIBRARY_SEPARATOR; }
<KEYWORD_CALL_NAME> {VariableFreeLiteralValue}                     { leaveState(); return KEYWORD_NAME; }

<SETTINGS_SECTION, SETTING, KEYWORD_ARGUMENTS, USER_KEYWORD_RETURN_STATEMENT, SINGLE_LITERAL_CONSTANT>  {VariableFreeLiteralValue}  { return LITERAL_CONSTANT; }

<SINGLE_LITERAL_CONSTANT_START>  {SpaceBasedEndMarker}       { yybegin(SINGLE_LITERAL_CONSTANT); return WHITE_SPACE; }
<SINGLE_LITERAL_CONSTANT>        {SpaceBasedEndMarker}       { leaveState(); return WHITE_SPACE; }

<LITERAL_CONSTANT_ONLY> {
    {NonNewlineWhitespace}+                                  { return WHITE_SPACE; }
    {ExtendedSpaceBasedEndMarker} {LineCommentSign}          { yypushback(1); return WHITE_SPACE; }
    {NonNewlineWhitespace}+ {NON_EOL}+                       { pushBackEverythingExceptLeadingWhitespace(); return WHITE_SPACE; }
    ^ {LineComment}                                          { pushBackTrailingWhitespace(); return COMMENT; }
    {LineComment}                                            { pushBackTrailingWhitespace(); return COMMENT; }
    {NON_EOL}+                                               { pushBackTrailingWhitespace(); return LITERAL_CONSTANT; }

    {EOL} {WhitespaceIncludingNewline}* {LineCommentSign}    { yypushback(1); return WHITE_SPACE; }
    {MultiLine} {WhitespaceIncludingNewline}*                { return WHITE_SPACE; }
}

<COMMENTS_SECTION> {
    ^ {SettingsSectionIdentifier}            { resetInternalState(); yybegin(SETTINGS_SECTION); pushBackTrailingWhitespace(); return SETTINGS_HEADER; }
    ^ {TestcaseSectionIdentifier}            { resetInternalState(); yybegin(TESTCASE_NAME_DEFINITION); pushBackTrailingWhitespace(); return TEST_CASES_HEADER; }
    ^ {TasksSectionIdentifier}               { resetInternalState(); yybegin(TASK_NAME_DEFINITION); pushBackTrailingWhitespace(); return TASKS_HEADER; }
    ^ {KeywordsSectionIdentifier}            { resetInternalState(); yybegin(USER_KEYWORD_NAME_DEFINITION); pushBackTrailingWhitespace(); return USER_KEYWORDS_HEADER; }
    ^ {VariablesSectionIdentifier}           { resetInternalState(); yybegin(VARIABLES_SECTION); pushBackTrailingWhitespace(); return VARIABLES_HEADER; }

    <YYINITIAL> {
        {WhitespaceIncludingNewline}+        { return WHITE_SPACE; }
        {NON_EOL}+                           { return COMMENT; }
    }
}

{ScalarVariableStart}                        { enterNewState(VARIABLE_USAGE); return SCALAR_VARIABLE_START; }
{ListVariableStart}                          { enterNewState(VARIABLE_USAGE); return LIST_VARIABLE_START; }
{DictVariableStart}                          { enterNewState(VARIABLE_USAGE); return DICT_VARIABLE_START; }
{EnvVariableStart}                           { enterNewState(VARIABLE_USAGE); return ENV_VARIABLE_START; }

{EmptyValue} {WhitespaceIncludingNewline}*   { yypushback(yylength() - 2); return LITERAL_CONSTANT; }

// Can't be combined to {WhitespaceIncludingNewline}+ because then it would override the EOL handling in various states if there is a newline followed by
// whitespace. It is just a fallback for any whitespace that is not handled in other states.
{NonNewlineWhitespace}+ | {EOL}+             { return WHITE_SPACE; }

[^] { return BAD_CHARACTER; }
