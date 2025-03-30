# For Listener API version 3
class RobotIntellijListener:
    ROBOT_LISTENER_API_VERSION = "3"

    def start_suite(self, data, result):
        location = f"file://{data.source}"
        parent_id = data.parent.id if not data.parent is None else '0'
        print_teamcity_message(f"testSuiteStarted name='{escape_name(data.name)}' nodeId='{data.id}' parentNodeId='{parent_id}' locationHint='{location}'")

    def end_suite(self, data, result):
        location = f"file://{data.source}"
        duration = int(result.elapsedtime)
        parent_id = data.parent.id if not data.parent is None else '0'
        print_teamcity_message(f"testSuiteFinished name='{escape_name(data.name)}' nodeId='{data.id}' parentNodeId='{parent_id}' locationHint='{location}' duration='{duration}'")

    def start_test(self, data, result):
        location = f"file://{data.source}:{data.lineno}"
        print_teamcity_message(f"testStarted name='{escape_name(data.name)}' nodeId='{data.id}' parentNodeId='{data.parent.id}' locationHint='{location}'")

    def end_test(self, data, result):
        location = f"file://{data.source}:{data.lineno}"
        duration = int(result.elapsedtime)
        if result.status == 'FAIL':
            message = escape_message(result.message)
            print_teamcity_message(f"testFailed name='{escape_name(data.name)}' nodeId='{data.id}' parentNodeId='{data.parent.id}' locationHint='{location}' message='{message}'")
        elif result.status == 'SKIP':
            print_teamcity_message(f"testIgnored name='{escape_name(data.name)}' nodeId='{data.id}' parentNodeId='{data.parent.id}' locationHint='{location}'")
        print_teamcity_message(f"testFinished name='{escape_name(data.name)}' nodeId='{data.id}' parentNodeId='{data.parent.id}' locationHint='{location}' duration='{duration}'")

def escape_name(name):
    # Escape special characters for TeamCity service messages
    return name.replace('|', '||').replace("'", "|'").replace("\n", "|n").replace("\r", "|r")

def escape_message(message):
    return escape_name(message).replace('[', '|[').replace(']', '|]')

def print_teamcity_message(message):
    print(f"##teamcity[{message}]\n")
