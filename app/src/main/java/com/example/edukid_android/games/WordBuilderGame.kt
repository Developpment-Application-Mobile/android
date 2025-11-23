package com.example.edukid_android.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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

data class WordPuzzle(
    val word: String,
    val scrambled: List<Char>
)

@Composable
fun WordBuilderGame(
    navController: NavController? = null
) {
    val words = listOf("CAT", "DOG", "BIRD", "FISH", "TREE", "STAR", "MOON", "SUN", "BOOK", "PLAY")
    
    var currentRound by remember { mutableStateOf(1) }
    var score by remember { mutableStateOf(0) }
    var currentWord by remember { mutableStateOf(words[0]) }
    var scrambledLetters by remember { mutableStateOf(currentWord.toList().shuffled()) }
    var selectedLetters by remember { mutableStateOf<List<Char>>(emptyList()) }
    var showFeedback by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }
    var gameComplete by remember { mutableStateOf(false) }

    val totalRounds = 10

    LaunchedEffect(showFeedback) {
        if (showFeedback) {
            delay(1500)
            if (currentRound < totalRounds) {
                currentRound++
                currentWord = words[currentRound - 1]
                scrambledLetters = currentWord.toList().shuffled()
                selectedLetters = emptyList()
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
                title = "Word Builder",
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
                    text = "Build the word!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Selected Letters Display
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (showFeedback) {
                            if (isCorrect) Color(0xFF4CAF50).copy(alpha = 0.3f) else Color(0xFFF44336).copy(alpha = 0.3f)
                        } else Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (selectedLetters.isEmpty()) {
                            Text(
                                text = "_ ".repeat(currentWord.length),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFCCCCCC)
                            )
                        } else {
                            selectedLetters.forEach { letter ->
                                Text(
                                    text = "$letter ",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E2E2E)
                                )
                            }
                            Text(
                                text = "_ ".repeat(maxOf(0, currentWord.length - selectedLetters.size)),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFCCCCCC)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Letter Tiles
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(scrambledLetters) { letter ->
                        val isUsed = selectedLetters.contains(letter) && 
                                    selectedLetters.count { it == letter } >= scrambledLetters.count { it == letter }
                        
                        LetterTile(
                            letter = letter,
                            isUsed = isUsed,
                            enabled = !showFeedback,
                            onClick = {
                                if (!isUsed && selectedLetters.size < currentWord.length) {
                                    selectedLetters = selectedLetters + letter
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            selectedLetters = emptyList()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF9800)
                        ),
                        enabled = !showFeedback && selectedLetters.isNotEmpty()
                    ) {
                        Text("Clear")
                    }

                    Button(
                        onClick = {
                            isCorrect = selectedLetters.joinToString("") == currentWord
                            if (isCorrect) score++
                            showFeedback = true
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFAF7EE7)
                        ),
                        enabled = !showFeedback && selectedLetters.size == currentWord.length
                    ) {
                        Text("Submit")
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
                    currentWord = words[0]
                    scrambledLetters = currentWord.toList().shuffled()
                    selectedLetters = emptyList()
                    showFeedback = false
                    gameComplete = false
                },
                onBackToGames = { navController?.popBackStack() }
            )
        }
    }
}

@Composable
fun LetterTile(
    letter: Char,
    isUsed: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(enabled = enabled && !isUsed, onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUsed) Color(0xFFCCCCCC) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isUsed) 0.dp else 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = letter.toString(),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = if (isUsed) Color(0xFF999999) else Color(0xFF2E2E2E)
            )
        }
    }
}
