package com.example.edukid_android.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.edukid_android.components.GameCompletionDialog
import com.example.edukid_android.components.GameHeader

@Composable
fun SudokuGame(navController: NavController) {
    // Dynamic Generator State
    var resetTrigger by remember { mutableStateOf(0) }
    
    val (initialBoard, solvedBoard) = remember(resetTrigger) {
        generateSudokuBoard()
    }
    
    var currentBoard by remember(resetTrigger) { mutableStateOf(initialBoard) }
    var selectedCell by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var gameComplete by remember { mutableStateOf(false) }

    fun checkWin() {
        var isFull = true
        var isCorrect = true
        for(r in 0..3) {
            for(c in 0..3) {
                if(currentBoard[r][c] == 0) isFull = false
                if(currentBoard[r][c] != solvedBoard[r][c]) isCorrect = false
            }
        }
        if (isFull && isCorrect) gameComplete = true
    }

    fun onNumberSelected(number: Int) {
        selectedCell?.let { (r, c) ->
            if (initialBoard[r][c] == 0) { // Only editable if empty in initial
                val newBoard = currentBoard.map { it.toMutableList() }.toMutableList()
                newBoard[r][c] = number
                currentBoard = newBoard
                checkWin()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF2196F3), Color(0xFF64B5F6))
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            GameHeader(title = "Kid Sudoku", score = 0, onBackClick = { navController.popBackStack() })
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Grid
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for(r in 0..3) {
                    Row {
                        for(c in 0..3) {
                            val value = currentBoard[r][c]
                            val isInitial = initialBoard[r][c] != 0
                            val isSelected = selectedCell == (r to c)
                            
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .border(1.dp, Color.Gray)
                                    .background(
                                        if (isSelected) Color(0xFFBBDEFB) else if (isInitial) Color(0xFFE3F2FD) else Color.White
                                    )
                                    .clickable { selectedCell = r to c },
                                contentAlignment = Alignment.Center
                            ) {
                                if (value != 0) {
                                    Text(
                                        text = value.toString(),
                                        fontSize = 32.sp,
                                        fontWeight = if (isInitial) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isInitial) Color.Black else Color(0xFF1976D2)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Number Pad
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                (1..4).forEach { num ->
                    Button(
                        onClick = { onNumberSelected(num) },
                        shape = CircleShape,
                        modifier = Modifier.size(64.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text(text = num.toString(), fontSize = 24.sp, color = Color.Black)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                 Button(
                    onClick = { onNumberSelected(0) }, // Clear
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350)),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("Clear Cell")
                }
                
                Button(
                    onClick = { resetTrigger++ }, // New Game
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("New Game")
                }
            }
        }
        
        if (gameComplete) {
            GameCompletionDialog(
                score = 100,
                totalQuestions = 1,
                onPlayAgain = {
                     resetTrigger++
                     gameComplete = false
                },
                onBackToGames = { navController.popBackStack() }
            )
        }
    }
}

// 4x4 Sudoku Generator
fun generateSudokuBoard(): Pair<List<List<Int>>, List<List<Int>>> {
    // 1. Start with a base valid board
    // 1 2 3 4
    // 3 4 1 2
    // 2 1 4 3
    // 4 3 2 1
    val base = mutableListOf(
        mutableListOf(1, 2, 3, 4),
        mutableListOf(3, 4, 1, 2),
        mutableListOf(2, 1, 4, 3),
        mutableListOf(4, 3, 2, 1)
    )
    
    // 2. Shuffle numbers (map 1..4 to random permutation of 1..4)
    val map = (1..4).toList().shuffled()
    val shuffled = base.map { row ->
        row.map { map[it - 1] }
    }
    
    // 3. Create puzzle by removing elements (leaving ~6-8 clues for kids)
    val puzzle = shuffled.map { it.toMutableList() }.toMutableList()
    val cellsToRemove = 8 // Remove 8 cells, leaving 8 clues
    
    val indices = (0 until 16).toList().shuffled().take(cellsToRemove)
    for(idx in indices) {
        val r = idx / 4
        val c = idx % 4
        puzzle[r][c] = 0
    }
    
    return puzzle to shuffled
}
