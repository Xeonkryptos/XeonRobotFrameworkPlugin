package dev.xeonkryptos.xeonrobotframeworkplugin.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotTag;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotVersionProvider;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotVersionProvider.RobotVersion;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

class StandardTagCompletionProvider extends CompletionProvider<CompletionParameters> {

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        PsiFile originalFile = parameters.getOriginalFile();
        RobotVersionProvider robotVersionProvider = RobotVersionProvider.getInstance(originalFile.getProject());
        RobotVersion robotVersion = robotVersionProvider.getRobotVersion(originalFile);

        RobotLocalSetting localSetting = PsiTreeUtil.getParentOfType(parameters.getPosition(), RobotLocalSetting.class);
        Set<String> definedTags = Set.of();
        if (localSetting != null) {
            definedTags = localSetting.getPositionalArgumentList().stream().map(PsiElement::getText).collect(Collectors.toSet());
        }
        for (RobotTag robotTag : RobotTag.values()) {
            if (!definedTags.contains(robotTag.getTag()) && (robotVersion == null || robotVersion.supports(robotTag.getSinceVersion()))) {
                LookupElement lookupElement = LookupElementBuilder.create(robotTag.getTag());
                result.addElement(lookupElement);
            }
        }
    }
}
