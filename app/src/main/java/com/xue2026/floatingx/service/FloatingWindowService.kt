package com.xue2026.floatingx.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import com.xue2026.floatingx.FloatingXApp
import com.xue2026.floatingx.R
import com.xue2026.floatingx.ui.MainActivity

/**
 * 悬浮窗核心服务
 * 
 * 使用 startForegroundService 启动，在前台运行。
 * 通过 WindowManager.addView 添加 TYPE_APPLICATION_OVERLAY 悬浮窗。
 * 
 * 行为:
 * - 拖拽: 触摸移动时更新悬浮窗位置
 * - 点击: 轻触打开 MainActivity
 * - 前台通知: 提供保活和快捷入口
 */
class FloatingWindowService : Service() {

    private lateinit var windowManager: WindowManager
    private var floatingView: View? = null
    private var params: WindowManager.LayoutParams? = null

    companion object {
        @Volatile
        var isRunning = false
            private set
        private const val NOTIFICATION_ID = 1001
    }

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification())
        isRunning = true
        showFloatingWindow()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        isRunning = false
        removeFloatingWindow()
        super.onDestroy()
    }

    /**
     * 创建前台服务通知
     * PendingIntent 指向 MainActivity 提供快速跳转
     */
    private fun createNotification() = NotificationCompat.Builder(this, FloatingXApp.NOTIFICATION_CHANNEL_ID)
        .setContentTitle("FloatingX")
        .setContentText("悬浮窗运行中")
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setOngoing(true)
        .setContentIntent(
            PendingIntent.getActivity(
                this,
                0,
                Intent(this, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
        .build()

    /**
     * 创建并显示悬浮窗
     * 
     * WindowManager.LayoutParams 配置:
     * - TYPE_APPLICATION_OVERLAY (Android O+) / TYPE_PHONE (兼容)
     * - FLAG_NOT_FOCUSABLE 让触摸事件穿透
     * - PixelFormat.TRANSLUCENT 支持透明
     * 
     * 拖拽通过 setOnTouchListener 实现:
     * - ACTION_DOWN: 记录初始坐标
     * - ACTION_MOVE: 计算偏移并更新位置
     * - ACTION_UP: 若非拖拽则触发点击
     */
    @Suppress("ClickableViewAccessibility")
    private fun showFloatingWindow() {
        if (floatingView != null) return

        floatingView = LayoutInflater.from(this).inflate(R.layout.floating_window, null)

        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 0
            y = 200
        }

        // 单次 touch listener: 拖拽 + 点击
        floatingView?.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f
            private var isDragging = false

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = params!!.x
                        initialY = params!!.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        isDragging = false
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        params!!.x = initialX + (event.rawX - initialTouchX).toInt()
                        params!!.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager.updateViewLayout(v, params)
                        if (Math.abs(event.rawX - initialTouchX) > 10 ||
                            Math.abs(event.rawY - initialTouchY) > 10
                        ) {
                            isDragging = true
                        }
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        if (!isDragging) {
                            // 轻触 -> 打开主界面
                            val intent = Intent(this@FloatingWindowService, MainActivity::class.java).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            startActivity(intent)
                        }
                        return true
                    }
                }
                return false
            }
        })

        try {
            windowManager.addView(floatingView, params)
        } catch (e: SecurityException) {
            // 缺少悬浮窗权限
            e.printStackTrace()
            floatingView = null
        } catch (e: Exception) {
            e.printStackTrace()
            floatingView = null
        }
    }

    /**
     * 移除悬浮窗
     * 在 onDestroy 中调用，确保窗口被正确清理
     */
    private fun removeFloatingWindow() {
        floatingView?.let {
            try {
                windowManager.removeView(it)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            floatingView = null
        }
    }
}