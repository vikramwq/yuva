# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\LAXMI\Android\sdk/tools/proguard/proguard-android.txt
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
-dontwarn com.google.android.exoplayer.**
-keepparameternames
-renamesourcefileattribute SourceFile
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,EnclosingMethod

# Preserve all annotations.

-keepattributes *Annotation*

# Preserve all public classes, and their public and protected fields and
# methods.

-keep public class * {
    public protected *;
}

# Preserve all .class method names.

-keepclassmembernames class * {
    java.lang.Class class$(java.lang.String);
    java.lang.Class class$(java.lang.String, boolean);
}

# Preserve all native method names and the names of their classes.

-keepclasseswithmembernames,includedescriptorclasses class * {
    native <methods>;
}

# Preserve the special static methods that are required in all enumeration
# classes.

-keepclassmembers,allowoptimization enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Explicitly preserve all serialization members. The Serializable interface
# is only a marker interface, so it wouldn't save them.
# You can comment this out if your library doesn't use serialization.
# If your code contains serializable classes that have to be backward
# compatible, please refer to the manual.

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keepattributes Exceptions
-ignorewarnings

# Youtube
# keep youtube / google
-keep class com.google.api.services.** { *; }
-keep class com.google.android.youtube.player.** { *; }
# Needed by google-api-client to keep generic types and @Key annotations accessed via reflection
-keepclassmembers class * {
  @com.google.api.client.util.Key <fields>;
}


#===================================================================================================
#======== VMAX START
#===================================================================================================
# VMAX Ads settings
-keep public class com.vmax.android.ads.** {
 public <fields>;
 public <methods>;
}
-keep public class com.vmax.android.ads.nativeHelper.** {
 *;
}
-keep class com.vmax.android.ads.mediation.partners.** {
public <fields>;
public <methods>;
 }
-keep class org.simpleframework.xml.** { *;} # Not required for SDK 3.5.0 onwards
-dontwarn org.simpleframework.xml.stream**  # Not required for SDK 3.5.0 onwards
-dontwarn android.support.v4.app**
-keep public class android.support.v4.content.ContextCompat { *;}
-keep public class android.support.v4.app.ActivityCompat { *;}


-keep class com.wamindia.dollywoodplay.models.**{ *;}


-dontwarn org.apache.http.**
-dontwarn android.net.**
-dontwarn com.google.ads.**
-dontwarn com.vmax.android.ads.volley.toolbox.**
-dontwarn com.chartboost.sdk.impl.**
-dontwarn com.google.android.gms.ads.**
-dontwarn com.vmax.android.ads.**

# Google Play Services library
-keep public class com.google.android.gms.ads.** {public *;}
-keep public class com.google.android.gms.common.ConnectionResult { public *;}
-keep public class com.google.android.gms.common.GooglePlayServicesUtil { public *;}
-dontwarn com.google.android.gms**
-keep class * extends java.util.ListResourceBundle { protected Object[][] getContents(); }
-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable { public static final *** NULL; }
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * { @com.google.android.gms.common.annotation.KeepName *; }
-keepnames class * implements android.os.Parcelable { public static final ** CREATOR; }
#===================================================================================================
#======== VMAX END
#===================================================================================================
-dontwarn okio.**
-dontwarn okhttp3.**
-dontwarn com.google.appengine.**
-dontwarn javax.servlet.**