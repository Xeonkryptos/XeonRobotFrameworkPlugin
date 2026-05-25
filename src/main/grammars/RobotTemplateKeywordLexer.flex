package dev.xeonkryptos.xeonrobotframeworkplugin.psi;

import dev.xeonkryptos.xeonrobotframeworkplugin.lexer.TemplateParseResult;

import static dev.xeonkryptos.xeonrobotframeworkplugin.lexer.TemplateParseResult.*;

%%

%{
  public RobotTemplateKeywordLexer() {
      this(null);
  }
%}

%public
%class RobotTemplateKeywordLexer
%function advance
%type TemplateParseResult
%unicode

// \u00A0 => NBSP (non-breaking space)
Space = " " | \u00A0
Tab = \t

Continuation = "..."

EOL = (\r) | (\n) | (\r\n)

NonNewlineWhitespace = [^\S\r\n]
WhitespaceIncludingNewline = \s

SpaceBasedEndMarker = {Space}{2} | {Tab}
ExtendedSpaceBasedEndMarker = {SpaceBasedEndMarker} {WhitespaceIncludingNewline}*

KeywordFinishedMarker = {SpaceBasedEndMarker} | {EOL}
ExtendedKeywordFinishedMarker = {KeywordFinishedMarker} {WhitespaceIncludingNewline}*

MultiLineContinuation = {Continuation} ({SpaceBasedEndMarker} | {EOL})

MultiLineStart = ({EOL} {NonNewlineWhitespace}*)+
MultiLine = {MultiLineStart} ({MultiLineContinuation} {NonNewlineWhitespace}*)+

None = [Nn][Oo][Nn][Ee]
%%

// Disables the template for the current test case / task due to the NONE "keyword"
({ExtendedSpaceBasedEndMarker} | {MultiLine}) {None} {ExtendedKeywordFinishedMarker}   {
      return NONE_RESET;
}
// Expects a template name (keyword) following the [Template] setting. Can be on the same line or on a new line after the continuation marker
({ExtendedSpaceBasedEndMarker} | {MultiLine})   {
      return KEYWORD;
}
// Represents the [Template] configuration WITHOUT any template name to "deactivate" the template for the current test case / task.
{NonNewlineWhitespace}* {EOL} {
      return EMPTY_RESET;
}
[^] { return null; }
