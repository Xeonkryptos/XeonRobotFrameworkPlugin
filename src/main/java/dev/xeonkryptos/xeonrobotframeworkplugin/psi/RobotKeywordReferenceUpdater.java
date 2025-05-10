package dev.xeonkryptos.xeonrobotframeworkplugin.psi;

import dev.xeonkryptos.xeonrobotframeworkplugin.MyLogger;
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SimpleModificationTracker;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiTreeChangeAdapter;
import com.intellij.psi.PsiTreeChangeEvent;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.concurrency.AppExecutorUtil;
import com.jetbrains.python.psi.PyDecorator;
import com.jetbrains.python.psi.PyDecoratorList;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.PyParameter;
import com.jetbrains.python.psi.PyParameterList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class RobotKeywordReferenceUpdater extends PsiTreeChangeAdapter {

    private final SimpleModificationTracker simpleModificationTracker = new SimpleModificationTracker();

    @Override
    public void childAdded(@NotNull PsiTreeChangeEvent event) {
        processPythonFunctionChange(event);
    }

    @Override
    public void childRemoved(@NotNull PsiTreeChangeEvent event) {
        processPythonFunctionChange(event);
    }

    @Override
    public void childReplaced(@NotNull PsiTreeChangeEvent event) {
        processPythonFunctionChange(event);
    }

    @Override
    public void childrenChanged(@NotNull PsiTreeChangeEvent event) {
        processPythonFunctionChange(event);
    }

    @SuppressWarnings("UnstableApiUsage")
    private void processPythonFunctionChange(@NotNull PsiTreeChangeEvent event) {
        PyFunction pyFunction = findAffectedPythonFunction(event);
        if (pyFunction != null) {
            if (MyLogger.logger.isDebugEnabled()) {
                MyLogger.logger.debug("Python function change detected: " + pyFunction.getName());
            }
            if (pyFunction.isValid()) {
                restartAnnotatorForReferencingKeywords(pyFunction);
            } else {
                PsiFile file = event.getFile();
                if (file != null) {
                    Project project = file.getProject();
                    restartAnnotatorForAllRobotFiles(project);
                }
            }
        }
    }

    @Nullable
    private PyFunction findAffectedPythonFunction(@NotNull PsiTreeChangeEvent event) {
        PsiElement element = event.getElement();
        if (element instanceof PyFunction pyFunction) {
            return pyFunction;
        }
        PsiElement child = event.getChild();
        if (child instanceof PyFunction pyFunction) {
            return pyFunction;
        }
        PyFunction parentFunction = findParentPyFunction(element);
        if (parentFunction != null) {
            return parentFunction;
        }
        if (child != null) {
            parentFunction = findParentPyFunction(child);
            if (parentFunction != null) {
                return parentFunction;
            }
        }
        PsiElement oldChild = event.getOldChild();
        if (oldChild != null) {
            parentFunction = findParentPyFunction(oldChild);
            if (parentFunction != null) {
                return parentFunction;
            }
        }
        PsiElement newChild = event.getNewChild();
        if (newChild != null) {
            return findParentPyFunction(newChild);
        }
        return null;
    }

    @Nullable
    private PyFunction findParentPyFunction(PsiElement element) {
        while (element != null && !(element instanceof PsiFile)) {
            if (element instanceof PyFunction pyFunction) {
                return pyFunction;
            }

            if (element instanceof PyDecorator || element instanceof PyParameter || element instanceof PyParameterList || element instanceof PyDecoratorList) {
                PsiElement parent = element.getParent();
                while (parent != null && !(parent instanceof PsiFile)) {
                    if (parent instanceof PyFunction pyFunction) {
                        return pyFunction;
                    }
                    parent = parent.getParent();
                }
            }

            element = element.getParent();
        }
        return null;
    }

    private void restartAnnotatorForReferencingKeywords(PyFunction pythonFunction) {
        simpleModificationTracker.incModificationCount();
        final long currentModificationCount = simpleModificationTracker.getModificationCount();
        ReadAction.nonBlocking(() -> {
                      if (pythonFunction.isValid()) {
                          Set<PsiFile> processedFiles = new HashSet<>();
                          ProgressManager.checkCanceled();
                          ReferencesSearch.search(pythonFunction, GlobalSearchScope.projectScope(pythonFunction.getProject())).forEach(reference -> {
                              ProgressManager.checkCanceled();
                              PsiElement element = reference.getElement();
                              PsiFile robotFile = element.getContainingFile();
                              if (robotFile != null) {
                                  processedFiles.add(robotFile);
                              }
                              return true;
                          });
                          processedFiles.forEach(robotFile -> DaemonCodeAnalyzer.getInstance(robotFile.getProject()).restart(robotFile));
                      }
                      return null;
                  })
                  .inSmartMode(pythonFunction.getProject())
                  .expireWhen(() -> currentModificationCount != simpleModificationTracker.getModificationCount())
                  .submit(AppExecutorUtil.getAppExecutorService());
    }

    private void restartAnnotatorForAllRobotFiles(@NotNull Project project) {
        long currentModificationCount = simpleModificationTracker.getModificationCount();
        ReadAction.nonBlocking(() -> {
                      ProgressManager.checkCanceled();

                      GlobalSearchScope projectScope = GlobalSearchScope.projectScope(project);

                      Collection<VirtualFile> files = FileTypeIndex.getFiles(RobotResourceFileType.getInstance(), projectScope);
                      Set<VirtualFile> robotFiles = new HashSet<>(files);

                      files = FileTypeIndex.getFiles(RobotFeatureFileType.getInstance(), projectScope);
                      robotFiles.addAll(files);

                      for (VirtualFile file : robotFiles) {
                          ProgressManager.checkCanceled();
                          PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
                          if (psiFile != null) {
                              DaemonCodeAnalyzer.getInstance(project).restart(psiFile);
                          }
                      }
                      return null;
                  })
                  .inSmartMode(project)
                  .expireWhen(() -> currentModificationCount != simpleModificationTracker.getModificationCount() || project.isDisposed())
                  .submit(AppExecutorUtil.getAppExecutorService());
    }
}
