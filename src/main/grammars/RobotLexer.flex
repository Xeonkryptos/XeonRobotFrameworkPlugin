package dev.xeonkryptos.xeonrobotframeworkplugin.psi;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes.*;

%%

%{
  public RobotLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class RobotLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

EOL=\R
WHITE_SPACE=\s+

SPACE=[ ]
TAB=\t
EOL=\r?\n
HASH=#
PIPE=\|
STAR=\*
STARS=\*{3}
DOT=\.
LBRACKET=\[
RBRACKET=\]
LBRACE=\{
RBRACE=\}
EQUALS==
SCALAR_VARIABLE_START=\$\{
LIST_VARIABLE_START=@\{
DICT_VARIABLE_START=&\{
ENV_VARIABLE_START=%\{
IF=IF
ELSE=ELSE
ELSE_IF=ELSE[ ]IF
END=END
FOR=FOR
IN=IN
TRY=TRY
EXCEPT=EXCEPT
FINALLY=FINALLY
WHILE=WHILE
RETURN=RETURN
BREAK=BREAK
CONTINUE=CONTINUE
VAR=VAR
ARG=arg
SETTINGS_WORDS=(Settings|Setting|Einstellungen|Configuración|Asetukset|Nastavení)
VARIABLES_WORDS=(Variables|Variable|Variablen|Muuttujat|Proměnné)
TESTCASES_WORDS=(Test[ ]Cases|Test[ ]Case|Testfälle|Casos[ ]de[ ]prueba|Testitapaukset|Testovací[ ]případy)
TASKS_WORDS=(Tasks|Task|Aufgaben|Tareas|Tehtävät|Úkoly)
KEYWORDS_WORDS=(Keywords|Keyword|User[ ]Keywords|Schlüsselwörter|Palabras[ ]clave|Avainsanat|Klíčová[ ]slova)
COMMENTS_WORDS=(Comments|Comment|Kommentare|Comentarios|Kommentit|Komentáře)
LIBRARY_WORDS=(Library|Bibliothek|Biblioteca|Kirjasto)
WITH_NAME_WORDS=(WITH[ ]NAME|MIT[ ]NAME|CON[ ]NOMBRE|NIMELLÄ)
QUOTED_STRING=\"([^\\\"]|\\.)*\"|'([^\']|\\.)*'
VARIABLE_NAME=[A-Za-z0-9_]+
SIMPLE_SETTING_NAME=[A-Za-z0-9 ]+
SETTING_NAME_CONTENT=[A-Za-z0-9 ]+
NAME=[^$@&#\t\n\r][^\t\n\r]*
CELL_CONTENT=[^$@&#|#\t\n\r][^|#\t\n\r]*
UNQUOTED_STRING=[^$@&#{}| \t\n\r][^{}| \t\n\r]*
NON_EOL=[^\n\r]*

%%
<YYINITIAL> {
  {WHITE_SPACE}                 { return WHITE_SPACE; }


  {SPACE}                       { return SPACE; }
  {TAB}                         { return TAB; }
  {EOL}                         { return EOL; }
  {HASH}                        { return HASH; }
  {PIPE}                        { return PIPE; }
  {STAR}                        { return STAR; }
  {STARS}                       { return STARS; }
  {DOT}                         { return DOT; }
  {LBRACKET}                    { return LBRACKET; }
  {RBRACKET}                    { return RBRACKET; }
  {LBRACE}                      { return LBRACE; }
  {RBRACE}                      { return RBRACE; }
  {EQUALS}                      { return EQUALS; }
  {SCALAR_VARIABLE_START}       { return SCALAR_VARIABLE_START; }
  {LIST_VARIABLE_START}         { return LIST_VARIABLE_START; }
  {DICT_VARIABLE_START}         { return DICT_VARIABLE_START; }
  {ENV_VARIABLE_START}          { return ENV_VARIABLE_START; }
  {IF}                          { return IF; }
  {ELSE}                        { return ELSE; }
  {ELSE_IF}                     { return ELSE_IF; }
  {END}                         { return END; }
  {FOR}                         { return FOR; }
  {IN}                          { return IN; }
  {TRY}                         { return TRY; }
  {EXCEPT}                      { return EXCEPT; }
  {FINALLY}                     { return FINALLY; }
  {WHILE}                       { return WHILE; }
  {RETURN}                      { return RETURN; }
  {BREAK}                       { return BREAK; }
  {CONTINUE}                    { return CONTINUE; }
  {VAR}                         { return VAR; }
  {ARG}                         { return ARG; }
  {SETTINGS_WORDS}              { return SETTINGS_WORDS; }
  {VARIABLES_WORDS}             { return VARIABLES_WORDS; }
  {TESTCASES_WORDS}             { return TESTCASES_WORDS; }
  {TASKS_WORDS}                 { return TASKS_WORDS; }
  {KEYWORDS_WORDS}              { return KEYWORDS_WORDS; }
  {COMMENTS_WORDS}              { return COMMENTS_WORDS; }
  {LIBRARY_WORDS}               { return LIBRARY_WORDS; }
  {WITH_NAME_WORDS}             { return WITH_NAME_WORDS; }
  {QUOTED_STRING}               { return QUOTED_STRING; }
  {VARIABLE_NAME}               { return VARIABLE_NAME; }
  {SIMPLE_SETTING_NAME}         { return SIMPLE_SETTING_NAME; }
  {SETTING_NAME_CONTENT}        { return SETTING_NAME_CONTENT; }
  {NAME}                        { return NAME; }
  {CELL_CONTENT}                { return CELL_CONTENT; }
  {UNQUOTED_STRING}             { return UNQUOTED_STRING; }
  {NON_EOL}                     { return NON_EOL; }

}

[^] { return BAD_CHARACTER; }
