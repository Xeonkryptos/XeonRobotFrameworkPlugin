package dev.xeonkryptos.xeonrobotframeworkplugin.usage;

import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableStatement;
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
        return element instanceof PsiNamedElement;
    }

    @Nullable
    @Override
    public String getHelpId(@NotNull PsiElement element) {
        return switch (element) {
            case RobotVariableStatement ignored -> "reference.dialogs.findUsages.variable";
            case RobotKeywordCall ignored -> "reference.dialogs.findUsages.method";
            case RobotUserKeywordStatement ignored -> "reference.dialogs.findUsages.class";
            case RobotParameter ignored -> "reference.dialogs.findUsages.parameter";
            case RobotPositionalArgument ignored -> "reference.dialogs.findUsages.argument";
            default -> null;
        };
    }

    @NotNull
    @Override
    public String getType(@NotNull PsiElement element) {
        return RobotBundle.message("usage.declaration");
    }

    @NotNull
    @Override
    public String getDescriptiveName(@NotNull PsiElement element) {
        return switch (element) {
            case RobotUserKeywordStatement ignored -> RobotBundle.message("usage.descriptive.keyword");
            case RobotVariableStatement ignored -> RobotBundle.message("usage.descriptive.variable");
            case RobotVariableDefinition ignored -> RobotBundle.message("usage.descriptive.variable");
            case RobotFile ignored -> RobotBundle.message("usage.descriptive.import");
            case RobotParameter ignored -> RobotBundle.message("usage.descriptive.parameter");
            case RobotPositionalArgument ignored -> RobotBundle.message("usage.descriptive.argument");
            default -> "";
        };
    }

    @NotNull
    @Override
    public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        return getDescriptiveName(element);
    }
}
