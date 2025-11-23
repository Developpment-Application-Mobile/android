package com.example.edukid_android.games

import androidx.compose.foundation.background
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

data class MemoryCard(
    val id: Int,
    val emoji: String,
    val isFlipped: Boolean = false,
    val isMatched: Boolean = false
)

@Composable
fun MemoryCardsGame(
    navController: NavController? = null
) {
    val emojis = listOf("🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼")
    val cards = remember {
        (emojis + emojis).mapIndexed { index, emoji ->
            MemoryCard(id = index, emoji = emoji)
        }.shuffled().toMutableStateList()
    }

    var flippedCards by remember { mutableStateOf<List<Int>>(emptyList()) }
    var moves by remember { mutableStateOf(0) }
    var matchedPairs by remember { mutableStateOf(0) }
    var gameComplete by remember { mutableStateOf(false) }
    var canFlip by remember { mutableStateOf(true) }

    LaunchedEffect(flippedCards.size) {
        if (flippedCards.size == 2) {
            canFlip = false
            delay(800)
            
            val first = cards[flippedCards[0]]
            val second = cards[flippedCards[1]]
            
            if (first.emoji == second.emoji) {
                cards[flippedCards[0]] = first.copy(isMatched = true)
                cards[flippedCards[1]] = second.copy(isMatched = true)
                matchedPairs++
                
                if (matchedPairs == 8) {
                    gameComplete = true
                }
            } else {
                cards[flippedCards[0]] = first.copy(isFlipped = false)
                cards[flippedCards[1]] = second.copy(isFlipped = false)
            }
            
            flippedCards = emptyList()
            canFlip = true
        }
    }

    val score = maxOf(0, 100 - (moves - 16) * 2)

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
                title = "Memory Cards",
                score = score,
                onBackClick = { navController?.popBackStack() }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Moves: $moves | Pairs: $matchedPairs/8",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(cards.size) { index ->
                        val card = cards[index]
                        MemoryCardItem(
                            card = card,
                            onClick = {
                                if (canFlip && !card.isFlipped && !card.isMatched && flippedCards.size < 2) {
                                    cards[index] = card.copy(isFlipped = true)
                                    flippedCards = flippedCards + index
                                    if (flippedCards.size == 2) moves++
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
                totalQuestions = 100,
                onPlayAgain = {
                    cards.clear()
                    cards.addAll(
                        (emojis + emojis).mapIndexed { index, emoji ->
                            MemoryCard(id = index, emoji = emoji)
                        }.shuffled()
                    )
                    flippedCards = emptyList()
                    moves = 0
                    matchedPairs = 0
                    gameComplete = false
                },
                onBackToGames = { navController?.popBackStack() }
            )
        }
    }
}

@Composable
fun MemoryCardItem(
    card: MemoryCard,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (card.isFlipped || card.isMatched) Color.White else Color(0xFFAF7EE7)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (card.isFlipped || card.isMatched) {
                Text(
                    text = card.emoji,
                    fontSize = 32.sp
                )
            } else {
                Text(
                    text = "?",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
