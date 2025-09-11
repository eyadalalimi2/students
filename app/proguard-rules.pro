###############################################
# الأساسيات
###############################################
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes RuntimeVisibleAnnotations, RuntimeInvisibleAnnotations, RuntimeVisibleParameterAnnotations, RuntimeInvisibleParameterAnnotations
#-keepattributes SourceFile,LineNumberTable

###############################################
# Parcelables
###############################################
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

###############################################
# Gson / DTOs
###############################################
-keep class com.eyadalalimi.students.request.** { *; }
-keep class com.eyadalalimi.students.response.** { *; }
-keep class com.eyadalalimi.students.model.** { *; }

###############################################
# Retrofit/OkHttp
###############################################
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**

###############################################
# WorkManager (لاحقًا)
###############################################
-keep class ** extends androidx.work.ListenableWorker { *; }

###############################################
# Firebase (لاحقًا)
###############################################
-keep class * extends com.google.firebase.messaging.FirebaseMessagingService { *; }

###############################################
# تحسين الضوضاء
###############################################
-dontnote
