package com.github.jnhyperion.hyperrobotframeworkplugin.ide.structure;

import com.github.jnhyperion.hyperrobotframeworkplugin.RobotBundle;
import com.github.jnhyperion.hyperrobotframeworkplugin.ide.icons.RobotIcons;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;

public enum RobotViewElementType {
    File {
        @Nullable
        @Override
        protected Icon getIcon(@Nullable PsiElement element) {
            return element == null ? null : element.getIcon(0);
        }

        @NotNull
        @Override
        protected String getMessage() {
            return RobotBundle.getMessage("action.structureView.show.files");
        }
    }, Heading {
        @Override
        protected Icon getIcon(@Nullable PsiElement element) {
            return RobotIcons.MODELS;
        }

        @NotNull
        @Override
        protected String getMessage() {
            return RobotBundle.getMessage("action.structureView.show.headings");
        }
    }, Settings {
        @Override
        protected Icon getIcon(@Nullable PsiElement element) {
            return RobotIcons.CONTROLLER;
        }

        @NotNull
        @Override
        protected String getMessage() {
            return RobotBundle.getMessage("action.structureView.show.settings");
        }
    }, TestCase {
        @Override
        protected Icon getIcon(@Nullable PsiElement element) {
            return RobotIcons.JUNIT;
        }

        @NotNull
        @Override
        protected String getMessage() {
            return RobotBundle.getMessage("action.structureView.show.testCases");
        }
    }, Keyword {
        @Override
        protected Icon getIcon(@Nullable PsiElement element) {
            return RobotIcons.FUNCTION;
        }

        @NotNull
        @Override
        protected String getMessage() {
            return RobotBundle.getMessage("action.structureView.show.keywords");
        }
    }, Variable {
        @Override
        protected Icon getIcon(@Nullable PsiElement element) {
            return RobotIcons.VARIABLE;
        }

        @NotNull
        @Override
        protected String getMessage() {
            return RobotBundle.getMessage("action.structureView.show.variables");
        }
    };

    @Nullable
    protected abstract Icon getIcon(@Nullable PsiElement var1);

    @NotNull
    protected abstract String getMessage();
}
