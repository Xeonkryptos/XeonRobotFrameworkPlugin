package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import org.jetbrains.annotations.NotNull;

public interface RobotFoldable extends RobotElement {

    @NotNull
    FoldingDescriptor[] fold(@NotNull Document document, boolean quick);
}
