package com.github.jnhyperion.hyperrobotframeworkplugin.ide.inspections.readability;

import com.github.jnhyperion.hyperrobotframeworkplugin.RobotBundle;
import com.github.jnhyperion.hyperrobotframeworkplugin.ide.inspections.SimpleRobotInspection;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RobotKeywordProvider;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RobotTokenTypes;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordDefinition;
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
        if (element instanceof KeywordDefinition) {
            String text = ((KeywordDefinition) element).getPresentableText();
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
