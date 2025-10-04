package dev.xeonkryptos.xeonrobotframeworkplugin;

import com.intellij.DynamicBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

import java.lang.ref.Reference;
import java.util.ResourceBundle;

public final class RobotBundle extends DynamicBundle {

    @NonNls
    private static final String BUNDLE = "messages.RobotBundle";
    private static final RobotBundle INSTANCE = new RobotBundle();

    private RobotBundle() {
        super(BUNDLE);
    }

    public static @NotNull @Nls String message(@NonNls @PropertyKey(resourceBundle = BUNDLE) String key, Object... params) {
        return INSTANCE.getMessage(key, params);
    }
}
