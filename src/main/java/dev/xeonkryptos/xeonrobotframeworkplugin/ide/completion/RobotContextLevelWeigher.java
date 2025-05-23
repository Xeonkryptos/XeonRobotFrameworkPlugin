package dev.xeonkryptos.xeonrobotframeworkplugin.ide.completion;

import com.intellij.codeInsight.completion.CompletionLocation;
import com.intellij.codeInsight.completion.CompletionWeigher;
import com.intellij.codeInsight.lookup.LookupElement;
import org.jetbrains.annotations.NotNull;

public class RobotContextLevelWeigher extends CompletionWeigher {

    private static final int PROJECT_SCOPE_WEIGHT = 1_050;
    private static final int LIBRARY_SCOPE_WEIGHT = 1_025;
    private static final int PARAMETER_WEIGHT = 1_000;
    private static final int VARIABLE_WEIGHT = 900;
    private static final int KEYWORDS_WEIGHT = 800;

    @Override
    @SuppressWarnings("rawtypes")
    public Comparable weigh(@NotNull LookupElement element, @NotNull CompletionLocation location) {
        RobotLookupContext lookupContext = element.getUserData(CompletionKeys.ROBOT_LOOKUP_CONTEXT);
        if (lookupContext == RobotLookupContext.WITHIN_KEYWORD_STATEMENT) {
            RobotLookupElementType lookupElementType = element.getUserData(CompletionKeys.ROBOT_LOOKUP_ELEMENT_TYPE);
            if (lookupElementType == RobotLookupElementType.PARAMETER || lookupElementType == RobotLookupElementType.ARGUMENT) {
                return PARAMETER_WEIGHT;
            }
            return VARIABLE_WEIGHT;
        }
        if (lookupContext == RobotLookupContext.KEYWORDS) {
            return KEYWORDS_WEIGHT;
        }
        if (lookupContext == RobotLookupContext.IMPORT) {
            RobotLookupScope scope = element.getUserData(CompletionKeys.ROBOT_LOOKUP_SCOPE);
            if (scope == RobotLookupScope.PROJECT_SCOPE) {
                return PROJECT_SCOPE_WEIGHT;
            }
            return LIBRARY_SCOPE_WEIGHT;
        }
        return Integer.MIN_VALUE;
    }
}
