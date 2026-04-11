package dev.xeonkryptos.xeonrobotframeworkplugin.psi;

import com.intellij.psi.tree.IElementType;

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

  protected void handleStateChangeOnFakeMultilineDetection() {
      leaveState();
      int currentState = yystate();
      switch (currentState) {
          case SETTING,
               SETTING_VALUES,
               KEYWORD_CALL,
               KEYWORD_ARGUMENTS,
               VARIABLE_DEFINITION,
               USER_KEYWORD_RETURN_STATEMENT,
               FOR_STRUCTURE_LOOP_START,
               FOR_STRUCTURE_LOOP,
               WHILE_CONFIGURATION,
               LITERAL_CONSTANT_ONLY,
               VARIABLE_DEFINITION_ARGUMENTS,
               SETTING_TEMPLATE_START,
               FOR_STRUCTURE,
               PARAMETER_VALUE,
               TEMPLATE_PARAMETER_VALUE,
               SINGLE_LITERAL_CONSTANT,
               PYTHON_EXECUTED_CONDITION -> handleStateChangeOnFakeMultilineDetection();
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

MultiLineContinuation = {Continuation} ({SpaceBasedEndMarker} | {EOL})

MultiLineStart = ({EOL} {NonNewlineWhitespace}*)+
MultiLine = {MultiLineStart} ({MultiLineContinuation} {NonNewlineWhitespace}*)+

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

VariableLiteralValue =   ([^}$@&%\s] | {ExceptionForAllowedVariableChar} | {OpeningVariable})+
LiteralValue =           [^\s]+([ ][^\s]+)*[ ]?
EverythingButVariableValue = {AllowedEverythingButVariableSeq} ({Space} {AllowedEverythingButVariableSeq})*
KeywordLibraryNameLiteralValue = [/*]? {EverythingButVariableValue} "."
ExtendedVariableAccessValue = {AllowedExtendedVariableAccessSeq}

LocalSettingKeywordStartWhitespaceFree = "["
LocalSettingKeywordEndWhitespaceFree = "]"

LocalSettingKeywordStart = {LocalSettingKeywordStartWhitespaceFree} {NonNewlineWhitespace}*
LocalSettingKeywordEnd = {NonNewlineWhitespace}* {LocalSettingKeywordEndWhitespaceFree}
LocalTemplateKeyword = {LocalSettingKeywordStart} "Template" {LocalSettingKeywordEnd}
LocalSetupTeardownKeywords = {LocalSettingKeywordStart} ("Setup" | "Teardown") {LocalSettingKeywordEnd}
LocalArgumentsKeyword = {LocalSettingKeywordStart} "Arguments" {LocalSettingKeywordEnd}
LocalTagsKeyword = {LocalSettingKeywordStart} "Tags" {LocalSettingKeywordEnd}
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

SetVariableIf = "Set" {IntraKeywordSeparator}? "Variable" {IntraKeywordSeparator}? "If"
ForLoopIf = ("Continue" | "Exit") {IntraKeywordSeparator}? "For" {IntraKeywordSeparator}? "Loop" {IntraKeywordSeparator}? "If"
PassExecutionIf = "Pass" {IntraKeywordSeparator}? "Execution" {IntraKeywordSeparator}? {IntraKeywordSeparator}? "If"
ReturnFromKeywordIf = "Return" {IntraKeywordSeparator}? "From" {IntraKeywordSeparator}? "Keyword" {IntraKeywordSeparator}? "If"
SkipIf = "Skip" {IntraKeywordSeparator}? "If"

ShouldBeTrue = "Should" {IntraKeywordSeparator}? "Be" {IntraKeywordSeparator}? "True"
ShouldNotBeTrue = "Should" {IntraKeywordSeparator}? "Not" {IntraKeywordSeparator}? "Be" {IntraKeywordSeparator}? "True"

SimpleConditionalKeywordCall = {SetVariableIf} | {ForLoopIf} | {PassExecutionIf} | {ReturnFromKeywordIf} | {SkipIf} | {ShouldBeTrue} | {ShouldNotBeTrue}

RepeatKeywordCall = "Repeat" {IntraKeywordSeparator}? "Keyword"

LineComment = {LineCommentSign} {NON_EOL}*

%state SETTINGS_SECTION, VARIABLES_SECTION
%state TESTCASE_NAME_DEFINITION, TESTCASE_DEFINITION, TASK_NAME_DEFINITION, TASK_DEFINITION
%state USER_KEYWORD_NAME_DEFINITION, USER_KEYWORD_DEFINITION, USER_KEYWORD_RETURN_STATEMENT
%state SETTING, SETTING_TEMPLATE_START, LOCAL_TEMPLATE_DEFINITION_START, INTERMEDIATE_TEMPLATE_CONFIGURATION, TEMPLATE_DEFINITION
%state KEYWORD_CALL, KEYWORD_ARGUMENTS, SINGLE_LITERAL_CONSTANT_START, SINGLE_LITERAL_CONSTANT
%state INLINE_VARIABLE_DEFINITION, VARIABLE_DEFINITION, VARIABLE_DEFINITION_ARGUMENTS, VARIABLE_USAGE, EXTENDED_VARIABLE_ACCESS, PYTHON_EXPRESSION
%state PARAMETER_VALUE, TEMPLATE_PARAMETER_VALUE
%state FOR_STRUCTURE, SIMPLE_CONTROL_STRUCTURE_START, FOR_STRUCTURE_LOOP_START, SIMPLE_CONTROL_STRUCTURE, FOR_STRUCTURE_LOOP, WHILE_CONFIGURATION
%state PYTHON_EXECUTED_CONDITION, PYTHON_EVALUATED_CONTROL_STRUCTURE_START

%xstate COMMENTS_SECTION, LITERAL_CONSTANT_ONLY, SETTING_VALUES, LOCAL_SETTING_DEFINITION
%xstate NORMAL_PARAMETER_ASSIGNMENT, TEMPLATE_PARAMETER_ASSIGNMENT
%xstate KEYWORD_LIBRARY_NAME_SEPARATOR, KEYWORD_CALL_NAME, KEYWORD_LIBRARY_NAME_SEPARATOR_FOR_SPECIAL_KEYWORD
%xstate IN_CONTINUATION, AFTER_CONTINUATION, FAKE_MULTILINE, SAME_LINE_FAKE_MULTILINE, AFTER_COMMENT
%xstate VARIABLE_OPENING_BRACE

%%

^ {NonNewlineWhitespace}+ {LineComment} {WhitespaceIncludingNewline}*      { enterNewState(AFTER_COMMENT); pushBackEverythingExceptLeadingWhitespace(); return WHITE_SPACE; }
^ {LineComment}                                                            { enterNewState(AFTER_COMMENT); return COMMENT; }
{LineComment}                                                              { return COMMENT; }

<SETTING, SETTING_VALUES, VARIABLE_DEFINITION_ARGUMENTS, KEYWORD_ARGUMENTS, KEYWORD_CALL, USER_KEYWORD_RETURN_STATEMENT, FOR_STRUCTURE_LOOP_START, FOR_STRUCTURE_LOOP, WHILE_CONFIGURATION, SIMPLE_CONTROL_STRUCTURE, LITERAL_CONSTANT_ONLY, VARIABLE_DEFINITION, INLINE_VARIABLE_DEFINITION, SETTING_TEMPLATE_START> {
    {EOL} {NonNewlineWhitespace}* ({LineComment} ({EOL} {NonNewlineWhitespace}*)+)+ {MultiLineContinuation}                 { enterNewState(IN_CONTINUATION); pushBackEverythingExceptLeadingWhitespace(); return WHITE_SPACE; }
    {EOL} {NonNewlineWhitespace}* {LineComment} ({EOL} {NonNewlineWhitespace}*)+ .?                                         { enterNewState(FAKE_MULTILINE); yypushback(yylength()); break; }

    {SpaceBasedEndMarker} {NonNewlineWhitespace}* ({LineComment} ({EOL} {NonNewlineWhitespace}*)+)+ {MultiLineContinuation} { enterNewState(IN_CONTINUATION); pushBackEverythingExceptLeadingWhitespace(); return WHITE_SPACE; }
    {SpaceBasedEndMarker} {NonNewlineWhitespace}* {LineComment} ({EOL} {NonNewlineWhitespace}*)+ .?                         { pushBackEverythingExceptLeadingWhitespace(); enterNewState(SAME_LINE_FAKE_MULTILINE); return WHITE_SPACE; }

    {MultiLine} {WhitespaceIncludingNewline}*                                                                               { yypushback(yylength()); enterNewState(IN_CONTINUATION); break; }
}

<FAKE_MULTILINE, SAME_LINE_FAKE_MULTILINE>  {
    {EOL}+               { handleStateChangeOnFakeMultilineDetection(); return EOL; }
    {LineComment}        { return COMMENT; }
}
<AFTER_COMMENT>   {
    {WhitespaceIncludingNewline}+                           { leaveState(); return WHITE_SPACE; }
    {WhitespaceIncludingNewline}+ {MultiLineContinuation}   { yypushback(yylength()); leaveState(); break; }
    {LineComment}                                           { return COMMENT; }
    [^]                                                     { yypushback(yylength()); leaveState(); break; }
}

^ {SettingsSectionIdentifier}   { resetInternalState(); yybegin(SETTINGS_SECTION); return SETTINGS_HEADER; }
^ {VariablesSectionIdentifier}  { resetInternalState(); yybegin(VARIABLES_SECTION); return VARIABLES_HEADER; }
^ {KeywordsSectionIdentifier}   { resetInternalState(); yybegin(USER_KEYWORD_NAME_DEFINITION); return USER_KEYWORDS_HEADER; }
^ {TestcaseSectionIdentifier}   { resetInternalState(); yybegin(TESTCASE_NAME_DEFINITION); return TEST_CASES_HEADER; }
^ {TasksSectionIdentifier}      { resetInternalState(); yybegin(TASK_NAME_DEFINITION); return TASKS_HEADER; }
^ {CommentSectionIdentifier}    { resetInternalState(); yybegin(COMMENTS_SECTION); return COMMENTS_HEADER; }

<IN_CONTINUATION>  {
    {LineComment}                                     { return COMMENT; }
    {MultiLineStart} | {NonNewlineWhitespace}+        { return WHITE_SPACE; }
    {Continuation} {NonNewlineWhitespace}* ({EOL} {NonNewlineWhitespace}*)+ {MultiLineContinuation}    {
          yypushback(yylength() - 3);
          return CONTINUATION;
    }
    {MultiLineContinuation}     {
          int initialLength = yylength();
          pushBackTrailingWhitespace();
          int lengthAfterPushback = yylength();
          if (initialLength == lengthAfterPushback) {
              leaveState();
              handleStateChangeOnMultiLineDetection();
          } else {
              yybegin(AFTER_CONTINUATION);
          }
          return WHITE_SPACE;
    }
}
<AFTER_CONTINUATION> {
    {WhitespaceIncludingNewline}+  { leaveState(); handleStateChangeOnMultiLineDetection(); return WHITE_SPACE; }
}

<VARIABLES_SECTION> {
    {ScalarVariableStart}       { enterNewState(VARIABLE_DEFINITION); enterNewState(VARIABLE_OPENING_BRACE); yypushback(yylength() - 1); return SCALAR_VARIABLE_START; }
    {ListVariableStart}         { enterNewState(VARIABLE_DEFINITION); enterNewState(VARIABLE_OPENING_BRACE); yypushback(yylength() - 1); return LIST_VARIABLE_START; }
    {DictVariableStart}         { enterNewState(VARIABLE_DEFINITION); enterNewState(VARIABLE_OPENING_BRACE); yypushback(yylength() - 1); return DICT_VARIABLE_START; }
    {EnvVariableStart}          { enterNewState(VARIABLE_DEFINITION); enterNewState(VARIABLE_OPENING_BRACE); yypushback(yylength() - 1); return ENV_VARIABLE_START; }
}

<INLINE_VARIABLE_DEFINITION> {
    {ScalarVariableStart}       { yybegin(VARIABLE_DEFINITION); enterNewState(VARIABLE_OPENING_BRACE); yypushback(yylength() - 1); return SCALAR_VARIABLE_START; }
    {ListVariableStart}         { yybegin(VARIABLE_DEFINITION); enterNewState(VARIABLE_OPENING_BRACE); yypushback(yylength() - 1); return LIST_VARIABLE_START; }
    {DictVariableStart}         { yybegin(VARIABLE_DEFINITION); enterNewState(VARIABLE_OPENING_BRACE); yypushback(yylength() - 1); return DICT_VARIABLE_START; }
    {EnvVariableStart}          { yybegin(VARIABLE_DEFINITION); enterNewState(VARIABLE_OPENING_BRACE); yypushback(yylength() - 1); return ENV_VARIABLE_START; }
}

<VARIABLE_DEFINITION> {
    {ClosingVariable}                                        { yybegin(VARIABLE_DEFINITION_ARGUMENTS); return VARIABLE_RBRACE; }
    {ClosingVariable} {NonNewlineWhitespace}? {EqualSign}    { yybegin(VARIABLE_DEFINITION_ARGUMENTS); yypushback(yylength() - 1); return VARIABLE_RBRACE; }
    {ClosingVariable} "["                                    { enterNewState(EXTENDED_VARIABLE_ACCESS); yypushback(1); return VARIABLE_RBRACE; }
    {ClosingVariable} "]"                                    { yybegin(VARIABLE_DEFINITION_ARGUMENTS); yypushback(1); return VARIABLE_RBRACE; }
    {EqualSign}                                              { yybegin(VARIABLE_DEFINITION_ARGUMENTS); return ASSIGNMENT; }
    {EOL}+                                                   { leaveState(); return EOL; }
}

<VARIABLE_DEFINITION_ARGUMENTS> {
    {EqualSign} {NonNewlineWhitespace}* {EverythingButVariableValue}?       { yypushback(yylength() - 1); return ASSIGNMENT; }
    "scope" {EqualSign} !{KeywordFinishedMarker}                            { yypushback(yylength() - "scope".length()); enterNewState(NORMAL_PARAMETER_ASSIGNMENT); return PARAMETER_NAME; }
    {EverythingButVariableValue}                                            { return LITERAL_CONSTANT; }
    {EOL}+                                                                  { leaveState(); return EOL; }
}

<VARIABLE_USAGE> {
    {ClosingVariable} "["                         { leaveState(); enterNewState(EXTENDED_VARIABLE_ACCESS); yypushback(1); return VARIABLE_RBRACE; }
    {ClosingVariable} "]"                         { leaveState(); yypushback(1); return VARIABLE_RBRACE; }
    {ClosingVariable}                             { leaveState(); return VARIABLE_RBRACE; }
    {OpeningVariable} (!{ClosingVariable}{2})+    { enterNewState(PYTHON_EXPRESSION); yypushback(yylength() - 1); return PYTHON_EXPRESSION_START; }
}

<VARIABLE_DEFINITION, VARIABLE_USAGE> {
    {VariableLiteralValue}                 { return VARIABLE_BODY; }
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
    ^ {ResourceImportKeyword} {ExtendedKeywordFinishedMarker}            { enterNewState(SETTING_VALUES); pushBackTrailingWhitespace(); return RESOURCE_IMPORT_KEYWORD; }
    ^ {VariablesImportKeyword} {ExtendedKeywordFinishedMarker}           { enterNewState(SETTING); pushBackTrailingWhitespace(); return VARIABLES_IMPORT_KEYWORD; }
    ^ {NameKeyword} {ExtendedKeywordFinishedMarker}                      { enterNewState(SETTING_VALUES); pushBackTrailingWhitespace(); return SUITE_NAME_KEYWORD; }
    ^ {DocumentationKeyword} {ExtendedKeywordFinishedMarker}             { enterNewState(SETTING_VALUES); pushBackTrailingWhitespace(); return DOCUMENTATION_KEYWORD; }
    ^ {MetadataKeyword} {ExtendedKeywordFinishedMarker}                  { enterNewState(SETTING); pushBackTrailingWhitespace(); return METADATA_KEYWORD; }
    ^ {SetupTeardownKeywords} {ExtendedKeywordFinishedMarker}            { enterNewState(KEYWORD_CALL); pushBackTrailingWhitespace(); return SETUP_TEARDOWN_STATEMENT_KEYWORDS; }
    ^ {TagsKeywords} {ExtendedKeywordFinishedMarker}                     { enterNewState(SETTING_VALUES); pushBackTrailingWhitespace(); return TAGS_KEYWORDS; }
    ^ {TemplateKeywords} {ExtendedKeywordFinishedMarker}                 {
          enterNewState(KEYWORD_CALL);
          pushBackTrailingWhitespace();
          globalTemplateEnabled = true;
          localTemplateEnabled = true;
          templateKeywordFound = true;
          return TEMPLATE_KEYWORDS;
      }
    ^ {TimeoutKeywords} {ExtendedKeywordFinishedMarker}                  { enterNewState(SETTING_VALUES); pushBackTrailingWhitespace(); return TIMEOUT_KEYWORDS; }
    ^ {GenericSettingsKeyword} {ExtendedKeywordFinishedMarker}           { enterNewState(SETTING_VALUES); pushBackTrailingWhitespace(); return UNKNOWN_SETTING_KEYWORD; }
}

<SETTING> {
    {WithNameKeyword} {ExtendedSpaceBasedEndMarker}    { pushBackTrailingWhitespace(); return WITH_NAME; }
    {EOL}+                                             { leaveState(); return EOL; }
}

<TESTCASE_NAME_DEFINITION>     {
    {EverythingButVariableValue}    { pushBackTrailingWhitespace(); return TEST_CASE_NAME_PART; }
    {EOL}+                          { yybegin(TESTCASE_DEFINITION); return EOL; }
}
<TASK_NAME_DEFINITION>         {
    {EverythingButVariableValue}    { pushBackTrailingWhitespace(); return TASK_NAME_PART; }
    {EOL}+                          { yybegin(TASK_DEFINITION); return EOL; }
}
<USER_KEYWORD_NAME_DEFINITION> {
    {EverythingButVariableValue}    { pushBackTrailingWhitespace(); return USER_KEYWORD_NAME_PART; }
    {EOL}+                          { yybegin(USER_KEYWORD_DEFINITION); return EOL; }
}

<TESTCASE_DEFINITION>          ^ [^\s#] {NON_EOL}+ {EOL}*    { localTemplateEnabled = globalTemplateEnabled; yypushback(yylength()); yybegin(TESTCASE_NAME_DEFINITION); break; }
<TASK_DEFINITION>              ^ [^\s#] {NON_EOL}+ {EOL}*    { localTemplateEnabled = globalTemplateEnabled; yypushback(yylength()); yybegin(TASK_NAME_DEFINITION); break; }
<USER_KEYWORD_DEFINITION>      ^ [^\s#] {NON_EOL}+ {EOL}*    { yypushback(yylength()); yybegin(USER_KEYWORD_NAME_DEFINITION); break; }

<KEYWORD_ARGUMENTS, SETTINGS_SECTION> {
    <SETTING, FOR_STRUCTURE_LOOP, WHILE_CONFIGURATION> {
        {EverythingButVariableValue} {EqualSign} {
              yypushback(1);
              enterNewState(NORMAL_PARAMETER_ASSIGNMENT);
              return PARAMETER_NAME;
        }
        {EverythingButVariableValue} {EqualSign} {EverythingButVariableValue} {
              int assignmentPos = indexOf('=');
              yypushback(yylength() - assignmentPos);
              enterNewState(NORMAL_PARAMETER_ASSIGNMENT);
              return PARAMETER_NAME;
        }
    }
    <TESTCASE_DEFINITION, TASK_DEFINITION, USER_KEYWORD_DEFINITION> {
        {EqualSign} {KeywordFinishedMarker}           { pushBackTrailingWhitespace(); return ASSIGNMENT; }
    }
}
<INTERMEDIATE_TEMPLATE_CONFIGURATION> {
    {ExtendedSpaceBasedEndMarker} "NONE" {ExtendedKeywordFinishedMarker}  {
          pushBackTrailingWhitespace();
          yypushback("NONE".length());
          return WHITE_SPACE;
    }
    {NonNewlineWhitespace}* {MultiLine} "NONE" {ExtendedKeywordFinishedMarker}  {
          pushBackTrailingWhitespace();
          yypushback("NONE".length());
          pushBackTrailingWhitespace();
          yypushback("...".length());
          enterNewState(IN_CONTINUATION);
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
        // Disables the template for the current test case / task due to the NONE "keyword"
        {LocalTemplateKeyword} ({ExtendedSpaceBasedEndMarker} | {MultiLine}) "NONE" {ExtendedKeywordFinishedMarker}   {
              yypushback(yylength() - 1);
              enterNewState(INTERMEDIATE_TEMPLATE_CONFIGURATION);
              enterNewState(LOCAL_SETTING_DEFINITION);
              localTemplateEnabled = false;
              return LOCAL_SETTING_START;
        }
        // Expects a template name (keyword) following the [Template] setting. Can be on the same line or on a new line after the continuation marker
        {LocalTemplateKeyword} ({ExtendedSpaceBasedEndMarker} | {MultiLine})   {
              yypushback(yylength() - 1);
              enterNewState(TEMPLATE_DEFINITION);
              enterNewState(SETTING_TEMPLATE_START);
              enterNewState(LOCAL_SETTING_DEFINITION);
              localTemplateEnabled = true;
              return LOCAL_SETTING_START;
        }
        // Represents the [Template] configuration WITHOUT any template name to "deactivate" the template for the current test case / task.
        {LocalTemplateKeyword} {NonNewlineWhitespace}* {EOL} {
              yypushback(yylength() - 1);
              enterNewState(LITERAL_CONSTANT_ONLY);
              enterNewState(LOCAL_SETTING_DEFINITION);
              localTemplateEnabled = false;
              return LOCAL_SETTING_START;
        }
    }
    ^ [^\s#] {NON_EOL}+ {EOL}*    {
        localTemplateEnabled = globalTemplateEnabled;
        leaveState();
        yypushback(yylength());
        if (yystate() == TESTCASE_DEFINITION) {
            yybegin(TESTCASE_NAME_DEFINITION);
        } else {
            yybegin(TASK_NAME_DEFINITION);
        }
        break;
    }
    {EverythingButVariableValue} {EqualSign}       {
          yypushback(1);
          enterNewState(TEMPLATE_PARAMETER_ASSIGNMENT);
          return TEMPLATE_PARAMETER_NAME;
    }
    {EverythingButVariableValue} {EqualSign} {EverythingButVariableValue}      {
          int assignmentPos = indexOf('=');
          yypushback(yylength() - assignmentPos);
          enterNewState(TEMPLATE_PARAMETER_ASSIGNMENT);
          return TEMPLATE_PARAMETER_NAME;
    }
    <TEMPLATE_PARAMETER_ASSIGNMENT>  {EqualSign}         { yybegin(TEMPLATE_PARAMETER_VALUE); return PARAMETER_ASSIGNMENT; }
    {EverythingButVariableValue}                         { return TEMPLATE_ARGUMENT_VALUE; }
}
<TEMPLATE_PARAMETER_VALUE>  {EverythingButVariableValue} { return TEMPLATE_ARGUMENT_VALUE; }

<USER_KEYWORD_DEFINITION> {
    "RETURN" {ExtendedKeywordFinishedMarker}     {
          yypushback(yylength() - "RETURN".length());
          enterNewState(USER_KEYWORD_RETURN_STATEMENT);
          return RETURN;
    }
    {EverythingButVariableValue}   { enterNewState(KEYWORD_CALL); yypushback(yylength()); break; }
}
<LOCAL_SETTING_DEFINITION> {
    {LocalSettingKeywordStart}                                      { pushBackTrailingWhitespace(); return LOCAL_SETTING_START; }
    {GenericSettingsKeyword}                                        { return LOCAL_SETTING_NAME; }
    {NonNewlineWhitespace}+ {LocalSettingKeywordEndWhitespaceFree}  { yypushback(1); return WHITE_SPACE; }
    {NonNewlineWhitespace}+                                         { return WHITE_SPACE; }
    {LocalSettingKeywordEndWhitespaceFree}                          { leaveState(); return LOCAL_SETTING_END; }
}
<USER_KEYWORD_DEFINITION> {
    {LocalArgumentsKeyword} {ExtendedKeywordFinishedMarker}         {
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
            {LocalTagsKeyword} {ExtendedKeywordFinishedMarker}            {
                yypushback(yylength() - 1);
                enterNewState(LITERAL_CONSTANT_ONLY);
                enterNewState(LOCAL_SETTING_DEFINITION);
                return LOCAL_SETTING_START;
            }
            {LocalSettingKeyword} {ExtendedKeywordFinishedMarker}          {
                yypushback(yylength() - 1);
                enterNewState(LITERAL_CONSTANT_ONLY);
                enterNewState(LOCAL_SETTING_DEFINITION);
                return LOCAL_SETTING_START;
            }
        }

        "FOR" {ExtendedSpaceBasedEndMarker}?         { yypushback(yylength() - "FOR".length()); enterNewState(FOR_STRUCTURE); return FOR; }
        "WHILE" {ExtendedSpaceBasedEndMarker}?       { yypushback(yylength() - "WHILE".length()); enterNewState(WHILE_CONFIGURATION); enterNewState(PYTHON_EVALUATED_CONTROL_STRUCTURE_START); return WHILE; }
        "IF" {ExtendedSpaceBasedEndMarker}?          { yypushback(yylength() - "IF".length()); enterNewState(PYTHON_EVALUATED_CONTROL_STRUCTURE_START); return IF; }
        "ELSE IF" {ExtendedSpaceBasedEndMarker}?     { yypushback(yylength() - "ELSE IF".length()); enterNewState(PYTHON_EVALUATED_CONTROL_STRUCTURE_START); return ELSE_IF; }
        "ELSE" {ExtendedKeywordFinishedMarker}?      { yypushback(yylength() - "ELSE".length()); return ELSE; }
        "TRY" {EOL}?                                 { yypushback(yylength() - "TRY".length()); return TRY; }
        "EXCEPT" {ExtendedSpaceBasedEndMarker}?      {
          if (yylength() > "EXCEPT".length()) {
              enterNewState(SIMPLE_CONTROL_STRUCTURE_START);
          }
          yypushback(yylength() - "EXCEPT".length());
          return EXCEPT;
        }
        "FINALLY" {EOL}?                             { yypushback(yylength() - "FINALLY".length()); return FINALLY; }
        "BREAK" {ExtendedKeywordFinishedMarker}?     { yypushback(yylength() - "BREAK".length()); return BREAK; }
        "CONTINUE" {ExtendedKeywordFinishedMarker}?  { yypushback(yylength() - "CONTINUE".length()); return CONTINUE; }
        "GROUP" {ExtendedSpaceBasedEndMarker}?       {
          if (yylength() > "GROUP".length()) {
              enterNewState(SIMPLE_CONTROL_STRUCTURE_START);
          }
          yypushback(yylength() - "GROUP".length());
          return GROUP;
        }
        "END" {EOL}?                                 { yypushback(yylength() - "END".length()); return END; }
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

    {EverythingButVariableValue}            {
          int nextState = localTemplateEnabled && templateKeywordFound ? TEMPLATE_DEFINITION : KEYWORD_CALL;
          enterNewState(nextState);
          yypushback(yylength());
          break;
      }
}

<FOR_STRUCTURE>  {
    "IN" {ExtendedKeywordFinishedMarker}            { yypushback(yylength() - "IN".length()); yybegin(FOR_STRUCTURE_LOOP_START); return FOR_IN; }
    "IN ENUMERATE" {ExtendedKeywordFinishedMarker}  { yypushback(yylength() - "IN ENUMERATE".length()); yybegin(FOR_STRUCTURE_LOOP_START); return FOR_IN_ENUMERATE; }
    "IN RANGE" {ExtendedKeywordFinishedMarker}      { yypushback(yylength() - "IN RANGE".length()); yybegin(FOR_STRUCTURE_LOOP_START); return FOR_IN_RANGE; }
    "IN ZIP" {ExtendedKeywordFinishedMarker}        { yypushback(yylength() - "IN ZIP".length()); yybegin(FOR_STRUCTURE_LOOP_START); return FOR_IN_ZIP; }
    "END" {EOL}                                     { yypushback(1); leaveState(); return END; }
    {EOL}+                                          { leaveState(); return EOL; }
}

<SIMPLE_CONTROL_STRUCTURE_START>            {ExtendedSpaceBasedEndMarker}      { yybegin(SIMPLE_CONTROL_STRUCTURE); return WHITE_SPACE; }
<FOR_STRUCTURE_LOOP_START> {
    {ExtendedSpaceBasedEndMarker}           { yybegin(FOR_STRUCTURE_LOOP); return WHITE_SPACE; }
    [^]                                     { yypushback(yylength()); yybegin(FOR_STRUCTURE_LOOP); break; }
}
<PYTHON_EVALUATED_CONTROL_STRUCTURE_START>  {
    {ExtendedSpaceBasedEndMarker}           { yybegin(PYTHON_EXECUTED_CONDITION); return WHITE_SPACE; }
    [^]                                     { yypushback(yylength()); yybegin(PYTHON_EXECUTED_CONDITION); break; }
}
<PYTHON_EXECUTED_CONDITION>  {
    {EverythingButVariableValue}            { return PYTHON_EXPRESSION_CONTENT; }
    {ExtendedSpaceBasedEndMarker}           { leaveState(); return EOS; }
    {EOL}+                                  { leaveState(); yypushback(yylength()); break; }
    {MultiLine}                             {
      if (previousStates[currentIndex] == WHILE_CONFIGURATION) {
          pushBackTrailingWhitespace();
          yypushback(3);
          leaveState();
          return EOS;
      } else {
          yypushback(yylength());
          leaveState();
          break;
      }
    }
}

<WHILE_CONFIGURATION> {
    {Continuation}                          { return CONTINUATION; }
    {NonNewlineWhitespace}+                 { return WHITE_SPACE; }
    [^]                                     { yypushback(yylength()); leaveState(); break; }
}

<SIMPLE_CONTROL_STRUCTURE> {
    <FOR_STRUCTURE_LOOP> {EverythingButVariableValue}        { return LITERAL_CONSTANT; }
    {ExtendedSpaceBasedEndMarker}                            { leaveState(); return EOL; }
}

<USER_KEYWORD_RETURN_STATEMENT, FOR_STRUCTURE_LOOP> {EOL}+   { leaveState(); return EOL; }
<TESTCASE_DEFINITION, TASK_DEFINITION, USER_KEYWORD_DEFINITION, TEMPLATE_DEFINITION> {EOL}+   { return EOL; }

<NORMAL_PARAMETER_ASSIGNMENT> {EqualSign} { yybegin(PARAMETER_VALUE); return PARAMETER_ASSIGNMENT; }
<PARAMETER_VALUE>       {
    {EverythingButVariableValue}          { return LITERAL_CONSTANT; }
    <TEMPLATE_PARAMETER_VALUE> {
        {ExtendedKeywordFinishedMarker}   { leaveState(); yypushback(yylength()); break; }
    }
}

<SETTING_TEMPLATE_START>  {
    {KeywordLibraryNameLiteralValue} {EverythingButVariableValue}        {
          templateKeywordFound = true;
          int libraryNameSeparatorStart = indexOf('.');
          yypushback(yylength() - libraryNameSeparatorStart);
          yybegin(KEYWORD_LIBRARY_NAME_SEPARATOR);
          return KEYWORD_LIBRARY_NAME;
    }
    {EverythingButVariableValue}                                         { templateKeywordFound = true; return KEYWORD_NAME; }
    {EOL}+                                                               { leaveState(); return EOL; }
}

<KEYWORD_CALL>  {
    [/*]? {RunKeywordCall}                                               { return KEYWORD_NAME; }
    [/*]? {ConditionalRunKeywordCall}                                    { enterNewState(PYTHON_EVALUATED_CONTROL_STRUCTURE_START); return KEYWORD_NAME; }
    [/*]? ({AssertRunKeywordCall} | {RepeatKeywordCall})                 { enterNewState(SINGLE_LITERAL_CONSTANT_START); return KEYWORD_NAME; }
    [/*]? {SimpleConditionalKeywordCall}                                 {
          yybegin(KEYWORD_ARGUMENTS);
          enterNewState(PYTHON_EVALUATED_CONTROL_STRUCTURE_START);
          return KEYWORD_NAME;
    }
    [/*]? {RepeatKeywordCall}                                            { return KEYWORD_NAME; }

    [/*]? {BuiltInNamespace} ({RunKeywordCall} | {ConditionalRunKeywordCall} | {AssertRunKeywordCall} | {SimpleConditionalKeywordCall} | {RepeatKeywordCall}) {
          int additionalPushbackLength = 0;
          if (yycharat(0) != 'B' && yycharat(0) != 'b') {
              additionalPushbackLength = 1;
          }
          yypushback(yylength() - "BuiltIn".length() - additionalPushbackLength);
          enterNewState(KEYWORD_LIBRARY_NAME_SEPARATOR_FOR_SPECIAL_KEYWORD);
          return KEYWORD_LIBRARY_NAME;
    }

    {KeywordLibraryNameLiteralValue} {EverythingButVariableValue} {
          int libraryNameSeparatorStart = indexOf('.');
          yypushback(yylength() - libraryNameSeparatorStart);
          yybegin(KEYWORD_LIBRARY_NAME_SEPARATOR);
          return KEYWORD_LIBRARY_NAME;
    }
    {EverythingButVariableValue}                                   { yybegin(KEYWORD_ARGUMENTS); return KEYWORD_NAME; }
    {EOL}+                                                         { leaveState(); return EOL; }
}
<KEYWORD_LIBRARY_NAME_SEPARATOR> "."                               { yybegin(KEYWORD_CALL_NAME); return KEYWORD_LIBRARY_SEPARATOR; }
<KEYWORD_LIBRARY_NAME_SEPARATOR_FOR_SPECIAL_KEYWORD> "."           { leaveState(); return KEYWORD_LIBRARY_SEPARATOR; }
<KEYWORD_CALL_NAME> {EverythingButVariableValue}                   { leaveState(); return KEYWORD_NAME; }

<KEYWORD_ARGUMENTS> {
    "ELSE IF" {ExtendedSpaceBasedEndMarker}     { yypushback(yylength() - "ELSE IF".length()); yybegin(PYTHON_EVALUATED_CONTROL_STRUCTURE_START); return ELSE_IF; }
    "ELSE" {ExtendedKeywordFinishedMarker}      { yypushback(yylength() - "ELSE".length()); yybegin(KEYWORD_CALL); return ELSE; }
    {EverythingButVariableValue}                { return LITERAL_CONSTANT; }
    {EOL}+                                      { leaveState(); return EOL; }
}

<LITERAL_CONSTANT_ONLY, SETTING_VALUES> {
    {ScalarVariableStart}                                    { yypushback(yylength() - 1); enterNewState(VARIABLE_USAGE); enterNewState(VARIABLE_OPENING_BRACE); return SCALAR_VARIABLE_START; }
    {ListVariableStart}                                      { yypushback(yylength() - 1); enterNewState(VARIABLE_USAGE); enterNewState(VARIABLE_OPENING_BRACE); return LIST_VARIABLE_START; }
    {DictVariableStart}                                      { yypushback(yylength() - 1); enterNewState(VARIABLE_USAGE); enterNewState(VARIABLE_OPENING_BRACE); return DICT_VARIABLE_START; }
    {EnvVariableStart}                                       { yypushback(yylength() - 1); enterNewState(VARIABLE_USAGE); enterNewState(VARIABLE_OPENING_BRACE); return ENV_VARIABLE_START; }

    ^ {LineComment}                                          { return COMMENT; }
    {LineComment}                                            { return COMMENT; }

    {EOL} {WhitespaceIncludingNewline}* {LineCommentSign}    { yypushback(1); return WHITE_SPACE; }

    {NonNewlineWhitespace}+                                  { return WHITE_SPACE; }
    {ExtendedSpaceBasedEndMarker} {LineCommentSign}          { yypushback(1); return WHITE_SPACE; }
    {EverythingButVariableValue}                             { pushBackTrailingWhitespace(); return LITERAL_CONSTANT; }
    {EOL}+                                                   { leaveState(); return EOL; }
}

<SETTINGS_SECTION, SETTING, USER_KEYWORD_RETURN_STATEMENT, SINGLE_LITERAL_CONSTANT>  {EverythingButVariableValue}  { return LITERAL_CONSTANT; }

<SINGLE_LITERAL_CONSTANT_START>  {SpaceBasedEndMarker}       { yybegin(SINGLE_LITERAL_CONSTANT); return WHITE_SPACE; }
<SINGLE_LITERAL_CONSTANT>        {SpaceBasedEndMarker}       { leaveState(); return WHITE_SPACE; }

<VARIABLE_OPENING_BRACE> {
    {OpeningVariable}        { leaveState(); return VARIABLE_LBRACE; }
    [^]                      { leaveState(); yypushback(yylength()); break; }
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

{ScalarVariableStart}                           { enterNewState(VARIABLE_USAGE); enterNewState(VARIABLE_OPENING_BRACE); yypushback(1); return SCALAR_VARIABLE_START; }
{ListVariableStart}                             { enterNewState(VARIABLE_USAGE); enterNewState(VARIABLE_OPENING_BRACE); yypushback(1); return LIST_VARIABLE_START; }
{DictVariableStart}                             { enterNewState(VARIABLE_USAGE); enterNewState(VARIABLE_OPENING_BRACE); yypushback(1); return DICT_VARIABLE_START; }
{EnvVariableStart}                              { enterNewState(VARIABLE_USAGE); enterNewState(VARIABLE_OPENING_BRACE); yypushback(1); return ENV_VARIABLE_START; }

{EmptyValue} {WhitespaceIncludingNewline}*      { yypushback(yylength() - 2); return LITERAL_CONSTANT; }

// Can't be combined to {WhitespaceIncludingNewline}+ because then it would override the EOL handling in various states if there is a newline followed by
// whitespace. It is just a fallback for any whitespace that is not handled in other states.
{NonNewlineWhitespace}+ | {EOL}+                { return WHITE_SPACE; }

[^] { return BAD_CHARACTER; }
