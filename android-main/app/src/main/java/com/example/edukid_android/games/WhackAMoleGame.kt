package com.example.edukid_android.games

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.edukid_android.components.GameCompletionDialog
import com.example.edukid_android.components.GameHeader
import kotlinx.coroutines.delay

@Composable
fun WhackAMoleGame(navController: NavController) {
    var score by remember { mutableStateOf(0) }
    var timeLeft by remember { mutableStateOf(30) }
    var activeHole by remember { mutableStateOf(-1) }
    var gameComplete by remember { mutableStateOf(false) }

    LaunchedEffect(timeLeft, gameComplete) {
        if (timeLeft > 0 && !gameComplete) {
            delay(1000)
            timeLeft--
            if (timeLeft == 0) gameComplete = true
        }
    }

    LaunchedEffect(activeHole, gameComplete) {
        if (!gameComplete) {
            // Keep switching holes faster as time goes on? or constant
            val speed = 800L
            delay(speed)
            activeHole = (0..8).random()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF795548)) // Dirt color
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            GameHeader(title = "Whack-a-Mole", score = score, timeLeft = timeLeft, onBackClick = { navController.popBackStack() })

            Spacer(modifier = Modifier.height(32.dp))

            // Grid of Holes
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (r in 0..2) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        for (c in 0..2) {
                            val index = r * 3 + c
                            MoleHole(
                                isActive = activeHole == index,
                                onWhack = {
                                    if (activeHole == index && !gameComplete) {
                                        score += 10
                                        activeHole = -1 // Hide immediately
                                    }
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
        
        if (gameComplete) {
            GameCompletionDialog(
                score = score,
                totalQuestions = 1,
                onPlayAgain = {
                    score = 0
                    timeLeft = 30
                    gameComplete = false
                },
                onBackToGames = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun MoleHole(isActive: Boolean, onWhack: () -> Unit) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(CircleShape)
            .background(Color(0xFF3E2723)) // Dark hole
            .clickable(enabled = isActive) { onWhack() },
        contentAlignment = Alignment.Center
    ) {
        if (isActive) {
             // Mole
             Box(
                 modifier = Modifier
                     .size(80.dp)
                     .clip(CircleShape)
                     .background(Color(0xFFFFB74D))
             ) {
                 // Eyes
                 Row(
                     modifier = Modifier.align(Alignment.TopCenter).padding(top=20.dp),
                     horizontalArrangement = Arrangement.spacedBy(10.dp)
                 ) {
                     Box(modifier = Modifier.size(10.dp).background(Color.Black, CircleShape))
                     Box(modifier = Modifier.size(10.dp).background(Color.Black, CircleShape))
                 }
             }
        }
    }
}
