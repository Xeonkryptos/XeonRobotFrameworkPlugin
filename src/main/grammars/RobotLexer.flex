package dev.xeonkryptos.xeonrobotframeworkplugin.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.tree.IElementType;
import dev.xeonkryptos.xeonrobotframeworkplugin.lexer.*;
import java.util.Map;
import java.util.EnumMap;
import org.jetbrains.annotations.NotNull;

import static com.intellij.psi.TokenType.*;
import static dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes.*;
import static dev.xeonkryptos.xeonrobotframeworkplugin.psi.ExtendedRobotTypes.*;
%%

%{
  private final Map<RobotSectionType, Integer> sectionTypes = new EnumMap<>(RobotSectionType.class);
  private final Map<RobotGlobalSettingType, Integer> globalSettingTypes = new EnumMap<>(RobotGlobalSettingType.class);
  private final Map<RobotLocalSettingType, Integer> localSettingTypes = new EnumMap<>(RobotLocalSettingType.class);

  public RobotLexer(Project project) {
    super(project);

    sectionTypes.put(RobotSectionType.SETTINGS, SETTINGS_SECTION);
    sectionTypes.put(RobotSectionType.VARIABLES, VARIABLES_SECTION);
    sectionTypes.put(RobotSectionType.TEST_CASES, TEST_CASES_SECTION_NAME_DEFINITION);
    sectionTypes.put(RobotSectionType.TASKS, TASKS_SECTION_NAME_DEFINITION);
    sectionTypes.put(RobotSectionType.KEYWORDS, USER_KEYWORD_NAME_DEFINITION);
    sectionTypes.put(RobotSectionType.COMMENTS, COMMENTS_SECTION);
    sectionTypes.put(RobotSectionType.INVALID, INVALID_SECTION);

    globalSettingTypes.put(RobotGlobalSettingType.SIMPLE_VALUE_SETTING, SETTING_VALUES);
    globalSettingTypes.put(RobotGlobalSettingType.CONFIGURABLE_SETTING, SETTING);
    globalSettingTypes.put(RobotGlobalSettingType.KEYWORD_CALL_SETTING, KEYWORD_CALL);

    localSettingTypes.put(RobotLocalSettingType.SIMPLE_VALUE_SETTING, LITERAL_CONSTANT_ONLY);
    localSettingTypes.put(RobotLocalSettingType.KEYWORD_CALL_SETTING, KEYWORD_CALL);
    localSettingTypes.put(RobotLocalSettingType.PARAMETER_DEFINITION_SETTING, INLINE_VARIABLE_DEFINITION);
  }

  @Override
  protected int getSectionStateId(@NotNull RobotSectionType sectionType) {
    return sectionTypes.getOrDefault(sectionType, INVALID_SECTION);
  }

  @Override
  protected int getGlobalSettingStateId(@NotNull RobotGlobalSettingType settingType) {
    return globalSettingTypes.getOrDefault(settingType, SETTING_VALUES);
  }

  @Override
  protected int getLocalSettingDefinitionState() {
    return LOCAL_SETTING_DEFINITION;
  }

  @Override
  protected int getLocalSettingStateId(@NotNull RobotLocalSettingType settingType) {
    return localSettingTypes.getOrDefault(settingType, LITERAL_CONSTANT_ONLY);
  }

  @Override
  protected boolean isTemplateSupportingState(int state) {
      return state == TESTCASE_DEFINITION || state == TASK_DEFINITION || state == TEMPLATE_DEFINITION;
  }

  @Override
  protected int @NotNull [] getNextTemplateStates(TemplateParseResult result) {
      return switch (result) {
          case TemplateParseResult.EMPTY_RESET -> new int[] {LITERAL_CONSTANT_ONLY, LOCAL_SETTING_DEFINITION};
          case TemplateParseResult.NONE_RESET -> new int[] {INTERMEDIATE_TEMPLATE_CONFIGURATION, LOCAL_SETTING_DEFINITION};
          case TemplateParseResult.KEYWORD -> new int[] {TEMPLATE_DEFINITION, TEMPLATE_ARGUMENTS, SETTING_TEMPLATE_START, LOCAL_SETTING_DEFINITION};
      };
  }

  @Override
  protected int getKeywordStateId(@NotNull RobotKeywordType keywordType) {
      return KEYWORD_CALL;
  }

  protected void handleStateChangeOnMultiLineDetection() {
    int currentState = yystate();
    if (shouldLeaveStateOnMultilineDetection(currentState)) {
        leaveState();
    } else if (currentState == SINGLE_LITERAL_CONSTANT_START) {
        yybegin(SINGLE_LITERAL_CONSTANT);
    } else if (currentState == PYTHON_EVALUATED_CONTROL_STRUCTURE_START) {
        yybegin(PYTHON_EXECUTED_CONDITION);
    } else if (currentState == PYTHON_EVALUATED_EXPRESSION_START) {
        yybegin(KEYWORD_PYTHON_EXPRESSION);
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
             KEYWORD_PYTHON_EXPRESSION,
             PYTHON_EXECUTED_CONDITION -> handleStateChangeOnFakeMultilineDetection();
    }
  }

  protected boolean shouldLeaveStateOnMultilineDetection(int currentState) {
    return currentState == SINGLE_LITERAL_CONSTANT
          || currentState == PARAMETER_VALUE
          || currentState == TEMPLATE_PARAMETER_VALUE
          || currentState == KEYWORD_PYTHON_EXPRESSION
          || currentState == PYTHON_EXECUTED_CONDITION;
  }
%}

