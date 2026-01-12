package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RobotFoldable extends RobotElement {

    @Nullable
    FoldingDescriptor[] fold(@NotNull Document document);
}
