package dev.xeonkryptos.xeonrobotframeworkplugin.util

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import dev.xeonkryptos.xeonrobotframeworkplugin.config.RobotOptionsProvider
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFile
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotRoot
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSettingsSection
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTemplateStatementsGlobalSetting
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseStatement
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor
import org.apache.commons.text.WordUtils

@Service(Service.Level.PROJECT)
class KeywordUtil(private val project: Project) {

    companion object {
        private val keywordNormalizerRegex = Regex("[_\\s]+")

        const val SPACE = " "
        const val UNDERSCORE = "_"

        @JvmStatic
        fun getInstance(project: Project): KeywordUtil = project.service<KeywordUtil>()

        @JvmStatic
        fun normalizeKeywordName(name: String): String = name.lowercase().replace(keywordNormalizerRegex, "")

        @JvmStatic
        fun findTemplateKeywordCall(element: PsiElement): RobotKeywordCall? {
            val robotFile = element.containingFile
            if (robotFile !is RobotFile) return null

            val testCaseStatement = PsiTreeUtil.getParentOfType(element, RobotTestCaseStatement::class.java)
            if (testCaseStatement != null) {
                for (localSetting in testCaseStatement.getLocalSettings()) {
                    if (RobotNames.TEMPLATE_LOCAL_SETTING_NAME.equals(localSetting.getSettingName(), ignoreCase = true)) {
                        return PsiTreeUtil.getChildOfType(localSetting, RobotKeywordCall::class.java)
                    }
                }
            }
            val testTemplateFinder = TestTemplateFinder()
            robotFile.acceptChildren(testTemplateFinder)
            return testTemplateFinder.templateKeywordCall
        }
    }

    fun functionToKeyword(function: String): String {
        var keyword =
            function.replace(UNDERSCORE.toRegex(), SPACE).trim { it <= ' ' }

        val capitalizeKeywords = RobotOptionsProvider.getInstance(project).capitalizeKeywords()
        if (capitalizeKeywords && keyword != function) {
            keyword = WordUtils.capitalize(keyword)
        }
        return keyword
    }

    private class TestTemplateFinder : RobotVisitor() {
        var templateKeywordCall: RobotKeywordCall? = null

        override fun visitRoot(o: RobotRoot) {
            super.visitRoot(o)
            o.acceptChildren(this)
        }

        override fun visitSettingsSection(o: RobotSettingsSection) {
            super.visitSettingsSection(o)
            o.acceptChildren(this)
        }

        override fun visitTemplateStatementsGlobalSetting(o: RobotTemplateStatementsGlobalSetting) {
            super.visitTemplateStatementsGlobalSetting(o)
            templateKeywordCall = o.getKeywordCall()
        }
    }
}
