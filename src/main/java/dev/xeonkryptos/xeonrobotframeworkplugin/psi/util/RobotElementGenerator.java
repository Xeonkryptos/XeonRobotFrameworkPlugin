package dev.xeonkryptos.xeonrobotframeworkplugin.psi.util;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.Service.Level;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.impl.PsiFileFactoryImpl;
import com.intellij.testFramework.LightVirtualFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotFeatureFileType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage;

@Service(Level.PROJECT)
public final class RobotElementGenerator {

    private final Project project;

    private RobotElementGenerator(Project project) {
        this.project = project;
    }

    public static RobotElementGenerator getInstance(Project project) {
        return project.getService(RobotElementGenerator.class);
    }

    public PsiFile createDummyPsiFile(String text) {
        PsiFileFactory factory = PsiFileFactory.getInstance(project);

        LightVirtualFile virtualFile = new LightVirtualFile("dummy.robot", RobotFeatureFileType.getInstance(), text);
        return ((PsiFileFactoryImpl) factory).trySetupPsiForFile(virtualFile, RobotLanguage.INSTANCE, false, true);
    }
}
