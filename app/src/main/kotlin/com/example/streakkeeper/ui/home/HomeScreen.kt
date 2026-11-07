package com.example.streakkeeper.ui.home

import android.content.Context
import android.media.AudioManager
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.streakkeeper.ui.habit.HabitItem
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

enum class HomeTab {
    LIST, PROFILE
}

/**
 * Animated Swiss Streak Capsule Component.
 * Features a flickering micro-flame and bold tabular numerals.
 */
@Composable
fun AnimatedStreakBadge(
    streakCount: Int = 0,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "StreakFlameAnim")
    val flameScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "FlameScale"
    )

    Row(
        modifier = modifier
            .background(CardPrimary, RoundedCornerShape(20.dp))
            .border(1.dp, BorderPrimary, RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        // Animated Micro Flame
        Box(
            modifier = Modifier
                .size(16.dp)
                .scale(flameScale),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(16.dp)) {
                val w = size.width
                val h = size.height

                // Flame Path
                val flamePath = Path().apply {
                    moveTo(w * 0.50f, h * 0.95f)
                    cubicTo(w * 0.20f, h * 0.82f, w * 0.15f, h * 0.50f, w * 0.35f, h * 0.40f)
                    cubicTo(w * 0.25f, h * 0.28f, w * 0.40f, h * 0.18f, w * 0.50f, h * 0.05f)
                    cubicTo(w * 0.60f, h * 0.20f, w * 0.75f, h * 0.28f, w * 0.65f, h * 0.42f)
                    cubicTo(w * 0.85f, h * 0.52f, w * 0.80f, h * 0.82f, w * 0.50f, h * 0.95f)
                    close()
                }

                drawPath(path = flamePath, color = AccentWarning, style = Fill)

                // Inner spark
                drawCircle(
                    color = Color.White.copy(alpha = 0.85f),
                    radius = w * 0.12f,
                    center = center.copy(y = h * 0.65f)
                )
            }
        }

        // Streak Count Number & Unit
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = "$streakCount",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                fontFamily = FontFamily.SansSerif
            )
            Text(
                text = "DAYS",
                color = TextMuted,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 1.5.dp)
            )
        }
    }
}

/**
 * Unambiguous Daily Routine Progress Badge.
 * Replaces the confusing global streak and freeze badges with a clear count of today's completed habits.
 */
@Composable
fun TodayProgressBadge(
    completedCount: Int,
    totalCount: Int,
    modifier: Modifier = Modifier
) {
    val allDone = totalCount > 0 && completedCount == totalCount
    Box(
        modifier = modifier
            .background(
                if (allDone) Color(0xFF142417) else CardPrimary,
                RoundedCornerShape(20.dp)
            )
            .border(
                1.dp,
                if (allDone) AccentSuccess.copy(alpha = 0.6f) else BorderPrimary,
                RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(if (allDone) AccentSuccess else AccentWarning, androidx.compose.foundation.shape.CircleShape)
            )
            Text(
                text = if (totalCount == 0) "NO HABITS"
                       else if (allDone) "✓ ALL DONE TODAY"
                       else "$completedCount / $totalCount DONE",
                color = if (allDone) AccentSuccess else TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 0.5.sp
            )
        }
    }
}

/**
 * Habit Card Streak Badge with dynamic flame glyph, scale-pop animation, and amber glow states.
 */
