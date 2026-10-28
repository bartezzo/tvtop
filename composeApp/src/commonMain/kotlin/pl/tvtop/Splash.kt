package pl.tvtop

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val SplashBg = Brush.linearGradient(listOf(Color(0xFF1B1F4B), Color(0xFF0B0D24)))
private val Cyan = Color(0xFF22D3EE)
private val Indigo = Color(0xFF6366F1)
private val Magenta = Color(0xFFD946EF)
private val Frame = Color(0xFFF8FAFC)

private const val SplashMillis = 1500L

/** Draws [content] with the splash screen on top, fading it out after a short delay. */
@Composable
fun WithSplash(content: @Composable () -> Unit) {
    var visible by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        delay(SplashMillis)
        visible = false
    }
    Box(Modifier.fillMaxSize()) {
        content()
        AnimatedVisibility(visible = visible, exit = fadeOut()) { SplashScreen() }
    }
}

@Composable
fun SplashScreen() {
    Box(Modifier.fillMaxSize().background(SplashBg), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            TvLogo(Modifier.size(180.dp))
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = Frame)) { append("tv") }
                    withStyle(SpanStyle(brush = Brush.horizontalGradient(listOf(Cyan, Magenta)))) { append("top") }
                },
                fontSize = 56.sp,
                fontWeight = FontWeight.ExtraBold,
            )
        }
    }
}

/** The tvtop television icon, drawn in a 512-unit coordinate space (see assets/logo-icon.svg). */
@Composable
private fun TvLogo(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val s = size.width / 512f
        fun p(x: Float, y: Float) = Offset(x * s, y * s)
        fun sz(w: Float, h: Float) = Size(w * s, h * s)

        drawAntenna(s)
        drawRoundRect(Frame, p(86f, 150f), sz(340f, 236f), CornerRadius(40f * s))
        drawRoundRect(
            Brush.linearGradient(listOf(Cyan, Indigo, Magenta), p(108f, 172f), p(404f, 364f)),
            p(108f, 172f), sz(296f, 192f), CornerRadius(24f * s),
        )
        val play = Path().apply {
            moveTo(232f * s, 214f * s); lineTo(232f * s, 322f * s); lineTo(316f * s, 268f * s); close()
        }
        drawPath(play, Color.White)
        drawPath(play, Color.White, style = Stroke(12f * s, join = StrokeJoin.Round))
        drawRoundRect(Frame, p(176f, 398f), sz(160f, 16f), CornerRadius(8f * s))
        drawRect(Frame, p(236f, 384f), sz(40f, 18f))
    }
}

private fun DrawScope.drawAntenna(s: Float) {
    val path = Path().apply {
        moveTo(196f * s, 150f * s); lineTo(256f * s, 100f * s); lineTo(316f * s, 150f * s)
    }
    drawPath(path, Frame, style = Stroke(14f * s, cap = StrokeCap.Round, join = StrokeJoin.Round))
    drawCircle(Cyan, 12f * s, Offset(256f * s, 100f * s))
}
