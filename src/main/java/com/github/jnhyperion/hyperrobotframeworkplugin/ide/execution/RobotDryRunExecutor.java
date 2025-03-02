package com.github.jnhyperion.hyperrobotframeworkplugin.ide.execution;

import com.github.jnhyperion.hyperrobotframeworkplugin.RobotBundle;
import com.github.jnhyperion.hyperrobotframeworkplugin.ide.icons.RobotIcons;
import com.intellij.execution.Executor;
import com.intellij.openapi.util.NlsActions;
import com.intellij.openapi.util.text.TextWithMnemonic;
import com.intellij.openapi.wm.ToolWindowId;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;

public class RobotDryRunExecutor extends Executor {

    public static final @NonNls String EXECUTOR_ID = "RobotDryrun";

    @NotNull
    @Override
    public String getToolWindowId() {
        return ToolWindowId.RUN;
    }

    @NotNull
    @Override
    public Icon getToolWindowIcon() {
        assert RobotIcons.FILE != null;
        return RobotIcons.FILE;
    }

    @NotNull
    @Override
    public Icon getIcon() {
        assert RobotIcons.FILE != null;
        return RobotIcons.FILE;
    }

    @Override
    public Icon getDisabledIcon() {
        return null;
    }

    @Override
    @NlsActions.ActionDescription
    public String getDescription() {
        return RobotBundle.getMessage("run.selected.configuration.with.dryrun.enabled");
    }

    @NotNull
    @Override
    @NlsActions.ActionText
    public String getActionName() {
        return RobotBundle.getMessage("action.name.dryrun");
    }

    @NotNull
    @NonNls
    @Override
    public String getId() {
        return EXECUTOR_ID;
    }

    @Override
    @NotNull
    public String getStartActionText() {
        return RobotBundle.getMessage("run.with.dryrun");
    }

    @NotNull
    @Override
    public String getStartActionText(@NotNull String configurationName) {
        if (configurationName.isEmpty()) {
            return getStartActionText();
        }
        String configName = shortenNameIfNeeded(configurationName);
        return TextWithMnemonic.parse(RobotBundle.getMessage("run.with.dryrun.mnemonic")).replaceFirst("%s", configName).toString();
    }

    @NonNls
    @Override
    public String getContextActionId() {
        return "RunDryRun";
    }

    @NonNls
    @Override
    public String getHelpId() {
        return "";
    }

    @Override
    public boolean isSupportedOnTarget() {
        return EXECUTOR_ID.equalsIgnoreCase(getId());
    }
}
