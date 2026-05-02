package dev.xeonkryptos.xeonrobotframeworkplugin.index

import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.util.indexing.DataIndexer
import com.intellij.util.indexing.FileBasedIndex
import com.intellij.util.indexing.FileContent
import com.intellij.util.indexing.ID
import com.intellij.util.indexing.ScalarIndexExtension
import com.intellij.util.indexing.hints.FileTypeInputFilterPredicate
import com.intellij.util.indexing.hints.FileTypeSubstitutionStrategy
import com.intellij.util.io.EnumeratorStringDescriptor
import com.intellij.util.io.KeyDescriptor
import com.jetbrains.python.PyNames
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotFeatureFileType
import java.util.Collections

class RobotFeatureFileNameIndex : ScalarIndexExtension<String>() {

    @Suppress("CompanionObjectInExtension")
    companion object {
        @JvmField
        val NAME = ID.create<String, Void?>("Robot.module.name")
    }

    override fun getName(): ID<String, Void?> = NAME

    override fun getIndexer(): DataIndexer<String, Void?, FileContent?> {
        return object : DataIndexer<String, Void?, FileContent?> {
            override fun map(inputData: FileContent): MutableMap<String, Void?> {
                val file = inputData.file
                val name = file.name
                if (PyNames.INIT_DOT_PY == name) {
                    val parent = file.parent
                    if (parent != null && parent.isDirectory) {
                        return Collections.singletonMap<String, Void?>(parent.name, null)
                    }
                } else {
                    return Collections.singletonMap<String, Void?>(FileUtilRt.getNameWithoutExtension(name), null)
                }
                return Collections.emptyMap()
            }
        }
    }

    override fun getKeyDescriptor(): KeyDescriptor<String> = EnumeratorStringDescriptor.INSTANCE

    @Suppress("UnstableApiUsage")
    override fun getInputFilter(): FileBasedIndex.InputFilter {
        return FileTypeInputFilterPredicate(FileTypeSubstitutionStrategy.BEFORE_SUBSTITUTION) { fileType: FileType -> fileType === RobotFeatureFileType.getInstance() }
    }

    override fun dependsOnFileContent(): Boolean = false

    override fun traceKeyHashToVirtualFileMapping(): Boolean = true

    override fun getVersion(): Int = 0
}
