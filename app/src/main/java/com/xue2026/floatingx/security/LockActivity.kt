package com.xue2026.floatingx.security

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.xue2026.floatingx.R
import com.xue2026.floatingx.databinding.ActivityLockBinding

/**
 * 安全锁 Activity
 *
 * 支持两种锁模式:
 * - "app": 应用启动密码验证
 * - "resource": 资源访问密码验证
 *
 * 密码以明文存储在 SharedPreferences 中 (后续可升级为加密存储)
 * 通过 intent extra "lock_type" 区分模式
 */
class LockActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLockBinding

    companion object {
        private const val PREF_NAME = "floatingx_security"
        private const val KEY_APP_PASSWORD = "app_password"
        private const val KEY_RESOURCE_PASSWORD = "resource_password"
        private const val KEY_IS_LOCK_ENABLED = "is_lock_enabled"

        fun isLockEnabled(context: Context): Boolean {
            return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_IS_LOCK_ENABLED, false)
        }

        fun setAppPassword(context: Context, password: String) {
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_APP_PASSWORD, password)
                .putBoolean(KEY_IS_LOCK_ENABLED, true)
                .apply()
        }

        fun verifyAppPassword(context: Context, input: String): Boolean {
            val stored = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getString(KEY_APP_PASSWORD, "") ?: ""
            return input == stored
        }

        fun setResourcePassword(context: Context, password: String) {
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_RESOURCE_PASSWORD, password)
                .apply()
        }

        fun verifyResourcePassword(context: Context, input: String): Boolean {
            val stored = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getString(KEY_RESOURCE_PASSWORD, "") ?: ""
            return input == stored
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLockBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val lockType = intent.getStringExtra("lock_type") ?: "app"
        binding.tvTitle.text = if (lockType == "resource") {
            getString(R.string.resource_lock_title)
        } else {
            getString(R.string.lock_title)
        }

        binding.btnUnlock.setOnClickListener {
            val input = binding.etPassword.text.toString()
            val isValid = if (lockType == "resource") {
                verifyResourcePassword(this, input)
            } else {
                verifyAppPassword(this, input)
            }
            if (isValid) {
                setResult(RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, R.string.lock_error, Toast.LENGTH_SHORT).show()
                binding.etPassword.text?.clear()
            }
        }
    }
}