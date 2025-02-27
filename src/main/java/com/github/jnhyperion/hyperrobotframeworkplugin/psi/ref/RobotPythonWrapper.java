package com.github.jnhyperion.hyperrobotframeworkplugin.psi.ref;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.dto.KeywordDto;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.dto.VariableDto;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.DefinedKeyword;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.DefinedVariable;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.util.ReservedVariable;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.util.ReservedVariableScope;
import com.jetbrains.python.psi.Property;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyDecorator;
import com.jetbrains.python.psi.PyDecoratorList;
import com.jetbrains.python.psi.PyExpression;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.PyTargetExpression;
import com.jetbrains.python.psi.types.TypeEvalContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map.Entry;

public abstract class RobotPythonWrapper {

    protected static String getValidName(@Nullable String name) {
        return name != null && isNotReservedName(name) ? name : null;
    }

    protected static void addDefinedVariables(@NotNull PyClass pyClass, @NotNull Collection<DefinedVariable> definedVariables) {
        TypeEvalContext context = TypeEvalContext.deepCodeInsight(pyClass.getProject());
        ArrayList<PyTargetExpression> attributes = new ArrayList<>(pyClass.getClassAttributes());
        attributes.addAll(pyClass.getInstanceAttributes());

        for (PyTargetExpression attribute : attributes) {
            String attributeName = attribute.getName();
            if (attributeName != null && isNotReservedName(attributeName)) {
                definedVariables.add(new VariableDto(attribute, ReservedVariable.wrapToScalar(attributeName), ReservedVariableScope.Global));
            }
        }

        for (Entry<String, Property> entry : pyClass.getProperties().entrySet()) {
            String propertyName = entry.getKey();
            PyFunction propertyFunction = pyClass.findMethodByName(propertyName, false, context);
            if (propertyName != null && isNotReservedName(propertyName) && propertyFunction != null) {
                definedVariables.add(new VariableDto(propertyFunction, ReservedVariable.wrapToScalar(propertyName), ReservedVariableScope.Global));
            }
        }

        for (PyClass superClass : pyClass.getSuperClasses(context)) {
            String superClassName = superClass.getName();
            if (superClassName != null && !superClassName.equals("object")) {
                addDefinedVariables(superClass, definedVariables);
            }
        }
    }

    private static boolean isNotReservedName(@NotNull String name) {
        return !name.startsWith("_") && !name.startsWith("ROBOT_LIBRARY_");
    }

    @Nullable
    protected static String getKeywordName(@NotNull PyFunction function) {
        String keywordName = null;

        try {
            PyDecoratorList decoratorList = function.getDecoratorList();
            if (decoratorList != null) {
                PyDecorator keywordDecorator = decoratorList.findDecorator("keyword");
                if (keywordDecorator != null && keywordDecorator.hasArgumentList()) {
                    PyExpression nameArgument = keywordDecorator.getKeywordArgument("name");
                    if (nameArgument != null) {
                        keywordName = nameArgument.getText().replaceAll("^\"|\"|'|'$", "");
                    } else {
                        PyExpression[] arguments = keywordDecorator.getArguments();
                        if (arguments.length > 0 && arguments[0].getName() == null) {
                            keywordName = arguments[0].getText().replaceAll("^\"|\"|'|'$", "");
                        }
                    }
                }
            }
        } catch (Throwable ignored) {
        }

        return keywordName;
    }

    protected static void addDefinedKeywords(@NotNull PyClass pyClass, @NotNull String namespace, @NotNull Collection<DefinedKeyword> keywords) {
        pyClass.visitMethods(method -> {
            String methodName = getValidName(method.getName());
            if (methodName != null) {
                String keywordName = getKeywordName(method);
                if (keywordName != null) {
                    methodName = keywordName;
                }
                keywords.add(new KeywordDto(method, namespace, methodName, Arrays.asList(method.getParameterList().getParameters())));
            }
            return true;
        }, true, null);

        pyClass.visitClassAttributes(attribute -> {
            String attributeName = getValidName(attribute.getName());
            if (attributeName != null) {
                keywords.add(new KeywordDto(attribute, namespace, attributeName));
            }
            return true;
        }, true, null);
    }
}
