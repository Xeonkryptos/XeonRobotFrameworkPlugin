package com.github.jnhyperion.hyperrobotframeworkplugin.psi;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.ArgumentImpl;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.BracketSettingImpl;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.HeadingImpl;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.ImportImpl;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordDefinitionIdImpl;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordDefinitionImpl;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordInvokableImpl;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordPartImpl;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordStatementImpl;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.ParameterIdImpl;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.ParameterImpl;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.RobotFileImpl;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.SettingImpl;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.VariableDefinitionIdImpl;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.VariableDefinitionImpl;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.VariableImpl;
import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiUtilCore;
import org.jetbrains.annotations.NotNull;

public class RobotParserDefinition implements ParserDefinition {

   private static final TokenSet WHITESPACE_SET = TokenSet.create(RobotTokenTypes.WHITESPACE);
   private static final TokenSet COMMENTS_SET = TokenSet.create(RobotTokenTypes.COMMENT);
   private static final TokenSet STRING_SET = TokenSet.create(RobotTokenTypes.GHERKIN, RobotTokenTypes.SYNTAX_MARKER);

   @NotNull
   public Lexer createLexer(Project project) {
      return new RobotLexer();
   }

   @NotNull
   public PsiParser createParser(Project project) {
      return new RobotParser();
   }

   @NotNull
   public IFileElementType getFileNodeType() {
      return RobotTokenTypes.ROBOT_FILE;
   }

   @NotNull
   public TokenSet getWhitespaceTokens() {
      return WHITESPACE_SET;
   }

   @NotNull
   public TokenSet getCommentTokens() {
      return COMMENTS_SET;
   }

   @NotNull
   public TokenSet getStringLiteralElements() {
      return STRING_SET;
   }

   @NotNull
   public PsiElement createElement(ASTNode node) {
      if (node.getElementType() == RobotTokenTypes.KEYWORD_DEFINITION) {
         return new KeywordDefinitionImpl(node);
      } else if (node.getElementType() == RobotTokenTypes.KEYWORD_DEFINITION_ID) {
         return new KeywordDefinitionIdImpl(node);
      } else if (node.getElementType() == RobotTokenTypes.KEYWORD_STATEMENT) {
         return new KeywordStatementImpl(node);
      } else if (node.getElementType() == RobotTokenTypes.KEYWORD) {
         return new KeywordInvokableImpl(node);
      } else if (node.getElementType() == RobotTokenTypes.SYNTAX_MARKER) {
         return new KeywordInvokableImpl(node);
      } else if (node.getElementType() == RobotTokenTypes.VARIABLE_DEFINITION) {
         return new VariableDefinitionImpl(node);
      } else if (node.getElementType() == RobotTokenTypes.VARIABLE_DEFINITION_ID) {
         return new VariableDefinitionIdImpl(node);
      } else if (node.getElementType() == RobotTokenTypes.HEADING) {
         return new HeadingImpl(node);
      } else if (node.getElementType() == RobotTokenTypes.PARAMETER) {
         return new ParameterImpl(node);
      } else if (node.getElementType() == RobotTokenTypes.PARAMETER_ID) {
         return new ParameterIdImpl(node);
      } else if (node.getElementType() == RobotTokenTypes.ARGUMENT) {
         return new ArgumentImpl(node);
      } else if (node.getElementType() == RobotTokenTypes.VARIABLE) {
         return new VariableImpl(node);
      } else if (node.getElementType() == RobotTokenTypes.IMPORT) {
         return new ImportImpl(node);
      } else if (node.getElementType() == RobotTokenTypes.SETTING) {
         return new SettingImpl(node);
      } else if (node.getElementType() == RobotTokenTypes.BRACKET_SETTING) {
         return new BracketSettingImpl(node);
      } else if (node.getElementType() == RobotTokenTypes.KEYWORD_PART) {
         return new KeywordPartImpl(node);
      } else {
         return PsiUtilCore.NULL_PSI_ELEMENT;
      }
   }

   @NotNull
   public PsiFile createFile(@NotNull FileViewProvider fileViewProvider) {
      return new RobotFileImpl(fileViewProvider);
   }
}
