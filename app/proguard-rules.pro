# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/shrey/Android/Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keep class com.google.inject.** { *; }
-keep class javax.inject.** { *; }
-keep class retrofit.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-keepclassmembernames interface * {
    @retrofit.http.* <methods>;
    @butterknife.* <methods>;
}
-dontwarn rx.**
-dontwarn retrofit.**
-dontwarn okio.**
-dontwarn com.squareup.okhttp.*
-dontwarn com.squareup.okhttp.*

-keep class butterknife.** { *; }
-keep class okio.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewInjector { *; }
-keep class **$$ViewBinder { *; }


-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}
-keepclassmembers class ** {
    @com.squareup.otto.Subscribe public *;
    @com.squareup.otto.Produce public *;
}

-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.crowdvox.apps.crowdvoxandroid.data.models.json_models.** { *; }

#Relic performance tool (22-8-2016)
-keep class com.newrelic.** { *; }
-dontwarn com.newrelic.**
-keepattributes Exceptions, Signature, InnerClasses, LineNumberTable

##---------------Begin: proguard configuration for Retrofit  ----------
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on RoboVM on iOS. Will not be used at runtime.
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions
##---------------End: proguard configuration for Retrofit  ----------

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

##---------------End: proguard configuration for Gson  ----------

-keep class co.lokalise.android.sdk.** { *; }
-dontwarn co.lokalise.android.sdk.*