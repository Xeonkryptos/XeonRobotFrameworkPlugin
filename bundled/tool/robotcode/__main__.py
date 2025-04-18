import os
import pathlib
import sys

if __name__ == "__main__":
    # Add current directory to path to make module importable
    module_dir = os.path.dirname(os.path.abspath(__file__))
    if module_dir not in sys.path:
        sys.path.insert(0, module_dir)

    if os.getenv("NO_TEAMCITY", '0').lower() not in ('true', '1', 'yes'):
        # Use module name with dot notation
        insert_pos = 2
        sys.argv.insert(insert_pos, "--listener=smt_listeners.RobotIntellijListener")

    from robotcode.cli import robotcode

    robotcode(
        windows_expand_args=False,
        default_map={"launcher_script": str(pathlib.Path(__file__).parent)},
    )
