package dev.xeonkryptos.xeonrobotframeworkplugin.psi;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

import java.util.Stack;

import static com.intellij.psi.TokenType.*;
import static dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes.*;

%%

%{
  private boolean globalTemplateEnabled = false;
  private boolean localTemplateEnabled = false;
  private boolean templateKeywordFound = false;

  private final Stack<Integer> previousStates = new Stack<>();

  public RobotLexer() {
      this((java.io.Reader)null);
  }

  private void enterNewState(int newState) {
      int previousState = yystate();
      previousStates.push(previousState);
      yybegin(newState);
  }

  private void leaveState() {
      if (!previousStates.empty()) {
          Integer previousState = previousStates.pop();
          yybegin(previousState);
      } else {
          yybegin(YYINITIAL);
      }
  }

  private void reset() {
      previousStates.clear();
      localTemplateEnabled = globalTemplateEnabled;
  }

  private void pushBackTrailingWhitespace() {
      CharSequence text = yytext();
      int trailingWhitespaceLength = computeTrailingWhitespaceLength(text);
      if (trailingWhitespaceLength > 0) {
          yypushback(trailingWhitespaceLength);
      }
  }

  private int computeTrailingWhitespaceLength(CharSequence text) {
      int length = 0;
      for (int i = text.length() - 1; i >= 0; i--) {
          char c = text.charAt(i);
          if (Character.isWhitespace(c)) {
              length++;
          } else {
              break;
          }
      }
      return length;
  }
%}

%public
%class RobotLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode
%caseless

Space = " "
Tab = \t
Star = "*"
EqualSign = "="

Ellipsis = "..."
LineCommentSign = "#"

EmptyValue = \\ {Space}

Whitespace = {Space} | {Tab}

WithNameKeyword = "WITH NAME" | "AS"

SectionSettingsWords = "Settings" | "Setting"
SectionVariablesWords = "Variables" | "Variable"
SectionTestcasesWords = "Test Cases" | "Test Case"
SectionTasksWords = "Tasks" | "Task"
SectionKeywordsWords = "Keywords" | "Keyword"
SectionCommentsWords = "Comments" | "Comment"

EOL = (\r) | (\n) | (\r\n)
NON_EOL = [^\r\n]

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
SetupTeardownKeywords = ("Suite Setup" | "Suite Teardown" | "Test Setup" | "Test Teardown" | "Task Setup" | "Task Teardown")
TagsKeywords = ("Test Tags" | "Force Tags" | "Default Tags" | "Keyword Tags")
TemplateKeywords = ("Test Template" | "Task Template")
TimeoutKeywords = ("Test Timeout" | "Task Timeout")
GenericSettingsKeyword = [\p{L}\p{N}_]+([ ][\p{L}\p{N}_])*

LiteralValue = [^\s]+([ ][^\s]+)*[ ]?
RestrictedLiteralValue = [^\s${}@%&=]+([ ][^\s${}@%&]+)*[ ]?
ParamLiteralValue =      [^\s${}@%&]+([ ][^\s${}@%&]+)*[ ]?

LocalSettingKeywordStart = "[" \s*
LocalSettingKeywordEnd = \s* "]"
LocalTemplateKeyword = {LocalSettingKeywordStart} "Template" {LocalSettingKeywordEnd}
LocalArgumentsKeyword = {LocalSettingKeywordStart} "Arguments" {LocalSettingKeywordEnd}
LocalSetupTeardownKeywords = {LocalSettingKeywordStart} ("Setup" | "Teardown") {LocalSettingKeywordEnd}
LocalSettingKeyword = {LocalSettingKeywordStart} {GenericSettingsKeyword} {LocalSettingKeywordEnd}

ParameterName = [\p{L}_][\p{L}\p{N}_]*

VariableSliceAccess = "[" \s* (-?\d+)? \s* : \s* (-?\d+)? (\s* : \s* (-?\d+))? \s* "]"
VariableIndexAccess = "[" \s* \d+ \s* "]"
VariableKeyAccess = "[" \s* ([^$@%&] | [$@%&][^{])[^\]]* \s* "]"

MultiLine = {EOL}+ \s* {Ellipsis} \s*

