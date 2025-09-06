package dev.xeonkryptos.xeonrobotframeworkplugin.ide.structure;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ColoredItemPresentation;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotGlobalSettingStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTaskStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RobotStructureViewElement implements StructureViewTreeElement {

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

        RobotSectionElementsCollector collector = new RobotSectionElementsCollector();
        element.acceptChildren(collector);

        Collection<RobotSection> sections = collector.getSections();
        for (RobotSection section : sections) {
            elements.add(createChild(section, RobotViewElementType.Section));
        }

        Collection<RobotGlobalSettingStatement> statements = collector.getMetadataStatements();
        for (PsiElement statement : statements) {
            elements.add(createChild(statement, RobotViewElementType.Settings));
        }

        for (RobotUserKeywordStatement userKeywordStatement : collector.getUserKeywordStatements()) {
            elements.add(createChild(userKeywordStatement, RobotViewElementType.Keyword));
        }

        for (RobotTestCaseStatement testCase : collector.getTestCases()) {
            elements.add(createChild(testCase, RobotViewElementType.TestCase));
        }

        for (RobotTaskStatement task : collector.getTasks()) {
            elements.add(createChild(task, RobotViewElementType.Task));
        }

        for (RobotVariableDefinition variableDefinition : collector.getVariableDefinitions()) {
            elements.add(createChild(variableDefinition, RobotViewElementType.Variable));
        }

        for (RobotKeywordCall keywordCall : collector.getKeywordCalls()) {
            elements.add(createChild(keywordCall, RobotViewElementType.Keyword));
        }
        return elements.toArray(StructureViewTreeElement.EMPTY_ARRAY);
    }

    @NotNull
    private String getDisplayName() {
        if (element instanceof RobotFile robotFile) {
            return robotFile.getName();
        } else if (element instanceof PsiNamedElement namedElement) {
            String name = namedElement.getName();
            if (name != null) {
                return name;
            }
        }
        return "Unknown";
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
            public Icon getIcon(boolean unused) {
                return getDisplayIcon();
            }
        };
    }
}
