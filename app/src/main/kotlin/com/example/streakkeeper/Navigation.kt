package com.example.streakkeeper

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.streakkeeper.data.HabitStorageManager
import com.example.streakkeeper.notifications.HabitAlarmReceiver
import com.example.streakkeeper.notifications.HabitNotificationScheduler
import com.example.streakkeeper.ui.habit.CreateHabitScreen
import com.example.streakkeeper.ui.habit.HabitDedicatedScreen
import com.example.streakkeeper.ui.habit.HabitItem
import com.example.streakkeeper.ui.home.HomeScreen
import com.example.streakkeeper.ui.main.MainScreen
import com.example.streakkeeper.ui.onboarding.AccountSetupScreen
import com.example.streakkeeper.ui.splash.SplashScreen

@Composable
fun MainNavigation() {
  val context = LocalContext.current
  val existingProfile = remember { HabitStorageManager.loadUserProfile(context) }
  val initialKey = if (existingProfile != null) Home(userName = existingProfile.name) else Splash
  val backStack = rememberNavBackStack(initialKey)
  var habitsList by remember { mutableStateOf(HabitStorageManager.loadHabits(context)) }

  val notificationPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission(),
    onResult = { isGranted ->
      HabitAlarmReceiver.createNotificationChannel(context)
      if (isGranted) {
        HabitNotificationScheduler.rescheduleAll(context, habitsList)
      }
    }
  )

  androidx.compose.runtime.LaunchedEffect(Unit) {
    HabitAlarmReceiver.createNotificationChannel(context)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
      }
    }
    HabitNotificationScheduler.rescheduleAll(context, habitsList)
  }

  fun updateAndPersistHabits(newList: List<HabitItem>) {
    habitsList = newList
    HabitStorageManager.saveHabits(context, newList)
    HabitNotificationScheduler.rescheduleAll(context, newList)
  }

  NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    entryProvider =
      entryProvider {
        entry<Splash> {
          SplashScreen(
            onMakeItClick = {
              val profile = HabitStorageManager.loadUserProfile(context)
              if (profile != null) {
                backStack.add(Home(userName = profile.name))
              } else {
                backStack.add(AccountSetup)
              }
            }
          )
        }
        entry<AccountSetup> {
          AccountSetupScreen(
            onBackClick = {
              backStack.removeLastOrNull()
            },
            onContinueClick = { name, password ->
              val resolvedName = if (name.isNotBlank()) name else "Alex"
              HabitStorageManager.saveUserProfile(context, resolvedName, password)
              backStack.add(Home(userName = resolvedName))
            }
          )
        }
        entry<Home> { homeKey ->
          val streakCount = habitsList.count { it.isCompleted }
          HomeScreen(
            userName = homeKey.userName,
            streakCount = streakCount,
            habits = habitsList,
            onAddHabitClick = {
              backStack.add(CreateHabit)
            },
            onHabitClick = { habitId ->
              backStack.add(HabitDedicated(habitId))
            },
            onHabitToggle = { habitId ->
              val updated = habitsList.map { habit ->
                if (habit.id == habitId) {
                  val newCompleted = !habit.isCompleted
                  val newDays = if (newCompleted) habit.completedDays + 6 else habit.completedDays - 6
                  var chain = 0
                  for (d in 6 downTo 1) {
                    if (newDays.contains(d)) chain++ else break
                  }
                  habit.copy(
                    isCompleted = newCompleted,
                    completedDays = newDays,
                    streak = chain
                  )
                } else habit
              }
              updateAndPersistHabits(updated)
            },
            onHabitsImported = { importedHabits ->
              updateAndPersistHabits(importedHabits)
            }
          )
        }
        entry<HabitDedicated> { dedicatedKey ->
          val habit = habitsList.find { it.id == dedicatedKey.habitId }
          if (habit != null) {
            HabitDedicatedScreen(
              habit = habit,
              onBackClick = { backStack.removeLastOrNull() },
              onDayToggle = { dayNumber ->
                val updated = habitsList.map { h ->
                  if (h.id == habit.id) {
                    val wasDone = h.completedDays.contains(dayNumber)
                    val newCompleted = if (wasDone) h.completedDays - dayNumber else h.completedDays + dayNumber
                    val isTodayDone = newCompleted.contains(6)
                    var unbrokenChain = 0
                    for (d in 6 downTo 1) {
                      if (newCompleted.contains(d)) unbrokenChain++ else break
                    }
                    h.copy(
                      completedDays = newCompleted,
                      streak = unbrokenChain,
                      isCompleted = isTodayDone
                    )
                  } else h
                }
                updateAndPersistHabits(updated)
              },
              onSaveReflection = { note, _ ->
                val nowCal = java.util.Calendar.getInstance()
                val todayKey = String.format("%04d-%02d-%02d",
                    nowCal.get(java.util.Calendar.YEAR),
                    nowCal.get(java.util.Calendar.MONTH) + 1,
                    nowCal.get(java.util.Calendar.DAY_OF_MONTH)
                )
                val updated = habitsList.map { h ->
                  if (h.id == habit.id) {
                    val newReflections = h.reflections.toMutableMap()
                    newReflections[todayKey] = note
                    h.copy(reflections = newReflections)
                  } else h
                }
                updateAndPersistHabits(updated)
              }
            )
          }
        }
        entry<CreateHabit> {
          CreateHabitScreen(
            onBackClick = {
              backStack.removeLastOrNull()
            },
            onSaveHabit = { title, durationMinutes, timeOfDay ->
              val newHabit = HabitItem(
                id = System.currentTimeMillis().toString(),
                title = title,
                durationMinutes = durationMinutes,
                timeOfDay = timeOfDay,
                streak = 1,
                isCompleted = true,
                completedDays = setOf(6)
              )
              updateAndPersistHabits(habitsList + newHabit)
              backStack.removeLastOrNull()
            }
          )
        }
        entry<Main> {
          MainScreen(onItemClick = { navKey -> backStack.add(navKey) })
        }
      },
  )
}