LineComment = {LineCommentSign} {NON_EOL}*

%state LANGUAGE_SETTING
%state SETTINGS_SECTION, VARIABLES_SECTION
%state TESTCASE_NAME_DEFINITION, TESTCASE_DEFINITION, TASK_NAME_DEFINITION, TASK_DEFINITION
%state USER_KEYWORD_NAME_DEFINITION, USER_KEYWORD_DEFINITION, USER_KEYWORD_RETURN_STATEMENT
%state SETTING, SETTING_TEMPLATE_START, TEMPLATE_DEFINITION
%state KEYWORD_CALL, KEYWORD_ARGUMENTS
%state INLINE_VARIABLE_DEFINITION, VARIABLE_DEFINITION, VARIABLE_USAGE, EXTENDED_VARIABLE_ACCESS, PYTHON_EXPRESSION
%state PARAMETER_ASSIGNMENT, PARAMETER_VALUE, TEMPLATE_PARAMETER_ASSIGNMENT, TEMPLATE_PARAMETER_VALUE
%state FOR_STRUCTURE, CONTROL_STRUCTURE_START, CONTROL_STRUCTURE

%xstate COMMENTS_SECTION

%%

{EmptyValue}                          { return ARGUMENT_VALUE; }
{Ellipsis} \s*                        { return WHITE_SPACE; }
{LineComment}                         { return COMMENT; }

<YYINITIAL, LANGUAGE_SETTING>        "Language:"   {
          yybegin(LANGUAGE_SETTING);
          return LANGUAGE_KEYWORD;
      }

{SettingsSectionIdentifier}   { reset(); yybegin(SETTINGS_SECTION); return SETTINGS_HEADER; }
{VariablesSectionIdentifier}  { reset(); yybegin(VARIABLES_SECTION); return VARIABLES_HEADER; }
{KeywordsSectionIdentifier}   { reset(); yybegin(USER_KEYWORD_NAME_DEFINITION); return USER_KEYWORDS_HEADER; }
{TestcaseSectionIdentifier}   { reset(); yybegin(TESTCASE_NAME_DEFINITION); return TEST_CASES_HEADER; }
{TasksSectionIdentifier}      { reset(); yybegin(TASK_NAME_DEFINITION); return TASKS_HEADER; }
{CommentSectionIdentifier}    { reset(); yybegin(COMMENTS_SECTION); return COMMENTS_HEADER; }

<VARIABLES_SECTION> {
    {ScalarVariableStart}                    { enterNewState(VARIABLE_DEFINITION); return SCALAR_VARIABLE_START; }
    {ListVariableStart}                      { enterNewState(VARIABLE_DEFINITION); return LIST_VARIABLE_START; }
    {DictVariableStart}                      { enterNewState(VARIABLE_DEFINITION); return DICT_VARIABLE_START; }
    {EnvVariableStart}                       { enterNewState(VARIABLE_DEFINITION); return ENV_VARIABLE_START; }
}

<INLINE_VARIABLE_DEFINITION> {
    {ScalarVariableStart}                    { yybegin(VARIABLE_DEFINITION); return SCALAR_VARIABLE_START; }
    {ListVariableStart}                      { yybegin(VARIABLE_DEFINITION); return LIST_VARIABLE_START; }
    {DictVariableStart}                      { yybegin(VARIABLE_DEFINITION); return DICT_VARIABLE_START; }
    {EnvVariableStart}                       { yybegin(VARIABLE_DEFINITION); return ENV_VARIABLE_START; }
}

{ScalarVariableStart}  { enterNewState(VARIABLE_USAGE); return SCALAR_VARIABLE_START; }
{ListVariableStart}    { enterNewState(VARIABLE_USAGE); return LIST_VARIABLE_START; }
{DictVariableStart}    { enterNewState(VARIABLE_USAGE); return DICT_VARIABLE_START; }
{EnvVariableStart}     { enterNewState(VARIABLE_USAGE); return ENV_VARIABLE_START; }

<LANGUAGE_SETTING> {RestrictedLiteralValue}  { return LANGUAGE_NAME; }

