// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface RobotLibraryImport extends PsiElement {

  @NotNull
  List<RobotValue> getValueList();

  @NotNull
  List<RobotWhitespace> getWhitespaceList();

  @NotNull
  PsiElement getEol();

  @NotNull
  PsiElement getLibraryWords();

  @Nullable
  PsiElement getWithNameWords();

}
