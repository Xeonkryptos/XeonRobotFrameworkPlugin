package dev.xeonkryptos.xeonrobotframeworkplugin.ide.config;

import com.intellij.openapi.options.Configurable.NoScroll;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.border.CustomLineBorder;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextField;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.intellij.util.ui.JBUI;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.util.Arrays;
import java.util.List;

public class RobotConfiguration implements NoScroll, SearchableConfigurable {

    private final JBPanel<?> panel;
    private final JBCheckBox allowTransitiveImports;
    private final JBCheckBox capitalizeKeywords;
    private final JBCheckBox smartAutoEncloseVariable;
    private final JBCheckBox multilineIndentation;
    private final JBTextField parameterNameCollationRulesTextField;
    private final JBCheckBox pythonLiveInspection;
    private final JBTextField pythonLiveInspectionCustomArgumentsTextField;
    private final JBTextField pythonLiveInspectionForDecoratorsTextField;

    public RobotConfiguration() {
        JBPanel<?> mainPanel = new JBPanel<>();
        this.panel = mainPanel;
        mainPanel.setLayout(new GridLayoutManager(3, 3, JBUI.emptyInsets(), -1, -1, false, false));

        int mainPanelRow = 0;
        JBPanel<?> checkBoxPanel = new JBPanel<>();
        checkBoxPanel.setLayout(new GridLayoutManager(8, 1, JBUI.emptyInsets(), -1, -1, false, false));
        mainPanel.add(checkBoxPanel, new GridConstraints(mainPanelRow++, 0, 1, 2, 0, 3, 3, 3, null, null, null));

        int checkBoxPanelRow = 0;
        JBCheckBox transitiveImportsCheckBox = new JBCheckBox();
        this.allowTransitiveImports = transitiveImportsCheckBox;
        transitiveImportsCheckBox.setText("Allow Transitive Imports");
        checkBoxPanel.add(transitiveImportsCheckBox, new GridConstraints(checkBoxPanelRow++, 0, 1, 1, 8, 0, 3, 3, null, null, null));

        JBCheckBox capitalizeKeywordsCheckBox = new JBCheckBox();
        this.capitalizeKeywords = capitalizeKeywordsCheckBox;
        capitalizeKeywordsCheckBox.setText("Capitalize Keywords");
        checkBoxPanel.add(capitalizeKeywordsCheckBox, new GridConstraints(checkBoxPanelRow++, 0, 1, 1, 8, 0, 3, 0, null, null, null));

        JBCheckBox smartAutoEncloseVariableCheckBox = new JBCheckBox();
        this.smartAutoEncloseVariable = smartAutoEncloseVariableCheckBox;
        smartAutoEncloseVariableCheckBox.setText("Smart Auto Enclose Variable");
        checkBoxPanel.add(smartAutoEncloseVariableCheckBox, new GridConstraints(checkBoxPanelRow++, 0, 1, 1, 8, 0, 3, 0, null, null, null));

        JBCheckBox multilineIndentationCheckBox = new JBCheckBox();
        this.multilineIndentation = multilineIndentationCheckBox;
        multilineIndentationCheckBox.setText("Smart Multiline Indentation");
        checkBoxPanel.add(multilineIndentationCheckBox, new GridConstraints(checkBoxPanelRow++, 0, 1, 1, 8, 0, 3, 0, null, null, null));

        JBPanel<?> parameterNameCollationRulesForDecoratorsPanel = new JBPanel<>();
        parameterNameCollationRulesForDecoratorsPanel.setLayout(new BoxLayout(parameterNameCollationRulesForDecoratorsPanel, BoxLayout.LINE_AXIS));

        JBLabel parameterNameCollationRulesLabel = new JBLabel();
        parameterNameCollationRulesLabel.setText("Parameter name collation rules: ");
        parameterNameCollationRulesForDecoratorsPanel.add(parameterNameCollationRulesLabel);

        JBTextField parameterNameCollationRulesTextField = new JBTextField();
        final Border originalParameterNameCollationRulesBorder = parameterNameCollationRulesTextField.getBorder();
        parameterNameCollationRulesTextField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                RobotOptionsProvider provider = getOptionProvider();
                if (provider != null) {
                    String collationRules = parameterNameCollationRulesTextField.getText();
                    String errorReason = provider.canParseParameterNameCollationRules(collationRules);
                    if (errorReason == null) {
                        parameterNameCollationRulesTextField.setBorder(originalParameterNameCollationRulesBorder);
                    } else {
                        Insets borderInsets = originalParameterNameCollationRulesBorder.getBorderInsets(parameterNameCollationRulesTextField);
                        parameterNameCollationRulesTextField.setBorder(new CustomLineBorder(JBUI.CurrentTheme.Validator.errorBorderColor(), borderInsets));
                        // TODO: Show error message
                    }
                }
            }
        });
        this.parameterNameCollationRulesTextField = parameterNameCollationRulesTextField;
        parameterNameCollationRulesForDecoratorsPanel.add(parameterNameCollationRulesTextField);
        checkBoxPanel.add(parameterNameCollationRulesForDecoratorsPanel,
                          new GridConstraints(checkBoxPanelRow++, 0, 1, 1, 8, GridBagConstraints.BOTH, 3, 0, null, null, null));

        JBCheckBox pythonLiveInspectionCheckBox = new JBCheckBox();
        this.pythonLiveInspection = pythonLiveInspectionCheckBox;
        pythonLiveInspectionCheckBox.setText("Python Arguments Live Inspection");
        // checkBoxPanel.add(pythonLiveInspectionCheckBox, new GridConstraints(checkBoxPanelRow++, 0, 1, 1, 8, 0, 3, 0, null, null, null));

        JBPanel<?> customArgumentsPanel = new JBPanel<>();
        customArgumentsPanel.setLayout(new BoxLayout(customArgumentsPanel, BoxLayout.LINE_AXIS));
        //        checkBoxPanel.add(customArgumentsPanel, new GridConstraints(row++, 0, 1, 1, 8, GridBagConstraints.BOTH, 3, 0, null, null, null));

        JBLabel customArgumentsLabel = new JBLabel();
        customArgumentsLabel.setText("Python inspection custom arguments: ");
        //        customArgumentsPanel.add(customArgumentsLabel);
        customArgumentsLabel.setToolTipText(RobotBundle.getMessage("options.inspection.custom.arguments.tooltip"));

        JBTextField pythonLiveInspectionCustomArgumentsTextField = new JBTextField();
        this.pythonLiveInspectionCustomArgumentsTextField = pythonLiveInspectionCustomArgumentsTextField;
        pythonLiveInspectionCustomArgumentsTextField.setText("Custom Arguments");
        //        customArgumentsPanel.add(pythonLiveInspectionCustomArgumentsTextField);

        JBPanel<?> pythonLiveInspectionForDecoratorsPanel = new JBPanel<>();
        pythonLiveInspectionForDecoratorsPanel.setLayout(new BoxLayout(pythonLiveInspectionForDecoratorsPanel, BoxLayout.LINE_AXIS));
        //        checkBoxPanel.add(pythonLiveInspectionForDecoratorsPanel, new GridConstraints(row, 0, 1, 1, 8, GridBagConstraints.BOTH, 3, 0, null, null, null));

        JBLabel pythonLiveInspectionForDecoratorsLabel = new JBLabel();
        pythonLiveInspectionForDecoratorsLabel.setText("Python inspection for decorators: ");
        pythonLiveInspectionForDecoratorsLabel.setToolTipText(RobotBundle.getMessage("options.inspection.decorators.tooltip"));
        pythonLiveInspectionForDecoratorsPanel.add(pythonLiveInspectionForDecoratorsLabel);

        JBTextField pythonLiveInspectionForDecoratorsTextField = new JBTextField();
        this.pythonLiveInspectionForDecoratorsTextField = pythonLiveInspectionForDecoratorsTextField;
        pythonLiveInspectionForDecoratorsTextField.setText("");
        pythonLiveInspectionForDecoratorsPanel.add(pythonLiveInspectionForDecoratorsTextField);

        pythonLiveInspection.addItemListener(e -> {
            int selectionStateChange = e.getStateChange();
            boolean enabled = selectionStateChange == ItemEvent.SELECTED;
            customArgumentsLabel.setEnabled(enabled);
            pythonLiveInspectionCustomArgumentsTextField.setEnabled(enabled);
            pythonLiveInspectionForDecoratorsLabel.setEnabled(enabled);
            pythonLiveInspectionForDecoratorsTextField.setEnabled(enabled);
        });
        customArgumentsLabel.setEnabled(pythonLiveInspectionCheckBox.isSelected());
        pythonLiveInspectionCustomArgumentsTextField.setEnabled(pythonLiveInspectionCheckBox.isSelected());
        pythonLiveInspectionForDecoratorsLabel.setEnabled(pythonLiveInspectionCheckBox.isSelected());
        pythonLiveInspectionForDecoratorsTextField.setEnabled(pythonLiveInspectionCheckBox.isSelected());

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
        return getHelpTopic();
    }

    @Nls
    @Override
    public String getDisplayName() {
        return RobotBundle.getMessage("options.entrypoint");
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
        return provider != null && (provider.allowTransitiveImports() != this.allowTransitiveImports.isSelected()
                                    || provider.capitalizeKeywords() != this.capitalizeKeywords.isSelected()
                                    || provider.smartAutoEncloseVariable() != this.smartAutoEncloseVariable.isSelected()
                                    || provider.multilineIndentation() != this.multilineIndentation.isSelected() || provider.parameterNameCollationRules()
                                                                                                                            .equals(this.parameterNameCollationRulesTextField.getText())
                                    || provider.pythonLiveInspection() != this.pythonLiveInspection.isSelected() || provider.pythonLiveInspection() && (
                !provider.getPythonLiveInspectionAdditionalArguments().equals(this.pythonLiveInspectionCustomArgumentsTextField.getText())
                || !provider.getPythonLiveInspectionDecorators().equals(convertToDecoratorNames())));
    }

    @Override
    public void apply() {
        RobotOptionsProvider provider = getOptionProvider();
        if (provider != null) {
            provider.setAllowTransitiveImports(this.allowTransitiveImports.isSelected());
            provider.setCapitalizeKeywords(this.capitalizeKeywords.isSelected());
            provider.setSmartAutoEncloseVariable(this.smartAutoEncloseVariable.isSelected());
            provider.setMultilineIndentation(this.multilineIndentation.isSelected());
            provider.setParameterNameCollationRules(this.parameterNameCollationRulesTextField.getText());
            provider.setPythonLiveInspection(this.pythonLiveInspection.isSelected());
            provider.setPythonLiveInspectionAdditionalArguments(this.pythonLiveInspectionCustomArgumentsTextField.getText());
            List<String> decoratorNames = convertToDecoratorNames();
            provider.setPythonLiveInspectionDecorators(decoratorNames);
        }
    }

    private List<String> convertToDecoratorNames() {
        String[] decorators = this.pythonLiveInspectionForDecoratorsTextField.getText().split("\\s*,\\s*");
        return Arrays.stream(decorators)
                     .map(decorator -> decorator.startsWith("@") ? decorator.substring(1) : decorator)
                     .filter(decorator -> !decorator.isBlank())
                     .toList();
    }

    @Override
    public void reset() {
        RobotOptionsProvider provider = getOptionProvider();
        if (provider != null) {
            this.allowTransitiveImports.setSelected(provider.allowTransitiveImports());
            this.capitalizeKeywords.setSelected(provider.capitalizeKeywords());
            this.smartAutoEncloseVariable.setSelected(provider.smartAutoEncloseVariable());
            this.multilineIndentation.setSelected(provider.multilineIndentation());
            this.parameterNameCollationRulesTextField.setText(provider.parameterNameCollationRules());
            this.pythonLiveInspection.setSelected(provider.pythonLiveInspection());
            this.pythonLiveInspectionCustomArgumentsTextField.setText(provider.getPythonLiveInspectionAdditionalArguments());
            this.pythonLiveInspectionForDecoratorsTextField.setText(String.join(", ", provider.getPythonLiveInspectionDecorators()));
        }
    }
}
