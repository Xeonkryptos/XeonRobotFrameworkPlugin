package dev.xeonkryptos.xeonrobotframeworkplugin.psi.reference

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import kotlin.io.path.Path

@Service(Service.Level.PROJECT)
class ResourceFileImportFinder(private val project: Project) {

    companion object {
        @JvmStatic
        fun getInstance(project: Project): ResourceFileImportFinder = project.service<ResourceFileImportFinder>()
    }

    @JvmOverloads
    fun findFileInFileSystem(resourceFileValue: String, sourceFile: PsiFile, expectedFileType: FileType? = null): PsiFile? {
        val filePath = Path(resourceFileValue)
        if (filePath.isAbsolute) {
            // With an absolute path defined, the file is used directly. No search is performed relative to the location of the importing file.
            return VfsUtil.findFile(filePath, true)?.let { absoluteVirtualFile ->
                val locatedResourceFile = PsiManager.getInstance(project).findFile(absoluteVirtualFile)
                if (expectedFileType == null || locatedResourceFile?.fileType === expectedFileType) locatedResourceFile else null
            }
        }

        // First, try to resolve the resource file path relative to the location of the importing file.
        val virtualSourceFile = sourceFile.virtualFile ?: sourceFile.originalFile.virtualFile ?: return null
        val relevantDir = if (virtualSourceFile.isDirectory) virtualSourceFile else virtualSourceFile.parent
        relevantDir?.findFileByRelativePath(resourceFileValue)?.let {
            return PsiManager.getInstance(project).findFile(it)?.takeIf { psiFile -> expectedFileType == null || psiFile.fileType === expectedFileType }
        }

        // Next, try to resolve the resource file path relative to the content root of the project/module (module search path of python).
        return RobotFileManager.findContentRootForFile(sourceFile)?.let { contentRootForFile ->
            contentRootForFile.findFileByRelativePath(resourceFileValue)?.let { fileByRelativePath ->
                val locatedResourceFile = PsiManager.getInstance(project).findFile(fileByRelativePath)
                if (expectedFileType == null || locatedResourceFile?.fileType === expectedFileType) locatedResourceFile else null
            }
        }
    }
}
