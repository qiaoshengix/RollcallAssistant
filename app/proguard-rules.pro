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
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Ignore missing classes from external libraries that are not used on Android
-dontwarn aQute.bnd.annotation.**
-dontwarn com.github.luben.zstd.**
-dontwarn edu.umd.cs.findbugs.annotations.**
-dontwarn java.awt.**
-dontwarn javax.xml.stream.**
-dontwarn org.apache.poi.**
-dontwarn org.apache.logging.log4j.**
-dontwarn org.apache.xmlbeans.**
-dontwarn org.openxmlformats.schemas.**
-dontwarn com.microsoft.schemas.**
-dontwarn org.apache.commons.compress.**
-dontwarn org.apache.commons.codec.binary.Base64
-dontwarn org.apache.commons.math3.**
-dontwarn com.zaxxer.sparsebits.**
-dontwarn org.tukaani.xz.**
-dontwarn org.bouncycastle.**
-dontwarn org.apache.jcp.xml.dsig.internal.dom.**
-dontwarn org.apache.xml.security.**
-dontwarn org.apache.xml.resolver.**
-dontwarn org.apache.xml.serializer.**
-dontwarn org.apache.xml.utils.**
-dontwarn org.apache.xpath.**
-dontwarn org.apache.html.dom.**
-dontwarn org.apache.wml.**
-dontwarn org.apache.wml.dom.**
-dontwarn javax.xml.namespace.QName
-dontwarn com.sun.msv.**
-dontwarn org.relaxng.datatype.**

# Gson rules
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.reflect.TypeToken
-keep class * extends com.google.gson.reflect.TypeToken
-keep public class * implements com.google.gson.TypeAdapterFactory
-keep public class * implements com.google.gson.JsonSerializer
-keep public class * implements com.google.gson.JsonDeserializer
-keep class com.google.gson.** { *; }

# Keep model classes that are used with Gson
-keep class com.qiaosheng.rollcallassistant.model.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**
