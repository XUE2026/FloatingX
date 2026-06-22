package com.xue2026.floatingx.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.xue2026.floatingx.BuildConfig
import com.xue2026.floatingx.R
import com.xue2026.floatingx.databinding.ActivityMainBinding
import com.xue2026.floatingx.editor.EditorActivity
import com.xue2026.floatingx.security.CrashLogActivity
import com.xue2026.floatingx.security.LockActivity
import com.xue2026.floatingx.service.FloatingWindowService
import com.xue2026.floatingx.ui.SettingsActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isFloatingRunning = false

    private val overlayPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        checkOverlayPermission()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        checkOverlayPermission()
    }

    private fun setupViews() {
        binding.btnToggleFloating.setOnClickListener {
            if (!checkOverlayPermission()) return@setOnClickListener
            toggleFloatingWindow()
        }

        binding.btnOpenEditor.setOnClickListener {
            startActivity(Intent(this, EditorActivity::class.java))
        }

        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        binding.btnCrashLog.setOnClickListener {
            startActivity(Intent(this, CrashLogActivity::class.java))
        }

        binding.btnAbout.setOnClickListener {
            showAboutDialog()
        }

        updateFloatingButtonState()
    }

    private fun checkOverlayPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                AlertDialog.Builder(this)
                    .setTitle(R.string.permission_dialog_title)
                    .setMessage(R.string.permission_dialog_message)
                    .setPositiveButton("去设置") { _, _ ->
                        val intent = Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:$packageName")
                        )
                        overlayPermissionLauncher.launch(intent)
                    }
                    .setNegativeButton("取消", null)
                    .show()
                return false
            }
        }
        return true
    }

    private fun toggleFloatingWindow() {
        if (isFloatingRunning) {
            stopService(Intent(this, FloatingWindowService::class.java))
            isFloatingRunning = false
        } else {
            val intent = Intent(this, FloatingWindowService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
            isFloatingRunning = true
        }
        updateFloatingButtonState()
    }

    private fun updateFloatingButtonState() {
        binding.btnToggleFloating.text = if (isFloatingRunning) {
            getString(R.string.stop_floating)
        } else {
            getString(R.string.start_floating)
        }
    }

    private fun showAboutDialog() {
        AlertDialog.Builder(this)
            .setTitle("关于 FloatingX")
            .setMessage(
                """
                FloatingX v${BuildConfig.VERSION_NAME}
                
                正在开发第一版，尚未配置许可证，默认不允许使用源代码。
                
                作者: XUE2026 (github.com/XUE2026)
                
                第三方依赖:
                • AndroidX - Apache 2.0
                • Room - Apache 2.0
                • JGit - EDL 1.0
                • Gson - Apache 2.0
                • Material Components - Apache 2.0
                """.trimIndent()
            )
            .setPositiveButton("确定", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        // Check service status
        isFloatingRunning = FloatingWindowService.isRunning
        updateFloatingButtonState()
    }
}