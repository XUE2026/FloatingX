package com.xue2026.floatingx.security

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.xue2026.floatingx.databinding.ActivityLockBinding

class LockActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLockBinding

    companion object {
        private const val PREF_NAME = "floatingx_security"
        private const val KEY_APP_PASSWORD = "app_password"
        private const val KEY_RESOURCE_PASSWORD = "resource_password"
        private const val KEY_IS_LOCK_ENABLED = "is_lock_enabled"

        fun isLockEnabled(context: Context): Boolean {
            val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return prefs.getBoolean(KEY_IS_LOCK_ENABLED, false)
        }

        fun setAppPassword(context: Context, password: String) {
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_APP_PASSWORD, password)
                .putBoolean(KEY_IS_LOCK_ENABLED, true)
                .apply()
        }

        fun verifyAppPassword(context: Context, input: String): Boolean {
            val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val stored = prefs.getString(KEY_APP_PASSWORD, "") ?: ""
            return input == stored
        }

        fun setResourcePassword(context: Context, password: String) {
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_RESOURCE_PASSWORD, password)
                .apply()
        }

        fun verifyResourcePassword(context: Context, input: String): Boolean {
            val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val stored = prefs.getString(KEY_RESOURCE_PASSWORD, "") ?: ""
            return input == stored
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLockBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val lockType = intent.getStringExtra("lock_type") ?: "app"

        binding.tvTitle.text = if (lockType == "resource") {
            "资源访问密码"
        } else {
            "请输入应用启动密码"
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
                Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show()
                binding.etPassword.text.clear()
            }
        }
    }
}