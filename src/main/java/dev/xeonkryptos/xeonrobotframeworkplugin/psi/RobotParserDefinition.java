package dev.xeonkryptos.xeonrobotframeworkplugin.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
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
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFileImpl;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotStubFileElementType;
import org.jetbrains.annotations.NotNull;

public class RobotParserDefinition implements ParserDefinition {

   private static final IFileElementType ROBOT_FILE = new RobotStubFileElementType();

   @NotNull
   @Override
   public Lexer createLexer(Project project) {
      return new RobotLexerAdapter();
   }

   @NotNull
   @Override
   public PsiParser createParser(Project project) {
      return new RobotParser();
   }

   @NotNull
   @Override
   public IFileElementType getFileNodeType() {
      return ROBOT_FILE;
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
       try {
           return RobotTypes.Factory.createElement(node);
       } catch (AssertionError ignored) {
           return new ASTWrapperPsiElement(node);
       }
   }

   @NotNull
   @Override
   public PsiFile createFile(@NotNull FileViewProvider fileViewProvider) {
      return new RobotFileImpl(fileViewProvider);
   }
}
