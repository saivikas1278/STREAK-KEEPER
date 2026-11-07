package com.example.streakkeeper.ui.habit

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.serialization.Serializable
import com.example.streakkeeper.ui.theme.AccentError
import com.example.streakkeeper.ui.theme.AccentWarning
import com.example.streakkeeper.ui.theme.BackgroundPrimary
import com.example.streakkeeper.ui.theme.BackgroundSecondary
import com.example.streakkeeper.ui.theme.BorderPrimary
import com.example.streakkeeper.ui.theme.BorderSubtle
import com.example.streakkeeper.ui.theme.ButtonPrimaryBg
import com.example.streakkeeper.ui.theme.ButtonPrimaryText
import com.example.streakkeeper.ui.theme.CardElevated
import com.example.streakkeeper.ui.theme.CardPrimary
import com.example.streakkeeper.ui.theme.TextDisabled
import com.example.streakkeeper.ui.theme.TextMuted
import com.example.streakkeeper.ui.theme.TextPrimary
import com.example.streakkeeper.ui.theme.TextSecondary

enum class TimeOfDayOption(val label: String) {
    ANYTIME("ANYTIME"),
    MORNING("MORNING"),
    AFTERNOON("NOON"),
    EVENING("NIGHT")
}

@Serializable
data class HabitItem(
    val id: String,
    val title: String,
    val durationMinutes: Int,
    val timeOfDay: String,
    val streak: Int = 0,
    val isCompleted: Boolean = false,
    val completedDays: Set<Int> = emptySet(),
    val reflections: Map<String, String> = emptyMap()
)

/**
 * Dedicated Create Habit Screen adhering to Dark Swiss Minimalism + Modern Bento Grid.
 * Collects:
 * 1. Title / Headline
 * 2. Duration / Timer (Hours & Minutes stepper + presets)
 * 3. Preferred Time of Day (Optional)
 */
