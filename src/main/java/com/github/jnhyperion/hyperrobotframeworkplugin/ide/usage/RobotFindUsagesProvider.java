package com.github.jnhyperion.hyperrobotframeworkplugin.ide.usage;

import com.github.jnhyperion.hyperrobotframeworkplugin.RobotBundle;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.Import;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordDefinition;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordStatement;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.Parameter;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.PositionalArgument;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.RobotFile;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.VariableDefinition;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotFindUsagesProvider implements FindUsagesProvider {

    @Nullable
    @Override
    public WordsScanner getWordsScanner() {
        return new RobotWordScanner();
    }

    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement element) {
        return !(element instanceof PositionalArgument) || !(element.getParent() instanceof Import) ?
               element instanceof PsiNamedElement :
               element == element.getParent().getFirstChild();
    }

    @Nullable
    @Override
    public String getHelpId(@NotNull PsiElement element) {
        if (element instanceof VariableDefinition) {
            return "reference.dialogs.findUsages.variable";
        } else if (element instanceof KeywordStatement) {
            return "reference.dialogs.findUsages.method";
        } else if (element instanceof KeywordDefinition) {
            return "reference.dialogs.findUsages.class";
        } else if (element instanceof Parameter) {
            return "reference.dialogs.findUsages.parameter";
        } else if (element instanceof PositionalArgument) {
            return "reference.dialogs.findUsages.argument";
        }
        return null;
    }

    @NotNull
    @Override
    public String getType(@NotNull PsiElement element) {
        return RobotBundle.getMessage("usage.declaration");
    }

    @NotNull
    @Override
    public String getDescriptiveName(@NotNull PsiElement element) {
        if (element instanceof KeywordDefinition) {
            return RobotBundle.getMessage("usage.descriptive.keyword");
        } else if (element instanceof VariableDefinition) {
            return RobotBundle.getMessage("usage.descriptive.variable");
        } else if (element instanceof RobotFile) {
            return RobotBundle.getMessage("usage.descriptive.import");
        } else if (element instanceof Parameter) {
            return RobotBundle.getMessage("usage.descriptive.parameter");
        } else if (element instanceof PositionalArgument) {
            return RobotBundle.getMessage("usage.descriptive.argument");
        } else {
            return "";
        }
    }

    @NotNull
    @Override
    public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        return getDescriptiveName(element);
    }
}
