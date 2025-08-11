<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# XeonRobotFrameworkPlugin Changelog

## Unreleased

## 0.4.2

### Fixed

- Fixed debug issues with Robot run configurations on new IDE version 2025.2

## 0.4.1

### Added
- Follow Python imports when searching for defined variables

### Fixed

- Fixed setting of breakpoints in robot files
- Fixed finding defined variables through keyword call (set global variable, set suite variable, set test variable)
- Fixed finding of globally defined variables, especially defined in python files and imported via resource files or variable files
- Fixed handling with variables in code completion

## 0.4.0

### Changed

- Updated build.gradle and gradle.properties to make plugin compatible with IntelliJ IDEA and all other JetBrains IDEs supporting Python
- Rewrote internal parsing logic to get a more robust, more efficient and easier to maintain codebase

## 0.3.3

### Fixed

- On parsing errors, simply mark everything as unparsable (ERROR) and fall through to avoid blocking the entire IDE

## 0.3.2

### Fixed

- Fixed reason for log warning about multiple module serialization when creating and updating run configurations
- Fixed invalid access to index when base PSI elements are invalid in reverse lookup search

## 0.3.1

### Fixed

- Fixed issues creating new run configurations

## 0.3.0

### Added

- Support for Find Usages of defined variables
- Annotate/Highlight unused variables as such and provide a quick-fix to remove them
- Show an error marking when a keyword is missing mandatory parameters
- Added Up/Down mover with support for moving of keywords, variable definitions, parameter/argument and test cases
- Added support for breadcrumbs
- Added search capability of test case names and symbols into PyCharm's searches

### Fixed

- Restrict stub creation to only the corresponding elements
- Fixed reasons for PsiInvalidElementAccessExceptions
- Fixed reason for too many started read actions with the same stacktrace
- Fixed multiline support detection for documentation settings when cursor is placed at a comment
- Fixed invalid code completion for keywords using meta characters like `*` or `/` with a variable definition before the keyword
- Freeze of UI when starting debug mode
- Fixed detection of locations a breakpoint in robot file is settable or not
- Fixed log warn messages indicating about more than one serialization of module configuration

### Changed

- Restrict error marking on unresolved parameter to the parameter name only
- Don't expect a keyword statement for every variable definition. When the argument of a variable definition is unresolvable, interpret it as a simple argument
- Add only parameters in code completion for keywords with meta characters that aren't defined yet
- Mark unresolvable parameter as arguments in highlighting with annotators
- Every kind of bracket setting supports now multi-line-mode

## 0.2.8

### Added

- Support escaping of equals sign to signal an argument rather than a parameter
- Reference fully qualified python modules in arguments to allow jumping to them
- Code completion when defining a library import

### Fixed

- Fixed reason for StackOverflowError
- Fixed reasons for PsiInvalidElementAccessExceptions
- Fixed not showing of Bracket settings in code completion
- Automatically add ellipsis and indentation on enter in keyword statements which are preceded by a variable definition or the current line followed by a
  comment
- Fixed too many started threads when a lot of file changes are detected at once
- Annotation of import definitions that can't be resolved
- Fixed auto-completion for resource imports
- Fixed auto-completion that provided sections at incorrect places

### Changed

- Interpret parameters without a value as an argument
- Detected parameters in a data template setup correctly
- Parameters without an argument are interpreted as an argument themselves
- Switched from CachedValuesManager to ResolveCache
- Changed the annotation level of unknown/unresolved variables to a weak warning

## 0.2.7

### Added

- Improved performance by adding more caches
- Added structural view of members in project explorer for robot files
- Support for assignment of multiple variables based on the return value of a keyword
- Add ellipsis and indentation automatically when typing in documentation settings and keyword statements
- Extended completion provider for keyword statements to add only mandatory parameters by prefixing the keyword completion with a `*`
- Provide enum values for code completions in parameters when one of the defined data types references an enum
- Mark used keywords in robot files and code completion as deprecated when they are marked as deprecated (`@deprecated` decorator even a custom one)

### Changed

- Removed global caches and replaced with element-centric ones where useful (should improve memory footprint)

### Fixed

- Different reasons for invalid PSI element accesses fixed
- Parsing of arguments containing an equal sign in the argument part
- Issues with case-sensitivity leading to incorrectly marked keywords as unresolvable on a case-insensitive file system

## 0.2.6

### Fixed

- Reduced CPU usage
- Fixed invalid index accesses

## 0.2.5

### Added

- Hyperlinking of Robot reports with opening them in the browser on click
- Retriggering annotations processing on changes in python functions used in robot keywords

### Changed

- Improved performance by reducing usages of PSI#getText() calls
- Don't keep executor buttons in an unusable state when an internal error occurred (usually because of a missing interpreter configuration)

### Fixed

- Parameter info not shown when at the end position of a keyword statement (cursor at the last position of line in the statement)

