package com.example.streakkeeper.data

import android.content.Context
import com.example.streakkeeper.ui.habit.HabitItem
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

data class UserProfile(
    val name: String,
    val passwordHash: String,
    val isOnboarded: Boolean
)

/**
 * 100% Offline-First Local Storage Manager.
 * Persists habits, schedules, and GitHub contribution grid days
 * directly into the app's private files directory (Zero server costs, complete privacy).
 */
object HabitStorageManager {
    private const val FILE_NAME = "habits_vault.json"
    private const val PREFS_NAME = "streakkeeper_prefs"

    fun saveUserProfile(context: Context, name: String, passwordHash: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString("user_name", name)
            .putString("password_hash", passwordHash)
            .putBoolean("is_onboarded", true)
            .apply()
    }

    fun loadUserProfile(context: Context): UserProfile? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isOnboarded = prefs.getBoolean("is_onboarded", false)
        if (!isOnboarded) return null
        val name = prefs.getString("user_name", "Alex") ?: "Alex"
        val hash = prefs.getString("password_hash", "") ?: ""
        return UserProfile(name, hash, true)
    }

    fun clearUserProfile(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }

    fun saveHabits(context: Context, habits: List<HabitItem>) {
        try {
            val file = File(context.filesDir, FILE_NAME)
            val jsonArray = JSONArray()
            habits.forEach { habit ->
                val obj = JSONObject().apply {
                    put("id", habit.id)
                    put("title", habit.title)
                    put("durationMinutes", habit.durationMinutes)
                    put("timeOfDay", habit.timeOfDay)
                    put("streak", habit.streak)
                    put("isCompleted", habit.isCompleted)
                    val daysArray = JSONArray()
                    habit.completedDays.forEach { daysArray.put(it) }
                    put("completedDays", daysArray)
                    val refObj = JSONObject()
                    habit.reflections.forEach { (k, v) -> refObj.put(k, v) }
                    put("reflections", refObj)
                }
                jsonArray.put(obj)
            }
            file.writeText(jsonArray.toString(2))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadHabits(context: Context): List<HabitItem> {
        val file = File(context.filesDir, FILE_NAME)
        if (!file.exists()) return emptyList()

        return try {
            val content = file.readText()
            val jsonArray = JSONArray(content)
            val list = mutableListOf<HabitItem>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val daysSet = mutableSetOf<Int>()
                val daysArr = obj.optJSONArray("completedDays")
                if (daysArr != null) {
                    for (j in 0 until daysArr.length()) {
                        daysSet.add(daysArr.getInt(j))
                    }
                }
                val refMap = mutableMapOf<String, String>()
                val refObj = obj.optJSONObject("reflections")
                if (refObj != null) {
                    val keys = refObj.keys()
                    while (keys.hasNext()) {
                        val k = keys.next()
                        refMap[k] = refObj.getString(k)
                    }
                }
                list.add(
                    HabitItem(
                        id = obj.optString("id", "${System.currentTimeMillis()}_$i"),
                        title = obj.optString("title", "Habit"),
                        durationMinutes = obj.optInt("durationMinutes", 30),
                        timeOfDay = obj.optString("timeOfDay", "07:00 – 07:30"),
                        streak = obj.optInt("streak", 0),
                        isCompleted = obj.optBoolean("isCompleted", false),
                        completedDays = daysSet,
                        reflections = refMap
                    )
                )
            }
            list
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun exportVaultJson(context: Context): String {
        val file = File(context.filesDir, FILE_NAME)
        return if (file.exists()) file.readText() else "[]"
    }

    fun importVaultJson(context: Context, jsonStr: String): List<HabitItem> {
        return try {
            val file = File(context.filesDir, FILE_NAME)
            file.writeText(jsonStr)
            loadHabits(context)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
