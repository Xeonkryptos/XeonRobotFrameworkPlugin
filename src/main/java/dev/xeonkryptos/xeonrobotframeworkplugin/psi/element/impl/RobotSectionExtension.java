package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.Ref;
import dev.xeonkryptos.xeonrobotframeworkplugin.config.RobotFoldingSettings;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotCommentsSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordsSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSettingsSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTasksSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCasesSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariablesSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.folding.RobotFoldingComputationUtil;
import org.jetbrains.annotations.NotNull;

public abstract class RobotSectionExtension extends RobotPsiElementBase implements RobotSection {

    public RobotSectionExtension(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @NotNull FoldingDescriptor @NotNull [] fold(@NotNull Document document, boolean quick) {
        if (!RobotFoldingComputationUtil.isFoldingUseful(this, document)) {
            return FoldingDescriptor.EMPTY_ARRAY;
        }
        Ref<Boolean> collapseByDefault = Ref.create(false);
        accept(new RobotVisitor() {
            @Override
            public void visitSettingsSection(@NotNull RobotSettingsSection o) {
                boolean collapse = RobotFoldingSettings.getInstance().getState().getCollapseSettingsSection();
                collapseByDefault.set(collapse);
            }

            @Override
            public void visitVariablesSection(@NotNull RobotVariablesSection o) {
                boolean collapse = RobotFoldingSettings.getInstance().getState().getCollapseVariablesSection();
                collapseByDefault.set(collapse);
            }

            @Override
            public void visitCommentsSection(@NotNull RobotCommentsSection o) {
                boolean collapse = RobotFoldingSettings.getInstance().getState().getCollapseCommentSection();
                collapseByDefault.set(collapse);
            }

            @Override
            public void visitKeywordsSection(@NotNull RobotKeywordsSection o) {
                boolean collapse = RobotFoldingSettings.getInstance().getState().getCollapseKeywordsSection();
                collapseByDefault.set(collapse);
            }

            @Override
            public void visitTestCasesSection(@NotNull RobotTestCasesSection o) {
                boolean collapse = RobotFoldingSettings.getInstance().getState().getCollapseTestCasesSection();
                collapseByDefault.set(collapse);
            }

            @Override
            public void visitTasksSection(@NotNull RobotTasksSection o) {
                boolean collapse = RobotFoldingSettings.getInstance().getState().getCollapseTasksSection();
                collapseByDefault.set(collapse);
            }
        });
        var foldingDescriptor = RobotFoldingComputationUtil.computeFoldingDescriptorForContainer(this, getFirstChild(), document, collapseByDefault.get());
        return foldingDescriptor != null ? new FoldingDescriptor[] { foldingDescriptor } : FoldingDescriptor.EMPTY_ARRAY;
    }
}
