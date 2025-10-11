package dev.xeonkryptos.xeonrobotframeworkplugin.ide.actions

import com.intellij.icons.AllIcons
import com.intellij.ide.actions.CreateDirectoryOrPackageHandler
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.ide.actions.NewFileActionWithCategory
import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.ide.fileTemplates.FileTemplateUtil
import com.intellij.ide.util.DirectoryChooserUtil
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFileSystemItem
import com.intellij.psi.SmartPointerManager
import com.intellij.psi.SmartPsiElementPointer
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import org.jetbrains.annotations.NonNls
import java.util.function.Consumer

@Suppress("UnstableApiUsage")
class CreatePackageAction : DumbAwareAction(
    RobotBundle.message("action.NewRobotPackage.text"),
    RobotBundle.message("action.NewRobotPackage.description"),
    AllIcons.Nodes.Package
), NewFileActionWithCategory {

    companion object {
        private val LOG = Logger.getInstance(CreatePackageAction::class.java)

        private const val ORDINARY_PACKAGE_TYPE: @NonNls String = "Package"
        private const val INIT_DOT_ROBOT = "__init__.robot"
    }

    override fun actionPerformed(e: AnActionEvent) {
        val view = e.getData(LangDataKeys.IDE_VIEW) ?: return
        val project = e.getData(CommonDataKeys.PROJECT) ?: return
        val directory = DirectoryChooserUtil.getOrChooseDirectory(view) ?: return

        val builder: CreateFileFromTemplateDialog.Builder = createDialogBuilder(project)

        val pointerManager = SmartPointerManager.getInstance(project)
        val directoryPointer = pointerManager.createSmartPsiElementPointer<PsiDirectory?>(directory)
        val newOrdinaryPackageHandler: CreateDirectoryOrPackageHandler = getNewOrdinaryPackageHandler(project, directory, directoryPointer)

        builder.show<PsiDirectory?>(
            RobotBundle.message("dialog.title.can.t.create.package"), ORDINARY_PACKAGE_TYPE, object : CreateFileFromTemplateDialog.FileCreator<PsiDirectory?> {
                override fun createFile(name: String, templateName: String): PsiDirectory {
                    createNewPackage(name, newOrdinaryPackageHandler) { item: PsiFileSystemItem? ->
                        if (item != null) {
                            view.selectElement(item)
                        }
                    }
                    return directory
                }

                override fun startInWriteAction(): Boolean = false

                override fun getActionName(name: String, templateName: String): String = RobotBundle.message("command.name.create.new.package", name)
            }) {}
    }

    override fun getCategory(): String = "Robot"

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        val enabled: Boolean = isEnabled(e)
        e.presentation.setEnabledAndVisible(enabled)
    }

    private fun getNewOrdinaryPackageHandler(
        project: Project, directory: PsiDirectory, directoryPointer: SmartPsiElementPointer<PsiDirectory?>
    ): CreateDirectoryOrPackageHandler = object : CreateDirectoryOrPackageHandler(project, directory, false, ".") {
        override fun createDirectories(subDirName: String?) {
            super.createDirectories(subDirName)
            val element = createdElement
            val restoredDirectory = directoryPointer.getElement()
            if (element is PsiDirectory && restoredDirectory != null) {
                createInitRobotInHierarchy(element, restoredDirectory)
            }
        }
    }

    private fun createNewPackage(
        name: String, createHandler: CreateDirectoryOrPackageHandler, consumer: Consumer<in PsiFileSystemItem?>
    ) {
        if (createHandler.checkInput(name) && createHandler.canClose(name)) {
            consumer.accept(createHandler.createdElement)
        } else {
            val errorMessage = createHandler.getErrorText(name)
            Messages.showErrorDialog(errorMessage, RobotBundle.message("dialog.title.can.t.create.package"))
        }
    }

    private fun createDialogBuilder(project: Project): CreateFileFromTemplateDialog.Builder = CreateFileFromTemplateDialog.createDialog(project)
        .setTitle(RobotBundle.message("dialog.title.new.robot.package"))
        .addKind(RobotBundle.message("new.package.list.item.ordinary.package"), AllIcons.Nodes.Package, ORDINARY_PACKAGE_TYPE)

    fun createInitRobotInHierarchy(created: PsiDirectory, ancestor: PsiDirectory) {
        var created = created
        if (created == ancestor) {
            createInitRobot(created)
        } else {
            do {
                createInitRobot(created)
                created = created.parent!!
            } while (created != ancestor)
        }
    }

    private fun createInitRobot(directory: PsiDirectory) {
        val fileTemplateManager = FileTemplateManager.getInstance(directory.project)
        val template = fileTemplateManager.getInternalTemplate("Robot Script")
        if (directory.findFile(INIT_DOT_ROBOT) == null) {
            try {
                FileTemplateUtil.createFromTemplate(template, INIT_DOT_ROBOT, fileTemplateManager.defaultProperties, directory)
            } catch (e: Exception) {
                LOG.error(e)
            }
        }
    }

    private fun isEnabled(e: AnActionEvent): Boolean {
        val project = e.getData(CommonDataKeys.PROJECT)
        val ideView = e.getData(LangDataKeys.IDE_VIEW)
        return project != null && ideView != null && ideView.directories.isNotEmpty()
    }
}
