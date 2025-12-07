package com.example.edukid_android.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.edukid_android.R
import com.example.edukid_android.components.BottomNavigationBar

data class Game(
    val id: String,
    val title: String,
    val icon: String,
    val difficulty: String,
    val description: String
)

@Composable
fun GamesScreen(
    navController: NavController? = null
) {
    val games = remember {
        listOf(
            Game(
                id = "number_match",
                title = "Number Match",
                icon = "🧮",
                difficulty = "Easy",
                description = "Match numbers with quantities"
            ),
            Game(
                id = "math_challenge",
                title = "Math Challenge",
                icon = "🔢",
                difficulty = "Medium",
                description = "Quick arithmetic problems"
            ),
            Game(
                id = "word_builder",
                title = "Word Builder",
                icon = "🔤",
                difficulty = "Easy",
                description = "Spell words from letters"
            ),
            Game(
                id = "vocabulary_quiz",
                title = "Vocabulary Quiz",
                icon = "📚",
                difficulty = "Medium",
                description = "Learn new words"
            ),
            Game(
                id = "pattern_puzzle",
                title = "Pattern Puzzle",
                icon = "🧩",
                difficulty = "Hard",
                description = "Complete the pattern"
            ),
            Game(
                id = "color_match",
                title = "Color Match",
                icon = "🎨",
                difficulty = "Easy",
                description = "Match colors and shapes"
            ),
            Game(
                id = "memory_cards",
                title = "Memory Cards",
                icon = "🧠",
                difficulty = "Medium",
                description = "Classic card matching game"
            ),
            Game(
                id = "world_explorer",
                title = "World Explorer",
                icon = "🌍",
                difficulty = "Hard",
                description = "Learn about countries"
            )
        )
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
        // Decorative elements
        DecorativeElementsGames()

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Main content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(20.dp)
            ) {
                // Header
                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = "🎮 Games",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Choose a game to play and learn!",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White.copy(alpha = 0.9f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Games Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(games) { game ->
                        GameCard(
                            game = game,
                            onClick = {
                                navController?.navigate("game/${game.id}")
                            }
                        )
                    }
                }
            }

            // Bottom Navigation
            BottomNavigationBar(
                currentRoute = "childGames",
                onNavigate = { route ->
                    navController?.navigate(route) {
                        popUpTo("childHome") { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

@Composable
fun GameCard(
    game: Game,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "hover")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.9f)
            .scale(scale)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.98f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFAF7EE7).copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFAF7EE7).copy(alpha = 0.2f),
                                Color(0xFFAF7EE7).copy(alpha = 0.05f)
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = game.icon,
                    fontSize = 36.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = game.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E2E2E),
                textAlign = TextAlign.Center,
                maxLines = 2
            )

            // Description
            Text(
                text = game.description,
                fontSize = 11.sp,
                color = Color(0xFF666666),
                textAlign = TextAlign.Center,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Difficulty Badge
            Box(
                modifier = Modifier
                    .background(
                        color = when (game.difficulty) {
                            "Easy" -> Color(0xFF4CAF50).copy(alpha = 0.15f)
                            "Medium" -> Color(0xFFFF9800).copy(alpha = 0.15f)
                            "Hard" -> Color(0xFFF44336).copy(alpha = 0.15f)
                            else -> Color.Gray.copy(alpha = 0.15f)
                        },
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = game.difficulty,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (game.difficulty) {
                        "Easy" -> Color(0xFF4CAF50)
                        "Medium" -> Color(0xFFFF9800)
                        "Hard" -> Color(0xFFF44336)
                        else -> Color.Gray
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Play Button
            Button(
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFAF7EE7)
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "Play",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun BoxScope.DecorativeElementsGames() {
    Image(
        painter = painterResource(id = R.drawable.education_book),
        contentDescription = "Education Book",
        modifier = Modifier
            .size(90.dp)
            .offset(x = (-20).dp, y = 20.dp)
            .blur(1.dp),
        contentScale = ContentScale.Fit
    )

    Image(
        painter = painterResource(id = R.drawable.coins),
        contentDescription = "Coins",
        modifier = Modifier
            .size(70.dp)
            .offset(x = 300.dp, y = 50.dp)
            .rotate(15f),
        contentScale = ContentScale.Fit
    )

    Image(
        painter = painterResource(id = R.drawable.coins),
        contentDescription = "Coins",
        modifier = Modifier
            .size(60.dp)
            .offset(x = 30.dp, y = 650.dp)
            .rotate(-20f),
        contentScale = ContentScale.Fit
    )
}
