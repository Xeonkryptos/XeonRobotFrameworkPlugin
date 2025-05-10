package dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.readability;

import com.intellij.codeInspection.options.OptPane;
import com.intellij.psi.PsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.SimpleInspection;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.SimpleRobotInspection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotKeywordProvider;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTokenTypes;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;

import static com.intellij.codeInspection.options.OptPane.checkbox;
import static com.intellij.codeInspection.options.OptPane.pane;

public class RobotGherkinInspection extends SimpleRobotInspection implements SimpleInspection {

    private static final Collection<String> NORMAL = new HashSet<>(RobotKeywordProvider.getSyntaxOfType(RobotTokenTypes.GHERKIN));

    @Nls
    @NotNull
    public String getDisplayName() {
        return RobotBundle.getMessage("INSP.NAME.gherkin.format");
    }

    @Override
    public @NotNull OptPane getOptionsPane() {
        return pane(checkbox("allowUppercase", RobotBundle.getMessage("INSP.OPT.gherkin.format.upper")));
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
