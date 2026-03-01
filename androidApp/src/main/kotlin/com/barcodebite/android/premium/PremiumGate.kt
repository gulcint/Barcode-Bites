package com.barcodebite.android.premium

interface PremiumGate {
    fun isPremiumEnabled(): Boolean
    fun enablePremium(plan: String)
    fun disablePremium()
}