## 0.2.4

### Fixed

- Fixed issues starting run/debug sessions of robot runs after last update

## 0.2.3

### Added

- Use live inspection of python functions to resolve parameters modified through decorators
- Include `__init__.robot` files when looking for defined variables

### Fixed

- Caching issue leading to incorrectly marked parameters
- Fixed parsing issue with new keyword definitions following after a bracket template keyword definition
- Don't mark unknown parameters as failure when there is a keyword container. Also, jump to the keyword container when jumping to the referenced parameter
- StringIndexOutOfBoundsException when requesting code completion for an argument of a keyword parameter
- When auto completing a section and typed for example `***` the suggested section part overwrites the `***` part instead of adding it again

### Changed

- Removed obsolete configuration entries
- Not analyzing `*.resource` files for unused dependencies. A more sophisticated approach is needed. Will be implemented at a later time
- Made some annotators not DumbAware to be able to access the index correctly

## 0.2.2

### Added

- Make it possible to create run configurations when right-clicking on a robot file with test cases and when in a robot file with keywords

### Changed

- Improved performance by reducing usages of PSI#getText() calls
- Mark only mandatory parameters of keywords as bold, optional parameters are not bold anymore
- Use annotators for compilation errors/warnings rather than inspections

### Fixed

- Fixed invalid showing code completion in keyword arguments for other keyword calls

## 0.2.1

### Fixed

