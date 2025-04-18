package com.github.jnhyperion.hyperrobotframeworkplugin.ide.config;

import com.github.jnhyperion.hyperrobotframeworkplugin.RobotBundle;
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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;

public class RobotConfiguration implements NoScroll, SearchableConfigurable {

    private final JPanel panel;
    private final JCheckBox enableDebug;
    private final JCheckBox allowTransitiveImports;
    private final JCheckBox allowGlobalVariables;
    private final JCheckBox capitalizeKeywords;
    private final JCheckBox inlineVariableSearch;
    private final JCheckBox reformatOnSave;
    private final JCheckBox smartAutoEncloseVariable;
    private final JCheckBox pythonLiveInspection;
    private final JLabel customArgumentsLabel;
    private final JTextField pythonLiveInspectionCustomArgumentsTextField;

    public RobotConfiguration() {
        JPanel mainPanel = new JPanel();
        this.panel = mainPanel;
        mainPanel.setLayout(new GridLayoutManager(2, 2, JBUI.emptyInsets(), -1, -1, false, false));

        JPanel checkBoxPanel = new JPanel();
        checkBoxPanel.setLayout(new GridLayoutManager(9, 1, JBUI.emptyInsets(), -1, -1, false, false));
        mainPanel.add(checkBoxPanel, new GridConstraints(0, 0, 1, 2, 0, 3, 3, 3, null, null, null));

        JCheckBox transitiveImportsCheckBox = new JCheckBox();
        this.allowTransitiveImports = transitiveImportsCheckBox;
        transitiveImportsCheckBox.setText("Allow Transitive Imports (performance concern)");
        checkBoxPanel.add(transitiveImportsCheckBox, new GridConstraints(0, 0, 1, 1, 8, 0, 3, 3, null, null, null));

        JCheckBox globalVariablesCheckBox = new JCheckBox();
        this.allowGlobalVariables = globalVariablesCheckBox;
        globalVariablesCheckBox.setText("Allow Global Variables (performance concern)");
        checkBoxPanel.add(globalVariablesCheckBox, new GridConstraints(1, 0, 1, 1, 8, 0, 3, 3, null, null, null));

        JCheckBox debugCheckBox = new JCheckBox();
        this.enableDebug = debugCheckBox;
        debugCheckBox.setText("Enable Debug Trace");
        checkBoxPanel.add(debugCheckBox, new GridConstraints(2, 0, 1, 1, 8, 0, 3, 3, null, null, null));

        JCheckBox capitalizeKeywordsCheckBox = new JCheckBox();
        this.capitalizeKeywords = capitalizeKeywordsCheckBox;
        capitalizeKeywordsCheckBox.setText("Capitalize Keywords");
        checkBoxPanel.add(capitalizeKeywordsCheckBox, new GridConstraints(3, 0, 1, 1, 8, 0, 3, 0, null, null, null));

        JCheckBox inlineVariableSearchCheckBox = new JCheckBox();
        this.inlineVariableSearch = inlineVariableSearchCheckBox;
        inlineVariableSearchCheckBox.setText("Inline Variable Keyword Search (performance concern)");
        checkBoxPanel.add(inlineVariableSearchCheckBox, new GridConstraints(4, 0, 1, 1, 8, 0, 3, 0, null, null, null));

        JCheckBox reformatOnSaveCheckBox = new JCheckBox();
        this.reformatOnSave = reformatOnSaveCheckBox;
        reformatOnSaveCheckBox.setText("Always Insert 4 whitespace When Typing \"Tab\"");
        checkBoxPanel.add(reformatOnSaveCheckBox, new GridConstraints(5, 0, 1, 1, 8, 0, 3, 0, null, null, null));

        JCheckBox smartAutoEncloseVariableCheckBox = new JCheckBox();
        this.smartAutoEncloseVariable = smartAutoEncloseVariableCheckBox;
        smartAutoEncloseVariableCheckBox.setText("Smart Auto Enclose Variable");
        checkBoxPanel.add(smartAutoEncloseVariableCheckBox, new GridConstraints(6, 0, 1, 1, 8, 0, 3, 0, null, null, null));

        JCheckBox pythonLiveInspectionCheckBox = new JCheckBox();
        this.pythonLiveInspection = pythonLiveInspectionCheckBox;
        pythonLiveInspectionCheckBox.setText("Python Arguments Live Inspection");
        checkBoxPanel.add(pythonLiveInspectionCheckBox, new GridConstraints(7, 0, 1, 1, 8, 0, 3, 0, null, null, null));

        JPanel customArgumentsPanel = new JPanel();
        customArgumentsPanel.setLayout(new FlowLayout());
        checkBoxPanel.add(customArgumentsPanel, new GridConstraints(8, 0, 1, 1, 8, 0, 3, 0, null, null, null));

        customArgumentsLabel = new JLabel();
        customArgumentsLabel.setText("Python Inspection Custom Arguments: ");
        customArgumentsPanel.add(customArgumentsLabel);
        customArgumentsLabel.setToolTipText(RobotBundle.getMessage("options.inspection.custom.arguments.tooltip"));

        JTextField pythonLiveInspectionCustomArgumentsTextField = new JTextField();
        this.pythonLiveInspectionCustomArgumentsTextField = pythonLiveInspectionCustomArgumentsTextField;
        pythonLiveInspectionCustomArgumentsTextField.setText("Custom Arguments");
        customArgumentsPanel.add(pythonLiveInspectionCustomArgumentsTextField);
        pythonLiveInspection.addItemListener(e -> {
            int selectionStateChange = e.getStateChange();
            boolean enabled = selectionStateChange == ItemEvent.SELECTED;
            customArgumentsLabel.setEnabled(enabled);
            pythonLiveInspectionCustomArgumentsTextField.setEnabled(enabled);
        });

        Spacer spacer = new Spacer();
        mainPanel.add(spacer, new GridConstraints(1, 1, 1, 1, 0, 2, 1, 6, null, null, null));
    }

