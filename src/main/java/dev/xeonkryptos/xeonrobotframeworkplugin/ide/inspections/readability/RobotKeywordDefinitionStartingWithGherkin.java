package dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.readability;

import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.SimpleRobotInspection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotKeywordProvider;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTokenTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordDefinition;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class RobotKeywordDefinitionStartingWithGherkin extends SimpleRobotInspection {
    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return RobotBundle.getMessage("INSP.NAME.define.keyword.gherkin.start");
    }

    @Override
    public final boolean skip(PsiElement element) {
        if (element instanceof KeywordDefinition keywordDefinition) {
            String text = keywordDefinition.getKeywordName();
            Set<String> syntax = RobotKeywordProvider.getSyntaxOfType(RobotTokenTypes.GHERKIN);
            int index = text.indexOf(" ");
            String word;
            if (index < 0) {
                word = text;
            } else {
                word = text.substring(0, index);
            }
            return !syntax.contains(word);
        }

        return true;
    }

    @Override
    public final String getMessage() {
        return RobotBundle.getMessage("INSP.define.keyword.gherkin.start");
    }

    @NotNull
    @Override
    protected final String getGroupNameKey() {
        return "INSP.GROUP.readability";
    }
}
