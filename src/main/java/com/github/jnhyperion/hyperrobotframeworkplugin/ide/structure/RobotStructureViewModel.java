package com.github.jnhyperion.hyperrobotframeworkplugin.ide.structure;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordDefinition;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.RobotFile;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.VariableDefinition;
import com.intellij.ide.structureView.StructureViewModel.ElementInfoProvider;
import com.intellij.ide.structureView.StructureViewModel.ExpandInfoProvider;
import com.intellij.ide.structureView.StructureViewModelBase;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.Filter;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotStructureViewModel extends StructureViewModelBase implements ElementInfoProvider, ExpandInfoProvider {

    private static final Filter[] FILTERS = new Filter[] { new RobotTypeFilter("SHOW_TEST_CASES", RobotViewElementType.TestCase),
                                                           new RobotTypeFilter("SHOW_KEYWORDS", RobotViewElementType.Keyword),
                                                           new RobotTypeFilter("SHOW_VARIABLES", RobotViewElementType.Variable),
                                                           new RobotTypeFilter("SHOW_SETTINGS", RobotViewElementType.Settings),
                                                           new RobotTypeFilter("SHOW_HEADINGS", RobotViewElementType.Heading) };

    public RobotStructureViewModel(@NotNull PsiFile psiFile, @Nullable Editor editor) {
        super(psiFile, editor, new RobotStructureViewElement(psiFile));

        withSorters(Sorter.ALPHA_SORTER, RobotTypeSorter.getInstance());
    }

    @Override
    public boolean isAlwaysShowsPlus(StructureViewTreeElement element) {
        return element.getValue() instanceof RobotFile;
    }

    @Override
    public boolean isAlwaysLeaf(StructureViewTreeElement element) {
        Object psiElement = element.getValue();
        return psiElement instanceof KeywordDefinition || psiElement instanceof VariableDefinition;
    }

    @Override
    public boolean shouldEnterElement(Object element) {
        return false;
    }

    @Override
    public Filter @NotNull [] getFilters() {
        return FILTERS;
    }

    @Override
    public boolean isAutoExpand(StructureViewTreeElement element) {
        return element.getValue() instanceof RobotFile;
    }

    @Override
    public boolean isSmartExpand() {
        return false;
    }
}
