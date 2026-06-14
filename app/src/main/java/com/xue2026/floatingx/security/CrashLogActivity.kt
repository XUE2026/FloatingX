package com.xue2026.floatingx.security

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.xue2026.floatingx.databinding.ActivityCrashLogBinding
import java.io.File

class CrashLogActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrashLogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrashLogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadCrashLogs()
    }

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

        // Clear logs button
        binding.btnClearLogs.setOnClickListener {
            files.forEach { it.delete() }
            loadCrashLogs()
            Toast.makeText(this, "日志已清除", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showCrashDetail(file: File) {
        val content = file.readText()
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(file.name)
            .setMessage(content)
            .setPositiveButton("确定", null)
            .setNeutralButton("删除") { _, _ ->
                file.delete()
                loadCrashLogs()
            }
            .show()
    }
}