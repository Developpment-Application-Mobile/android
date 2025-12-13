package com.example.edukid_android.games

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import kotlinx.coroutines.delay

@Composable
fun TicTacToeGame(navController: NavController) {
    var board by remember { mutableStateOf(List(9) { "" }) }
    var currentPlayer by remember { mutableStateOf("X") }
    var gameComplete by remember { mutableStateOf(false) }
    var winner by remember { mutableStateOf<String?>(null) }
    var isAiTurn by remember { mutableStateOf(false) }

    fun checkWinner(b: List<String>): String? {
        val lines = listOf(
            listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8), // Rows
            listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8), // Cols
            listOf(0, 4, 8), listOf(2, 4, 6) // Diagonals
        )
        for (line in lines) {
            val (a, bIdx, c) = line
            if (b[a].isNotEmpty() && b[a] == b[bIdx] && b[a] == b[c]) {
                return b[a]
            }
        }
        if (b.none { it.isEmpty() }) return "Draw"
        return null
    }

    LaunchedEffect(isAiTurn) {
        if (isAiTurn && !gameComplete) {
            delay(500) // Thinking time
            // Simple AI: Random empty spot
            val emptyIndices = board.indices.filter { board[it].isEmpty() }
            if (emptyIndices.isNotEmpty()) {
                val move = emptyIndices.random()
                val newBoard = board.toMutableList()
                newBoard[move] = "O"
                board = newBoard
                
                val result = checkWinner(board)
                if (result != null) {
                    winner = result
                    gameComplete = true
                } else {
                    currentPlayer = "X"
                }
            }
            isAiTurn = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFFE91E63), Color(0xFF880E4F))
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GameHeader(title = "Tic Tac Toe", score = 0, onBackClick = { navController.popBackStack() })
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = if (gameComplete) {
                    if (winner == "Draw") "It's a Draw!" else "Winner: $winner"
                } else {
                    "Turn: $currentPlayer"
                },
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .aspectRatio(1f)
                    .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    for (row in 0..2) {
                        Row(modifier = Modifier.weight(1f)) {
                            for (col in 0..2) {
                                val index = row * 3 + col
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .padding(4.dp)
                                        .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                                        .clickable {
                                            if (!gameComplete && board[index].isEmpty() && !isAiTurn) {
                                                val newBoard = board.toMutableList()
                                                newBoard[index] = "X"
                                                board = newBoard
                                                val result = checkWinner(board)
                                                if (result != null) {
                                                    winner = result
                                                    gameComplete = true
                                                } else {
                                                    currentPlayer = "O"
                                                    isAiTurn = true
                                                }
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = board[index],
                                        fontSize = 48.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (board[index] == "X") Color(0xFFE91E63) else Color(0xFF2196F3)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        
        if (gameComplete) {
            GameCompletionDialog(
                score = if(winner == "X") 100 else 50,
                totalQuestions = 1,
                onPlayAgain = {
                    board = List(9) { "" }
                    currentPlayer = "X"
                    winner = null
                    gameComplete = false
                    isAiTurn = false
                },
                onBackToGames = { navController.popBackStack() }
            )
        }
    }
}