    @Nullable
    private static RobotOptionsProvider getOptionProvider() {
        Project[] projects = ProjectManager.getInstance().getOpenProjects();
        return projects.length > 0 ? RobotOptionsProvider.getInstance(projects[0]) : null;
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
        return provider != null &&
               (provider.isDebug() != this.enableDebug.isSelected() ||
                provider.allowTransitiveImports() != this.allowTransitiveImports.isSelected() ||
                provider.allowGlobalVariables() != this.allowGlobalVariables.isSelected() ||
                provider.capitalizeKeywords() != this.capitalizeKeywords.isSelected() ||
                provider.inlineVariableSearch() != this.inlineVariableSearch.isSelected() ||
                provider.reformatOnSave() != this.reformatOnSave.isSelected() ||
                provider.smartAutoEncloseVariable() != this.smartAutoEncloseVariable.isSelected() ||
                provider.pythonLiveInspection() != this.pythonLiveInspection.isSelected() ||
                provider.pythonLiveInspection() &&
                !provider.getPythonLiveInspectionAdditionalArguments().equals(this.pythonLiveInspectionCustomArgumentsTextField.getText()));
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
            provider.setPythonLiveInspection(this.pythonLiveInspection.isSelected());
            provider.setPythonLiveInspectionAdditionalArguments(this.pythonLiveInspectionCustomArgumentsTextField.getText());
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
            this.pythonLiveInspection.setSelected(provider.pythonLiveInspection());
            this.pythonLiveInspectionCustomArgumentsTextField.setText(provider.getPythonLiveInspectionAdditionalArguments());
            this.pythonLiveInspectionCustomArgumentsTextField.setEnabled(pythonLiveInspection.isSelected());
            this.customArgumentsLabel.setEnabled(pythonLiveInspection.isSelected());
        }
    }
}
