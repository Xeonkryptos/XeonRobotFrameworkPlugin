package dev.xeonkryptos.xeonrobotframeworkplugin.psi.util;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.Service.Level;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.config.RobotOptionsProvider;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotRoot;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSettingsSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTemplateStatementsGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
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

    public RobotKeywordCall findTemplateKeywordCall(@NotNull PsiElement element) {
        if (!(element.getContainingFile() instanceof RobotFile robotFile)) {
            return null;
        }
        RobotTestCaseStatement testCaseStatement = PsiTreeUtil.getParentOfType(element, RobotTestCaseStatement.class);
        if (testCaseStatement != null) {
            for (RobotLocalSetting localSetting : testCaseStatement.getLocalSettingList()) {
                if ("[Template]".equalsIgnoreCase(localSetting.getSettingName())) {
                    return PsiTreeUtil.getChildOfType(localSetting, RobotKeywordCall.class);
                }
            }
        }
        TestTemplateFinder testTemplateFinder = new TestTemplateFinder();
        robotFile.acceptChildren(testTemplateFinder);
        return testTemplateFinder.templateKeywordCall;
    }

    private static class TestTemplateFinder extends RobotVisitor {

        private RobotKeywordCall templateKeywordCall;

        @Override
        public void visitRoot(@NotNull RobotRoot o) {
            super.visitRoot(o);
            o.acceptChildren(this);
        }

        @Override
        public void visitSettingsSection(@NotNull RobotSettingsSection o) {
            super.visitSettingsSection(o);
            o.acceptChildren(this);
        }

        @Override
        public void visitTemplateStatementsGlobalSetting(@NotNull RobotTemplateStatementsGlobalSetting o) {
            super.visitTemplateStatementsGlobalSetting(o);
            templateKeywordCall = o.getKeywordCall();
        }
    }
}
