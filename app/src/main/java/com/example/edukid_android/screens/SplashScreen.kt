package com.example.edukid_android.screens


import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    var logoScale by remember { mutableStateOf(0.3f) }
    var logoOpacity by remember { mutableStateOf(0f) }
    var titleOffset by remember { mutableStateOf(50f) }
    var titleOpacity by remember { mutableStateOf(0f) }
    var taglineOpacity by remember { mutableStateOf(0f) }
    var decorativeRotation by remember { mutableStateOf(0f) }

    // Animations
    val logoScaleAnim by animateFloatAsState(
        targetValue = logoScale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    val logoOpacityAnim by animateFloatAsState(
        targetValue = logoOpacity,
        animationSpec = tween(800)
    )

    val titleOffsetAnim by animateFloatAsState(
        targetValue = titleOffset,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )

    val titleOpacityAnim by animateFloatAsState(
        targetValue = titleOpacity,
        animationSpec = tween(600)
    )

    val taglineOpacityAnim by animateFloatAsState(
        targetValue = taglineOpacity,
        animationSpec = tween(600)
    )

    val infiniteTransition = rememberInfiniteTransition()
    val decorativeRotationAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val dotScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Start animations
    LaunchedEffect(Unit) {
        // Logo animation
        logoScale = 1f
        logoOpacity = 1f

        // Title animation (delayed)
        delay(300)
        titleOffset = 0f
        titleOpacity = 1f

        // Tagline animation (delayed)
        delay(300)
        taglineOpacity = 1f

        // Navigate after 5 seconds (matching iOS)
        delay(4400)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x99AF7DE7), // Purple with opacity
                        Color(0xFF272052)  // Dark purple
                    ),
                    center = Offset(0.3f, 0.3f),
                    radius = 1000f
                )
            )
    ) {
        // Decorative Elements
        DecorativeElements(rotation = decorativeRotationAnim)

        // Main Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Logo with Glow
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .scale(logoScaleAnim)
                    .alpha(logoOpacityAnim)
            ) {
                // Glow circles
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .scale(1.2f)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0x66AF7DE7),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )

                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .scale(1.3f)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0x4DAF7DE7),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )

                // Logo Circle Background
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.3f),
                                    Color.White.copy(alpha = 0.15f)
                                )
                            ),
                            shape = CircleShape
                        )
                )

                // Logo Icon (Book with Star)
                Box(contentAlignment = Alignment.Center) {
                    // Book Icon (use your drawable or emoji)
                    Text(
                        text = "📚",
                        fontSize = 70.sp,
                        modifier = Modifier.offset(y = (-5).dp)
                    )

                    // Star accent
                    Text(
                        text = "⭐",
                        fontSize = 28.sp,
                        modifier = Modifier
                            .offset(x = 35.dp, y = (-35).dp)
                            .rotate(decorativeRotationAnim)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // App Title
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .offset(y = titleOffsetAnim.dp)
                    .alpha(titleOpacityAnim)
            ) {
                Text(
                    text = "EduKid",
                    style = TextStyle(
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Black,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.White,
                                Color.White.copy(alpha = 0.9f)
                            )
                        ),
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.2f),
                            offset = Offset(0f, 5f),
                            blurRadius = 10f
                        )
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Shimmer line
                Box(
                    modifier = Modifier
                        .width(200.dp)
                        .height(2.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.White.copy(alpha = 0.6f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tagline
            Text(
                text = "Where Learning Meets Fun",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White.copy(alpha = 0.9f)
                ),
                modifier = Modifier.alpha(taglineOpacityAnim)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Loading Indicator
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.alpha(taglineOpacityAnim)
            ) {
                // Animated dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(3) { index ->
                        val delay = index * 200
                        val dotScaleAnim by infiniteTransition.animateFloat(
                            initialValue = 0.8f,
                            targetValue = 1.2f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(
                                    durationMillis = 600,
                                    delayMillis = delay,
                                    easing = FastOutSlowInEasing
                                ),
                                repeatMode = RepeatMode.Reverse
                            )
                        )

                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .scale(dotScaleAnim)
                                .background(Color.White, CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Loading...",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                )
            }
        }
    }
}

@Composable
fun DecorativeElements(rotation: Float) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Education Book - Top Left
        Text(
            text = "📖",
            fontSize = 100.sp,
            modifier = Modifier
                .offset(x = (-70).dp, y = 50.dp)
                .rotate(rotation * 0.5f)
                .alpha(0.6f)
                .blur(1.dp)
        )

        // Coins - Top Right
        Text(
            text = "🪙",
            fontSize = 60.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-20).dp, y = 60.dp)
                .rotate(-rotation * 0.3f)
                .alpha(0.6f)
        )

        // Book Stacks - Bottom Left
        Text(
            text = "📚",
            fontSize = 80.sp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 20.dp, y = (-80).dp)
                .rotate(rotation * 0.4f)
                .alpha(0.5f)
        )

        // Chemistry - Bottom Right
        Text(
            text = "🧪",
            fontSize = 70.sp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-30).dp, y = (-90).dp)
                .rotate(-rotation * 0.6f)
                .alpha(0.5f)
        )
    }
}
