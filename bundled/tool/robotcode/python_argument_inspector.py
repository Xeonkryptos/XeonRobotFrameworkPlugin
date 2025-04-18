#!/usr/bin/env python
# -*- coding: utf-8 -*-

from importlib import import_module
from inspect import Parameter, signature

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

@click.command(context_settings=dict(ignore_unknown_options=True, allow_extra_args=True))
@click.option("--namespace", required=True)
@click.option("--classname")
@click.option("--functions", multiple=True, required=True)
def analyze(namespace, classname, functions):
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
