# FloatingX

> Android 悬浮窗宠物/模型展示应用

![GitHub](https://img.shields.io/badge/version-0.1.0--dev-blue)
![Android](https://img.shields.io/badge/Android-8.0%2B-brightgreen)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-purple)

## 简介

FloatingX 是一个 Android 悬浮窗应用，可以在手机屏幕上显示 3D/2D 模型宠物。内置编辑器支持导入和编辑 PMX、FBX、Spine 等格式的模型与动画。

## 架构

采用"一体两面"架构：

- **宠物运行时** — 低能耗发布版本，悬浮窗服务负责权限管理、渲染和交互
- **编辑器** — 高能耗测试版本，支持模型/动作导入和可视化调整

## 功能

- 悬浮窗展示 3D/2D 模型宠物
- PMX/VMD 模型与动作导入
- FBX 模型兼容
- Spine 2D 动画集成
- 积木式 VMD 动作编辑器
- Termux:X11 远程渲染
- 应用/资源双重密码锁
- 崩溃保存与日志系统
- JGit 存档版本管理

## 开发

### 环境要求

- Android Studio Hedgehog (2023.1.1) 或更新版本
- JDK 17+
- Android SDK 34
- Gradle 8.14

### 构建

```bash
# 调试构建
./gradlew assembleDebug

# 发布构建
./gradlew assembleRelease
```

### 开发分支

```bash
git checkout development
```

## 许可证

正在开发第一版，尚未配置许可证，默认不允许使用源代码。

## 作者

- **XUE2026** — [github.com/XUE2026](https://github.com/XUE2026)

## 第三方依赖

- [AndroidX](https://developer.android.com/jetpack/androidx) — Apache 2.0
- [Room](https://developer.android.com/training/data-storage/room) — Apache 2.0
- [JGit](https://www.eclipse.org/jgit/) — EDL 1.0
- [Gson](https://github.com/google/gson) — Apache 2.0
- [Material Components](https://material.io/develop/android) — Apache 2.0
- [Spine Runtime](https://esotericsoftware.com/spine-internal) — Spine 官方许可
- [Assimp](https://github.com/assimp/assimp) — BSD 3-Clause