%public
%class RobotLexer
%extends RobotMultiLingualFlexLexerBase
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

LiteralValue = [^\s]+([ ][^\s]+)*[ ]?

SectionIdentifierParts = ({Space}? {Star})* {Space}?
GenericSectionIdentifier = {Star} {SectionIdentifierParts} {LiteralValue} {SpaceBasedEndMarker}? {NON_EOL}*

OpeningVariable = "{"
ClosingVariable = "}"

ScalarVariableStart = "$" {OpeningVariable}
ListVariableStart = "@" {OpeningVariable}
DictVariableStart = "&" {OpeningVariable}
EnvVariableStart = "%" {OpeningVariable}

Test = [Tt][Ee][Ss][Tt]
Continue = [Cc][Oo][Nn][Tt][Ii][Nn][Uu][Ee]
On = [Oo][Nn]
Failure = [Ff][Aa][Ii][Ll][Uu][Rr][Ee]
Keyword = [Kk][Ee][Yy][Ww][Oo][Rr][Dd]
Run = [Rr][Uu][Nn]
If = [Ii][Ff]
Timeout = [Tt][Ii][Mm][Ee][Oo][Uu][Tt]
Return = [Rr][Ee][Tt][Uu][Rr][Nn]
Passed = [Pp][Aa][Ss]{2}[Ee][Dd]
Failed = [Ff][Aa][Ii][Ll][Ee][Dd]
And = [Aa][Nn][Dd]
Error = [Ee][Rr]{2}[Oo][Rr]
Should = [Ss][Hh][Oo][Uu][Ll][Dd]
None = [Nn][Oo][Nn][Ee]
GenericSettingsKeyword = [\w_-]+([ ][\w_-]+)*

VariableCharNotAllowed = [^\s$@%&]
ExceptionForAllowedVariableChar = [$@%&] [^{] | {EscapeChar}{1} [\s$@%&]
AllowedEverythingButVariableChar = {VariableCharNotAllowed} | {ExceptionForAllowedVariableChar}
AllowedEverythingButVariableSeq = {AllowedEverythingButVariableChar}+

AllowedExtendedVariableAccessChar = [^\s\[\]$@%&] | {EscapeChar}{1} "[" | {EscapeChar}{1} "]" | {ExceptionForAllowedVariableChar}
AllowedExtendedVariableAccessSeq = {AllowedExtendedVariableAccessChar}+

VariableLiteralValue =   ([^}$@&%\r\n] | {ExceptionForAllowedVariableChar} | {OpeningVariable})+
EverythingButVariableValue = {AllowedEverythingButVariableSeq} ({Space} {AllowedEverythingButVariableSeq})*
KeywordLibraryNameLiteralValue = {EverythingButVariableValue} "."
ExtendedVariableAccessValue = {AllowedExtendedVariableAccessSeq}

LocalSettingKeywordStartWhitespaceFree = "["
LocalSettingKeywordEndWhitespaceFree = "]"

LocalSettingKeywordStart = {LocalSettingKeywordStartWhitespaceFree} {NonNewlineWhitespace}*
LocalSettingKeywordEnd = {NonNewlineWhitespace}* {LocalSettingKeywordEndWhitespaceFree}
LocalSettingKeyword = {LocalSettingKeywordStart} {GenericSettingsKeyword} {LocalSettingKeywordEnd}

BuiltInNamespace = [Bb][Uu][Ii][Ll][Tt][Ii][Nn]"."
IntraKeywordSeparator = {Space} | "_"+ ({Space} "_"+)*

// Builtin keywords accepting a keyword as an argument and every parameter after that is passed to the called keyword.
AndContinueOnFailure = {Continue} {IntraKeywordSeparator}? {On} {IntraKeywordSeparator}? {Failure}
AndIgnoreError = [Ii][Gg][Nn][Oo][Rr][Ee] {IntraKeywordSeparator}? {Error}
AndReturnStatus = {Return} {IntraKeywordSeparator} ?[Ss][Tt][Aa][Tt][Uu][Ss]
AndWarnOnFailure = [Ww][Aa][Rr][Nn] {IntraKeywordSeparator}? {On} {IntraKeywordSeparator}? {Failure}
RunKeywordCall = {Run} {IntraKeywordSeparator}? {Keyword} ({IntraKeywordSeparator}? And {IntraKeywordSeparator}? ({AndContinueOnFailure} | {AndIgnoreError} | {Return} | {AndReturnStatus} | {AndWarnOnFailure}))?

