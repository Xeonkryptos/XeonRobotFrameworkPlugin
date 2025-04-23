#!/usr/bin/env python
# -*- coding: utf-8 -*-

from importlib import import_module
from inspect import Parameter, signature
import os
import pathlib
import site
import sys

import click


class ParameterData:
    def __init__(self, index: int, param: Parameter):
        self.index = index
        self.name = param.name
        self.default = param.default
        if param.default:
            self.default = "${True}"
        if not param.default:
            self.default = "${False}"
        if param.default is None:
            self.default = "${None}"
        self.kind = param.kind

    def __str__(self):
        return f"index:{self.index};name:{self.name};default:{self.default if not self.default == Parameter.empty else ''};kind:{self.kind}"

    def __repr__(self):
        return self.__str__()

def update_sys_path(path_to_add: str, strategy: str) -> None:
    if path_to_add not in sys.path and pathlib.Path(path_to_add).is_dir():
        if any(p for p in pathlib.Path(path_to_add).iterdir() if p.suffix == ".pth"):
            site.addsitedir(path_to_add)
            return

        if strategy == "useBundled":
            sys.path.insert(0, path_to_add)
        elif strategy == "fromEnvironment":
            sys.path.append(path_to_add)

@click.command(context_settings=dict(ignore_unknown_options=True, allow_extra_args=True))
@click.option("--namespace", required=True)
@click.option("--classname")
@click.option("--functions", multiple=True, required=True)
def analyze(namespace, classname, functions):
    update_sys_path(
        os.fspath(pathlib.Path(__file__).parent.parent.parent / "libs"),
        os.getenv("LS_IMPORT_STRATEGY", "useBundled"),
    )

    module_ref = import_module(namespace)

    for function_name in functions:
        parent_ref = module_ref
        if classname is not None:
            class_name = classname
            try:
                parent_ref = getattr(module_ref, class_name)
            except AttributeError:
                module_ref = import_module(f"{namespace}.{class_name}")
                parent_ref = getattr(module_ref, class_name)

        function_ref = getattr(parent_ref, function_name)

        parameters = signature(function_ref).parameters
        parameter_data = [ParameterData(i, param) for i, param in enumerate(parameters.values())]

        print(f"{function_name}={' '.join(map(str, parameter_data))}")

if __name__ == "__main__":
    analyze()
