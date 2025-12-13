package com.example.edukid_android.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.ThumbUp
//import androidx.compose.material.icons.filled.EmojiEmotions
//import androidx.compose.material.icons.filled.MusicNote
//import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.edukid_android.R

data class OfflineGame(
    val title: String,
    val icon: ImageVector,
    val route: String,
    val color: Color
)

@Composable
fun OfflineGamesScreen(
    navController: NavController
) {
    val games = listOf(
        OfflineGame("Memory", Icons.Default.Place, "game/memory_cards", Color(0xFF4CAF50)),
        OfflineGame("Kid Sudoku", Icons.Default.AccountBox, "game/sudoku", Color(0xFF2196F3)),
        OfflineGame("Sliding Puzzle", Icons.Default.Face, "game/puzzle", Color(0xFFFF9800)),
        OfflineGame("Tic Tac Toe", Icons.Default.Close, "game/tictactoe", Color(0xFFE91E63)),
        OfflineGame("Drawing Pad", Icons.Default.Edit, "game/drawing", Color(0xFF9C27B0)),
        OfflineGame("Word Guess", Icons.Default.Star, "game/word_guess", Color(0xFF673AB7)),
        OfflineGame("Snake", Icons.Default.Check, "game/snake", Color(0xFF43A047)),
        OfflineGame("Piano", Icons.Default.DateRange, "game/piano", Color(0xFF3F51B5)),
        OfflineGame("Math Dash", Icons.Default.Share, "game/math_dash", Color(0xFF009688)), // Fixed typo
        OfflineGame("Whack-a-Mole", Icons.Default.ThumbUp, "game/whack_a_mole", Color(0xFF795548))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF272052),
                        Color(0xFF673AB7)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ThumbUp,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Offline Arcade",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Text(
                text = "No Internet? Play these games!",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )

            // Games Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(games) { game ->
                    OfflineGameCard(game = game, onClick = { navController.navigate(game.route) })
                }
            }
        }
    }
}

@Composable
fun OfflineGameCard(
    game: OfflineGame,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            game.color.copy(alpha = 0.8f),
                            game.color
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(100f, 100f)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.White.copy(alpha = 0.2f),
                    modifier = Modifier.size(64.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = game.icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = game.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
