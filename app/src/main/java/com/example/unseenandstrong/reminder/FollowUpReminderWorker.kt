package com.example.unseenandstrong.reminder

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.unseenandstrong.MainActivity
import com.example.unseenandstrong.R

class FollowUpReminderWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : Worker(appContext, workerParameters) {

    override fun doWork(): Result {
        val preferences = FollowUpReminderPreferences(applicationContext)
        if (!preferences.remindersEnabled || !FollowUpNotificationSupport.canPostNotifications(applicationContext)) {
            return Result.success()
        }

        val kind = inputData.getString(INPUT_REMINDER_KIND)
            ?.let { storedValue -> ReminderKind.entries.firstOrNull { it.storageValue == storedValue } }
            ?: return Result.failure()
        val notificationId = inputData.getInt(INPUT_NOTIFICATION_ID, 0)
        if (notificationId == 0) return Result.failure()

        FollowUpNotificationSupport.showNotification(
            context = applicationContext,
            kind = kind,
            notificationId = notificationId
        )
        return Result.success()
    }

    companion object {
        const val INPUT_REMINDER_KIND = "reminder_kind"
        const val INPUT_NOTIFICATION_ID = "notification_id"
    }
}

internal object FollowUpNotificationSupport {
    const val CHANNEL_ID = "advocacy_follow_up_reminders"

    fun createChannel(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.follow_up_notification_channel_name),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = context.getString(R.string.follow_up_notification_channel_description)
            setShowBadge(false)
        }
        manager.createNotificationChannel(channel)
    }

    fun hasRuntimePermission(context: Context): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
    }

    fun canPostNotifications(context: Context): Boolean {
        if (!hasRuntimePermission(context)) return false

        val manager = context.getSystemService(NotificationManager::class.java)
        if (!manager.areNotificationsEnabled()) return false

        val channel = manager.getNotificationChannel(CHANNEL_ID)
        return channel == null || channel.importance != NotificationManager.IMPORTANCE_NONE
    }

    fun showNotification(
        context: Context,
        kind: ReminderKind,
        notificationId: Int
    ) {
        createChannel(context)
        if (!canPostNotifications(context)) return

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = Notification.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(context.getString(kind.messageResId))
            .setCategory(Notification.CATEGORY_REMINDER)
            .setVisibility(Notification.VISIBILITY_PRIVATE)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        context.getSystemService(NotificationManager::class.java)
            .notify(notificationId, notification)
    }
}
