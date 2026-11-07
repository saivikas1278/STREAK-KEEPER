package com.example.streakkeeper.ui.habit

import android.content.Context
import android.media.AudioManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.streakkeeper.ui.theme.AccentSuccess
import com.example.streakkeeper.ui.theme.AccentWarning
import com.example.streakkeeper.ui.theme.BackgroundPrimary
import com.example.streakkeeper.ui.theme.BackgroundSecondary
import com.example.streakkeeper.ui.theme.BorderPrimary
import com.example.streakkeeper.ui.theme.BorderSubtle
import com.example.streakkeeper.ui.theme.CardElevated
import com.example.streakkeeper.ui.theme.CardPrimary
import com.example.streakkeeper.ui.theme.TextMuted
import com.example.streakkeeper.ui.theme.TextPrimary
import com.example.streakkeeper.ui.theme.TextSecondary

/**
 * Dedicated Habit Screen featuring a GitHub-style single-month contribution grid.
 * Allows user to view habit details and click individual date dots to toggle completion.
 */
@Composable
fun HabitDedicatedScreen(
    habit: HabitItem,
    modifier: Modifier = Modifier,
    initialYear: Int = 2026,
    initialMonth: Int = 11, // 1-12
    onBackClick: () -> Unit = {},
    onDayToggle: (Int) -> Unit = {},
    onSaveReflection: (note: String, vibe: String) -> Unit = { _, _ -> }
) {
    val scrollState = rememberScrollState()
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager }
    var selectedYear by remember { mutableIntStateOf(initialYear) }
    var selectedMonth by remember { mutableIntStateOf(initialMonth) }
    var showReflectionDialog by remember { mutableStateOf(false) }

    val monthNames = remember {
        listOf("JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER")
    }
    val currentMonthName = "${monthNames[selectedMonth - 1]} $selectedYear"

    val calendar = java.util.Calendar.getInstance().apply {
        set(java.util.Calendar.YEAR, selectedYear)
        set(java.util.Calendar.MONTH, selectedMonth - 1)
        set(java.util.Calendar.DAY_OF_MONTH, 1)
    }
    val totalDaysInMonth = calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)
    val dayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK)
    val firstDayOfWeekOffset = (dayOfWeek + 5) % 7 // Monday=0, Sunday=6

    val completedCount = habit.completedDays.size
    val completionPercentage = if (totalDaysInMonth > 0) {
        (completedCount * 100) / totalDaysInMonth
    } else 0

    val nowCal = remember { java.util.Calendar.getInstance() }
    val currentYear = nowCal.get(java.util.Calendar.YEAR)
    val currentMonth = nowCal.get(java.util.Calendar.MONTH) + 1
    val currentDay = nowCal.get(java.util.Calendar.DAY_OF_MONTH)
    val todayDateKey = String.format("%04d-%02d-%02d", currentYear, currentMonth, currentDay)
    val monthAbbr = listOf("JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC").getOrElse(currentMonth - 1) { "NOV" }
    val formattedDateHeader = String.format("DAILY SESSION LOG • %s %02d", monthAbbr, currentDay)
    val todayReflection = habit.reflections[todayDateKey]

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // --- 1. Top Navigation Bar ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(CardPrimary, RoundedCornerShape(12.dp))
                        .border(1.dp, BorderPrimary, RoundedCornerShape(12.dp))
                        .clickable { onBackClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "←",
                        color = TextSecondary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "DISCIPLINE OVERVIEW",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.4.sp
                )

                Spacer(modifier = Modifier.size(38.dp))
            }

            // --- 2. Bento Card 1: Habit Title & Schedule Info ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardPrimary, RoundedCornerShape(20.dp))
                    .border(1.dp, BorderPrimary, RoundedCornerShape(20.dp))
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TARGET HABIT",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                    Box(
                        modifier = Modifier
                            .background(BackgroundSecondary, RoundedCornerShape(6.dp))
                            .border(1.dp, BorderSubtle, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = habit.timeOfDay,
                            color = AccentWarning,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Text(
                    text = habit.title,
                    color = TextPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    fontFamily = FontFamily.SansSerif
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(CardElevated, RoundedCornerShape(8.dp))
                            .border(1.dp, BorderPrimary, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (habit.durationMinutes >= 60) "${habit.durationMinutes / 60}H DURATION" else "${habit.durationMinutes}M DURATION",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(Color(0xFF161A17), RoundedCornerShape(8.dp))
                            .border(1.dp, AccentSuccess.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "ACTIVE ROUTINE",
                            color = AccentSuccess,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // --- 3. Bento Card 2: GitHub-Style Monthly Contribution Grid ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardPrimary, RoundedCornerShape(20.dp))
                    .border(1.dp, BorderPrimary, RoundedCornerShape(20.dp))
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Month Header with Multi-Month Navigation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Previous Month Button
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(CardElevated, RoundedCornerShape(8.dp))
                                .border(1.dp, BorderPrimary, RoundedCornerShape(8.dp))
                                .clickable {
                                    if (selectedMonth == 1) {
                                        selectedMonth = 12
                                        selectedYear -= 1
                                    } else {
                                        selectedMonth -= 1
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("‹", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }

                        Text(
                            text = currentMonthName,
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            fontFamily = FontFamily.SansSerif
                        )

                        // Next Month Button
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(CardElevated, RoundedCornerShape(8.dp))
                                .border(1.dp, BorderPrimary, RoundedCornerShape(8.dp))
                                .clickable {
                                    if (selectedMonth == 12) {
                                        selectedMonth = 1
                                        selectedYear += 1
                                    } else {
                                        selectedMonth += 1
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("›", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Text(
                        text = "1-TAP TOGGLE",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                // Days of Week Row (Mon to Sun)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val daysOfWeek = listOf("M", "T", "W", "T", "F", "S", "S")
                    daysOfWeek.forEach { dayName ->
                        Text(
                            text = dayName,
                            color = Color(0xFF505050),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // 7-Column Grid of days
                val totalSlots = firstDayOfWeekOffset + totalDaysInMonth
                val totalRows = (totalSlots + 6) / 7

                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (row in 0 until totalRows) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            for (col in 0 until 7) {
                                val slotIndex = row * 7 + col
                                val dayNumber = slotIndex - firstDayOfWeekOffset + 1

                                if (dayNumber in 1..totalDaysInMonth) {
                                    val isFilled = habit.completedDays.contains(dayNumber)
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1f)
                                            .background(
                                                if (isFilled) AccentSuccess else CardElevated,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .border(
                                                1.dp,
                                                if (isFilled) AccentSuccess else BorderPrimary,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .clickable {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                audioManager?.playSoundEffect(AudioManager.FX_KEY_CLICK, 0.8f)
                                                onDayToggle(dayNumber)
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$dayNumber",
                                            color = if (isFilled) BackgroundPrimary else TextMuted,
                                            fontSize = 11.sp,
                                            fontWeight = if (isFilled) FontWeight.Bold else FontWeight.Medium
                                        )
                                    }
                                } else {
                                    // Empty blank cell for alignment
                                    Spacer(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1f)
                                    )
                                }
                            }
                        }
                    }
                }

                // Heatmap Legend
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(modifier = Modifier.size(6.dp).background(AccentSuccess, RoundedCornerShape(2.dp)))
                            Text(text = "COMPLETED", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(modifier = Modifier.size(6.dp).background(CardElevated, RoundedCornerShape(2.dp)))
                            Text(text = "PENDING", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "LESS", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Box(modifier = Modifier.size(8.dp).background(CardElevated, RoundedCornerShape(2.dp)).border(1.dp, BorderPrimary, RoundedCornerShape(2.dp)))
                        Box(modifier = Modifier.size(8.dp).background(Color(0xFF38BDF8), RoundedCornerShape(2.dp)))
                        Box(modifier = Modifier.size(8.dp).background(AccentSuccess, RoundedCornerShape(2.dp)))
                        Text(text = "MORE", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // --- 4. Bento Card 3: Consistency Metrics ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Streak Card
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(CardPrimary, RoundedCornerShape(18.dp))
                        .border(1.dp, BorderPrimary, RoundedCornerShape(18.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "STREAK",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "$completedCount",
                            color = TextPrimary,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif,
                            lineHeight = 30.sp
                        )
                        Text(
                            text = "DAYS",
                            color = AccentWarning,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 3.dp)
                        )
                    }
                    Text(
                        text = "Consecutive logged",
                        color = Color(0xFF505050),
                        fontSize = 10.sp
                    )
                }

                // Month Rate Card
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(CardPrimary, RoundedCornerShape(18.dp))
                        .border(1.dp, BorderPrimary, RoundedCornerShape(18.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "MONTH RATE",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "$completionPercentage%",
                            color = AccentSuccess,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif,
                            lineHeight = 30.sp
                        )
                        Text(
                            text = "$completedCount/$totalDaysInMonth",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 3.dp)
                        )
                    }
                    Text(
                        text = "Monthly consistency",
                        color = Color(0xFF505050),
                        fontSize = 10.sp
                    )
                }
            }

            // --- 5. Bento Card 4: Daily Session Log & Reflection (Dynamic Date) ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardPrimary, RoundedCornerShape(20.dp))
                    .border(1.dp, BorderPrimary, RoundedCornerShape(20.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
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
                        Box(modifier = Modifier.size(6.dp).background(AccentSuccess, androidx.compose.foundation.shape.CircleShape))
                        Text(
                            text = formattedDateHeader,
                            color = TextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    Text(
                        text = if (todayReflection.isNullOrBlank()) "✍️ LOG NOTE" else "✏️ EDIT NOTE",
                        color = AccentSuccess,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { showReflectionDialog = true }
                    )
                }

                if (!todayReflection.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0F1A12), RoundedCornerShape(12.dp))
                            .border(1.dp, AccentSuccess.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .clickable { showReflectionDialog = true }
                            .padding(12.dp)
                    ) {
                        Text(
                            text = todayReflection,
                            color = TextPrimary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CardElevated, RoundedCornerShape(12.dp))
                            .border(1.dp, BorderPrimary, RoundedCornerShape(12.dp))
                            .clickable { showReflectionDialog = true }
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No reflection recorded yet for today. Tap anywhere here to add how your session went.",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (showReflectionDialog) {
            DailyReflectionDialog(
                habitTitle = habit.title,
                initialNote = todayReflection ?: "",
                onDismiss = { showReflectionDialog = false },
                onSaveReflection = { note, vibe ->
                    showReflectionDialog = false
                    onSaveReflection(note, vibe)
                }
            )
        }
    }
}
