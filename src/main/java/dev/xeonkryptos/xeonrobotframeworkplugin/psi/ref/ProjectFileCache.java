package dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedVariable;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class ProjectFileCache {

   private static final Map<Project, Cache> CACHE = new HashMap<>();

   private static synchronized Cache getCache(Project project) {
       Cache cache = CACHE.get(project);
       if (cache == null) {
           cache = new Cache();
           CACHE.put(project, cache);
       }
       return cache;
   }

   public static Map<String, PsiFile> getCachedRobotSystemFiles(Project project) {
      return getCache(project).ROBOT_SYSTEM_FILE_CACHE;
   }

   public static Collection<DefinedVariable> getGlobalVariables(Project project) {
      return getCache(project).GLOBAL_VARIABLES_CACHE;
   }

    public static void clearProjectCache(Project project) {
        Cache cache = CACHE.get(project);
        if (cache != null) {
            cache.ROBOT_SYSTEM_FILE_CACHE.clear();
            synchronized (cache.GLOBAL_VARIABLES_CACHE) {
                cache.GLOBAL_VARIABLES_CACHE.clear();
            }
        }
    }

   protected final static class Cache {

      final Map<String, PsiFile> ROBOT_SYSTEM_FILE_CACHE = new HashMap<>();
      final Collection<DefinedVariable> GLOBAL_VARIABLES_CACHE = new LinkedHashSet<>();

      private Cache() {
      }
   }
}
