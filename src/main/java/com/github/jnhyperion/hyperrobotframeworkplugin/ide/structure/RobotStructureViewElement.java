package com.github.jnhyperion.hyperrobotframeworkplugin.ide.structure;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.DefinedKeyword;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.Heading;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordDefinition;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
        return element;
    }

    @Override
    public void navigate(boolean requestFocus) {
        if (element instanceof Navigatable navigatable) {
            navigatable.navigate(requestFocus);
        }
    }

    @Override
    public boolean canNavigate() {
        return type != RobotViewElementType.File && element instanceof Navigatable navigatable && navigatable.canNavigate();
    }

    @Override
    public boolean canNavigateToSource() {
        return type != RobotViewElementType.File && element instanceof Navigatable navigatable && navigatable.canNavigateToSource();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof RobotStructureViewElement viewElement) {
            String name = getDisplayName();
            String otherName = viewElement.getDisplayName();
            return name.equals(otherName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getDisplayName().hashCode();
    }

    @Override
    public TreeElement @NotNull [] getChildren() {
        List<StructureViewTreeElement> elements = new ArrayList<>();
        if (element instanceof RobotFile) {
            Heading[] headings = PsiTreeUtil.getChildrenOfType(element, Heading.class);
            if (headings != null) {
                for (Heading heading : headings) {
                    elements.add(createChild(heading, RobotViewElementType.Heading));
                }
            }
        } else if (element instanceof Heading heading) {
            Collection<RobotStatement> statements = heading.getMetadataStatements();
            for (RobotStatement statement : statements) {
                elements.add(createChild(statement, RobotViewElementType.Settings));
            }

            for (DefinedKeyword definedKeyword : heading.collectDefinedKeywords()) {
                elements.add(createChild(definedKeyword.reference(), RobotViewElementType.Keyword));
            }

            for (KeywordDefinition testCase : heading.getTestCases()) {
                elements.add(createChild(testCase, RobotViewElementType.TestCase));
            }

            for (VariableDefinition variableDefinition : heading.getVariableDefinitions()) {
                elements.add(createChild(variableDefinition, RobotViewElementType.Variable));
            }
        }
        return elements.toArray(StructureViewTreeElement.EMPTY_ARRAY);
    }

    @NotNull
    private String getDisplayName() {
        if (element instanceof RobotFile robotFile) {
            return robotFile.getName();
        } else if (element instanceof RobotStatement robotStatement) {
            return robotStatement.getPresentableText();
        } else {
            return UNKNOWN;
        }
    }

    @NotNull
    public final RobotViewElementType getType() {
        return type;
    }

    @Nullable
    private Icon getDisplayIcon() {
        return type.getIcon(element);
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
