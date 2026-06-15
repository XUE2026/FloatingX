plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.xue2026.floatingx"
    compileSdk = 34  // Android 14 (GitHub Actions 默认镜像已预装)

    defaultConfig {
        applicationId = "com.xue2026.floatingx"
        minSdk = 26   // Android 8.0
        targetSdk = 34
        versionCode = 1
        versionName = "0.0.1"  // preview dev v0.0.1

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // KSP: Room schema 导出路径
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
    }

    // JGit META-INF 冲突排除
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/DEPENDENCIES"
            excludes += "/META-INF/INDEX.LIST"
        }
    }
}

dependencies {
    // ========== AndroidX Core ==========
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-service:2.8.7")
    implementation("androidx.activity:activity-ktx:1.9.3")

    // ========== UI ==========
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.webkit:webkit:1.12.1")

    // ========== Room (本地数据库) ==========
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // ========== JGit (用户存档版本管理) ==========
    implementation("org.eclipse.jgit:org.eclipse.jgit:6.9.0.202403050737-r")

    // ========== 序列化 ==========
    implementation("com.google.code.gson:gson:2.11.0")

    // ========== 协程 ==========
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

    // ========== SAF 文件选择器 ==========
    implementation("androidx.documentfile:documentfile:1.0.1")
}