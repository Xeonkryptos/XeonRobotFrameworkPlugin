package dev.xeonkryptos.xeonrobotframeworkplugin.psi.util;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.Service.Level;
import com.intellij.openapi.project.Project;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.config.RobotOptionsProvider;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;

@Service(Level.PROJECT)
@SuppressWarnings("ApplicationServiceAsStaticFinalFieldOrProperty")
public record KeywordUtil(Project project) {

    private static final String SPACE = " ";
    private static final String UNDERSCORE = "_";

    public static KeywordUtil getInstance(Project project) {
        return project.getService(KeywordUtil.class);
    }

    @NotNull
    public String functionToKeyword(@NotNull String function) {
        String keyword = function.replaceAll(UNDERSCORE, SPACE).trim();

        boolean capitalizeKeywords = RobotOptionsProvider.getInstance(project).capitalizeKeywords();
        if (capitalizeKeywords && !keyword.equals(function)) {
            keyword = WordUtils.capitalize(keyword);
        }
        return keyword;
    }

    @NotNull
    public String keywordToFunction(@NotNull String keyword) {
        return keyword.toLowerCase().replaceAll(SPACE, UNDERSCORE).trim();
    }
}
