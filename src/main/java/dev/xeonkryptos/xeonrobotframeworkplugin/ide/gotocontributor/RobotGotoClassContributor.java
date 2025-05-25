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
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotQualifiedNameOwner;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.KeywordDefinitionNameIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotGotoClassContributor implements GotoClassContributor, ChooseByNameContributorEx, PossiblyDumbAware, DumbAware {

    @Override
    public void processNames(@NotNull Processor<? super String> processor, @NotNull GlobalSearchScope scope, @Nullable IdFilter filter) {
        DumbModeAccessType.RAW_INDEX_DATA_ACCEPTABLE.ignoreDumbMode(() -> StubIndex.getInstance()
                                                                                   .processAllKeys(KeywordDefinitionNameIndex.KEY, processor, scope, filter));
    }

    @Override
    public void processElementsWithName(@NotNull String name, @NotNull Processor<? super NavigationItem> processor, @NotNull FindSymbolParameters parameters) {
        Project project = parameters.getProject();
        GlobalSearchScope scope = parameters.getSearchScope();
        IdFilter filter = parameters.getIdFilter();
        DumbModeAccessType.RELIABLE_DATA_ONLY.ignoreDumbMode(() -> StubIndex.getInstance()
                                                                            .processElements(KeywordDefinitionNameIndex.KEY,
                                                                                             name,
                                                                                             project,
                                                                                             scope,
                                                                                             filter,
                                                                                             KeywordDefinition.class,
                                                                                             processor));
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
