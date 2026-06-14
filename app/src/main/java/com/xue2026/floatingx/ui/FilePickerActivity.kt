package com.xue2026.floatingx.ui

import android.os.Bundle
import androidx.documentfile.provider.DocumentFile
import androidx.appcompat.app.AppCompatActivity
import com.xue2026.floatingx.databinding.ActivityFilePickerBinding

/**
 * 文件选择器 Activity
 * 使用 Storage Access Framework 让用户选择文件
 * 支持选择 Termux 家目录等外部存储位置
 */
class FilePickerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFilePickerBinding

    companion object {
        const val REQUEST_CODE_OPEN_DIR = 1001
        const val REQUEST_CODE_OPEN_FILE = 1002
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilePickerBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}