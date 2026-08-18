package com.example.unseenandstrong

import android.content.Context

interface FlareDayPreferenceStore {
    var isEnabled: Boolean
}

class FlareDayPreferences(context: Context) : FlareDayPreferenceStore {
    private val preferences = context.applicationContext.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )

    override var isEnabled: Boolean
        get() = preferences.getBoolean(KEY_FLARE_DAY_ENABLED, false)
        set(value) {
            preferences.edit().putBoolean(KEY_FLARE_DAY_ENABLED, value).apply()
        }

    private companion object {
        const val PREFERENCES_NAME = "flare_day_preferences"
        const val KEY_FLARE_DAY_ENABLED = "flare_day_enabled"
    }
}
