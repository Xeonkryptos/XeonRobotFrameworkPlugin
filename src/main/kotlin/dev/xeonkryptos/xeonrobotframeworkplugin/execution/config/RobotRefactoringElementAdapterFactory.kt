package dev.xeonkryptos.xeonrobotframeworkplugin.execution.config

import com.intellij.openapi.module.Module
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.util.Pair
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.isFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileSystemItem
import com.intellij.refactoring.listeners.RefactoringElementAdapter
import com.intellij.refactoring.listeners.RefactoringElementListener
import dev.xeonkryptos.xeonrobotframeworkplugin.execution.config.RobotRunConfiguration.RobotRunnableUnitExecutionInfo
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotQualifiedNameOwner
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTaskStatement
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseStatement
import java.io.File

class RobotRefactoringElementAdapterFactory(private val configuration: RobotRunConfiguration) {

    companion object {
        private fun findAssociatedRelativePath(module: Module, virtualFile: VirtualFile, separator: Char? = null): String? {
            for (contentRoot in ModuleRootManager.getInstance(module).contentRoots) {
                val relativePath = if (separator != null) VfsUtil.getRelativePath(virtualFile, contentRoot, separator)
                else VfsUtil.getRelativePath(virtualFile, contentRoot)
                return relativePath ?: continue
            }
            return null
        }
    }

    fun getRefactoringElementListener(element: PsiElement): RefactoringElementListener? {
        if (element is RobotTestCaseStatement && !configuration.testCases.isEmpty()) {
            return createRefactoringElementListenerForTestCasesIfNecessary(element, configuration.testCases)
        }
        if (element is RobotTaskStatement && !configuration.tasks.isEmpty()) {
            return createRefactoringElementListenerForTestCasesIfNecessary(element, configuration.tasks)
        }
        if (element is PsiFileSystemItem) {
            return createRefactoringElementAdapterForFilesDirectoriesIfNecessary(element)
        }
        return null
    }

    private fun createRefactoringElementListenerForTestCasesIfNecessary(
        qualifiedNameOwner: RobotQualifiedNameOwner, items: MutableList<RobotRunnableUnitExecutionInfo>
    ): RefactoringElementAdapter? {
        val qualifiedName = qualifiedNameOwner.getQualifiedName()
        val indexExecutionInfoPairs = mutableListOf<Pair<Int, RobotRunnableUnitExecutionInfo>>()
        items.forEachIndexed { i, info ->
            if (qualifiedName == info.fqdn) {
                val indexExecutionInfoPair = Pair(i, info)
                indexExecutionInfoPairs.add(indexExecutionInfoPair)
            }
        }
        if (!indexExecutionInfoPairs.isEmpty()) {
            return object : RefactoringElementAdapter() {
                override fun elementRenamedOrMoved(newElement: PsiElement) {
                    val owner = newElement as RobotQualifiedNameOwner
                    val elementName = owner.name!!
                    val newQualifiedName = owner.getQualifiedName()
                    val newQualifiedLocation = newQualifiedName.take(newQualifiedName.length - elementName.length - 1)
                    for (pair in indexExecutionInfoPairs) {
                        val newExecInfo = RobotRunnableUnitExecutionInfo(newQualifiedLocation, elementName)
                        items[pair.first] = newExecInfo
                    }
                }

                override fun undoElementMovedOrRenamed(newElement: PsiElement, oldQualifiedName: String) {
                    indexExecutionInfoPairs.forEach { items[it.first] = it.second }
                }
            }
        }
        return null
    }

    private fun createRefactoringElementAdapterForFilesDirectoriesIfNecessary(fileSystemItem: PsiFileSystemItem): RefactoringElementAdapter? {
        val virtualFile = fileSystemItem.virtualFile
        val module: Module? = configuration.pythonRunConfiguration.module
        if (virtualFile == null || module == null) return null

        val directories: MutableList<String> = configuration.directories
        val testCases: MutableList<RobotRunnableUnitExecutionInfo> = configuration.testCases
        val tasks: MutableList<RobotRunnableUnitExecutionInfo> = configuration.tasks

        val indexDirectoryPairs = computeFilePairs(module, virtualFile)
        val indexTestCasesExecInfoPairs = computeChangedPsiElementPairs(module, virtualFile, testCases)
        val indexTasksExecInfoPairs = computeChangedPsiElementPairs(module, virtualFile, tasks)
        if (indexDirectoryPairs.isEmpty() && indexTestCasesExecInfoPairs.isEmpty() && indexTasksExecInfoPairs.isEmpty()) return null

        return object : RefactoringElementAdapter() {
            override fun elementRenamedOrMoved(newElement: PsiElement) {
                (newElement as PsiFileSystemItem).virtualFile?.let { virtualFile ->
                    val relativePath = findAssociatedRelativePath(module, virtualFile, File.separatorChar) ?: return
                    indexDirectoryPairs.forEach { pair -> directories[pair.first] = relativePath }

                    var newLocation = relativePath.replace(File.separatorChar, '.')
                    if (virtualFile.isFile && virtualFile.extension != null) {
                        newLocation = newLocation.removeSuffix(".${virtualFile.extension}")
                    }
                    indexTestCasesExecInfoPairs.forEach { pair -> testCases[pair.first] = pair.second.copy().apply { location = newLocation } }
                    indexTasksExecInfoPairs.forEach { pair -> tasks[pair.first] = pair.second.copy().apply { location = newLocation } }
                }
            }

            override fun undoElementMovedOrRenamed(newElement: PsiElement, oldQualifiedName: String) {
                indexDirectoryPairs.forEach { pair -> directories[pair.first] = pair.second }
                indexTestCasesExecInfoPairs.forEach { pair -> testCases[pair.first] = pair.second }
                indexTasksExecInfoPairs.forEach { pair -> tasks[pair.first] = pair.second }
            }
        }
    }

    private fun computeFilePairs(module: Module, virtualFile: VirtualFile): List<Pair<Int, String>> {
        val directories = configuration.directories
        val indexDirectoryPairs = mutableListOf<Pair<Int, String>>()
        val optimizedDirectoryCache = mutableMapOf<String, String>()
        val relativePath = findAssociatedRelativePath(module, virtualFile) ?: return emptyList()

        directories.forEachIndexed { i, directory ->
            val comparableDirectory = optimizedDirectoryCache.computeIfAbsent(directory) { dir -> dir.replace('\\', '/') }
            if (relativePath == comparableDirectory) {
                val indexDirectoryPair = Pair(i, directory)
                indexDirectoryPairs.add(indexDirectoryPair)
            }
        }
        return indexDirectoryPairs
    }

    private fun computeChangedPsiElementPairs(
        module: Module, virtualFile: VirtualFile, executionInfos: List<RobotRunnableUnitExecutionInfo>
    ): List<Pair<Int, RobotRunnableUnitExecutionInfo>> {
        val indexExecInfoPairs = mutableListOf<Pair<Int, RobotRunnableUnitExecutionInfo>>()
        var relativePath = findAssociatedRelativePath(module, virtualFile, '.') ?: return emptyList()

        if (virtualFile.isFile && virtualFile.extension != null) {
            relativePath = relativePath.removeSuffix(".${virtualFile.extension}")
        }

        executionInfos.forEachIndexed { i, execInfo ->
            val location = execInfo.location
            if (location.startsWith(relativePath) && (location.length == relativePath.length || location[relativePath.length] == '.')) {
                val indexExecInfoPair = Pair(i, execInfo)
                indexExecInfoPairs.add(indexExecInfoPair)
            }
        }
        return indexExecInfoPairs
    }
}
