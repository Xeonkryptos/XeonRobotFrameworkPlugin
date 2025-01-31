package com.github.jnhyperion.hyperrobotframeworkplugin.ide.structure;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordDefinition;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.RobotFile;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.VariableDefinition;
import com.intellij.ide.structureView.StructureViewModelBase;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.StructureViewModel.ElementInfoProvider;
import com.intellij.ide.structureView.StructureViewModel.ExpandInfoProvider;
import com.intellij.ide.util.treeView.smartTree.Filter;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotStructureViewModel extends StructureViewModelBase implements ElementInfoProvider, ExpandInfoProvider {

   private static final Filter[] FILTERS = new Filter[]{
      new RobotTypeFilter("SHOW_TEST_CASES", RobotViewElementType.TestCase),
      new RobotTypeFilter("SHOW_KEYWORDS", RobotViewElementType.Keyword),
      new RobotTypeFilter("SHOW_VARIABLES", RobotViewElementType.Variable),
      new RobotTypeFilter("SHOW_SETTINGS", RobotViewElementType.Settings),
      new RobotTypeFilter("SHOW_HEADINGS", RobotViewElementType.Heading)
   };

   public RobotStructureViewModel(@NotNull PsiFile psiFile, @Nullable Editor editor) {
      this(psiFile, editor, new RobotStructureViewElement(psiFile));

      this.withSorters(Sorter.ALPHA_SORTER, RobotTypeSorter.getInstance());
   }

   private RobotStructureViewModel(@NotNull PsiFile var1, @Nullable Editor var2, @NotNull StructureViewTreeElement var3) {
      super(var1, var2, var3);
   }

   @Override
   public boolean isAlwaysShowsPlus(StructureViewTreeElement var1) {
      return var1.getValue() instanceof RobotFile;
   }

   @Override
   public boolean isAlwaysLeaf(StructureViewTreeElement var1) {
      Object var2;
      return (var2 = var1.getValue()) instanceof KeywordDefinition || var2 instanceof VariableDefinition;
   }

   @Override
   public boolean shouldEnterElement(Object var1) {
      return false;
   }

   @NotNull
   @Override
   public Filter @NotNull [] getFilters() {
      return FILTERS;
   }

   @Override
   public boolean isAutoExpand(StructureViewTreeElement var1) {
      return var1.getValue() instanceof RobotFile;
   }

   @Override
   public boolean isSmartExpand() {
      return false;
   }
}
