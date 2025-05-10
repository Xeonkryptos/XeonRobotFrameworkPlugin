package dev.xeonkryptos.xeonrobotframeworkplugin.ide.usage;

import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.Import;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.Parameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.PositionalArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.VariableDefinition;
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
        return switch (element) {
            case VariableDefinition ignored -> "reference.dialogs.findUsages.variable";
            case KeywordStatement ignored -> "reference.dialogs.findUsages.method";
            case KeywordDefinition ignored -> "reference.dialogs.findUsages.class";
            case Parameter ignored -> "reference.dialogs.findUsages.parameter";
            case PositionalArgument ignored -> "reference.dialogs.findUsages.argument";
            default -> null;
        };
    }

    @NotNull
    @Override
    public String getType(@NotNull PsiElement element) {
        return RobotBundle.getMessage("usage.declaration");
    }

    @NotNull
    @Override
    public String getDescriptiveName(@NotNull PsiElement element) {
        return switch (element) {
            case KeywordDefinition ignored -> RobotBundle.getMessage("usage.descriptive.keyword");
            case VariableDefinition ignored -> RobotBundle.getMessage("usage.descriptive.variable");
            case RobotFile ignored -> RobotBundle.getMessage("usage.descriptive.import");
            case Parameter ignored -> RobotBundle.getMessage("usage.descriptive.parameter");
            case PositionalArgument ignored -> RobotBundle.getMessage("usage.descriptive.argument");
            default -> "";
        };
    }

    @NotNull
    @Override
    public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        return getDescriptiveName(element);
    }
}
