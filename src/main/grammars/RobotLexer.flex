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

  protected final Stack<Integer> previousStates = new Stack<>();

  public RobotLexer() {
      this((java.io.Reader)null);
  }

  protected void enterNewState(int newState) {
      int previousState = yystate();
      previousStates.push(previousState);
      yybegin(newState);
  }

  protected void leaveState() {
      if (!previousStates.empty()) {
          yybegin(previousStates.pop());
      } else {
          yybegin(YYINITIAL);
      }
  }

  private void resetInternalState() {
      previousStates.clear();
      localTemplateEnabled = globalTemplateEnabled;
  }

  protected void resetLexer() {
      previousStates.clear();
      localTemplateEnabled = false;
      templateKeywordFound = false;
      globalTemplateEnabled = false;
  }

  protected int indexOf(char character) {
      int length = yylength();
      for (int i = 0; i < length; i++) {
        if (yycharat(i) == character) {
          return i;
        }
      }
      return -1;
  }
%}

%public
%buffer 65536
%class RobotLexer
%extends AbstractRobotLexer
%function advance
%type IElementType
%unicode
%caseless

Space = " "
Tab = \t
NBSP = \u00A0
Star = "*"
EqualSign = "="

Ellipsis = "..."
LineCommentSign = "#"

EscapeChar = \\
Escape = {EscapeChar} (.|\R)
EmptyValue = {EscapeChar} {Space}

Whitespace = {Space} | {Tab} | {NBSP}

SpaceBasedEndMarker = {Space}{2}{Space}* | {Tab}+

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
GenericSettingsKeyword = \p{Letter}+([ ][\p{Letter}_]+)*

