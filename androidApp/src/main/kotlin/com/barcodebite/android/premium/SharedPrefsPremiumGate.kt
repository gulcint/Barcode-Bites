package com.barcodebite.android.premium

import android.content.Context

class SharedPrefsPremiumGate(context: Context) : PremiumGate {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun isPremiumEnabled(): Boolean = prefs.getBoolean(KEY_ENABLED, false)

    override fun enablePremium(plan: String) {
        prefs.edit()
            .putBoolean(KEY_ENABLED, true)
            .putString(KEY_PLAN, plan)
            .apply()
    }

    override fun disablePremium() {
        prefs.edit()
            .putBoolean(KEY_ENABLED, false)
            .putString(KEY_PLAN, "free")
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "barcodebite_premium"
        private const val KEY_ENABLED = "premium_enabled"
        private const val KEY_PLAN = "premium_plan"
    }
}
