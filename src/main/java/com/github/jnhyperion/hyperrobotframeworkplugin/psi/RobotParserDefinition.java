package com.github.jnhyperion.hyperrobotframeworkplugin.psi;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.PositionalArgumentImpl;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.BracketSettingImpl;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.HeadingImpl;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.ImportImpl;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordDefinitionIdImpl;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordDefinitionImpl;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordInvokableImpl;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordPartImpl;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordStatementImpl;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.ParameterImpl;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.ParameterIdImpl;
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

   @NotNull
   @Override
   public Lexer createLexer(Project project) {
      return new RobotLexer();
   }

   @NotNull
   @Override
   public PsiParser createParser(Project project) {
      return new RobotParser();
   }

   @NotNull
   @Override
   public IFileElementType getFileNodeType() {
      return RobotStubTokenTypes.ROBOT_FILE;
   }

   @NotNull
   @Override
   public TokenSet getWhitespaceTokens() {
      return RobotTokenSets.WHITESPACE_SET;
   }

   @NotNull
   @Override
   public TokenSet getCommentTokens() {
      return RobotTokenSets.COMMENTS_SET;
   }

   @NotNull
   @Override
   public TokenSet getStringLiteralElements() {
      return RobotTokenSets.STRING_SET;
   }

   @NotNull
   @Override
   public PsiElement createElement(ASTNode node) {
      if (node.getElementType() == RobotStubTokenTypes.KEYWORD_DEFINITION) {
         return new KeywordDefinitionImpl(node);
      } else if (node.getElementType() == RobotTokenTypes.KEYWORD_DEFINITION_ID) {
         return new KeywordDefinitionIdImpl(node);
      } else if (node.getElementType() == RobotStubTokenTypes.KEYWORD_STATEMENT) {
         return new KeywordStatementImpl(node);
      } else if (node.getElementType() == RobotTokenTypes.KEYWORD) {
         return new KeywordInvokableImpl(node);
      } else if (node.getElementType() == RobotTokenTypes.SYNTAX_MARKER) {
         return new KeywordInvokableImpl(node);
      } else if (node.getElementType() == RobotStubTokenTypes.VARIABLE_DEFINITION) {
         return new VariableDefinitionImpl(node);
      } else if (node.getElementType() == RobotTokenTypes.VARIABLE_DEFINITION_ID) {
         return new VariableDefinitionIdImpl(node);
      } else if (node.getElementType() == RobotTokenTypes.HEADING) {
         return new HeadingImpl(node);
      } else if (node.getElementType() == RobotTokenTypes.PARAMETER) {
         return new ParameterImpl(node);
      } else if (node.getElementType() == RobotTokenTypes.PARAMETER_ID) {
         return new ParameterIdImpl(node);
      } else if (node.getElementType() == RobotStubTokenTypes.ARGUMENT) {
         return new PositionalArgumentImpl(node);
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
   @Override
   public PsiFile createFile(@NotNull FileViewProvider fileViewProvider) {
      return new RobotFileImpl(fileViewProvider);
   }
}
