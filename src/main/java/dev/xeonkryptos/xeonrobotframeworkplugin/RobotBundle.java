package dev.xeonkryptos.xeonrobotframeworkplugin;

import com.intellij.AbstractBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.PropertyKey;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ResourceBundle;

public class RobotBundle {

    @NonNls
    private static final String BUNDLE = "messages.RobotBundle";

    private static Reference<ResourceBundle> instance;

    private RobotBundle() {
    }

    public static String getMessage(@NonNls @PropertyKey(resourceBundle = BUNDLE) String key, Object... params) {
        ResourceBundle bundle = instance != null ? instance.get() : null;
        if (bundle == null) {
            bundle = ResourceBundle.getBundle(BUNDLE);
            instance = new SoftReference<>(bundle);
        }
        return AbstractBundle.message(bundle, key, params);
    }
}
