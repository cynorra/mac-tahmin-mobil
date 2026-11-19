# KotlinX Serialization
-keepattributes *Annotation*,InnerClasses
-dontnote kotlinx.serialization.SerializationKt
-keepclassmembers class * {
    *** Companion;
}
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}

# Room & SQLite
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Coil 3
-keep class coil3.** { *; }
-dontwarn coil3.**

# Firebase & Play Services
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn com.google.android.gms.**
-keep class com.google.firebase.** { *; }

# AdMob
-keep public class com.google.android.gms.ads.** {
   public *;
}
-dontwarn com.google.android.gms.ads.**
