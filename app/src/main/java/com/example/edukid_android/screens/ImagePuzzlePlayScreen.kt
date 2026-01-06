package com.example.edukid_android.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.layout
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.edukid_android.components.GameCompletionDialog
import com.example.edukid_android.models.PuzzleResponse
import kotlinx.coroutines.delay
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagePuzzlePlayScreen(
    puzzle: PuzzleResponse,
    onBack: () -> Unit,
    onComplete: (Int, Int) -> Unit // score, timeSpent
) {
    val context = LocalContext.current
    val gridSize = puzzle.gridSize
    
    // Game State
    var tiles by remember { mutableStateOf(puzzle.pieces.map { it.currentPosition }) }
    var moves by remember { mutableStateOf(0) }
    var timeSpent by remember { mutableStateOf(0) }
    var gameComplete by remember { mutableStateOf(false) }
    var isShuffling by remember { mutableStateOf(true) }
    var countdown by remember { mutableStateOf(5) }
    
    // Timer
    LaunchedEffect(Unit) {
        // Countdown before shuffle
        while (countdown > 0) {
            delay(1000)
            countdown--
        }
        
        // Shuffle the tiles
        var currentTiles = (0 until gridSize * gridSize).toList().toMutableList()
        var currentEmpty = gridSize * gridSize - 1 // Last tile is empty
        
        // Simulate random moves to create solvable puzzle
        repeat(100) {
            val validMoves = mutableListOf<Int>()
            val row = currentEmpty / gridSize
            val col = currentEmpty % gridSize
            if (row > 0) validMoves.add(currentEmpty - gridSize)
            if (row < gridSize - 1) validMoves.add(currentEmpty + gridSize)
            if (col > 0) validMoves.add(currentEmpty - 1)
            if (col < gridSize - 1) validMoves.add(currentEmpty + 1)
            
            val move = validMoves.random()
            val temp = currentTiles[move]
            currentTiles[move] = currentTiles[currentEmpty]
            currentTiles[currentEmpty] = temp
            currentEmpty = move
        }
        
        tiles = currentTiles
        isShuffling = false
        
        // Start timer
        while (!gameComplete) {
            delay(1000)
            timeSpent++
        }
    }
    
    // Move tile logic
    fun moveTile(index: Int) {
        if (isShuffling || gameComplete) return
        
        val emptyIndex = tiles.indexOf(gridSize * gridSize - 1)
        val row = index / gridSize
        val col = index % gridSize
        val emptyRow = emptyIndex / gridSize
        val emptyCol = emptyIndex % gridSize
        
        // Check if adjacent
        if ((kotlin.math.abs(row - emptyRow) == 1 && col == emptyCol) ||
            (kotlin.math.abs(col - emptyCol) == 1 && row == emptyRow)
        ) {
            val newTiles = tiles.toMutableList()
            val temp = newTiles[index]
            newTiles[index] = newTiles[emptyIndex]
            newTiles[emptyIndex] = temp
            tiles = newTiles
            moves++
            
            // Check if solved
            if (tiles == (0 until gridSize * gridSize).toList()) {
                gameComplete = true
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(puzzle.title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    Text(
                        text = "Moves: $moves",
                        modifier = Modifier.padding(end = 16.dp),
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = puzzle.puzzleType.color
                )
            )
        },
        containerColor = Color(0xFF272052)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Timer and Info
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Time: ${timeSpent}s",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${gridSize}×${gridSize} Grid",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Puzzle Grid
                if (isShuffling) {
                    // Show countdown
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        // Show full image
                        puzzle.imageUrl?.let { url ->
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(url)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(16.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                        
                        // Countdown overlay
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Get Ready!",
                                    color = Color.White,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = countdown.toString(),
                                    color = Color(0xFFAF7EE7),
                                    fontSize = 72.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Memorize the image!",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 18.sp
                                )
                            }
                        }
                    }
                } else {
                    // Show puzzle grid
                    PuzzleGrid(
                        puzzle = puzzle,
                        tiles = tiles,
                        gridSize = gridSize,
                        onTileClick = { moveTile(it) }
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = if (isShuffling) "Memorize the image!" else "Tap tiles to move them!",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            // Completion Dialog
            if (gameComplete) {
                GameCompletionDialog(
                    score = maxOf(0, 1000 - moves * 10 - timeSpent),
                    totalQuestions = 1,
                    onPlayAgain = {
                        // Reset game
                        moves = 0
                        timeSpent = 0
                        gameComplete = false
                        isShuffling = true
                        countdown = 5
                    },
                    onBackToGames = {
                        onComplete(maxOf(0, 1000 - moves * 10 - timeSpent), timeSpent)
                        onBack()
                    }
                )
            }
        }
    }
}

@Composable
fun PuzzleGrid(
    puzzle: PuzzleResponse,
    tiles: List<Int>,
    gridSize: Int,
    onTileClick: (Int) -> Unit
) {
    val context = LocalContext.current
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black.copy(alpha = 0.3f))
            .padding(4.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            for (row in 0 until gridSize) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    for (col in 0 until gridSize) {
                        val index = row * gridSize + col
                        val tileValue = tiles[index]
                        val isEmpty = tileValue == gridSize * gridSize - 1
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(2.dp)
                        ) {
                            if (!isEmpty && puzzle.imageUrl != null) {
                                PuzzlePiece(
                                    imageUrl = puzzle.imageUrl,
                                    pieceValue = tileValue,
                                    gridSize = gridSize,
                                    onClick = { onTileClick(index) }
                                )
                            } else {
                                // Empty space
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White.copy(alpha = 0.1f))
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PuzzlePiece(
    imageUrl: String,
    pieceValue: Int,
    gridSize: Int,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    
    // Calculate which row and column this piece belongs to in the original image
    val pieceRow = pieceValue / gridSize
    val pieceCol = pieceValue % gridSize
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .border(
                width = 1.5.dp,
                color = Color.White.copy(alpha = 0.6f),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(context)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds()
        ) {
            val painter = painter
            if (painter != null) {
                // Get the intrinsic size of the image
                val imageWidth = painter.intrinsicSize.width
                val imageHeight = painter.intrinsicSize.height
                
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clipToBounds()
                ) {
                    Image(
                        painter = painter,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .layout { measurable, constraints ->
                                // Each piece should show 1/gridSize of the image
                                val pieceWidth = constraints.maxWidth
                                val pieceHeight = constraints.maxHeight
                                
                                // The full image needs to be gridSize times larger
                                val fullWidth = pieceWidth * gridSize
                                val fullHeight = pieceHeight * gridSize
                                
                                // Measure the image at full size
                                val placeable = measurable.measure(
                                    constraints.copy(
                                        minWidth = fullWidth,
                                        maxWidth = fullWidth,
                                        minHeight = fullHeight,
                                        maxHeight = fullHeight
                                    )
                                )
                                
                                // Layout with piece size but position to show correct section
                                layout(pieceWidth, pieceHeight) {
                                    // Place the image offset to show the correct piece
                                    placeable.place(
                                        x = -pieceWidth * pieceCol,
                                        y = -pieceHeight * pieceRow
                                    )
                                }
                            }
                    )
                }
            } else {
                // Loading state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Gray.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                }
            }
        }
    }
}
