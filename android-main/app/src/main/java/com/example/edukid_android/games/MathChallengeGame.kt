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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.edukid_android.components.GameCompletionDialog
import com.example.edukid_android.components.GameHeader
import kotlinx.coroutines.delay
import kotlin.random.Random

data class MathQuestion(
    val num1: Int,
    val num2: Int,
    val operation: String,
    val correctAnswer: Int,
    val options: List<Int>
)

@Composable
fun MathChallengeGame(
    navController: NavController? = null
) {
    var currentRound by remember { mutableStateOf(1) }
    var score by remember { mutableStateOf(0) }
    var timeLeft by remember { mutableStateOf(30) }
    var question by remember { mutableStateOf(generateMathQuestion()) }
    var selectedAnswer by remember { mutableStateOf<Int?>(null) }
    var showFeedback by remember { mutableStateOf(false) }
    var gameComplete by remember { mutableStateOf(false) }

    val totalRounds = 10

    // Timer
    LaunchedEffect(currentRound, showFeedback) {
        if (!showFeedback && !gameComplete) {
            while (timeLeft > 0) {
                delay(1000)
                timeLeft--
            }
            // Time's up
            showFeedback = true
        }
    }

    LaunchedEffect(showFeedback) {
        if (showFeedback) {
            delay(1500)
            if (currentRound < totalRounds) {
                currentRound++
                question = generateMathQuestion()
                selectedAnswer = null
                showFeedback = false
                timeLeft = 30
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
                title = "Math Challenge",
                score = score,
                currentQuestion = currentRound,
                totalQuestions = totalRounds,
                timeLeft = timeLeft,
                onBackClick = { navController?.popBackStack() }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Question
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${question.num1} ${question.operation} ${question.num2} = ?",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFAF7EE7),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Answer Options
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    question.options.forEach { option ->
                        AnswerButton(
                            answer = option,
                            isSelected = selectedAnswer == option,
                            showFeedback = showFeedback,
                            isCorrect = option == question.correctAnswer,
                            onClick = {
                                if (!showFeedback) {
                                    selectedAnswer = option
                                    if (option == question.correctAnswer) score++
                                    showFeedback = true
                                }
                            }
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
                    timeLeft = 30
                    question = generateMathQuestion()
                    selectedAnswer = null
                    showFeedback = false
                    gameComplete = false
                },
                onBackToGames = { navController?.popBackStack() }
            )
        }
    }
}

@Composable
fun AnswerButton(
    answer: Int,
    isSelected: Boolean,
    showFeedback: Boolean,
    isCorrect: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        showFeedback && isSelected && isCorrect -> Color(0xFF4CAF50)
        showFeedback && isSelected && !isCorrect -> Color(0xFFF44336)
        showFeedback && isCorrect -> Color(0xFF4CAF50)
        else -> Color.White
    }

    val textColor = when {
        showFeedback && (isCorrect || isSelected) -> Color.White
        else -> Color(0xFF2E2E2E)
    }

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(16.dp),
        enabled = !showFeedback
    ) {
        Text(
            text = answer.toString(),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

fun generateMathQuestion(): MathQuestion {
    val operations = listOf("+", "-")
    val operation = operations.random()
    
    val (num1, num2, correctAnswer) = when (operation) {
        "+" -> {
            val n1 = Random.nextInt(1, 50)
            val n2 = Random.nextInt(1, 50)
            Triple(n1, n2, n1 + n2)
        }
        else -> { // "-"
            val n1 = Random.nextInt(10, 50)
            val n2 = Random.nextInt(1, n1)
            Triple(n1, n2, n1 - n2)
        }
    }

    val options = mutableSetOf(correctAnswer)
    while (options.size < 4) {
        val offset = Random.nextInt(-10, 11)
        val option = correctAnswer + offset
        if (option > 0 && option != correctAnswer) {
            options.add(option)
        }
    }

    return MathQuestion(num1, num2, operation, correctAnswer, options.shuffled())
}
