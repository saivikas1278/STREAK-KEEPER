package com.example.streakkeeper.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.streakkeeper.MainActivity
import com.example.streakkeeper.data.HabitStorageManager

/**
 * 100% Offline-First Alarm BroadcastReceiver.
 * Fires pre-habit alarms (5 minutes prior), on-time start reminders,
 * and 8:00 PM Evening Streak Shield notifications entirely on-device without any cloud dependencies.
 */
class HabitAlarmReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "streakkeeper_offline_reminders"
        const val CHANNEL_NAME = "StreakKeeper Habit Reminders"

        const val EXTRA_HABIT_ID = "extra_habit_id"
        const val EXTRA_HABIT_TITLE = "extra_habit_title"
        const val EXTRA_NOTIFICATION_TYPE = "extra_notification_type"

        const val TYPE_PRE_REMINDER = "TYPE_PRE_REMINDER"
        const val TYPE_ON_TIME = "TYPE_ON_TIME"
        const val TYPE_EVENING_SHIELD = "TYPE_EVENING_SHIELD"

        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "100% Offline Local Reminders for Streaks and Habits"
                    enableVibration(true)
                }
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val habits = HabitStorageManager.loadHabits(context)
            HabitNotificationScheduler.rescheduleAll(context, habits)
            return
        }

        createNotificationChannel(context)
        val habitTitle = intent.getStringExtra(EXTRA_HABIT_TITLE) ?: "Your Habit"
        val notifType = intent.getStringExtra(EXTRA_NOTIFICATION_TYPE) ?: TYPE_ON_TIME

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val (title, message) = when (notifType) {
            TYPE_PRE_REMINDER -> Pair(
                "⏰ Upcoming in 5 mins: $habitTitle",
                "Starts shortly! Prepare now to keep your streak unbroken."
            )
            TYPE_EVENING_SHIELD -> Pair(
                "🛡️ Evening Streak Shield",
                "You have pending habits today! Complete them before midnight to protect your streak."
            )
            else -> Pair(
                "🔥 Time for $habitTitle",
                "Your scheduled window is now! Complete your session to keep your momentum alive."
            )
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notifId = (habitTitle.hashCode() + notifType.hashCode()).let { if (it < 0) -it else it }
        notificationManager.notify(notifId, notification)
    }
}
