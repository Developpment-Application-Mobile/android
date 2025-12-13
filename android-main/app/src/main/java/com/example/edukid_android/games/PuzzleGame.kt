package com.example.edukid_android.games

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.edukid_android.R
import com.example.edukid_android.components.GameHeader
import com.example.edukid_android.components.GameCompletionDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.unit.IntOffset
import kotlin.random.Random

@Composable
fun PuzzleGame(navController: NavController) {
    val context = LocalContext.current
    var selectedImageRes by remember { mutableStateOf(R.drawable.puzzle_1) }
    var gameStarted by remember { mutableStateOf(false) }
    
    // Game State
    // 3x3 grid = 9 tiles. 0-8. 8 is empty.
    var tiles by remember { mutableStateOf(List(9) { it }) }
    var moves by remember { mutableStateOf(0) }
    var gameComplete by remember { mutableStateOf(false) }

    // Helper to slice bitmap
    val bitmap = remember(selectedImageRes) {
        BitmapFactory.decodeResource(context.resources, selectedImageRes)
    }

    // Logic to move tiles
    fun moveTile(index: Int) {
        val emptyIndex = tiles.indexOf(8) // 8 is the empty slot
        val clickedIndex = index // This is the index in the current grid, not the value

        // Check adjacency
        val row = index / 3
        val col = index % 3
        val emptyRow = emptyIndex / 3
        val emptyCol = emptyIndex % 3

        if ((kotlin.math.abs(row - emptyRow) == 1 && col == emptyCol) ||
            (kotlin.math.abs(col - emptyCol) == 1 && row == emptyRow)
        ) {
            // Swap
            val newTiles = tiles.toMutableList()
            // Swap values at these indices
            val temp = newTiles[index]
            newTiles[index] = newTiles[emptyIndex]
            newTiles[emptyIndex] = temp
            
            tiles = newTiles
            moves++

            // Check win
            if (tiles == List(9) { it }) {
                gameComplete = true
            }
        }
    }

    if (!gameStarted) {
        PuzzleSelectionScreen(
            onImageSelected = { res ->
                selectedImageRes = res
                tiles = (0..8).toList().shuffled()
                // Ensure solvable (simplification: simple shuffle might not be solvable, 
                // but for kid app, let's valid shuffle or just simple random for now. 
                // Better: simulate random moves from solved state)
                var currentTiles = (0..8).toList().toMutableList()
                val emptyIdx = 8
                // Simulate 50 moves to shuffle
                var currentEmpty = 8
                repeat(100) {
                    val validMoves = mutableListOf<Int>()
                     val row = currentEmpty / 3
                     val col = currentEmpty % 3
                     if (row > 0) validMoves.add(currentEmpty - 3)
                     if (row < 2) validMoves.add(currentEmpty + 3)
                     if (col > 0) validMoves.add(currentEmpty - 1)
                     if (col < 2) validMoves.add(currentEmpty + 1)
                     
                     val move = validMoves.random()
                     val temp = currentTiles[move]
                     currentTiles[move] = currentTiles[currentEmpty]
                     currentTiles[currentEmpty] = temp
                     currentEmpty = move
                }
                tiles = currentTiles
                moves = 0
                gameStarted = true
            },
            navController = navController
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFFF9800), Color(0xFFF57C00)) // Orange theme
                    )
                )
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                GameHeader(
                    title = "Sliding Puzzle",
                    score = moves,
                    onBackClick = { gameStarted = false }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Puzzle Grid
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .padding(16.dp)
                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                        .padding(8.dp)
                ) {
                   
                    val tileWidth = bitmap.width / 3
                    val tileHeight = bitmap.height / 3

                   Column {
                       for (r in 0..2) {
                           Row(modifier = Modifier.weight(1f)) {
                               for (c in 0..2) {
                                   val index = r * 3 + c
                                   val tileValue = tiles[index]
                                   
                                   Box(
                                       modifier = Modifier
                                           .weight(1f)
                                           .fillMaxHeight()
                                           .padding(2.dp)
                                           .clip(RoundedCornerShape(8.dp))
                                           .clickable { 
                                               if (!gameComplete && tileValue != 8) moveTile(index)
                                           }
                                   ) {
                                       if (tileValue != 8) { // 8 is empty
                                            // Calculate source rect
                                            val srcX = (tileValue % 3) * tileWidth
                                            val srcY = (tileValue / 3) * tileHeight
                                            
                                            // Create bitmap subset
                                            // Note: In real app, cache these bitmaps
                                            val tileBitmap = Bitmap.createBitmap(bitmap, srcX, srcY, tileWidth, tileHeight)
                                            
                                            Image(
                                                bitmap = tileBitmap.asImageBitmap(),
                                                contentDescription = null,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                       } else {
                                           Box(modifier = Modifier
                                               .fillMaxSize()
                                               .background(Color.White.copy(alpha = 0.1f)))
                                       }
                                   }
                               }
                           }
                       }
                   }
                }
                
                Text(
                    text = "Tap tiles to move them!",
                    color = Color.White,
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 16.dp)
                )
            }
            
            if (gameComplete) {
                GameCompletionDialog(
                    score = 100 - moves, // Simple score logic
                    totalQuestions = 1,
                    onPlayAgain = {
                        gameStarted = false
                        gameComplete = false
                    },
                    onBackToGames = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun PuzzleSelectionScreen(
    onImageSelected: (Int) -> Unit,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF272052), Color(0xFF673AB7))))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
               Icon(
                   imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack, 
                   contentDescription = "Back",
                   tint = Color.White
               )
            }
            Text("Select a Puzzle", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        val options = listOf(
            R.drawable.puzzle_1 to "Lion King",
            R.drawable.puzzle_2 to "Magic Forest",
            R.drawable.puzzle_3 to "Space Explorer"
        )
        
        options.forEach { (res, title) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 16.dp)
                    .clickable { onImageSelected(res) },
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Box {
                    Image(
                        painter = androidx.compose.ui.res.painterResource(id = res),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha=0.7f))))
                    )
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}
