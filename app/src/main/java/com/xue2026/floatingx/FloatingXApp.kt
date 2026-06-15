package com.xue2026.floatingx

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.xue2026.floatingx.security.CrashHandler

/**
 * FloatingX Application 入口
 *
 * 职责:
 * 1. 创建前台服务通知渠道 (FloatingX 悬浮窗保活用)
 * 2. 注册全局崩溃处理器 (CrashHandler)
 * 3. 持有全局单例引用 (FloatingXApp.instance)
 */
class FloatingXApp : Application() {

    companion object {
        /** 通知渠道 ID, 与 FloatingWindowService 共享 */
        const val NOTIFICATION_CHANNEL_ID = "floatingx_service"

        /** 全局 Application 实例引用 */
        lateinit var instance: FloatingXApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        createNotificationChannel()
        setupCrashHandler()
    }

    /**
     * 创建前台服务通知渠道
     * IMPORTANCE_LOW: 不在通知栏弹出声音/横幅
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                getString(R.string.floating_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.floating_channel_desc)
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    /**
     * 注册全局崩溃捕获
     * Thread.setDefaultUncaughtExceptionHandler 替换系统默认 handler
     */
    private fun setupCrashHandler() {
        Thread.setDefaultUncaughtExceptionHandler(
            CrashHandler(this)
        )
    }
}