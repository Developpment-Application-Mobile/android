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

data class VocabWord(
    val word: String,
    val definition: String,
    val wrongDefinitions: List<String>
)

@Composable
fun VocabularyQuizGame(
    navController: NavController? = null
) {
    val vocabulary = listOf(
        VocabWord("Happy", "Feeling joy or pleasure", listOf("Feeling sad", "Feeling angry", "Feeling tired")),
        VocabWord("Brave", "Showing courage", listOf("Showing fear", "Showing weakness", "Showing sadness")),
        VocabWord("Kind", "Being friendly and caring", listOf("Being mean", "Being rude", "Being selfish")),
        VocabWord("Smart", "Having intelligence", listOf("Being silly", "Being lazy", "Being slow")),
        VocabWord("Fast", "Moving quickly", listOf("Moving slowly", "Standing still", "Going backwards")),
        VocabWord("Big", "Large in size", listOf("Small in size", "Medium in size", "Tiny in size")),
        VocabWord("Bright", "Full of light", listOf("Full of darkness", "Somewhat dark", "Very dark")),
        VocabWord("Loud", "Making much noise", listOf("Making no noise", "Being quiet", "Being silent")),
        VocabWord("Clean", "Free from dirt", listOf("Full of dirt", "Somewhat dirty", "Very messy")),
        VocabWord("Strong", "Having power", listOf("Being weak", "Being fragile", "Being delicate"))
    )

    var currentRound by remember { mutableStateOf(1) }
    var score by remember { mutableStateOf(0) }
    var currentWord by remember { mutableStateOf(vocabulary[0]) }
    var options by remember { mutableStateOf((listOf(currentWord.definition) + currentWord.wrongDefinitions).shuffled()) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var showFeedback by remember { mutableStateOf(false) }
    var gameComplete by remember { mutableStateOf(false) }

    val totalRounds = 10

    LaunchedEffect(showFeedback) {
        if (showFeedback) {
            delay(2000)
            if (currentRound < totalRounds) {
                currentRound++
                currentWord = vocabulary[currentRound - 1]
                options = (listOf(currentWord.definition) + currentWord.wrongDefinitions).shuffled()
                selectedAnswer = null
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
                title = "Vocabulary Quiz",
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
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "What does this word mean?",
                            fontSize = 16.sp,
                            color = Color(0xFF666666)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = currentWord.word,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFAF7EE7)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    options.forEach { option ->
                        val isCorrect = option == currentWord.definition
                        val isSelected = selectedAnswer == option

                        DefinitionButton(
                            definition = option,
                            isSelected = isSelected,
                            showFeedback = showFeedback,
                            isCorrect = isCorrect,
                            onClick = {
                                if (!showFeedback) {
                                    selectedAnswer = option
                                    if (isCorrect) score++
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
                    currentWord = vocabulary[0]
                    options = (listOf(currentWord.definition) + currentWord.wrongDefinitions).shuffled()
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
fun DefinitionButton(
    definition: String,
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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !showFeedback, onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = definition,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = textColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
