package nick.template.usecase

import java.io.File as JavaFile
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import nick.template.R
import nick.template.data.File

interface Filesystem {
    val files: Flow<List<File>>
    suspend fun toggle(file: File.Directory)
    suspend fun addRandomFiles()
}

class AndroidAppScopedFilesystem @Inject constructor(
    @ApplicationContext appContext: Context,
    private val ioContext: CoroutineContext
) : Filesystem {
    private val root: JavaFile = appContext.filesDir
    private val _files = MutableStateFlow<List<File>>(emptyList())
    override val files: Flow<List<File>> = _files.onSubscription { _files.value = loadFiles(root) }

    override suspend fun toggle(file: File.Directory) {
        _files.update { files ->
            val index = files.indexOf(file)
            require(index != -1)
            files.toMutableList().apply {
                remove(file)

                if (file.isExpanded) {
                    // remove all children
                    removeAll { element ->
                        val elementParentAbsolutePath = element.parentAbsolutePath
                        element != file
                            && elementParentAbsolutePath != null
                            && elementParentAbsolutePath.startsWith(file.absolutePath)
                    }
                    add(index, file.copy(isExpanded = false))
                } else {
                    // add all children
                    val children = loadFiles(
                        directory = JavaFile(file.absolutePath),
                        depth = file.depth + 1,
                        parentAbsolutePath = file.absolutePath
                    )
                    addAll(index, children)
                    add(index, file.copy(isExpanded = true))
                }
            }
        }
    }

    override suspend fun addRandomFiles() = withContext(ioContext) {
        val smallRandomAmount = { (0..2).random() }
        val randomAmount = { (2..4).random() }

        // Nested directories/leaves
        repeat(randomAmount()) {
            val javaFile = JavaFile(root, UUID.randomUUID().toString())
                .also { it.mkdir() }

            repeat(randomAmount()) {
                val child1 = JavaFile(javaFile.absolutePath, UUID.randomUUID().toString())
                    .also { it.mkdir() }

                repeat(randomAmount()) {
                    val child2 = JavaFile(child1.absolutePath, UUID.randomUUID().toString())
                        .also { it.mkdir() }

                    repeat(randomAmount()) {
                        JavaFile(child2.absolutePath, UUID.randomUUID().toString()).createNewFile()
                    }
                }

                repeat(smallRandomAmount()) {
                    JavaFile(child1.absolutePath, UUID.randomUUID().toString()).createNewFile()
                }
            }

            repeat(smallRandomAmount()) {
                JavaFile(javaFile.absolutePath, UUID.randomUUID().toString()).createNewFile()
            }
        }

        // An empty directory
        JavaFile(root, UUID.randomUUID().toString()).mkdir()

        // Leaves at the root
        repeat(randomAmount()) {
            JavaFile(root, UUID.randomUUID().toString()).createNewFile()
        }

        _files.update { loadFiles(root) }
    }

    private suspend fun loadFiles(
        directory: JavaFile,
        depth: Int = 0,
        parentAbsolutePath: String? = null
    ) = withContext(ioContext) {
        directory
            .listFiles()
            .orEmpty()
            .map { javaFile -> javaFile.toFile(depth = depth, parentAbsolutePath = parentAbsolutePath) }
    }

    private fun JavaFile.toFile(
        depth: Int,
        parentAbsolutePath: String?
    ): File {
        return if (isDirectory) {
            File.Directory(
                absolutePath = absolutePath,
                name = name,
                depth = depth,
                parentAbsolutePath = parentAbsolutePath,
                isExpanded = false,
                icon1 = R.drawable.arrow_right,
                icon2 = R.drawable.folder
            )
        } else {
            File.Leaf(
                absolutePath = absolutePath,
                name = name,
                depth = depth,
                parentAbsolutePath = parentAbsolutePath,
                icon = R.drawable.file
            )
        }
    }
}
