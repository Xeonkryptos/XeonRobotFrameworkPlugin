package dev.xeonkryptos.xeonrobotframeworkplugin.psi.reference.external.file

import com.intellij.json.psi.JsonArray
import com.intellij.json.psi.JsonFile
import com.intellij.json.psi.JsonObject
import com.intellij.json.psi.JsonProperty
import com.intellij.json.psi.impl.JsonRecursiveElementVisitor
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

class RobotJsonFile(private val file: JsonFile) : KeywordFile {

    companion object {
        private val LOCAL_VARIABLE_DEFINITIONS_CACHE_KEY = Key.create<CachedValue<Collection<DefinedVariable>>>("LOCAL_VARIABLE_DEFINITIONS_CACHE_KEY")

        private fun resolveVariables(file: JsonFile): Collection<DefinedVariable> = CachedValuesManager.getCachedValue(file, LOCAL_VARIABLE_DEFINITIONS_CACHE_KEY) {
            val variables = mutableSetOf<DefinedVariable>()
            val visitor = object : JsonRecursiveElementVisitor() {
                private var parentElements = mutableListOf<String>()

                override fun visitProperty(o: JsonProperty) {
                    val name = o.name
                    val value = o.value

                    val variableType = when (value) {
                        is JsonArray -> VariableType.LIST
                        is JsonObject -> VariableType.DICTIONARY
                        else -> VariableType.SCALAR
                    }

                    val layeredNameBuilder = StringBuilder()
                    parentElements.forEach {
                        if (it.startsWith('[')) layeredNameBuilder.deleteAt(layeredNameBuilder.length - 1)
                        layeredNameBuilder.append(it).append('.')
                    }
                    variables.add(VariableDto(o, "$layeredNameBuilder$name", variableType, VariableScope.Global))

                    parentElements.add(name)
                    super.visitProperty(o)
                    parentElements.removeLast()
                }

                override fun visitArray(o: JsonArray) {
                    for ((index, value) in o.valueList.withIndex()) {
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