<VARIABLE_DEFINITION> {
    {ClosingVariable}            { return VARIABLE_END; }
    {ClosingVariable} "["        { enterNewState(EXTENDED_VARIABLE_ACCESS); yypushback(1); return VARIABLE_END; }
    {ClosingVariable} "]"        { yypushback(1); return VARIABLE_END; }
    {EqualSign} \s*              { pushBackTrailingWhitespace(); return ASSIGNMENT; }
    {RestrictedLiteralValue}     { pushBackTrailingWhitespace(); return ARGUMENT_VALUE; }
}

<VARIABLE_USAGE> {
    {ClosingVariable} "["        { leaveState(); enterNewState(EXTENDED_VARIABLE_ACCESS); yypushback(1); return VARIABLE_END; }
    {ClosingVariable} "]"        { leaveState(); yypushback(1); return VARIABLE_END; }
    {ClosingVariable}            { leaveState(); return VARIABLE_END; }
    "{" \s*                      { enterNewState(PYTHON_EXPRESSION); pushBackTrailingWhitespace(); return PYTHON_EXPRESSION_START; }
    {RestrictedLiteralValue}     { return ARGUMENT_VALUE; }
}

<EXTENDED_VARIABLE_ACCESS> {
    {VariableSliceAccess}        { return VARIABLE_SLICE_ACCESS; }
    {VariableIndexAccess}        { return VARIABLE_INDEX_ACCESS; }
    {VariableKeyAccess}          { return VARIABLE_KEY_ACCESS; }

    // Workaround for nested variable usage in the extended variable access syntax
    "["                          { return VARIABLE_ACCESS_START; }
    "]"                          { return VARIABLE_ACCESS_END; }

    {VariableSliceAccess} \s+    { leaveState(); pushBackTrailingWhitespace(); return VARIABLE_SLICE_ACCESS; }
    {VariableIndexAccess} \s+    { leaveState(); pushBackTrailingWhitespace(); return VARIABLE_INDEX_ACCESS; }
    {VariableKeyAccess}   \s+    { leaveState(); pushBackTrailingWhitespace(); return VARIABLE_KEY_ACCESS; }
    "]" \s+                      { leaveState(); pushBackTrailingWhitespace(); return VARIABLE_ACCESS_END; }
}

<PYTHON_EXPRESSION> {
    {ClosingVariable}{2}         { leaveState(); yypushback(1); return PYTHON_EXPRESSION_END; }
    ( [^}] | }[^}] )+            { return PYTHON_EXPRESSION_CONTENT; }
}

<SETTINGS_SECTION> {
    {LibraryImportKeyword} \s+             { enterNewState(SETTING); pushBackTrailingWhitespace(); return LIBRARY_IMPORT_KEYWORD; }
    {WithNameKeyword} \s+                  { enterNewState(SETTING); pushBackTrailingWhitespace(); return WITH_NAME_KEYWORD; }
    {ResourceImportKeyword} \s+            { enterNewState(SETTING); pushBackTrailingWhitespace(); return RESOURCE_IMPORT_KEYWORD; }
    {VariablesImportKeyword} \s+           { enterNewState(SETTING); pushBackTrailingWhitespace(); return VARIABLES_IMPORT_KEYWORD; }
    {NameKeyword} \s+                      { enterNewState(SETTING); pushBackTrailingWhitespace(); return SUITE_NAME_KEYWORD; }
    {DocumentationKeyword} \s+             { enterNewState(SETTING); pushBackTrailingWhitespace(); return DOCUMENTATION_KEYWORD; }
    {MetadataKeyword} \s+                  { enterNewState(SETTING); pushBackTrailingWhitespace(); return METADATA_KEYWORD; }
    {SetupTeardownKeywords} \s+            { enterNewState(KEYWORD_CALL); pushBackTrailingWhitespace(); return SETUP_TEARDOWN_STATEMENT_KEYWORDS; }
    {TagsKeywords} \s+                     { enterNewState(SETTING); pushBackTrailingWhitespace(); return TAGS_KEYWORDS; }
    {TemplateKeywords} \s+                 {
          enterNewState(KEYWORD_CALL);
          pushBackTrailingWhitespace();
          globalTemplateEnabled = true;
          localTemplateEnabled = true;
          return TEMPLATE_KEYWORDS;
      }
    {TimeoutKeywords} \s+                  { enterNewState(SETTING); pushBackTrailingWhitespace(); return TIMEOUT_KEYWORDS; }

    {GenericSettingsKeyword} \s+           { enterNewState(SETTING); pushBackTrailingWhitespace(); return UNKNOWN_SETTING_KEYWORD; }
}

