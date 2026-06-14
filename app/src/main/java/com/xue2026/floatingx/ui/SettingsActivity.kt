package com.xue2026.floatingx.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.xue2026.floatingx.databinding.ActivitySettingsBinding
import com.xue2026.floatingx.security.LockActivity

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
            // 设置应用密码
            Toast.makeText(this, "设置应用密码功能开发中", Toast.LENGTH_SHORT).show()
        }

        binding.btnSetResourcePassword.setOnClickListener {
            Toast.makeText(this, "设置资源密码功能开发中", Toast.LENGTH_SHORT).show()
        }

        binding.switchAutoSave.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(this, "自动保存: ${if (isChecked) "开启" else "关闭"}", Toast.LENGTH_SHORT).show()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}