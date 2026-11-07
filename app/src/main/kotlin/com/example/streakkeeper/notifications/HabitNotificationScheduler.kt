package com.example.streakkeeper.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.streakkeeper.ui.habit.HabitItem
import java.util.Calendar

/**
 * 100% Offline-First Alarm Scheduler.
 * Manages device-level exact alarms using AlarmManager:
 * 1. 5-minute pre-reminder before habit start window.
 * 2. On-time start reminder.
 * 3. Daily 8:00 PM Evening Streak Shield.
 */
object HabitNotificationScheduler {

    fun rescheduleAll(context: Context, habits: List<HabitItem>) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        // 1. Schedule 8:00 PM Evening Streak Shield
        scheduleEveningStreakShield(context, alarmManager)

        // 2. Schedule per-habit pre-reminders and on-time reminders
        habits.forEach { habit ->
            scheduleHabitReminders(context, alarmManager, habit)
        }
    }

    fun scheduleHabitReminders(context: Context, habit: HabitItem) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        scheduleHabitReminders(context, alarmManager, habit)
    }

    private fun scheduleHabitReminders(context: Context, alarmManager: AlarmManager, habit: HabitItem) {
        val timeStr = habit.timeOfDay
        val regex = Regex("""^(\d{1,2}):(\d{2})""")
        val match = regex.find(timeStr) ?: return
        val hour = match.groupValues[1].toIntOrNull() ?: return
        val minute = match.groupValues[2].toIntOrNull() ?: return

        // A. 5-Minute Pre-Reminder
        var preHour = hour
        var preMin = minute - 5
        if (preMin < 0) {
            preMin += 60
            preHour = (preHour - 1 + 24) % 24
        }
        scheduleExactDailyAlarm(
            context = context,
            alarmManager = alarmManager,
            requestCode = (habit.id.hashCode() * 31 + 1),
            hour = preHour,
            minute = preMin,
            habitId = habit.id,
            habitTitle = habit.title,
            notifType = HabitAlarmReceiver.TYPE_PRE_REMINDER
        )

        // B. On-Time Reminder
        scheduleExactDailyAlarm(
            context = context,
            alarmManager = alarmManager,
            requestCode = (habit.id.hashCode() * 31 + 2),
            hour = hour,
            minute = minute,
            habitId = habit.id,
            habitTitle = habit.title,
            notifType = HabitAlarmReceiver.TYPE_ON_TIME
        )
    }

    private fun scheduleEveningStreakShield(context: Context, alarmManager: AlarmManager) {
        scheduleExactDailyAlarm(
            context = context,
            alarmManager = alarmManager,
            requestCode = 9999,
            hour = 20, // 8:00 PM
            minute = 0,
            habitId = "evening_shield",
            habitTitle = "Streak Shield",
            notifType = HabitAlarmReceiver.TYPE_EVENING_SHIELD
        )
    }

    private fun scheduleExactDailyAlarm(
        context: Context,
        alarmManager: AlarmManager,
        requestCode: Int,
        hour: Int,
        minute: Int,
        habitId: String,
        habitTitle: String,
        notifType: String
    ) {
        val intent = Intent(context, HabitAlarmReceiver::class.java).apply {
            putExtra(HabitAlarmReceiver.EXTRA_HABIT_ID, habitId)
            putExtra(HabitAlarmReceiver.EXTRA_HABIT_TITLE, habitTitle)
            putExtra(HabitAlarmReceiver.EXTRA_NOTIFICATION_TYPE, notifType)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        }
    }

    fun cancelHabitReminders(context: Context, habitId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val preCode = (habitId.hashCode() * 31 + 1)
        val onCode = (habitId.hashCode() * 31 + 2)

        listOf(preCode, onCode).forEach { code ->
            val intent = Intent(context, HabitAlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                code,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent)
            }
        }
    }
}
