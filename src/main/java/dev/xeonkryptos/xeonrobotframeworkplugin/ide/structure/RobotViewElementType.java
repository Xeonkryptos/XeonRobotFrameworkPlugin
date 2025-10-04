package dev.xeonkryptos.xeonrobotframeworkplugin.ide.structure;

import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.icons.RobotIcons;
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
            return RobotBundle.message("action.structureView.show.files");
        }
    }, Section {
        @Override
        protected Icon getIcon(@Nullable PsiElement element) {
            return RobotIcons.MODELS;
        }

        @NotNull
        @Override
        protected String getMessage() {
            return RobotBundle.message("action.structureView.show.headings");
        }
    }, Settings {
        @Override
        protected Icon getIcon(@Nullable PsiElement element) {
            return RobotIcons.CONTROLLER;
        }

        @NotNull
        @Override
        protected String getMessage() {
            return RobotBundle.message("action.structureView.show.settings");
        }
    }, TestCase {
        @Override
        protected Icon getIcon(@Nullable PsiElement element) {
            return RobotIcons.JUNIT;
        }

        @NotNull
        @Override
        protected String getMessage() {
            return RobotBundle.message("action.structureView.show.testCases");
        }
    }, Task {
        @Override
        protected Icon getIcon(@Nullable PsiElement element) {
            return RobotIcons.APPLICATION;
        }

        @NotNull
        @Override
        protected String getMessage() {
            return RobotBundle.message("action.structureView.show.tasks");
        }
    }, Keyword {
        @Override
        protected Icon getIcon(@Nullable PsiElement element) {
            return RobotIcons.FUNCTION;
        }

        @NotNull
        @Override
        protected String getMessage() {
            return RobotBundle.message("action.structureView.show.keywords");
        }
    }, Variable {
        @Override
        protected Icon getIcon(@Nullable PsiElement element) {
            return RobotIcons.VARIABLE;
        }

        @NotNull
        @Override
        protected String getMessage() {
            return RobotBundle.message("action.structureView.show.variables");
        }
    };

    @Nullable
    protected abstract Icon getIcon(@Nullable PsiElement element);

    @NotNull
    protected abstract String getMessage();
}
