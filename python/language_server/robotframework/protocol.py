from typing import (
    TYPE_CHECKING,
    Any,
    ClassVar,
    Dict,
    Final,
    List,
)

from robotcode.core.event import event
from robotcode.core.language import LanguageDefinition
from robotcode.core.lsp.types import InitializeError
from robotcode.core.utils.logging import LoggingDescriptor
from robotcode.jsonrpc2.protocol import JsonRPCErrorException, JsonRPCErrors
from robotcode.robot.utils import RF_VERSION

from language_server.common.protocol import LanguageServerProtocol

from ..__version__ import __version__

if TYPE_CHECKING:
    from .server import RobotLanguageServer


class RobotCodeError(Exception):
    pass


class RobotModuleNotFoundError(RobotCodeError):
    pass


class RobotVersionDontMatchError(RobotCodeError):
    pass


def check_robotframework() -> None:
    try:
        __import__("robot")
    except ImportError as e:
        raise RobotModuleNotFoundError(
            "RobotFramework not installed in current Python environment, please install it."
        ) from e

    if RF_VERSION < (5, 0):
        raise RobotVersionDontMatchError("Wrong RobotFramework version. Expect version >= 5.0")


class RobotLanguageServerProtocol(LanguageServerProtocol):
    _logger: Final = LoggingDescriptor()

    name = "RobotCode Language Server"
    short_name = "RobotCode"
    version = __version__

    file_extensions: ClassVar = {
        "robot",
        "resource",
        "py",
        "yaml",
        "yml",
        "json",
    }

    language_definitions: ClassVar[List[LanguageDefinition]] = [
        LanguageDefinition(
            id="robotframework",
            extensions=[".robot", ".resource"],
            extensions_ignore_case=True,
            aliases=["Robot Framework", "robotframework"],
        )
    ]

    def __init__(
            self,
            server: "RobotLanguageServer"
    ):
        super().__init__(server)
        self.on_initialize.add(self._on_initialize)
        self.on_initialized.add(self.server_initialized)

    @_logger.call
    def _on_initialize(self, sender: Any) -> None:
        try:
            check_robotframework()
        except RobotCodeError as e:
            raise JsonRPCErrorException(
                JsonRPCErrors.INTERNAL_ERROR,
                f"Can't start language server: {e}",
                InitializeError(retry=False),
            ) from e

    def _on_did_change_configuration(self, sender: Any, settings: Dict[str, Any]) -> None:
        pass

    @event
    def on_robot_initialized(sender) -> None:
        ...

    def server_initialized(self, sender: Any) -> None:
        # for folder in self.workspace.workspace_folders:
        #     for p in self.robot_profile.python_path or []:
        #         pa = Path(str(p))
        #         if not pa.is_absolute():
        #             pa = Path(folder.uri.to_path(), pa)
        #
        #         absolute_path = str(pa.absolute())
        #         for f in glob.glob(absolute_path):
        #             if Path(f).is_dir() and f not in sys.path:
        #                 sys.path.insert(0, f)
        #
        # config: RobotConfig = self.workspace.get_configuration(RobotConfig, folder.uri)
        # if config is not None:
        #     if config.env:
        #         for k, v in config.env.items():
        #             os.environ[k] = v
        #
        #     if config.python_path:
        #         for p in config.python_path:
        #             pa = Path(p)
        #             if not pa.is_absolute():
        #                 pa = Path(folder.uri.to_path(), pa)
        #
        #             absolute_path = str(pa.absolute())
        #             for f in glob.glob(absolute_path):
        #                 if Path(f).is_dir() and f not in sys.path:
        #                     sys.path.insert(0, f)

        self.on_robot_initialized(self)
