package dev.xeonkryptos.xeonrobotframeworkplugin.ide.completion;

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
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotVersionProvider;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotVersionProvider.RobotVersion;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

class StandardTagCompletionProvider extends CompletionProvider<CompletionParameters> {

    private enum RobotTag {

        CONTINUE_ON_FAILURE("robot:continue-on-failure", new RobotVersion(0, 0, 0)),
        RECURSIVE_CONTINUE_ON_FAILURE("robot:recursive-continue-on-failure", new RobotVersion(0, 0, 0)),
        STOP_ON_FAILURE("robot:stop-on-failure", new RobotVersion(6, 0, 0)),
        RECURSIVE_STOP_ON_FAILURE("robot:recursive-stop-on-failure", new RobotVersion(6, 0, 0)),
        EXIT_ON_FAILURE("robot:exit-on-failure", new RobotVersion(7, 0, 0)),
        SKIP_ON_FAILURE("robot:skip-on-failure", new RobotVersion(0, 0, 0)),
        SKIP("robot:skip", new RobotVersion(0, 0, 0)),
        EXCLUDE("robot:exclude", new RobotVersion(0, 0, 0)),
        PRIVATE("robot:private", new RobotVersion(6, 0, 0)),
        NO_DRY_RUN("robot:no-dry-run", new RobotVersion(0, 0, 0)),
        EXIT("robot:exit", new RobotVersion(0, 0, 0)),
        FLATTEN("robot:flatten", new RobotVersion(6, 1, 0));

        private final String tag;
        private final RobotVersion sinceVersion;

        RobotTag(String tag, RobotVersion sinceVersion) {
            this.tag = tag;
            this.sinceVersion = sinceVersion;
        }
    }

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
            if (!definedTags.contains(robotTag.tag) && (robotVersion == null || robotVersion.supports(robotTag.sinceVersion))) {
                LookupElement lookupElement = LookupElementBuilder.create(robotTag.tag);
                result.addElement(lookupElement);
            }
        }
    }
}
