package com.xue2026.floatingx.security

import android.content.Context
import android.content.Intent
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CrashHandler(private val context: Context) : Thread.UncaughtExceptionHandler {

    private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        saveCrashLog(throwable)
        // Also pass to default handler for system handling
        defaultHandler?.uncaughtException(thread, throwable)
    }

    private fun saveCrashLog(throwable: Throwable) {
        try {
            val crashDir = File(context.filesDir, "crash_logs")
            if (!crashDir.exists()) {
                crashDir.mkdirs()
            }

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
                writer.write("\n--- Cause ---\n")
                var cause = throwable.cause
                while (cause != null) {
                    val csw = StringWriter()
                    val cpw = PrintWriter(csw)
                    cause.printStackTrace(cpw)
                    cpw.flush()
                    writer.write(csw.toString())
                    writer.write("\n")
                    cause = cause.cause
                }
            }

            // Update crash flag
            context.getSharedPreferences("floatingx_prefs", Context.MODE_PRIVATE)
                .edit()
                .putBoolean("has_crash_log", true)
                .putString("latest_crash_file", crashFile.absolutePath)
                .apply()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getAppVersion(): String {
        return try {
            val pkg = context.packageManager.getPackageInfo(context.packageName, 0)
            "${pkg.versionName} (${pkg.versionCode})"
        } catch (e: Exception) {
            "unknown"
        }
    }
}