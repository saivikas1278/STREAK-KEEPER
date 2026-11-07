package com.example.streakkeeper.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.streakkeeper.ui.theme.BackgroundPrimary
import com.example.streakkeeper.ui.theme.ButtonPrimaryBg
import com.example.streakkeeper.ui.theme.ButtonPrimaryText
import com.example.streakkeeper.ui.theme.TextPrimary
import com.example.streakkeeper.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Splash Screen designed under Dark Swiss Minimalism + Bento Grid principles.
 * Features kinetic rotating mind scribble loops and an aggressive "dive-inside-the-head"
 * zoom transition on "LET'S MAKE IT" click.
 */
@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    onMakeItClick: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()

    val logoScale = remember { Animatable(0.85f) }
    val logoAlpha = remember { Animatable(0f) }
    val contentOffsetY = remember { Animatable(16f) }
    val contentAlpha = remember { Animatable(0f) }
    val buttonAlpha = remember { Animatable(0f) }
    val buttonOffsetY = remember { Animatable(16f) }

    // Camera dive-in zoom scale
    val diveInZoom = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        launch {
            logoAlpha.animateTo(1f, tween(400, easing = FastOutSlowInEasing))
        }
        launch {
            logoScale.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
        }

        delay(200)
        launch {
            contentAlpha.animateTo(1f, tween(450, easing = FastOutSlowInEasing))
        }
        launch {
            contentOffsetY.animateTo(0f, tween(450, easing = FastOutSlowInEasing))
        }

        delay(150)
        launch {
            buttonAlpha.animateTo(1f, tween(400, easing = FastOutSlowInEasing))
        }
        launch {
            buttonOffsetY.animateTo(0f, tween(400, easing = FastOutSlowInEasing))
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        // --- Center Editorial Block ---
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            // Rotating Scribble Brand Mark with Dive-In Zoom
            Box(
                modifier = Modifier
                    .scale(logoScale.value)
                    .alpha(logoAlpha.value)
            ) {
                StreakFlameLogo(
                    size = 150.dp,
                    zoomScale = diveInZoom.value
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Typography Block
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .offset { IntOffset(x = 0, y = contentOffsetY.value.toInt()) }
                    .alpha(contentAlpha.value)
            ) {
                Text(
                    text = "STREAKKEEPER",
                    color = TextPrimary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    fontFamily = FontFamily.SansSerif,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Consistency over perfection.",
                    color = TextSecondary,
                    fontSize = 17.sp,
                    fontStyle = FontStyle.Italic,
                    fontFamily = FontFamily.Serif,
                    textAlign = TextAlign.Center
                )
            }
        }

        // --- Bottom Swiss Primary Button: "LET'S MAKE IT" ---
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp, start = 24.dp, end = 24.dp)
                .fillMaxWidth()
                .offset { IntOffset(x = 0, y = buttonOffsetY.value.toInt()) }
                .alpha(buttonAlpha.value)
        ) {
            Button(
                onClick = {
                    coroutineScope.launch {
                        // 1. Fade UI elements rapidly
                        launch { contentAlpha.animateTo(0f, tween(150)) }
                        launch { buttonAlpha.animateTo(0f, tween(150)) }

                        // 2. High-speed zoom directly into the center of the head
                        launch {
                            diveInZoom.animateTo(
                                targetValue = 22f,
                                animationSpec = tween(durationMillis = 550, easing = FastOutSlowInEasing)
                            )
                        }

                        // 3. Trigger navigation callback into Account Setup
                        delay(520)
                        onMakeItClick()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonPrimaryBg,
                    contentColor = ButtonPrimaryText
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "LET'S MAKE IT  →",
                        color = ButtonPrimaryText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF090909)
@Composable
private fun SplashScreenPreview() {
    SplashScreen()
}
