package dev.xeonkryptos.xeonrobotframeworkplugin.inspections

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.execution.TestStateStorage
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.SmartPointerManager
import com.intellij.psi.SmartPsiElementPointer
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testIntegration.TestFailedLineManager
import com.intellij.util.containers.FactoryMap
import dev.xeonkryptos.xeonrobotframeworkplugin.execution.RobotSMTestLocator
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTaskStatement
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseStatement

@Service(Service.Level.PROJECT)
class RobotTestFailedLineManager(private val project: Project) : TestFailedLineManager, FileEditorManagerListener {

    private val testStorage = TestStateStorage.getInstance(project)
    private val cache = FactoryMap.create<VirtualFile, MutableMap<String, TestInfoCache>> { hashMapOf() }

    init {
        project.messageBus.connect().subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, this)
    }

    companion object {
        private const val TEST_FAILED_MAGNITUDE = 6 // see TestStateInfo#Magnitude

        fun getInstance(project: Project): RobotTestFailedLineManager = project.service()
    }

    override fun fileClosed(source: FileEditorManager, file: VirtualFile) {
        cache.remove(file)?.forEach { (s: String, info: TestInfoCache) -> testStorage.writeState(s, info.record) }
    }

    override fun getTestInfo(element: PsiElement?): TestFailedLineManager.TestInfo? {
        if (element == null) return null

        val executableStatement = PsiTreeUtil.getParentOfType(element, true, RobotTestCaseStatement::class.java, RobotTaskStatement::class.java) ?: return null
        val url = computeLocationUrl(executableStatement) ?: return null
        var record = testStorage.getState(url) ?: return null

        val containingFile = element.containingFile ?: return null
        val vFile = containingFile.virtualFile ?: return null
        val document = PsiDocumentManager.getInstance(project).getDocument(containingFile) ?: return null

        val infoInFile = cache[vFile] ?: return null
        var info = infoInFile[url]
        if (info == null || record.date != info.record.date) {
            info = TestInfoCache(record)
            infoInFile[url] = info
        }
        val elementLine = document.getLineNumber(element.textOffset)

        info.pointer?.element?.let {
            if (element == it) return info.apply {
                record = TestStateStorage.Record(
                    record.magnitude,
                    record.date,
                    record.configurationHash,
                    document.getLineNumber(executableStatement.textOffset),
                    record.failedMethod,
                    record.errorMessage,
                    elementLine.toString()
                )
                testStorage.writeState(url, record)
                info.record = record
            }
        }

        val topStacktraceLine = info.record.topStacktraceLine
        if (topStacktraceLine == null || topStacktraceLine.isBlank() || topStacktraceLine.toInt() != elementLine) return null

        info.pointer = SmartPointerManager.createPointer(element)
        return if (info.record.magnitude < TEST_FAILED_MAGNITUDE) null else info
    }

    private fun computeLocationUrl(element: PsiElement): String? {
        val containingFile = element.containingFile ?: return null
        val virtualFile = containingFile.originalFile.virtualFile ?: return null
        val document = PsiDocumentManager.getInstance(project).getDocument(containingFile) ?: return null
        val lineNumber = document.getLineNumber(element.textOffset)
        return RobotSMTestLocator.createLocationUrl(virtualFile.path, lineNumber)
    }

    override fun getRunQuickFix(element: PsiElement?): LocalQuickFix? = null

    override fun getDebugQuickFix(element: PsiElement?, topStackTraceLine: String?): LocalQuickFix? = null

    private class TestInfoCache(
        var record: TestStateStorage.Record, var pointer: SmartPsiElementPointer<PsiElement>? = null
    ) : TestFailedLineManager.TestInfo {
        override fun getMagnitude(): Int = record.magnitude

        override fun getErrorMessage(): String = record.errorMessage

        override fun getTopStackTraceLine(): String = record.topStacktraceLine
    }
}
