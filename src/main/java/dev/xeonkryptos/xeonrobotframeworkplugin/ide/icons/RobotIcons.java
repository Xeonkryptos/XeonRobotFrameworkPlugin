package dev.xeonkryptos.xeonrobotframeworkplugin.ide.icons;

import com.intellij.icons.AllIcons.Nodes;
import com.intellij.icons.AllIcons.RunConfigurations;
import com.intellij.openapi.util.IconLoader;

import javax.swing.Icon;

public class RobotIcons {

    public static final Icon FILE = IconLoader.findIcon("/images/robot.png", RobotIcons.class.getClassLoader());
    public static final Icon RESOURCE = IconLoader.findIcon("/images/resource.png", RobotIcons.class.getClassLoader());
    public static final Icon SYNTAX = IconLoader.findIcon("/images/syntax.svg", RobotIcons.class.getClassLoader());
    public static final Icon PYTHON = IconLoader.findIcon("/images/python.svg", RobotIcons.class.getClassLoader());
    public static final Icon MODELS = Nodes.Models;
    public static final Icon CONTROLLER = Nodes.Controller;
    public static final Icon FUNCTION = Nodes.Function;
    public static final Icon JUNIT = RunConfigurations.Junit;
    public static final Icon VARIABLE = Nodes.Variable;
    public static final Icon PARAMETER = Nodes.Parameter;

    private RobotIcons() {
    }
}
