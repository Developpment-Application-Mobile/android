package com.example.edukid_android.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.edukid_android.components.GameCompletionDialog
import com.example.edukid_android.components.GameHeader
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

data class Point(val x: Int, val y: Int)
enum class Direction { UP, DOWN, LEFT, RIGHT }

@Composable
fun SnakeGame(navController: NavController) {
    val gridSize = 20
    
    // State
    var snake by remember { mutableStateOf(listOf(Point(10, 10), Point(10, 11), Point(10, 12))) }
    var food by remember { mutableStateOf(Point(5, 5)) }
    var direction by remember { mutableStateOf(Direction.UP) }
    var score by remember { mutableStateOf(0) }
    var gameComplete by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(true) }

    fun generateFood(): Point {
        var newFood: Point
        do {
            newFood = Point((0 until gridSize).random(), (0 until gridSize).random())
        } while (snake.contains(newFood))
        return newFood
    }

    LaunchedEffect(isPlaying) {
        while (isPlaying && isActive) {
            delay(200) // Speed
            
            val head = snake.first()
            val newHead = when (direction) {
                Direction.UP -> Point(head.x, head.y - 1)
                Direction.DOWN -> Point(head.x, head.y + 1)
                Direction.LEFT -> Point(head.x - 1, head.y)
                Direction.RIGHT -> Point(head.x + 1, head.y)
            }

            // Check collision with walls
            if (newHead.x < 0 || newHead.x >= gridSize || newHead.y < 0 || newHead.y >= gridSize) {
                isPlaying = false
                gameComplete = true
                continue
            }

            // Check collision with self
            if (snake.contains(newHead)) {
                isPlaying = false
                gameComplete = true
                continue
            }

            // Move
            val newSnake = snake.toMutableList()
            newSnake.add(0, newHead)
            
            if (newHead == food) {
                score += 10
                food = generateFood()
                // Don't remove tail (grow)
            } else {
                newSnake.removeAt(newSnake.size - 1)
            }
            snake = newSnake
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF212121))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GameHeader(title = "Snake", score = score, onBackClick = { navController.popBackStack() })

            Spacer(modifier = Modifier.height(16.dp))

            // Game Board
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .padding(8.dp)
                    .background(Color.Black, RoundedCornerShape(8.dp))
                    .border(2.dp, Color(0xFF4CAF50), RoundedCornerShape(8.dp))
            ) {
                val tileSize = maxWidth / gridSize

                // Draw Snake
                snake.forEach { segment ->
                    Box(
                        modifier = Modifier
                            .offset(x = tileSize * segment.x, y = tileSize * segment.y)
                            .size(tileSize)
                            .padding(1.dp)
                            .background(Color(0xFF4CAF50), RoundedCornerShape(2.dp))
                    )
                }

                // Draw Food
                Box(
                    modifier = Modifier
                        .offset(x = tileSize * food.x, y = tileSize * food.y)
                        .size(tileSize)
                        .padding(2.dp)
                        .background(Color(0xFFFF5252), CircleShape)
                )
            }

            // Controls
            Box(
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(
                        onClick = { if (direction != Direction.DOWN) direction = Direction.UP },
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(Icons.Default.KeyboardArrowUp, null, tint = Color.White, modifier = Modifier.size(48.dp))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(48.dp)) {
                        IconButton(
                            onClick = { if (direction != Direction.RIGHT) direction = Direction.LEFT },
                            modifier = Modifier.size(64.dp)
                        ) {
                            Icon(Icons.Default.KeyboardArrowLeft, null, tint = Color.White, modifier = Modifier.size(48.dp))
                        }
                        IconButton(
                            onClick = { if (direction != Direction.LEFT) direction = Direction.RIGHT },
                            modifier = Modifier.size(64.dp)
                        ) {
                            Icon(Icons.Default.KeyboardArrowRight, null, tint = Color.White, modifier = Modifier.size(48.dp))
                        }
                    }
                    IconButton(
                        onClick = { if (direction != Direction.UP) direction = Direction.DOWN },
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(Icons.Default.KeyboardArrowDown, null, tint = Color.White, modifier = Modifier.size(48.dp))
                    }
                }
            }
        }
        
        if (gameComplete) {
            GameCompletionDialog(
                score = score,
                totalQuestions = 1,
                onPlayAgain = {
                    snake = listOf(Point(10, 10), Point(10, 11), Point(10, 12))
                    direction = Direction.UP
                    score = 0
                    gameComplete = false
                    isPlaying = true
                },
                onBackToGames = { navController.popBackStack() }
            )
        }
    }
}
