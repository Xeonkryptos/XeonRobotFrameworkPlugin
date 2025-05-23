package dev.xeonkryptos.xeonrobotframeworkplugin.ide.debugger.breakpoint;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.breakpoints.SuspendPolicy;
import com.intellij.xdebugger.breakpoints.XLineBreakpointType;
import com.jetbrains.python.debugger.PyDebugSupportUtils;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotFeatureFileType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotResourceFileType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTokenTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.Heading;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordDefinitionId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.VariableDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

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

    @SuppressWarnings("SuspiciousMethodCalls")
    protected void lineHasStoppablePsi(@NotNull Project project, int line, Document document, Ref<? super Boolean> stoppable) {
        XDebuggerUtil.getInstance().iterateLine(project, document, line, psiElement -> {
            if (psiElement.getNode() != null && Set.of(RobotTokenTypes.WHITESPACE, RobotTokenTypes.ERROR).contains(psiElement.getNode().getElementType())) {
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
            // TODO: Not optimal solution. Better use visitor pattern here, but the visitor needs to be implemented first
            return context instanceof KeywordStatement || context instanceof VariableDefinition || context instanceof KeywordDefinition
                   || context instanceof KeywordDefinitionId || context instanceof Heading heading && heading.isSettings();
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
