package com.example.edukid_android.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
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

data class ColorShape(
    val color: Color,
    val colorName: String,
    val shape: String
)

@Composable
fun ColorMatchGame(
    navController: NavController? = null,
    parentId: String? = null,
    kidId: String? = null
) {
    val colors = listOf(
        ColorShape(Color(0xFFFF0000), "Red", "⬤"),
        ColorShape(Color(0xFF00FF00), "Green", "⬤"),
        ColorShape(Color(0xFF0000FF), "Blue", "⬤"),
        ColorShape(Color(0xFFFFFF00), "Yellow", "⬤"),
        ColorShape(Color(0xFFFF00FF), "Purple", "⬤"),
        ColorShape(Color(0xFF00FFFF), "Cyan", "⬤")
    )

    val shapes = listOf("⬤", "■", "▲", "★")

    var currentRound by remember { mutableStateOf(1) }
    var score by remember { mutableStateOf(0) }
    var targetColor by remember { mutableStateOf(colors.random()) }
    var targetShape by remember { mutableStateOf(shapes.random()) }
    var options by remember { mutableStateOf(generateColorOptions(targetColor, targetShape, colors, shapes)) }
    var selectedOption by remember { mutableStateOf<Pair<ColorShape, String>?>(null) }
    var showFeedback by remember { mutableStateOf(false) }
    var gameComplete by remember { mutableStateOf(false) }

    val totalRounds = 15

    val scope = rememberCoroutineScope()
    
    LaunchedEffect(showFeedback) {
        if (showFeedback) {
            delay(1000)
            if (currentRound < totalRounds) {
                currentRound++
                targetColor = colors.random()
                targetShape = shapes.random()
                options = generateColorOptions(targetColor, targetShape, colors, shapes)
                selectedOption = null
                showFeedback = false
            } else {
                gameComplete = true
                // Track progress
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
                title = "Color Match",
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
                            text = "Find:",
                            fontSize = 18.sp,
                            color = Color(0xFF666666)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = targetShape,
                            fontSize = 64.sp,
                            color = targetColor.color
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = targetColor.colorName,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E2E2E)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(options) { option ->
                        val (color, shape) = option
                        val isCorrect = color.colorName == targetColor.colorName && shape == targetShape
                        val isSelected = selectedOption == option

                        ColorShapeOption(
                            color = color.color,
                            shape = shape,
                            isSelected = isSelected,
                            showFeedback = showFeedback,
                            isCorrect = isCorrect,
                            onClick = {
                                if (!showFeedback) {
                                    selectedOption = option
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
                    // Track progress if credentials are available
                    if (parentId != null && kidId != null) {
                         // We can launch this in a coroutine but we need a scope.
                         // However, this is mainly triggered on 'Play Again' or 'Back to Games' usually?
                         // The request is better done when gameComplete becomes true.
                    }
                    currentRound = 1
                    score = 0
                    targetColor = colors.random()
                    targetShape = shapes.random()
                    options = generateColorOptions(targetColor, targetShape, colors, shapes)
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
fun ColorShapeOption(
    color: Color,
    shape: String,
    isSelected: Boolean,
    showFeedback: Boolean,
    isCorrect: Boolean,
    onClick: () -> Unit
) {
    val borderColor = when {
        showFeedback && isSelected && isCorrect -> Color(0xFF4CAF50)
        showFeedback && isSelected && !isCorrect -> Color(0xFFF44336)
        showFeedback && isCorrect -> Color(0xFF4CAF50)
        else -> Color.Transparent
    }

    Card(
        modifier = Modifier
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

fun generateColorOptions(
    targetColor: ColorShape,
    targetShape: String,
    allColors: List<ColorShape>,
    allShapes: List<String>
): List<Pair<ColorShape, String>> {
    val options = mutableListOf<Pair<ColorShape, String>>()
    options.add(Pair(targetColor, targetShape))
    
    while (options.size < 9) {
        val randomColor = allColors.random()
        val randomShape = allShapes.random()
        val option = Pair(randomColor, randomShape)
        
        if (option !in options) {
            options.add(option)
        }
    }
    
    return options.shuffled()
}
