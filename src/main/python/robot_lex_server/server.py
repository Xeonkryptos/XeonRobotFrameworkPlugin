"""Minimal RobotCode language server exposing lexing tokens.

The server uses RobotCode's ``LanguageServerBase`` infrastructure and registers a
single :class:`LexingProtocol` which forwards ``lexing/tokenize`` requests to the
existing ``robot_lexing`` bridge.
"""

from typing import Optional

# Minimal independent language server for lexing tokens.
# This implementation does **not** depend on RobotCode's language server
# infrastructure. It supports two modes:
#   * stdio – read JSON‑RPC requests from stdin and write responses to stdout
#   * tcp   – listen on a TCP port (default 6610) and handle one request per
#            line per connection.
# The server exposes a single method ``lexing/tokenize`` which forwards the
# request dictionary to the existing ``robot_lexer_bridge.handle_command``.
# A placeholder ``commands/execute`` method is also provided for compatibility.

import argparse
import json
import sys
import socket
import threading
from typing import Any, Dict, Optional

import base64
from robot.parsing import Token
from robot.parsing.lexer.lexer import get_tokens, get_init_tokens, get_resource_tokens

# Build token name mapping
_token_names: dict[str, str] = {}
for _attr in dir(Token):
    if _attr.isupper():
        _value = getattr(Token, _attr)
        if isinstance(_value, str):
            _token_names[_value] = _attr


def _jsonrpc_response(request_id: Any, result: Optional[Dict] = None, error: Optional[Dict] = None) -> str:
    """Create a JSON‑RPC 2.0 response string."""
    resp: Dict[str, Any] = {"jsonrpc": "2.0", "id": request_id}
    if error is not None:
        resp["error"] = error
    else:
        resp["result"] = result
    return json.dumps(resp)


def _build_line_start_offsets(text: str) -> list[int]:
    """Return a list of character offsets where each line starts."""
    starts = [0]
    i = 0
    length = len(text)
    while i < length:
        ch = text[i]
        if ch == "\r":
            if i + 1 < length and text[i + 1] == "\n":
                i += 1
            starts.append(i + 1)
        elif ch == "\n":
            starts.append(i + 1)
        i += 1
    return starts


def _absolute_offset(lineno: int, col_offset: int, line_starts: list[int]) -> int | None:
    if lineno < 1 or lineno > len(line_starts):
        return None
    return line_starts[lineno - 1] + col_offset


def _absolute_span(lineno: int, col_offset: int, end_col_offset: int | None, line_starts: list[int]) -> tuple[int | None, int | None]:
    start = _absolute_offset(lineno, col_offset, line_starts)
    end = _absolute_offset(lineno, end_col_offset, line_starts) if end_col_offset is not None else start
    return start, end


def _normalize_tokens(tokens_iter, line_start_offsets: list[int]):
    """Convert Robot Framework Token objects to the JSON structure expected by clients."""
    results = []
    for token in tokens_iter:
        lineno = getattr(token, "lineno", None)
        col_offset = getattr(token, "col_offset", None)
        end_col_offset = getattr(token, "end_col_offset", None)
        if lineno is None or col_offset is None:
            continue
        start, end = _absolute_span(lineno, col_offset, end_col_offset, line_start_offsets)
        ttype = token.type if hasattr(token, "type") else None
        if start is not None and end is not None and start < end:
            results.append({"start": start, "end": end, "type": _token_names.get(ttype, ttype)})
    return results


def _lexing(params: Dict[str, Any]) -> Dict[str, Any]:
    """Perform lexing based on incoming params.

    Expected keys in ``params``:
        - cmd: "TOKENIZE", "INIT_TOKENS" or "RESOURCE_TOKENS"
        - source: base64‑encoded source string (optional)
        - dataOnly: bool (default False)
        - tokenizeVariables: bool (default True)
    """
    name = str(params.get("cmd", "")).upper()
    source = params.get("source", "")
    if source:
        source = base64.b64decode(source).decode()
    data_only = bool(params.get("dataOnly", False))
    tokenize_vars = bool(params.get("tokenizeVariables", True))
    try:
        if name == "TOKENIZE":
            lexed = get_tokens(source=source, data_only=data_only, tokenize_variables=tokenize_vars)
        elif name == "INIT_TOKENS":
            lexed = get_init_tokens(source=source, data_only=data_only, tokenize_variables=tokenize_vars)
        elif name == "RESOURCE_TOKENS":
            lexed = get_resource_tokens(source=source, data_only=data_only, tokenize_variables=tokenize_vars)
        else:
            return {"ok": False, "cmd": name, "error": f"Unknown command: {name}"}
        line_offsets = _build_line_start_offsets(source)
        tokens = _normalize_tokens(lexed, line_offsets)
        return {"ok": True, "cmd": name, "tokens": tokens}
    except Exception as e:
        return {"ok": False, "cmd": name, "error": str(e)}

def _handle_request(line: str) -> str:
    """Process a single JSON‑RPC request line and return the response string."""
    try:
        request = json.loads(line)
        method = request.get("method")
        params = request.get("params", {})
        request_id = request.get("id")
        if method == "lexing/tokenize":
            result = _lexing(params)
            return _jsonrpc_response(request_id, result=result)
        elif method == "commands/execute":
            return _jsonrpc_response(
                request_id,
                result={"ok": True, "cmd": params.get("cmd", "UNKNOWN"), "message": "Command endpoint not implemented"},
            )
        else:
            return _jsonrpc_response(
                request_id,
                error={"code": -32601, "message": f"Method {method!r} not found"},
            )
    except json.JSONDecodeError as e:
        return _jsonrpc_response(None, error={"code": -32700, "message": f"Parse error: {e}"})
    except Exception as e:
        return _jsonrpc_response(None, error={"code": -32603, "message": str(e)})


def run_stdio() -> None:
    """Run the server using standard input/output streams."""
    for line in sys.stdin:
        line = line.strip()
        if not line:
            continue
        response = _handle_request(line)
        print(response)
        sys.stdout.flush()


class _TCPHandler(threading.Thread):
    def __init__(self, conn: socket.socket, addr):
        super().__init__(daemon=True)
        self.conn = conn
        self.addr = addr

    def run(self) -> None:
        with self.conn:
            buffer = b""
            while True:
                data = self.conn.recv(4096)
                if not data:
                    break
                buffer += data
                while b"\n" in buffer:
                    line, buffer = buffer.split(b"\n", 1)
                    response = _handle_request(line.decode())
                    self.conn.sendall(response.encode() + b"\n")


def run_tcp(port: int = 6610) -> None:
    """Run the server listening on the given TCP port."""
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as sock:
        sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        sock.bind(("", port))
        sock.listen()
        print(f"Lexing language server listening on port {port}")
        while True:
            conn, addr = sock.accept()
            handler = _TCPHandler(conn, addr)
            handler.start()


def main() -> None:
    parser = argparse.ArgumentParser(description="Minimal RobotFramework lexing language server")
    group = parser.add_mutually_exclusive_group()
    group.add_argument("--stdio", action="store_true", help="Use standard I/O (default)")
    group.add_argument("--tcp", type=int, metavar="PORT", help="Listen on TCP port (default 6610)")
    args = parser.parse_args()

    if args.tcp:
        run_tcp(args.tcp)
    else:
        run_stdio()


if __name__ == "__main__":
    main()

