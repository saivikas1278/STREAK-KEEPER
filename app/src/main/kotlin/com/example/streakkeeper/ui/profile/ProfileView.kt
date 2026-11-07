package com.example.streakkeeper.ui.profile

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.streakkeeper.data.HabitStorageManager
import com.example.streakkeeper.ui.habit.HabitItem
import com.example.streakkeeper.ui.theme.AccentSuccess
import com.example.streakkeeper.ui.theme.AccentWarning
import com.example.streakkeeper.ui.theme.BackgroundPrimary
import com.example.streakkeeper.ui.theme.CardElevated
import com.example.streakkeeper.ui.theme.CardPrimary
import com.example.streakkeeper.ui.theme.BorderPrimary
import com.example.streakkeeper.ui.theme.TextMuted
import com.example.streakkeeper.ui.theme.TextPrimary
import com.example.streakkeeper.ui.theme.TextSecondary

@Composable
fun ProfileView(
    userName: String = "Alex",
    habits: List<HabitItem> = emptyList(),
    modifier: Modifier = Modifier,
    onLogoutClick: () -> Unit = {},
    onImportSuccess: (List<HabitItem>) -> Unit = {}
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var selectedDossierHabit by remember { mutableStateOf<HabitItem?>(null) }

    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val jsonString = HabitStorageManager.exportVaultJson(context)
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(jsonString.toByteArray(Charsets.UTF_8))
                }
                Toast.makeText(context, "Backup exported successfully!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Export failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val openDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val content = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    inputStream.bufferedReader(Charsets.UTF_8).readText()
                }
                if (!content.isNullOrBlank()) {
                    val importedHabits = HabitStorageManager.importVaultJson(context, content)
                    if (importedHabits.isNotEmpty()) {
                        onImportSuccess(importedHabits)
                        Toast.makeText(context, "Restored ${importedHabits.size} habits successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "No valid habits found in file", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Restore failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 1. Calculations
    val totalHabits = habits.size
    val bestStreak = habits.maxOfOrNull { it.streak } ?: 0
    val totalTicks = habits.sumOf { it.completedDays.size }
    val maxPossibleTicks = (totalHabits * 30).coerceAtLeast(1)
    val overallScore = if (totalHabits > 0) ((totalTicks.toFloat() / maxPossibleTicks.toFloat()) * 100).toInt().coerceIn(0, 100) else 0

    val animatedProgress by animateFloatAsState(
        targetValue = overallScore / 100f,
        animationSpec = tween(durationMillis = 1000),
        label = "RadialProgress"
    )

    // 2. Weekly Day-of-Week distribution for Nov 2026
    val dayCounts = IntArray(7) { 0 }
    habits.forEach { h ->
        h.completedDays.forEach { d ->
            if (d in 1..30) {
                val dow = (d + 5) % 7
                dayCounts[dow] += 1
            }
        }
    }
    val maxDayCount = dayCounts.maxOrNull()?.coerceAtLeast(1) ?: 1
    val dayLabels = listOf("M", "T", "W", "T", "F", "S", "S")

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(top = 4.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // BENTO 1: Hero Identity & Animated Radial Score
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardPrimary, RoundedCornerShape(20.dp))
                .border(1.dp, BorderPrimary, RoundedCornerShape(20.dp))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(modifier = Modifier.size(6.dp).background(AccentSuccess, CircleShape))
                    Text(
                        text = "VAULT MASTER • 100% OFFLINE",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
                Text(
                    text = userName.uppercase(),
                    color = TextPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif
                )
                Box(
                    modifier = Modifier
                        .background(Color(0xFF1F1A12), RoundedCornerShape(6.dp))
                        .border(1.dp, AccentWarning.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (overallScore >= 80) "APEX ARCHITECT" else if (overallScore >= 50) "STEADY MOMENTUM" else "BUILDING HABIT",
                        color = AccentWarning,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Radial Gauge
            Box(
                modifier = Modifier.size(80.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Canvas(modifier = Modifier.size(80.dp)) {
                    drawArc(
                        color = Color(0xFF1F1F1F),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = AccentSuccess,
                        startAngle = -90f,
                        sweepAngle = 360f * animatedProgress,
                        useCenter = false,
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$overallScore%",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif
                    )
                    Text(
                        text = "SCORE",
                        color = TextMuted,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // BENTO 2: 4-Metrics Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricCard(
                label = "ACTIVE HABITS",
                value = "$totalHabits",
                unit = "DISCIPLINES",
                valueColor = TextPrimary,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                label = "BEST STREAK",
                value = "$bestStreak",
                unit = "DAYS",
                valueColor = AccentWarning,
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricCard(
                label = "DAYS LOGGED",
                value = "$totalTicks",
                unit = "TOTAL",
                valueColor = AccentSuccess,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                label = "CONSISTENCY",
                value = "$overallScore%",
                unit = "MONTH",
                valueColor = TextPrimary,
                modifier = Modifier.weight(1f)
            )
        }

        // BENTO 3: Active Disciplines & 12-Month Dossier Trigger
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardPrimary, RoundedCornerShape(20.dp))
                .border(1.dp, BorderPrimary, RoundedCornerShape(20.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ACTIVE DISCIPLINES",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Box(
                    modifier = Modifier
                        .background(Color(0xFF161A17), RoundedCornerShape(4.dp))
                        .border(1.dp, AccentSuccess.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "$totalHabits IN VAULT",
                        color = AccentSuccess,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = "Tap any discipline to inspect its full 12-month dossier & daily reflections.",
                color = TextSecondary,
                fontSize = 11.sp
            )

            if (habits.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardElevated, RoundedCornerShape(12.dp))
                        .border(1.dp, BorderPrimary, RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No disciplines created yet. Tap LIST to start your first habit.",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            } else {
                habits.forEach { habit ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CardElevated, RoundedCornerShape(14.dp))
                            .border(1.dp, BorderPrimary, RoundedCornerShape(14.dp))
                            .clickable { selectedDossierHabit = habit }
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = habit.title.uppercase(),
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF1E1E1E), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "${habit.durationMinutes}m",
                                        color = TextMuted,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = habit.timeOfDay.ifBlank { "Active Daily" },
                                    color = TextMuted,
                                    fontSize = 9.sp
                                )
                                Text(
                                    text = "•",
                                    color = TextMuted,
                                    fontSize = 9.sp
                                )
                                Text(
                                    text = "🔥 ${habit.streak} DAYS",
                                    color = AccentWarning,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .background(Color(0xFF131D15), RoundedCornerShape(8.dp))
                                .border(1.dp, AccentSuccess.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "12-MONTH DOSSIER ➔",
                                color = AccentSuccess,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // BENTO 4: Weekly Consistency Rhythm (Bar Chart)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardPrimary, RoundedCornerShape(20.dp))
                .border(1.dp, BorderPrimary, RoundedCornerShape(20.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "WEEKLY CONSISTENCY RHYTHM",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Box(
                    modifier = Modifier
                        .background(Color(0xFF161A17), RoundedCornerShape(4.dp))
                        .border(1.dp, AccentSuccess.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "PEAK: FRIDAY",
                        color = AccentSuccess,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 7 Columns
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                dayCounts.forEachIndexed { idx, count ->
                    val fraction = (count.toFloat() / maxDayCount.toFloat()).coerceIn(0.12f, 1f)
                    val isPeak = count == dayCounts.maxOrNull() && count > 0

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "$count",
                            color = TextMuted,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .width(22.dp)
                                .height((56 * fraction).dp)
                                .background(
                                    if (isPeak) AccentSuccess else CardElevated,
                                    RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = dayLabels[idx],
                            color = TextMuted,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // BENTO 5: Habit-Specific Discipline Milestones
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardPrimary, RoundedCornerShape(20.dp))
                .border(1.dp, BorderPrimary, RoundedCornerShape(20.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val totalMilestonesUnlocked = habits.sumOf { habit ->
                val s = habit.streak.coerceAtLeast(habit.completedDays.size)
                listOf(s >= 7, s >= 30, s >= 50, s >= 70, s >= 100).count { it }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DISCIPLINE MILESTONES BY HABIT",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "$totalMilestonesUnlocked EARNED",
                    color = AccentSuccess,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (habits.isEmpty()) {
                Text(
                    text = "Create your first discipline to start earning habit-specific milestone badges.",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontStyle = FontStyle.Italic
                )
            } else {
                habits.forEach { habit ->
                    val habitStreak = habit.streak.coerceAtLeast(habit.completedDays.size)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CardElevated, RoundedCornerShape(14.dp))
                            .border(1.dp, BorderPrimary, RoundedCornerShape(14.dp))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Habit Name Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = habit.title.uppercase(),
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "CURRENT: $habitStreak DAYS",
                                color = if (habitStreak > 0) AccentWarning else TextMuted,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Milestone Grid for this habit
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            HabitMilestonePill(
                                title = "7D FIRE",
                                habitName = habit.title,
                                icon = "🔥",
                                isUnlocked = habitStreak >= 7,
                                currentProgress = habitStreak,
                                targetDays = 7,
                                modifier = Modifier.weight(1f)
                            )
                            HabitMilestonePill(
                                title = "30D TITAN",
                                habitName = habit.title,
                                icon = "🛡️",
                                isUnlocked = habitStreak >= 30,
                                currentProgress = habitStreak,
                                targetDays = 30,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            HabitMilestonePill(
                                title = "50D RELENTLESS",
                                habitName = habit.title,
                                icon = "⚡",
                                isUnlocked = habitStreak >= 50,
                                currentProgress = habitStreak,
                                targetDays = 50,
                                modifier = Modifier.weight(1f)
                            )
                            HabitMilestonePill(
                                title = "70D CHAMPION",
                                habitName = habit.title,
                                icon = "⚔️",
                                isUnlocked = habitStreak >= 70,
                                currentProgress = habitStreak,
                                targetDays = 70,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        HabitMilestonePill(
                            title = "100D CENTURY MASTER",
                            habitName = habit.title,
                            icon = "👑",
                            isUnlocked = habitStreak >= 100,
                            currentProgress = habitStreak,
                            targetDays = 100,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // BENTO 6: Local Vault Backup & Security
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardPrimary, RoundedCornerShape(20.dp))
                .border(1.dp, BorderPrimary, RoundedCornerShape(20.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LOCAL VAULT BACKUP",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "OFFLINE JSON",
                    color = AccentSuccess,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "Export your habit progress, streaks, and reflections as an offline JSON backup, or restore anytime.",
                color = TextSecondary,
                fontSize = 11.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Export Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(CardElevated, RoundedCornerShape(12.dp))
                        .border(1.dp, BorderPrimary, RoundedCornerShape(12.dp))
                        .clickable {
                            val defaultName = "streakkeeper_backup_${System.currentTimeMillis() / 1000}.json"
                            createDocumentLauncher.launch(defaultName)
                        }
                        .padding(vertical = 10.dp, horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "⬆", fontSize = 11.sp, color = AccentWarning)
                        Text(
                            text = "EXPORT JSON",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Restore Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(CardElevated, RoundedCornerShape(12.dp))
                        .border(1.dp, BorderPrimary, RoundedCornerShape(12.dp))
                        .clickable {
                            openDocumentLauncher.launch(arrayOf("application/json", "text/*", "*/*"))
                        }
                        .padding(vertical = 10.dp, horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "⬇", fontSize = 11.sp, color = AccentSuccess)
                        Text(
                            text = "RESTORE JSON",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    // Interactive 12-Month Dossier Dialog
    if (selectedDossierHabit != null) {
        YearlyDossierDialog(
            habit = selectedDossierHabit!!,
            onDismiss = { selectedDossierHabit = null }
        )
    }
}

data class MonthDossierInfo(
    val monthNumber: Int,
    val shortName: String,
    val fullName: String,
    val totalDays: Int
)

@Composable
fun YearlyDossierDialog(
    habit: HabitItem,
    onDismiss: () -> Unit
) {
    val months = remember {
        listOf(
            MonthDossierInfo(1, "JAN", "JANUARY", 31),
            MonthDossierInfo(2, "FEB", "FEBRUARY", 28),
            MonthDossierInfo(3, "MAR", "MARCH", 31),
            MonthDossierInfo(4, "APR", "APRIL", 30),
            MonthDossierInfo(5, "MAY", "MAY", 31),
            MonthDossierInfo(6, "JUN", "JUNE", 30),
            MonthDossierInfo(7, "JUL", "JULY", 31),
            MonthDossierInfo(8, "AUG", "AUGUST", 31),
            MonthDossierInfo(9, "SEP", "SEPTEMBER", 30),
            MonthDossierInfo(10, "OCT", "OCTOBER", 31),
            MonthDossierInfo(11, "NOV", "NOVEMBER", 30),
            MonthDossierInfo(12, "DEC", "DECEMBER", 31)
        )
    }

    var inspectedMonth by remember { mutableStateOf(11) } // Default to November
    var inspectedDay by remember { mutableStateOf(6) }     // Default to Nov 6 (Today)

    val inspectedMonthInfo = months.firstOrNull { it.monthNumber == inspectedMonth } ?: months[10]

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF090909))
                .padding(horizontal = 14.dp, vertical = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(modifier = Modifier.size(6.dp).background(AccentSuccess, CircleShape))
                            Text(
                                text = "2026 ANNUAL DOSSIER",
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                        Text(
                            text = habit.title.uppercase(),
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(CardElevated, CircleShape)
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✕", color = TextMuted, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Stats Banner
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(CardPrimary, RoundedCornerShape(12.dp))
                            .border(1.dp, BorderPrimary, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("🔥", fontSize = 14.sp)
                            Column {
                                Text(text = "${habit.streak} DAYS", color = AccentWarning, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(text = "CURRENT STREAK", color = TextMuted, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(CardPrimary, RoundedCornerShape(12.dp))
                            .border(1.dp, BorderPrimary, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("⏰", fontSize = 14.sp)
                            Column {
                                Text(text = habit.timeOfDay.ifBlank { "07:00 – 07:30" }, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(text = "SCHEDULE WINDOW", color = TextMuted, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Subtitle Instruction
                Text(
                    text = "12-Month Year-at-a-Glance. Tap any dot to inspect day completion & reflections.",
                    color = TextSecondary,
                    fontSize = 10.sp
                )

                // 12 Months: Strict 2 Columns Side-by-Side (6 Rows)
                val monthPairs = months.chunked(2)
                monthPairs.forEach { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        pair.forEach { monthInfo ->
                            MonthCompactCard(
                                monthInfo = monthInfo,
                                habit = habit,
                                isInspected = inspectedMonth == monthInfo.monthNumber,
                                inspectedDay = if (inspectedMonth == monthInfo.monthNumber) inspectedDay else null,
                                onSelectDay = { day ->
                                    inspectedMonth = monthInfo.monthNumber
                                    inspectedDay = day
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (pair.size < 2) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

                // Interactive Day Inspector Strip (Pinned below Grid)
                val dateKey = "2026-${inspectedMonth.toString().padStart(2, '0')}-${inspectedDay.toString().padStart(2, '0')}"
                val isCompleted = inspectedMonth == 11 && habit.completedDays.contains(inspectedDay)
                val reflectionText = habit.reflections[dateKey]

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardPrimary, RoundedCornerShape(16.dp))
                        .border(1.dp, AccentSuccess.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(
                                        if (isCompleted) AccentSuccess else Color(0xFF333333),
                                        CircleShape
                                    )
                            )
                            Text(
                                text = "${inspectedMonthInfo.fullName} ${inspectedDay.toString().padStart(2, '0')}, 2026${if (inspectedMonth == 11 && inspectedDay == 6) " (TODAY)" else ""}",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(
                                    if (isCompleted) Color(0xFF16291B) else Color(0xFF1E1E1E),
                                    RoundedCornerShape(4.dp)
                                )
                                .border(
                                    1.dp,
                                    if (isCompleted) AccentSuccess.copy(alpha = 0.5f) else BorderPrimary,
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isCompleted) "✓ COMPLETED" else "NOT LOGGED",
                                color = if (isCompleted) AccentSuccess else TextMuted,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Reflection Note Display
                    if (!reflectionText.isNullOrBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF0E1A11), RoundedCornerShape(8.dp))
                                .border(1.dp, AccentSuccess.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("📝", fontSize = 12.sp)
                                Text(
                                    text = reflectionText,
                                    color = TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    } else {
                        Text(
                            text = if (inspectedMonth == 11 && inspectedDay == 6)
                                "Today's active session. Complete today to record your daily reflection note."
                            else
                                "No reflection note recorded for this date.",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }

                // Close Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CardElevated),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("CLOSE DOSSIER", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun MonthCompactCard(
    monthInfo: MonthDossierInfo,
    habit: HabitItem,
    isInspected: Boolean,
    inspectedDay: Int?,
    onSelectDay: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val completedCount = if (monthInfo.monthNumber == 11) habit.completedDays.size else 0

    Column(
        modifier = modifier
            .background(CardPrimary, RoundedCornerShape(12.dp))
            .border(
                1.dp,
                if (isInspected) AccentSuccess.copy(alpha = 0.6f) else BorderPrimary,
                RoundedCornerShape(12.dp)
            )
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Month Title & Stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = monthInfo.shortName,
                color = if (isInspected) AccentSuccess else TextPrimary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (monthInfo.monthNumber == 11) "$completedCount/${monthInfo.totalDays}" else "0/${monthInfo.totalDays}",
                color = TextMuted,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Dot Grid: chunked into rows of 7
        val dayRows = (1..monthInfo.totalDays).chunked(7)
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            dayRows.forEach { rowDays ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    rowDays.forEach { day ->
                        val isDone = monthInfo.monthNumber == 11 && habit.completedDays.contains(day)
                        val isSelected = inspectedDay == day

                        val dotColor = if (isDone) AccentSuccess else Color(0xFF222222)

                        Box(
                            modifier = Modifier
                                .size(9.dp)
                                .background(dotColor, CircleShape)
                                .border(
                                    if (isSelected) 1.5.dp else 0.dp,
                                    if (isSelected) Color.White else Color.Transparent,
                                    CircleShape
                                )
                                .clickable { onSelectDay(day) }
                        )
                    }
                    if (rowDays.size < 7) {
                        repeat(7 - rowDays.size) {
                            Spacer(modifier = Modifier.size(9.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    label: String,
    value: String,
    unit: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(CardPrimary, RoundedCornerShape(18.dp))
            .border(1.dp, BorderPrimary, RoundedCornerShape(18.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(
            text = label,
            color = TextMuted,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        )
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = value,
                color = valueColor,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )
            Text(
                text = unit,
                color = TextMuted,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun HabitMilestonePill(
    title: String,
    habitName: String,
    icon: String,
    isUnlocked: Boolean,
    currentProgress: Int,
    targetDays: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                if (isUnlocked) Color(0xFF141D16) else Color(0xFF101010),
                RoundedCornerShape(10.dp)
            )
            .border(
                1.dp,
                if (isUnlocked) AccentSuccess.copy(alpha = 0.5f) else BorderPrimary,
                RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(
                    if (isUnlocked) AccentSuccess else Color(0xFF1E1E1E),
                    RoundedCornerShape(6.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = icon,
                fontSize = 11.sp
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                color = if (isUnlocked) TextPrimary else TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (isUnlocked) "UNLOCKED • $habitName" else "$currentProgress / $targetDays DAYS",
                color = if (isUnlocked) AccentSuccess else TextMuted.copy(alpha = 0.7f),
                fontSize = 8.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun MilestonePill(
    title: String,
    subtitle: String,
    icon: String,
    isUnlocked: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                if (isUnlocked) Color(0xFF141D16) else CardElevated,
                RoundedCornerShape(12.dp)
            )
            .border(
                1.dp,
                if (isUnlocked) AccentSuccess.copy(alpha = 0.4f) else BorderPrimary,
                RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(
                    if (isUnlocked) AccentSuccess else Color(0xFF222222),
                    RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = icon,
                fontSize = 13.sp,
                color = if (isUnlocked) BackgroundPrimary else TextMuted
            )
        }
        Column {
            Text(
                text = title,
                color = if (isUnlocked) TextPrimary else TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                color = TextMuted,
                fontSize = 8.sp
            )
        }
    }
}
