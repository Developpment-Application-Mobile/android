package com.example.edukid_android.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun GameCompletionDialog(
    score: Int,
    totalQuestions: Int,
    onPlayAgain: () -> Unit,
    onBackToGames: () -> Unit
) {
    val percentage = (score.toFloat() / totalQuestions * 100).toInt()
    val stars = when {
        percentage >= 90 -> 3
        percentage >= 70 -> 2
        percentage >= 50 -> 1
        else -> 0
    }

    val message = when (stars) {
        3 -> "Amazing! You're a star! ⭐"
        2 -> "Great job! Keep it up! 🎉"
        1 -> "Good effort! Try again! 💪"
        else -> "Keep practicing! You can do it! 🌟"
    }

    val confettiColors = listOf(
        Color(0xFFFF6B6B), Color(0xFF4ECDC4), Color(0xFFFFE66D),
        Color(0xFF95E1D3), Color(0xFFF38181), Color(0xFFAA96DA)
    )

    Dialog(onDismissRequest = onBackToGames) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Confetti effect for high scores
            if (stars >= 2) {
                repeat(20) { index ->
                    val infiniteTransition = rememberInfiniteTransition(label = "confetti_$index")
                    val offsetY by infiniteTransition.animateFloat(
                        initialValue = -100f,
                        targetValue = 800f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(
                                durationMillis = 3000 + index * 100,
                                easing = LinearEasing
                            ),
                            repeatMode = RepeatMode.Restart
                        ),
                        label = "confetti_y_$index"
                    )
                    
                    val offsetX = remember { Random.nextInt(-50, 50).toFloat() }
                    
                    Box(
                        modifier = Modifier
                            .offset(
                                x = (100 + offsetX).dp,
                                y = offsetY.dp
                            )
                            .size(8.dp)
                            .background(
                                color = confettiColors.random(),
                                shape = CircleShape
                            )
                    )
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFFAF7EE7).copy(alpha = 0.15f),
                                    Color(0xFF7E57C2).copy(alpha = 0.05f),
                                    Color.White
                                )
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Animated emoji
                        val scale by animateFloatAsState(
                            targetValue = 1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            label = "emoji_scale"
                        )
                        
                        Text(
                            text = "🎮",
                            fontSize = 72.sp,
                            modifier = Modifier.scale(scale)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Game Complete!",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E2E2E)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = message,
                            fontSize = 16.sp,
                            color = Color(0xFF666666),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Premium Stars with animation
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            repeat(3) { index ->
                                StarIcon(filled = index < stars, delay = index * 100)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Premium Score Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFF8F9FA)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(
                                                Color(0xFFAF7EE7).copy(alpha = 0.1f),
                                                Color(0xFF7E57C2).copy(alpha = 0.05f)
                                            )
                                        )
                                    )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Your Score",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF666666),
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    Row(
                                        verticalAlignment = Alignment.Bottom,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = score.toString(),
                                            fontSize = 48.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFAF7EE7)
                                        )
                                        Text(
                                            text = "/ $totalQuestions",
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF999999),
                                            modifier = Modifier.padding(bottom = 4.dp)
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                color = when {
                                                    percentage >= 90 -> Color(0xFF4CAF50)
                                                    percentage >= 70 -> Color(0xFFFF9800)
                                                    percentage >= 50 -> Color(0xFF2196F3)
                                                    else -> Color(0xFF9E9E9E)
                                                }.copy(alpha = 0.15f),
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .padding(horizontal = 16.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = "$percentage%",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when {
                                                percentage >= 90 -> Color(0xFF4CAF50)
                                                percentage >= 70 -> Color(0xFFFF9800)
                                                percentage >= 50 -> Color(0xFF2196F3)
                                                else -> Color(0xFF9E9E9E)
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        // Premium Buttons
                        Button(
                            onClick = onPlayAgain,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            ),
                            contentPadding = PaddingValues(0.dp),
                            shape = RoundedCornerShape(18.dp),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 8.dp,
                                pressedElevation = 4.dp
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(
                                                Color(0xFFAF7EE7),
                                                Color(0xFF7E57C2)
                                            )
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(text = "🔄", fontSize = 20.sp)
                                    Text(
                                        text = "Play Again",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = onBackToGames,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFFAF7EE7)
                            ),
                            border = BorderStroke(2.dp, Color(0xFFAF7EE7).copy(alpha = 0.3f)),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(text = "←", fontSize = 20.sp)
                                Text(
                                    text = "Back to Games",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StarIcon(filled: Boolean, delay: Int = 0) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(delay.toLong())
        visible = true
    }
    
    val scale by animateFloatAsState(
        targetValue = if (visible && filled) 1f else if (visible) 0.8f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "star_scale"
    )
    
    val rotation by animateFloatAsState(
        targetValue = if (visible && filled) 0f else -20f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "star_rotation"
    )

    Box(
        modifier = Modifier
            .size(56.dp)
            .scale(scale)
            .rotate(rotation)
            .background(
                brush = if (filled) {
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFD700).copy(alpha = 0.3f),
                            Color(0xFFFFD700).copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    )
                } else {
                    Brush.radialGradient(colors = listOf(Color.Transparent, Color.Transparent))
                },
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (filled) "⭐" else "☆",
            fontSize = 40.sp
        )
    }
}

@Composable
fun GameHeader(
    title: String,
    score: Int,
    currentQuestion: Int? = null,
    totalQuestions: Int? = null,
    timeLeft: Int? = null,
    onBackClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFAF7EE7).copy(alpha = 0.12f),
                            Color(0xFF7E57C2).copy(alpha = 0.08f),
                            Color(0xFFAF7EE7).copy(alpha = 0.12f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back Button
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFFAF7EE7).copy(alpha = 0.2f),
                                        Color(0xFFAF7EE7).copy(alpha = 0.1f)
                                    )
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Text(
                                text = "←",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFAF7EE7)
                            )
                        }
                    }

                    // Title & Progress
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E2E2E),
                            letterSpacing = 0.5.sp
                        )
                        if (currentQuestion != null && totalQuestions != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = Color(0xFFAF7EE7).copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "$currentQuestion / $totalQuestions",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFFAF7EE7)
                                )
                            }
                        }
                    }

                    // Score Badge
                    Box(
                        modifier = Modifier
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFFAF7EE7).copy(alpha = 0.2f),
                                        Color(0xFF7E57C2).copy(alpha = 0.15f)
                                    )
                                ),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "⭐",
                                fontSize = 18.sp
                            )
                            Text(
                                text = score.toString(),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFAF7EE7)
                            )
                        }
                    }
                }

                // Timer Progress Bar
                timeLeft?.let { time ->
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "⏱️ Time Left",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF666666)
                            )
                            Text(
                                text = "${time}s",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    time > 20 -> Color(0xFF4CAF50)
                                    time > 10 -> Color(0xFFFF9800)
                                    else -> Color(0xFFF44336)
                                }
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .background(
                                    color = Color(0xFFE0E0E0),
                                    shape = RoundedCornerShape(5.dp)
                                )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(time / 30f)
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = when {
                                                time > 20 -> listOf(
                                                    Color(0xFF4CAF50),
                                                    Color(0xFF66BB6A)
                                                )
                                                time > 10 -> listOf(
                                                    Color(0xFFFF9800),
                                                    Color(0xFFFFA726)
                                                )
                                                else -> listOf(
                                                    Color(0xFFF44336),
                                                    Color(0xFFEF5350)
                                                )
                                            }
                                        ),
                                        shape = RoundedCornerShape(5.dp)
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}
