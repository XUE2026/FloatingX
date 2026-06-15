# FloatingX ProGuard
-keep class org.eclipse.jgit.** { *; }
-dontwarn org.eclipse.jgit.**
-keep class com.xue2026.floatingx.data.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keep class com.xue2026.floatingx.model.** { *; }