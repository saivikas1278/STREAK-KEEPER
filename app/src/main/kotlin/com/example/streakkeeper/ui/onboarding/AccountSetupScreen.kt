package com.example.streakkeeper.ui.onboarding

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.streakkeeper.ui.theme.AccentError
import com.example.streakkeeper.ui.theme.AccentSuccess
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

/**
 * Account Setup Screen adhering to Dark Swiss Minimalism + Modern Bento Grid.
 * Allows user to configure their Name and local encryption Passcode/Password.
 */
@Composable
fun AccountSetupScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onContinueClick: (name: String, passcode: String) -> Unit = { _, _ -> }
) {
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // Minimalist 4-segment strength calculation
    val passwordLength = password.length
    val (strengthLabel, strengthColor, activeBars) = when {
        passwordLength == 0 -> Triple("Minimum 6 characters", TextMuted, 0)
        passwordLength < 6 -> Triple("Too short", AccentError, 1)
        passwordLength < 9 -> Triple("Medium strength", AccentWarning, 2)
        else -> Triple("Strong passcode", AccentSuccess, 4)
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
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // --- 1. Top Navigation Bar ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button
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
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Step Tracker
                Text(
                    text = "01 / 03 — IDENTITY",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.2.sp
                )

                // Balancer
                Spacer(modifier = Modifier.size(38.dp))
            }

            // --- 2. Bento Grid Form Content ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Title Block
                Column {
                    Text(
                        text = "CREATE YOUR PROFILE",
                        color = TextPrimary,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Secure your private daily consistency journey.",
                        color = TextSecondary,
                        fontSize = 15.sp,
                        fontStyle = FontStyle.Italic,
                        fontFamily = FontFamily.Serif
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Bento Card 1: Name Input
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardPrimary, RoundedCornerShape(18.dp))
                        .border(1.dp, BorderPrimary, RoundedCornerShape(18.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "YOUR NAME",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.8.sp
                        )

                        BasicTextField(
                            value = name,
                            onValueChange = { name = it },
                            textStyle = TextStyle(
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontFamily = FontFamily.SansSerif
                            ),
                            cursorBrush = SolidColor(TextPrimary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CardElevated, RoundedCornerShape(12.dp))
                                .border(1.dp, BorderPrimary, RoundedCornerShape(12.dp))
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            decorationBox = { innerTextField ->
                                if (name.isEmpty()) {
                                    Text(
                                        text = "e.g. Alex Vance",
                                        color = TextDisabled,
                                        fontSize = 15.sp
                                    )
                                }
                                innerTextField()
                            }
                        )
                    }
                }

                // Bento Card 2: Password Input & Strength Meter
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardPrimary, RoundedCornerShape(18.dp))
                        .border(1.dp, BorderPrimary, RoundedCornerShape(18.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "MASTER PASSCODE",
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.8.sp
                            )
                            Text(
                                text = strengthLabel,
                                color = strengthColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Password Input Field with Toggle
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CardElevated, RoundedCornerShape(12.dp))
                                .border(1.dp, BorderPrimary, RoundedCornerShape(12.dp))
                                .padding(horizontal = 14.dp, vertical = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    BasicTextField(
                                        value = password,
                                        onValueChange = { password = it },
                                        textStyle = TextStyle(
                                            color = TextPrimary,
                                            fontSize = 15.sp,
                                            fontFamily = FontFamily.SansSerif
                                        ),
                                        cursorBrush = SolidColor(TextPrimary),
                                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                        modifier = Modifier.fillMaxWidth(),
                                        decorationBox = { innerTextField ->
                                            if (password.isEmpty()) {
                                                Text(
                                                    text = "Create password",
                                                    color = TextDisabled,
                                                    fontSize = 15.sp
                                                )
                                            }
                                            innerTextField()
                                        }
                                    )
                                }

                                Text(
                                    text = if (isPasswordVisible) "HIDE" else "SHOW",
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp,
                                    modifier = Modifier
                                        .clickable { isPasswordVisible = !isPasswordVisible }
                                        .padding(start = 8.dp)
                                )
                            }
                        }

                        // 4-Segment Minimalist Swiss Strength Meter
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            for (i in 1..4) {
                                val barColor = if (i <= activeBars) strengthColor else BorderPrimary
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(4.dp)
                                        .background(barColor, RoundedCornerShape(2.dp))
                                )
                            }
                        }
                    }
                }

                // Bento Card 3: Offline Security Guarantee
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundSecondary, RoundedCornerShape(16.dp))
                        .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(text = "🛡️", fontSize = 16.sp)
                        Text(
                            text = "Offline vault. Your password encrypts your local habit database on this device.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // --- 3. Bottom Swiss Primary Button ---
            Button(
                onClick = { onContinueClick(name, password) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonPrimaryBg,
                    contentColor = ButtonPrimaryText
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(bottom = 8.dp)
            ) {
                Text(
                    text = "CONTINUE TO ROUTINE  →",
                    color = ButtonPrimaryText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.8.sp
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF090909)
@Composable
private fun AccountSetupScreenPreview() {
    AccountSetupScreen()
}
