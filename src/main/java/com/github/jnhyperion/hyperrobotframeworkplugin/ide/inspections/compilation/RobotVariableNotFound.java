package com.github.jnhyperion.hyperrobotframeworkplugin.ide.inspections.compilation;

import com.github.jnhyperion.hyperrobotframeworkplugin.RobotBundle;
import com.github.jnhyperion.hyperrobotframeworkplugin.ide.inspections.SimpleRobotInspection;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.Argument;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordStatement;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.Variable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RobotVariableNotFound extends SimpleRobotInspection {

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("^\\$\\{(.*)}$");

    @Nls
    @NotNull
    public String getDisplayName() {
        return RobotBundle.getMessage("INSP.NAME.variable.undefined");
    }

    @Override
    public final boolean skip(PsiElement element) {
        if (!(element instanceof Variable)) {
            return true;
        }

        PsiReference reference = element.getReference();
        if (reference != null && reference.resolve() != null) {
            return true;
        }

        if (((Variable) element).isNested()) {
            return true;
        }

        String elementText = element.getText();
        Matcher matcher = VARIABLE_PATTERN.matcher(elementText);
        if (matcher.matches()) {
            try {
                Double.parseDouble(matcher.group(1));
                return true;
            } catch (NumberFormatException ignored) {
            }
        }

        KeywordStatement keywordStatement = PsiTreeUtil.getParentOfType(element, KeywordStatement.class);
        if (keywordStatement != null) {
            List<Argument> arguments = keywordStatement.getArguments();
            return keywordStatement.getGlobalVariable() != null && arguments.size() > 1 && element == arguments.get(0);
        }

        return false;
    }

    @Override
    public final String getMessage() {
        return RobotBundle.getMessage("INSP.variable.undefined");
    }

    @NotNull
    @Override
    protected final String getGroupNameKey() {
        return "INSP.GROUP.compilation";
    }
}
