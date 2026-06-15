package com.xue2026.floatingx.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.xue2026.floatingx.R
import com.xue2026.floatingx.databinding.ActivitySettingsBinding

/**
 * 设置界面
 *
 * 安全设置:
 * - 应用启动密码 (LockActivity 的 app 模式)
 * - 资源访问密码 (LockActivity 的 resource 模式)
 *
 * 数据管理:
 * - 自动保存开关 (Room DB + JGit 自动 commit)
 *
 * TODO: 实现密码设置对话框
 * TODO: 实现自动保存逻辑
 */
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
    }

    private fun setupViews() {
        binding.btnSetAppPassword.setOnClickListener {
            Toast.makeText(this, R.string.feature_developing, Toast.LENGTH_SHORT).show()
        }

        binding.btnSetResourcePassword.setOnClickListener {
            Toast.makeText(this, R.string.feature_developing, Toast.LENGTH_SHORT).show()
        }

        binding.switchAutoSave.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(this, "${getString(R.string.auto_save)}: ${if (isChecked) "开启" else "关闭"}", Toast.LENGTH_SHORT).show()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}