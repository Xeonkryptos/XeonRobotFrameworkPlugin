package dev.xeonkryptos.xeonrobotframeworkplugin.psi.reference;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProjectFileCache {

   private static final Map<Project, Cache> CACHE = new ConcurrentHashMap<>();

   private static Cache getCache(Project project) {
       return CACHE.computeIfAbsent(project, k -> new Cache());
   }

   public static Map<String, PsiFile> getCachedRobotSystemFiles(Project project) {
      return getCache(project).ROBOT_SYSTEM_FILE_CACHE;
   }

   protected final static class Cache {

      final Map<String, PsiFile> ROBOT_SYSTEM_FILE_CACHE = new HashMap<>();

      private Cache() {
      }
   }
}