- Fixed issue with debugging of Python code (python debugger holding at breakpoints, but view doesn't show any data like variables and stack frames)
- Don't show teamcity messages for split view when terminal is emulated and no split-view shown

## 0.2.0

### Added

- Added support for Comments section
- Replaced simple console view with test cases result reports view like for xUnit test executions
- Allow creation of run configurations from project explorer when mouse right-click on a directory

### Fixed

- Parsing and handling of custom keywords in robot files with defined input \[Arguments]
- Fixed parsing issue of variables section: Spaces in a new line without anything else was interpreted as a new variable definition
- Find Usages to do a reverse reference search for keywords from python functions
- Don't provide code completion in the following bracket settings: Documentation, Arguments, Tags

## 0.1.21

### Added

- When starting a keyword statement with / the code completion provides the complete list of available keywords and will add any defined parameter of this
  keyword with default values if available
- Added inspection to check for unknown keyword parameters
- Added inspection to check for keyword parameters without a value
- Added a new executor to execute a robot testcase with dry-run option
- Added parameter info handling for keywords - showing parameters and highlights them on CTRL+P

### Fixed

- StringIndexOutOfBoundsException when requesting code completion for an argument of a keyword parameter
- Don't create another run configuration for the same testcase when one exists already but created by another run mode
- Show available parameters in code completion for keywords when a previous parameter is defined but missing a value and a new parameter definition is possible
  because of an ellipsis or super space
- Jumping to parameter definitions in python files from keywords now works correctly with CTRL+Left Mouse Click
- When a process started in debug mode ends, it isn't shown as running in the run tool window anymore

### Changed

- Use module SDK when creating a new Robot run configuration
- Handling of smart enclosing of variables when typing $, @, & optimized - changed handling to IntelliJ's default behavior

## 0.1.20

### Fixed

- Fixed IndexOutOfBoundsException when exception occurs in Robot file but default breakpoint is removed
- Fixed error running a robot testcase with robotcode module in run configuration
- When breakpoints are muted tell the debugger to continue
- IllegalArgumentException on creating run configuration: When base path doesn't match file path for relativization

## 0.1.19

### Added

- Added support for debugging of Robot files

## 0.1.18-1

### Added

- Jump to parameter's definition in python files from keywords

### Changed

- Parsing of keyword parameters changed
- Optimized code completion suggestions

## 0.1.18

### Added

- This is [中秋节 🥮](https://en.wikipedia.org/wiki/Mid-Autumn_Festival) special edition, please enjoy! 🍻
- Support syntax markers `VAR` ([#73](https://github.com/jnhyperion/HyperRobotFrameworkPlugin/issues/73))

## 0.1.17

### Added

- Support Gherkin syntax markers `BUT` ([#69](https://github.com/jnhyperion/HyperRobotFrameworkPlugin/issues/69))
- Support code completion with library alias after `AS` / `WITH NAME` ([#14](https://github.com/jnhyperion/HyperRobotFrameworkPlugin/issues/14))

## 0.1.16

### Added

- Support syntax markers `FINALLY`

### Changed

- Code completion enhancement
- Inspection performance improvements

## 0.1.15

### Added

- Support Robot file spellcheck

## 0.1.14

### Added

- Config option `Smart Auto Enclose Variable` (In `Preferences -> Languages & Frameworks -> Robot Options`)
- Config option `Always Insert 4 whitespace When Typing Tab`

### Fixed

- Some keywords with absolute library path cannot be resolved properly
- Some libraries with relative imports cannot be resolved properly

## 0.1.13

### Added

- Smart auto enclose variable when typing `$`, `@`, `&`
- Always insert 4 whitespace when typing `Tab`
- Folding for all syntax markers block

## 0.1.12

### Added

- Smart auto indent when pressing enter button
- Support syntax markers `AS`, `IN`, `IN RANGE`, `IN ENUMERATE`

## 0.1.11

### Fixed

- [[BUG]](https://github.com/jnhyperion/HyperRobotFrameworkPlugin/issues/54): Plugin error "Cannot distinguish StubFileElementTypes"

## 0.1.10

### Fixed

- `Variables/Keywords` defined in python files are resolved incorrectly when python file is modified sometimes

## 0.1.9

### Added

- Keyword with decorator @keyword defined in python file library can be resolved
- Support parsing robot variables defined in python class members and properties

## 0.1.8

### Fixed

- [[BUG]](https://github.com/jnhyperion/HyperRobotFrameworkPlugin/issues/50): Variables defined in resource file cannot be resolved

## 0.1.7

### Fixed

- Keyword parsing is broken in data driven style Robot file

## 0.1.6

### Fixed

- Library is incorrectly resolved sometimes when multi library classes found
- Library is incorrectly resolved sometimes when multi `PyCharm` windows is opened

## 0.1.5

### Fixed

- Robot builtin keywords cannot be resolved sometimes
- Other minor bugs

## 0.1.4

### Added

- Robot file create action in `File -> New -> Robot File`
- Standalone Robot run configuration, support configuration template
- Support test execution in arbitrary working directory

### Fixed

- Builtin library with prefix `robot.libraries` cannot be recognized
- Library is incorrectly resolved sometimes

## 0.1.3

### Changed

- Robot test run configuration will enable option `Emulate terminal in output console` by default

### Fixed

- Failed to load python libraries when restarting project sometimes
- Low performance in code inspections
- IDE UI freeze when project files are modified sometimes

## 0.1.2

### Fixed

- Resource file import cannot be resolved in `Windows`
- Resource file import code completion is incorrect in `Windows`

## 0.1.1

### Added

- Folding function for comments

### Fixed

- Code inspection RobotKeywordNotFound not working properly
- Variable embedded Keyword navigation not working properly
- Library import reference is incorrect sometimes

## 0.1.0]

### Added

- Better file structure view
- Folding function for multi line statements

### Fixed

- No word completion for `tasks`

## 0.0.9

### Changed

- `RETURN` is changed from keyword to robot syntax marker

### Fixed

- Variable cannot be resolved after multi line statements
- Incorrect paring after robot syntax markers
- Other minor bugs

## 0.0.8

### Fixed

- Reference link is incorrect sometimes when refactoring projects
- Incorrect inspection warning when `resource` file is not directly used in transitive import
- Cannot parse variable properly in syntax maker statement block

## 0.0.7

### Added

- When plugin cannot find resource/library/variable file by the exact given path, it will try to search the entire project for the most possible files. (Will be
  useful when your file path is dynamic during the runtime.)
- If plugin find more than one results during the fuzzy search, this file is still marked as unrecognized, you can find the multi results by clicking the
  references.
- Note, this feature may ignore your potential runtime importing errors.

### Fixed

- Some potential IDE errors
- Robot builtin variable cannot be recognized sometimes
- Some defined variables are missing in word completion list
- Some defined variables' formats are incorrect in word completion list

## 0.0.6

### Added

- Variables dynamically set in settings `Test Setup` & `Suite Setup` now can be resolved
- Variables dynamically set in `__init__.robot` now can be resolved

### Fixed

- Incorrect import reference when there are multi python/resource files with the same name

## 0.0.5

### Fixed

- Some plugin errors when updating project structure
- Some plugin errors when updating project python interpreter

## 0.0.4

### Added

- Support `WHILE` `CONTINUE` `BREAK` syntax
- Support defined variable word completion in keyword and test

### Fixed

- Some builtin variables are incorrect in recommendation word completion
- Some library keywords completion contains string `'`
- Dict variables are not considered as variables
- Number variable such as `${1}` will be recognized as undefined variable
- Variable definition is not recognized after `FOR` statement

## 0.0.3

### Fixed

- Fix some plugin errors

### Added

- Support more Pycharm Editions
- Support `TRY` `EXCEPT` syntax
- Support `*** Tasks ***` syntax
- Support data drive style tests

### Changed

- Keyword with arguments tail is changed from 2 blanks to 4
- Optimize recommendation word completion
- Optimize syntax highlight color

## 0.0.2

### Changed

- Optimize recommendation word completion
- Optimize syntax highlight color
- Support `TRY` `EXCEPT` syntax
- Support `*** Tasks ***` syntax
- Support data drive style tests
- Fix some plugin errors

## 0.0.1

- Initial scaffold created from [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template)
