package com.example.edukid_android.games

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import kotlin.random.Random

@Composable
fun MathDashGame(navController: NavController) {
    var score by remember { mutableStateOf(0) }
    var timeLeft by remember { mutableStateOf(60) }
    var n1 by remember { mutableStateOf(Random.nextInt(1, 10)) }
    var n2 by remember { mutableStateOf(Random.nextInt(1, 10)) }
    var isAddition by remember { mutableStateOf(Random.nextBoolean()) }
    var userAnswer by remember { mutableStateOf("") }
    var gameComplete by remember { mutableStateOf(false) }

    LaunchedEffect(timeLeft, gameComplete) {
        if (timeLeft > 0 && !gameComplete) {
            delay(1000)
            timeLeft--
            if (timeLeft == 0) gameComplete = true
        }
    }

    fun checkAnswer() {
        val correctAnswer = if (isAddition) n1 + n2 else n1 - n2
        if (userAnswer.toIntOrNull() == correctAnswer) {
            score += 10 + (timeLeft / 5) // Bonus for speed
            // New Question
            n1 = Random.nextInt(1, 20)
            n2 = Random.nextInt(1, 20)
            if (!isAddition && n1 < n2) {
                 val temp = n1; n1 = n2; n2 = temp
            }
            isAddition = Random.nextBoolean()
            userAnswer = ""
        } else {
             // Shake effect or feedback? For now, just clear
             userAnswer = ""
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF009688), Color(0xFF80CBC4))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GameHeader(title = "Math Dash", score = score, timeLeft = timeLeft, onBackClick = { navController.popBackStack() })

            Spacer(modifier = Modifier.height(48.dp))

            // Question Box
            Card(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth().height(150.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    val operator = if (isAddition) "+" else "-"
                    // Ensure non-negative for subtraction
                    if (!isAddition && n1 < n2) {
                         // Swap visually for simplicity if logic missed it
                         Text("$n2 $operator $n1 = ?", fontSize = 48.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Text("$n1 $operator $n2 = ?", fontSize = 48.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Answer Field
            Text(
                text = userAnswer.ifEmpty { "..." },
                fontSize = 40.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                 modifier = Modifier
                    .background(Color.White.copy(alpha=0.2f), RoundedCornerShape(8.dp))
                    .padding(16.dp)
                    .width(150.dp),
                 textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            // Numpad
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val rows = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("C", "0", "GO")
                )
                
                rows.forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        row.forEach { key ->
                            Button(
                                onClick = { 
                                    when(key) {
                                        "C" -> userAnswer = ""
                                        "GO" -> checkAnswer()
                                        else -> if (userAnswer.length < 3) userAnswer += key
                                    }
                                },
                                modifier = Modifier.size(80.dp),
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (key == "GO") Color(0xFFFFC107) else Color.White
                                )
                            ) {
                                Text(key, fontSize = 24.sp, color = Color.Black)
                            }
                        }
                    }
                }
            }
        }
        
        if (gameComplete) {
            GameCompletionDialog(
                score = score,
                totalQuestions = 1,
                onPlayAgain = {
                    score = 0
                    timeLeft = 60
                    userAnswer = ""
                    gameComplete = false
                },
                onBackToGames = { navController.popBackStack() }
            )
        }
    }
}
