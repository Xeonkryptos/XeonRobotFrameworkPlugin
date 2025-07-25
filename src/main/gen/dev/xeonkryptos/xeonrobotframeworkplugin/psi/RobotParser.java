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
    b = adapt_builder_(t, b, this, EXTENDS_SETS_);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    r = parse_root_(t, b);
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b) {
    return parse_root_(t, b, 0);
  }

  static boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return internal_root(b, l + 1);
  }

  public static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {
    create_token_set_(INLINE_VARIABLE_STATEMENT, KEYWORD_VARIABLE_STATEMENT, SINGLE_VARIABLE_STATEMENT),
    create_token_set_(DICT_VARIABLE, ENVIRONMENT_VARIABLE, LIST_VARIABLE, SCALAR_VARIABLE,
      VARIABLE),
    create_token_set_(EXECUTABLE_STATEMENT, FOR_LOOP_STRUCTURE, GROUP_STRUCTURE, IF_STRUCTURE,
      TRY_STRUCTURE, WHILE_LOOP_STRUCTURE),
    create_token_set_(COMMENTS_SECTION, KEYWORDS_SECTION, SECTION, SETTINGS_SECTION,
      TASKS_SECTION, TEST_CASES_SECTION, VARIABLES_SECTION),
    create_token_set_(DOCUMENTATION_STATEMENT_GLOBAL_SETTING, GLOBAL_SETTING_STATEMENT, LIBRARY_IMPORT_GLOBAL_SETTING, METADATA_STATEMENT_GLOBAL_SETTING,
      RESOURCE_IMPORT_GLOBAL_SETTING, SETUP_TEARDOWN_STATEMENTS_GLOBAL_SETTING, SUITE_NAME_STATEMENT_GLOBAL_SETTING, TAGS_STATEMENT_GLOBAL_SETTING,
      TEMPLATE_STATEMENTS_GLOBAL_SETTING, TIMEOUT_STATEMENTS_GLOBAL_SETTING, VARIABLES_IMPORT_GLOBAL_SETTING),
  };

  /* ********************************************************** */
  // keyword_call_id (parameter | positional_argument | ASSIGNMENT)*
  static boolean base_keyword_call(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "base_keyword_call")) return false;
    if (!nextTokenIs(b, KEYWORD_NAME)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = keyword_call_id(b, l + 1);
    p = r; // pin = 1
    r = r && base_keyword_call_1(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (parameter | positional_argument | ASSIGNMENT)*
  private static boolean base_keyword_call_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "base_keyword_call_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!base_keyword_call_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "base_keyword_call_1", c)) break;
    }
    return true;
  }

  // parameter | positional_argument | ASSIGNMENT
  private static boolean base_keyword_call_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "base_keyword_call_1_0")) return false;
    boolean r;
    r = parameter(b, l + 1);
    if (!r) r = positional_argument(b, l + 1);
    if (!r) r = consumeToken(b, ASSIGNMENT);
    return r;
  }

  /* ********************************************************** */
  // (
  //         GIVEN eol_based_keyword_call WHEN eol_based_keyword_call
  //         | GIVEN eol_based_keyword_call
  //         | WHEN eol_based_keyword_call
  //     )
  //     THEN eol_based_keyword_call (AND eol_based_keyword_call)* (BUT eol_based_keyword_call)*
  public static boolean bdd_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bdd_statement")) return false;
    if (!nextTokenIs(b, "<bdd statement>", GIVEN, WHEN)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BDD_STATEMENT, "<bdd statement>");
    r = bdd_statement_0(b, l + 1);
    r = r && consumeToken(b, THEN);
    r = r && eol_based_keyword_call(b, l + 1);
    r = r && bdd_statement_3(b, l + 1);
    r = r && bdd_statement_4(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // GIVEN eol_based_keyword_call WHEN eol_based_keyword_call
  //         | GIVEN eol_based_keyword_call
  //         | WHEN eol_based_keyword_call
  private static boolean bdd_statement_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bdd_statement_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = bdd_statement_0_0(b, l + 1);
    if (!r) r = bdd_statement_0_1(b, l + 1);
    if (!r) r = bdd_statement_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // GIVEN eol_based_keyword_call WHEN eol_based_keyword_call
  private static boolean bdd_statement_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bdd_statement_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, GIVEN);
    r = r && eol_based_keyword_call(b, l + 1);
    r = r && consumeToken(b, WHEN);
    r = r && eol_based_keyword_call(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // GIVEN eol_based_keyword_call
  private static boolean bdd_statement_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bdd_statement_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, GIVEN);
    r = r && eol_based_keyword_call(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // WHEN eol_based_keyword_call
  private static boolean bdd_statement_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bdd_statement_0_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, WHEN);
    r = r && eol_based_keyword_call(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (AND eol_based_keyword_call)*
  private static boolean bdd_statement_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bdd_statement_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!bdd_statement_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "bdd_statement_3", c)) break;
    }
    return true;
  }

  // AND eol_based_keyword_call
  private static boolean bdd_statement_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bdd_statement_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, AND);
    r = r && eol_based_keyword_call(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (BUT eol_based_keyword_call)*
  private static boolean bdd_statement_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bdd_statement_4")) return false;
    while (true) {
      int c = current_position_(b);
      if (!bdd_statement_4_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "bdd_statement_4", c)) break;
    }
    return true;
  }

  // BUT eol_based_keyword_call
  private static boolean bdd_statement_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bdd_statement_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, BUT);
    r = r && eol_based_keyword_call(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // eol_based_keyword_call | inline_variable_statement | keyword_variable_statement | single_variable_statement
  static boolean call_structure(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "call_structure")) return false;
    boolean r;
    r = eol_based_keyword_call(b, l + 1);
    if (!r) r = inline_variable_statement(b, l + 1);
    if (!r) r = keyword_variable_statement(b, l + 1);
    if (!r) r = single_variable_statement(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // COMMENTS_HEADER COMMENT*
  public static boolean comments_section(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "comments_section")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, COMMENTS_SECTION, "<Section>");
    r = consumeToken(b, COMMENTS_HEADER);
    p = r; // pin = 1
    r = r && comments_section_1(b, l + 1);
    exit_section_(b, l, m, r, p, RobotParser::section_recover);
    return r || p;
  }

  // COMMENT*
  private static boolean comments_section_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "comments_section_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!consumeToken(b, COMMENT)) break;
      if (!empty_element_parsed_guard_(b, "comments_section_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // if_structure | try_structure | group_structure
  static boolean control_structure(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "control_structure")) return false;
    boolean r;
    r = if_structure(b, l + 1);
    if (!r) r = try_structure(b, l + 1);
    if (!r) r = group_structure(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // DICT_VARIABLE_START (variable_content | python_expression) VARIABLE_END (extended_variable_key_access | extended_variable_nested_access)*
  public static boolean dict_variable(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dict_variable")) return false;
    if (!nextTokenIs(b, DICT_VARIABLE_START)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DICT_VARIABLE_START);
    r = r && dict_variable_1(b, l + 1);
    r = r && consumeToken(b, VARIABLE_END);
    r = r && dict_variable_3(b, l + 1);
    exit_section_(b, m, DICT_VARIABLE, r);
    return r;
  }

  // variable_content | python_expression
  private static boolean dict_variable_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dict_variable_1")) return false;
    boolean r;
    r = variable_content(b, l + 1);
    if (!r) r = python_expression(b, l + 1);
    return r;
  }

  // (extended_variable_key_access | extended_variable_nested_access)*
  private static boolean dict_variable_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dict_variable_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!dict_variable_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "dict_variable_3", c)) break;
    }
    return true;
  }

  // extended_variable_key_access | extended_variable_nested_access
  private static boolean dict_variable_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dict_variable_3_0")) return false;
    boolean r;
    r = extended_variable_key_access(b, l + 1);
    if (!r) r = extended_variable_nested_access(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // DOCUMENTATION_KEYWORD positional_argument* eol_marker
  public static boolean documentation_statement_global_setting(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "documentation_statement_global_setting")) return false;
    if (!nextTokenIs(b, DOCUMENTATION_KEYWORD)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DOCUMENTATION_KEYWORD);
    r = r && documentation_statement_global_setting_1(b, l + 1);
    r = r && eol_marker(b, l + 1);
    exit_section_(b, m, DOCUMENTATION_STATEMENT_GLOBAL_SETTING, r);
    return r;
  }

  // positional_argument*
  private static boolean documentation_statement_global_setting_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "documentation_statement_global_setting_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!positional_argument(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "documentation_statement_global_setting_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // ENV_VARIABLE_START variable_content VARIABLE_END
  public static boolean environment_variable(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "environment_variable")) return false;
    if (!nextTokenIs(b, ENV_VARIABLE_START)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ENV_VARIABLE_START);
    r = r && variable_content(b, l + 1);
    r = r && consumeToken(b, VARIABLE_END);
    exit_section_(b, m, ENVIRONMENT_VARIABLE, r);
    return r;
  }

  /* ********************************************************** */
  // base_keyword_call eol_marker?
  public static boolean eol_based_keyword_call(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "eol_based_keyword_call")) return false;
    if (!nextTokenIs(b, KEYWORD_NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = base_keyword_call(b, l + 1);
    r = r && eol_based_keyword_call_1(b, l + 1);
    exit_section_(b, m, KEYWORD_CALL, r);
    return r;
  }

  // eol_marker?
  private static boolean eol_based_keyword_call_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "eol_based_keyword_call_1")) return false;
    eol_marker(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // base_keyword_call
  public static boolean eol_free_keyword_call(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "eol_free_keyword_call")) return false;
    if (!nextTokenIs(b, KEYWORD_NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = base_keyword_call(b, l + 1);
    exit_section_(b, m, KEYWORD_CALL, r);
    return r;
  }

  /* ********************************************************** */
  // EOL+ | <<eof>>
  static boolean eol_marker(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "eol_marker")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = eol_marker_0(b, l + 1);
    if (!r) r = eof(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL+
  private static boolean eol_marker_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "eol_marker_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EOL);
    while (r) {
      int c = current_position_(b);
      if (!consumeToken(b, EOL)) break;
      if (!empty_element_parsed_guard_(b, "eol_marker_0", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // loop_structure | loop_control_structure | control_structure | call_structure | return_structure
  public static boolean executable_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "executable_statement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, EXECUTABLE_STATEMENT, "<executable statement>");
    r = loop_structure(b, l + 1);
    if (!r) r = loop_control_structure(b, l + 1);
    if (!r) r = control_structure(b, l + 1);
    if (!r) r = call_structure(b, l + 1);
    if (!r) r = return_structure(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // VARIABLE_INDEX_ACCESS
  public static boolean extended_variable_index_access(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_variable_index_access")) return false;
    if (!nextTokenIs(b, VARIABLE_INDEX_ACCESS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, VARIABLE_INDEX_ACCESS);
    exit_section_(b, m, EXTENDED_VARIABLE_INDEX_ACCESS, r);
    return r;
  }

  /* ********************************************************** */
  // VARIABLE_KEY_ACCESS
  public static boolean extended_variable_key_access(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_variable_key_access")) return false;
    if (!nextTokenIs(b, VARIABLE_KEY_ACCESS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, VARIABLE_KEY_ACCESS);
    exit_section_(b, m, EXTENDED_VARIABLE_KEY_ACCESS, r);
    return r;
  }

  /* ********************************************************** */
  // VARIABLE_ACCESS_START (variable | literal_constant_value | variable_body_id | VARIABLE_BODY_EXTENSION)+ VARIABLE_ACCESS_END
  public static boolean extended_variable_nested_access(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_variable_nested_access")) return false;
    if (!nextTokenIs(b, VARIABLE_ACCESS_START)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, VARIABLE_ACCESS_START);
    r = r && extended_variable_nested_access_1(b, l + 1);
    r = r && consumeToken(b, VARIABLE_ACCESS_END);
    exit_section_(b, m, EXTENDED_VARIABLE_NESTED_ACCESS, r);
    return r;
  }

  // (variable | literal_constant_value | variable_body_id | VARIABLE_BODY_EXTENSION)+
  private static boolean extended_variable_nested_access_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_variable_nested_access_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = extended_variable_nested_access_1_0(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!extended_variable_nested_access_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "extended_variable_nested_access_1", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // variable | literal_constant_value | variable_body_id | VARIABLE_BODY_EXTENSION
  private static boolean extended_variable_nested_access_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_variable_nested_access_1_0")) return false;
    boolean r;
    r = variable(b, l + 1);
    if (!r) r = literal_constant_value(b, l + 1);
    if (!r) r = variable_body_id(b, l + 1);
    if (!r) r = consumeToken(b, VARIABLE_BODY_EXTENSION);
    return r;
  }

  /* ********************************************************** */
  // VARIABLE_SLICE_ACCESS
  public static boolean extended_variable_slice_access(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_variable_slice_access")) return false;
    if (!nextTokenIs(b, VARIABLE_SLICE_ACCESS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, VARIABLE_SLICE_ACCESS);
    exit_section_(b, m, EXTENDED_VARIABLE_SLICE_ACCESS, r);
    return r;
  }

  /* ********************************************************** */
  // FOR variable+ FOR_IN positional_argument+ parameter* eol_marker executable_statement* END eol_marker
  public static boolean for_loop_structure(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "for_loop_structure")) return false;
    if (!nextTokenIs(b, FOR)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FOR_LOOP_STRUCTURE, null);
    r = consumeToken(b, FOR);
    p = r; // pin = 1
    r = r && report_error_(b, for_loop_structure_1(b, l + 1));
    r = p && report_error_(b, consumeToken(b, FOR_IN)) && r;
    r = p && report_error_(b, for_loop_structure_3(b, l + 1)) && r;
    r = p && report_error_(b, for_loop_structure_4(b, l + 1)) && r;
    r = p && report_error_(b, eol_marker(b, l + 1)) && r;
    r = p && report_error_(b, for_loop_structure_6(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, END)) && r;
    r = p && eol_marker(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // variable+
  private static boolean for_loop_structure_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "for_loop_structure_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = variable(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!variable(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "for_loop_structure_1", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // positional_argument+
  private static boolean for_loop_structure_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "for_loop_structure_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = positional_argument(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!positional_argument(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "for_loop_structure_3", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // parameter*
  private static boolean for_loop_structure_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "for_loop_structure_4")) return false;
    while (true) {
      int c = current_position_(b);
      if (!parameter(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "for_loop_structure_4", c)) break;
    }
    return true;
  }

  // executable_statement*
  private static boolean for_loop_structure_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "for_loop_structure_6")) return false;
    while (true) {
      int c = current_position_(b);
      if (!executable_statement(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "for_loop_structure_6", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // library_import_global_setting
  //     | resource_import_global_setting
  //     | variables_import_global_setting
  //     | metadata_statement_global_setting
  //     | documentation_statement_global_setting
  //     | suite_name_statement_global_setting
  //     | setup_teardown_statements_global_setting
  //     | tags_statement_global_setting
  //     | template_statements_global_setting
  //     | timeout_statements_global_setting
  //     | unknown_setting_statements_global_setting
  public static boolean global_setting_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "global_setting_statement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, GLOBAL_SETTING_STATEMENT, "<global setting statement>");
    r = library_import_global_setting(b, l + 1);
    if (!r) r = resource_import_global_setting(b, l + 1);
    if (!r) r = variables_import_global_setting(b, l + 1);
    if (!r) r = metadata_statement_global_setting(b, l + 1);
    if (!r) r = documentation_statement_global_setting(b, l + 1);
    if (!r) r = suite_name_statement_global_setting(b, l + 1);
    if (!r) r = setup_teardown_statements_global_setting(b, l + 1);
    if (!r) r = tags_statement_global_setting(b, l + 1);
    if (!r) r = template_statements_global_setting(b, l + 1);
    if (!r) r = timeout_statements_global_setting(b, l + 1);
    if (!r) r = unknown_setting_statements_global_setting(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // GROUP positional_argument? eol_marker executable_statement* END eol_marker
  public static boolean group_structure(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "group_structure")) return false;
    if (!nextTokenIs(b, GROUP)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, GROUP_STRUCTURE, null);
    r = consumeToken(b, GROUP);
    p = r; // pin = 1
    r = r && report_error_(b, group_structure_1(b, l + 1));
    r = p && report_error_(b, eol_marker(b, l + 1)) && r;
    r = p && report_error_(b, group_structure_3(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, END)) && r;
    r = p && eol_marker(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // positional_argument?
  private static boolean group_structure_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "group_structure_1")) return false;
    positional_argument(b, l + 1);
    return true;
  }

  // executable_statement*
  private static boolean group_structure_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "group_structure_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!executable_statement(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "group_structure_3", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // IF positional_argument+ eol_marker? executable_statement*
  //     (ELSE_IF positional_argument+ eol_marker? executable_statement*)*
  //     (ELSE eol_marker? executable_statement*)?
  //     (END eol_marker)?
  public static boolean if_structure(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "if_structure")) return false;
    if (!nextTokenIs(b, IF)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, IF_STRUCTURE, null);
    r = consumeToken(b, IF);
    p = r; // pin = 1
    r = r && report_error_(b, if_structure_1(b, l + 1));
    r = p && report_error_(b, if_structure_2(b, l + 1)) && r;
    r = p && report_error_(b, if_structure_3(b, l + 1)) && r;
    r = p && report_error_(b, if_structure_4(b, l + 1)) && r;
    r = p && report_error_(b, if_structure_5(b, l + 1)) && r;
    r = p && if_structure_6(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // positional_argument+
  private static boolean if_structure_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "if_structure_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = positional_argument(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!positional_argument(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "if_structure_1", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // eol_marker?
  private static boolean if_structure_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "if_structure_2")) return false;
    eol_marker(b, l + 1);
    return true;
  }

  // executable_statement*
  private static boolean if_structure_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "if_structure_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!executable_statement(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "if_structure_3", c)) break;
    }
    return true;
  }

  // (ELSE_IF positional_argument+ eol_marker? executable_statement*)*
  private static boolean if_structure_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "if_structure_4")) return false;
    while (true) {
      int c = current_position_(b);
      if (!if_structure_4_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "if_structure_4", c)) break;
    }
    return true;
  }

  // ELSE_IF positional_argument+ eol_marker? executable_statement*
  private static boolean if_structure_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "if_structure_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ELSE_IF);
    r = r && if_structure_4_0_1(b, l + 1);
    r = r && if_structure_4_0_2(b, l + 1);
    r = r && if_structure_4_0_3(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // positional_argument+
  private static boolean if_structure_4_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "if_structure_4_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = positional_argument(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!positional_argument(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "if_structure_4_0_1", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // eol_marker?
  private static boolean if_structure_4_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "if_structure_4_0_2")) return false;
    eol_marker(b, l + 1);
    return true;
  }

  // executable_statement*
  private static boolean if_structure_4_0_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "if_structure_4_0_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!executable_statement(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "if_structure_4_0_3", c)) break;
    }
    return true;
  }

  // (ELSE eol_marker? executable_statement*)?
  private static boolean if_structure_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "if_structure_5")) return false;
    if_structure_5_0(b, l + 1);
    return true;
  }

  // ELSE eol_marker? executable_statement*
  private static boolean if_structure_5_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "if_structure_5_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ELSE);
    r = r && if_structure_5_0_1(b, l + 1);
    r = r && if_structure_5_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // eol_marker?
  private static boolean if_structure_5_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "if_structure_5_0_1")) return false;
    eol_marker(b, l + 1);
    return true;
  }

  // executable_statement*
  private static boolean if_structure_5_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "if_structure_5_0_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!executable_statement(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "if_structure_5_0_2", c)) break;
    }
    return true;
  }

  // (END eol_marker)?
  private static boolean if_structure_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "if_structure_6")) return false;
    if_structure_6_0(b, l + 1);
    return true;
  }

  // END eol_marker
  private static boolean if_structure_6_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "if_structure_6_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, END);
    r = r && eol_marker(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // VAR variable_definition ASSIGNMENT? variable_value+ eol_marker?
  public static boolean inline_variable_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inline_variable_statement")) return false;
    if (!nextTokenIs(b, VAR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, VAR);
    r = r && variable_definition(b, l + 1);
    r = r && inline_variable_statement_2(b, l + 1);
    r = r && inline_variable_statement_3(b, l + 1);
    r = r && inline_variable_statement_4(b, l + 1);
    exit_section_(b, m, INLINE_VARIABLE_STATEMENT, r);
    return r;
  }

  // ASSIGNMENT?
  private static boolean inline_variable_statement_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inline_variable_statement_2")) return false;
    consumeToken(b, ASSIGNMENT);
    return true;
  }

  // variable_value+
  private static boolean inline_variable_statement_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inline_variable_statement_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = variable_value(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!variable_value(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "inline_variable_statement_3", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // eol_marker?
  private static boolean inline_variable_statement_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "inline_variable_statement_4")) return false;
    eol_marker(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // !<<eof>> root
  static boolean internal_root(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "internal_root")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = internal_root_0(b, l + 1);
    r = r && root(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // !<<eof>>
  private static boolean internal_root_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "internal_root_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !eof(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // KEYWORD_NAME
  public static boolean keyword_call_id(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyword_call_id")) return false;
    if (!nextTokenIs(b, KEYWORD_NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KEYWORD_NAME);
    exit_section_(b, m, KEYWORD_CALL_ID, r);
    return r;
  }

  /* ********************************************************** */
  // variable_definition+ ASSIGNMENT? eol_based_keyword_call
  public static boolean keyword_variable_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyword_variable_statement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, KEYWORD_VARIABLE_STATEMENT, "<keyword variable statement>");
    r = keyword_variable_statement_0(b, l + 1);
    r = r && keyword_variable_statement_1(b, l + 1);
    r = r && eol_based_keyword_call(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // variable_definition+
  private static boolean keyword_variable_statement_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyword_variable_statement_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = variable_definition(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!variable_definition(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "keyword_variable_statement_0", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // ASSIGNMENT?
  private static boolean keyword_variable_statement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyword_variable_statement_1")) return false;
    consumeToken(b, ASSIGNMENT);
    return true;
  }

  /* ********************************************************** */
  // USER_KEYWORDS_HEADER user_keyword_statement*
  public static boolean keywords_section(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keywords_section")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, KEYWORDS_SECTION, "<Section>");
    r = consumeToken(b, USER_KEYWORDS_HEADER);
    p = r; // pin = 1
    r = r && keywords_section_1(b, l + 1);
    exit_section_(b, l, m, r, p, RobotParser::section_recover);
    return r || p;
  }

  // user_keyword_statement*
  private static boolean keywords_section_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keywords_section_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!user_keyword_statement(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "keywords_section_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // LANGUAGE_KEYWORD language_id
  public static boolean language(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "language")) return false;
    if (!nextTokenIs(b, LANGUAGE_KEYWORD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, LANGUAGE, null);
    r = consumeToken(b, LANGUAGE_KEYWORD);
    p = r; // pin = 1
    r = r && language_id(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // LANGUAGE_NAME
  public static boolean language_id(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "language_id")) return false;
    if (!nextTokenIs(b, LANGUAGE_NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LANGUAGE_NAME);
    exit_section_(b, m, LANGUAGE_ID, r);
    return r;
  }

  /* ********************************************************** */
  // LIBRARY_IMPORT_KEYWORD positional_argument (parameter | positional_argument)* (WITH_NAME new_library_name)? eol_marker
  public static boolean library_import_global_setting(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "library_import_global_setting")) return false;
    if (!nextTokenIs(b, LIBRARY_IMPORT_KEYWORD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, LIBRARY_IMPORT_GLOBAL_SETTING, null);
    r = consumeToken(b, LIBRARY_IMPORT_KEYWORD);
    r = r && positional_argument(b, l + 1);
    r = r && library_import_global_setting_2(b, l + 1);
    p = r; // pin = 3
    r = r && report_error_(b, library_import_global_setting_3(b, l + 1));
    r = p && eol_marker(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (parameter | positional_argument)*
  private static boolean library_import_global_setting_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "library_import_global_setting_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!library_import_global_setting_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "library_import_global_setting_2", c)) break;
    }
    return true;
  }

  // parameter | positional_argument
  private static boolean library_import_global_setting_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "library_import_global_setting_2_0")) return false;
    boolean r;
    r = parameter(b, l + 1);
    if (!r) r = positional_argument(b, l + 1);
    return r;
  }

  // (WITH_NAME new_library_name)?
  private static boolean library_import_global_setting_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "library_import_global_setting_3")) return false;
    library_import_global_setting_3_0(b, l + 1);
    return true;
  }

  // WITH_NAME new_library_name
  private static boolean library_import_global_setting_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "library_import_global_setting_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, WITH_NAME);
    r = r && new_library_name(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // LIST_VARIABLE_START (variable_content | python_expression) VARIABLE_END (extended_variable_slice_access | extended_variable_index_access | extended_variable_nested_access)*
  public static boolean list_variable(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "list_variable")) return false;
    if (!nextTokenIs(b, LIST_VARIABLE_START)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LIST_VARIABLE_START);
    r = r && list_variable_1(b, l + 1);
    r = r && consumeToken(b, VARIABLE_END);
    r = r && list_variable_3(b, l + 1);
    exit_section_(b, m, LIST_VARIABLE, r);
    return r;
  }

  // variable_content | python_expression
  private static boolean list_variable_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "list_variable_1")) return false;
    boolean r;
    r = variable_content(b, l + 1);
    if (!r) r = python_expression(b, l + 1);
    return r;
  }

  // (extended_variable_slice_access | extended_variable_index_access | extended_variable_nested_access)*
  private static boolean list_variable_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "list_variable_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!list_variable_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "list_variable_3", c)) break;
    }
    return true;
  }

  // extended_variable_slice_access | extended_variable_index_access | extended_variable_nested_access
  private static boolean list_variable_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "list_variable_3_0")) return false;
    boolean r;
    r = extended_variable_slice_access(b, l + 1);
    if (!r) r = extended_variable_index_access(b, l + 1);
    if (!r) r = extended_variable_nested_access(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // LITERAL_CONSTANT
  public static boolean literal_constant_value(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literal_constant_value")) return false;
    if (!nextTokenIs(b, LITERAL_CONSTANT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LITERAL_CONSTANT);
    exit_section_(b, m, LITERAL_CONSTANT_VALUE, r);
    return r;
  }

  /* ********************************************************** */
  // local_setting_id (local_setting_argument | positional_argument | parameter | eol_free_keyword_call | ASSIGNMENT)* eol_marker
  public static boolean local_setting(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "local_setting")) return false;
    if (!nextTokenIs(b, LOCAL_SETTING_NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = local_setting_id(b, l + 1);
    r = r && local_setting_1(b, l + 1);
    r = r && eol_marker(b, l + 1);
    exit_section_(b, m, LOCAL_SETTING, r);
    return r;
  }

  // (local_setting_argument | positional_argument | parameter | eol_free_keyword_call | ASSIGNMENT)*
  private static boolean local_setting_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "local_setting_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!local_setting_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "local_setting_1", c)) break;
    }
    return true;
  }

  // local_setting_argument | positional_argument | parameter | eol_free_keyword_call | ASSIGNMENT
  private static boolean local_setting_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "local_setting_1_0")) return false;
    boolean r;
    r = local_setting_argument(b, l + 1);
    if (!r) r = positional_argument(b, l + 1);
    if (!r) r = parameter(b, l + 1);
    if (!r) r = eol_free_keyword_call(b, l + 1);
    if (!r) r = consumeToken(b, ASSIGNMENT);
    return r;
  }

  /* ********************************************************** */
  // variable ASSIGNMENT positional_argument
  public static boolean local_setting_argument(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "local_setting_argument")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LOCAL_SETTING_ARGUMENT, "<local setting argument>");
    r = variable(b, l + 1);
    r = r && consumeToken(b, ASSIGNMENT);
    r = r && positional_argument(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // LOCAL_SETTING_NAME
  public static boolean local_setting_id(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "local_setting_id")) return false;
    if (!nextTokenIs(b, LOCAL_SETTING_NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LOCAL_SETTING_NAME);
    exit_section_(b, m, LOCAL_SETTING_ID, r);
    return r;
  }

  /* ********************************************************** */
  // (BREAK | CONTINUE) eol_marker?
  static boolean loop_control_structure(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "loop_control_structure")) return false;
    if (!nextTokenIs(b, "", BREAK, CONTINUE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = loop_control_structure_0(b, l + 1);
    r = r && loop_control_structure_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // BREAK | CONTINUE
  private static boolean loop_control_structure_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "loop_control_structure_0")) return false;
    boolean r;
    r = consumeToken(b, BREAK);
    if (!r) r = consumeToken(b, CONTINUE);
    return r;
  }

  // eol_marker?
  private static boolean loop_control_structure_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "loop_control_structure_1")) return false;
    eol_marker(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // for_loop_structure | while_loop_structure
  static boolean loop_structure(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "loop_structure")) return false;
    if (!nextTokenIs(b, "", FOR, WHILE)) return false;
    boolean r;
    r = for_loop_structure(b, l + 1);
    if (!r) r = while_loop_structure(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // METADATA_KEYWORD positional_argument* eol_marker
  public static boolean metadata_statement_global_setting(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "metadata_statement_global_setting")) return false;
    if (!nextTokenIs(b, METADATA_KEYWORD)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, METADATA_KEYWORD);
    r = r && metadata_statement_global_setting_1(b, l + 1);
    r = r && eol_marker(b, l + 1);
    exit_section_(b, m, METADATA_STATEMENT_GLOBAL_SETTING, r);
    return r;
  }

  // positional_argument*
  private static boolean metadata_statement_global_setting_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "metadata_statement_global_setting_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!positional_argument(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "metadata_statement_global_setting_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // ARGUMENT_VALUE
  public static boolean new_library_name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "new_library_name")) return false;
    if (!nextTokenIs(b, ARGUMENT_VALUE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ARGUMENT_VALUE);
    exit_section_(b, m, NEW_LIBRARY_NAME, r);
    return r;
  }

  /* ********************************************************** */
  // parameter_id ASSIGNMENT positional_argument?
  public static boolean parameter(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter")) return false;
    if (!nextTokenIs(b, PARAMETER_NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = parameter_id(b, l + 1);
    r = r && consumeToken(b, ASSIGNMENT);
    r = r && parameter_2(b, l + 1);
    exit_section_(b, m, PARAMETER, r);
    return r;
  }

  // positional_argument?
  private static boolean parameter_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_2")) return false;
    positional_argument(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // PARAMETER_NAME
  public static boolean parameter_id(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_id")) return false;
    if (!nextTokenIs(b, PARAMETER_NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, PARAMETER_NAME);
    exit_section_(b, m, PARAMETER_ID, r);
    return r;
  }

  /* ********************************************************** */
  // external_positional_argument
  public static boolean positional_argument(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "positional_argument")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, POSITIONAL_ARGUMENT, "<positional argument>");
    r = parsePositionalArgument(b, l + 1, RobotParser::positional_argument_content);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // literal_constant_value | variable
  static boolean positional_argument_content(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "positional_argument_content")) return false;
    boolean r;
    r = literal_constant_value(b, l + 1);
    if (!r) r = variable(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // PYTHON_EXPRESSION_START PYTHON_EXPRESSION_CONTENT PYTHON_EXPRESSION_END
  public static boolean python_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "python_expression")) return false;
    if (!nextTokenIs(b, PYTHON_EXPRESSION_START)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, PYTHON_EXPRESSION_START, PYTHON_EXPRESSION_CONTENT, PYTHON_EXPRESSION_END);
    exit_section_(b, m, PYTHON_EXPRESSION, r);
    return r;
  }

  /* ********************************************************** */
  // RESOURCE_IMPORT_KEYWORD positional_argument eol_marker
  public static boolean resource_import_global_setting(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "resource_import_global_setting")) return false;
    if (!nextTokenIs(b, RESOURCE_IMPORT_KEYWORD)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, RESOURCE_IMPORT_KEYWORD);
    r = r && positional_argument(b, l + 1);
    r = r && eol_marker(b, l + 1);
    exit_section_(b, m, RESOURCE_IMPORT_GLOBAL_SETTING, r);
    return r;
  }

  /* ********************************************************** */
  // RETURN positional_argument* eol_marker?
  static boolean return_structure(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "return_structure")) return false;
    if (!nextTokenIs(b, RETURN)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = consumeToken(b, RETURN);
    p = r; // pin = 1
    r = r && report_error_(b, return_structure_1(b, l + 1));
    r = p && return_structure_2(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // positional_argument*
  private static boolean return_structure_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "return_structure_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!positional_argument(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "return_structure_1", c)) break;
    }
    return true;
  }

  // eol_marker?
  private static boolean return_structure_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "return_structure_2")) return false;
    eol_marker(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // language* section*
  public static boolean root(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "root")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ROOT, "<root>");
    r = root_0(b, l + 1);
    r = r && root_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // language*
  private static boolean root_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "root_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!language(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "root_0", c)) break;
    }
    return true;
  }

  // section*
  private static boolean root_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "root_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!section(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "root_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // SCALAR_VARIABLE_START (variable_content | python_expression) VARIABLE_END (extended_variable_key_access | extended_variable_slice_access | extended_variable_index_access | extended_variable_nested_access)*
  public static boolean scalar_variable(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "scalar_variable")) return false;
    if (!nextTokenIs(b, SCALAR_VARIABLE_START)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SCALAR_VARIABLE_START);
    r = r && scalar_variable_1(b, l + 1);
    r = r && consumeToken(b, VARIABLE_END);
    r = r && scalar_variable_3(b, l + 1);
    exit_section_(b, m, SCALAR_VARIABLE, r);
    return r;
  }

  // variable_content | python_expression
  private static boolean scalar_variable_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "scalar_variable_1")) return false;
    boolean r;
    r = variable_content(b, l + 1);
    if (!r) r = python_expression(b, l + 1);
    return r;
  }

  // (extended_variable_key_access | extended_variable_slice_access | extended_variable_index_access | extended_variable_nested_access)*
  private static boolean scalar_variable_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "scalar_variable_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!scalar_variable_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "scalar_variable_3", c)) break;
    }
    return true;
  }

  // extended_variable_key_access | extended_variable_slice_access | extended_variable_index_access | extended_variable_nested_access
  private static boolean scalar_variable_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "scalar_variable_3_0")) return false;
    boolean r;
    r = extended_variable_key_access(b, l + 1);
    if (!r) r = extended_variable_slice_access(b, l + 1);
    if (!r) r = extended_variable_index_access(b, l + 1);
    if (!r) r = extended_variable_nested_access(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // settings_section
  //     | variables_section
  //     | test_cases_section
  //     | tasks_section
  //     | keywords_section
  //     | comments_section
  public static boolean section(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "section")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, SECTION, "<Section>");
    r = settings_section(b, l + 1);
    if (!r) r = variables_section(b, l + 1);
    if (!r) r = test_cases_section(b, l + 1);
    if (!r) r = tasks_section(b, l + 1);
    if (!r) r = keywords_section(b, l + 1);
    if (!r) r = comments_section(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // !(SETTINGS_HEADER | VARIABLES_HEADER | TEST_CASES_HEADER | TASKS_HEADER | USER_KEYWORDS_HEADER | COMMENTS_HEADER)
  static boolean section_recover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "section_recover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !section_recover_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // SETTINGS_HEADER | VARIABLES_HEADER | TEST_CASES_HEADER | TASKS_HEADER | USER_KEYWORDS_HEADER | COMMENTS_HEADER
  private static boolean section_recover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "section_recover_0")) return false;
    boolean r;
    r = consumeToken(b, SETTINGS_HEADER);
    if (!r) r = consumeToken(b, VARIABLES_HEADER);
    if (!r) r = consumeToken(b, TEST_CASES_HEADER);
    if (!r) r = consumeToken(b, TASKS_HEADER);
    if (!r) r = consumeToken(b, USER_KEYWORDS_HEADER);
    if (!r) r = consumeToken(b, COMMENTS_HEADER);
    return r;
  }

  /* ********************************************************** */
  // SETTINGS_HEADER global_setting_statement*
  public static boolean settings_section(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "settings_section")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, SETTINGS_SECTION, "<Section>");
    r = consumeToken(b, SETTINGS_HEADER);
    p = r; // pin = 1
    r = r && settings_section_1(b, l + 1);
    exit_section_(b, l, m, r, p, RobotParser::section_recover);
    return r || p;
  }

  // global_setting_statement*
  private static boolean settings_section_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "settings_section_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!global_setting_statement(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "settings_section_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // SETUP_TEARDOWN_STATEMENT_KEYWORDS (eol_based_keyword_call | variable)
  public static boolean setup_teardown_statements_global_setting(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "setup_teardown_statements_global_setting")) return false;
    if (!nextTokenIs(b, SETUP_TEARDOWN_STATEMENT_KEYWORDS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SETUP_TEARDOWN_STATEMENT_KEYWORDS);
    r = r && setup_teardown_statements_global_setting_1(b, l + 1);
    exit_section_(b, m, SETUP_TEARDOWN_STATEMENTS_GLOBAL_SETTING, r);
    return r;
  }

  // eol_based_keyword_call | variable
  private static boolean setup_teardown_statements_global_setting_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "setup_teardown_statements_global_setting_1")) return false;
    boolean r;
    r = eol_based_keyword_call(b, l + 1);
    if (!r) r = variable(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // variable_definition ASSIGNMENT? variable_value* eol_marker?
  public static boolean single_variable_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "single_variable_statement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SINGLE_VARIABLE_STATEMENT, "<single variable statement>");
    r = variable_definition(b, l + 1);
    r = r && single_variable_statement_1(b, l + 1);
    r = r && single_variable_statement_2(b, l + 1);
    r = r && single_variable_statement_3(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ASSIGNMENT?
  private static boolean single_variable_statement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "single_variable_statement_1")) return false;
    consumeToken(b, ASSIGNMENT);
    return true;
  }

  // variable_value*
  private static boolean single_variable_statement_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "single_variable_statement_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!variable_value(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "single_variable_statement_2", c)) break;
    }
    return true;
  }

  // eol_marker?
  private static boolean single_variable_statement_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "single_variable_statement_3")) return false;
    eol_marker(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // SUITE_NAME_KEYWORD positional_argument eol_marker
  public static boolean suite_name_statement_global_setting(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "suite_name_statement_global_setting")) return false;
    if (!nextTokenIs(b, SUITE_NAME_KEYWORD)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SUITE_NAME_KEYWORD);
    r = r && positional_argument(b, l + 1);
    r = r && eol_marker(b, l + 1);
    exit_section_(b, m, SUITE_NAME_STATEMENT_GLOBAL_SETTING, r);
    return r;
  }

  /* ********************************************************** */
  // TAGS_KEYWORDS positional_argument* eol_marker
  public static boolean tags_statement_global_setting(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tags_statement_global_setting")) return false;
    if (!nextTokenIs(b, TAGS_KEYWORDS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, TAGS_KEYWORDS);
    r = r && tags_statement_global_setting_1(b, l + 1);
    r = r && eol_marker(b, l + 1);
    exit_section_(b, m, TAGS_STATEMENT_GLOBAL_SETTING, r);
    return r;
  }

  // positional_argument*
  private static boolean tags_statement_global_setting_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tags_statement_global_setting_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!positional_argument(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "tags_statement_global_setting_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // TASK_NAME
  public static boolean task_id(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "task_id")) return false;
    if (!nextTokenIs(b, TASK_NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, TASK_NAME);
    exit_section_(b, m, TASK_ID, r);
    return r;
  }

  /* ********************************************************** */
  // task_id eol_marker? testcase_task_statement*
  public static boolean task_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "task_statement")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, TASK_STATEMENT, "<task statement>");
    r = task_id(b, l + 1);
    r = r && task_statement_1(b, l + 1);
    p = r; // pin = 2
    r = r && task_statement_2(b, l + 1);
    exit_section_(b, l, m, r, p, RobotParser::task_statement_recover);
    return r || p;
  }

  // eol_marker?
  private static boolean task_statement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "task_statement_1")) return false;
    eol_marker(b, l + 1);
    return true;
  }

  // testcase_task_statement*
  private static boolean task_statement_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "task_statement_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!testcase_task_statement(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "task_statement_2", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // !task_id & section_recover
  static boolean task_statement_recover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "task_statement_recover")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = task_statement_recover_0(b, l + 1);
    r = r && task_statement_recover_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // !task_id
  private static boolean task_statement_recover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "task_statement_recover_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !task_id(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // & section_recover
  private static boolean task_statement_recover_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "task_statement_recover_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _AND_);
    r = section_recover(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // TASKS_HEADER task_statement*
  public static boolean tasks_section(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tasks_section")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, TASKS_SECTION, "<Section>");
    r = consumeToken(b, TASKS_HEADER);
    p = r; // pin = 1
    r = r && tasks_section_1(b, l + 1);
    exit_section_(b, l, m, r, p, RobotParser::section_recover);
    return r || p;
  }

  // task_statement*
  private static boolean tasks_section_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tasks_section_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!task_statement(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "tasks_section_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // TEMPLATE_ARGUMENT_VALUE
  public static boolean template_argument(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "template_argument")) return false;
    if (!nextTokenIs(b, TEMPLATE_ARGUMENT_VALUE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, TEMPLATE_ARGUMENT_VALUE);
    exit_section_(b, m, TEMPLATE_ARGUMENT, r);
    return r;
  }

  /* ********************************************************** */
  // (template_argument | template_parameter | variable)+ eol_marker
  public static boolean template_arguments(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "template_arguments")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, TEMPLATE_ARGUMENTS, "<template arguments>");
    r = template_arguments_0(b, l + 1);
    r = r && eol_marker(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (template_argument | template_parameter | variable)+
  private static boolean template_arguments_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "template_arguments_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = template_arguments_0_0(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!template_arguments_0_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "template_arguments_0", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // template_argument | template_parameter | variable
  private static boolean template_arguments_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "template_arguments_0_0")) return false;
    boolean r;
    r = template_argument(b, l + 1);
    if (!r) r = template_parameter(b, l + 1);
    if (!r) r = variable(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // template_parameter_id ASSIGNMENT (template_parameter_argument | variable)
  public static boolean template_parameter(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "template_parameter")) return false;
    if (!nextTokenIs(b, TEMPLATE_PARAMETER_NAME)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, TEMPLATE_PARAMETER, null);
    r = template_parameter_id(b, l + 1);
    r = r && consumeToken(b, ASSIGNMENT);
    p = r; // pin = 2
    r = r && template_parameter_2(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // template_parameter_argument | variable
  private static boolean template_parameter_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "template_parameter_2")) return false;
    boolean r;
    r = template_parameter_argument(b, l + 1);
    if (!r) r = variable(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // TEMPLATE_ARGUMENT_VALUE
  public static boolean template_parameter_argument(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "template_parameter_argument")) return false;
    if (!nextTokenIs(b, TEMPLATE_ARGUMENT_VALUE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, TEMPLATE_ARGUMENT_VALUE);
    exit_section_(b, m, TEMPLATE_PARAMETER_ARGUMENT, r);
    return r;
  }

  /* ********************************************************** */
  // TEMPLATE_PARAMETER_NAME
  public static boolean template_parameter_id(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "template_parameter_id")) return false;
    if (!nextTokenIs(b, TEMPLATE_PARAMETER_NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, TEMPLATE_PARAMETER_NAME);
    exit_section_(b, m, TEMPLATE_PARAMETER_ID, r);
    return r;
  }

  /* ********************************************************** */
  // TEMPLATE_KEYWORDS eol_based_keyword_call
  public static boolean template_statements_global_setting(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "template_statements_global_setting")) return false;
    if (!nextTokenIs(b, TEMPLATE_KEYWORDS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, TEMPLATE_KEYWORDS);
    r = r && eol_based_keyword_call(b, l + 1);
    exit_section_(b, m, TEMPLATE_STATEMENTS_GLOBAL_SETTING, r);
    return r;
  }

  /* ********************************************************** */
  // TEST_CASE_NAME
  public static boolean test_case_id(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "test_case_id")) return false;
    if (!nextTokenIs(b, TEST_CASE_NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, TEST_CASE_NAME);
    exit_section_(b, m, TEST_CASE_ID, r);
    return r;
  }

  /* ********************************************************** */
  // test_case_id eol_marker? testcase_task_statement*
  public static boolean test_case_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "test_case_statement")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, TEST_CASE_STATEMENT, "<test case statement>");
    r = test_case_id(b, l + 1);
    r = r && test_case_statement_1(b, l + 1);
    p = r; // pin = 2
    r = r && test_case_statement_2(b, l + 1);
    exit_section_(b, l, m, r, p, RobotParser::test_case_statement_recover);
    return r || p;
  }

  // eol_marker?
  private static boolean test_case_statement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "test_case_statement_1")) return false;
    eol_marker(b, l + 1);
    return true;
  }

  // testcase_task_statement*
  private static boolean test_case_statement_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "test_case_statement_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!testcase_task_statement(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "test_case_statement_2", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // !test_case_id & section_recover
  static boolean test_case_statement_recover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "test_case_statement_recover")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = test_case_statement_recover_0(b, l + 1);
    r = r && test_case_statement_recover_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // !test_case_id
  private static boolean test_case_statement_recover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "test_case_statement_recover_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !test_case_id(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // & section_recover
  private static boolean test_case_statement_recover_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "test_case_statement_recover_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _AND_);
    r = section_recover(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // TEST_CASES_HEADER test_case_statement*
  public static boolean test_cases_section(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "test_cases_section")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, TEST_CASES_SECTION, "<Section>");
    r = consumeToken(b, TEST_CASES_HEADER);
    p = r; // pin = 1
    r = r && test_cases_section_1(b, l + 1);
    exit_section_(b, l, m, r, p, RobotParser::section_recover);
    return r || p;
  }

  // test_case_statement*
  private static boolean test_cases_section_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "test_cases_section_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!test_case_statement(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "test_cases_section_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // local_setting
  //     | bdd_statement
  //     | executable_statement
  //     | template_arguments
  static boolean testcase_task_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "testcase_task_statement")) return false;
    boolean r;
    r = local_setting(b, l + 1);
    if (!r) r = bdd_statement(b, l + 1);
    if (!r) r = executable_statement(b, l + 1);
    if (!r) r = template_arguments(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // TIMEOUT_KEYWORDS positional_argument eol_marker
  public static boolean timeout_statements_global_setting(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "timeout_statements_global_setting")) return false;
    if (!nextTokenIs(b, TIMEOUT_KEYWORDS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, TIMEOUT_KEYWORDS);
    r = r && positional_argument(b, l + 1);
    r = r && eol_marker(b, l + 1);
    exit_section_(b, m, TIMEOUT_STATEMENTS_GLOBAL_SETTING, r);
    return r;
  }

  /* ********************************************************** */
  // TRY eol_marker executable_statement+
  //     (EXCEPT (positional_argument | parameter*)? eol_marker executable_statement*)*
  //     (ELSE eol_marker executable_statement*)?
  //     (FINALLY executable_statement*)?
  //     END eol_marker
  public static boolean try_structure(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "try_structure")) return false;
    if (!nextTokenIs(b, TRY)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, TRY_STRUCTURE, null);
    r = consumeToken(b, TRY);
    p = r; // pin = 1
    r = r && report_error_(b, eol_marker(b, l + 1));
    r = p && report_error_(b, try_structure_2(b, l + 1)) && r;
    r = p && report_error_(b, try_structure_3(b, l + 1)) && r;
    r = p && report_error_(b, try_structure_4(b, l + 1)) && r;
    r = p && report_error_(b, try_structure_5(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, END)) && r;
    r = p && eol_marker(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // executable_statement+
  private static boolean try_structure_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "try_structure_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = executable_statement(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!executable_statement(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "try_structure_2", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // (EXCEPT (positional_argument | parameter*)? eol_marker executable_statement*)*
  private static boolean try_structure_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "try_structure_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!try_structure_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "try_structure_3", c)) break;
    }
    return true;
  }

  // EXCEPT (positional_argument | parameter*)? eol_marker executable_statement*
  private static boolean try_structure_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "try_structure_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EXCEPT);
    r = r && try_structure_3_0_1(b, l + 1);
    r = r && eol_marker(b, l + 1);
    r = r && try_structure_3_0_3(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (positional_argument | parameter*)?
  private static boolean try_structure_3_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "try_structure_3_0_1")) return false;
    try_structure_3_0_1_0(b, l + 1);
    return true;
  }

  // positional_argument | parameter*
  private static boolean try_structure_3_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "try_structure_3_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = positional_argument(b, l + 1);
    if (!r) r = try_structure_3_0_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // parameter*
  private static boolean try_structure_3_0_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "try_structure_3_0_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!parameter(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "try_structure_3_0_1_0_1", c)) break;
    }
    return true;
  }

  // executable_statement*
  private static boolean try_structure_3_0_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "try_structure_3_0_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!executable_statement(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "try_structure_3_0_3", c)) break;
    }
    return true;
  }

  // (ELSE eol_marker executable_statement*)?
  private static boolean try_structure_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "try_structure_4")) return false;
    try_structure_4_0(b, l + 1);
    return true;
  }

  // ELSE eol_marker executable_statement*
  private static boolean try_structure_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "try_structure_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ELSE);
    r = r && eol_marker(b, l + 1);
    r = r && try_structure_4_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // executable_statement*
  private static boolean try_structure_4_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "try_structure_4_0_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!executable_statement(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "try_structure_4_0_2", c)) break;
    }
    return true;
  }

  // (FINALLY executable_statement*)?
  private static boolean try_structure_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "try_structure_5")) return false;
    try_structure_5_0(b, l + 1);
    return true;
  }

  // FINALLY executable_statement*
  private static boolean try_structure_5_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "try_structure_5_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, FINALLY);
    r = r && try_structure_5_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // executable_statement*
  private static boolean try_structure_5_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "try_structure_5_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!executable_statement(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "try_structure_5_0_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // UNKNOWN_SETTING_KEYWORD (parameter | positional_argument)* eol_marker
  public static boolean unknown_setting_statements_global_setting(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unknown_setting_statements_global_setting")) return false;
    if (!nextTokenIs(b, UNKNOWN_SETTING_KEYWORD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, UNKNOWN_SETTING_STATEMENTS_GLOBAL_SETTING, null);
    r = consumeToken(b, UNKNOWN_SETTING_KEYWORD);
    p = r; // pin = 1
    r = r && report_error_(b, unknown_setting_statements_global_setting_1(b, l + 1));
    r = p && eol_marker(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (parameter | positional_argument)*
  private static boolean unknown_setting_statements_global_setting_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unknown_setting_statements_global_setting_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!unknown_setting_statements_global_setting_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "unknown_setting_statements_global_setting_1", c)) break;
    }
    return true;
  }

  // parameter | positional_argument
  private static boolean unknown_setting_statements_global_setting_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unknown_setting_statements_global_setting_1_0")) return false;
    boolean r;
    r = parameter(b, l + 1);
    if (!r) r = positional_argument(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // user_keyword_statement_id eol_marker (local_setting | executable_statement)*
  public static boolean user_keyword_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "user_keyword_statement")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, USER_KEYWORD_STATEMENT, "<user keyword statement>");
    r = user_keyword_statement_id(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, eol_marker(b, l + 1));
    r = p && user_keyword_statement_2(b, l + 1) && r;
    exit_section_(b, l, m, r, p, RobotParser::user_keyword_statement_recover);
    return r || p;
  }

  // (local_setting | executable_statement)*
  private static boolean user_keyword_statement_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "user_keyword_statement_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!user_keyword_statement_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "user_keyword_statement_2", c)) break;
    }
    return true;
  }

  // local_setting | executable_statement
  private static boolean user_keyword_statement_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "user_keyword_statement_2_0")) return false;
    boolean r;
    r = local_setting(b, l + 1);
    if (!r) r = executable_statement(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // USER_KEYWORD_NAME
  public static boolean user_keyword_statement_id(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "user_keyword_statement_id")) return false;
    if (!nextTokenIs(b, USER_KEYWORD_NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, USER_KEYWORD_NAME);
    exit_section_(b, m, USER_KEYWORD_STATEMENT_ID, r);
    return r;
  }

  /* ********************************************************** */
  // !user_keyword_statement_id & section_recover
  static boolean user_keyword_statement_recover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "user_keyword_statement_recover")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = user_keyword_statement_recover_0(b, l + 1);
    r = r && user_keyword_statement_recover_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // !user_keyword_statement_id
  private static boolean user_keyword_statement_recover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "user_keyword_statement_recover_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !user_keyword_statement_id(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // & section_recover
  private static boolean user_keyword_statement_recover_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "user_keyword_statement_recover_1")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _AND_);
    r = section_recover(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // scalar_variable | list_variable | dict_variable | environment_variable
  public static boolean variable(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variable")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, VARIABLE, "<variable>");
    r = scalar_variable(b, l + 1);
    if (!r) r = list_variable(b, l + 1);
    if (!r) r = dict_variable(b, l + 1);
    if (!r) r = environment_variable(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // VARIABLE_BODY
  public static boolean variable_body_id(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variable_body_id")) return false;
    if (!nextTokenIs(b, VARIABLE_BODY)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, VARIABLE_BODY);
    exit_section_(b, m, VARIABLE_BODY_ID, r);
    return r;
  }

  /* ********************************************************** */
  // (variable | variable_body_id | VARIABLE_BODY_EXTENSION | extended_variable_nested_access)+
  public static boolean variable_content(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variable_content")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, VARIABLE_CONTENT, "<variable content>");
    r = variable_content_0(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!variable_content_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "variable_content", c)) break;
    }
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // variable | variable_body_id | VARIABLE_BODY_EXTENSION | extended_variable_nested_access
  private static boolean variable_content_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variable_content_0")) return false;
    boolean r;
    r = variable(b, l + 1);
    if (!r) r = variable_body_id(b, l + 1);
    if (!r) r = consumeToken(b, VARIABLE_BODY_EXTENSION);
    if (!r) r = extended_variable_nested_access(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // variable
  public static boolean variable_definition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variable_definition")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, VARIABLE_DEFINITION, "<variable definition>");
    r = variable(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // variable | positional_argument | parameter
  public static boolean variable_value(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variable_value")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, VARIABLE_VALUE, "<variable value>");
    r = variable(b, l + 1);
    if (!r) r = positional_argument(b, l + 1);
    if (!r) r = parameter(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // VARIABLES_IMPORT_KEYWORD positional_argument (parameter | positional_argument)* eol_marker
  public static boolean variables_import_global_setting(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variables_import_global_setting")) return false;
    if (!nextTokenIs(b, VARIABLES_IMPORT_KEYWORD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, VARIABLES_IMPORT_GLOBAL_SETTING, null);
    r = consumeToken(b, VARIABLES_IMPORT_KEYWORD);
    r = r && positional_argument(b, l + 1);
    r = r && variables_import_global_setting_2(b, l + 1);
    p = r; // pin = 3
    r = r && eol_marker(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (parameter | positional_argument)*
  private static boolean variables_import_global_setting_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variables_import_global_setting_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!variables_import_global_setting_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "variables_import_global_setting_2", c)) break;
    }
    return true;
  }

  // parameter | positional_argument
  private static boolean variables_import_global_setting_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variables_import_global_setting_2_0")) return false;
    boolean r;
    r = parameter(b, l + 1);
    if (!r) r = positional_argument(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // VARIABLES_HEADER single_variable_statement*
  public static boolean variables_section(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variables_section")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, VARIABLES_SECTION, "<Section>");
    r = consumeToken(b, VARIABLES_HEADER);
    p = r; // pin = 1
    r = r && variables_section_1(b, l + 1);
    exit_section_(b, l, m, r, p, RobotParser::section_recover);
    return r || p;
  }

  // single_variable_statement*
  private static boolean variables_section_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variables_section_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!single_variable_statement(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "variables_section_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // WHILE positional_argument+ eol_marker (parameter+ eol_marker)? executable_statement* END eol_marker
  public static boolean while_loop_structure(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "while_loop_structure")) return false;
    if (!nextTokenIs(b, WHILE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, WHILE_LOOP_STRUCTURE, null);
    r = consumeToken(b, WHILE);
    p = r; // pin = 1
    r = r && report_error_(b, while_loop_structure_1(b, l + 1));
    r = p && report_error_(b, eol_marker(b, l + 1)) && r;
    r = p && report_error_(b, while_loop_structure_3(b, l + 1)) && r;
    r = p && report_error_(b, while_loop_structure_4(b, l + 1)) && r;
    r = p && report_error_(b, consumeToken(b, END)) && r;
    r = p && eol_marker(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // positional_argument+
  private static boolean while_loop_structure_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "while_loop_structure_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = positional_argument(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!positional_argument(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "while_loop_structure_1", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // (parameter+ eol_marker)?
  private static boolean while_loop_structure_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "while_loop_structure_3")) return false;
    while_loop_structure_3_0(b, l + 1);
    return true;
  }

  // parameter+ eol_marker
  private static boolean while_loop_structure_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "while_loop_structure_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = while_loop_structure_3_0_0(b, l + 1);
    r = r && eol_marker(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // parameter+
  private static boolean while_loop_structure_3_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "while_loop_structure_3_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = parameter(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!parameter(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "while_loop_structure_3_0_0", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // executable_statement*
  private static boolean while_loop_structure_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "while_loop_structure_4")) return false;
    while (true) {
      int c = current_position_(b);
      if (!executable_statement(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "while_loop_structure_4", c)) break;
    }
    return true;
  }

}
