package com.xue2026.floatingx.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.xue2026.floatingx.databinding.ActivityFilePickerBinding

/**
 * SAF 文件选择器 Activity
 * 
 * 使用 Storage Access Framework 让用户选择文件/目录。
 * 支持选择 Termux 家目录等外部存储路径。
 * 
 * TODO: 实现文件选择逻辑:
 * 1. 使用 Intent(Intent.ACTION_OPEN_DOCUMENT_TREE) 选择目录
 * 2. 使用 Intent(Intent.ACTION_OPEN_DOCUMENT) 选择文件
 * 3. 通过 DocumentFile API 读取/写入
 */
class FilePickerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFilePickerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilePickerBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}