@Composable
fun CreateHabitScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onSaveHabit: (title: String, durationMinutes: Int, timeOfDay: String) -> Unit = { _, _, _ -> }
) {
    var habitTitle by remember { mutableStateOf("") }
    var durationMinutes by remember { mutableIntStateOf(30) }
    var startHour by remember { mutableIntStateOf(7) }
    var startMinute by remember { mutableIntStateOf(0) }
    var isFlexible by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    // Formatted duration label
    val durationFormatted = remember(durationMinutes) {
        if (durationMinutes >= 60) {
            val hrs = durationMinutes / 60
            val mins = durationMinutes % 60
            if (mins == 0) "$hrs ${if (hrs == 1) "HR" else "HRS"}" else "${hrs}H ${mins}M"
        } else {
            "$durationMinutes MIN"
        }
    }

    val totalEndMinutes = (startHour * 60 + startMinute + durationMinutes) % (24 * 60)
    val endHour = totalEndMinutes / 60
    val endMinute = totalEndMinutes % 60
    val timeWindowFormatted = remember(startHour, startMinute, endHour, endMinute, isFlexible) {
        if (isFlexible) "ANYTIME"
        else String.format("%02d:%02d – %02d:%02d", startHour, startMinute, endHour, endMinute)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- 1. Minimalist Top Header ---
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
                    text = "NEW DISCIPLINE",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.4.sp
                )

                // Placeholder for symmetry
                Spacer(modifier = Modifier.size(38.dp))
            }

            // --- 2. Bento Grid Form Content ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header Titles
                Column(modifier = Modifier.padding(bottom = 2.dp)) {
                    Text(
                        text = "ADD HABIT",
                        color = TextPrimary,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Define your daily commitment with precision.",
                        color = TextSecondary,
                        fontSize = 15.sp,
                        fontStyle = FontStyle.Italic
                    )
                }

                // --- Bento Card 1: Habit Title / Headline ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardPrimary, RoundedCornerShape(18.dp))
                        .border(
                            1.dp,
                            if (showError) AccentError else BorderPrimary,
                            RoundedCornerShape(18.dp)
                        )
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "HABIT TITLE / HEADLINE",
                        color = if (showError) AccentError else TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )

                    BasicTextField(
                        value = habitTitle,
                        onValueChange = {
                            habitTitle = it
                            if (showError && it.isNotBlank()) showError = false
                        },
                        textStyle = TextStyle(
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        cursorBrush = SolidColor(TextPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CardElevated, RoundedCornerShape(12.dp))
                            .border(1.dp, BorderPrimary, RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        decorationBox = { innerTextField ->
                            if (habitTitle.isEmpty()) {
                                Text(
                                    text = "e.g. Read Philosophy, 1 DSA Problem",
                                    color = TextDisabled,
                                    fontSize = 14.sp
                                )
                            }
                            innerTextField()
                        }
                    )
                }

                // --- Bento Card 2: Duration / Timer (Hours & Minutes Stepper + Presets) ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardPrimary, RoundedCornerShape(18.dp))
                        .border(1.dp, BorderPrimary, RoundedCornerShape(18.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "HOW MANY HOURS / TIMER",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = durationFormatted,
                            color = AccentWarning,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }

                    // Stepper Display
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BackgroundSecondary, RoundedCornerShape(14.dp))
                            .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Minus Button
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(CardElevated, RoundedCornerShape(10.dp))
                                .border(1.dp, BorderPrimary, RoundedCornerShape(10.dp))
                                .clickable {
                                    if (durationMinutes > 15) {
                                        durationMinutes -= 15
                                    } else if (durationMinutes > 5) {
                                        durationMinutes = 5
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "−",
                                color = TextPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Central Timer Reading
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = if (durationMinutes >= 60) "${durationMinutes / 60}" else "$durationMinutes",
                                color = TextPrimary,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.SansSerif,
                                lineHeight = 32.sp
                            )
                            Text(
                                text = if (durationMinutes >= 60) {
                                    val mins = durationMinutes % 60
                                    if (mins > 0) "HR ${mins}M" else "HOURS"
                                } else {
                                    "MINUTES"
                                },
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }

                        // Plus Button
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(CardElevated, RoundedCornerShape(10.dp))
                                .border(1.dp, BorderPrimary, RoundedCornerShape(10.dp))
                                .clickable {
                                    if (durationMinutes < 720) {
                                        durationMinutes += 15
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+",
                                color = TextPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Quick Preset Pills
                    val presets = listOf(15, 30, 45, 60, 120)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        presets.forEach { preset ->
                            val isSelected = durationMinutes == preset
                            val label = if (preset >= 60) "${preset / 60}h" else "${preset}m"

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isSelected) CardElevated else BackgroundSecondary,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) TextPrimary else BorderPrimary,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { durationMinutes = preset }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSelected) TextPrimary else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                // --- Bento Card 3: 24-Hour Fixed Schedule / Time Window ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardPrimary, RoundedCornerShape(18.dp))
                        .border(1.dp, BorderPrimary, RoundedCornerShape(18.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "24-HOUR FIXED SCHEDULE",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp
                        )
                        Box(
                            modifier = Modifier
                                .background(BackgroundSecondary, RoundedCornerShape(6.dp))
                                .border(1.dp, BorderSubtle, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = timeWindowFormatted,
                                color = AccentWarning,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    // 2-Column Time Window: FROM (Start) & TO (Finish)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // FROM Card
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(BackgroundSecondary, RoundedCornerShape(14.dp))
                                .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "FROM (START)",
                                color = TextMuted,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = String.format("%02d:%02d", startHour, startMinute),
                                    color = TextPrimary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.SansSerif
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .background(CardElevated, RoundedCornerShape(6.dp))
                                            .border(1.dp, BorderPrimary, RoundedCornerShape(6.dp))
                                            .clickable {
                                                isFlexible = false
                                                startHour = (startHour + 23) % 24
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("−", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .background(CardElevated, RoundedCornerShape(6.dp))
                                            .border(1.dp, BorderPrimary, RoundedCornerShape(6.dp))
                                            .clickable {
                                                isFlexible = false
                                                startHour = (startHour + 1) % 24
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("+", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // TO Card
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(BackgroundSecondary, RoundedCornerShape(14.dp))
                                .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "TO (FINISH)",
                                color = TextMuted,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isFlexible) "--:--" else String.format("%02d:%02d", endHour, endMinute),
                                    color = TextPrimary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.SansSerif
                                )
                                Text(
                                    text = "AUTO",
                                    color = TextMuted,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp
                                )
                            }
                        }
                    }

                    // Quick 24h Presets Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        data class PeriodPreset(val label: String, val hour: Int, val flexible: Boolean = false)
                        val periodPresets = listOf(
                            PeriodPreset("MORN", 7),
                            PeriodPreset("NOON", 13),
                            PeriodPreset("EVN", 18),
                            PeriodPreset("NIGHT", 21),
                            PeriodPreset("ANY", 0, flexible = true)
                        )

                        periodPresets.forEach { p ->
                            val isSelected = if (p.flexible) isFlexible else (!isFlexible && startHour == p.hour)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isSelected) CardElevated else BackgroundSecondary,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) TextPrimary else BorderPrimary,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        if (p.flexible) {
                                            isFlexible = true
                                        } else {
                                            isFlexible = false
                                            startHour = p.hour
                                            startMinute = 0
                                        }
                                    }
                                    .padding(vertical = 7.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = p.label,
                                    color = if (isSelected) TextPrimary else TextMuted,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                }
            }

            // --- 3. Primary Action Button ---
            Button(
                onClick = {
                    if (habitTitle.isBlank()) {
                        showError = true
                    } else {
                        onSaveHabit(habitTitle.trim(), durationMinutes, timeWindowFormatted)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(bottom = 6.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonPrimaryBg,
                    contentColor = ButtonPrimaryText
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "SAVE HABIT",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "→",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun CreateHabitScreenPreview() {
    CreateHabitScreen()
}
