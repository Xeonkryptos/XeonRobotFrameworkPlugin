package dev.xeonkryptos.xeonrobotframeworkplugin.execution.debugger.breakpoint;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.TokenType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.breakpoints.SuspendPolicy;
import com.intellij.xdebugger.breakpoints.XLineBreakpointType;
import com.jetbrains.python.debugger.PyDebugSupportUtils;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotFeatureFileType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotResourceFileType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSettingsSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableStatement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Class that configures a breakpoint for robot framework.
 * <p>
 * It must also be registered in the plugin.xml.
 * <p>
 * Apparently, what Intellij does when a breakpoint is added is collect
 * all the breakpoint classes registered in the plugin, instantiate all
 * of them for the breakpoint and then check if `canPutAt` is valid
 * (and if it is the breakpoint is created).
 * <p>
 * Later on the `XDebugProcess` implementation must provide subclasses of
 * `XBreakpointHandler` in `getBreakpointHandlers`, which is responsible
 * to actually handle a registered breakpoint and send it to the debugger
 * backend.
 */
public class RobotLineBreakpointType extends XLineBreakpointType<RobotLineBreakpointProperties> {

    protected RobotLineBreakpointType() {
        super("robot-line", "Robot Line Breakpoint");
    }

    @Override
    public @Nullable RobotLineBreakpointProperties createBreakpointProperties(@NotNull VirtualFile file, int line) {
        return new RobotLineBreakpointProperties();
    }

    @Override
    public boolean canPutAt(@NotNull VirtualFile file, int line, @NotNull Project project) {
        Ref<Boolean> stoppable = Ref.create(false);
        Document document = FileDocumentManager.getInstance().getDocument(file);
        lineHasStoppablePsi(project, line, document, new Ref<>());
        if (document != null && (file.getFileType() instanceof RobotFeatureFileType || file.getFileType() instanceof RobotResourceFileType)) {
            this.lineHasStoppablePsi(project, line, document, stoppable);
        }
        return stoppable.get();
    }

    protected void lineHasStoppablePsi(@NotNull Project project, int line, Document document, Ref<? super Boolean> stoppable) {
        XDebuggerUtil.getInstance().iterateLine(project, document, line, psiElement -> {
            if (psiElement.getNode() != null && TokenType.WHITE_SPACE == psiElement.getNode().getElementType()) {
                return true;
            } else {
                if (isPsiElementStoppable(psiElement)) {
                    stoppable.set(true);
                }
                return false;
            }
        });
        if (PyDebugSupportUtils.isContinuationLine(document, line - 1)) {
            stoppable.set(false);
        }
    }

    protected boolean isPsiElementStoppable(PsiElement psiElement) {
        if (psiElement.getLanguage() == RobotLanguage.INSTANCE) {
            PsiElement context = psiElement.getContext();
            if (context == null) {
                return false;
            }
            return PsiTreeUtil.getParentOfType(context, false, RobotKeywordCall.class, RobotVariableStatement.class) != null
                   || context instanceof RobotSettingsSection;
        }
        return false;
    }

    @Override
    public boolean isSuspendThreadSupported() {
        return true;
    }

    @Override
    public SuspendPolicy getDefaultSuspendPolicy() {
        return SuspendPolicy.THREAD;
    }

    @Override
    public String getBreakpointsDialogHelpTopic() {
        return "reference.dialogs.breakpoints";
    }
}
