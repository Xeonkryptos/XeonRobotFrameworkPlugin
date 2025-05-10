package dev.xeonkryptos.xeonrobotframeworkplugin.ide.config;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotFeatureFileType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotHighlighter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import java.util.Map;

public class RobotColorsPage implements ColorSettingsPage {

    private static final ColorDescriptor[] COLORS = ColorDescriptor.EMPTY_ARRAY;
    private static final AttributesDescriptor[] ATTRIBUTES = new AttributesDescriptor[] { new AttributesDescriptor(RobotBundle.getMessage(
            "color.settings.heading"), RobotHighlighter.HEADING),
                                                                                          new AttributesDescriptor(RobotBundle.getMessage(
                                                                                                  "color.settings.comment"), RobotHighlighter.COMMENT),
                                                                                          new AttributesDescriptor(RobotBundle.getMessage(
                                                                                                  "color.settings.parameter"), RobotHighlighter.PARAMETER),
                                                                                          new AttributesDescriptor(RobotBundle.getMessage(
                                                                                                  "color.settings.argument"), RobotHighlighter.ARGUMENT),
                                                                                          new AttributesDescriptor(RobotBundle.getMessage("color.settings.error"),
                                                                                                                   RobotHighlighter.ERROR),
                                                                                          new AttributesDescriptor(RobotBundle.getMessage(
                                                                                                  "color.settings.gherkin"), RobotHighlighter.GHERKIN),
                                                                                          new AttributesDescriptor(RobotBundle.getMessage(
                                                                                                  "color.settings.syntaxMarker"),
                                                                                                                   RobotHighlighter.SYNTAX_MARKER),
                                                                                          new AttributesDescriptor(RobotBundle.getMessage(
                                                                                                  "color.settings.variable"), RobotHighlighter.VARIABLE),
                                                                                          new AttributesDescriptor(RobotBundle.getMessage(
                                                                                                  "color.settings.variableDefinition"),
                                                                                                                   RobotHighlighter.VARIABLE_DEFINITION),
                                                                                          new AttributesDescriptor(RobotBundle.getMessage(
                                                                                                  "color.settings.keyword"), RobotHighlighter.KEYWORD),
                                                                                          new AttributesDescriptor(RobotBundle.getMessage(
                                                                                                  "color.settings.keywordDefinition"),
                                                                                                                   RobotHighlighter.KEYWORD_DEFINITION),
                                                                                          new AttributesDescriptor(RobotBundle.getMessage(
                                                                                                  "color.settings.bracketSetting"),
                                                                                                                   RobotHighlighter.BRACKET_SETTING),
                                                                                          new AttributesDescriptor(RobotBundle.getMessage(
                                                                                                  "color.settings.setting"), RobotHighlighter.SETTING),
                                                                                          new AttributesDescriptor(RobotBundle.getMessage(
                                                                                                  "color.settings.import"), RobotHighlighter.IMPORT) };

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
        return """
               invalid
               *** Settings ***
               Documentation     This is some demo text
               Library           CalculatorLibrary
               
               *** Variables ***
               ${var1}  \
               12345
               ${var2}  another variable
               
               *** Test Cases ***
               Addition
                 [Tags]  Calculator
                   Given calculator has been cleared
                   \
               When user types "1 + 1"
                   And user pushes equals
                   Then result is "2"
               
               #Subtraction
               #  [Tags]  Calculator
               #    \
               TODO: implement me
               
               *** Keywords ***
               Calculator has been cleared
                   Push button    C
               
               User types "${expression}"
                   Push buttons    \
               ${expression}
               
               User pushes equals
                   Push button    =
               
               Result is "${result}"
                   Result should be    ${result}""";
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
    }

    @NotNull
    @Override
    public AttributesDescriptor @NotNull [] getAttributeDescriptors() {
        return ATTRIBUTES;
    }

    @NotNull
    @Override
    public ColorDescriptor @NotNull [] getColorDescriptors() {
        return COLORS;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Robot";
    }
}