// Builtin keywords accepting a condition to decide whether to run the keyword or not. After that, working like the builtin keywords above, expecting a keyword
// to execute and its parameters
AllTestsPassed = [Aa][Ll]{2} {IntraKeywordSeparator}? {Test}[Ss] {IntraKeywordSeparator}? {Passed}
AnyTestsFailed = [Aa][Nn][Yy] {IntraKeywordSeparator}? {Test}[Ss] {IntraKeywordSeparator}? {Failed}
TestFailed = {Test} {IntraKeywordSeparator}? {Failed}
TestPassed = {Test} {IntraKeywordSeparator}? {Passed}
TimeoutOccurred = {Timeout} {IntraKeywordSeparator}? [Oo][Cc]{2}[Uu][Rr]{2}[Ee][Dd]
ConditionalRunKeywordCall = {Run} {IntraKeywordSeparator}? {Keyword} {IntraKeywordSeparator}? {If} ({IntraKeywordSeparator}? ({AllTestsPassed} | {AnyTestsFailed} | {TestFailed} | {TestPassed} | {TimeoutOccurred}))?
    | {Run} {IntraKeywordSeparator}? {Keyword} {IntraKeywordSeparator}? [Uu][Nn][Ll][Ee][Ss]{2}
    | {Run} {IntraKeywordSeparator}? {Keyword} {IntraKeywordSeparator}? {And} {IntraKeywordSeparator}? {Return} {IntraKeywordSeparator}? {If}

AssertRunKeywordCall = {Run} {IntraKeywordSeparator}? {Keyword} {IntraKeywordSeparator}? {And} {IntraKeywordSeparator}? [Ee][Xx][Pp][Ee][Cc][Tt] {IntraKeywordSeparator}? {Error}

SetVariableIf = [Ss][Ee][Tt] {IntraKeywordSeparator}? [Vv][Aa][Rr][Ii][Aa][Bb][Ll][Ee] {IntraKeywordSeparator}? {If}
ForLoopIf = ({Continue} | [Ee][Xx][Ii][Tt]) {IntraKeywordSeparator}? [Ff][Oo][Rr] {IntraKeywordSeparator}? [Ll][Oo]{2}[Pp] {IntraKeywordSeparator}? {If}
PassExecutionIf = [Pp][Aa][Ss]{2} {IntraKeywordSeparator}? [Ee][Xx][Ee][Cc][Uu][Tt][Ii][Oo][Nn] {IntraKeywordSeparator}? {IntraKeywordSeparator}? {If}
ReturnFromKeywordIf = {Return} {IntraKeywordSeparator}? [Ff][Rr][Oo][Mm] {IntraKeywordSeparator}? {Keyword} {IntraKeywordSeparator}? {If}
SkipIf = [Ss][Kk][Ii][Pp] {IntraKeywordSeparator}? {If}

ShouldBeTrue = {Should} {IntraKeywordSeparator}? [Bb][Ee] {IntraKeywordSeparator}? [Tt][Rr][Uu][Ee]
ShouldNotBeTrue = {Should} {IntraKeywordSeparator}? [Nn][Oo][Tt] {IntraKeywordSeparator}? [Bb][Ee] {IntraKeywordSeparator}? [Tt][Rr][Uu][Ee]

SimpleConditionalKeywordCall = {SetVariableIf} | {ForLoopIf} | {PassExecutionIf} | {ReturnFromKeywordIf} | {SkipIf} | {ShouldBeTrue} | {ShouldNotBeTrue}

RepeatKeywordCall = [Rr][Ee][Pp][Ee][Aa][Tt] {IntraKeywordSeparator}? {Keyword}

EvaluateKeywordCall = [Ee][Vv][Aa][Ll][Uu][Aa][Tt][Ee]

LineComment = {LineCommentSign} {NON_EOL}*

%state SETTINGS_SECTION, VARIABLES_SECTION, TEST_CASES_SECTION_NAME_DEFINITION, TASKS_SECTION_NAME_DEFINITION
%state TESTCASE_NAME_DEFINITION, TESTCASE_DEFINITION, TASK_NAME_DEFINITION, TASK_DEFINITION
%state USER_KEYWORD_NAME_DEFINITION, USER_KEYWORD_DEFINITION, USER_KEYWORD_RETURN_STATEMENT
%state SETTING, SETTING_TEMPLATE_START, LOCAL_TEMPLATE_DEFINITION_START, INTERMEDIATE_TEMPLATE_CONFIGURATION, TEMPLATE_DEFINITION, TEMPLATE_ARGUMENTS
%state KEYWORD_CALL, KEYWORD_ARGUMENTS, SINGLE_LITERAL_CONSTANT_START, SINGLE_LITERAL_CONSTANT
%state INLINE_VARIABLE_DEFINITION, VARIABLE_DEFINITION, VARIABLE_DEFINITION_ARGUMENTS, VARIABLE_USAGE, EXTENDED_VARIABLE_ACCESS
%state PARAMETER_VALUE, TEMPLATE_PARAMETER_VALUE
%state FOR_STRUCTURE, SIMPLE_CONTROL_STRUCTURE_START, FOR_STRUCTURE_LOOP_START, SIMPLE_CONTROL_STRUCTURE, FOR_STRUCTURE_LOOP, WHILE_CONFIGURATION
%state PYTHON_EVALUATED_EXPRESSION_START, KEYWORD_PYTHON_EXPRESSION, PYTHON_EXPRESSION, PYTHON_EXECUTED_CONDITION, PYTHON_EVALUATED_CONTROL_STRUCTURE_START