<TESTCASE_NAME_DEFINITION>      {LiteralValue}    { enterNewState(TESTCASE_DEFINITION); pushBackTrailingWhitespace(); return TEST_CASE_NAME; }
<TASK_NAME_DEFINITION>          {LiteralValue}    { enterNewState(TASK_DEFINITION); pushBackTrailingWhitespace(); return TASK_NAME; }
<USER_KEYWORD_NAME_DEFINITION>  {LiteralValue}    { enterNewState(USER_KEYWORD_DEFINITION); pushBackTrailingWhitespace(); return USER_KEYWORD_NAME; }

<TESTCASE_DEFINITION>         ^ {LiteralValue}    { localTemplateEnabled = globalTemplateEnabled; pushBackTrailingWhitespace(); return TEST_CASE_NAME; }
<TASK_DEFINITION>             ^ {LiteralValue}    { localTemplateEnabled = globalTemplateEnabled; pushBackTrailingWhitespace(); return TASK_NAME; }
<USER_KEYWORD_DEFINITION>     ^ {LiteralValue}    { pushBackTrailingWhitespace(); return USER_KEYWORD_NAME; }

<TEMPLATE_DEFINITION>         ^ {LiteralValue}    {
          templateKeywordFound = false;
          pushBackTrailingWhitespace();
          leaveState();
          return previousStates.peek() == TESTCASE_DEFINITION ? TEST_CASE_NAME : TASK_NAME;
      }

