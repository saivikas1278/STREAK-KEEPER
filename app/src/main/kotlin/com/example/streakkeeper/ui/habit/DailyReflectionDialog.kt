package com.example.streakkeeper.ui.habit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.streakkeeper.ui.theme.AccentSuccess
import com.example.streakkeeper.ui.theme.AccentWarning
import com.example.streakkeeper.ui.theme.BorderPrimary
import com.example.streakkeeper.ui.theme.CardElevated
import com.example.streakkeeper.ui.theme.CardPrimary
import com.example.streakkeeper.ui.theme.TextMuted
import com.example.streakkeeper.ui.theme.TextPrimary
import com.example.streakkeeper.ui.theme.TextSecondary

@Composable
fun DailyReflectionDialog(
    habitTitle: String,
    initialNote: String = "",
    onDismiss: () -> Unit,
    onSaveReflection: (note: String, vibe: String) -> Unit
) {
    var noteText by remember { mutableStateOf(initialNote) }
    var selectedVibe by remember { mutableStateOf("🔥 Crushed it") }
    val vibes = listOf("🔥 Crushed it", "⚡ Energized", "🧘 Mindful", "💪 Pushed through")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .background(CardPrimary, RoundedCornerShape(24.dp))
                .border(1.dp, BorderPrimary, RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "DAILY REFLECTION",
                            color = TextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = habitTitle.uppercase(),
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(CardElevated, CircleShape)
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "✕", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Text(
                    text = "How did today's session go? Capture a quick 1-line recap to remember your progress.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                // Quick Vibe Chips
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "SELECT VIBE",
                        color = TextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        vibes.take(2).forEach { vibe ->
                            val isSelected = vibe == selectedVibe
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(if (isSelected) Color(0xFF1E281F) else CardElevated, RoundedCornerShape(10.dp))
                                    .border(1.dp, if (isSelected) AccentSuccess else BorderPrimary, RoundedCornerShape(10.dp))
                                    .clickable { selectedVibe = vibe }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = vibe,
                                    color = if (isSelected) AccentSuccess else TextMuted,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        vibes.drop(2).forEach { vibe ->
                            val isSelected = vibe == selectedVibe
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(if (isSelected) Color(0xFF1E281F) else CardElevated, RoundedCornerShape(10.dp))
                                    .border(1.dp, if (isSelected) AccentSuccess else BorderPrimary, RoundedCornerShape(10.dp))
                                    .clickable { selectedVibe = vibe }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = vibe,
                                    color = if (isSelected) AccentSuccess else TextMuted,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // 140-Char Recap Field
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "SHORT RECAP",
                            color = TextMuted,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${noteText.length}/140",
                            color = if (noteText.length > 130) AccentWarning else TextMuted,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(76.dp)
                            .background(Color(0xFF0F0F0F), RoundedCornerShape(12.dp))
                            .border(1.dp, BorderPrimary, RoundedCornerShape(12.dp))
                            .padding(10.dp)
                    ) {
                        if (noteText.isEmpty()) {
                            Text(
                                text = "e.g., Felt sharp during morning run, beat 5k pace!",
                                color = TextMuted.copy(alpha = 0.5f),
                                fontSize = 12.sp
                            )
                        }
                        BasicTextField(
                            value = noteText,
                            onValueChange = { if (it.length <= 140) noteText = it },
                            textStyle = TextStyle(
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.SansSerif
                            ),
                            cursorBrush = SolidColor(AccentSuccess),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CardElevated),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("SKIP", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            val finalNote = if (noteText.isBlank()) selectedVibe else "$noteText | $selectedVibe"
                            onSaveReflection(finalNote, selectedVibe)
                        },
                        modifier = Modifier
                            .weight(1.5f)
                            .height(44.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentSuccess),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("SAVE REFLECTION", color = Color(0xFF0A140C), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
