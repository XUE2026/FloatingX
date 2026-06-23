package com.xue2026.floatingx.security

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.xue2026.floatingx.R
import com.xue2026.floatingx.databinding.ActivityCrashLogBinding
import java.io.File

/**
 * 崩溃日志查看器
 *
 * 读取 internal storage/crash_logs/ 下的崩溃日志文件并展示列表。
 * 支持: 查看详情、复制全文、删除单条、一键清除全部
 */
class CrashLogActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrashLogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrashLogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadCrashLogs()
    }

    /** 加载崩溃日志列表 */
    private fun loadCrashLogs() {
        val crashDir = File(filesDir, "crash_logs")
        if (!crashDir.exists() || crashDir.listFiles().isNullOrEmpty()) {
            binding.tvNoCrash.visibility = android.view.View.VISIBLE
            binding.rvCrashLogs.visibility = android.view.View.GONE
            return
        }

        binding.tvNoCrash.visibility = android.view.View.GONE
        binding.rvCrashLogs.visibility = android.view.View.VISIBLE

        val files = crashDir.listFiles()?.sortedByDescending { it.lastModified() } ?: emptyList()

        binding.rvCrashLogs.layoutManager = LinearLayoutManager(this)
        binding.rvCrashLogs.adapter = CrashLogAdapter(files.toList()) { file ->
            showCrashDetail(file)
        }

        binding.btnClearLogs.setOnClickListener {
            files.forEach { it.delete() }
            loadCrashLogs()
            Toast.makeText(this, R.string.logs_cleared, Toast.LENGTH_SHORT).show()
        }
    }

    /** 弹窗显示崩溃详情 + 复制按钮 */
    private fun showCrashDetail(file: File) {
        val content = file.readText()
        AlertDialog.Builder(this)
            .setTitle(file.name)
            .setMessage(content)
            .setPositiveButton(R.string.btn_ok, null)
            .setNeutralButton(R.string.btn_copy) { _, _ ->
                val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("crash_log", content))
                Toast.makeText(this, R.string.crash_copied, Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.btn_delete) { _, _ ->
                file.delete()
                loadCrashLogs()
                Toast.makeText(this, R.string.log_deleted, Toast.LENGTH_SHORT).show()
            }
            .show()
    }
}