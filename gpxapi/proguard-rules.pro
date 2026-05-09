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
## Proj4j 클래스 유지 규칙 ##

# Proj4j 라이브러리 전체의 클래스와 멤버를 유지합니다.
-keep class org.osgeo.proj4j.** { *; }

# Proj4j가 내부적으로 사용하는 핵심 클래스들이 제거되지 않도록 합니다.
-dontwarn org.osgeo.proj4j.**