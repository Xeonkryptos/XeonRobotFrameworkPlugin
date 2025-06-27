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
    return root(b, l + 1);
  }

  public static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {
    create_token_set_(KEYWORD_VARIABLE_STATEMENT, SINGLE_VARIABLE_STATEMENT),
    create_token_set_(DICT_VARIABLE, ENVIRONMENT_VARIABLE, LIST_VARIABLE, SCALAR_VARIABLE,
      VARIABLE),
    create_token_set_(COMMENTS_SECTION, KEYWORDS_SECTION, SECTION, SETTINGS_SECTION,
      TASKS_SECTION, TEST_CASES_SECTION, VARIABLES_SECTION),
  };

  /* ********************************************************** */
  // value
  public static boolean argument(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "argument")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ARGUMENT, "<argument>");
    r = value(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // bracket_setting_id line_comment* (parameter | argument | keyword_call_id)* line_comment* EOL
  public static boolean bracket_setting(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bracket_setting")) return false;
    if (!nextTokenIs(b, BRACKET_SETTING_NAME)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, BRACKET_SETTING, null);
    r = bracket_setting_id(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, bracket_setting_1(b, l + 1));
    r = p && report_error_(b, bracket_setting_2(b, l + 1)) && r;
    r = p && report_error_(b, bracket_setting_3(b, l + 1)) && r;
    r = p && consumeToken(b, EOL) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // line_comment*
  private static boolean bracket_setting_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bracket_setting_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!line_comment(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "bracket_setting_1", c)) break;
    }
    return true;
  }

  // (parameter | argument | keyword_call_id)*
  private static boolean bracket_setting_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bracket_setting_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!bracket_setting_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "bracket_setting_2", c)) break;
    }
    return true;
  }

  // parameter | argument | keyword_call_id
  private static boolean bracket_setting_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bracket_setting_2_0")) return false;
    boolean r;
    r = parameter(b, l + 1);
    if (!r) r = argument(b, l + 1);
    if (!r) r = keyword_call_id(b, l + 1);
    return r;
  }

  // line_comment*
  private static boolean bracket_setting_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bracket_setting_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!line_comment(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "bracket_setting_3", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // BRACKET_SETTING_NAME
  public static boolean bracket_setting_id(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bracket_setting_id")) return false;
    if (!nextTokenIs(b, BRACKET_SETTING_NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, BRACKET_SETTING_NAME);
    exit_section_(b, m, BRACKET_SETTING_ID, r);
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
  // ARGUMENT_VALUE
  public static boolean constant_value(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "constant_value")) return false;
    if (!nextTokenIs(b, ARGUMENT_VALUE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ARGUMENT_VALUE);
    exit_section_(b, m, CONSTANT_VALUE, r);
    return r;
  }

  /* ********************************************************** */
  // DICT_VARIABLE_START variable_id VARIABLE_END (extended_variable_key_access | extended_variable_nested_access)*
  public static boolean dict_variable(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dict_variable")) return false;
    if (!nextTokenIs(b, DICT_VARIABLE_START)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DICT_VARIABLE_START);
    r = r && variable_id(b, l + 1);
    r = r && consumeToken(b, VARIABLE_END);
    r = r && dict_variable_3(b, l + 1);
    exit_section_(b, m, DICT_VARIABLE, r);
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
  // DOCUMENTATION_KEYWORD line_comment* argument+ line_comment* EOL
  public static boolean documentation_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "documentation_statement")) return false;
    if (!nextTokenIs(b, DOCUMENTATION_KEYWORD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, DOCUMENTATION_STATEMENT, null);
    r = consumeToken(b, DOCUMENTATION_KEYWORD);
    p = r; // pin = 1
    r = r && report_error_(b, documentation_statement_1(b, l + 1));
    r = p && report_error_(b, documentation_statement_2(b, l + 1)) && r;
    r = p && report_error_(b, documentation_statement_3(b, l + 1)) && r;
    r = p && consumeToken(b, EOL) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // line_comment*
  private static boolean documentation_statement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "documentation_statement_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!line_comment(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "documentation_statement_1", c)) break;
    }
    return true;
  }

  // argument+
  private static boolean documentation_statement_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "documentation_statement_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = argument(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!argument(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "documentation_statement_2", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // line_comment*
  private static boolean documentation_statement_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "documentation_statement_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!line_comment(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "documentation_statement_3", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // ENV_VARIABLE_START variable_id VARIABLE_END
  public static boolean environment_variable(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "environment_variable")) return false;
    if (!nextTokenIs(b, ENV_VARIABLE_START)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ENV_VARIABLE_START);
    r = r && variable_id(b, l + 1);
    r = r && consumeToken(b, VARIABLE_END);
    exit_section_(b, m, ENVIRONMENT_VARIABLE, r);
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
  // VARIABLE_ACCESS_START value VARIABLE_ACCESS_END
  public static boolean extended_variable_nested_access(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "extended_variable_nested_access")) return false;
    if (!nextTokenIs(b, VARIABLE_ACCESS_START)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, VARIABLE_ACCESS_START);
    r = r && value(b, l + 1);
    r = r && consumeToken(b, VARIABLE_ACCESS_END);
    exit_section_(b, m, EXTENDED_VARIABLE_NESTED_ACCESS, r);
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
  // language* section*
  public static boolean file2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "file2")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FILE_2, "<file 2>");
    r = file2_0(b, l + 1);
    r = r && file2_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // language*
  private static boolean file2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "file2_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!language(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "file2_0", c)) break;
    }
    return true;
  }

  // section*
  private static boolean file2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "file2_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!section(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "file2_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // keyword_call_id (parameter | argument | line_comment)* EOL
  public static boolean keyword_call(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyword_call")) return false;
    if (!nextTokenIs(b, KEYWORD_NAME)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, KEYWORD_CALL, null);
    r = keyword_call_id(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, keyword_call_1(b, l + 1));
    r = p && consumeToken(b, EOL) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (parameter | argument | line_comment)*
  private static boolean keyword_call_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyword_call_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!keyword_call_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "keyword_call_1", c)) break;
    }
    return true;
  }

  // parameter | argument | line_comment
  private static boolean keyword_call_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyword_call_1_0")) return false;
    boolean r;
    r = parameter(b, l + 1);
    if (!r) r = argument(b, l + 1);
    if (!r) r = line_comment(b, l + 1);
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
  // keyword_statement_id
  public static boolean keyword_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyword_statement")) return false;
    if (!nextTokenIs(b, KEYWORD_STATEMENT_NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = keyword_statement_id(b, l + 1);
    exit_section_(b, m, KEYWORD_STATEMENT, r);
    return r;
  }

  /* ********************************************************** */
  // KEYWORD_STATEMENT_NAME
  public static boolean keyword_statement_id(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyword_statement_id")) return false;
    if (!nextTokenIs(b, KEYWORD_STATEMENT_NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KEYWORD_STATEMENT_NAME);
    exit_section_(b, m, KEYWORD_STATEMENT_ID, r);
    return r;
  }

  /* ********************************************************** */
  // variable+ ASSIGNMENT? keyword_call
  public static boolean keyword_variable_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyword_variable_statement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, KEYWORD_VARIABLE_STATEMENT, "<keyword variable statement>");
    r = keyword_variable_statement_0(b, l + 1);
    r = r && keyword_variable_statement_1(b, l + 1);
    r = r && keyword_call(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // variable+
  private static boolean keyword_variable_statement_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keyword_variable_statement_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = variable(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!variable(b, l + 1)) break;
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
  // KEYWORDS_HEADER keyword_statement*
  public static boolean keywords_section(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keywords_section")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, KEYWORDS_SECTION, "<Section>");
    r = consumeToken(b, KEYWORDS_HEADER);
    p = r; // pin = 1
    r = r && keywords_section_1(b, l + 1);
    exit_section_(b, l, m, r, p, RobotParser::section_recover);
    return r || p;
  }

  // keyword_statement*
  private static boolean keywords_section_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keywords_section_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!keyword_statement(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "keywords_section_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // LANGUAGE_KEYWORD line_comment* language_id line_comment*
  public static boolean language(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "language")) return false;
    if (!nextTokenIs(b, LANGUAGE_KEYWORD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, LANGUAGE, null);
    r = consumeToken(b, LANGUAGE_KEYWORD);
    p = r; // pin = 1
    r = r && report_error_(b, language_1(b, l + 1));
    r = p && report_error_(b, language_id(b, l + 1)) && r;
    r = p && language_3(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // line_comment*
  private static boolean language_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "language_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!line_comment(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "language_1", c)) break;
    }
    return true;
  }

  // line_comment*
  private static boolean language_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "language_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!line_comment(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "language_3", c)) break;
    }
    return true;
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
  // LIBRARY_IMPORT_KEYWORD line_comment* argument (parameter | argument)* (WITH_NAME_KEYWORD new_library_name)? line_comment* EOL
  public static boolean library_import(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "library_import")) return false;
    if (!nextTokenIs(b, LIBRARY_IMPORT_KEYWORD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, LIBRARY_IMPORT, null);
    r = consumeToken(b, LIBRARY_IMPORT_KEYWORD);
    p = r; // pin = 1
    r = r && report_error_(b, library_import_1(b, l + 1));
    r = p && report_error_(b, argument(b, l + 1)) && r;
    r = p && report_error_(b, library_import_3(b, l + 1)) && r;
    r = p && report_error_(b, library_import_4(b, l + 1)) && r;
    r = p && report_error_(b, library_import_5(b, l + 1)) && r;
    r = p && consumeToken(b, EOL) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // line_comment*
  private static boolean library_import_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "library_import_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!line_comment(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "library_import_1", c)) break;
    }
    return true;
  }

  // (parameter | argument)*
  private static boolean library_import_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "library_import_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!library_import_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "library_import_3", c)) break;
    }
    return true;
  }

  // parameter | argument
  private static boolean library_import_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "library_import_3_0")) return false;
    boolean r;
    r = parameter(b, l + 1);
    if (!r) r = argument(b, l + 1);
    return r;
  }

  // (WITH_NAME_KEYWORD new_library_name)?
  private static boolean library_import_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "library_import_4")) return false;
    library_import_4_0(b, l + 1);
    return true;
  }

  // WITH_NAME_KEYWORD new_library_name
  private static boolean library_import_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "library_import_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, WITH_NAME_KEYWORD);
    r = r && new_library_name(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // line_comment*
  private static boolean library_import_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "library_import_5")) return false;
    while (true) {
      int c = current_position_(b);
      if (!line_comment(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "library_import_5", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // COMMENT
  static boolean line_comment(PsiBuilder b, int l) {
    return consumeToken(b, COMMENT);
  }

  /* ********************************************************** */
  // LIST_VARIABLE_START variable_id VARIABLE_END (extended_variable_slice_access | extended_variable_index_access | extended_variable_nested_access)*
  public static boolean list_variable(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "list_variable")) return false;
    if (!nextTokenIs(b, LIST_VARIABLE_START)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LIST_VARIABLE_START);
    r = r && variable_id(b, l + 1);
    r = r && consumeToken(b, VARIABLE_END);
    r = r && list_variable_3(b, l + 1);
    exit_section_(b, m, LIST_VARIABLE, r);
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
  // METADATA_KEYWORD line_comment* argument+ line_comment* EOL
  public static boolean metadata_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "metadata_statement")) return false;
    if (!nextTokenIs(b, METADATA_KEYWORD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, METADATA_STATEMENT, null);
    r = consumeToken(b, METADATA_KEYWORD);
    p = r; // pin = 1
    r = r && report_error_(b, metadata_statement_1(b, l + 1));
    r = p && report_error_(b, metadata_statement_2(b, l + 1)) && r;
    r = p && report_error_(b, metadata_statement_3(b, l + 1)) && r;
    r = p && consumeToken(b, EOL) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // line_comment*
  private static boolean metadata_statement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "metadata_statement_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!line_comment(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "metadata_statement_1", c)) break;
    }
    return true;
  }

  // argument+
  private static boolean metadata_statement_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "metadata_statement_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = argument(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!argument(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "metadata_statement_2", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // line_comment*
  private static boolean metadata_statement_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "metadata_statement_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!line_comment(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "metadata_statement_3", c)) break;
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
  // parameter_id ASSIGNMENT argument
  public static boolean parameter(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter")) return false;
    if (!nextTokenIs(b, PARAMETER_NAME)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, PARAMETER, null);
    r = parameter_id(b, l + 1);
    r = r && consumeToken(b, ASSIGNMENT);
    p = r; // pin = 2
    r = r && argument(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
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
  // RESOURCE_IMPORT_KEYWORD line_comment* argument line_comment* EOL
  public static boolean resource_import(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "resource_import")) return false;
    if (!nextTokenIs(b, RESOURCE_IMPORT_KEYWORD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, RESOURCE_IMPORT, null);
    r = consumeToken(b, RESOURCE_IMPORT_KEYWORD);
    p = r; // pin = 1
    r = r && report_error_(b, resource_import_1(b, l + 1));
    r = p && report_error_(b, argument(b, l + 1)) && r;
    r = p && report_error_(b, resource_import_3(b, l + 1)) && r;
    r = p && consumeToken(b, EOL) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // line_comment*
  private static boolean resource_import_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "resource_import_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!line_comment(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "resource_import_1", c)) break;
    }
    return true;
  }

  // line_comment*
  private static boolean resource_import_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "resource_import_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!line_comment(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "resource_import_3", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // !<<eof>> file2
  static boolean root(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "root")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = root_0(b, l + 1);
    r = r && file2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // !<<eof>>
  private static boolean root_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "root_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !eof(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // SCALAR_VARIABLE_START variable_id VARIABLE_END (extended_variable_slice_access | extended_variable_index_access | extended_variable_nested_access)*
  public static boolean scalar_variable(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "scalar_variable")) return false;
    if (!nextTokenIs(b, SCALAR_VARIABLE_START)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SCALAR_VARIABLE_START);
    r = r && variable_id(b, l + 1);
    r = r && consumeToken(b, VARIABLE_END);
    r = r && scalar_variable_3(b, l + 1);
    exit_section_(b, m, SCALAR_VARIABLE, r);
    return r;
  }

  // (extended_variable_slice_access | extended_variable_index_access | extended_variable_nested_access)*
  private static boolean scalar_variable_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "scalar_variable_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!scalar_variable_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "scalar_variable_3", c)) break;
    }
    return true;
  }

  // extended_variable_slice_access | extended_variable_index_access | extended_variable_nested_access
  private static boolean scalar_variable_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "scalar_variable_3_0")) return false;
    boolean r;
    r = extended_variable_slice_access(b, l + 1);
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
  // !(SETTINGS_HEADER | VARIABLES_HEADER | TEST_CASES_HEADER | TASKS_HEADER | KEYWORDS_HEADER | COMMENTS_HEADER)
  static boolean section_recover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "section_recover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !section_recover_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // SETTINGS_HEADER | VARIABLES_HEADER | TEST_CASES_HEADER | TASKS_HEADER | KEYWORDS_HEADER | COMMENTS_HEADER
  private static boolean section_recover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "section_recover_0")) return false;
    boolean r;
    r = consumeToken(b, SETTINGS_HEADER);
    if (!r) r = consumeToken(b, VARIABLES_HEADER);
    if (!r) r = consumeToken(b, TEST_CASES_HEADER);
    if (!r) r = consumeToken(b, TASKS_HEADER);
    if (!r) r = consumeToken(b, KEYWORDS_HEADER);
    if (!r) r = consumeToken(b, COMMENTS_HEADER);
    return r;
  }

  /* ********************************************************** */
  // library_import
  //     | resource_import
  //     | variables_import
  //     | metadata_statement
  //     | documentation_statement
  //     | suite_name_statement
  //     | setup_teardown_statements
  //     | tags_statement
  //     | template_statements
  //     | timeout_statements
  //     | unknown_setting_statements
  static boolean setting_statements(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "setting_statements")) return false;
    boolean r;
    r = library_import(b, l + 1);
    if (!r) r = resource_import(b, l + 1);
    if (!r) r = variables_import(b, l + 1);
    if (!r) r = metadata_statement(b, l + 1);
    if (!r) r = documentation_statement(b, l + 1);
    if (!r) r = suite_name_statement(b, l + 1);
    if (!r) r = setup_teardown_statements(b, l + 1);
    if (!r) r = tags_statement(b, l + 1);
    if (!r) r = template_statements(b, l + 1);
    if (!r) r = timeout_statements(b, l + 1);
    if (!r) r = unknown_setting_statements(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // SETTINGS_HEADER setting_statements*
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

  // setting_statements*
  private static boolean settings_section_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "settings_section_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!setting_statements(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "settings_section_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // SETUP_TEARDOWN_STATEMENT_KEYWORDS line_comment* (keyword_call | variable) line_comment*
  public static boolean setup_teardown_statements(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "setup_teardown_statements")) return false;
    if (!nextTokenIs(b, SETUP_TEARDOWN_STATEMENT_KEYWORDS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, SETUP_TEARDOWN_STATEMENTS, null);
    r = consumeToken(b, SETUP_TEARDOWN_STATEMENT_KEYWORDS);
    p = r; // pin = 1
    r = r && report_error_(b, setup_teardown_statements_1(b, l + 1));
    r = p && report_error_(b, setup_teardown_statements_2(b, l + 1)) && r;
    r = p && setup_teardown_statements_3(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // line_comment*
  private static boolean setup_teardown_statements_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "setup_teardown_statements_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!line_comment(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "setup_teardown_statements_1", c)) break;
    }
    return true;
  }

  // keyword_call | variable
  private static boolean setup_teardown_statements_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "setup_teardown_statements_2")) return false;
    boolean r;
    r = keyword_call(b, l + 1);
    if (!r) r = variable(b, l + 1);
    return r;
  }

  // line_comment*
  private static boolean setup_teardown_statements_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "setup_teardown_statements_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!line_comment(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "setup_teardown_statements_3", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // variable ASSIGNMENT? variable_value* EOL
  public static boolean single_variable_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "single_variable_statement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, SINGLE_VARIABLE_STATEMENT, "<single variable statement>");
    r = variable(b, l + 1);
    r = r && single_variable_statement_1(b, l + 1);
    r = r && single_variable_statement_2(b, l + 1);
    r = r && consumeToken(b, EOL);
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

  /* ********************************************************** */
  // SUITE_NAME_KEYWORD line_comment* argument line_comment* EOL
  public static boolean suite_name_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "suite_name_statement")) return false;
    if (!nextTokenIs(b, SUITE_NAME_KEYWORD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, SUITE_NAME_STATEMENT, null);
    r = consumeToken(b, SUITE_NAME_KEYWORD);
    p = r; // pin = 1
    r = r && report_error_(b, suite_name_statement_1(b, l + 1));
    r = p && report_error_(b, argument(b, l + 1)) && r;
    r = p && report_error_(b, suite_name_statement_3(b, l + 1)) && r;
    r = p && consumeToken(b, EOL) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // line_comment*
  private static boolean suite_name_statement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "suite_name_statement_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!line_comment(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "suite_name_statement_1", c)) break;
    }
    return true;
  }

  // line_comment*
  private static boolean suite_name_statement_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "suite_name_statement_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!line_comment(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "suite_name_statement_3", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // TAGS_KEYWORDS line_comment* argument+ line_comment* EOL
  public static boolean tags_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tags_statement")) return false;
    if (!nextTokenIs(b, TAGS_KEYWORDS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, TAGS_STATEMENT, null);
    r = consumeToken(b, TAGS_KEYWORDS);
    p = r; // pin = 1
    r = r && report_error_(b, tags_statement_1(b, l + 1));
    r = p && report_error_(b, tags_statement_2(b, l + 1)) && r;
    r = p && report_error_(b, tags_statement_3(b, l + 1)) && r;
    r = p && consumeToken(b, EOL) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // line_comment*
  private static boolean tags_statement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tags_statement_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!line_comment(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "tags_statement_1", c)) break;
    }
    return true;
  }

  // argument+
  private static boolean tags_statement_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tags_statement_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = argument(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!argument(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "tags_statement_2", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // line_comment*
  private static boolean tags_statement_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tags_statement_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!line_comment(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "tags_statement_3", c)) break;
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
  // task_id (bracket_setting | keyword_variable_statement | keyword_call | template_arguments | argument | parameter | line_comment)*
  public static boolean task_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "task_statement")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, TASK_STATEMENT, "<task statement>");
    r = task_id(b, l + 1);
    p = r; // pin = 1
    r = r && task_statement_1(b, l + 1);
    exit_section_(b, l, m, r, p, RobotParser::task_statement_recover);
    return r || p;
  }

  // (bracket_setting | keyword_variable_statement | keyword_call | template_arguments | argument | parameter | line_comment)*
  private static boolean task_statement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "task_statement_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!task_statement_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "task_statement_1", c)) break;
    }
    return true;
  }

  // bracket_setting | keyword_variable_statement | keyword_call | template_arguments | argument | parameter | line_comment
  private static boolean task_statement_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "task_statement_1_0")) return false;
    boolean r;
    r = bracket_setting(b, l + 1);
    if (!r) r = keyword_variable_statement(b, l + 1);
    if (!r) r = keyword_call(b, l + 1);
    if (!r) r = template_arguments(b, l + 1);
    if (!r) r = argument(b, l + 1);
    if (!r) r = parameter(b, l + 1);
    if (!r) r = line_comment(b, l + 1);
    return r;
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
  // (template_argument | template_parameter | variable)+ EOL
  public static boolean template_arguments(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "template_arguments")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, TEMPLATE_ARGUMENTS, "<template arguments>");
    r = template_arguments_0(b, l + 1);
    r = r && consumeToken(b, EOL);
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
  // TEMPLATE_KEYWORDS line_comment* keyword_call line_comment*
  public static boolean template_statements(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "template_statements")) return false;
    if (!nextTokenIs(b, TEMPLATE_KEYWORDS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, TEMPLATE_STATEMENTS, null);
    r = consumeToken(b, TEMPLATE_KEYWORDS);
    p = r; // pin = 1
    r = r && report_error_(b, template_statements_1(b, l + 1));
    r = p && report_error_(b, keyword_call(b, l + 1)) && r;
    r = p && template_statements_3(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // line_comment*
  private static boolean template_statements_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "template_statements_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!line_comment(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "template_statements_1", c)) break;
    }
    return true;
  }

  // line_comment*
  private static boolean template_statements_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "template_statements_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!line_comment(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "template_statements_3", c)) break;
    }
    return true;
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
  // test_case_id (bracket_setting | keyword_variable_statement | keyword_call | template_arguments | argument | parameter | line_comment)*
  public static boolean test_case_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "test_case_statement")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, TEST_CASE_STATEMENT, "<test case statement>");
    r = test_case_id(b, l + 1);
    p = r; // pin = 1
    r = r && test_case_statement_1(b, l + 1);
    exit_section_(b, l, m, r, p, RobotParser::test_case_statement_recover);
    return r || p;
  }

  // (bracket_setting | keyword_variable_statement | keyword_call | template_arguments | argument | parameter | line_comment)*
  private static boolean test_case_statement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "test_case_statement_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!test_case_statement_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "test_case_statement_1", c)) break;
    }
    return true;
  }

  // bracket_setting | keyword_variable_statement | keyword_call | template_arguments | argument | parameter | line_comment
  private static boolean test_case_statement_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "test_case_statement_1_0")) return false;
    boolean r;
    r = bracket_setting(b, l + 1);
    if (!r) r = keyword_variable_statement(b, l + 1);
    if (!r) r = keyword_call(b, l + 1);
    if (!r) r = template_arguments(b, l + 1);
    if (!r) r = argument(b, l + 1);
    if (!r) r = parameter(b, l + 1);
    if (!r) r = line_comment(b, l + 1);
    return r;
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
  // TIMEOUT_KEYWORDS line_comment* argument line_comment* EOL
  public static boolean timeout_statements(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "timeout_statements")) return false;
    if (!nextTokenIs(b, TIMEOUT_KEYWORDS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, TIMEOUT_STATEMENTS, null);
    r = consumeToken(b, TIMEOUT_KEYWORDS);
    p = r; // pin = 1
    r = r && report_error_(b, timeout_statements_1(b, l + 1));
    r = p && report_error_(b, argument(b, l + 1)) && r;
    r = p && report_error_(b, timeout_statements_3(b, l + 1)) && r;
    r = p && consumeToken(b, EOL) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // line_comment*
  private static boolean timeout_statements_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "timeout_statements_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!line_comment(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "timeout_statements_1", c)) break;
    }
    return true;
  }

  // line_comment*
  private static boolean timeout_statements_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "timeout_statements_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!line_comment(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "timeout_statements_3", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // UNKNOWN_SETTING_KEYWORD
  public static boolean unknown_setting_statement_id(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unknown_setting_statement_id")) return false;
    if (!nextTokenIs(b, UNKNOWN_SETTING_KEYWORD)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, UNKNOWN_SETTING_KEYWORD);
    exit_section_(b, m, UNKNOWN_SETTING_STATEMENT_ID, r);
    return r;
  }

  /* ********************************************************** */
  // unknown_setting_statement_id (parameter | argument | line_comment)* EOL
  public static boolean unknown_setting_statements(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unknown_setting_statements")) return false;
    if (!nextTokenIs(b, UNKNOWN_SETTING_KEYWORD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, UNKNOWN_SETTING_STATEMENTS, null);
    r = unknown_setting_statement_id(b, l + 1);
    p = r; // pin = 1
    r = r && report_error_(b, unknown_setting_statements_1(b, l + 1));
    r = p && consumeToken(b, EOL) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (parameter | argument | line_comment)*
  private static boolean unknown_setting_statements_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unknown_setting_statements_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!unknown_setting_statements_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "unknown_setting_statements_1", c)) break;
    }
    return true;
  }

  // parameter | argument | line_comment
  private static boolean unknown_setting_statements_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unknown_setting_statements_1_0")) return false;
    boolean r;
    r = parameter(b, l + 1);
    if (!r) r = argument(b, l + 1);
    if (!r) r = line_comment(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // scalar_variable
  //         | list_variable
  //         | dict_variable
  //         | environment_variable
  //         | constant_value
  static boolean value(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "value")) return false;
    boolean r;
    r = scalar_variable(b, l + 1);
    if (!r) r = list_variable(b, l + 1);
    if (!r) r = dict_variable(b, l + 1);
    if (!r) r = environment_variable(b, l + 1);
    if (!r) r = constant_value(b, l + 1);
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
  // value
  public static boolean variable_id(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variable_id")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, VARIABLE_ID, "<variable id>");
    r = value(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // value+
  public static boolean variable_value(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variable_value")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, VARIABLE_VALUE, "<variable value>");
    r = value(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!value(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "variable_value", c)) break;
    }
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // VARIABLES_IMPORT_KEYWORD line_comment* argument (parameter | argument)* line_comment* EOL
  public static boolean variables_import(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variables_import")) return false;
    if (!nextTokenIs(b, VARIABLES_IMPORT_KEYWORD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, VARIABLES_IMPORT, null);
    r = consumeToken(b, VARIABLES_IMPORT_KEYWORD);
    p = r; // pin = 1
    r = r && report_error_(b, variables_import_1(b, l + 1));
    r = p && report_error_(b, argument(b, l + 1)) && r;
    r = p && report_error_(b, variables_import_3(b, l + 1)) && r;
    r = p && report_error_(b, variables_import_4(b, l + 1)) && r;
    r = p && consumeToken(b, EOL) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // line_comment*
  private static boolean variables_import_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variables_import_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!line_comment(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "variables_import_1", c)) break;
    }
    return true;
  }

  // (parameter | argument)*
  private static boolean variables_import_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variables_import_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!variables_import_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "variables_import_3", c)) break;
    }
    return true;
  }

  // parameter | argument
  private static boolean variables_import_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variables_import_3_0")) return false;
    boolean r;
    r = parameter(b, l + 1);
    if (!r) r = argument(b, l + 1);
    return r;
  }

  // line_comment*
  private static boolean variables_import_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "variables_import_4")) return false;
    while (true) {
      int c = current_position_(b);
      if (!line_comment(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "variables_import_4", c)) break;
    }
    return true;
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

}
