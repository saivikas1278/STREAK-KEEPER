package com.example.streakkeeper.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.streakkeeper.ui.theme.AccentWarning
import com.example.streakkeeper.ui.theme.TextDisabled
import com.example.streakkeeper.ui.theme.TextMuted
import com.example.streakkeeper.ui.theme.TextPrimary
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * "Scribble-Head to Clarity" Brand Mark for StreakKeeper.
 * Includes continuous kinetic swirl rotation for the mental wire loops
 * and zoom-scale support for the cinematic "dive-inside-the-head" transition.
 */
@Composable
fun StreakFlameLogo(
    modifier: Modifier = Modifier,
    size: Dp = 160.dp,
    zoomScale: Float = 1f,
    animatedEntrance: Boolean = true
) {
    val entranceProgress = remember { Animatable(if (animatedEntrance) 0f else 1f) }

    LaunchedEffect(Unit) {
        if (animatedEntrance) {
            entranceProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing)
            )
        }
    }

    // Continuous kinetic swirl rotation
    val infiniteTransition = rememberInfiniteTransition(label = "MindSwirl")
    val swirlAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SwirlAngle"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val scale = entranceProgress.value
            val cx = w * 0.50f
            val cy = h * 0.34f
            val baseRadius = w * 0.24f

            // Apply camera dive-in zoom anchored at head center (cx, cy)
            scale(scaleX = zoomScale, scaleY = zoomScale, pivot = Offset(cx, cy)) {
                // ==============================================================
                // 1. Crewneck T-Shirt Torso & Neck (Static)
                // ==============================================================
                val collarOuter = Path().apply {
                    moveTo(w * 0.38f, h * 0.65f)
                    cubicTo(w * 0.44f, h * 0.72f, w * 0.56f, h * 0.72f, w * 0.62f, h * 0.65f)
                }
                drawPath(
                    path = collarOuter,
                    color = TextPrimary.copy(alpha = 0.95f * scale),
                    style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
                )

                val collarInner = Path().apply {
                    moveTo(w * 0.395f, h * 0.665f)
                    cubicTo(w * 0.45f, h * 0.735f, w * 0.55f, h * 0.735f, w * 0.605f, h * 0.665f)
                }
                drawPath(
                    path = collarInner,
                    color = TextPrimary.copy(alpha = 0.95f * scale),
                    style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
                )

                val neckPath = Path().apply {
                    moveTo(w * 0.42f, h * 0.52f)
                    lineTo(w * 0.42f, h * 0.58f)
                    lineTo(w * 0.38f, h * 0.65f)

                    moveTo(w * 0.58f, h * 0.52f)
                    lineTo(w * 0.58f, h * 0.58f)
                    lineTo(w * 0.62f, h * 0.65f)
                }
                drawPath(
                    path = neckPath,
                    color = TextPrimary.copy(alpha = 0.95f * scale),
                    style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
                )

                val throatFold = Path().apply {
                    moveTo(w * 0.45f, h * 0.56f)
                    lineTo(w * 0.50f, h * 0.65f)
                    lineTo(w * 0.55f, h * 0.56f)
                }
                drawPath(
                    path = throatFold,
                    color = TextMuted.copy(alpha = 0.8f * scale),
                    style = Stroke(width = 1.2.dp.toPx(), cap = StrokeCap.Round)
                )

                val bodyPath = Path().apply {
                    moveTo(w * 0.38f, h * 0.65f)
                    cubicTo(w * 0.30f, h * 0.68f, w * 0.22f, h * 0.74f, w * 0.15f, h * 0.88f)
                    lineTo(w * 0.165f, h * 0.98f)

                    moveTo(w * 0.26f, h * 0.76f)
                    cubicTo(w * 0.28f, h * 0.84f, w * 0.29f, h * 0.90f, w * 0.29f, h * 0.98f)

                    moveTo(w * 0.62f, h * 0.65f)
                    cubicTo(w * 0.70f, h * 0.68f, w * 0.78f, h * 0.74f, w * 0.85f, h * 0.88f)
                    lineTo(w * 0.835f, h * 0.98f)

                    moveTo(w * 0.74f, h * 0.76f)
                    cubicTo(w * 0.72f, h * 0.84f, w * 0.71f, h * 0.90f, w * 0.71f, h * 0.98f)
                }
                drawPath(
                    path = bodyPath,
                    color = TextPrimary.copy(alpha = 0.95f * scale),
                    style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                )

                val foldPath = Path().apply {
                    moveTo(w * 0.28f, h * 0.80f)
                    lineTo(w * 0.31f, h * 0.86f)
                    moveTo(w * 0.72f, h * 0.79f)
                    lineTo(w * 0.70f, h * 0.85f)
                    lineTo(w * 0.72f, h * 0.91f)
                }
                drawPath(
                    path = foldPath,
                    color = TextDisabled.copy(alpha = 0.7f * scale),
                    style = Stroke(width = 1.2.dp.toPx(), cap = StrokeCap.Round)
                )

                // ==============================================================
                // 2. KINETIC ROTATING SCRIBBLE HEAD (Swirling Confusion)
                // ==============================================================
                rotate(degrees = swirlAngle, pivot = Offset(cx, cy)) {
                    val scribblePath = Path()
                    var seed = 42
                    fun pseudoRand(): Float {
                        seed = (seed * 9301 + 49297) % 233280
                        return seed / 233280f
                    }

                    var curAngle = 0f
                    var curR = baseRadius * 0.35f
                    val totalSteps = 240

                    for (step in 0 until totalSteps) {
                        curAngle += 0.38f + pseudoRand() * 0.42f
                        val targetR = (baseRadius * 0.2f) + pseudoRand() * (baseRadius * 0.85f)
                        curR += (targetR - curR) * 0.35f

                        val loopAngle = curAngle + (pseudoRand() - 0.5f) * 1.4f
                        val px = cx + cos(loopAngle) * curR
                        val py = cy + sin(loopAngle) * (curR * 0.94f)

                        if (step == 0) {
                            scribblePath.moveTo(px, py)
                        } else {
                            val cpx = cx + cos(loopAngle + (pseudoRand() - 0.5f) * 1.1f) * (curR * (0.8f + pseudoRand() * 0.4f))
                            val cpy = cy + sin(loopAngle + (pseudoRand() - 0.5f) * 1.1f) * (curR * (0.8f + pseudoRand() * 0.4f))
                            scribblePath.quadraticTo(cpx, cpy, px, py)
                        }
                    }

                    drawPath(
                        path = scribblePath,
                        color = TextPrimary.copy(alpha = 0.85f * scale),
                        style = Stroke(width = 1.25.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )

                    // Wild outer flyaways
                    val flyaways = Path()
                    for (f in 0 until 28) {
                        val startAngle = pseudoRand() * 2 * PI.toFloat()
                        val startR = baseRadius * (0.7f + pseudoRand() * 0.25f)
                        val peakR = baseRadius * (1.12f + pseudoRand() * 0.28f)
                        val endAngle = startAngle + (pseudoRand() - 0.5f) * 2.2f

                        val x1 = cx + cos(startAngle) * startR
                        val y1 = cy + sin(startAngle) * startR
                        val cpx1 = cx + cos(startAngle + 0.6f) * peakR
                        val cpy1 = cy + sin(startAngle + 0.6f) * peakR
                        val cpx2 = cx + cos(endAngle - 0.6f) * peakR
                        val cpy2 = cy + sin(endAngle - 0.6f) * peakR
                        val x2 = cx + cos(endAngle) * (startR * 0.9f)
                        val y2 = cy + sin(endAngle) * (startR * 0.9f)

                        flyaways.moveTo(x1, y1)
                        flyaways.cubicTo(cpx1, cpy1, cpx2, cpy2, x2, y2)
                    }

                    drawPath(
                        path = flyaways,
                        color = TextPrimary.copy(alpha = 0.65f * scale),
                        style = Stroke(width = 1.1.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // 3. Central Golden Clarity Spark (Anchor)
                drawCircle(
                    color = AccentWarning.copy(alpha = scale),
                    radius = 3.5.dp.toPx(),
                    center = Offset(cx, cy)
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF090909)
@Composable
private fun StreakFlameLogoPreview() {
    StreakFlameLogo(size = 180.dp, animatedEntrance = false)
}
