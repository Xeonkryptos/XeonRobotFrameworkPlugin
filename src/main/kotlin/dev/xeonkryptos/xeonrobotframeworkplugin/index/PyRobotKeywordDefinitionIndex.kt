package dev.xeonkryptos.xeonrobotframeworkplugin.index

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.indexing.DataIndexer
import com.intellij.util.indexing.DefaultFileTypeSpecificInputFilter
import com.intellij.util.indexing.FileBasedIndex
import com.intellij.util.indexing.FileBasedIndexExtension
import com.intellij.util.indexing.FileContent
import com.intellij.util.indexing.ID
import com.intellij.util.indexing.PsiDependentFileContent
import com.intellij.util.io.DataExternalizer
import com.intellij.util.io.EnumeratorStringDescriptor
import com.intellij.util.io.KeyDescriptor
import com.jetbrains.python.PythonFileType
import com.jetbrains.python.psi.PyFile
import com.jetbrains.python.psi.PyFunction
import com.jetbrains.python.psi.PyParameter
import com.jetbrains.python.psi.stubs.PyFunctionNameIndex
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.KeywordDto
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedKeyword
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotPyUtil
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RobotPyFunctionKeywordLocator
import dev.xeonkryptos.xeonrobotframeworkplugin.util.KeywordNameUtil
import java.io.DataInput
import java.io.DataOutput
import java.io.IOException
import java.util.function.Consumer

@Suppress("UnstableApiUsage")
class PyRobotKeywordDefinitionIndex : FileBasedIndexExtension<String, PyRobotKeywordDefinitionIndex.IntArrayWrapper>() {

    object Util {
        @JvmField
        val INDEX_ID: ID<String, IntArrayWrapper> = ID.create("dev.xeonkryptos.xeonrobotframeworkplugin.PyRobotKeywordIndex")

        @JvmStatic
        fun findKeywordFunctions(
            keywordName: String, project: Project, scope: GlobalSearchScope = GlobalSearchScope.projectScope(project)
        ): Collection<PyFunction> {
            val normalizedKeywordName = KeywordNameUtil.normalizeKeywordName(keywordName)

            val fileBasedIndex = FileBasedIndex.getInstance()
            val virtualFiles: Collection<VirtualFile> = fileBasedIndex.getContainingFiles(INDEX_ID, normalizedKeywordName, scope)
            if (virtualFiles.isEmpty()) return emptyList()

            val psiManager = PsiManager.getInstance(project)
            val result = mutableListOf<PyFunction>()
            for (virtualFile in virtualFiles) {
                val pyFile = psiManager.findFile(virtualFile) as? PyFile ?: continue
                val fileData = fileBasedIndex.getFileData(INDEX_ID, virtualFile, project)
                val offsets = fileData[normalizedKeywordName] ?: continue

                for (offset in offsets.array) {
                    val elementAt = pyFile.findElementAt(offset.coerceAtMost(pyFile.textLength - 1))
                    val pyFunction = PsiTreeUtil.getParentOfType(elementAt, PyFunction::class.java, false) ?: continue
                    val keywordName = RobotPyUtil.findCustomKeywordNameDecoratorExpression(pyFunction).map { it.stringValue }.orElse(pyFunction.name)
                    val normalizedFoundKeywordName = KeywordNameUtil.normalizeKeywordName(keywordName ?: continue)
                    if (normalizedFoundKeywordName == normalizedKeywordName) {
                        result += pyFunction
                    }
                }
            }
            return result.filterNot { pyFunc -> hasDuplicatedPythonFunctionInSameLocation(pyFunc, project, scope) }
        }

        private fun hasDuplicatedPythonFunctionInSameLocation(pyFunc: PyFunction, project: Project, searchScope: GlobalSearchScope): Boolean {
            val qName = pyFunc.qualifiedName ?: return false
            return PyFunctionNameIndex.findByQualifiedName(qName, project, searchScope).size > 1
        }

