package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.dto.ImportType;

public interface Import extends RobotStatement {

   boolean isResource();

   boolean isLibrary();

   boolean isVariables();

   ImportType getImportType();

   String getImportText();
}
