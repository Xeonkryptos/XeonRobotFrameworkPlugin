package com.github.jnhyperion.hyperrobotframeworkplugin.ide.config;

import com.github.jnhyperion.hyperrobotframeworkplugin.RobotBundle;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RobotFeatureFileType;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RobotHighlighter;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RobotKeywordProvider;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import java.util.Map;

public class RobotColorsPage implements ColorSettingsPage {

    private static final ColorDescriptor[] COLORS = new ColorDescriptor[0];
    private static final AttributesDescriptor[] ATTRIBUTES = new AttributesDescriptor[] { new AttributesDescriptor(RobotBundle.getMessage("color.settings.heading"),
                                                                                                                   RobotHighlighter.HEADING),
                                                                                          new AttributesDescriptor(RobotBundle.getMessage("color.settings.comment"),
                                                                                                          RobotHighlighter.COMMENT),
                                                                                          new AttributesDescriptor(RobotBundle.getMessage("color.settings.argument"),
                                                                                                          RobotHighlighter.ARGUMENT),
                                                                                          new AttributesDescriptor(RobotBundle.getMessage("color.settings.error"),
                                                                                                          RobotHighlighter.ERROR),
                                                                                          new AttributesDescriptor(RobotBundle.getMessage("color.settings.gherkin"),
                                                                                                          RobotHighlighter.GHERKIN),
                                                                                          new AttributesDescriptor(RobotBundle.getMessage("color.settings.syntaxMarker"),
                                                                                                          RobotHighlighter.SYNTAX_MARKER),
                                                                                          new AttributesDescriptor(RobotBundle.getMessage("color.settings.variable"),
                                                                                                          RobotHighlighter.VARIABLE),
                                                                                          new AttributesDescriptor(RobotBundle.getMessage(
                                                                                         "color.settings.variableDefinition"),
                                                                                                          RobotHighlighter.VARIABLE_DEFINITION),
                                                                                          new AttributesDescriptor(RobotBundle.getMessage("color.settings.keyword"),
                                                                                                          RobotHighlighter.KEYWORD),
                                                                                          new AttributesDescriptor(RobotBundle.getMessage(
                                                                                         "color.settings.keywordDefinition"),
                                                                                                          RobotHighlighter.KEYWORD_DEFINITION),
                                                                                          new AttributesDescriptor(RobotBundle.getMessage("color.settings.bracketSetting"),
                                                                                                          RobotHighlighter.BRACKET_SETTING),
                                                                                          new AttributesDescriptor(RobotBundle.getMessage("color.settings.setting"),
                                                                                                          RobotHighlighter.SETTING),
                                                                                          new AttributesDescriptor(RobotBundle.getMessage("color.settings.import"),
                                                                                                          RobotHighlighter.IMPORT) };

    @Nullable
    @Override
    public Icon getIcon() {
        return RobotFeatureFileType.getInstance().getIcon();
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new RobotHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        return "invalid\n*** Settings ***\nDocumentation     This is some demo text\nLibrary           CalculatorLibrary\n\n*** Variables ***\n${var1}  "
               + "12345\n${var2}  another variable\n\n*** Test Cases ***\nAddition\n  [Tags]  Calculator\n    Given calculator has been cleared\n    "
               + "When user types \"1 + 1\"\n    And user pushes equals\n    Then result is \"2\"\n\n#Subtraction\n#  [Tags]  Calculator\n#    "
               + "TODO: implement me\n\n*** Keywords ***\nCalculator has been cleared\n    Push button    C\n\nUser types \"${expression}\"\n    Push buttons    "
               + "${expression}\n\nUser pushes equals\n    Push button    =\n\nResult is \"${result}\"\n    Result should be    ${result}";
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
    }

    @NotNull
    @Override
    public AttributesDescriptor[] getAttributeDescriptors() {
        return ATTRIBUTES;
    }

    @NotNull
    @Override
    public ColorDescriptor[] getColorDescriptors() {
        return COLORS;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Robot";
    }
}
