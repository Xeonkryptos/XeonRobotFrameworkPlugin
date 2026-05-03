# XeonRobotFrameworkPlugin

<!-- Plugin description -->
Robot Framework plugin for PyCharm.

If you like this plugin, please leave your [review](https://plugins.jetbrains.com/plugin/27395-xeon-robotframework-support/reviews/new) with five stars, also, please star my
GitHub [project](https://github.com/xeonkryptos/XeonRobotFrameworkPlugin).

## Features

### Code Formatting

### Syntax Highlighting

![](https://raw.githubusercontent.com/xeonkryptos/XeonRobotFrameworkPlugin/main/docs/imgs/syntax-highlighting.jpg)

### Easy Test Execution

![](https://raw.githubusercontent.com/xeonkryptos/XeonRobotFrameworkPlugin/main/docs/imgs/easy-test-execution.jpg)

### Debugging In Python And Robot Code

![](https://raw.githubusercontent.com/xeonkryptos/XeonRobotFrameworkPlugin/main/docs/imgs/debugging-in-robot-code.jpg)

### Code Navigation

![](https://raw.githubusercontent.com/xeonkryptos/XeonRobotFrameworkPlugin/main/docs/imgs/navigation.jpg)

### Find Usages For Keywords and Variables

### Robot File Structure And Folding

![](https://raw.githubusercontent.com/xeonkryptos/XeonRobotFrameworkPlugin/main/docs/imgs/folding.jpg)

### Code Inspection

Inspect your code, find things to improve/optimize, find potential bugs and more.

### Code Completion

![](https://raw.githubusercontent.com/xeonkryptos/XeonRobotFrameworkPlugin/main/docs/imgs/code-completion.jpg)

### SMT Console View

The SMT Console View is a console view that shows the output of the Robot Framework test execution. It is the same view used for all test
executions in JetBrains IDEs. It shows the output of the test execution in a tree view, with the ability to expand and collapse the test cases
and keywords.

![](https://raw.githubusercontent.com/xeonkryptos/XeonRobotFrameworkPlugin/main/docs/imgs/smt-console.jpg)

### Rename-Refactor

![](https://raw.githubusercontent.com/xeonkryptos/XeonRobotFrameworkPlugin/main/docs/imgs/rename-refactor.jpg)

## Prerequisites

* Python 3.10 or newer
* Robot Framework 5.0 or newer

## Limitations

* Only static code analysis is available for now. Dynamic features like described [here](https://robotframework.org/robotframework/latest/RobotFrameworkUserGuide.html#dynamic-library-api) isn't
  supported yet. It is planned as an upcoming feature, though. Same applies to Python's decorators which modify the function signature.
* Code completion for object access in Robot's variables isn't possible yet.

## Upcoming Features

For the upcoming releases, the following features are planned to get implemented:

* Support dynamic libraries. Load provided variables, keywords, everything of relevance from them and provide them in code navigation, code completion, inspections, etc.
* Extended code completion in variables, especially for object access.
* Support to load documentation provided in user keywords when hovering a used user keyword or when looking for it in code completion.

Keep in mind that this is not a complete list of planned features. More features will be added in the future. Also, the order of implementation isn't described by this list.

## Report Issues

* https://github.com/xeonkryptos/XeonRobotFrameworkPlugin/issues

<!-- Plugin description end -->

## Buy Me A Coffee

![](https://raw.githubusercontent.com/xeonkryptos/XeonRobotFrameworkPlugin/main/docs/imgs/qr.jpg)

## Credits

Credits go to the Jnhyperion and their plugin [Hyper Robot Framework](https://github.com/jnhyperion/HyperRobotFrameworkPlugin). This plugin is a fork of
it and makes changes and improvements to increase the set of features and performance.

Credits also go to [RobotCodeDev](https://github.com/robotcodedev/robotcode) for the toolsets to make working with the Robot Framework easier.
