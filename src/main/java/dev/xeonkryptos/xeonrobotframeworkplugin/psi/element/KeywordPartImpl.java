package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.RobotKeywordReference;
import com.intellij.lang.ASTNode;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

public class KeywordPartImpl extends RobotPsiElementBase implements KeywordInvokable {

   public KeywordPartImpl(@NotNull ASTNode node) {
      super(node);
   }

   @NotNull
   @Override
   public Collection<Parameter> getParameters() {
      return Collections.emptySet();
   }

   @NotNull
   @Override
   public final Collection<PositionalArgument> getPositionalArguments() {
       return Collections.emptySet();
   }

   @NotNull
   @Override
   public PsiReference getReference() {
      return new RobotKeywordReference(this);
   }

   @NotNull
   @Override
   public final String getPresentableText() {
      PsiElement parent = getParent();
      String unescapedText = InjectedLanguageManager.getInstance(getProject()).getUnescapedText(parent);
      return getPresentableText(unescapedText);
   }
}