VariableCharNotAllowed = [^\s$@%&]
ExceptionForAllowedVariableChar = [$@%&] [^{] | {EscapeChar}{1} [\s$@%&]
AllowedEverythingButVariableChar = {VariableCharNotAllowed} | {ExceptionForAllowedVariableChar}
AllowedEverythingButVariableSeq = {AllowedEverythingButVariableChar}+

AllowedExtendedVariableAccessChar = [^\s\[\]$@%&] | {EscapeChar}{1} "[" | {EscapeChar}{1} "]" | {ExceptionForAllowedVariableChar}
AllowedExtendedVariableAccessSeq = {AllowedExtendedVariableAccessChar}+

AllowedChar = [^\s$@%&=] | {ExceptionForAllowedVariableChar}
AllowedSeq = {AllowedChar}+

AllowedKeywordLibraryNameChar = [\p{Alpha}_-]
AllowedKeywordLibraryNameSeq = {AllowedKeywordLibraryNameChar}+

AllowedKeywordChar = [^\s$@%&.] | {ExceptionForAllowedVariableChar}
AllowedKeywordSeq = {AllowedKeywordChar}+

AllowedParamChar = [^\s$@%&] | {ExceptionForAllowedVariableChar}
AllowedParamSeq = {AllowedParamChar}+

RestrictedLiteralValue = {AllowedSeq} ({Space} {AllowedSeq})*
KeywordLibraryNameLiteralValue = [/*]? {AllowedKeywordLibraryNameSeq}+ "."
KeywordLiteralValue = {AllowedKeywordSeq} ({Space} {AllowedKeywordSeq})*
EverythingButVariableValue = {AllowedEverythingButVariableSeq} ({Space} {AllowedEverythingButVariableSeq})*
ExtendedVariableAccessValue = {AllowedExtendedVariableAccessSeq}

VariableLiteralValue =   ({Escape} | [^}$@&%] | [$@&%] [^{] | {OpeningVariable})+
ParamLiteralValue =      {AllowedParamSeq} ({Space} {AllowedParamSeq})*
LiteralValue =           [^\s]+([ ][^\s]+)*[ ]?

LocalSettingKeywordStart = "[" \s*
LocalSettingKeywordEnd = \s* "]"
LocalTemplateKeyword = {LocalSettingKeywordStart} "Template" {LocalSettingKeywordEnd}
LocalSetupTeardownKeywords = {LocalSettingKeywordStart} ("Setup" | "Teardown") {LocalSettingKeywordEnd}
LocalSettingKeyword = {LocalSettingKeywordStart} {GenericSettingsKeyword} {LocalSettingKeywordEnd}

ParameterName = [\p{Alpha}_-]+

RobotKeyword = "GIVEN" | "WHEN" | "THEN" | "AND" | "BUT" | "VAR" | "FOR" | "IN" | "IN ENUMERATE" | "IN RANGE" | "IN ZIP" | "END" | "WHILE" | "IF" | "ELSE IF" | "ELSE" | "TRY" | "EXCEPT" | "FINALLY" | "BREAK" | "CONTINUE" | "GROUP" | "RETURN"

MultiLine = {EOL}+ \s* {Ellipsis} \s* {EOL}*

LineComment = {LineCommentSign} {NON_EOL}*

%state SETTINGS_SECTION, VARIABLES_SECTION
%state TESTCASE_NAME_DEFINITION, TESTCASE_DEFINITION, TASK_NAME_DEFINITION, TASK_DEFINITION
%state USER_KEYWORD_NAME_DEFINITION, USER_KEYWORD_DEFINITION, USER_KEYWORD_RETURN_STATEMENT
%state SETTING, LOCAL_SETTING_DEFINITION, SETTING_TEMPLATE_START, LOCAL_TEMPLATE_DEFINITION_START, INTERMEDIATE_TEMPLATE_CONFIGURATION, TEMPLATE_DEFINITION
%state KEYWORD_CALL, KEYWORD_ARGUMENTS
%state INLINE_VARIABLE_DEFINITION, VARIABLE_DEFINITION, VARIABLE_DEFINITION_ARGUMENTS, VARIABLE_USAGE, EXTENDED_VARIABLE_ACCESS, PYTHON_EXPRESSION
%state PARAMETER_ASSIGNMENT, PARAMETER_VALUE, TEMPLATE_PARAMETER_ASSIGNMENT, TEMPLATE_PARAMETER_VALUE
%state FOR_STRUCTURE, SIMPLE_CONTROL_STRUCTURE_START, CONTROL_STRUCTURE_START, PYTHON_EXECUTED_CONDITION, PYTHON_EVALUATED_CONTROL_STRUCTURE_START, SIMPLE_CONTROL_STRUCTURE, CONTROL_STRUCTURE

%xstate COMMENTS_SECTION

%%

{Ellipsis} \s*                                  { return WHITE_SPACE; }
// Define a comment when it is the only thing on the line
^ {LineComment}                                 { pushBackTrailingWhitespace(); return COMMENT; }
// Define a comment when it comes after some content on the line
({SpaceBasedEndMarker} | {EOL})  {LineComment}  { pushBackTrailingWhitespace(); return COMMENT; }

{SettingsSectionIdentifier}   { resetInternalState(); yybegin(SETTINGS_SECTION); return SETTINGS_HEADER; }
{VariablesSectionIdentifier}  { resetInternalState(); yybegin(VARIABLES_SECTION); return VARIABLES_HEADER; }
{KeywordsSectionIdentifier}   { resetInternalState(); yybegin(USER_KEYWORD_NAME_DEFINITION); return USER_KEYWORDS_HEADER; }
{TestcaseSectionIdentifier}   { resetInternalState(); yybegin(TESTCASE_NAME_DEFINITION); return TEST_CASES_HEADER; }
{TasksSectionIdentifier}      { resetInternalState(); yybegin(TASK_NAME_DEFINITION); return TASKS_HEADER; }
{CommentSectionIdentifier}    { resetInternalState(); yybegin(COMMENTS_SECTION); return COMMENTS_HEADER; }

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

<VARIABLE_DEFINITION> {
    {ClosingVariable}                        { yybegin(VARIABLE_DEFINITION_ARGUMENTS); return VARIABLE_END; }
    {ClosingVariable} \s* {EqualSign} \s*    { yybegin(VARIABLE_DEFINITION_ARGUMENTS); yypushback(yylength() - 1); return VARIABLE_END; }
    {ClosingVariable} "["                    { enterNewState(EXTENDED_VARIABLE_ACCESS); yypushback(1); return VARIABLE_END; }
    {ClosingVariable} "]"                    { yybegin(VARIABLE_DEFINITION_ARGUMENTS); yypushback(1); return VARIABLE_END; }
    {EqualSign} \s*                          { yybegin(VARIABLE_DEFINITION_ARGUMENTS); pushBackTrailingWhitespace(); return ASSIGNMENT; }
}

<VARIABLE_DEFINITION_ARGUMENTS> {
    {EqualSign} \s*                                       { pushBackTrailingWhitespace(); return ASSIGNMENT; }
    "scope" {EqualSign} !({SpaceBasedEndMarker} | {EOL})  { yypushback(yylength() - "scope".length()); enterNewState(PARAMETER_ASSIGNMENT); return PARAMETER_NAME; }
    {ParamLiteralValue}                                   { pushBackTrailingWhitespace(); return LITERAL_CONSTANT; }
}

<VARIABLE_USAGE> {
    {ClosingVariable} "["                             { leaveState(); enterNewState(EXTENDED_VARIABLE_ACCESS); yypushback(1); return VARIABLE_END; }
    {ClosingVariable} "]"                             { leaveState(); yypushback(1); return VARIABLE_END; }
    {ClosingVariable}                                 { leaveState(); return VARIABLE_END; }
    {OpeningVariable} ( ! {ClosingVariable}{2} )+     { enterNewState(PYTHON_EXPRESSION); yypushback(yylength() - 1); return PYTHON_EXPRESSION_START; }
}

<VARIABLE_DEFINITION, VARIABLE_USAGE> {VariableLiteralValue}  { return VARIABLE_BODY; }

<EXTENDED_VARIABLE_ACCESS> {
    "["                          { return VARIABLE_ACCESS_START; }
    "]"                          { return VARIABLE_ACCESS_END; }
    "]" (\s+ | [^\[])            {
          leaveState();
          yypushback(yylength() - 1);
          return VARIABLE_ACCESS_END;
      }
    {ExtendedVariableAccessValue} { pushBackTrailingWhitespace(); return EXTENDED_VARIABLE_ACCESS_BODY; }
}

<PYTHON_EXPRESSION> {
    {ClosingVariable}{2}         { leaveState(); yypushback(1); return PYTHON_EXPRESSION_END; }
    ( [^}] | }[^}] )+            { return PYTHON_EXPRESSION_CONTENT; }
}

<SETTINGS_SECTION> {
    {LibraryImportKeyword} \s+             { enterNewState(SETTING); pushBackTrailingWhitespace(); return LIBRARY_IMPORT_KEYWORD; }
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
          templateKeywordFound = true;
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

<INTERMEDIATE_TEMPLATE_CONFIGURATION> {
    {SpaceBasedEndMarker} \s* | \s* {MultiLine} "NONE" \s*  {
          pushBackTrailingWhitespace();
          yypushback("NONE".length());
          return WHITE_SPACE;
    }
    "NONE" \s*  {
          pushBackTrailingWhitespace();
          yybegin(SETTING); // move into SETTING state without remembering this state. When leaving this SETTING state then going back into TEMPLATE_DEFINITION
          return LITERAL_CONSTANT;
    }
    \s* (\R \s* !{Ellipsis} | {MultiLine} \R)  {
          leaveState(); // back into TEMPLATE_DEFINITION
          return WHITE_SPACE;
    }
}
<TEMPLATE_DEFINITION> {
    {LocalTemplateKeyword} ({SpaceBasedEndMarker} \s* | \s* {MultiLine}) "NONE"   {
          yypushback(yylength());
          enterNewState(INTERMEDIATE_TEMPLATE_CONFIGURATION);
          enterNewState(LOCAL_SETTING_DEFINITION);
          localTemplateEnabled = false;
          break;
    }
    {LocalTemplateKeyword} \s* (\R \s* !{Ellipsis} | {MultiLine} \R)  {
          yypushback(yylength());
          enterNewState(INTERMEDIATE_TEMPLATE_CONFIGURATION);
          enterNewState(LOCAL_SETTING_DEFINITION);
          localTemplateEnabled = false;
          break;
    }
    {LocalTemplateKeyword} \s*   {
          yypushback(yylength());
          enterNewState(TEMPLATE_DEFINITION);
          enterNewState(SETTING_TEMPLATE_START);
          enterNewState(LOCAL_SETTING_DEFINITION);
          localTemplateEnabled = true;
          break;
    }
    ^ {LiteralValue}    {
        localTemplateEnabled = globalTemplateEnabled;
        leaveState();
        yypushback(yylength());
        break;
    }
    {ParameterName} {EqualSign}         {
          pushBackTrailingWhitespace();
          yypushback(1);
          enterNewState(TEMPLATE_PARAMETER_ASSIGNMENT);
          return TEMPLATE_PARAMETER_NAME;
      }
    <TEMPLATE_PARAMETER_ASSIGNMENT>  {EqualSign}       { yybegin(TEMPLATE_PARAMETER_VALUE); return ASSIGNMENT; }
    {RestrictedLiteralValue}                           { pushBackTrailingWhitespace(); return TEMPLATE_ARGUMENT_VALUE; }
    {EOL}+                                             { return EOL; }
}
<TEMPLATE_PARAMETER_VALUE>      {ParamLiteralValue}    { pushBackTrailingWhitespace(); return TEMPLATE_ARGUMENT_VALUE; }

<USER_KEYWORD_DEFINITION> {
    "RETURN" (\s{2}\s* | \R+)     {
          yypushback(yylength() - "RETURN".length());
          enterNewState(USER_KEYWORD_RETURN_STATEMENT);
          return RETURN;
      }

    {RestrictedLiteralValue}       { enterNewState(KEYWORD_CALL); yypushback(yylength()); break; }
}
<LOCAL_SETTING_DEFINITION> {
    {LocalSettingKeywordStart} { pushBackTrailingWhitespace(); return LOCAL_SETTING_START; }
    {GenericSettingsKeyword}   { pushBackTrailingWhitespace(); return LOCAL_SETTING_NAME; }
    {LocalSettingKeywordEnd}   { pushBackTrailingWhitespace(); leaveState(); return LOCAL_SETTING_END; }
}
<TESTCASE_DEFINITION, TASK_DEFINITION> {
    <USER_KEYWORD_DEFINITION> {
        <TEMPLATE_DEFINITION> {
            {LocalSetupTeardownKeywords} \s+     {
                yypushback(yylength());
                enterNewState(KEYWORD_CALL);
                enterNewState(LOCAL_SETTING_DEFINITION);
                break;
            }
            {LocalSettingKeyword} \s*            {
                yypushback(yylength());
                enterNewState(SETTING);
                enterNewState(LOCAL_SETTING_DEFINITION);
                break;
            }
        }

        "FOR" {SpaceBasedEndMarker}\s* {LiteralValue}             { yypushback(yylength() - "FOR".length()); enterNewState(FOR_STRUCTURE); return FOR; }
        "WHILE" {SpaceBasedEndMarker}\s*                          { pushBackTrailingWhitespace(); enterNewState(PYTHON_EVALUATED_CONTROL_STRUCTURE_START); return WHILE; }
        "IF" {SpaceBasedEndMarker}\s*                             { pushBackTrailingWhitespace(); enterNewState(PYTHON_EVALUATED_CONTROL_STRUCTURE_START); return IF; }
        "ELSE IF" {SpaceBasedEndMarker}\s*                        { pushBackTrailingWhitespace(); enterNewState(PYTHON_EVALUATED_CONTROL_STRUCTURE_START); return ELSE_IF; }
        "ELSE" \s*                                                { pushBackTrailingWhitespace(); return ELSE; }
        "TRY" \s*                                                 { pushBackTrailingWhitespace(); return TRY; }
        "EXCEPT" {SpaceBasedEndMarker}\s* {LiteralValue}          { yypushback(yylength() - "EXCEPT".length()); enterNewState(SIMPLE_CONTROL_STRUCTURE_START); return EXCEPT; }
        "FINALLY" \s*                                             { pushBackTrailingWhitespace(); return FINALLY; }
        "BREAK" \s*                                               { pushBackTrailingWhitespace(); return BREAK; }
        "CONTINUE" \s*                                            { pushBackTrailingWhitespace(); return CONTINUE; }
        "GROUP" ({SpaceBasedEndMarker}\s* {LiteralValue})?        { yypushback(yylength() - "GROUP".length()); enterNewState(SIMPLE_CONTROL_STRUCTURE_START); return GROUP; }
        "END" \s*                                                 { pushBackTrailingWhitespace(); return END; }
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

    "VAR" \s{2}\s* [^\R]+                        {
          yypushback(yylength() - "VAR".length());
          enterNewState(INLINE_VARIABLE_DEFINITION);
          return VAR;
      }

    {RestrictedLiteralValue}                {
          int nextState = localTemplateEnabled && templateKeywordFound ? TEMPLATE_DEFINITION : KEYWORD_CALL;
          enterNewState(nextState);
          yypushback(yylength());
          break;
      }
}

<FOR_STRUCTURE>  {
    "IN" \s{2}\s* {LiteralValue}            { yypushback(yylength() - "IN".length()); yybegin(CONTROL_STRUCTURE_START); return FOR_IN; }
    "IN ENUMERATE" \s{2}\s* {LiteralValue}  { yypushback(yylength() - "IN ENUMERATE".length()); yybegin(CONTROL_STRUCTURE_START); return FOR_IN; }
    "IN RANGE" \s{2}\s* {LiteralValue}      { yypushback(yylength() - "IN RANGE".length()); yybegin(CONTROL_STRUCTURE_START); return FOR_IN; }
    "IN ZIP" \s{2}\s* {LiteralValue}        { yypushback(yylength() - "IN ZIP".length()); yybegin(CONTROL_STRUCTURE_START); return FOR_IN; }
    "END" \s*                               { pushBackTrailingWhitespace(); leaveState(); return END; }
}

<SIMPLE_CONTROL_STRUCTURE_START>            {SpaceBasedEndMarker}     { yybegin(SIMPLE_CONTROL_STRUCTURE); return WHITE_SPACE; }
<CONTROL_STRUCTURE_START>                   {SpaceBasedEndMarker}     { yybegin(CONTROL_STRUCTURE); return WHITE_SPACE; }

<PYTHON_EVALUATED_CONTROL_STRUCTURE_START>  {SpaceBasedEndMarker} | {MultiLine} | {EOL} {Whitespace}* {LineComment}   { yybegin(PYTHON_EXECUTED_CONDITION); return WHITE_SPACE; }
<PYTHON_EXECUTED_CONDITION>  {
    {EverythingButVariableValue}            { pushBackTrailingWhitespace(); return PYTHON_EXPRESSION_CONTENT; }
    {SpaceBasedEndMarker}                   { leaveState(); return EOS; }
    {EOL}+                                  { leaveState(); return EOL; }
}

<SIMPLE_CONTROL_STRUCTURE> {
    <SETTING, CONTROL_STRUCTURE> {RestrictedLiteralValue} | {EqualSign} { pushBackTrailingWhitespace(); return LITERAL_CONSTANT; }
    {SpaceBasedEndMarker}                            { leaveState(); return EOL; }
}
<SETTING>     {WithNameKeyword} \s+                  { pushBackTrailingWhitespace(); return WITH_NAME; }

// Multiline handling (don't return EOL on detected multiline). If there is a multiline without the Ellipsis (...) marker,
// then return EOL to mark the end of the statement.
<SETTING, KEYWORD_CALL, KEYWORD_ARGUMENTS, VARIABLE_DEFINITION, VARIABLE_DEFINITION_ARGUMENTS, SETTING_TEMPLATE_START> {
    <TESTCASE_DEFINITION, TASK_DEFINITION, USER_KEYWORD_DEFINITION, PYTHON_EXECUTED_CONDITION> {
        {MultiLine}                                  { return WHITE_SPACE; }
        {EOL} {Whitespace}* {LineComment}            { yypushback(yylength() - 1); return WHITE_SPACE; }
    }
    <USER_KEYWORD_RETURN_STATEMENT, CONTROL_STRUCTURE> {EOL}+           { leaveState(); return EOL; }
}

<KEYWORD_ARGUMENTS, TESTCASE_DEFINITION, TASK_DEFINITION, USER_KEYWORD_DEFINITION, VARIABLE_DEFINITION> {
    <SETTINGS_SECTION> {
        <SETTING, FOR_STRUCTURE> {ParameterName} {EqualSign}        {
              pushBackTrailingWhitespace();
              yypushback(1);
              enterNewState(PARAMETER_ASSIGNMENT);
              return PARAMETER_NAME;
          }
        {EqualSign}                                   { return ASSIGNMENT; }
    }
    {EOL}+                                            { return EOL; }
}
<PARAMETER_ASSIGNMENT>  {EqualSign}                   { yybegin(PARAMETER_VALUE); return ASSIGNMENT; }
<PARAMETER_VALUE>       {
    {ParamLiteralValue}                               { pushBackTrailingWhitespace(); return LITERAL_CONSTANT; }
    <TEMPLATE_PARAMETER_VALUE> {
        {ScalarVariableStart}                         { enterNewState(VARIABLE_USAGE); return SCALAR_VARIABLE_START; }
        {ListVariableStart}                           { enterNewState(VARIABLE_USAGE); return LIST_VARIABLE_START; }
        {DictVariableStart}                           { enterNewState(VARIABLE_USAGE); return DICT_VARIABLE_START; }
        {EnvVariableStart}                            { enterNewState(VARIABLE_USAGE); return ENV_VARIABLE_START; }
        {SpaceBasedEndMarker} \s* | {EOL}+            { leaveState(); yypushback(yylength()); break; }
    }
}

<SETTING_TEMPLATE_START>  {KeywordLiteralValue}       { templateKeywordFound = true; pushBackTrailingWhitespace(); return KEYWORD_NAME; }
// Consciously used yybegin instead of enterNewState to avoid pushing the state onto the stack. We're technically still in the KEYWORD_CALL state
// and when we're, even in KEYWORD_ARGUMENTS, leave the state, it should return to whatever was before KEYWORD_CALL.
// Just switched into another state to provide keyword arguments as such instead of interpreting them incorrectly as KEYWORD_NAME.
<KEYWORD_CALL, SETTING_TEMPLATE_START>  {
    {KeywordLibraryNameLiteralValue}      { yypushback(1); return KEYWORD_LIBRARY_NAME; }
    "."                                   { return KEYWORD_LIBRARY_SEPARATOR; }
    {KeywordLiteralValue}                 { yybegin(KEYWORD_ARGUMENTS); pushBackTrailingWhitespace(); return KEYWORD_NAME; }
}

<KEYWORD_ARGUMENTS> {
    {SpaceBasedEndMarker} {RobotKeyword}  { yypushback(yylength()); leaveState(); break; }
}

<SETTINGS_SECTION, SETTING, KEYWORD_ARGUMENTS, USER_KEYWORD_RETURN_STATEMENT> {
    {RestrictedLiteralValue}              { pushBackTrailingWhitespace(); return LITERAL_CONSTANT; }
}

<COMMENTS_SECTION> {
    {SettingsSectionIdentifier}            { resetInternalState(); yybegin(SETTINGS_SECTION); pushBackTrailingWhitespace(); return SETTINGS_HEADER; }
    {TestcaseSectionIdentifier}            { resetInternalState(); yybegin(TESTCASE_NAME_DEFINITION); pushBackTrailingWhitespace(); return TEST_CASES_HEADER; }
    {TasksSectionIdentifier}               { resetInternalState(); yybegin(TASK_NAME_DEFINITION); pushBackTrailingWhitespace(); return TASKS_HEADER; }
    {KeywordsSectionIdentifier}            { resetInternalState(); yybegin(USER_KEYWORD_NAME_DEFINITION); pushBackTrailingWhitespace(); return USER_KEYWORDS_HEADER; }
    {VariablesSectionIdentifier}           { resetInternalState(); yybegin(VARIABLES_SECTION); pushBackTrailingWhitespace(); return VARIABLES_HEADER; }

    <YYINITIAL> {
        ({Whitespace} | {Ellipsis} | {EOL})+   { return WHITE_SPACE; }
        {NON_EOL}+                             { return COMMENT; }
    }
}

{ScalarVariableStart}    { enterNewState(VARIABLE_USAGE); return SCALAR_VARIABLE_START; }
{ListVariableStart}      { enterNewState(VARIABLE_USAGE); return LIST_VARIABLE_START; }
{DictVariableStart}      { enterNewState(VARIABLE_USAGE); return DICT_VARIABLE_START; }
{EnvVariableStart}       { enterNewState(VARIABLE_USAGE); return ENV_VARIABLE_START; }

{EmptyValue} \s*         { yypushback(yylength() - 2); return LITERAL_CONSTANT; }
{Whitespace}+ | {EOL}+   { return WHITE_SPACE; }

[^] { return BAD_CHARACTER; }
