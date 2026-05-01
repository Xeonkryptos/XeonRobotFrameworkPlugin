package dev.xeonkryptos.xeonrobotframeworkplugin.psi.reference.external.file

import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ImportType
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.VariableDto
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.VariableType
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedVariable
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordFile
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.VariableScope
import org.jetbrains.yaml.psi.YAMLFile
import org.jetbrains.yaml.psi.YAMLKeyValue
import org.jetbrains.yaml.psi.YAMLMapping
import org.jetbrains.yaml.psi.YAMLSequence
import org.jetbrains.yaml.psi.YamlRecursivePsiElementVisitor

class RobotYamlFile(private val file: YAMLFile) : KeywordFile {

    companion object {
        private val LOCAL_VARIABLE_DEFINITIONS_CACHE_KEY = Key.create<CachedValue<Collection<DefinedVariable>>>("LOCAL_VARIABLE_DEFINITIONS_CACHE_KEY")

        private fun resolveVariables(file: YAMLFile): Collection<DefinedVariable> = CachedValuesManager.getCachedValue(file, LOCAL_VARIABLE_DEFINITIONS_CACHE_KEY) {
            val variables = mutableSetOf<DefinedVariable>()
            val visitor = object : YamlRecursivePsiElementVisitor() {
                private var parentElements = mutableListOf<String>()

                override fun visitKeyValue(keyValue: YAMLKeyValue) {
                    val name = keyValue.keyText
                    val value = keyValue.value

                    val variableType = when (value) {
                        is YAMLSequence -> VariableType.LIST
                        is YAMLMapping -> VariableType.DICTIONARY
                        else -> VariableType.SCALAR
                    }

                    val layeredNameBuilder = StringBuilder()
                    parentElements.forEach {
                        if (it.startsWith('[')) layeredNameBuilder.deleteAt(layeredNameBuilder.length - 1)
                        layeredNameBuilder.append(it).append('.')
                    }
                    variables.add(VariableDto(keyValue, "$layeredNameBuilder$name", variableType, VariableScope.Global))

                    parentElements.add(name)
                    super.visitKeyValue(keyValue)
                    parentElements.removeLast()
                }

                override fun visitSequence(sequence: YAMLSequence) {
                    for ((index, value) in sequence.items.withIndex()) {
                        parentElements.add("[${index}]")
                        value.accept(this)
                        parentElements.removeLast()
                    }
                }
            }
            file.acceptChildren(visitor)
            return@getCachedValue CachedValueProvider.Result.createSingleDependency(variables, file)
        }
    }

    override fun findDefinedVariable(variableName: String): Collection<DefinedVariable> = locallyDefinedVariables.filter { it.matches(variableName) }

    override fun getLocallyDefinedVariables(): Collection<DefinedVariable> = resolveVariables(file)

    override fun getImportType(): ImportType = ImportType.VARIABLES

    override fun getImportedFiles(includeTransitive: Boolean, vararg importTypes: ImportType?): Collection<KeywordFile?> = setOf()

    override fun getVirtualFiles(includeTransitive: Boolean): Collection<VirtualFile> = setOf()

    override fun getVirtualFile(): VirtualFile? = file.virtualFile

    override fun getPsiFile(): PsiFile = file

    override fun getLibraryName(): String? = null
}
