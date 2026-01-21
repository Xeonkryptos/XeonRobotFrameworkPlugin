package dev.xeonkryptos.xeonrobotframeworkplugin.misc;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.CustomFoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.util.containers.ContainerUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotConditionalStructure;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotElseStructure;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotExceptStructure;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFinallyStructure;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFoldable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotForLoopStructure;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotGroupStructure;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTaskStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTryStructure;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotWhileLoopStructure;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.folding.RobotFoldingComputationUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RecursiveRobotVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RobotFoldingBuilder extends CustomFoldingBuilder {

    @Override
    protected void buildLanguageFoldRegions(@NotNull List<FoldingDescriptor> descriptors, @NotNull PsiElement root, @NotNull Document document, boolean quick) {
        PsiElementVisitor visitor = new RobotFoldingElementsVisitor(document, descriptors);
        root.accept(visitor);
    }

    @Override
    protected String getLanguagePlaceholderText(@NotNull ASTNode node, @NotNull TextRange range) {
        ItemPresentation presentation = ((NavigationItem) node.getPsi()).getPresentation();
        return presentation != null ? presentation.getPresentableText() : RobotFoldingComputationUtil.CONTAINER_FOLDING_PLACEHOLDER;
    }

    @Override
    protected boolean isRegionCollapsedByDefault(@NotNull ASTNode node) {
        return false;
    }

    private static final class RobotFoldingElementsVisitor extends RecursiveRobotVisitor {

        private final Document document;
        private final List<FoldingDescriptor> descriptors;

        private RobotFoldingElementsVisitor(Document document, List<FoldingDescriptor> descriptors) {
            this.document = document;
            this.descriptors = descriptors;
        }

        @Override
        public void visitKeywordCall(@NotNull RobotKeywordCall o) {
            visitFoldable(o);
        }

        @Override
        public void visitTaskStatement(@NotNull RobotTaskStatement o) {
            visitFoldable(o);
        }

        @Override
        public void visitTestCaseStatement(@NotNull RobotTestCaseStatement o) {
            visitFoldable(o);
        }

        @Override
        public void visitUserKeywordStatement(@NotNull RobotUserKeywordStatement o) {
            visitFoldable(o);
        }

        @Override
        public void visitForLoopStructure(@NotNull RobotForLoopStructure o) {
            visitFoldable(o);
        }

        @Override
        public void visitConditionalStructure(@NotNull RobotConditionalStructure o) {
            visitFoldable(o);
        }

        @Override
        public void visitGroupStructure(@NotNull RobotGroupStructure o) {
            visitFoldable(o);
        }

        @Override
        public void visitExceptStructure(@NotNull RobotExceptStructure o) {
            visitFoldable(o);
        }

        @Override
        public void visitFinallyStructure(@NotNull RobotFinallyStructure o) {
            visitFoldable(o);
        }

        @Override
        public void visitTryStructure(@NotNull RobotTryStructure o) {
            visitFoldable(o);
        }

        @Override
        public void visitElseStructure(@NotNull RobotElseStructure o) {
            visitFoldable(o);
        }

        @Override
        public void visitWhileLoopStructure(@NotNull RobotWhileLoopStructure o) {
            visitFoldable(o);
        }

        @Override
        public void visitFoldable(@NotNull RobotFoldable foldable) {
            FoldingDescriptor[] foldingRegions = foldable.fold(document);
            if (foldingRegions != null && foldingRegions.length > 0) {
                ContainerUtil.addAll(descriptors, foldingRegions);
            }

            super.visitFoldable(foldable);
        }
    }
}
