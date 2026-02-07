package dev.xeonkryptos.xeonrobotframeworkplugin.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.util.ProcessingContext;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotResourceFileType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotCommentsSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordsSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSettingsSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTasksSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCasesSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariablesSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RecursiveRobotVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

class SectionCompletionProvider extends CompletionProvider<CompletionParameters> {

    private final Set<String> excludedSections = Set.of("*** Test Cases ***", "*** Tasks ***");

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        Collection<LookupElement> lookupElements = new LinkedList<>();
        boolean isResource = parameters.getOriginalFile().getFileType() == RobotResourceFileType.getInstance();
        Set<String> definedSections = new HashSet<>(6);
        parameters.getOriginalFile().acceptChildren(new RecursiveRobotVisitor() {

            @Override
            public void visitCommentsSection(@NotNull RobotCommentsSection o) {
                definedSections.add("*** Comments ***");
            }

            @Override
            public void visitKeywordsSection(@NotNull RobotKeywordsSection o) {
                definedSections.add("*** Keywords ***");
            }

            @Override
            public void visitSettingsSection(@NotNull RobotSettingsSection o) {
                definedSections.add("*** Settings ***");
            }

            @Override
            public void visitTasksSection(@NotNull RobotTasksSection o) {
                definedSections.add("*** Tasks ***");
            }

            @Override
            public void visitTestCasesSection(@NotNull RobotTestCasesSection o) {
                definedSections.add("*** Test Cases ***");
            }

            @Override
            public void visitVariablesSection(@NotNull RobotVariablesSection o) {
                definedSections.add("*** Variables ***");
            }

        });
        for (LookupElement element : CompletionProviderUtils.computeAdditionalSyntaxLookups(RobotTypes.SECTION)) {
            String lookupString = element.getLookupString();
            if (!definedSections.contains(lookupString) && (!isResource || !excludedSections.contains(lookupString))) {
                lookupElements.add(element);
            }
        }
        result.addAllElements(lookupElements);
    }
}
