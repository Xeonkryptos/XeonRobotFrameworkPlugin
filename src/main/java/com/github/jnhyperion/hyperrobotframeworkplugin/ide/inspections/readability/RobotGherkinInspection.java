package com.github.jnhyperion.hyperrobotframeworkplugin.ide.inspections.readability;

import com.github.jnhyperion.hyperrobotframeworkplugin.RobotBundle;
import com.github.jnhyperion.hyperrobotframeworkplugin.ide.inspections.SimpleInspection;
import com.github.jnhyperion.hyperrobotframeworkplugin.ide.inspections.SimpleRobotInspection;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RobotKeywordProvider;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RobotTokenTypes;
import com.intellij.codeInspection.ui.MultipleCheckboxOptionsPanel;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;
import java.util.Collection;
import java.util.HashSet;

public class RobotGherkinInspection extends SimpleRobotInspection implements SimpleInspection {

    private static final Collection<String> NORMAL = new HashSet<>();
    private static final Collection<String> UPPER = new HashSet<>();

    private boolean c = false;

    static {
        for (String syntax : RobotKeywordProvider.getSyntaxOfType(RobotTokenTypes.GHERKIN)) {
            NORMAL.add(syntax);
            UPPER.add(syntax.toUpperCase());
        }
    }

    @Nls
    @NotNull
    public String getDisplayName() {
        return RobotBundle.getMessage("INSP.NAME.gherkin.format");
    }

    public JComponent createOptionsPanel() {
        MultipleCheckboxOptionsPanel panel = new MultipleCheckboxOptionsPanel(this);
        panel.addCheckbox(RobotBundle.getMessage("INSP.OPT.gherkin.format.upper"), "allowUppercase");
        return panel;
    }

    @Override
    public final boolean skip(PsiElement element) {
        if (element.getNode().getElementType() == RobotTokenTypes.GHERKIN) {
            String text = element.getText();
            return NORMAL.contains(text);
        }
        return true;
    }

    @Override
    public final String getMessage() {
        return RobotBundle.getMessage("INSP.gherkin.format");
    }

    @NotNull
    @Override
    protected final String getGroupNameKey() {
        return "INSP.GROUP.readability";
    }
}
