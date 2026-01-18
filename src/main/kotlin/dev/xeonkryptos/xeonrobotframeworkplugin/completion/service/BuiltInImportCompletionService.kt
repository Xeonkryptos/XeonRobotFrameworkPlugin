package dev.xeonkryptos.xeonrobotframeworkplugin.completion.service

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.reference.RobotFileManager
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotNames
import org.apache.commons.text.WordUtils
import java.util.Locale

@Service(Service.Level.PROJECT)
class BuiltInImportCompletionService(private val project: Project) {

    companion object {
        @JvmStatic
        fun getInstance(project: Project): BuiltInImportCompletionService = project.service<BuiltInImportCompletionService>()
    }

    val builtInLibraryCompletions: List<LookupElement> by lazy {
        computeBuiltinLibraryCompletions()
    }

    private fun computeBuiltinLibraryCompletions(): List<LookupElementBuilder> =
        RobotFileManager.getCachedRobotSystemFiles(project).keys.asSequence().map { libraryName: String? ->
            val lookupStrings = arrayOf<String?>(libraryName, WordUtils.capitalize(libraryName), libraryName!!.lowercase(Locale.getDefault()))
            LookupElementBuilder.create(libraryName)
                .withPresentableText(libraryName)
                .withLookupStrings(listOf(*lookupStrings))
                .withCaseSensitivity(true)
                .withIcon(AllIcons.Nodes.Package)
                .withTypeText(RobotNames.BUILTIN_FULL_PYTHON_NAMESPACE)
        }.toList()
}
