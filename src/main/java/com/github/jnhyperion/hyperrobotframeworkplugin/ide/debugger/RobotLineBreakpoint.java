package com.github.jnhyperion.hyperrobotframeworkplugin.ide.debugger;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RobotFeatureFileType;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RobotLanguage;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RobotResourceFileType;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RobotTokenTypes;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordDefinitionId;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordInvokable;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.VariableDefinition;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.breakpoints.SuspendPolicy;
import com.intellij.xdebugger.breakpoints.XLineBreakpointTypeBase;
import com.jetbrains.python.debugger.PyDebugSupportUtils;
import org.jetbrains.annotations.NotNull;

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
public class RobotLineBreakpoint extends XLineBreakpointTypeBase {

    public static final String ID = "robot-line";

    protected RobotLineBreakpoint() {
        super(ID, "Robot Line Breakpoint", new RobotDebuggerEditorsProvider());
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
    protected void lineHasStoppablePsi(@NotNull Project project,
                                       int line,
                                       Document document,
                                       Ref<? super Boolean> stoppable) {
        XDebuggerUtil.getInstance().iterateLine(project, document, line, psiElement -> {
            if (psiElement.getNode() != null &&
                       Set.of(RobotTokenTypes.WHITESPACE, RobotTokenTypes.ERROR).contains(psiElement.getNode().getElementType())) {
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
            if (!(psiElement instanceof PsiWhiteSpace) && PsiTreeUtil.getChildOfType(psiElement.getParent(), KeywordInvokable.class) != null) {
                return true;
            }
            PsiElement nextSibling = psiElement.getNextSibling();
            if (PsiTreeUtil.getChildOfType(nextSibling, KeywordInvokable.class) != null) {
                return true;
            }
            if (nextSibling instanceof VariableDefinition && PsiTreeUtil.findChildOfType(nextSibling, KeywordInvokable.class) != null) {
                return true;
            }
            return !(psiElement instanceof PsiWhiteSpace) && psiElement.getParent() instanceof KeywordDefinitionId;
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
