package dev.xeonkryptos.xeonrobotframeworkplugin.psi;

import com.intellij.psi.tree.IElementType;

import static com.intellij.psi.TokenType.*;
import static dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes.*;
%%

%{
  public RobotExtendedVariableAccessLayerLexer() {
      this((java.io.Reader)null);
  }
%}

%public
%buffer 65536
%class RobotExtendedVariableAccessLayerLexer
%extends AbstractRobotLexer
%function advance
%type IElementType
%unicode
%caseless

Space = " "
Tab = \t
NBSP = \u00A0
Whitespace = {Space} | {Tab} | {NBSP}

VariableSliceAccess = \s* (-?\d+)? \s* : \s* (-?\d+)? (\s* : \s* (-?\d+))? \s*
VariableIndexAccess = \s* \d+ \s*

%%

{VariableSliceAccess}        { return VARIABLE_SLICE_ACCESS; }
{VariableIndexAccess}        { return VARIABLE_INDEX_ACCESS; }

{Whitespace}+   { return WHITE_SPACE; }

[^\\] "]" | \R  { return null; }

[^]+ { return LITERAL_CONSTANT; }
