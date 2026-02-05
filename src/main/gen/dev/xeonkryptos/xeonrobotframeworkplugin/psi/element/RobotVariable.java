// This is a generated file. Not intended for manual editing.
package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;

public interface RobotVariable extends RobotFoldable, RobotAssignedVariable, RobotElement {

  @Nullable String getVariableName();

  FoldingDescriptor[] fold(@NotNull Document ignoredDocument, boolean quick);

  FoldingText getAssignedValues();

}