<USER_KEYWORD_DEFINITION> {
    "RETURN" \s* [^\R]+            {
          yypushback(yylength() - "RETURN".length());
          pushBackTrailingWhitespace();
          enterNewState(USER_KEYWORD_RETURN_STATEMENT);
          return RETURN;
      }

    {RestrictedLiteralValue}       { enterNewState(KEYWORD_CALL); yypushback(yylength()); }
}
<TESTCASE_DEFINITION, TASK_DEFINITION> {
    {LocalTemplateKeyword} (\s{2} \s* | \s* {MultiLine}) "NONE"   {
          yypushback("NONE".length());
          pushBackTrailingWhitespace();
          enterNewState(SETTING);
          localTemplateEnabled = false;
          return LOCAL_SETTING_NAME;
      }
    {LocalTemplateKeyword} / \s* (\R \s* !{Ellipsis} | {MultiLine} \R)      { pushBackTrailingWhitespace(); localTemplateEnabled = false; return LOCAL_SETTING_NAME; }
    {LocalTemplateKeyword} \s*              {
          enterNewState(SETTING_TEMPLATE_START);
          pushBackTrailingWhitespace();
          localTemplateEnabled = true;
          return LOCAL_SETTING_NAME;
      }

    <USER_KEYWORD_DEFINITION> {
        {LocalSetupTeardownKeywords} \s+          { enterNewState(KEYWORD_CALL); pushBackTrailingWhitespace(); return LOCAL_SETTING_NAME; }
        {LocalSettingKeyword} \s*                 { enterNewState(SETTING); pushBackTrailingWhitespace(); return LOCAL_SETTING_NAME; }

        "FOR" \s{2}\s* {LiteralValue}             { yypushback(yylength() - "FOR".length()); return FOR; }
        "IN" \s{2}\s* {LiteralValue}              { yypushback(yylength() - "IN".length()); enterNewState(CONTROL_STRUCTURE_START); return FOR_IN; }
        "IN ENUMERATE" \s{2}\s* {LiteralValue}    { yypushback(yylength() - "IN ENUMERATE".length()); enterNewState(CONTROL_STRUCTURE_START); return FOR_IN; }
        "IN RANGE" \s{2}\s* {LiteralValue}        { yypushback(yylength() - "IN RANGE".length()); enterNewState(CONTROL_STRUCTURE_START); return FOR_IN; }
        "IN ZIP" \s{2}\s* {LiteralValue}          { yypushback(yylength() - "IN ZIP".length()); enterNewState(CONTROL_STRUCTURE_START); return FOR_IN; }
        "WHILE" \s{2}\s* {LiteralValue}?          { yypushback(yylength() - "WHILE".length()); enterNewState(CONTROL_STRUCTURE_START); return WHILE; }
        "IF" \s{2}\s* {LiteralValue}              { yypushback(yylength() - "IF".length()); enterNewState(CONTROL_STRUCTURE_START); return IF; }
        "ELSE IF" \s{2}\s* {LiteralValue}         { yypushback(yylength() - "ELSE IF".length()); enterNewState(CONTROL_STRUCTURE_START); return ELSE_IF; }
        "ELSE" \s*                                { pushBackTrailingWhitespace(); return ELSE; }
        "TRY" \s*                                 { pushBackTrailingWhitespace(); return TRY; }
        "EXCEPT" \s{2}\s* {LiteralValue}          { yypushback(yylength() - "EXCEPT".length()); enterNewState(CONTROL_STRUCTURE_START); return EXCEPT; }
        "FINALLY" \s*                             { pushBackTrailingWhitespace(); return FINALLY; }
        "BREAK" \s*                               { pushBackTrailingWhitespace(); return BREAK; }
        "CONTINUE" \s*                            { pushBackTrailingWhitespace(); return CONTINUE; }
        "GROUP" (\s{2}\s* {LiteralValue})?        { yypushback(yylength() - "GROUP".length()); enterNewState(CONTROL_STRUCTURE_START); return GROUP; }
        "END" \s*                                 { pushBackTrailingWhitespace(); return END; }
    }

    "GIVEN" \s+ {RestrictedLiteralValue}    {
          yypushback(yylength() - "GIVEN".length());
          enterNewState(KEYWORD_CALL);
          return GIVEN;
      }
    "WHEN" \s+  {RestrictedLiteralValue}    {
         yypushback(yylength() - "WHEN".length());
         enterNewState(KEYWORD_CALL);
         return WHEN;
     }
    "THEN" \s+  {RestrictedLiteralValue}    {
         yypushback(yylength() - "THEN".length());
         enterNewState(KEYWORD_CALL);
         return THEN;
     }
    "AND" \s+  {RestrictedLiteralValue}     {
         yypushback(yylength() - "AND".length());
         enterNewState(KEYWORD_CALL);
         return AND;
     }
    "BUT" \s+  {RestrictedLiteralValue}     {
         yypushback(yylength() - "BUT".length());
         enterNewState(KEYWORD_CALL);
         return BUT;
     }

    // Using yybegin instead of enterNewState to avoid pushing the state onto the stack. Don't switch the state on the stack to something else than
    // the current one. This way, after variable definition via VAR is done, we will reach this state again.
    "VAR" \s+ [^\R]+                        {
          yypushback(yylength() - "VAR".length());
          pushBackTrailingWhitespace();
          enterNewState(INLINE_VARIABLE_DEFINITION);
          return VAR;
      }

    {RestrictedLiteralValue}                {
              int nextState = localTemplateEnabled && templateKeywordFound ? TEMPLATE_DEFINITION : KEYWORD_CALL;
              enterNewState(nextState);
              yypushback(yylength());
      }
}

<CONTROL_STRUCTURE_START>  ({Space}{2}{Space}* | {Tab}+) | {EOL}+     { yybegin(CONTROL_STRUCTURE); return WHITE_SPACE; }
<CONTROL_STRUCTURE> {
    {RestrictedLiteralValue}                         { pushBackTrailingWhitespace(); return ARGUMENT_VALUE; }
    ({Space}{2}{Space}* | {Tab}+) | {EOL}+           { leaveState(); return EOL; }
}

