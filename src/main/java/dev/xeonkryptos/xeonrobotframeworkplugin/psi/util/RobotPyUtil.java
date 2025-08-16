package dev.xeonkryptos.xeonrobotframeworkplugin.psi.util;

import com.jetbrains.python.psi.PyDecorator;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.PyStringLiteralExpression;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class RobotPyUtil {

    @SuppressWarnings("UnstableApiUsage")
    public static Optional<PyStringLiteralExpression> findCustomKeywordNameDecoratorExpression(@NotNull PyFunction pyFunction) {
        return findCustomKeywordDecorator(pyFunction).map(decorator -> decorator.getArgument(0, "name", PyStringLiteralExpression.class));
    }

    public static Optional<PyDecorator> findCustomKeywordDecorator(@NotNull PyFunction pyFunction) {
        return Optional.ofNullable(pyFunction.getDecoratorList()).map(decoratorList -> decoratorList.findDecorator("keyword"));
    }
}
