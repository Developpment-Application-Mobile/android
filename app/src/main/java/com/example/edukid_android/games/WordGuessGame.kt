package com.example.edukid_android.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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

enum class LetterState {
    EMPTY, CORRECT, PRESENT, ABSENT
}

data class Letter(val char: Char, val state: LetterState = LetterState.EMPTY)

@Composable
fun WordGuessGame(navController: NavController) {
    // Kid friendly 5-letter words
    val wordList = remember {
        listOf(
            "APPLE", "BEACH", "BRAIN", "BREAD", "BRUSH", "CHAIR", "CHEST", "CHORD",
            "CLICK", "CLOCK", "CLOUD", "DANCE", "DIARY", "DRINK", "DRIVE", "EARTH",
            "FEAST", "FIELD", "FRUIT", "GLASS", "GRAPE", "GREEN", "GHOST", "HEART",
            "HOUSE", "JUICE", "LIGHT", "LEMON", "MELON", "MONEY", "MUSIC", "NIGHT",
            "OCEAN", "PARTY", "PHONE", "PIZZA", "PLANE", "PLANT", "PLATE", "POWER",
            "RADIO", "RIVER", "ROBOT", "SHIRT", "SHOES", "SMILE", "SNAKE", "SPACE",
            "SPOON", "STORM", "SUGAR", "SWEET", "TABLE", "TIGER", "TOAST", "TOUCH",
            "TRAIN", "TRUCK", "WATER", "WATCH", "WHALE", "WORLD", "WRITE", "ZEBRA"
        )
    }

    var targetWord by remember { mutableStateOf(wordList.random()) }
    var grid by remember { mutableStateOf(List(6) { List(5) { Letter(' ') } }) }
    var currentRow by remember { mutableStateOf(0) }
    var currentCol by remember { mutableStateOf(0) }
    var gameComplete by remember { mutableStateOf(false) }
    var won by remember { mutableStateOf(false) }
    var showInvalidWord by remember { mutableStateOf(false) }

    fun checkWord() {
        if (currentCol != 5) return

        val guess = grid[currentRow].map { it.char }.joinToString("")
        // Simple check: is it in our specific list? Or just allow any? 
        // For kids app, maybe lax validation or use a larger dictionary?
        // Let's assume valid for now if 5 letters.
        
        val newRow = grid[currentRow].mapIndexed { index, letter ->
            val char = letter.char
            val state = when {
                char == targetWord[index] -> LetterState.CORRECT
                targetWord.contains(char) -> LetterState.PRESENT
                else -> LetterState.ABSENT
            }
            letter.copy(state = state)
        }
        
        val newGrid = grid.toMutableList()
        newGrid[currentRow] = newRow
        grid = newGrid

        if (guess == targetWord) {
            won = true
            gameComplete = true
        } else if (currentRow == 5) {
            gameComplete = true
        } else {
            currentRow++
            currentCol = 0
        }
    }

    fun onKeyClick(char: Char) {
        if (gameComplete) return
        if (currentCol < 5) {
            val newGrid = grid.toMutableList()
            val newRow = newGrid[currentRow].toMutableList()
            newRow[currentCol] = Letter(char)
            newGrid[currentRow] = newRow
            grid = newGrid
            currentCol++
        }
    }

    fun onBackspace() {
        if (gameComplete) return
        if (currentCol > 0) {
            currentCol--
            val newGrid = grid.toMutableList()
            val newRow = newGrid[currentRow].toMutableList()
            newRow[currentCol] = Letter(' ')
            newGrid[currentRow] = newRow
            grid = newGrid
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF673AB7), Color(0xFF9575CD))
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GameHeader(title = "Word Guess", score = 0, onBackClick = { navController.popBackStack() })

            Spacer(modifier = Modifier.height(16.dp))

            // Grid
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = 32.dp)
            ) {
                grid.forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        row.forEach { letter ->
                            WordleCell(letter)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Keyboard
            WordleKeyboard(
                onKeyClick = ::onKeyClick,
                onBackspace = ::onBackspace,
                onEnter = ::checkWord
            )
        }
        
        if (gameComplete) {
            GameCompletionDialog(
                score = if (won) 100 - (currentRow * 10) else 0,
                totalQuestions = 1,
                onPlayAgain = {
                    targetWord = wordList.random()
                    grid = List(6) { List(5) { Letter(' ') } }
                    currentRow = 0
                    currentCol = 0
                    gameComplete = false
                    won = false
                },
                onBackToGames = { navController.popBackStack() },
                customMessage = if (won) "You guessed it!" else "The word was $targetWord"
            )
        }
    }
}

@Composable
fun RowScope.WordleCell(letter: Letter) {
    val backgroundColor = when (letter.state) {
        LetterState.CORRECT -> Color(0xFF4CAF50)
        LetterState.PRESENT -> Color(0xFFFFC107)
        LetterState.ABSENT -> Color(0xFF757575)
        LetterState.EMPTY -> Color.Transparent
    }
    
    val borderColor = if (letter.state == LetterState.EMPTY && letter.char == ' ') {
        Color.White.copy(alpha = 0.5f)
    } else {
        Color.Transparent
    }

    Box(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
            .background(backgroundColor, RoundedCornerShape(4.dp))
            .border(2.dp, borderColor, RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = letter.char.toString(),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun WordleKeyboard(
    onKeyClick: (Char) -> Unit,
    onBackspace: () -> Unit,
    onEnter: () -> Unit
) {
    val keys = listOf(
        "QWERTYUIOP",
        "ASDFGHJKL",
        "ZXCVBNM"
    )
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.2f))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        keys.forEachIndexed { index, rowKeys ->
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                if (index == 2) {
                     // Enter Key
                    Button(
                        onClick = onEnter,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                         contentPadding = PaddingValues(0.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .width(60.dp)
                    ) {
                        Text("ENT", fontSize = 12.sp)
                    }
                }
                
                rowKeys.forEach { char ->
                    Button(
                        onClick = { onKeyClick(char) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .width(32.dp)
                    ) {
                        Text(char.toString(), color = Color.Black, fontSize = 16.sp)
                    }
                }
                
                if (index == 2) {
                    // Backspace Key
                    Button(
                        onClick = onBackspace,
                         colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)),
                         contentPadding = PaddingValues(0.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .width(60.dp)
                    ) {
                        Text("DEL", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
