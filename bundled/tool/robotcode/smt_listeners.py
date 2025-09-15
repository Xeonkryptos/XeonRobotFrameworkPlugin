# For Listener API version 3
from typing import Dict, Any


def extract_ids(attributes: Dict[str, Any]):
    current_id = attributes.get('id', '0')
    children_id_start_index = current_id.rfind('-')
    parent_id = current_id[:children_id_start_index] if children_id_start_index != -1 else '0'
    return parent_id, current_id


class RobotIntellijListener:
    ROBOT_LISTENER_API_VERSION = "2"

    def __init__(self):
        self.keyword_parent_id = 's1-t1'
        self.keyword_id = 1
        self.tests_count = 0

    def start_suite(self, name: str, attributes: Dict[str, Any]):
        location = f"file://{attributes['source']}"
        parent_id, current_id = extract_ids(attributes)
        self.tests_count = attributes.get('totaltests', 0)
        print_teamcity_message(f"testSuiteStarted name='{escape_name(name)}' nodeId='{current_id}' parentNodeId='{parent_id}' locationHint='{location}'")
        print_teamcity_message(f"testCount count='{self.tests_count}'")

    def end_suite(self, name: str, attributes: Dict[str, Any]):
        parent_id, current_id = extract_ids(attributes)
        print_teamcity_message(f"testSuiteFinished name='{escape_name(name)}' nodeId='{current_id}' parentNodeId='{parent_id}'")

    def start_test(self, name: str, attributes: Dict[str, Any]):
        self.keyword_parent_id = attributes['id']
        location = f"file://{attributes['source']}:{attributes['lineno']}"
        parent_id, current_id = extract_ids(attributes)
        print_teamcity_message(f"testStarted name='{escape_name(name)}' nodeId='{current_id}' parentNodeId='{parent_id}' locationHint='{location}'")

    def end_test(self, name: str, attributes: Dict[str, Any]):
        duration = int(attributes['elapsedtime'])
        parent_id, current_id = extract_ids(attributes)
        teamcity_name = escape_name(name)
        if attributes['status'] == 'FAIL':
            message = escape_message(attributes.get('message', ''))
            print_teamcity_message(f"testFailed name='{teamcity_name}' nodeId='{current_id}' parentNodeId='{parent_id}' message='{message}'")
        elif attributes['status'] == 'SKIP':
            print_teamcity_message(f"testIgnored name='{teamcity_name}' nodeId='{current_id}' parentNodeId='{parent_id}'")
        print_teamcity_message(f"testFinished name='{teamcity_name}' nodeId='{current_id}' parentNodeId='{parent_id}' duration='{duration}'")

        self.keyword_parent_id = 's1-t1'
        self.keyword_id = 1

    def start_keyword(self, name: str, attributes: Dict[str, Any]):
        if attributes['type'] == 'KEYWORD':
            location = f"file://{attributes['source']}:{attributes['lineno']}"
            print_teamcity_message(
                f"testSuiteStarted name='{escape_name(attributes['kwname'])}' nodeId='{self.keyword_parent_id}-k{self.keyword_id}' parentNodeId='{self.keyword_parent_id}' locationHint='{location}'")

    def end_keyword(self, name: str, attributes: Dict[str, Any]):
        if attributes['type'] == 'KEYWORD':
            teamcity_name = escape_name(attributes['kwname'])
            node_id = f"{self.keyword_parent_id}-k{self.keyword_id}"
            print_teamcity_message(
                f"testSuiteStarted name='{teamcity_name}' nodeId='{node_id}' parentNodeId='{self.keyword_parent_id}' metainfo='status={attributes['status']}'")
            print_teamcity_message(f"testSuiteFinished name='{teamcity_name}' nodeId='{node_id}' parentNodeId='{self.keyword_parent_id}'")
            self.keyword_id += 1

def escape_name(name):
    # Escape special characters for TeamCity service messages
    return name.replace('|', '||').replace("'", "|'").replace("\n", "|n").replace("\r", "|r")


def escape_message(message):
    return escape_name(message).replace('[', '|[').replace(']', '|]')


def print_teamcity_message(message):
    print(f"##teamcity[{message}]")