%xstate COMMENTS_SECTION, INVALID_SECTION, LITERAL_CONSTANT_ONLY, SETTING_VALUES, LOCAL_SETTING_DEFINITION
%xstate NORMAL_PARAMETER_ASSIGNMENT, TEMPLATE_PARAMETER_ASSIGNMENT
%xstate KEYWORD_LIBRARY_NAME_SEPARATOR, KEYWORD_CALL_NAME, KEYWORD_LIBRARY_NAME_SEPARATOR_FOR_SPECIAL_KEYWORD
%xstate IN_CONTINUATION, AFTER_CONTINUATION, FAKE_MULTILINE, SAME_LINE_FAKE_MULTILINE, AFTER_COMMENT
%xstate VARIABLE_OPENING_BRACE, EOL_EXPECTED

%%

^ {NonNewlineWhitespace}+ {LineComment} {WhitespaceIncludingNewline}*      { enterNewState(AFTER_COMMENT); pushBackEverythingExceptLeadingWhitespace(); return WHITE_SPACE; }
^ {LineComment}                                                            { enterNewState(AFTER_COMMENT); return COMMENT; }
{LineComment}                                                              { return COMMENT; }

<SETTING, SETTING_VALUES, VARIABLE_DEFINITION_ARGUMENTS, KEYWORD_ARGUMENTS, KEYWORD_CALL, USER_KEYWORD_RETURN_STATEMENT, FOR_STRUCTURE_LOOP_START, FOR_STRUCTURE_LOOP, WHILE_CONFIGURATION, SIMPLE_CONTROL_STRUCTURE, LITERAL_CONSTANT_ONLY, VARIABLE_DEFINITION, INLINE_VARIABLE_DEFINITION, SETTING_TEMPLATE_START> {
    {EOL} {NonNewlineWhitespace}* ({LineComment} ({EOL} {NonNewlineWhitespace}*)+)+ {MultiLineContinuation}                 { enterNewState(IN_CONTINUATION); pushBackEverythingExceptLeadingWhitespace(); return WHITE_SPACE; }
    {EOL} {NonNewlineWhitespace}* {LineComment} ({EOL} {NonNewlineWhitespace}*)+ .?                                         { enterNewState(FAKE_MULTILINE); pushBackEverythingExceptLeadingWhitespace(); return WHITE_SPACE; }

    {SpaceBasedEndMarker} {NonNewlineWhitespace}* ({LineComment} ({EOL} {NonNewlineWhitespace}*)+)+ {MultiLineContinuation} { enterNewState(IN_CONTINUATION); pushBackEverythingExceptLeadingWhitespace(); return WHITE_SPACE; }
    {SpaceBasedEndMarker} {NonNewlineWhitespace}* {LineComment} ({EOL} {NonNewlineWhitespace}*)+ .?                         { pushBackEverythingExceptLeadingWhitespace(); enterNewState(SAME_LINE_FAKE_MULTILINE); return WHITE_SPACE; }

    {MultiLine} {WhitespaceIncludingNewline}*                                                                               { yypushback(yylength()); enterNewState(IN_CONTINUATION); break; }
}

<FAKE_MULTILINE, SAME_LINE_FAKE_MULTILINE>  {
    {EOL}                                                   { handleStateChangeOnFakeMultilineDetection(); return EOL; }
    {EOL} {NonNewlineWhitespace}* {LineComment}             { pushBackEverythingExceptLeadingWhitespace(); return WHITE_SPACE; }
    {LineComment}                                           { return COMMENT; }
}
<AFTER_COMMENT>   {
    {WhitespaceIncludingNewline}+                           { leaveState(); return WHITE_SPACE; }
    {WhitespaceIncludingNewline}+ {MultiLineContinuation}   { yypushback(yylength()); leaveState(); break; }
    {LineComment}                                           { return COMMENT; }
    [^]                                                     { yypushback(yylength()); leaveState(); break; }
}

^ {GenericSectionIdentifier} {EOL}    { resetInternalState(); yypushback(1); return switchSection(); }
^ {GenericSectionIdentifier}          { resetInternalState(); return switchSection(); }

<TEST_CASES_SECTION_NAME_DEFINITION>  {
    {EOL}                             { yybegin(TESTCASE_NAME_DEFINITION); return EOL; }
}
<TASKS_SECTION_NAME_DEFINITION>       {
    {EOL}                             { yybegin(TASK_NAME_DEFINITION); return EOL; }
}
<TEST_CASES_SECTION_NAME_DEFINITION, TASKS_SECTION_NAME_DEFINITION>  {
    {EOL} {EOL}+                      { yypushback(1); return WHITE_SPACE; }
    {LiteralValue}                    { pushBackTrailingWhitespace(); return DATA_DRIVEN_COLUMN_NAME; }
}

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
          return CONTINUATION;
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
    {EOL}                                                    { leaveState(); return EOL; }
}

