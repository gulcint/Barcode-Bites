package com.barcodebite.android.session

import android.content.Context

class SessionStore(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getAccessToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)

    fun setAccessToken(token: String?) {
        prefs.edit().putString(KEY_ACCESS_TOKEN, token).apply()
    }

    companion object {
        private const val PREFS_NAME = "barcodebite_session"
        private const val KEY_ACCESS_TOKEN = "access_token"
    }
}
