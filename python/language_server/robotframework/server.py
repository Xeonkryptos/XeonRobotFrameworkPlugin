from typing import Optional

from robotcode.core.types import ServerMode, TcpParams

from language_server.common.server import (
    TCP_DEFAULT_PORT,
    LanguageServerBase,
)
from .protocol import RobotLanguageServerProtocol


class RobotLanguageServer(LanguageServerBase[RobotLanguageServerProtocol]):
    def __init__(
        self,
        mode: ServerMode = ServerMode.STDIO,
        tcp_params: TcpParams = TcpParams(None, TCP_DEFAULT_PORT),
        pipe_name: Optional[str] = None,
    ):
        super().__init__(mode, tcp_params, pipe_name)

    def create_protocol(self) -> RobotLanguageServerProtocol:
        return RobotLanguageServerProtocol(self)
