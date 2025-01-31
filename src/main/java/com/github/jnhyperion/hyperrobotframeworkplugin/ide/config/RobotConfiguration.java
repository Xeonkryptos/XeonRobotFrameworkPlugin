package com.github.jnhyperion.hyperrobotframeworkplugin.ide.config;

import com.intellij.openapi.options.Configurable.NoScroll;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class RobotConfiguration implements NoScroll, SearchableConfigurable {

    private final JPanel panel;
    private final JCheckBox enableDebug;
    private final JCheckBox allowTransitiveImports;
    private final JCheckBox allowGlobalVariables;
    private final JCheckBox capitalizeKeywords;
    private final JCheckBox inlineVariableSearch;
    private final JCheckBox reformatOnSave;
    private final JCheckBox smartAutoEncloseVariable;

    public RobotConfiguration() {
        JPanel var2 = new JPanel();
        this.panel = var2;
        var2.setLayout(new GridLayoutManager(2, 2, JBUI.emptyInsets(), -1, -1, false, false));
        JPanel var3;
        (var3 = new JPanel()).setLayout(new GridLayoutManager(7, 1, JBUI.emptyInsets(), -1, -1, false, false));
        var2.add(var3, new GridConstraints(0, 0, 1, 2, 0, 3, 3, 3, null, null, null));
        JCheckBox var4 = new JCheckBox();
        this.allowTransitiveImports = var4;
        var4.setText("Allow Transitive Imports (performance concern)");
        var3.add(var4, new GridConstraints(0, 0, 1, 1, 8, 0, 3, 3, null, null, null));
        var4 = new JCheckBox();
        this.allowGlobalVariables = var4;
        var4.setText("Allow Global Variables (performance concern)");
        var3.add(var4, new GridConstraints(1, 0, 1, 1, 8, 0, 3, 3, null, null, null));
        var4 = new JCheckBox();
        this.enableDebug = var4;
        var4.setText("Enable Debug Trace");
        var3.add(var4, new GridConstraints(2, 0, 1, 1, 8, 0, 3, 3, null, null, null));
        var4 = new JCheckBox();
        this.capitalizeKeywords = var4;
        var4.setText("Capitalize Keywords");
        var3.add(var4, new GridConstraints(3, 0, 1, 1, 8, 0, 3, 0, null, null, null));
        var4 = new JCheckBox();
        this.inlineVariableSearch = var4;
        var4.setText("Inline Variable Keyword Search (performance concern)");
        var3.add(var4, new GridConstraints(4, 0, 1, 1, 8, 0, 3, 0, null, null, null));
        var4 = new JCheckBox();
        this.reformatOnSave = var4;
        var4.setText("Always Insert 4 whitespace When Typing \"Tab\"");
        var3.add(var4, new GridConstraints(5, 0, 1, 1, 8, 0, 3, 0, null, null, null));
        var4 = new JCheckBox();
        this.smartAutoEncloseVariable = var4;
        var4.setText("Smart Auto Enclose Variable");
        var3.add(var4, new GridConstraints(6, 0, 1, 1, 8, 0, 3, 0, null, null, null));
        Spacer var1 = new Spacer();
        var2.add(var1, new GridConstraints(1, 1, 1, 1, 0, 2, 1, 6, null, null, null));
    }

    @Nullable
    private static RobotOptionsProvider getOptionProvider() {
        Project[] projects = ProjectManager.getInstance().getOpenProjects();
        if (projects.length > 0) {
            return RobotOptionsProvider.getInstance(projects[0]);
        } else {
            return null;
        }
    }

    @NotNull
    @Override
    public String getId() {
        return this.getHelpTopic();
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Robot Options";
    }

    @NotNull
    @Override
    public String getHelpTopic() {
        return "reference.idesettings.robot";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return this.panel;
    }

    @Override
    public boolean isModified() {
        RobotOptionsProvider provider = getOptionProvider();
        return provider != null && (provider.isDebug() != this.enableDebug.isSelected()
                                    || provider.allowTransitiveImports() != this.allowTransitiveImports.isSelected()
                                    || provider.allowGlobalVariables() != this.allowGlobalVariables.isSelected()
                                    || provider.capitalizeKeywords() != this.capitalizeKeywords.isSelected()
                                    || provider.inlineVariableSearch() != this.inlineVariableSearch.isSelected()
                                    || provider.reformatOnSave() != this.reformatOnSave.isSelected()
                                    || provider.smartAutoEncloseVariable() != this.smartAutoEncloseVariable.isSelected());
    }

    @Override
    public void apply() {
        RobotOptionsProvider provider = getOptionProvider();
        if (provider != null) {
            provider.setDebug(this.enableDebug.isSelected());
            provider.setAllowTransitiveImports(this.allowTransitiveImports.isSelected());
            provider.setGlobalVariables(this.allowGlobalVariables.isSelected());
            provider.setCapitalizeKeywords(this.capitalizeKeywords.isSelected());
            provider.setInlineVariableSearch(this.inlineVariableSearch.isSelected());
            provider.setReformatOnSave(this.reformatOnSave.isSelected());
            provider.setSmartAutoEncloseVariable(this.smartAutoEncloseVariable.isSelected());
        }
    }

    @Override
    public void reset() {
        RobotOptionsProvider provider = getOptionProvider();
        if (provider != null) {
            this.enableDebug.setSelected(provider.isDebug());
            this.allowTransitiveImports.setSelected(provider.allowTransitiveImports());
            this.allowGlobalVariables.setSelected(provider.allowGlobalVariables());
            this.capitalizeKeywords.setSelected(provider.capitalizeKeywords());
            this.inlineVariableSearch.setSelected(provider.inlineVariableSearch());
            this.reformatOnSave.setSelected(provider.reformatOnSave());
            this.smartAutoEncloseVariable.setSelected(provider.smartAutoEncloseVariable());
        }
    }
}
