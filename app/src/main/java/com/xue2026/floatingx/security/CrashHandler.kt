package com.xue2026.floatingx.security

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 全局未捕获异常处理器
 * 
 * 使用 Thread.setDefaultUncaughtExceptionHandler 注册。
 * 崩溃发生时:
 * 1. 将堆栈 + 设备信息写入 internal storage/crash_logs/
 * 2. 文件名格式: crash_yyyy-MM-dd_HH-mm-ss.log
 * 3. 标记 SharedPreferences has_crash_log=true
 * 4. 转交给系统默认 handler（系统会弹崩溃对话框）
 */
class CrashHandler(private val context: Context) : Thread.UncaughtExceptionHandler {

    private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        saveCrashLog(throwable)
        defaultHandler?.uncaughtException(thread, throwable)
    }

    /**
     * 将崩溃信息写入文件
     * 包含: 时间戳, 设备型号, Android 版本, 应用版本, 完整堆栈
     */
    private fun saveCrashLog(throwable: Throwable) {
        try {
            val crashDir = File(context.filesDir, "crash_logs")
            if (!crashDir.exists()) crashDir.mkdirs()

            val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault())
            val timestamp = dateFormat.format(Date())
            val crashFile = File(crashDir, "crash_$timestamp.log")

            FileWriter(crashFile).use { writer ->
                val sw = StringWriter()
                val pw = PrintWriter(sw)
                throwable.printStackTrace(pw)
                pw.flush()

                writer.write("=== FloatingX Crash Report ===\n")
                writer.write("Time: $timestamp\n")
                writer.write("Device: ${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}\n")
                writer.write("Android: ${android.os.Build.VERSION.RELEASE} (API ${android.os.Build.VERSION.SDK_INT})\n")
                writer.write("App Version: ${getAppVersion()}\n")
                writer.write("\n--- Stack Trace ---\n")
                writer.write(sw.toString())

                // 递归写入 cause 链
                var cause = throwable.cause
                while (cause != null) {
                    val csw = StringWriter()
                    val cpw = PrintWriter(csw)
                    cause.printStackTrace(cpw)
                    cpw.flush()
                    writer.write("\n--- Caused by ---\n")
                    writer.write(csw.toString())
                    cause = cause.cause
                }
            }

            // 标记存在崩溃日志, 供 CrashLogActivity 读取
            context.getSharedPreferences("floatingx_prefs", Context.MODE_PRIVATE)
                .edit()
                .putBoolean("has_crash_log", true)
                .putString("latest_crash_file", crashFile.absolutePath)
                .apply()

        } catch (e: Exception) {
            // 日志写入失败时不抛异常 (可能处于崩溃恢复中)
            e.printStackTrace()
        }
    }

    /**
     * 获取应用版本号
     * 
     * 兼容 API 33+ (TIRAMISU) 的新 PackageManager API
     */
    private fun getAppVersion(): String {
        return try {
            val pkg = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.PackageInfoFlags.of(0)
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(context.packageName, 0)
            }
            "${pkg.versionName} (${pkg.versionCode})"
        } catch (e: Exception) {
            "unknown"
        }
    }
}