@Composable
fun CardStreakFlameBadge(
    streak: Int,
    isCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    val hasStreak = streak > 0
    val scaleAnim by animateFloatAsState(
        targetValue = if (isCompleted) 1.08f else 1.0f,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "cardStreakScale"
    )

    val bgColor = when {
        hasStreak && isCompleted -> Color(0xFF1D170D)
        hasStreak -> Color(0xFF18130B)
        else -> BackgroundSecondary
    }

    val borderColor = when {
        hasStreak && isCompleted -> Color(0xFFE5B96B).copy(alpha = 0.55f)
        hasStreak -> Color(0xFFE5B96B).copy(alpha = 0.28f)
        else -> BorderSubtle
    }

    val flameTint = if (hasStreak) AccentWarning else TextMuted.copy(alpha = 0.45f)
    val numberColor = when {
        hasStreak && isCompleted -> AccentWarning
        hasStreak -> TextPrimary
        else -> TextMuted
    }
    val unitColor = if (hasStreak) AccentWarning.copy(alpha = 0.7f) else TextMuted

    Row(
        modifier = modifier
            .scale(scaleAnim)
            .background(bgColor, RoundedCornerShape(10.dp))
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Canvas(modifier = Modifier.size(12.dp)) {
            val w = size.width
            val h = size.height

            val flamePath = Path().apply {
                moveTo(w * 0.50f, h * 0.95f)
                cubicTo(w * 0.20f, h * 0.82f, w * 0.15f, h * 0.50f, w * 0.35f, h * 0.40f)
                cubicTo(w * 0.25f, h * 0.28f, w * 0.40f, h * 0.18f, w * 0.50f, h * 0.05f)
                cubicTo(w * 0.60f, h * 0.20f, w * 0.75f, h * 0.28f, w * 0.65f, h * 0.42f)
                cubicTo(w * 0.85f, h * 0.52f, w * 0.80f, h * 0.82f, w * 0.50f, h * 0.95f)
                close()
            }
            drawPath(path = flamePath, color = flameTint, style = Fill)

            if (hasStreak) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.85f),
                    radius = w * 0.12f,
                    center = center.copy(y = h * 0.65f)
                )
            }
        }

        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(1.5.dp)
        ) {
            Text(
                text = "$streak",
                color = numberColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )
            Text(
                text = "D",
                color = unitColor,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Home Screen adhering to Dark Swiss Minimalism.
 * Features an animated Swiss streak badge, a prominent center Plus (+) action button,
 * and a 2-item bottom menu (List & Profile).
 */
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    userName: String = "ALEX",
    streakCount: Int = 0,
    habits: List<HabitItem> = emptyList(),
    onAddHabitClick: () -> Unit = {},
    onHabitToggle: (String) -> Unit = {},
    onHabitClick: (String) -> Unit = {},
    onHabitsImported: (List<HabitItem>) -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(HomeTab.LIST) }
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager }

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
            // --- 1. Top Minimalist Bar ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (selectedTab == HomeTab.PROFILE) "DISCIPLINE DOSSIER" else "DAILY CONSISTENCY",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = if (selectedTab == HomeTab.PROFILE) "${userName.uppercase()}'S PROFILE" else "${userName.uppercase()}'S ROUTINE",
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                }

                if (selectedTab == HomeTab.PROFILE) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF161A17), RoundedCornerShape(20.dp))
                            .border(1.dp, AccentSuccess.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(modifier = Modifier.size(6.dp).background(AccentSuccess, RoundedCornerShape(3.dp)))
                            Text(
                                text = "100% PRIVATE",
                                color = AccentSuccess,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                        }
                    }
                } else {
                    val completedTodayCount = habits.count { it.isCompleted }
                    TodayProgressBadge(
                        completedCount = completedTodayCount,
                        totalCount = habits.size
                    )
                }
            }

            // --- 2. Screen Center: Large Plus Button OR Active Habits List OR Profile Dossier ---
            if (selectedTab == HomeTab.PROFILE) {
                com.example.streakkeeper.ui.profile.ProfileView(
                    userName = userName,
                    habits = habits,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    onImportSuccess = onHabitsImported
                )
            } else if (habits.isEmpty()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) {
                    // Interactive Center Plus (+) Button
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(CardPrimary, RoundedCornerShape(22.dp))
                            .border(1.dp, BorderPrimary, RoundedCornerShape(22.dp))
                            .clickable { onAddHabitClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+",
                            color = TextPrimary,
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Light,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Swiss Editorial Typography
                    Text(
                        text = "ADD YOUR FIRST HABIT",
                        color = TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontFamily = FontFamily.SansSerif,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Tap the center plus button to start building an unbroken streak.",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        fontStyle = FontStyle.Italic,
                        fontFamily = FontFamily.Serif,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ACTIVE DISCIPLINES",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.2.sp
                        )
                        Box(
                            modifier = Modifier
                                .background(CardPrimary, RoundedCornerShape(8.dp))
                                .border(1.dp, BorderPrimary, RoundedCornerShape(8.dp))
                                .clickable { onAddHabitClick() }
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+ NEW HABIT",
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(habits, key = { it.id }) { habit ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (habit.isCompleted) Color(0xFF161A17) else CardPrimary,
                                        RoundedCornerShape(18.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (habit.isCompleted) AccentSuccess.copy(alpha = 0.5f) else BorderPrimary,
                                        RoundedCornerShape(18.dp)
                                    )
                                    .clickable { onHabitClick(habit.id) }
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(
                                                if (habit.isCompleted) AccentSuccess else CardElevated,
                                                RoundedCornerShape(10.dp)
                                            )
                                            .border(1.dp, BorderPrimary, RoundedCornerShape(10.dp))
                                            .clickable {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                audioManager?.playSoundEffect(AudioManager.FX_KEY_CLICK, 1.0f)
                                                onHabitToggle(habit.id)
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "✓",
                                            color = if (habit.isCompleted) BackgroundPrimary else TextSecondary,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = habit.title,
                                            color = if (habit.isCompleted) AccentSuccess else TextPrimary,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = if (habit.durationMinutes >= 60) "${habit.durationMinutes / 60}H" else "${habit.durationMinutes}M",
                                                color = AccentWarning,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(text = "•", color = BorderPrimary, fontSize = 8.sp)
                                            Text(
                                                text = habit.timeOfDay,
                                                color = TextMuted,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }

                                CardStreakFlameBadge(
                                    streak = habit.streak,
                                    isCompleted = habit.isCompleted
                                )
                            }
                        }
                    }
                }
            }

            // --- 3. Bottom Menu Bar: ONLY TWO OPTIONS (LIST & PROFILE) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardPrimary, RoundedCornerShape(22.dp))
                        .border(1.dp, BorderPrimary, RoundedCornerShape(22.dp))
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Option 1: LIST
                    val isListActive = selectedTab == HomeTab.LIST
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { selectedTab = HomeTab.LIST }
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "☰",
                            color = if (isListActive) TextPrimary else TextMuted,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "LIST",
                            color = if (isListActive) TextPrimary else TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp
                        )
                    }

                    // Divider
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(24.dp)
                            .background(BorderSubtle)
                    )

                    // Option 2: PROFILE
                    val isProfileActive = selectedTab == HomeTab.PROFILE
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { selectedTab = HomeTab.PROFILE }
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "👤",
                            color = if (isProfileActive) TextPrimary else TextMuted,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "PROFILE",
                            color = if (isProfileActive) TextPrimary else TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF090909)
@Composable
private fun HomeScreenPreview() {
    HomeScreen()
}
