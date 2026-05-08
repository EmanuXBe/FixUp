# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# DataFaker usa snakeyaml internamente, que referencia java.beans.* (API de Java desktop).
# Esas clases no existen en Android pero el código nunca se ejecuta en runtime.
-dontwarn java.beans.**

# DTOs usados con Gson (Retrofit). R8 renombraría los campos y rompería la serialización JSON.
-keep class edu.javeriana.fixup.data.network.dto.** { *; }

# Gson necesita acceder a los campos por reflexión
-keepclassmembers class edu.javeriana.fixup.data.network.dto.** { *; }

# Firebase Messaging Service con Hilt
-keep class edu.javeriana.fixup.data.network.FixUpMessagingService { *; }