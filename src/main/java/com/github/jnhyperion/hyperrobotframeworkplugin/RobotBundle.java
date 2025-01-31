package com.github.jnhyperion.hyperrobotframeworkplugin;

import com.intellij.AbstractBundle;
import com.intellij.reference.SoftReference;
import java.lang.ref.Reference;
import java.util.ResourceBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.PropertyKey;

public class RobotBundle {

   @NonNls
   private static final String BUNDLE = "messages.RobotBundle";

   private static Reference<ResourceBundle> instance;

   private RobotBundle() {
   }

   public static String getMessage(@NonNls @PropertyKey(resourceBundle = BUNDLE) String key, Object... params) {
      ResourceBundle bundle = SoftReference.dereference(instance);
      if (bundle == null) {
         bundle = ResourceBundle.getBundle(BUNDLE);
         instance = new SoftReference<>(bundle);
      }
      return AbstractBundle.message(bundle, key, params);
   }
}
