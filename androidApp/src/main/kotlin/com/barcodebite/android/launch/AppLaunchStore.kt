package com.barcodebite.android.launch

import android.content.Context

class AppLaunchStore(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isOnboardingCompleted(): Boolean = prefs.getBoolean(KEY_ONBOARDING_DONE, false)

    fun setOnboardingCompleted(value: Boolean) {
        prefs.edit().putBoolean(KEY_ONBOARDING_DONE, value).apply()
    }

    companion object {
        private const val PREFS_NAME = "barcodebite_launch"
        private const val KEY_ONBOARDING_DONE = "onboarding_done"
    }
}
