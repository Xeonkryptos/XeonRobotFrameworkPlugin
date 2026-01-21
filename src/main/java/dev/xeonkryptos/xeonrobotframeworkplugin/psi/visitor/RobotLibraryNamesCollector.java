package dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor;

import com.intellij.psi.PsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotImportArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLibraryImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotNewLibraryName;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotRoot;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSettingsSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

public final class RobotLibraryNamesCollector extends RobotVisitor {

    private final Map<String, PsiElement> renamedLibraries = new LinkedHashMap<>();

    @Override
    public void visitRoot(@NotNull RobotRoot o) {
        o.acceptChildren(this);
    }

    @Override
    public void visitSettingsSection(@NotNull RobotSettingsSection o) {
        o.acceptChildren(this);
    }

    @Override
    public void visitLibraryImportGlobalSetting(@NotNull RobotLibraryImportGlobalSetting o) {
        RobotImportArgument importArgument = o.getImportedFile();
        if (importArgument != null) {
            importArgument.acceptChildren(this);

            RobotNewLibraryName newLibraryName = o.getNewLibraryName();
            if (newLibraryName != null) {
                String libraryName = newLibraryName.getText();
                renamedLibraries.put(libraryName, newLibraryName);
            }
        }
    }

    public Map<String, PsiElement> getRenamedLibraries() {
        return renamedLibraries;
    }
}
