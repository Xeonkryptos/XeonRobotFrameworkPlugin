package dev.xeonkryptos.xeonrobotframeworkplugin.ide.actions;

import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.icons.RobotIcons;
import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog.Builder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import org.jetbrains.annotations.NotNull;

public class RobotCreateAction extends CreateFileFromTemplateAction {

    public RobotCreateAction() {
        super(RobotBundle.message("command.name.create.new.file"), RobotBundle.message("command.name.create.new.file.description"), RobotIcons.FILE);
    }

    @Override
    protected void buildDialog(@NotNull Project project, @NotNull PsiDirectory psiDirectory, @NotNull Builder builder) {
        builder.setTitle("New Robot File")
               .addKind("Robot feature file", RobotIcons.FILE, "Robot Feature File.robot")
               .addKind("Robot resource file", RobotIcons.RESOURCE, "Robot Resource File.resource");
    }

    @Override
    protected String getActionName(PsiDirectory psiDirectory, @NotNull String newName, @NotNull String templateName) {
        return "Create New Robot File";
    }
}
