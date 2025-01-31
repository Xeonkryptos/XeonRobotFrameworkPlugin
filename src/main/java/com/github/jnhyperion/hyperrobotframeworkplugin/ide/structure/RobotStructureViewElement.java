package com.github.jnhyperion.hyperrobotframeworkplugin.ide.structure;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.DefinedKeyword;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.Heading;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.RobotFile;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.RobotStatement;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.VariableDefinition;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ColoredItemPresentation;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.Icon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotStructureViewElement implements StructureViewTreeElement {

   private static final String UNKNOWN = "Unknown";

   private final PsiElement element;
   private final RobotViewElementType type;

   protected RobotStructureViewElement(PsiElement element) {
      this(element, RobotViewElementType.File);
   }

   private RobotStructureViewElement(PsiElement element, RobotViewElementType type) {
      this.element = element;
      this.type = type;
   }

   private static StructureViewTreeElement createChild(PsiElement element, RobotViewElementType type) {
      return new RobotStructureViewElement(element, type);
   }

   @Override
   public PsiElement getValue() {
      return this.element;
   }

   @Override
   public void navigate(boolean requestFocus) {
      if (this.element instanceof Navigatable) {
         ((Navigatable)this.element).navigate(requestFocus);
      }
   }

   @Override
   public boolean canNavigate() {
      return this.type != RobotViewElementType.File && this.element instanceof Navigatable && ((Navigatable)this.element).canNavigate();
   }

   @Override
   public boolean canNavigateToSource() {
      return this.type != RobotViewElementType.File && this.element instanceof Navigatable && ((Navigatable)this.element).canNavigateToSource();
   }

   @Override
   public boolean equals(Object o) {
      if (o instanceof RobotStructureViewElement) {
         String name = getDisplayName();
         String otherName = ((RobotStructureViewElement) o).getDisplayName();
         return name.equals(otherName);
      }
      return false;
   }

   @Override
   public int hashCode() {
      return getDisplayName().hashCode();
   }

   @NotNull
   @Override
   public TreeElement @NotNull [] getChildren() {
      List<StructureViewTreeElement> elements = new ArrayList<>();
      if (this.element instanceof RobotFile) {
         Heading[] headings = PsiTreeUtil.getChildrenOfType(this.element, Heading.class);
         if (headings != null) {
            for (Heading heading : headings) {
               elements.add(createChild(heading, RobotViewElementType.Heading));
            }
         }
      } else if (this.element instanceof Heading) {
         Heading heading = (Heading) this.element;
         Collection<RobotStatement> statements = heading.getMetadataStatements();
         for (RobotStatement statement : statements) {
            elements.add(createChild(statement, RobotViewElementType.Settings));
         }

         for (DefinedKeyword definedKeyword : heading.collectDefinedKeywords()) {
            elements.add(createChild(definedKeyword.reference(), RobotViewElementType.Keyword));
         }

         for (DefinedKeyword testCase : heading.getTestCases()) {
            elements.add(createChild(testCase.reference(), RobotViewElementType.TestCase));
         }

         for (VariableDefinition variableDefinition : heading.getVariableDefinitions()) {
            elements.add(createChild(variableDefinition, RobotViewElementType.Variable));
         }
      }
      return elements.toArray(new StructureViewTreeElement[0]);
   }

   @NotNull
   private String getDisplayName() {
      if (this.element instanceof RobotFile) {
         return ((RobotFile) this.element).getName();
      } else if (this.element instanceof RobotStatement) {
         return ((RobotStatement) this.element).getPresentableText();
      } else {
         return UNKNOWN;
      }
   }

   @NotNull
   public final RobotViewElementType getType() {
      return this.type;
   }

   @Nullable
   private Icon getDisplayIcon() {
      return this.type.getIcon(this.element);
   }

   @NotNull
   @Override
   public ItemPresentation getPresentation() {
      return new ColoredItemPresentation() {

         @Override
         public String getPresentableText() {
            return getDisplayName();
         }

         @Nullable
         @Override
         public TextAttributesKey getTextAttributesKey() {
            return null;
         }

         @Nullable
         @Override
         public String getLocationString() {
            return null;
         }

         @Nullable
         @Override
         public Icon getIcon(boolean var1) {
            return getDisplayIcon();
         }
      };
   }
}
