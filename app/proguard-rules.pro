# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
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

# Firebase
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Firebase Performance Monitoring
-keepclassmembers class com.google.firebase.perf.** { *; }
-keep class com.google.firebase.perf.** { *; }
-keep class com.google.android.material.color.** { *; }

# For Firebase Messaging
-keep class com.google.firebase.messaging.** { *; }

#Event Bus Proguard Rules
-keepattributes *Annotation*
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# And if you use AsyncExecutor:
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

-keep class androidx.**
-keepclasseswithmembernames class androidx.** {
    *;
}

-keep class com.google.**
-keepclasseswithmembernames class com.google.** {
    *;
}
-keep class com.google.android.gms.** { *; }

-keep class com.google.android.gms.common.ConnectionResult {
    int SUCCESS;
}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient {
    com.google.android.gms.ads.identifier.AdvertisingIdClient$Info getAdvertisingIdInfo(android.content.Context);
}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient$Info {
    java.lang.String getId();
    boolean isLimitAdTrackingEnabled();
}

-keepnames class androidx.navigation.fragment.NavHostFragment
-keep class * extends androidx.fragment.app.Fragment{}


# coroutines Library
# ServiceLoader support
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepnames class kotlinx.coroutines.android.AndroidExceptionPreHandler {}
-keepnames class kotlinx.coroutines.android.AndroidDispatcherFactory {}
-keep class kotlinx.coroutines.android.AndroidDispatcherFactory {*;}

# Most of volatile fields are updated with AFU and should not be mangled
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# Same story for the standard library's SafeContinuation that also uses AtomicReferenceFieldUpdater
-keepclassmembernames class kotlin.coroutines.SafeContinuation {
    volatile <fields>;
}

# Glide Proguard Rules
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}

#AppsFlyer Proguard Rules
-keep class com.appsflyer.** { *; }

#Viewmodel Proguard Rules
-keep class * extends androidx.lifecycle.ViewModel {
    <init>();
}
-keep class * extends androidx.lifecycle.AndroidViewModel {
    <init>(android.app.Application);
}

#Navigation Component
-keep class * extends androidx.fragment.app.Fragment{}
-keepnames class * extends android.os.Parcelable
-keepnames class androidx.navigation.fragment.NavHostFragment

# Android Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

#Enum
-keep enum * { *; }

-keep class com.facebook.** { *; }
-keep class com.facebook.applinks.** { *; }
-keepclassmembers class com.facebook.applinks.** { *; }
-keep class com.facebook.FacebookSdk { *; }

#State Machines as their states they are saved in prefs as class name
-keep class com.jar.app.feature.onboarding.state_machine.** { *; }

-dontwarn com.appsflyer.**
-keep public class com.google.firebase.messaging.FirebaseMessagingService {
  public *;
}

#Juspay Proguard Rules
-keep class in.juspay.** {*;}

#ViewBindingKotlinModel Proguard Rules
-keepclassmembers class * extends androidx.viewbinding.ViewBinding {
  public static *** bind(android.view.View);
}

-keep public class * implements java.lang.reflect.Type
#Clevertap Proguard Rules
-keep class com.clevertap.android.sdk.Logger
-keep class com.clevertap.android.sdk.pushnotification.fcm.FcmPushProvider{*;}
-keep class com.google.firebase.messaging.FirebaseMessagingService{*;}
-keep class com.clevertap.android.sdk.pushnotification.CTNotificationIntentService{*;}
-keep class e.** { *; }
-keep class com.clevertap.android.sdk.** { *; }


-dontwarn com.userexperior.**
-keep class com.userexperior.** { *; }

#kotlinx.serialization rules
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt # core serialization annotations

# kotlinx-serialization-json specific. Add this if you have java.lang.NoClassDefFoundError kotlinx.serialization.json.JsonObjectSerializer
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep `Companion` object fields of serializable classes.
# This avoids serializer lookup through `getDeclaredClasses` as done for named companion objects.
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
}

# Keep `serializer()` on companion objects (both default and named) of serializable classes.
-if @kotlinx.serialization.Serializable class ** {
    static **$* *;
}
-keepclassmembers class <2>$<3> {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep `INSTANCE.serializer()` of serializable objects.
-if @kotlinx.serialization.Serializable class ** {
    public static ** INSTANCE;
}
-keepclassmembers class <1> {
    public static <1> INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}

# @Serializable and @Polymorphic are used at runtime for polymorphic serialization.
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault

# Don't print notes about potential mistakes or omissions in the configuration for kotlinx-serialization classes
# See also https://github.com/Kotlin/kotlinx.serialization/issues/1900
-dontnote kotlinx.serialization.**

# Serialization core uses `java.lang.ClassValue` for caching inside these specified classes.
# If there is no `java.lang.ClassValue` (for example, in Android), then R8/ProGuard will print a warning.
# However, since in this case they will not be used, we can disable these warnings
-dontwarn kotlinx.serialization.internal.ClassValueReferences


#Change xxx.DemoMessageRreceiver to the full class name defined in your app
-keep class xxx.DemoMessageReceiver {*;}

#SDK has been obfuscated and compressed to avoid class not found error due to re-obfuscation.
-keep class com.xiaomi.**

#If the compiling Android version you are using is 23, you can prevent getting a false warning which makes it impossible to compile.
-dontwarn com.xiaomi.push.**

-keep class com.xiaomi.mipush.sdk.MiPushMessage {*;}
-keep class com.xiaomi.mipush.sdk.MiPushCommandMessage {*;}
-keep class com.xiaomi.mipush.sdk.PushMessageReceiver {*;}
-keep class com.xiaomi.mipush.sdk.MessageHandleService {*;}
-keep class com.xiaomi.push.service.XMJobService {*;}
-keep class com.xiaomi.push.service.XMPushService {*;}
-keep class com.xiaomi.mipush.sdk.PushMessageHandler {*;}
-keep class com.xiaomi.push.service.receivers.NetworkStatusReceiver {*;}
-keep class com.xiaomi.push.service.receivers.PingReceiver {*;}
-keep class com.xiaomi.mipush.sdk.NotificationClickedActivity {*;}
-keep class com.jar.app.alias.JarDiwaliLauncherAlias {*;}
-keep class com.jar.app.alias.JarLauncherAlias {*;}