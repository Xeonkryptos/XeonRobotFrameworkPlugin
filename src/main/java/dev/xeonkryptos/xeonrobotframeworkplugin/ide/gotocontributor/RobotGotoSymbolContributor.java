package dev.xeonkryptos.xeonrobotframeworkplugin.ide.gotocontributor;

import com.intellij.lang.Language;
import com.intellij.navigation.ChooseByNameContributorEx;
import com.intellij.navigation.GotoClassContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.PossiblyDumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.Processor;
import com.intellij.util.indexing.DumbModeAccessType;
import com.intellij.util.indexing.FindSymbolParameters;
import com.intellij.util.indexing.IdFilter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotQualifiedNameOwner;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTaskStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.KeywordDefinitionNameIndex;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.KeywordStatementNameIndex;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.TaskNameIndex;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.TestCaseNameIndex;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.VariableDefinitionNameIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotGotoSymbolContributor implements GotoClassContributor, ChooseByNameContributorEx, PossiblyDumbAware, DumbAware {

    @Override
    public void processNames(final @NotNull Processor<? super String> processor, final @NotNull GlobalSearchScope scope, final @Nullable IdFilter filter) {
        StubIndex stubIndex = StubIndex.getInstance();
        DumbModeAccessType.RAW_INDEX_DATA_ACCEPTABLE.ignoreDumbMode(() -> {
            if (!stubIndex.processAllKeys(KeywordDefinitionNameIndex.KEY, processor, scope, filter)) {
                return;
            }
            if (!stubIndex.processAllKeys(TaskNameIndex.KEY, processor, scope, filter)) {
                return;
            }
            if (!stubIndex.processAllKeys(TestCaseNameIndex.KEY, processor, scope, filter)) {
                return;
            }
            if (!stubIndex.processAllKeys(KeywordStatementNameIndex.KEY, processor, scope, filter)) {
                return;
            }
            stubIndex.processAllKeys(VariableDefinitionNameIndex.KEY, processor, scope, filter);
        });
    }

    @Override
    public void processElementsWithName(@NotNull String name, @NotNull Processor<? super NavigationItem> processor, @NotNull FindSymbolParameters parameters) {
        Project project = parameters.getProject();
        GlobalSearchScope scope = parameters.getSearchScope();
        IdFilter filter = parameters.getIdFilter();
        StubIndex stubIndex = StubIndex.getInstance();
        DumbModeAccessType.RELIABLE_DATA_ONLY.ignoreDumbMode(() -> {
            if (!stubIndex.processElements(KeywordDefinitionNameIndex.KEY, name, project, scope, filter, RobotUserKeywordStatement.class, processor)) {
                return;
            }
            if (!stubIndex.processElements(TaskNameIndex.KEY, name, project, scope, filter, RobotTaskStatement.class, processor)) {
                return;
            }
            if (!stubIndex.processElements(TestCaseNameIndex.KEY, name, project, scope, filter, RobotTestCaseStatement.class, processor)) {
                return;
            }
            if (!stubIndex.processElements(KeywordStatementNameIndex.KEY, name, project, scope, filter, RobotKeywordCall.class, processor)) {
                return;
            }
            stubIndex.processElements(VariableDefinitionNameIndex.KEY, name, project, scope, filter, RobotVariableDefinition.class, processor);
        });
    }

    @Nullable
    @Override
    public String getQualifiedName(@NotNull NavigationItem item) {
        if (item instanceof RobotQualifiedNameOwner qualifiedNameOwner) {
            return qualifiedNameOwner.getQualifiedName();
        }
        return null;
    }

    @Nullable
    @Override
    public String getQualifiedNameSeparator() {
        return ".";
    }

    @Nullable
    @Override
    public Language getElementLanguage() {
        return RobotLanguage.INSTANCE;
    }
}