        @JvmStatic
        fun getKeywordNames(
            project: Project,
            scope: GlobalSearchScope = GlobalSearchScope.projectScope(project),
            libraryName: String? = null
        ): Collection<DefinedKeyword> {
            val fileBasedIndex = FileBasedIndex.getInstance()
            val virtualFiles = mutableSetOf<VirtualFile>()
            fileBasedIndex.processAllKeys(INDEX_ID, { key ->
                val containingFiles = fileBasedIndex.getContainingFiles(INDEX_ID, key, scope)
                virtualFiles.addAll(containingFiles)
                return@processAllKeys true
            }, scope, null)
            if (virtualFiles.isEmpty()) return emptyList()

            val psiManager = PsiManager.getInstance(project)
            val result = mutableSetOf<DefinedKeyword>()
            for (virtualFile in virtualFiles) {
                val pyFile = psiManager.findFile(virtualFile) as? PyFile ?: continue
                val fileData = fileBasedIndex.getFileData(INDEX_ID, virtualFile, project)
                fileData.forEach { (_, offsets) ->
                    for (offset in offsets.array) {
                        val elementAt = pyFile.findElementAt(offset.coerceAtMost(pyFile.textLength - 1))
                        val pyFunction = PsiTreeUtil.getParentOfType(elementAt, PyFunction::class.java, false) ?: continue
                        RobotPyUtil.getPythonKeywordName(pyFunction).ifPresent(Consumer { keywordName ->
                            result += KeywordDto(pyFunction, libraryName, keywordName, listOf<PyParameter?>(*pyFunction.parameterList.parameters))
                        })
                    }
                }
            }
            return result
        }
    }

    companion object {
        private const val VERSION: Int = 2
    }

    override fun getName(): ID<String, IntArrayWrapper> = Util.INDEX_ID

    override fun getVersion(): Int = VERSION

    override fun dependsOnFileContent(): Boolean = true

    override fun getIndexer(): DataIndexer<String, IntArrayWrapper, FileContent> = DataIndexer<String, IntArrayWrapper, FileContent> { inputData ->
        val psiDependentFileContent = inputData as PsiDependentFileContent
        val lighterAST = psiDependentFileContent.lighterAST

        val robotPyFunctionKeywordLocator = RobotPyFunctionKeywordLocator(lighterAST, inputData.contentAsText)
        robotPyFunctionKeywordLocator.visitNode(lighterAST.root)
        val keywordPythonFunctionOccurrences = robotPyFunctionKeywordLocator.keywordPythonFunctionOccurrences
        if (keywordPythonFunctionOccurrences.isEmpty()) return@DataIndexer emptyMap()

        val result = mutableMapOf<String, IntArrayWrapper>()
        for ((normalizedKeywordNames, offsets) in keywordPythonFunctionOccurrences) {
            val arr = IntArray(offsets.size)
            for (i in offsets.indices) arr[i] = offsets[i]
            result[normalizedKeywordNames] = IntArrayWrapper(arr)
        }
        result
    }

    override fun getKeyDescriptor(): KeyDescriptor<String> = EnumeratorStringDescriptor()

    override fun getValueExternalizer(): DataExternalizer<IntArrayWrapper> = IntArrayExternalizer

    override fun getInputFilter(): FileBasedIndex.InputFilter = DefaultFileTypeSpecificInputFilter(PythonFileType.INSTANCE)

    private object IntArrayExternalizer : DataExternalizer<IntArrayWrapper> {
        @Throws(IOException::class)
        override fun save(out: DataOutput, value: IntArrayWrapper) {
            out.writeInt(value.array.size)
            for (v in value.array) out.writeInt(v)
        }

        @Throws(IOException::class)
        override fun read(input: DataInput): IntArrayWrapper {
            val len = input.readInt()
            val arr = IntArray(len)
            for (i in 0 until len) arr[i] = input.readInt()
            return IntArrayWrapper(arr)
        }
    }

    data class IntArrayWrapper(val array: IntArray) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is IntArrayWrapper) return false

            if (!array.contentEquals(other.array)) return false
            return true
        }

        override fun hashCode(): Int {
            return array.contentHashCode()
        }
    }
}
