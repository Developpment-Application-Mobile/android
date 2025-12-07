package com.example.edukid_android.games

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.edukid_android.components.GameCompletionDialog
import com.example.edukid_android.components.GameHeader
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun NumberMatchGame(
    navController: NavController? = null,
    parentId: String? = null,
    kidId: String? = null
) {
    var currentRound by remember { mutableStateOf(1) }
    var score by remember { mutableStateOf(0) }
    var targetNumber by remember { mutableStateOf(Random.nextInt(1, 11)) }
    var options by remember { mutableStateOf(generateOptions(targetNumber)) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var showFeedback by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }
    var gameComplete by remember { mutableStateOf(false) }

    val totalRounds = 10

    LaunchedEffect(showFeedback) {
        if (showFeedback) {
            delay(1000)
            if (currentRound < totalRounds) {
                currentRound++
                targetNumber = Random.nextInt(1, 11)
                options = generateOptions(targetNumber)
                selectedOption = null
                showFeedback = false
            } else {
                gameComplete = true
                if (parentId != null && kidId != null) {
                    com.example.edukid_android.utils.ApiClient.trackQuestProgress(
                        parentId = parentId,
                        kidId = kidId,
                        questType = "COMPLETE_GAMES",
                        increment = 1
                    )
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFAF7EE7).copy(alpha = 0.6f),
                        Color(0xFF272052)
                    ),
                    center = Offset(200f, 200f),
                    radius = 400f
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            GameHeader(
                title = "Number Match",
                score = score,
                currentQuestion = currentRound,
                totalQuestions = totalRounds,
                onBackClick = { navController?.popBackStack() }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Target Number
                Card(
                    modifier = Modifier
                        .size(140.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFFAF7EE7).copy(alpha = 0.15f),
                                        Color.White
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = targetNumber.toString(),
                            fontSize = 72.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFAF7EE7)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Tap the group with",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "$targetNumber items ⭐",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Options
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    options.chunked(2).forEach { rowOptions ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            rowOptions.forEach { option ->
                                OptionCard(
                                    count = option,
                                    isSelected = selectedOption == option,
                                    showFeedback = showFeedback,
                                    isCorrect = option == targetNumber,
                                    onClick = {
                                        if (!showFeedback) {
                                            selectedOption = option
                                            isCorrect = option == targetNumber
                                            if (isCorrect) score++
                                            showFeedback = true
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }

        if (gameComplete) {
            GameCompletionDialog(
                score = score,
                totalQuestions = totalRounds,
                onPlayAgain = {
                    currentRound = 1
                    score = 0
                    targetNumber = Random.nextInt(1, 11)
                    options = generateOptions(targetNumber)
                    selectedOption = null
                    showFeedback = false
                    gameComplete = false
                },
                onBackToGames = { navController?.popBackStack() }
            )
        }
    }
}

@Composable
fun OptionCard(
    count: Int,
    isSelected: Boolean,
    showFeedback: Boolean,
    isCorrect: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        showFeedback && isSelected && isCorrect -> Color(0xFF4CAF50)
        showFeedback && isSelected && !isCorrect -> Color(0xFFF44336)
        else -> Color.White
    }

    val borderColor = when {
        showFeedback && isSelected && isCorrect -> Color(0xFF4CAF50)
        showFeedback && isSelected && !isCorrect -> Color(0xFFF44336)
        showFeedback && isCorrect -> Color(0xFF4CAF50)
        else -> Color(0xFFAF7EE7).copy(alpha = 0.2f)
    }

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "card_scale"
    )

    Card(
        modifier = modifier
            .aspectRatio(1f)
            .scale(scale)
            .clickable(enabled = !showFeedback, onClick = onClick)
            .border(3.dp, borderColor, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 2.dp else 8.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (!showFeedback) {
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFAF7EE7).copy(alpha = 0.08f),
                                Color.Transparent
                            )
                        )
                    } else {
                        Brush.radialGradient(colors = listOf(Color.Transparent, Color.Transparent))
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "⭐".repeat(count),
                fontSize = 26.sp,
                textAlign = TextAlign.Center,
                lineHeight = 30.sp,
                modifier = Modifier.padding(12.dp)
            )
            
            // Checkmark or X for feedback
            if (showFeedback && isSelected) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Text(
                        text = if (isCorrect) "✓" else "✗",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

fun generateOptions(correctAnswer: Int): List<Int> {
    val options = mutableSetOf(correctAnswer)
    while (options.size < 4) {
        val option = Random.nextInt(1, 11)
        if (option != correctAnswer) {
            options.add(option)
        }
    }
    return options.shuffled()
}
