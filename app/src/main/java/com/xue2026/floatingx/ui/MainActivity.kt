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
import com.xue2026.floatingx.BuildConfig
import com.xue2026.floatingx.R
import com.xue2026.floatingx.databinding.ActivityMainBinding
import com.xue2026.floatingx.editor.EditorActivity
import com.xue2026.floatingx.security.CrashLogActivity
import com.xue2026.floatingx.service.FloatingWindowService

/**
 * 主界面 Activity
 *
 * 提供五个主要入口:
 * 1. 启动/关闭悬浮窗 (startForegroundService)
 * 2. 打开编辑器 (EditorActivity, 横屏)
 * 3. 设置 (SettingsActivity)
 * 4. 查看崩溃日志 (CrashLogActivity)
 * 5. 关于信息
 *
 * 启动时自动检查悬浮窗权限 (SYSTEM_ALERT_WINDOW)
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isFloatingRunning = false

    // 悬浮窗权限请求回调
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

    /** 绑定按钮事件 */
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

    /**
     * 检查悬浮窗权限
     * API 23+ 需要动态申请 SYSTEM_ALERT_WINDOW 权限
     */
    private fun checkOverlayPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                AlertDialog.Builder(this)
                    .setTitle(R.string.permission_dialog_title)
                    .setMessage(R.string.permission_dialog_message)
                    .setPositiveButton(R.string.btn_go_settings) { _, _ ->
                        val intent = Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:$packageName")
                        )
                        overlayPermissionLauncher.launch(intent)
                    }
                    .setNegativeButton(R.string.btn_cancel, null)
                    .show()
                return false
            }
        }
        return true
    }

    /** 切换悬浮窗启停 */
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

    /** 更新按钮文字（启动/关闭） */
    private fun updateFloatingButtonState() {
        binding.btnToggleFloating.text = if (isFloatingRunning) {
            getString(R.string.stop_floating)
        } else {
            getString(R.string.start_floating)
        }
    }

    /** 显示"关于"对话框 */
    private fun showAboutDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.about)
            .setMessage(
                """
                FloatingX v${BuildConfig.VERSION_NAME}
                
                ${getString(R.string.about_license)}
                
                ${getString(R.string.about_author)}
                
                ${getString(R.string.about_dependencies)}
                • AndroidX - Apache 2.0
                • Room - Apache 2.0
                • JGit - EDL 1.0
                • Gson - Apache 2.0
                • Material Components - Apache 2.0
                """.trimIndent()
            )
            .setPositiveButton(R.string.btn_ok, null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        isFloatingRunning = FloatingWindowService.isRunning
        updateFloatingButtonState()
    }
}