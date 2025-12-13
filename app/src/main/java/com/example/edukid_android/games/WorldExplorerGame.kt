package com.example.edukid_android.games

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.edukid_android.components.GameCompletionDialog
import com.example.edukid_android.components.GameHeader
import kotlinx.coroutines.delay

data class CountryQuestion(
    val country: String,
    val capital: String,
    val flag: String,
    val wrongCapitals: List<String>
)

@Composable
fun WorldExplorerGame(
    navController: NavController? = null
) {
    val countries = listOf(
        CountryQuestion("France", "Paris", "🇫🇷", listOf("London", "Berlin", "Rome")),
        CountryQuestion("USA", "Washington DC", "🇺🇸", listOf("New York", "Los Angeles", "Chicago")),
        CountryQuestion("Japan", "Tokyo", "🇯🇵", listOf("Osaka", "Kyoto", "Yokohama")),
        CountryQuestion("Brazil", "Brasília", "🇧🇷", listOf("Rio de Janeiro", "São Paulo", "Salvador")),
        CountryQuestion("Egypt", "Cairo", "🇪🇬", listOf("Alexandria", "Giza", "Luxor")),
        CountryQuestion("Australia", "Canberra", "🇦🇺", listOf("Sydney", "Melbourne", "Brisbane")),
        CountryQuestion("India", "New Delhi", "🇮🇳", listOf("Mumbai", "Bangalore", "Kolkata")),
        CountryQuestion("Canada", "Ottawa", "🇨🇦", listOf("Toronto", "Montreal", "Vancouver")),
        CountryQuestion("Germany", "Berlin", "🇩🇪", listOf("Munich", "Hamburg", "Frankfurt")),
        CountryQuestion("Italy", "Rome", "🇮🇹", listOf("Milan", "Venice", "Florence"))
    )

    var currentRound by remember { mutableStateOf(1) }
    var score by remember { mutableStateOf(0) }
    var currentCountry by remember { mutableStateOf(countries[0]) }
    var options by remember { mutableStateOf((listOf(currentCountry.capital) + currentCountry.wrongCapitals).shuffled()) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var showFeedback by remember { mutableStateOf(false) }
    var gameComplete by remember { mutableStateOf(false) }

    val totalRounds = 10

    LaunchedEffect(showFeedback) {
        if (showFeedback) {
            delay(2000)
            if (currentRound < totalRounds) {
                currentRound++
                currentCountry = countries[currentRound - 1]
                options = (listOf(currentCountry.capital) + currentCountry.wrongCapitals).shuffled()
                selectedAnswer = null
                showFeedback = false
            } else {
                gameComplete = true
            }
        }
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
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            GameHeader(
                title = "World Explorer",
                score = score,
                currentQuestion = currentRound,
                totalQuestions = totalRounds,
                onBackClick = { navController?.popBackStack() }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = currentCountry.flag,
                            fontSize = 80.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = currentCountry.country,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E2E2E)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "What is the capital?",
                            fontSize = 16.sp,
                            color = Color(0xFF666666)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    options.forEach { option ->
                        val isCorrect = option == currentCountry.capital
                        val isSelected = selectedAnswer == option

                        CapitalButton(
                            capital = option,
                            isSelected = isSelected,
                            showFeedback = showFeedback,
                            isCorrect = isCorrect,
                            onClick = {
                                if (!showFeedback) {
                                    selectedAnswer = option
                                    if (isCorrect) score++
                                    showFeedback = true
                                }
                            }
                        )
                    }
                }
            }
        }

        if (gameComplete) {
            GameCompletionDialog(
                score = score,
                totalQuestions = totalRounds,
                onPlayAgain = {
                    currentRound = 1
                    score = 0
                    currentCountry = countries[0]
                    options = (listOf(currentCountry.capital) + currentCountry.wrongCapitals).shuffled()
                    selectedAnswer = null
                    showFeedback = false
                    gameComplete = false
                },
                onBackToGames = { navController?.popBackStack() }
            )
        }
    }
}

@Composable
fun CapitalButton(
    capital: String,
    isSelected: Boolean,
    showFeedback: Boolean,
    isCorrect: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        showFeedback && isSelected && isCorrect -> Color(0xFF4CAF50)
        showFeedback && isSelected && !isCorrect -> Color(0xFFF44336)
        showFeedback && isCorrect -> Color(0xFF4CAF50)
        else -> Color.White
    }

    val textColor = when {
        showFeedback && (isCorrect || isSelected) -> Color.White
        else -> Color(0xFF2E2E2E)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !showFeedback, onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = capital,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = textColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
