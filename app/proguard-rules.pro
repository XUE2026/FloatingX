# FloatingX ProGuard Rules

# Keep JGit classes
-keep class org.eclipse.jgit.** { *; }
-dontwarn org.eclipse.jgit.**

# Keep Room entities
-keep class com.xue2026.floatingx.data.** { *; }

# Keep Gson serialization
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }

# Keep model classes
-keep class com.xue2026.floatingx.model.** { *; }