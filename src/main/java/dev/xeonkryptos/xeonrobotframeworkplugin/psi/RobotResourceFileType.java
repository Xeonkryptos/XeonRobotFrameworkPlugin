package dev.xeonkryptos.xeonrobotframeworkplugin.psi;

import dev.xeonkryptos.xeonrobotframeworkplugin.ide.icons.RobotIcons;
import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;

public class RobotResourceFileType extends LanguageFileType {

    private static final RobotResourceFileType INSTANCE = new RobotResourceFileType();

    private RobotResourceFileType() {
        super(RobotLanguage.INSTANCE);
    }

    public static RobotResourceFileType getInstance() {
        return INSTANCE;
    }

    @NotNull
    public String getName() {
        return "Robot Resource";
    }

    @NotNull
    public String getDescription() {
        return "Robot resource files";
    }

    @NotNull
    public String getDefaultExtension() {
        return "resource";
    }

    @Nullable
    public Icon getIcon() {
        return RobotIcons.RESOURCE;
    }

    @NotNull
    public String getDisplayName() {
        return getName();
    }
}
