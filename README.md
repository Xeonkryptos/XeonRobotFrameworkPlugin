# XeonRobotFrameworkPlugin
<!-- Plugin description -->
Robot Framework plugin for PyCharm.

If you like this plugin, please leave your [review](https://plugins.jetbrains.com/plugin/27395-xeon-robotframework-support/reviews/new) with five stars, also, please star my Github [project](https://github.com/xeonkryptos/XeonRobotFrameworkPlugin).

## Features
### Syntax Highlighting
### Code Completion
### Easy Test Execution
### Debugging In Python And Robot Code
### Code Navigation
### Find Keyword Usages
### Robot File Structure And Folding
### Code Inspection
### Code Completion

Besides the normal suggestion of keywords, imports, variables and whatever can be code completed, for keywords you can prefix them with
\* (star) or / (slash) to extend the code completion feature by changing what is added on acceptance of the code completion.

* With the \* (star) you can add the keyword with its mandatory parameter names already added for you. Mandatory parameters are identified
by taking a look at the python function, searching for keyword arguments and checking if they have a default value or not. With a missing
default value, they're considered mandatory.
* With the / (slash) you can add the keyword with all its defined parameter names already added for you.

It can be used in combination with the TAB feature of the IDE too. Especially useful for an already defined keyword, you can add the
mandatory or all parameters.

If you also use type hints in your Python code and one of the parameters you want to fill references to an enum, the enum values are added
to the completion list for the parameter.

### SMT Console View

The SMT Console View is a console view that shows the output of the Robot Framework test execution. It is the same view used for all test 
executions in Jetbrains IDEs. It shows the output of the test execution in a tree view, with the ability to expand and collapse the test cases 
and keywords.

## Prerequisites
  * Set your `Python` interpreter properly for your `PyCharm`

## Report Issues
  * https://github.com/xeonkryptos/XeonRobotFrameworkPlugin/issues

<!-- Plugin description end -->

## Buy Me A Coffee
![](https://raw.githubusercontent.com/xeonkryptos/XeonRobotFrameworkPlugin/main/docs/imgs/qr.jpg)

## Credits

Credits go to the plugin Jnhyperion and its plugin [Hyper Robot Framework](https://github.com/jnhyperion/HyperRobotFrameworkPlugin). This plugin is a fork of
it and make some changes and improvements.

Credits also go to [RobotCodeDev](https://github.com/robotcodedev/robotcode) for the toolsets to make working with the Robot Framework easier.
