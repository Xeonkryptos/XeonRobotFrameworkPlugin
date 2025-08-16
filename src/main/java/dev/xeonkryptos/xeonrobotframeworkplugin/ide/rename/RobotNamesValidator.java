package dev.xeonkryptos.xeonrobotframeworkplugin.ide.rename;

import com.intellij.lang.refactoring.NamesValidator;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class RobotNamesValidator implements NamesValidator {

    private static final Set<String> ROBOT_IDENTIFIERS = Set.of("IF",
                                                                "ELSE IF",
                                                                "ELSE",
                                                                "FOR",
                                                                "IN",
                                                                "IN RANGE",
                                                                "IN ENUMERATE",
                                                                "IN ZIP",
                                                                "WHILE",
                                                                "TRY",
                                                                "EXCEPT",
                                                                "FINALLY",
                                                                "RETURN",
                                                                "BREAK",
                                                                "CONTINUE",
                                                                "END",
                                                                "GIVEN",
                                                                "WHEN",
                                                                "THEN",
                                                                "AND",
                                                                "GROUP",
                                                                "VAR");

    @Override
    public boolean isKeyword(@NotNull String name, Project project) {
        return false;
    }

    @Override
    public boolean isIdentifier(@NotNull String name, Project project) {
        return !ROBOT_IDENTIFIERS.contains(name.toUpperCase());
    }
}
