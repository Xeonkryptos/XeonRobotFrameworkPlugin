<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# HyperRobotFrameworkPlugin Changelog
## [0.1.20]
### Fixed
- Fixed IndexOutOfBoundsException when exception occurs in Robot file but default breakpoint is removed
- Fixed error running a robot testcase with robotcode module in run configuration
- When breakpoints are muted tell the debugger to continue
- IllegalArgumentException on creating run configuration: When base path doesn't match file path for relativization

## [0.1.19]
### Added
- Added support for debugging of Robot files

## [0.1.18-1]
### Added
- Jump to parameter's definition in python files from keywords

### Changed
- Parsing of keyword parameters changed
- Optimized code completion suggestions

## [0.1.18]
### Added
- This is [中秋节 🥮](https://en.wikipedia.org/wiki/Mid-Autumn_Festival) special edition, please enjoy! 🍻
- Support syntax markers `VAR` ([#73](https://github.com/jnhyperion/HyperRobotFrameworkPlugin/issues/73))

## [0.1.17]
### Added
- Support Gherkin syntax markers `BUT` ([#69](https://github.com/jnhyperion/HyperRobotFrameworkPlugin/issues/69))
- Support code completion with library alias after `AS` / `WITH NAME` ([#14](https://github.com/jnhyperion/HyperRobotFrameworkPlugin/issues/14))

## [0.1.16]
### Added
- Support syntax markers `FINALLY`

### Changed
- Code completion enhancement
- Inspection performance improvements

## [0.1.15]
### Added
- Support Robot file spellcheck

## [0.1.14]
### Added
- Config option `Smart Auto Enclose Variable` (In `Preferences -> Languages & Frameworks -> Robot Options`)
- Config option `Always Insert 4 whitespace When Typing Tab`

### Fixed
- Some keywords with absolute library path cannot be resolved properly
- Some libraries with relative imports cannot be resolved properly

## [0.1.13]
### Added
- Smart auto enclose variable when typing `$`, `@`, `&`
- Always insert 4 whitespace when typing `Tab`
- Folding for all syntax markers block

## [0.1.12]
### Added
- Smart auto indent when pressing enter button
- Support syntax markers `AS`, `IN`, `IN RANGE`, `IN ENUMERATE`

## [0.1.11]
### Fixed
- [[BUG]](https://github.com/jnhyperion/HyperRobotFrameworkPlugin/issues/54): Plugin error "Cannot distinguish StubFileElementTypes"

## [0.1.10]
### Fixed
- `Variables/Keywords` defined in python files are resolved incorrectly when python file is modified sometimes

## [0.1.9]
### Added
- Keyword with decorator @keyword defined in python file library can be resolved
- Support parsing robot variables defined in python class members and properties

## [0.1.8]
### Fixed
- [[BUG]](https://github.com/jnhyperion/HyperRobotFrameworkPlugin/issues/50): Variables defined in resource file cannot be resolved

## [0.1.7]
### Fixed
- Keyword parsing is broken in data driven style Robot file

## [0.1.6]
### Fixed
- Library is incorrectly resolved sometimes when multi library classes found
- Library is incorrectly resolved sometimes when multi `PyCharm` windows is opened

## [0.1.5]
### Fixed
- Robot builtin keywords cannot be resolved sometimes
- Other minor bugs

## [0.1.4]
### Added
- Robot file create action in `File -> New -> Robot File`
- Standalone Robot run configuration, support configuration template
- Support test execution in arbitrary working directory

### Fixed
- Builtin library with prefix `robot.libraries` cannot be recognized
- Library is incorrectly resolved sometimes

## [0.1.3]
### Changed
- Robot test run configuration will enable option `Emulate terminal in output console` by default

### Fixed
- Failed to load python libraries when restarting project sometimes
- Low performance in code inspections
- IDE UI freeze when project files are modified sometimes

## [0.1.2]
### Fixed
- Resource file import cannot be resolved in `Windows`
- Resource file import code completion is incorrect in `Windows`

## [0.1.1]
### Added
- Folding function for comments

### Fixed
- Code inspection RobotKeywordNotFound not working properly
- Variable embedded Keyword navigation not working properly
- Library import reference is incorrect sometimes

## [0.1.0]
### Added
- Better file structure view
- Folding function for multi line statements

### Fixed
- No word completion for `tasks`

## [0.0.9]
### Changed
- `RETURN` is changed from keyword to robot syntax marker

### Fixed
- Variable cannot be resolved after multi line statements
- Incorrect paring after robot syntax markers
- Other minor bugs

## [0.0.8]
### Fixed
- Reference link is incorrect sometimes when refactoring projects
- Incorrect inspection warning when `resource` file is not directly used in transitive import 
- Cannot parse variable properly in syntax maker statement block

## [0.0.7]
### Added
- When plugin cannot find resource/library/variable file by the exact given path, it will try to search the entire project for the most possible files. (Will be useful when your file path is dynamic during the runtime.)
- If plugin find more than one results during the fuzzy search, this file is still marked as unrecognized, you can find the multi results by clicking the references.
- Note, this feature may ignore your potential runtime importing errors.

### Fixed
- Some potential IDE errors
- Robot builtin variable cannot be recognized sometimes
- Some defined variables are missing in word completion list
- Some defined variables' formats are incorrect in word completion list

## [0.0.6]
### Added
- Variables dynamically set in settings `Test Setup` & `Suite Setup` now can be resolved
- Variables dynamically set in `__init__.robot` now can be resolved

### Fixed
- Incorrect import reference when there are multi python/resource files with the same name

## [0.0.5]
### Fixed
- Some plugin errors when updating project structure
- Some plugin errors when updating project python interpreter

## [0.0.4]
### Added
- Support `WHILE` `CONTINUE` `BREAK` syntax
- Support defined variable word completion in keyword and test
### Fixed
- Some builtin variables are incorrect in recommendation word completion
- Some library keywords completion contains string `'`
- Dict variables are not considered as variables
- Number variable such as `${1}` will be recognized as undefined variable
- Variable definition is not recognized after `FOR` statement


## [0.0.3]
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
