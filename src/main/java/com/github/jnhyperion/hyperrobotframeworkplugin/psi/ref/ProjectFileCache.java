package com.github.jnhyperion.hyperrobotframeworkplugin.psi.ref;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.DefinedKeyword;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.DefinedVariable;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

   public static Map<String, PsiElement> getCachedElements(Project project) {
      return getCache(project).ELEMENT_CACHE;
   }

   public static Map<String, PsiFile> getCachedFiles(Project project) {
      return getCache(project).FILE_CACHE;
   }

   public static Collection<DefinedVariable> getGlobalVariables(Project project) {
      return getCache(project).GLOBAL_VARIABLES_CACHE;
   }

   public static Map<String, Collection<DefinedVariable>> getCachedVariables(Project project) {
      return getCache(project).VARIABLE_CACHE;
   }

   private final static class Cache {

      final HashMap<String, PsiElement> ELEMENT_CACHE = new HashMap<>();
      final HashMap<String, PsiFile> FILE_CACHE = new HashMap<>();
      final Collection<DefinedVariable> GLOBAL_VARIABLES_CACHE = new LinkedHashSet<>();
      final ConcurrentHashMap<String, Collection<DefinedVariable>> VARIABLE_CACHE = new ConcurrentHashMap<>();

      private Cache() {
      }
   }
}
