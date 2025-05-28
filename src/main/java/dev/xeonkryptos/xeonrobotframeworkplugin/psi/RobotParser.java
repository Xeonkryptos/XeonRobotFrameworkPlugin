// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes.*;
import static dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotParserUtil.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class RobotParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, null);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    r = parse_root_(t, b);
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b) {
    return parse_root_(t, b, 0);
  }

  static boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return file(b, l + 1);
  }

  /* ********************************************************** */
  // LBRACKET SETTING_NAME_CONTENT RBRACKET
  public static boolean bracket_setting_name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bracket_setting_name")) return false;
    if (!nextTokenIs(b, LBRACKET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, LBRACKET, SETTING_NAME_CONTENT, RBRACKET);
    exit_section_(b, m, BRACKET_SETTING_NAME, r);
    return r;
  }

  /* ********************************************************** */
  // bracket_setting_name whitespace+ setting_value
  public static boolean bracket_setting_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bracket_setting_statement")) return false;
    if (!nextTokenIs(b, LBRACKET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = bracket_setting_name(b, l + 1);
    r = r && bracket_setting_statement_1(b, l + 1);
    r = r && setting_value(b, l + 1);
    exit_section_(b, m, BRACKET_SETTING_STATEMENT, r);
    return r;
  }

  // whitespace+
  private static boolean bracket_setting_statement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bracket_setting_statement_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = whitespace(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!whitespace(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "bracket_setting_statement_1", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // BREAK (whitespace+ value)?
  public static boolean break_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "break_statement")) return false;
    if (!nextTokenIs(b, BREAK)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, BREAK);
    r = r && break_statement_1(b, l + 1);
    exit_section_(b, m, BREAK_STATEMENT, r);
    return r;
  }

  // (whitespace+ value)?
  private static boolean break_statement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "break_statement_1")) return false;
    break_statement_1_0(b, l + 1);
    return true;
  }

  // whitespace+ value
  private static boolean break_statement_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "break_statement_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = break_statement_1_0_0(b, l + 1);
    r = r && value(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // whitespace+
  private static boolean break_statement_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "break_statement_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = whitespace(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!whitespace(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "break_statement_1_0_0", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // CELL_CONTENT | quoted_cell
  public static boolean cell(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "cell")) return false;
    if (!nextTokenIs(b, "<cell>", CELL_CONTENT, QUOTED_STRING)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CELL, "<cell>");
    r = consumeToken(b, CELL_CONTENT);
    if (!r) r = quoted_cell(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // space_or_tab* HASH NON_EOL* EOL
  public static boolean comment_line(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "comment_line")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, COMMENT_LINE, "<comment line>");
    r = comment_line_0(b, l + 1);
    r = r && consumeToken(b, HASH);
    r = r && comment_line_2(b, l + 1);
    r = r && consumeToken(b, EOL);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // space_or_tab*
  private static boolean comment_line_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "comment_line_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!space_or_tab(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "comment_line_0", c)) break;
    }
    return true;
  }

  // NON_EOL*
  private static boolean comment_line_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "comment_line_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!consumeToken(b, NON_EOL)) break;
      if (!empty_element_parsed_guard_(b, "comment_line_2", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // STAR+ space_or_tab* COMMENTS_WORDS space_or_tab* STAR*
  public static boolean comments_header(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "comments_header")) return false;
    if (!nextTokenIs(b, STAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = comments_header_0(b, l + 1);
    r = r && comments_header_1(b, l + 1);
    r = r && consumeToken(b, COMMENTS_WORDS);
    r = r && comments_header_3(b, l + 1);
    r = r && comments_header_4(b, l + 1);
    exit_section_(b, m, COMMENTS_HEADER, r);
    return r;
  }

  // STAR+
  private static boolean comments_header_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "comments_header_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, STAR);
    while (r) {
      int c = current_position_(b);
      if (!consumeToken(b, STAR)) break;
      if (!empty_element_parsed_guard_(b, "comments_header_0", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // space_or_tab*
  private static boolean comments_header_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "comments_header_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!space_or_tab(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "comments_header_1", c)) break;
    }
    return true;
  }

  // space_or_tab*
  private static boolean comments_header_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "comments_header_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!space_or_tab(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "comments_header_3", c)) break;
    }
    return true;
  }

  // STAR*
  private static boolean comments_header_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "comments_header_4")) return false;
    while (true) {
      int c = current_position_(b);
      if (!consumeToken(b, STAR)) break;
      if (!empty_element_parsed_guard_(b, "comments_header_4", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // comments_header
  //                     (comment_line | empty_line)*
  public static boolean comments_section(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "comments_section")) return false;
    if (!nextTokenIs(b, STAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = comments_header(b, l + 1);
    r = r && comments_section_1(b, l + 1);
    exit_section_(b, m, COMMENTS_SECTION, r);
    return r;
  }

  // (comment_line | empty_line)*
  private static boolean comments_section_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "comments_section_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!comments_section_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "comments_section_1", c)) break;
    }
    return true;
  }

  // comment_line | empty_line
  private static boolean comments_section_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "comments_section_1_0")) return false;
    boolean r;
    r = comment_line(b, l + 1);
    if (!r) r = empty_line(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // value (whitespace+ value)*
  public static boolean condition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "condition")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CONDITION, "<condition>");
    r = value(b, l + 1);
    r = r && condition_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (whitespace+ value)*
  private static boolean condition_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "condition_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!condition_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "condition_1", c)) break;
    }
    return true;
  }

  // whitespace+ value
  private static boolean condition_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "condition_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = condition_1_0_0(b, l + 1);
    r = r && value(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // whitespace+
  private static boolean condition_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "condition_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = whitespace(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!whitespace(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "condition_1_0_0", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // CONTINUE
  public static boolean continue_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "continue_statement")) return false;
    if (!nextTokenIs(b, CONTINUE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CONTINUE);
    exit_section_(b, m, CONTINUE_STATEMENT, r);
    return r;
  }

  /* ********************************************************** */
  // DICT_VARIABLE_START variable_name RBRACE
  public static boolean dict_variable(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dict_variable")) return false;
    if (!nextTokenIs(b, DICT_VARIABLE_START)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DICT_VARIABLE_START);
    r = r && variable_name(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, DICT_VARIABLE, r);
    return r;
  }

  /* ********************************************************** */
  // space_or_tab* EOL
  public static boolean empty_line(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "empty_line")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EMPTY_LINE, "<empty line>");
    r = empty_line_0(b, l + 1);
    r = r && consumeToken(b, EOL);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // space_or_tab*
  private static boolean empty_line_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "empty_line_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!space_or_tab(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "empty_line_0", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // ENV_VARIABLE_START variable_name RBRACE
  public static boolean environment_variable(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "environment_variable")) return false;
    if (!nextTokenIs(b, ENV_VARIABLE_START)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ENV_VARIABLE_START);
    r = r && variable_name(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, ENVIRONMENT_VARIABLE, r);
    return r;
  }

  /* ********************************************************** */
  // FOR whitespace+ variable_definition whitespace+ IN whitespace+ value (whitespace+ value)* EOL
  //                        (pipe_statement | empty_line | comment_line)*
  //                        END EOL
  public static boolean extended_for_syntax(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_for_syntax")) return false;
    if (!nextTokenIs(b, FOR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, FOR);
    r = r && extended_for_syntax_1(b, l + 1);
    r = r && variable_definition(b, l + 1);
    r = r && extended_for_syntax_3(b, l + 1);
    r = r && consumeToken(b, IN);
    r = r && extended_for_syntax_5(b, l + 1);
    r = r && value(b, l + 1);
    r = r && extended_for_syntax_7(b, l + 1);
    r = r && consumeToken(b, EOL);
    r = r && extended_for_syntax_9(b, l + 1);
    r = r && consumeTokens(b, 0, END, EOL);
    exit_section_(b, m, EXTENDED_FOR_SYNTAX, r);
    return r;
  }

  // whitespace+
  private static boolean extended_for_syntax_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_for_syntax_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = whitespace(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!whitespace(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "extended_for_syntax_1", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // whitespace+
  private static boolean extended_for_syntax_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_for_syntax_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = whitespace(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!whitespace(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "extended_for_syntax_3", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // whitespace+
  private static boolean extended_for_syntax_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_for_syntax_5")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = whitespace(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!whitespace(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "extended_for_syntax_5", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // (whitespace+ value)*
  private static boolean extended_for_syntax_7(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_for_syntax_7")) return false;
    while (true) {
      int c = current_position_(b);
      if (!extended_for_syntax_7_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "extended_for_syntax_7", c)) break;
    }
    return true;
  }

  // whitespace+ value
  private static boolean extended_for_syntax_7_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_for_syntax_7_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = extended_for_syntax_7_0_0(b, l + 1);
    r = r && value(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // whitespace+
  private static boolean extended_for_syntax_7_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_for_syntax_7_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = whitespace(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!whitespace(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "extended_for_syntax_7_0_0", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // (pipe_statement | empty_line | comment_line)*
  private static boolean extended_for_syntax_9(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_for_syntax_9")) return false;
    while (true) {
      int c = current_position_(b);
      if (!extended_for_syntax_9_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "extended_for_syntax_9", c)) break;
    }
    return true;
  }

  // pipe_statement | empty_line | comment_line
  private static boolean extended_for_syntax_9_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_for_syntax_9_0")) return false;
    boolean r;
    r = pipe_statement(b, l + 1);
    if (!r) r = empty_line(b, l + 1);
    if (!r) r = comment_line(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // IF whitespace+ condition EOL
  //                       (pipe_statement | empty_line | comment_line)*
  //                       (ELSE_IF whitespace+ condition EOL
  //                       (pipe_statement | empty_line | comment_line)*)*
  //                       (ELSE EOL
  //                       (pipe_statement | empty_line | comment_line)*)?
  //                       END EOL
  public static boolean extended_if_syntax(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_if_syntax")) return false;
    if (!nextTokenIs(b, IF)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IF);
    r = r && extended_if_syntax_1(b, l + 1);
    r = r && condition(b, l + 1);
    r = r && consumeToken(b, EOL);
    r = r && extended_if_syntax_4(b, l + 1);
    r = r && extended_if_syntax_5(b, l + 1);
    r = r && extended_if_syntax_6(b, l + 1);
    r = r && consumeTokens(b, 0, END, EOL);
    exit_section_(b, m, EXTENDED_IF_SYNTAX, r);
    return r;
  }

  // whitespace+
  private static boolean extended_if_syntax_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_if_syntax_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = whitespace(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!whitespace(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "extended_if_syntax_1", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // (pipe_statement | empty_line | comment_line)*
  private static boolean extended_if_syntax_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_if_syntax_4")) return false;
    while (true) {
      int c = current_position_(b);
      if (!extended_if_syntax_4_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "extended_if_syntax_4", c)) break;
    }
    return true;
  }

  // pipe_statement | empty_line | comment_line
  private static boolean extended_if_syntax_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_if_syntax_4_0")) return false;
    boolean r;
    r = pipe_statement(b, l + 1);
    if (!r) r = empty_line(b, l + 1);
    if (!r) r = comment_line(b, l + 1);
    return r;
  }

  // (ELSE_IF whitespace+ condition EOL
  //                       (pipe_statement | empty_line | comment_line)*)*
  private static boolean extended_if_syntax_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_if_syntax_5")) return false;
    while (true) {
      int c = current_position_(b);
      if (!extended_if_syntax_5_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "extended_if_syntax_5", c)) break;
    }
    return true;
  }

  // ELSE_IF whitespace+ condition EOL
  //                       (pipe_statement | empty_line | comment_line)*
  private static boolean extended_if_syntax_5_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_if_syntax_5_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ELSE_IF);
    r = r && extended_if_syntax_5_0_1(b, l + 1);
    r = r && condition(b, l + 1);
    r = r && consumeToken(b, EOL);
    r = r && extended_if_syntax_5_0_4(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // whitespace+
  private static boolean extended_if_syntax_5_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_if_syntax_5_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = whitespace(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!whitespace(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "extended_if_syntax_5_0_1", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // (pipe_statement | empty_line | comment_line)*
  private static boolean extended_if_syntax_5_0_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_if_syntax_5_0_4")) return false;
    while (true) {
      int c = current_position_(b);
      if (!extended_if_syntax_5_0_4_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "extended_if_syntax_5_0_4", c)) break;
    }
    return true;
  }

  // pipe_statement | empty_line | comment_line
  private static boolean extended_if_syntax_5_0_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_if_syntax_5_0_4_0")) return false;
    boolean r;
    r = pipe_statement(b, l + 1);
    if (!r) r = empty_line(b, l + 1);
    if (!r) r = comment_line(b, l + 1);
    return r;
  }

  // (ELSE EOL
  //                       (pipe_statement | empty_line | comment_line)*)?
  private static boolean extended_if_syntax_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_if_syntax_6")) return false;
    extended_if_syntax_6_0(b, l + 1);
    return true;
  }

  // ELSE EOL
  //                       (pipe_statement | empty_line | comment_line)*
  private static boolean extended_if_syntax_6_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_if_syntax_6_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, ELSE, EOL);
    r = r && extended_if_syntax_6_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (pipe_statement | empty_line | comment_line)*
  private static boolean extended_if_syntax_6_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_if_syntax_6_0_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!extended_if_syntax_6_0_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "extended_if_syntax_6_0_2", c)) break;
    }
    return true;
  }

  // pipe_statement | empty_line | comment_line
  private static boolean extended_if_syntax_6_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_if_syntax_6_0_2_0")) return false;
    boolean r;
    r = pipe_statement(b, l + 1);
    if (!r) r = empty_line(b, l + 1);
    if (!r) r = comment_line(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // TRY EOL
  //                        (pipe_statement | empty_line | comment_line)*
  //                        (EXCEPT (whitespace+ value (whitespace+ value)*)? EOL
  //                        (pipe_statement | empty_line | comment_line)*)*
  //                        (FINALLY EOL
  //                        (pipe_statement | empty_line | comment_line)*)?
  //                        END EOL
  public static boolean extended_try_syntax(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_try_syntax")) return false;
    if (!nextTokenIs(b, TRY)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, TRY, EOL);
    r = r && extended_try_syntax_2(b, l + 1);
    r = r && extended_try_syntax_3(b, l + 1);
    r = r && extended_try_syntax_4(b, l + 1);
    r = r && consumeTokens(b, 0, END, EOL);
    exit_section_(b, m, EXTENDED_TRY_SYNTAX, r);
    return r;
  }

  // (pipe_statement | empty_line | comment_line)*
  private static boolean extended_try_syntax_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_try_syntax_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!extended_try_syntax_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "extended_try_syntax_2", c)) break;
    }
    return true;
  }

  // pipe_statement | empty_line | comment_line
  private static boolean extended_try_syntax_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_try_syntax_2_0")) return false;
    boolean r;
    r = pipe_statement(b, l + 1);
    if (!r) r = empty_line(b, l + 1);
    if (!r) r = comment_line(b, l + 1);
    return r;
  }

  // (EXCEPT (whitespace+ value (whitespace+ value)*)? EOL
  //                        (pipe_statement | empty_line | comment_line)*)*
  private static boolean extended_try_syntax_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_try_syntax_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!extended_try_syntax_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "extended_try_syntax_3", c)) break;
    }
    return true;
  }

  // EXCEPT (whitespace+ value (whitespace+ value)*)? EOL
  //                        (pipe_statement | empty_line | comment_line)*
  private static boolean extended_try_syntax_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_try_syntax_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EXCEPT);
    r = r && extended_try_syntax_3_0_1(b, l + 1);
    r = r && consumeToken(b, EOL);
    r = r && extended_try_syntax_3_0_3(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (whitespace+ value (whitespace+ value)*)?
  private static boolean extended_try_syntax_3_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_try_syntax_3_0_1")) return false;
    extended_try_syntax_3_0_1_0(b, l + 1);
    return true;
  }

  // whitespace+ value (whitespace+ value)*
  private static boolean extended_try_syntax_3_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_try_syntax_3_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = extended_try_syntax_3_0_1_0_0(b, l + 1);
    r = r && value(b, l + 1);
    r = r && extended_try_syntax_3_0_1_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // whitespace+
  private static boolean extended_try_syntax_3_0_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_try_syntax_3_0_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = whitespace(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!whitespace(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "extended_try_syntax_3_0_1_0_0", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // (whitespace+ value)*
  private static boolean extended_try_syntax_3_0_1_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_try_syntax_3_0_1_0_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!extended_try_syntax_3_0_1_0_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "extended_try_syntax_3_0_1_0_2", c)) break;
    }
    return true;
  }

  // whitespace+ value
  private static boolean extended_try_syntax_3_0_1_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_try_syntax_3_0_1_0_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = extended_try_syntax_3_0_1_0_2_0_0(b, l + 1);
    r = r && value(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // whitespace+
  private static boolean extended_try_syntax_3_0_1_0_2_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_try_syntax_3_0_1_0_2_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = whitespace(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!whitespace(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "extended_try_syntax_3_0_1_0_2_0_0", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // (pipe_statement | empty_line | comment_line)*
  private static boolean extended_try_syntax_3_0_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_try_syntax_3_0_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!extended_try_syntax_3_0_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "extended_try_syntax_3_0_3", c)) break;
    }
    return true;
  }

  // pipe_statement | empty_line | comment_line
  private static boolean extended_try_syntax_3_0_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_try_syntax_3_0_3_0")) return false;
    boolean r;
    r = pipe_statement(b, l + 1);
    if (!r) r = empty_line(b, l + 1);
    if (!r) r = comment_line(b, l + 1);
    return r;
  }

  // (FINALLY EOL
  //                        (pipe_statement | empty_line | comment_line)*)?
  private static boolean extended_try_syntax_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_try_syntax_4")) return false;
    extended_try_syntax_4_0(b, l + 1);
    return true;
  }

  // FINALLY EOL
  //                        (pipe_statement | empty_line | comment_line)*
  private static boolean extended_try_syntax_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_try_syntax_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, FINALLY, EOL);
    r = r && extended_try_syntax_4_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (pipe_statement | empty_line | comment_line)*
  private static boolean extended_try_syntax_4_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_try_syntax_4_0_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!extended_try_syntax_4_0_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "extended_try_syntax_4_0_2", c)) break;
    }
    return true;
  }

  // pipe_statement | empty_line | comment_line
  private static boolean extended_try_syntax_4_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_try_syntax_4_0_2_0")) return false;
    boolean r;
    r = pipe_statement(b, l + 1);
    if (!r) r = empty_line(b, l + 1);
    if (!r) r = comment_line(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // WHILE whitespace+ condition EOL
  //                          (pipe_statement | empty_line | comment_line)*
  //                          END EOL
  public static boolean extended_while_syntax(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_while_syntax")) return false;
    if (!nextTokenIs(b, WHILE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, WHILE);
    r = r && extended_while_syntax_1(b, l + 1);
    r = r && condition(b, l + 1);
    r = r && consumeToken(b, EOL);
    r = r && extended_while_syntax_4(b, l + 1);
    r = r && consumeTokens(b, 0, END, EOL);
    exit_section_(b, m, EXTENDED_WHILE_SYNTAX, r);
    return r;
  }

  // whitespace+
  private static boolean extended_while_syntax_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_while_syntax_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = whitespace(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!whitespace(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "extended_while_syntax_1", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // (pipe_statement | empty_line | comment_line)*
  private static boolean extended_while_syntax_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_while_syntax_4")) return false;
    while (true) {
      int c = current_position_(b);
      if (!extended_while_syntax_4_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "extended_while_syntax_4", c)) break;
    }
    return true;
  }

  // pipe_statement | empty_line | comment_line
  private static boolean extended_while_syntax_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_while_syntax_4_0")) return false;
    boolean r;
    r = pipe_statement(b, l + 1);
    if (!r) r = empty_line(b, l + 1);
    if (!r) r = comment_line(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // (comment_line | section | empty_line | header_statement)*
  static boolean file(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "file")) return false;
    while (true) {
      int c = current_position_(b);
      if (!file_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "file", c)) break;
    }
    return true;
  }

  // comment_line | section | empty_line | header_statement
  private static boolean file_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "file_0")) return false;
    boolean r;
    r = comment_line(b, l + 1);
    if (!r) r = section(b, l + 1);
    if (!r) r = empty_line(b, l + 1);
    if (!r) r = header_statement(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // space_or_tab* line_comment
  public static boolean header_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "header_statement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, HEADER_STATEMENT, "<header statement>");
    r = header_statement_0(b, l + 1);
    r = r && line_comment(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // space_or_tab*
  private static boolean header_statement_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "header_statement_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!space_or_tab(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "header_statement_0", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // keyword_statement_name EOL
  //                       (keyword_step | empty_line | comment_line)*
  public static boolean keyword_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyword_statement")) return false;
    if (!nextTokenIs(b, NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = keyword_statement_name(b, l + 1);
    r = r && consumeToken(b, EOL);
    r = r && keyword_statement_2(b, l + 1);
    exit_section_(b, m, KEYWORD_STATEMENT, r);
    return r;
  }

  // (keyword_step | empty_line | comment_line)*
  private static boolean keyword_statement_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyword_statement_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!keyword_statement_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "keyword_statement_2", c)) break;
    }
    return true;
  }

  // keyword_step | empty_line | comment_line
  private static boolean keyword_statement_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyword_statement_2_0")) return false;
    boolean r;
    r = keyword_step(b, l + 1);
    if (!r) r = empty_line(b, l + 1);
    if (!r) r = comment_line(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // NAME
  public static boolean keyword_statement_name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyword_statement_name")) return false;
    if (!nextTokenIs(b, NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NAME);
    exit_section_(b, m, KEYWORD_STATEMENT_NAME, r);
    return r;
  }

  /* ********************************************************** */
  // whitespace+ step
  public static boolean keyword_step(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyword_step")) return false;
    if (!nextTokenIs(b, "<keyword step>", SPACE, TAB)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, KEYWORD_STEP, "<keyword step>");
    r = keyword_step_0(b, l + 1);
    r = r && step(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // whitespace+
  private static boolean keyword_step_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyword_step_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = whitespace(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!whitespace(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "keyword_step_0", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // STAR+ space_or_tab* KEYWORDS_WORDS space_or_tab* STAR*
  public static boolean keywords_header(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keywords_header")) return false;
    if (!nextTokenIs(b, STAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = keywords_header_0(b, l + 1);
    r = r && keywords_header_1(b, l + 1);
    r = r && consumeToken(b, KEYWORDS_WORDS);
    r = r && keywords_header_3(b, l + 1);
    r = r && keywords_header_4(b, l + 1);
    exit_section_(b, m, KEYWORDS_HEADER, r);
    return r;
  }

  // STAR+
  private static boolean keywords_header_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keywords_header_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, STAR);
    while (r) {
      int c = current_position_(b);
      if (!consumeToken(b, STAR)) break;
      if (!empty_element_parsed_guard_(b, "keywords_header_0", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // space_or_tab*
  private static boolean keywords_header_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keywords_header_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!space_or_tab(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "keywords_header_1", c)) break;
    }
    return true;
  }

  // space_or_tab*
  private static boolean keywords_header_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keywords_header_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!space_or_tab(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "keywords_header_3", c)) break;
    }
    return true;
  }

  // STAR*
  private static boolean keywords_header_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keywords_header_4")) return false;
    while (true) {
      int c = current_position_(b);
      if (!consumeToken(b, STAR)) break;
      if (!empty_element_parsed_guard_(b, "keywords_header_4", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // keywords_header
  //                     (keyword_statement | comment_line | empty_line)*
  public static boolean keywords_section(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keywords_section")) return false;
    if (!nextTokenIs(b, STAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = keywords_header(b, l + 1);
    r = r && keywords_section_1(b, l + 1);
    exit_section_(b, m, KEYWORDS_SECTION, r);
    return r;
  }

  // (keyword_statement | comment_line | empty_line)*
  private static boolean keywords_section_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keywords_section_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!keywords_section_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "keywords_section_1", c)) break;
    }
    return true;
  }

  // keyword_statement | comment_line | empty_line
  private static boolean keywords_section_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keywords_section_1_0")) return false;
    boolean r;
    r = keyword_statement(b, l + 1);
    if (!r) r = comment_line(b, l + 1);
    if (!r) r = empty_line(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // LIBRARY_WORDS whitespace+ value
  //                   (whitespace+ WITH_NAME_WORDS whitespace+ value)?
  //                   (whitespace+ ARG whitespace* EQUALS whitespace* value)* EOL
  public static boolean library_import(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "library_import")) return false;
    if (!nextTokenIs(b, LIBRARY_WORDS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LIBRARY_WORDS);
    r = r && library_import_1(b, l + 1);
    r = r && value(b, l + 1);
    r = r && library_import_3(b, l + 1);
    r = r && library_import_4(b, l + 1);
    r = r && consumeToken(b, EOL);
    exit_section_(b, m, LIBRARY_IMPORT, r);
    return r;
  }

  // whitespace+
  private static boolean library_import_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "library_import_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = whitespace(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!whitespace(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "library_import_1", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // (whitespace+ WITH_NAME_WORDS whitespace+ value)?
  private static boolean library_import_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "library_import_3")) return false;
    library_import_3_0(b, l + 1);
    return true;
  }

  // whitespace+ WITH_NAME_WORDS whitespace+ value
  private static boolean library_import_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "library_import_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = library_import_3_0_0(b, l + 1);
    r = r && consumeToken(b, WITH_NAME_WORDS);
    r = r && library_import_3_0_2(b, l + 1);
    r = r && value(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // whitespace+
  private static boolean library_import_3_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "library_import_3_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = whitespace(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!whitespace(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "library_import_3_0_0", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // whitespace+
  private static boolean library_import_3_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "library_import_3_0_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = whitespace(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!whitespace(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "library_import_3_0_2", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // (whitespace+ ARG whitespace* EQUALS whitespace* value)*
  private static boolean library_import_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "library_import_4")) return false;
    while (true) {
      int c = current_position_(b);
      if (!library_import_4_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "library_import_4", c)) break;
    }
    return true;
  }

  // whitespace+ ARG whitespace* EQUALS whitespace* value
  private static boolean library_import_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "library_import_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = library_import_4_0_0(b, l + 1);
    r = r && consumeToken(b, ARG);
    r = r && library_import_4_0_2(b, l + 1);
    r = r && consumeToken(b, EQUALS);
    r = r && library_import_4_0_4(b, l + 1);
    r = r && value(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // whitespace+
  private static boolean library_import_4_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "library_import_4_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = whitespace(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!whitespace(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "library_import_4_0_0", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // whitespace*
  private static boolean library_import_4_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "library_import_4_0_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!whitespace(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "library_import_4_0_2", c)) break;
    }
    return true;
  }

  // whitespace*
  private static boolean library_import_4_0_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "library_import_4_0_4")) return false;
    while (true) {
      int c = current_position_(b);
      if (!whitespace(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "library_import_4_0_4", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // HASH NON_EOL* EOL
  public static boolean line_comment(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "line_comment")) return false;
    if (!nextTokenIs(b, HASH)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, HASH);
    r = r && line_comment_1(b, l + 1);
    r = r && consumeToken(b, EOL);
    exit_section_(b, m, LINE_COMMENT, r);
    return r;
  }

  // NON_EOL*
  private static boolean line_comment_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "line_comment_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!consumeToken(b, NON_EOL)) break;
      if (!empty_element_parsed_guard_(b, "line_comment_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // LIST_VARIABLE_START variable_name RBRACE
  public static boolean list_variable(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "list_variable")) return false;
    if (!nextTokenIs(b, LIST_VARIABLE_START)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LIST_VARIABLE_START);
    r = r && variable_name(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, LIST_VARIABLE, r);
    return r;
  }

  /* ********************************************************** */
  // UNQUOTED_STRING | QUOTED_STRING
  public static boolean literal_value(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literal_value")) return false;
    if (!nextTokenIs(b, "<literal value>", QUOTED_STRING, UNQUOTED_STRING)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LITERAL_VALUE, "<literal value>");
    r = consumeToken(b, UNQUOTED_STRING);
    if (!r) r = consumeToken(b, QUOTED_STRING);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // PIPE cell (PIPE cell)* PIPE? EOL
  public static boolean pipe_separated_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pipe_separated_statement")) return false;
    if (!nextTokenIs(b, PIPE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, PIPE);
    r = r && cell(b, l + 1);
    r = r && pipe_separated_statement_2(b, l + 1);
    r = r && pipe_separated_statement_3(b, l + 1);
    r = r && consumeToken(b, EOL);
    exit_section_(b, m, PIPE_SEPARATED_STATEMENT, r);
    return r;
  }

  // (PIPE cell)*
  private static boolean pipe_separated_statement_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pipe_separated_statement_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!pipe_separated_statement_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "pipe_separated_statement_2", c)) break;
    }
    return true;
  }

  // PIPE cell
  private static boolean pipe_separated_statement_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pipe_separated_statement_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, PIPE);
    r = r && cell(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // PIPE?
  private static boolean pipe_separated_statement_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pipe_separated_statement_3")) return false;
    consumeToken(b, PIPE);
    return true;
  }

  /* ********************************************************** */
  // pipe_separated_statement | space_separated_statement
  public static boolean pipe_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pipe_statement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PIPE_STATEMENT, "<pipe statement>");
    r = pipe_separated_statement(b, l + 1);
    if (!r) r = space_separated_statement(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // QUOTED_STRING
  public static boolean quoted_cell(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "quoted_cell")) return false;
    if (!nextTokenIs(b, QUOTED_STRING)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, QUOTED_STRING);
    exit_section_(b, m, QUOTED_CELL, r);
    return r;
  }

  /* ********************************************************** */
  // RETURN (whitespace+ value (whitespace+ value)*)?
  public static boolean return_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "return_statement")) return false;
    if (!nextTokenIs(b, RETURN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, RETURN);
    r = r && return_statement_1(b, l + 1);
    exit_section_(b, m, RETURN_STATEMENT, r);
    return r;
  }

  // (whitespace+ value (whitespace+ value)*)?
  private static boolean return_statement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "return_statement_1")) return false;
    return_statement_1_0(b, l + 1);
    return true;
  }

  // whitespace+ value (whitespace+ value)*
  private static boolean return_statement_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "return_statement_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = return_statement_1_0_0(b, l + 1);
    r = r && value(b, l + 1);
    r = r && return_statement_1_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // whitespace+
  private static boolean return_statement_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "return_statement_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = whitespace(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!whitespace(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "return_statement_1_0_0", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // (whitespace+ value)*
  private static boolean return_statement_1_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "return_statement_1_0_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!return_statement_1_0_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "return_statement_1_0_2", c)) break;
    }
    return true;
  }

  // whitespace+ value
  private static boolean return_statement_1_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "return_statement_1_0_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = return_statement_1_0_2_0_0(b, l + 1);
    r = r && value(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // whitespace+
  private static boolean return_statement_1_0_2_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "return_statement_1_0_2_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = whitespace(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!whitespace(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "return_statement_1_0_2_0_0", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // SCALAR_VARIABLE_START variable_name RBRACE
  public static boolean scalar_variable(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "scalar_variable")) return false;
    if (!nextTokenIs(b, SCALAR_VARIABLE_START)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SCALAR_VARIABLE_START);
    r = r && variable_name(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, SCALAR_VARIABLE, r);
    return r;
  }

  /* ********************************************************** */
  // settings_section | variables_section | test_cases_section
  //           | tasks_section | keywords_section | comments_section
  public static boolean section(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "section")) return false;
    if (!nextTokenIs(b, STAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = settings_section(b, l + 1);
    if (!r) r = variables_section(b, l + 1);
    if (!r) r = test_cases_section(b, l + 1);
    if (!r) r = tasks_section(b, l + 1);
    if (!r) r = keywords_section(b, l + 1);
    if (!r) r = comments_section(b, l + 1);
    exit_section_(b, m, SECTION, r);
    return r;
  }

  /* ********************************************************** */
  // simple_setting_statement | bracket_setting_statement | library_import
  public static boolean setting_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "setting_statement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SETTING_STATEMENT, "<setting statement>");
    r = simple_setting_statement(b, l + 1);
    if (!r) r = bracket_setting_statement(b, l + 1);
    if (!r) r = library_import(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // value (whitespace+ value)*
  public static boolean setting_value(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "setting_value")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SETTING_VALUE, "<setting value>");
    r = value(b, l + 1);
    r = r && setting_value_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (whitespace+ value)*
  private static boolean setting_value_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "setting_value_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!setting_value_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "setting_value_1", c)) break;
    }
    return true;
  }

  // whitespace+ value
  private static boolean setting_value_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "setting_value_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = setting_value_1_0_0(b, l + 1);
    r = r && value(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // whitespace+
  private static boolean setting_value_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "setting_value_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = whitespace(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!whitespace(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "setting_value_1_0_0", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // STAR+ space_or_tab* SETTINGS_WORDS space_or_tab* STAR*
  public static boolean settings_header(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "settings_header")) return false;
    if (!nextTokenIs(b, STAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = settings_header_0(b, l + 1);
    r = r && settings_header_1(b, l + 1);
    r = r && consumeToken(b, SETTINGS_WORDS);
    r = r && settings_header_3(b, l + 1);
    r = r && settings_header_4(b, l + 1);
    exit_section_(b, m, SETTINGS_HEADER, r);
    return r;
  }

  // STAR+
  private static boolean settings_header_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "settings_header_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, STAR);
    while (r) {
      int c = current_position_(b);
      if (!consumeToken(b, STAR)) break;
      if (!empty_element_parsed_guard_(b, "settings_header_0", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // space_or_tab*
  private static boolean settings_header_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "settings_header_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!space_or_tab(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "settings_header_1", c)) break;
    }
    return true;
  }

  // space_or_tab*
  private static boolean settings_header_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "settings_header_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!space_or_tab(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "settings_header_3", c)) break;
    }
    return true;
  }

  // STAR*
  private static boolean settings_header_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "settings_header_4")) return false;
    while (true) {
      int c = current_position_(b);
      if (!consumeToken(b, STAR)) break;
      if (!empty_element_parsed_guard_(b, "settings_header_4", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // settings_header
  //                     (setting_statement | comment_line | empty_line)*
  public static boolean settings_section(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "settings_section")) return false;
    if (!nextTokenIs(b, STAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = settings_header(b, l + 1);
    r = r && settings_section_1(b, l + 1);
    exit_section_(b, m, SETTINGS_SECTION, r);
    return r;
  }

  // (setting_statement | comment_line | empty_line)*
  private static boolean settings_section_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "settings_section_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!settings_section_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "settings_section_1", c)) break;
    }
    return true;
  }

  // setting_statement | comment_line | empty_line
  private static boolean settings_section_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "settings_section_1_0")) return false;
    boolean r;
    r = setting_statement(b, l + 1);
    if (!r) r = comment_line(b, l + 1);
    if (!r) r = empty_line(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // SIMPLE_NAME
  public static boolean simple_setting_name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "simple_setting_name")) return false;
    if (!nextTokenIs(b, SIMPLE_NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SIMPLE_NAME);
    exit_section_(b, m, SIMPLE_SETTING_NAME, r);
    return r;
  }

  /* ********************************************************** */
  // simple_setting_name whitespace+ setting_value
  public static boolean simple_setting_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "simple_setting_statement")) return false;
    if (!nextTokenIs(b, SIMPLE_NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = simple_setting_name(b, l + 1);
    r = r && simple_setting_statement_1(b, l + 1);
    r = r && setting_value(b, l + 1);
    exit_section_(b, m, SIMPLE_SETTING_STATEMENT, r);
    return r;
  }

  // whitespace+
  private static boolean simple_setting_statement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "simple_setting_statement_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = whitespace(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!whitespace(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "simple_setting_statement_1", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // SPACE | TAB
  public static boolean space_or_tab(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "space_or_tab")) return false;
    if (!nextTokenIs(b, "<space or tab>", SPACE, TAB)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SPACE_OR_TAB, "<space or tab>");
    r = consumeToken(b, SPACE);
    if (!r) r = consumeToken(b, TAB);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // cell (whitespace+ cell)* EOL
  public static boolean space_separated_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "space_separated_statement")) return false;
    if (!nextTokenIs(b, "<space separated statement>", CELL_CONTENT, QUOTED_STRING)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SPACE_SEPARATED_STATEMENT, "<space separated statement>");
    r = cell(b, l + 1);
    r = r && space_separated_statement_1(b, l + 1);
    r = r && consumeToken(b, EOL);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (whitespace+ cell)*
  private static boolean space_separated_statement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "space_separated_statement_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!space_separated_statement_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "space_separated_statement_1", c)) break;
    }
    return true;
  }

  // whitespace+ cell
  private static boolean space_separated_statement_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "space_separated_statement_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = space_separated_statement_1_0_0(b, l + 1);
    r = r && cell(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // whitespace+
  private static boolean space_separated_statement_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "space_separated_statement_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = whitespace(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!whitespace(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "space_separated_statement_1_0_0", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // extended_if_syntax
  //        | extended_for_syntax
  //        | extended_try_syntax
  //        | extended_while_syntax
  //        | return_statement
  //        | break_statement
  //        | continue_statement
  //        | var_statement
  //        | value (whitespace+ value)*
  public static boolean step(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "step")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, STEP, "<step>");
    r = extended_if_syntax(b, l + 1);
    if (!r) r = extended_for_syntax(b, l + 1);
    if (!r) r = extended_try_syntax(b, l + 1);
    if (!r) r = extended_while_syntax(b, l + 1);
    if (!r) r = return_statement(b, l + 1);
    if (!r) r = break_statement(b, l + 1);
    if (!r) r = continue_statement(b, l + 1);
    if (!r) r = var_statement(b, l + 1);
    if (!r) r = step_8(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // value (whitespace+ value)*
  private static boolean step_8(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "step_8")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = value(b, l + 1);
    r = r && step_8_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (whitespace+ value)*
  private static boolean step_8_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "step_8_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!step_8_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "step_8_1", c)) break;
    }
    return true;
  }

  // whitespace+ value
  private static boolean step_8_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "step_8_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = step_8_1_0_0(b, l + 1);
    r = r && value(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // whitespace+
  private static boolean step_8_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "step_8_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = whitespace(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!whitespace(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "step_8_1_0_0", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // STAR+ space_or_tab* TASKS_WORDS space_or_tab* STAR*
  public static boolean tasks_header(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tasks_header")) return false;
    if (!nextTokenIs(b, STAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = tasks_header_0(b, l + 1);
    r = r && tasks_header_1(b, l + 1);
    r = r && consumeToken(b, TASKS_WORDS);
    r = r && tasks_header_3(b, l + 1);
    r = r && tasks_header_4(b, l + 1);
    exit_section_(b, m, TASKS_HEADER, r);
    return r;
  }

  // STAR+
  private static boolean tasks_header_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tasks_header_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, STAR);
    while (r) {
      int c = current_position_(b);
      if (!consumeToken(b, STAR)) break;
      if (!empty_element_parsed_guard_(b, "tasks_header_0", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // space_or_tab*
  private static boolean tasks_header_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tasks_header_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!space_or_tab(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "tasks_header_1", c)) break;
    }
    return true;
  }

  // space_or_tab*
  private static boolean tasks_header_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tasks_header_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!space_or_tab(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "tasks_header_3", c)) break;
    }
    return true;
  }

  // STAR*
  private static boolean tasks_header_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tasks_header_4")) return false;
    while (true) {
      int c = current_position_(b);
      if (!consumeToken(b, STAR)) break;
      if (!empty_element_parsed_guard_(b, "tasks_header_4", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // tasks_header
  //                 (test_case_statement | comment_line | empty_line)*
  public static boolean tasks_section(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tasks_section")) return false;
    if (!nextTokenIs(b, STAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = tasks_header(b, l + 1);
    r = r && tasks_section_1(b, l + 1);
    exit_section_(b, m, TASKS_SECTION, r);
    return r;
  }

  // (test_case_statement | comment_line | empty_line)*
  private static boolean tasks_section_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tasks_section_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!tasks_section_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "tasks_section_1", c)) break;
    }
    return true;
  }

  // test_case_statement | comment_line | empty_line
  private static boolean tasks_section_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tasks_section_1_0")) return false;
    boolean r;
    r = test_case_statement(b, l + 1);
    if (!r) r = comment_line(b, l + 1);
    if (!r) r = empty_line(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // NAME
  public static boolean test_case_name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "test_case_name")) return false;
    if (!nextTokenIs(b, NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NAME);
    exit_section_(b, m, TEST_CASE_NAME, r);
    return r;
  }

  /* ********************************************************** */
  // test_case_name EOL
  //                         (test_case_step | empty_line | comment_line)*
  public static boolean test_case_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "test_case_statement")) return false;
    if (!nextTokenIs(b, NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = test_case_name(b, l + 1);
    r = r && consumeToken(b, EOL);
    r = r && test_case_statement_2(b, l + 1);
    exit_section_(b, m, TEST_CASE_STATEMENT, r);
    return r;
  }

  // (test_case_step | empty_line | comment_line)*
  private static boolean test_case_statement_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "test_case_statement_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!test_case_statement_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "test_case_statement_2", c)) break;
    }
    return true;
  }

  // test_case_step | empty_line | comment_line
  private static boolean test_case_statement_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "test_case_statement_2_0")) return false;
    boolean r;
    r = test_case_step(b, l + 1);
    if (!r) r = empty_line(b, l + 1);
    if (!r) r = comment_line(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // whitespace+ step
  public static boolean test_case_step(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "test_case_step")) return false;
    if (!nextTokenIs(b, "<test case step>", SPACE, TAB)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, TEST_CASE_STEP, "<test case step>");
    r = test_case_step_0(b, l + 1);
    r = r && step(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // whitespace+
  private static boolean test_case_step_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "test_case_step_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = whitespace(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!whitespace(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "test_case_step_0", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // STAR+ space_or_tab* TESTCASES_WORDS space_or_tab* STAR*
  public static boolean test_cases_header(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "test_cases_header")) return false;
    if (!nextTokenIs(b, STAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = test_cases_header_0(b, l + 1);
    r = r && test_cases_header_1(b, l + 1);
    r = r && consumeToken(b, TESTCASES_WORDS);
    r = r && test_cases_header_3(b, l + 1);
    r = r && test_cases_header_4(b, l + 1);
    exit_section_(b, m, TEST_CASES_HEADER, r);
    return r;
  }

  // STAR+
  private static boolean test_cases_header_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "test_cases_header_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, STAR);
    while (r) {
      int c = current_position_(b);
      if (!consumeToken(b, STAR)) break;
      if (!empty_element_parsed_guard_(b, "test_cases_header_0", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // space_or_tab*
  private static boolean test_cases_header_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "test_cases_header_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!space_or_tab(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "test_cases_header_1", c)) break;
    }
    return true;
  }

  // space_or_tab*
  private static boolean test_cases_header_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "test_cases_header_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!space_or_tab(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "test_cases_header_3", c)) break;
    }
    return true;
  }

  // STAR*
  private static boolean test_cases_header_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "test_cases_header_4")) return false;
    while (true) {
      int c = current_position_(b);
      if (!consumeToken(b, STAR)) break;
      if (!empty_element_parsed_guard_(b, "test_cases_header_4", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // test_cases_header
  //                     (test_case_statement | comment_line | empty_line)*
  public static boolean test_cases_section(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "test_cases_section")) return false;
    if (!nextTokenIs(b, STAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = test_cases_header(b, l + 1);
    r = r && test_cases_section_1(b, l + 1);
    exit_section_(b, m, TEST_CASES_SECTION, r);
    return r;
  }

  // (test_case_statement | comment_line | empty_line)*
  private static boolean test_cases_section_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "test_cases_section_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!test_cases_section_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "test_cases_section_1", c)) break;
    }
    return true;
  }

  // test_case_statement | comment_line | empty_line
  private static boolean test_cases_section_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "test_cases_section_1_0")) return false;
    boolean r;
    r = test_case_statement(b, l + 1);
    if (!r) r = comment_line(b, l + 1);
    if (!r) r = empty_line(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // scalar_variable
  //         | list_variable
  //         | dict_variable
  //         | environment_variable
  //         | literal_value
  public static boolean value(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "value")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, VALUE, "<value>");
    r = scalar_variable(b, l + 1);
    if (!r) r = list_variable(b, l + 1);
    if (!r) r = dict_variable(b, l + 1);
    if (!r) r = environment_variable(b, l + 1);
    if (!r) r = literal_value(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // VAR whitespace+ variable_name whitespace+ value (whitespace+ value)*
  public static boolean var_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "var_statement")) return false;
    if (!nextTokenIs(b, VAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, VAR);
    r = r && var_statement_1(b, l + 1);
    r = r && variable_name(b, l + 1);
    r = r && var_statement_3(b, l + 1);
    r = r && value(b, l + 1);
    r = r && var_statement_5(b, l + 1);
    exit_section_(b, m, VAR_STATEMENT, r);
    return r;
  }

  // whitespace+
  private static boolean var_statement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "var_statement_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = whitespace(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!whitespace(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "var_statement_1", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // whitespace+
  private static boolean var_statement_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "var_statement_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = whitespace(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!whitespace(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "var_statement_3", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // (whitespace+ value)*
  private static boolean var_statement_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "var_statement_5")) return false;
    while (true) {
      int c = current_position_(b);
      if (!var_statement_5_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "var_statement_5", c)) break;
    }
    return true;
  }

  // whitespace+ value
  private static boolean var_statement_5_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "var_statement_5_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = var_statement_5_0_0(b, l + 1);
    r = r && value(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // whitespace+
  private static boolean var_statement_5_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "var_statement_5_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = whitespace(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!whitespace(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "var_statement_5_0_0", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // scalar_variable | list_variable | dict_variable
  public static boolean variable_definition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variable_definition")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, VARIABLE_DEFINITION, "<variable definition>");
    r = scalar_variable(b, l + 1);
    if (!r) r = list_variable(b, l + 1);
    if (!r) r = dict_variable(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // SIMPLE_NAME (DOT SIMPLE_NAME)*
  public static boolean variable_name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variable_name")) return false;
    if (!nextTokenIs(b, SIMPLE_NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SIMPLE_NAME);
    r = r && variable_name_1(b, l + 1);
    exit_section_(b, m, VARIABLE_NAME, r);
    return r;
  }

  // (DOT SIMPLE_NAME)*
  private static boolean variable_name_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variable_name_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!variable_name_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "variable_name_1", c)) break;
    }
    return true;
  }

  // DOT SIMPLE_NAME
  private static boolean variable_name_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variable_name_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, DOT, SIMPLE_NAME);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // variable_definition (whitespace+ variable_value)*
  public static boolean variable_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variable_statement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, VARIABLE_STATEMENT, "<variable statement>");
    r = variable_definition(b, l + 1);
    r = r && variable_statement_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (whitespace+ variable_value)*
  private static boolean variable_statement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variable_statement_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!variable_statement_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "variable_statement_1", c)) break;
    }
    return true;
  }

  // whitespace+ variable_value
  private static boolean variable_statement_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variable_statement_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = variable_statement_1_0_0(b, l + 1);
    r = r && variable_value(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // whitespace+
  private static boolean variable_statement_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variable_statement_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = whitespace(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!whitespace(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "variable_statement_1_0_0", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // value (whitespace+ value)*
  public static boolean variable_value(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variable_value")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, VARIABLE_VALUE, "<variable value>");
    r = value(b, l + 1);
    r = r && variable_value_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (whitespace+ value)*
  private static boolean variable_value_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variable_value_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!variable_value_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "variable_value_1", c)) break;
    }
    return true;
  }

  // whitespace+ value
  private static boolean variable_value_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variable_value_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = variable_value_1_0_0(b, l + 1);
    r = r && value(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // whitespace+
  private static boolean variable_value_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variable_value_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = whitespace(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!whitespace(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "variable_value_1_0_0", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // STAR+ space_or_tab* VARIABLES_WORDS space_or_tab* STAR*
  public static boolean variables_header(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variables_header")) return false;
    if (!nextTokenIs(b, STAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = variables_header_0(b, l + 1);
    r = r && variables_header_1(b, l + 1);
    r = r && consumeToken(b, VARIABLES_WORDS);
    r = r && variables_header_3(b, l + 1);
    r = r && variables_header_4(b, l + 1);
    exit_section_(b, m, VARIABLES_HEADER, r);
    return r;
  }

  // STAR+
  private static boolean variables_header_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variables_header_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, STAR);
    while (r) {
      int c = current_position_(b);
      if (!consumeToken(b, STAR)) break;
      if (!empty_element_parsed_guard_(b, "variables_header_0", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // space_or_tab*
  private static boolean variables_header_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variables_header_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!space_or_tab(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "variables_header_1", c)) break;
    }
    return true;
  }

  // space_or_tab*
  private static boolean variables_header_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variables_header_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!space_or_tab(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "variables_header_3", c)) break;
    }
    return true;
  }

  // STAR*
  private static boolean variables_header_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variables_header_4")) return false;
    while (true) {
      int c = current_position_(b);
      if (!consumeToken(b, STAR)) break;
      if (!empty_element_parsed_guard_(b, "variables_header_4", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // variables_header
  //                     (variable_statement | comment_line | empty_line)*
  public static boolean variables_section(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variables_section")) return false;
    if (!nextTokenIs(b, STAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = variables_header(b, l + 1);
    r = r && variables_section_1(b, l + 1);
    exit_section_(b, m, VARIABLES_SECTION, r);
    return r;
  }

  // (variable_statement | comment_line | empty_line)*
  private static boolean variables_section_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variables_section_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!variables_section_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "variables_section_1", c)) break;
    }
    return true;
  }

  // variable_statement | comment_line | empty_line
  private static boolean variables_section_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variables_section_1_0")) return false;
    boolean r;
    r = variable_statement(b, l + 1);
    if (!r) r = comment_line(b, l + 1);
    if (!r) r = empty_line(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // space_or_tab+
  public static boolean whitespace(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "whitespace")) return false;
    if (!nextTokenIs(b, "<whitespace>", SPACE, TAB)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, WHITESPACE, "<whitespace>");
    r = space_or_tab(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!space_or_tab(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "whitespace", c)) break;
    }
    exit_section_(b, l, m, r, false, null);
    return r;
  }

}
