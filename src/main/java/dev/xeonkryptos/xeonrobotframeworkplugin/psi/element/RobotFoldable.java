package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.folding.RobotFoldingComputationUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RobotFoldable extends RobotElement {

    @Nullable
    default FoldingDescriptor[] fold(@NotNull Document document) {
        if (!RobotFoldingComputationUtil.isFoldingUseful(getTextRange(), document)) {
            return null;
        }
        var foldingDescriptor = RobotFoldingComputationUtil.computeSimpleFoldingRegionFor(this, document);
        return new FoldingDescriptor[] { foldingDescriptor };
    }
}
