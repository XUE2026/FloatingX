// Top-level build file — 项目级构建配置
plugins {
    // AGP 8.8.x 稳定版, 兼容 Gradle 8.12+
    id("com.android.application") version "8.8.2" apply false
    // Kotlin 2.0.x 新一代编译器, K2 模式
    id("org.jetbrains.kotlin.android") version "2.0.21" apply false
    // KSP: Kotlin Symbol Processing (替代 kapt, 编译速度更快)
    id("com.google.devtools.ksp") version "2.0.21-1.0.28" apply false
}