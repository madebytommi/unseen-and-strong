package com.example.unseenandstrong.reminder

import android.content.Context

internal interface ReminderPreferenceStore {
    var remindersEnabled: Boolean
}

class FollowUpReminderPreferences(context: Context) : ReminderPreferenceStore {
    private val preferences = context.applicationContext.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )

    override var remindersEnabled: Boolean
        get() = preferences.getBoolean(KEY_REMINDERS_ENABLED, false)
        set(value) {
            preferences.edit().putBoolean(KEY_REMINDERS_ENABLED, value).apply()
        }

    var notificationPermissionRequested: Boolean
        get() = preferences.getBoolean(KEY_PERMISSION_REQUESTED, false)
        set(value) {
            preferences.edit().putBoolean(KEY_PERMISSION_REQUESTED, value).apply()
        }

    private companion object {
        const val PREFERENCES_NAME = "follow_up_reminder_preferences"
        const val KEY_REMINDERS_ENABLED = "follow_up_reminders_enabled"
        const val KEY_PERMISSION_REQUESTED = "notification_permission_requested"
    }
}
