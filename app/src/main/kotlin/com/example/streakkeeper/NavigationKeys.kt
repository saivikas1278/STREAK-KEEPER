package com.example.streakkeeper

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable data object Splash : NavKey
@Serializable data object AccountSetup : NavKey
@Serializable data class Home(val userName: String = "Alex") : NavKey
@Serializable data object CreateHabit : NavKey
@Serializable data class HabitDedicated(val habitId: String) : NavKey
@Serializable data object Main : NavKey
