package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ImportType;

public interface Import extends RobotStatement {

   boolean isResource();

   boolean isLibrary();

   boolean isVariables();

   ImportType getImportType();

   String getImportText();
}
