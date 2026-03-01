package com.barcodebite.android.observability

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics

interface AppAnalytics {
    fun logEvent(name: String, params: Map<String, String> = emptyMap())
}

interface CrashReporter {
    fun recordException(throwable: Throwable, message: String? = null)
}

class FirebaseAppAnalytics(
    private val analytics: FirebaseAnalytics?,
) : AppAnalytics {
    override fun logEvent(name: String, params: Map<String, String>) {
        if (analytics == null) {
            Log.d("BarcodeBiteAnalytics", "event=$name params=$params")
            return
        }
        val bundle = android.os.Bundle()
        params.forEach { (key, value) -> bundle.putString(key, value) }
        analytics.logEvent(name, bundle)
    }
}

class FirebaseCrashReporter(
    private val crashlytics: FirebaseCrashlytics?,
) : CrashReporter {
    override fun recordException(throwable: Throwable, message: String?) {
        if (crashlytics == null) {
            Log.e("BarcodeBiteCrash", message ?: "error", throwable)
            return
        }
        message?.let(crashlytics::log)
        crashlytics.recordException(throwable)
    }
}

fun createAppAnalytics(context: Context): AppAnalytics {
    val app = runCatching { FirebaseApp.initializeApp(context) }.getOrNull()
    val analytics = if (app != null) runCatching { FirebaseAnalytics.getInstance(context) }.getOrNull() else null
    return FirebaseAppAnalytics(analytics)
}

fun createCrashReporter(context: Context): CrashReporter {
    val app = runCatching { FirebaseApp.initializeApp(context) }.getOrNull()
    val crashlytics = if (app != null) runCatching { FirebaseCrashlytics.getInstance() }.getOrNull() else null
    return FirebaseCrashReporter(crashlytics)
}