<KEYWORD_ARGUMENTS, SETTINGS_SECTION, TESTCASE_DEFINITION, TASK_DEFINITION, USER_KEYWORD_DEFINITION, VARIABLE_DEFINITION> {
    {ParameterName} / {EqualSign} (!(\s{2}) | !\R)   { enterNewState(PARAMETER_ASSIGNMENT); return PARAMETER_NAME; }
    {EqualSign}                                      { return ASSIGNMENT; }
}
<PARAMETER_ASSIGNMENT>  {EqualSign}                  { yybegin(PARAMETER_VALUE); return ASSIGNMENT; }
<PARAMETER_VALUE>       {ParamLiteralValue}          { leaveState(); pushBackTrailingWhitespace(); return ARGUMENT_VALUE; }

<TEMPLATE_DEFINITION> {
    {ParameterName} / {EqualSign} (!(\s{2}) | !\R)   { enterNewState(TEMPLATE_PARAMETER_ASSIGNMENT); return TEMPLATE_PARAMETER_NAME; }
    {EqualSign}                                      { return ASSIGNMENT; }
    {RestrictedLiteralValue}                         { pushBackTrailingWhitespace(); return TEMPLATE_ARGUMENT_VALUE; }
    {EOL}+                                           { return EOL; }
}
<TEMPLATE_PARAMETER_ASSIGNMENT> {EqualSign}          { yybegin(TEMPLATE_PARAMETER_VALUE); return ASSIGNMENT; }
<TEMPLATE_PARAMETER_VALUE>      {ParamLiteralValue}  { leaveState(); pushBackTrailingWhitespace(); return TEMPLATE_ARGUMENT_VALUE; }

// Multiline handling (don't return EOL on detected multiline). If there is a multiline without the Ellipsis (...) marker,
// then return EOL to mark the end of the statement.
<SETTING, SETTING_TEMPLATE_START, KEYWORD_CALL, KEYWORD_ARGUMENTS, VARIABLE_DEFINITION> {
    {MultiLine}                                { return WHITE_SPACE; }
    {EOL} \s* {LineComment}                    { yypushback(yylength() - 1); return WHITE_SPACE; }
    <USER_KEYWORD_RETURN_STATEMENT> {EOL}+     { leaveState(); return EOL; }
}

<SETTING_TEMPLATE_START>  {RestrictedLiteralValue}        { templateKeywordFound = true; pushBackTrailingWhitespace(); return KEYWORD_NAME; }

// Consciously used yybegin instead of enterNewState to avoid pushing the state onto the stack. We're technically still in the KEYWORD_CALL state
// and when we're, even in KEYWORD_ARGUMENTS, leave the state, it should return to whatever was before KEYWORD_CALL.
// Just switched into another state to provide keyword arguments as such instead of interpreting them incorrectly as KEYWORD_NAME.
<KEYWORD_CALL>      {RestrictedLiteralValue}  { yybegin(KEYWORD_ARGUMENTS); pushBackTrailingWhitespace(); return KEYWORD_NAME; }

<SETTINGS_SECTION, SETTING, KEYWORD_ARGUMENTS, USER_KEYWORD_RETURN_STATEMENT> {RestrictedLiteralValue}        { pushBackTrailingWhitespace(); return ARGUMENT_VALUE; }

<COMMENTS_SECTION> {
    {SettingsSectionIdentifier}            { reset(); yybegin(SETTINGS_SECTION); pushBackTrailingWhitespace(); return SETTINGS_HEADER; }
    {TestcaseSectionIdentifier}            { reset(); yybegin(TESTCASE_NAME_DEFINITION); pushBackTrailingWhitespace(); return TEST_CASES_HEADER; }
    {TasksSectionIdentifier}               { reset(); yybegin(TASK_NAME_DEFINITION); pushBackTrailingWhitespace(); return TASKS_HEADER; }
    {KeywordsSectionIdentifier}            { reset(); yybegin(USER_KEYWORD_NAME_DEFINITION); pushBackTrailingWhitespace(); return USER_KEYWORDS_HEADER; }
    {VariablesSectionIdentifier}           { reset(); yybegin(VARIABLES_SECTION); pushBackTrailingWhitespace(); return VARIABLES_HEADER; }

    ({Whitespace} | {Ellipsis} | {EOL})+   { return WHITE_SPACE; }
    {NON_EOL}+                             { return COMMENT; }
}

{Whitespace}+        { return WHITE_SPACE; }
{EOL}+               { return WHITE_SPACE; }

[^] { return BAD_CHARACTER; }
