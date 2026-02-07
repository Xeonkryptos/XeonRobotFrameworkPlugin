package dev.xeonkryptos.xeonrobotframeworkplugin.execution;

import com.intellij.execution.lineMarker.ExecutorAction;
import com.intellij.execution.lineMarker.RunLineMarkerContributor;
import com.intellij.icons.AllIcons.RunConfigurations.TestState;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilCore;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotResourceFileType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotQualifiedNameOwner;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTaskStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseStatement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import java.util.Collection;
import java.util.Set;

public class RobotRunLineMarkerProvider extends RunLineMarkerContributor implements DumbAware {

    private static final Set<IElementType> EXECUTABLE_SECTION_TYPES = Set.of(RobotTypes.TEST_CASES_HEADER, RobotTypes.TASKS_HEADER);
    private static final Set<IElementType> EXECUTABLE_ELEMENT_TYPES = Set.of(RobotTypes.TEST_CASE_NAME, RobotTypes.TASK_NAME);

    @Nullable
    @Override
    public Info getInfo(@NotNull PsiElement element) {
        IElementType type = PsiUtilCore.getElementType(element);
        PsiFile containingFile = element.getContainingFile();
        FileType fileType = containingFile.getFileType();
        if (fileType != RobotResourceFileType.getInstance()) {
            Project project = element.getProject();
            if (EXECUTABLE_ELEMENT_TYPES.contains(type)) {
                return constructExecutableInfoIcon(element, project, containingFile, false);
            } else if (EXECUTABLE_SECTION_TYPES.contains(type)) {
                Collection<? extends RobotQualifiedNameOwner> children = PsiTreeUtil.findChildrenOfAnyType(element, true, RobotTestCaseStatement.class, RobotTaskStatement.class);
                if (!children.isEmpty()) {
                    return constructExecutableInfoIcon(element, project, containingFile, true);
                }
            }
        }
        return null;
    }

    private static @NotNull Info constructExecutableInfoIcon(@NotNull PsiElement element, Project project, PsiFile containingFile, boolean isClass) {
        AnAction[] actions = ExecutorAction.getActions();
        Document document = PsiDocumentManager.getInstance(project).getDocument(containingFile);
        Icon icon = TestState.Green2;
        if (document != null) {
            int textOffset = element.getTextOffset();
            int lineNumber = document.getLineNumber(textOffset);
            String path = containingFile.getOriginalFile().getVirtualFile().getPath();
            String url = RobotSMTestLocator.createLocationUrl(path, lineNumber);
            icon = getTestStateIcon(url, project, isClass);
        }
        return new Info(icon, actions);
    }
}
