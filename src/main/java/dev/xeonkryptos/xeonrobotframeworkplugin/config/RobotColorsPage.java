package dev.xeonkryptos.xeonrobotframeworkplugin.config;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotFeatureFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import java.util.Map;

public class RobotColorsPage implements ColorSettingsPage {

    private static final ColorDescriptor[] COLORS = ColorDescriptor.EMPTY_ARRAY;
    private static final AttributesDescriptor[] ATTRIBUTES = new AttributesDescriptor[] { new AttributesDescriptor(RobotBundle.message(
            "color.settings.sectionHeader"), RobotHighlighter.SECTION_TITLE),
                                                                                          new AttributesDescriptor(RobotBundle.message("color.settings.comment"),
                                                                                                                   RobotHighlighter.COMMENT),
                                                                                          new AttributesDescriptor(RobotBundle.message(
                                                                                                  "color.settings.parameter"), RobotHighlighter.PARAMETER),
                                                                                          new AttributesDescriptor(RobotBundle.message("color.settings.argument"),
                                                                                                                   RobotHighlighter.ARGUMENT),
                                                                                          new AttributesDescriptor(RobotBundle.message("color.settings.gherkin"),
                                                                                                                   RobotHighlighter.GHERKIN),
                                                                                          new AttributesDescriptor(RobotBundle.message(
                                                                                                  "color.settings.syntaxMarker"),
                                                                                                                   RobotHighlighter.STRUCTURAL_KEYWORDS),
                                                                                          new AttributesDescriptor(RobotBundle.message("color.settings.variable"),
                                                                                                                   RobotHighlighter.VARIABLE),
                                                                                          new AttributesDescriptor(RobotBundle.message("color.settings.keyword"),
                                                                                                                   RobotHighlighter.KEYWORD),
                                                                                          new AttributesDescriptor(RobotBundle.message(
                                                                                                  "color.settings.userKeywordName"),
                                                                                                                   RobotHighlighter.USER_KEYWORD_NAME),
                                                                                          new AttributesDescriptor(RobotBundle.message(
                                                                                                  "color.settings.testCaseName"),
                                                                                                                   RobotHighlighter.TEST_CASE_NAME),
                                                                                          new AttributesDescriptor(RobotBundle.message("color.settings.taskName"),
                                                                                                                   RobotHighlighter.TASK_NAME),
                                                                                          new AttributesDescriptor(RobotBundle.message(
                                                                                                  "color.settings.bracketSetting"),
                                                                                                                   RobotHighlighter.LOCAL_SETTING_OPTION),
                                                                                          new AttributesDescriptor(RobotBundle.message("color.settings.setting"),
                                                                                                                   RobotHighlighter.GLOBAL_SETTING_OPTION),
                                                                                          new AttributesDescriptor(RobotBundle.message(
                                                                                                  "color.settings.pythonExpression"),
                                                                                                                   RobotHighlighter.PYTHON_EXPRESSION_CONTENT) };

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
               Ignored
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
                   Given  calculator has been cleared
                   \
               When user types "1 + 1"
                   And  user pushes equals
                   Then  result is "2"
               
               #Subtraction
               #  [Tags]  Calculator
               #    \
               TODO: implement me
               
               *** Keywords ***
               Calculator has been cleared
                   Push button    C
               
               IF  '${variable}' == 'B'
                   Push Button    A
               END
               
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
