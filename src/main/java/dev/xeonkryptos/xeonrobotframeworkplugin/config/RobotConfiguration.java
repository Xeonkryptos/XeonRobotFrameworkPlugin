package dev.xeonkryptos.xeonrobotframeworkplugin.config;

import com.intellij.openapi.options.Configurable.NoScroll;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.border.CustomLineBorder;
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

public class RobotConfiguration implements NoScroll, SearchableConfigurable {

    private final JBPanel<?> panel;
    private final JBTextField parameterNameCollationRulesTextField;

    public RobotConfiguration() {
        JBPanel<?> mainPanel = new JBPanel<>();
        this.panel = mainPanel;
        mainPanel.setLayout(new GridLayoutManager(2, 3, JBUI.emptyInsets(), -1, -1, false, false));

        int mainPanelRow = 0;
        JBPanel<?> checkBoxPanel = new JBPanel<>();
        checkBoxPanel.setLayout(new GridLayoutManager(7, 1, JBUI.emptyInsets(), -1, -1, false, false));
        mainPanel.add(checkBoxPanel, new GridConstraints(mainPanelRow, 0, 1, 2, 0, 3, 3, 3, null, null, null));

        int checkBoxPanelRow = 0;
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
        checkBoxPanel.add(parameterNameCollationRulesForDecoratorsPanel, new GridConstraints(checkBoxPanelRow, 0, 1, 1, 8, GridBagConstraints.BOTH, 3, 0, null, null, null));

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
        return RobotBundle.message("options.entrypoint");
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
        return provider != null && (provider.parameterNameCollationRules().equals(this.parameterNameCollationRulesTextField.getText()));
    }

    @Override
    public void apply() {
        RobotOptionsProvider provider = getOptionProvider();
        if (provider != null) {
            provider.setParameterNameCollationRules(this.parameterNameCollationRulesTextField.getText());
        }
    }

    @Override
    public void reset() {
        RobotOptionsProvider provider = getOptionProvider();
        if (provider != null) {
            this.parameterNameCollationRulesTextField.setText(provider.parameterNameCollationRules());
        }
    }
}
