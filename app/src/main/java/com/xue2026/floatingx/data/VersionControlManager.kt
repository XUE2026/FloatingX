package com.xue2026.floatingx.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.PushCommand
import org.eclipse.jgit.internal.storage.file.FileRepository
import java.io.File

/**
 * 版本控制管理器 - 使用 JGit 管理用户存档
 *
 * 自动备份用户配置、模型缓存等数据
 */
class VersionControlManager(private val context: Context) {

    private val repoDir: File get() = File(context.filesDir, "user_data_repo")
    private var git: Git? = null

    suspend fun initialize(): Boolean = withContext(Dispatchers.IO) {
        try {
            if (!repoDir.exists()) {
                repoDir.mkdirs()
                Git.init().setDirectory(repoDir).call().use { initialized ->
                    git = initialized
                    // Create initial commit
                    val file = File(repoDir, ".gitkeep")
                    file.createNewFile()
                    initialized.add().addFilepattern(".").call()
                    initialized.commit()
                        .setMessage("Initial commit - FloatingX user data")
                        .call()
                }
            } else {
                git = Git(FileRepository(File(repoDir, ".git")))
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun saveSnapshot(description: String): Boolean = withContext(Dispatchers.IO) {
        try {
            git?.let { g ->
                g.add().addFilepattern(".").call()
                g.commit()
                    .setMessage(description)
                    .setAuthor("FloatingX", "floatingx@local")
                    .call()
                true
            } ?: false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getHistory(): List<String> = withContext(Dispatchers.IO) {
        try {
            git?.log()?.setMaxCount(20)?.call()?.map { commit ->
                "${commit.abbreviateId(8).name()} - ${commit.fullMessage}"
            }?.toList() ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun getRepoPath(): String = repoDir.absolutePath

    fun close() {
        git?.close()
    }
}