<VARIABLE_DEFINITION_ARGUMENTS> {
    {EqualSign} {NonNewlineWhitespace}* {EverythingButVariableValue}?       { yypushback(yylength() - 1); return ASSIGNMENT; }
    [Ss][Cc][Oo][Pp][Ee] {EqualSign} !{KeywordFinishedMarker}               { yypushback(yylength() - "scope".length()); enterNewState(NORMAL_PARAMETER_ASSIGNMENT); return PARAMETER_NAME; }
    {EverythingButVariableValue}                                            { return LITERAL_CONSTANT; }
    {EOL}                                                                   { leaveState(); return EOL; }
}

<VARIABLE_USAGE> {
    {ClosingVariable} "["                           { leaveState(); enterNewState(EXTENDED_VARIABLE_ACCESS); yypushback(1); return VARIABLE_RBRACE; }
    {ClosingVariable} "]"                           { leaveState(); yypushback(1); return VARIABLE_RBRACE; }
    {ClosingVariable}                               { leaveState(); return VARIABLE_RBRACE; }
    {OpeningVariable} (!{ClosingVariable}{2})+      { enterNewState(PYTHON_EXPRESSION); yypushback(yylength() - 1); return PYTHON_EXPRESSION_START; }
}

<VARIABLE_DEFINITION, VARIABLE_USAGE> {
    {VariableLiteralValue}                          { return VARIABLE_BODY; }
    {EOL}                                           { leaveState(); return EOL; }
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
    {EOL}                                           { leaveState(); return EOL; }
}

<PYTHON_EXPRESSION> {
    {ClosingVariable}{2}         { leaveState(); yypushback(1); return PYTHON_EXPRESSION_END; }
    ( [^}] | }[^}] )+            { return PYTHON_EXPRESSION_CONTENT; }
}
<KEYWORD_PYTHON_EXPRESSION> {
    {EverythingButVariableValue} { return PYTHON_EXPRESSION_CONTENT; }
    {SpaceBasedEndMarker}        { leaveState(); return WHITE_SPACE; }
    {EOL} | {MultiLine}          { leaveState(); yypushback(yylength()); break; }
}

<SETTINGS_SECTION> {
    ^ {GenericSettingsKeyword} {ExtendedKeywordFinishedMarker}         { return switchGlobalSetting(); }
}

<SETTING> {
    {WithNameKeyword} {ExtendedSpaceBasedEndMarker}          { pushBackTrailingWhitespace(); return WITH_NAME; }
    {EOL}                                                    { leaveState(); return EOL; }
}

<TESTCASE_NAME_DEFINITION>     {
    {EverythingButVariableValue}                             { pushBackTrailingWhitespace(); return TEST_CASE_NAME_PART; }
    {SpaceBasedEndMarker} {NonNewlineWhitespace}*            { yybegin(TESTCASE_DEFINITION); return EOS; }
    {EOL}                                                    { yybegin(TESTCASE_DEFINITION); return EOL; }
}
<TASK_NAME_DEFINITION>         {
    {EverythingButVariableValue}                             { pushBackTrailingWhitespace(); return TASK_NAME_PART; }
    {SpaceBasedEndMarker} {NonNewlineWhitespace}*            { yybegin(TASK_DEFINITION); return EOS; }
    {EOL}                                                    { yybegin(TASK_DEFINITION); return EOL; }
}
<USER_KEYWORD_NAME_DEFINITION> {
    {EverythingButVariableValue}                             { pushBackTrailingWhitespace(); return USER_KEYWORD_NAME_PART; }
    {SpaceBasedEndMarker} {NonNewlineWhitespace}*            { yybegin(USER_KEYWORD_DEFINITION); return EOS; }
    {EOL}                                                    { yybegin(USER_KEYWORD_DEFINITION); return EOL; }
}
<TESTCASE_NAME_DEFINITION, TASK_NAME_DEFINITION, USER_KEYWORD_NAME_DEFINITION> {
    {SpaceBasedEndMarker} {NonNewlineWhitespace}* {EOL}      { yypushback(1); return WHITE_SPACE; }
}

