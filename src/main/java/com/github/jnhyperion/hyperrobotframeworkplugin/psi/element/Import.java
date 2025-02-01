package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

public interface Import extends RobotStatement {

   boolean isResource();

   boolean isLibrary();

   boolean isVariables();

   String getImportText();
}
