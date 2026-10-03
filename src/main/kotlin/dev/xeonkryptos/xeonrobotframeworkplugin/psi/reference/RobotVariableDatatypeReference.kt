package dev.xeonkryptos.xeonrobotframeworkplugin.psi.reference

import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.PsiPolyVariantReferenceBase
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.search.DelegatingGlobalSearchScope
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.GlobalSearchScopesCore
import com.jetbrains.python.psi.stubs.PyClassNameIndex
import com.jetbrains.python.sdk.PythonSdkUtil
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDatatype

class RobotVariableDatatypeReference(element: RobotVariableDatatype) : PsiPolyVariantReferenceBase<RobotVariableDatatype>(element) {

    override fun multiResolve(incompleteCode: Boolean): Array<out ResolveResult> {
        val resolveCache = ResolveCache.getInstance(element.project)
        return resolveCache.resolveWithCaching(this, { reference, _ ->
            val variableDatatype = reference.element
            val datatype = mapToSupportedType(variableDatatype.text)

            val project = variableDatatype.project
            val sdk: Sdk = ModuleUtilCore.findModuleForPsiElement(element)?.let { PythonSdkUtil.findPythonSdk(it) }
                ?: ProjectRootManager.getInstance(project).projectSdk?.takeIf { PythonSdkUtil.isPythonSdk(it) } ?: return@resolveWithCaching emptyArray<ResolveResult>()

            val stdlibScope = object : DelegatingGlobalSearchScope(getPythonSdkScope(project, sdk)) {
                override fun contains(file: VirtualFile) = super.contains(file) && PythonSdkUtil.isStdLib(file, sdk)
            }

            PyClassNameIndex.find(datatype, element.project, stdlibScope).map { PsiElementResolveResult(it) }.toTypedArray()
        }, false, false)
    }

    private fun getPythonSdkScope(project: Project, sdk: Sdk): GlobalSearchScope {
        val roots = sdk.rootProvider.getFiles(OrderRootType.CLASSES)
        if (roots.isEmpty()) return GlobalSearchScope.EMPTY_SCOPE
        return GlobalSearchScopesCore.directoriesScope(project, true, *roots)
    }

    private fun mapToSupportedType(datatype: String): String {
        return when (datatype) {
            "boolean" -> "bool"
            "integer", "long" -> "int"
            "double" -> "float"
            "string", "unicode" -> "str"
            "dictionary" -> "dict"
            "map" -> "Mapping"
            else -> datatype
        }
    }
}
