package com.example.edukid_android.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.edukid_android.components.GameCompletionDialog
import com.example.edukid_android.components.GameHeader
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun PatternPuzzleGame(
    navController: NavController? = null
) {
    val shapes = listOf("⬤", "■", "▲", "★", "♥", "◆")
    val colors = listOf(
        Color(0xFFFF0000), Color(0xFF00FF00), Color(0xFF0000FF),
        Color(0xFFFFFF00), Color(0xFFFF00FF), Color(0xFF00FFFF)
    )

    var currentRound by remember { mutableStateOf(1) }
    var score by remember { mutableStateOf(0) }
    var pattern by remember { mutableStateOf(generatePattern(shapes, colors)) }
    var options by remember { mutableStateOf(generatePatternOptions(pattern, shapes, colors)) }
    var selectedOption by remember { mutableStateOf<Pair<String, Color>?>(null) }
    var showFeedback by remember { mutableStateOf(false) }
    var gameComplete by remember { mutableStateOf(false) }

    val totalRounds = 10

    LaunchedEffect(showFeedback) {
        if (showFeedback) {
            delay(1500)
            if (currentRound < totalRounds) {
                currentRound++
                pattern = generatePattern(shapes, colors)
                options = generatePatternOptions(pattern, shapes, colors)
                selectedOption = null
                showFeedback = false
            } else {
                gameComplete = true
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
                title = "Pattern Puzzle",
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
                Text(
                    text = "Complete the pattern",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Pattern Display
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        pattern.dropLast(1).forEach { (shape, color) ->
                            Text(
                                text = shape,
                                fontSize = 40.sp,
                                color = color
                            )
                        }
                        Text(
                            text = "?",
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFCCCCCC)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Options
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    options.forEach { option ->
                        val (shape, color) = option
                        val isCorrect = option == pattern.last()
                        val isSelected = selectedOption == option

                        PatternOption(
                            shape = shape,
                            color = color,
                            isSelected = isSelected,
                            showFeedback = showFeedback,
                            isCorrect = isCorrect,
                            onClick = {
                                if (!showFeedback) {
                                    selectedOption = option
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

        if (gameComplete) {
            GameCompletionDialog(
                score = score,
                totalQuestions = totalRounds,
                onPlayAgain = {
                    currentRound = 1
                    score = 0
                    pattern = generatePattern(shapes, colors)
                    options = generatePatternOptions(pattern, shapes, colors)
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
fun PatternOption(
    shape: String,
    color: Color,
    isSelected: Boolean,
    showFeedback: Boolean,
    isCorrect: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = when {
        showFeedback && isSelected && isCorrect -> Color(0xFF4CAF50)
        showFeedback && isSelected && !isCorrect -> Color(0xFFF44336)
        showFeedback && isCorrect -> Color(0xFF4CAF50)
        else -> Color.Transparent
    }

    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(enabled = !showFeedback, onClick = onClick)
            .border(3.dp, borderColor, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = shape,
                fontSize = 48.sp,
                color = color
            )
        }
    }
}

fun generatePattern(shapes: List<String>, colors: List<Color>): List<Pair<String, Color>> {
    val patternLength = 4
    val shape = shapes.random()
    val colorList = colors.shuffled().take(patternLength)
    return colorList.map { Pair(shape, it) }
}

fun generatePatternOptions(
    pattern: List<Pair<String, Color>>,
    shapes: List<String>,
    colors: List<Color>
): List<Pair<String, Color>> {
    val correctAnswer = pattern.last()
    val options = mutableListOf(correctAnswer)
    
    while (options.size < 4) {
        val option = Pair(pattern[0].first, colors.random())
        if (option !in options) {
            options.add(option)
        }
    }
    
    return options.shuffled()
}