<TESTCASE_DEFINITION>          ^ [^\s#] {NON_EOL}+ {EOL}*    { resetTemplateState(); yypushback(yylength()); yybegin(TESTCASE_NAME_DEFINITION); break; }
<TASK_DEFINITION>              ^ [^\s#] {NON_EOL}+ {EOL}*    { resetTemplateState(); yypushback(yylength()); yybegin(TASK_NAME_DEFINITION); break; }
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
    {ExtendedSpaceBasedEndMarker} {None} {ExtendedKeywordFinishedMarker}  {
          pushBackTrailingWhitespace();
          yypushback("NONE".length());
          return WHITE_SPACE;
    }
    {NonNewlineWhitespace}* {MultiLine} {None} {ExtendedKeywordFinishedMarker}  {
          pushBackTrailingWhitespace();
          yypushback("NONE".length());
          pushBackTrailingWhitespace();
          yypushback("...".length());
          enterNewState(IN_CONTINUATION);
          return WHITE_SPACE;
    }
    {None} {ExtendedKeywordFinishedMarker}  {
          yypushback(yylength() - "NONE".length());
          yybegin(EOL_EXPECTED);
          return LITERAL_CONSTANT;
    }
}

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
<TESTCASE_DEFINITION, TASK_DEFINITION, USER_KEYWORD_DEFINITION, TEMPLATE_DEFINITION> {
    <USER_KEYWORD_DEFINITION> {
        {LocalSettingKeyword} {ExtendedKeywordFinishedMarker}   { return switchLocalSetting(); }
    }

    <TEMPLATE_ARGUMENTS> {
        "FOR" {ExtendedSpaceBasedEndMarker}?         { yypushback(yylength() - "FOR".length()); enterNewState(FOR_STRUCTURE); return FOR; }
        "WHILE" {ExtendedSpaceBasedEndMarker}?       { yypushback(yylength() - "WHILE".length()); enterNewState(WHILE_CONFIGURATION); enterNewState(PYTHON_EVALUATED_CONTROL_STRUCTURE_START); return WHILE; }
        "IF" {ExtendedSpaceBasedEndMarker}?          { yypushback(yylength() - "IF".length()); enterNewState(EOL_EXPECTED); enterNewState(PYTHON_EVALUATED_CONTROL_STRUCTURE_START); return IF; }
        "ELSE IF" {ExtendedSpaceBasedEndMarker}?     { yypushback(yylength() - "ELSE IF".length()); enterNewState(EOL_EXPECTED); enterNewState(PYTHON_EVALUATED_CONTROL_STRUCTURE_START); return ELSE_IF; }
        "ELSE" {ExtendedKeywordFinishedMarker}?      { yypushback(yylength() - "ELSE".length()); enterNewState(EOL_EXPECTED); return ELSE; }
        "BREAK" {ExtendedKeywordFinishedMarker}?     { yypushback(yylength() - "BREAK".length()); enterNewState(EOL_EXPECTED); return BREAK; }
        "CONTINUE" {ExtendedKeywordFinishedMarker}?  { yypushback(yylength() - "CONTINUE".length()); enterNewState(EOL_EXPECTED); return CONTINUE; }
        "END" {ExtendedKeywordFinishedMarker}?       { yypushback(yylength() - "END".length()); enterNewState(EOL_EXPECTED); return END; }
    }
}
<TESTCASE_DEFINITION, TASK_DEFINITION, USER_KEYWORD_DEFINITION> {
    "TRY" {ExtendedKeywordFinishedMarker}?       { yypushback(yylength() - "TRY".length()); enterNewState(EOL_EXPECTED); return TRY; }
    "EXCEPT" {ExtendedSpaceBasedEndMarker}?      {
      enterNewState(SIMPLE_CONTROL_STRUCTURE_START);
      yypushback(yylength() - "EXCEPT".length());
      return EXCEPT;
    }
    "FINALLY" {ExtendedKeywordFinishedMarker}?   { yypushback(yylength() - "FINALLY".length()); enterNewState(EOL_EXPECTED); return FINALLY; }
    "GROUP" {ExtendedKeywordFinishedMarker}?     {
      enterNewState(SIMPLE_CONTROL_STRUCTURE_START);
      yypushback(yylength() - "GROUP".length());
      return GROUP;
    }
}
<TESTCASE_DEFINITION, TASK_DEFINITION> {
    "VAR" {ExtendedSpaceBasedEndMarker}     {
          yypushback(yylength() - "VAR".length());
          enterNewState(INLINE_VARIABLE_DEFINITION);
          return VAR;
    }

    {EverythingButVariableValue} {
          if (getLocalTemplateEnabled() && getTemplateKeywordFound()) {
              enterNewState(TEMPLATE_DEFINITION);
              enterNewState(TEMPLATE_ARGUMENTS);
              yypushback(yylength());
              break;
          }
          IElementType elementType = switchPotentialKeyword();
          if (elementType != null) {
              return elementType;
          }
          break;
   }

   {ScalarVariableStart} | {ListVariableStart} | {DictVariableStart} | {EnvVariableStart}  {
         if (getLocalTemplateEnabled() && getTemplateKeywordFound()) {
             enterNewState(TEMPLATE_DEFINITION);
             enterNewState(TEMPLATE_ARGUMENTS);
         } else {
             enterNewState(VARIABLE_USAGE);
             enterNewState(VARIABLE_OPENING_BRACE);
             yypushback(1);
             return switch(yycharat(0)) {
                 case '$' -> SCALAR_VARIABLE_START;
                 case '@' -> LIST_VARIABLE_START;
                 case '&' -> DICT_VARIABLE_START;
                 case '%' -> ENV_VARIABLE_START;
                 default -> null;
             };
         }
         yypushback(yylength());
         break;
   }
}

<TEMPLATE_ARGUMENTS> {
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

<TEMPLATE_DEFINITION> {
    ^ [^\s#] {NON_EOL}+ {EOL}*    {
        resetTemplateState();
        leaveState();
        yypushback(yylength());
        if (yystate() == TESTCASE_DEFINITION) {
            yybegin(TESTCASE_NAME_DEFINITION);
        } else {
            yybegin(TASK_NAME_DEFINITION);
        }
        break;
    }
    ^ {NonNewlineWhitespace} {NonNewlineWhitespace}+ [^\[#\s]{1}     { enterNewState(TEMPLATE_ARGUMENTS); yypushback(yylength()); break; }
}

<FOR_STRUCTURE>  {
    "IN" {ExtendedKeywordFinishedMarker}            { yypushback(yylength() - "IN".length()); yybegin(FOR_STRUCTURE_LOOP_START); return FOR_IN; }
    "IN ENUMERATE" {ExtendedKeywordFinishedMarker}  { yypushback(yylength() - "IN ENUMERATE".length()); yybegin(FOR_STRUCTURE_LOOP_START); return FOR_IN_ENUMERATE; }
    "IN RANGE" {ExtendedKeywordFinishedMarker}      { yypushback(yylength() - "IN RANGE".length()); yybegin(FOR_STRUCTURE_LOOP_START); return FOR_IN_RANGE; }
    "IN ZIP" {ExtendedKeywordFinishedMarker}        { yypushback(yylength() - "IN ZIP".length()); yybegin(FOR_STRUCTURE_LOOP_START); return FOR_IN_ZIP; }
    "END" {EOL}                                     { yypushback(1); return END; }
    {EOL}                                           { leaveState(); return EOL; }
}

<SIMPLE_CONTROL_STRUCTURE_START>            {
    {ExtendedSpaceBasedEndMarker}           { yybegin(SIMPLE_CONTROL_STRUCTURE); return WHITE_SPACE; }
    {EOL}                                   { leaveState(); return EOL; }
}
<FOR_STRUCTURE_LOOP_START> {
    {ExtendedSpaceBasedEndMarker}           { yybegin(FOR_STRUCTURE_LOOP); return WHITE_SPACE; }
    [^]                                     { yypushback(yylength()); yybegin(FOR_STRUCTURE_LOOP); break; }
}
<PYTHON_EVALUATED_EXPRESSION_START>  {
    {ExtendedSpaceBasedEndMarker}           { yybegin(KEYWORD_PYTHON_EXPRESSION); return WHITE_SPACE; }
    {MultiLine}                             { enterNewState(IN_CONTINUATION); yypushback(yylength()); break; }
    [^]                                     { yypushback(yylength()); yybegin(KEYWORD_PYTHON_EXPRESSION); break; }
}
<PYTHON_EVALUATED_CONTROL_STRUCTURE_START>  {
    {ExtendedSpaceBasedEndMarker}           { yybegin(PYTHON_EXECUTED_CONDITION); return WHITE_SPACE; }
    {MultiLine}                             { enterNewState(IN_CONTINUATION); yypushback(yylength()); break; }
    [^]                                     { yypushback(yylength()); yybegin(PYTHON_EXECUTED_CONDITION); break; }
}
<PYTHON_EXECUTED_CONDITION>  {
    {EverythingButVariableValue}            { return PYTHON_EXPRESSION_CONTENT; }
    {ExtendedSpaceBasedEndMarker}           { leaveState(); return EOS; }
    {EOL}                                   { leaveState(); yypushback(yylength()); break; }
    {MultiLine}                             {
      if (getPreviousStates()[getCurrentIndex()] == WHILE_CONFIGURATION) {
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
    {EOL}                                   { leaveState(); return EOL; }
    [^]                                     { yypushback(yylength()); leaveState(); break; }
}

<SIMPLE_CONTROL_STRUCTURE> {
    <FOR_STRUCTURE_LOOP> {EverythingButVariableValue}        { return LITERAL_CONSTANT; }
    {ExtendedSpaceBasedEndMarker}                            { leaveState(); return EOL; }
}

<USER_KEYWORD_RETURN_STATEMENT, FOR_STRUCTURE_LOOP, TEMPLATE_ARGUMENTS> {EOL}    { leaveState(); return EOL; }

<NORMAL_PARAMETER_ASSIGNMENT> {EqualSign} { yybegin(PARAMETER_VALUE); return PARAMETER_ASSIGNMENT; }
<PARAMETER_VALUE>       {
    {EverythingButVariableValue}          { return LITERAL_CONSTANT; }
    <TEMPLATE_PARAMETER_VALUE> {
        {ExtendedKeywordFinishedMarker}   { leaveState(); yypushback(yylength()); break; }
    }
}

<SETTING_TEMPLATE_START>  {
    {KeywordLibraryNameLiteralValue} {EverythingButVariableValue}        {
          markTemplateKeywordFound();
          int libraryNameSeparatorStart = indexOf('.');
          yypushback(yylength() - libraryNameSeparatorStart);
          yybegin(KEYWORD_LIBRARY_NAME_SEPARATOR);
          return KEYWORD_LIBRARY_NAME;
    }
    {EverythingButVariableValue}                                         { markTemplateKeywordFound(); return KEYWORD_NAME; }
    {EOL}                                                                { leaveState(); return EOL; }
}

<KEYWORD_CALL>  {
    {RunKeywordCall}                                               { return KEYWORD_NAME; }
    {ConditionalRunKeywordCall}                                    { enterNewState(PYTHON_EVALUATED_CONTROL_STRUCTURE_START); return KEYWORD_NAME; }
    {AssertRunKeywordCall} | {RepeatKeywordCall}                   { enterNewState(SINGLE_LITERAL_CONSTANT_START); return KEYWORD_NAME; }
    {SimpleConditionalKeywordCall}                                 {
          yybegin(KEYWORD_ARGUMENTS);
          enterNewState(PYTHON_EVALUATED_CONTROL_STRUCTURE_START);
          return KEYWORD_NAME;
    }
    {RepeatKeywordCall}                                            { return KEYWORD_NAME; }
    {EvaluateKeywordCall}                                          { yybegin(KEYWORD_ARGUMENTS); enterNewState(PYTHON_EVALUATED_EXPRESSION_START); return KEYWORD_NAME; }

    {BuiltInNamespace} ({RunKeywordCall} | {ConditionalRunKeywordCall} | {AssertRunKeywordCall} | {SimpleConditionalKeywordCall} | {RepeatKeywordCall}) {
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
    {EOL}                                                          { leaveState(); return EOL; }
}
<KEYWORD_LIBRARY_NAME_SEPARATOR> "."                               { yybegin(KEYWORD_CALL_NAME); return KEYWORD_LIBRARY_SEPARATOR; }
<KEYWORD_LIBRARY_NAME_SEPARATOR_FOR_SPECIAL_KEYWORD> "."           { leaveState(); return KEYWORD_LIBRARY_SEPARATOR; }
<KEYWORD_CALL_NAME> {EverythingButVariableValue}                   { leaveState(); return KEYWORD_NAME; }

<KEYWORD_ARGUMENTS> {
    "ELSE IF" {ExtendedSpaceBasedEndMarker}     { yypushback(yylength() - "ELSE IF".length()); yybegin(PYTHON_EVALUATED_CONTROL_STRUCTURE_START); return ELSE_IF; }
    "ELSE" {ExtendedKeywordFinishedMarker}      { yypushback(yylength() - "ELSE".length()); yybegin(KEYWORD_CALL); return ELSE; }
    {EverythingButVariableValue}                { return LITERAL_CONSTANT; }
    {EOL}                                       { leaveState(); return EOL; }
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
    {EOL}                                                    { leaveState(); return EOL; }
}

<SETTINGS_SECTION, SETTING, USER_KEYWORD_RETURN_STATEMENT, SINGLE_LITERAL_CONSTANT>  {EverythingButVariableValue}  { return LITERAL_CONSTANT; }

<SINGLE_LITERAL_CONSTANT_START>  {SpaceBasedEndMarker}       { yybegin(SINGLE_LITERAL_CONSTANT); return WHITE_SPACE; }
<SINGLE_LITERAL_CONSTANT>        {SpaceBasedEndMarker}       { leaveState(); return WHITE_SPACE; }

<VARIABLE_OPENING_BRACE> {
    {OpeningVariable}        { leaveState(); return VARIABLE_LBRACE; }
    [^]                      { leaveState(); yypushback(yylength()); break; }
}

<EOL_EXPECTED> {
    {NonNewlineWhitespace}+  { return WHITE_SPACE; }
    {EOL}                    { leaveState(); return EOL; }
    [^]                      { yypushback(yylength()); leaveState(); break; }
}

<COMMENTS_SECTION> {
    <INVALID_SECTION> {
        ^ {GenericSectionIdentifier} {EOL}    { resetInternalState(); yypushback(1); return switchSection(); }
        ^ {GenericSectionIdentifier}          { resetInternalState(); return switchSection(); }
    }
    <YYINITIAL> {
        <INVALID_SECTION> {
            {WhitespaceIncludingNewline}+     { return WHITE_SPACE; }
        }
        {NON_EOL}+                            { return COMMENT; }
    }
}
<INVALID_SECTION> {
    [^]   { return BAD_CHARACTER; }
}

{ScalarVariableStart} | {ListVariableStart} | {DictVariableStart} | {EnvVariableStart}  {
      enterNewState(VARIABLE_USAGE);
      enterNewState(VARIABLE_OPENING_BRACE);
      yypushback(1);
      return switch(yycharat(0)) {
          case '$' -> SCALAR_VARIABLE_START;
          case '@' -> LIST_VARIABLE_START;
          case '&' -> DICT_VARIABLE_START;
          case '%' -> ENV_VARIABLE_START;
          default -> null;
      };
}

{EmptyValue} {WhitespaceIncludingNewline}*      { yypushback(yylength() - 2); return LITERAL_CONSTANT; }

// Can't be combined to {WhitespaceIncludingNewline}+ because then it would override the EOL handling in various states if there is a newline followed by
// whitespace. It is just a fallback for any whitespace that is not handled in other states.
{NonNewlineWhitespace}+ | {EOL}                 { return WHITE_SPACE; }

[^] { return BAD_CHARACTER; }
