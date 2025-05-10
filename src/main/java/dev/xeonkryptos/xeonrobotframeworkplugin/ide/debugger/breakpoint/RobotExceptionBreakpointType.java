package dev.xeonkryptos.xeonrobotframeworkplugin.ide.debugger.breakpoint;

import com.intellij.icons.AllIcons;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XBreakpointType;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;

public class RobotExceptionBreakpointType extends XBreakpointType<XBreakpoint<RobotExceptionBreakpointProperties>, RobotExceptionBreakpointProperties> {

    public RobotExceptionBreakpointType() {
        super("robotcode-exception", "Robot Framework Exception Breakpoint");
    }

    @Nls
    @Override
    public String getDisplayText(XBreakpoint<RobotExceptionBreakpointProperties> breakpoint) {
        return "Any Exception";
    }

    @Override
    public @NotNull Icon getEnabledIcon() {
        return AllIcons.Debugger.Db_exception_breakpoint;
    }

    @Override
    public @NotNull Icon getDisabledIcon() {
        return AllIcons.Debugger.Db_disabled_exception_breakpoint;
    }

    @Override
    public @Nullable RobotExceptionBreakpointProperties createProperties() {
        return new RobotExceptionBreakpointProperties();
    }

    @Nullable
    @NonNls
    @Override
    public String getBreakpointsDialogHelpTopic() {
        return "reference.dialogs.breakpoints";
    }

    @Nullable
    @Override
    public XBreakpoint<RobotExceptionBreakpointProperties> createDefaultBreakpoint(@NotNull XBreakpointCreator<RobotExceptionBreakpointProperties> creator) {
        return creator.createBreakpoint(new RobotExceptionBreakpointProperties());
    }
}
