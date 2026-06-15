package com.xue2026.floatingx.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import java.io.File

/**
 * JGit 用户存档版本管理器
 * 
 * 在 internal storage/user_data_repo/ 下维护一个 Git 仓库。
 * 每次用户修改配置/模型数据后自动 commit, 形成可追溯的版本历史。
 * 
 * 数据存储位置: context.filesDir/user_data_repo/
 */
class VersionControlManager(private val context: Context) {

    private val repoDir: File get() = File(context.filesDir, "user_data_repo")
    private var git: Git? = null

    /**
     * 初始化或打开已有仓库
     * - 目录不存在则 git init
     * - 存在则复用
     */
    suspend fun initialize(): Boolean = withContext(Dispatchers.IO) {
        try {
            if (!repoDir.exists()) {
                repoDir.mkdirs()
                Git.init().setDirectory(repoDir).call().use { initialized ->
                    git = initialized
                    val file = File(repoDir, ".gitkeep")
                    file.createNewFile()
                    initialized.add().addFilepattern(".").call()
                    initialized.commit()
                        .setMessage("Initial commit - FloatingX user data")
                        .call()
                }
            } else {
                // FileRepositoryBuilder 替代弃用的内部 FileRepository API
                val repo = FileRepositoryBuilder()
                    .setGitDir(File(repoDir, ".git"))
                    .readEnvironment()
                    .findGitDir()
                    .build()
                git = Git(repo)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 自动 commit 当前存档
     * @param description 存档描述 (e.g. "Auto: 2026-06-14 12:00 config update")
     */
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

    /**
     * 获取最近 20 条提交记录
     */
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