package com.example.edukid_android.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.edukid_android.R
import com.example.edukid_android.models.*
import com.example.edukid_android.utils.ApiClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun QuizPlayScreen(
    quiz: Quiz?,
    navController: NavController? = null,
    onExit: (() -> Unit)? = null,
    onQuizSubmitted: (() -> Unit)? = null,
    parentId: String? = null,
    kidId: String? = null
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    // Responsive padding and sizing
    val horizontalPadding = (screenWidth * 0.05f).coerceIn(12.dp, 24.dp)
    val verticalPadding = (screenHeight * 0.02f).coerceIn(8.dp, 20.dp)

    var errorMessage by remember { mutableStateOf<String?>(null) }
    val questions = remember(quiz) { quiz?.questions ?: emptyList() }
    var currentIndex by remember { mutableStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var isAnswered by remember { mutableStateOf(false) }
    var totalScore by remember { mutableStateOf(quiz?.score ?: 0) }
    var correctCount by remember { mutableStateOf(0) }
    val perQuestionAnswer = remember(questions) { mutableStateListOf(*Array(questions.size) { null as Int? }) }
    val perQuestionCorrect = remember(questions) { mutableStateListOf(*Array(questions.size) { false }) }
    var showSummary by remember { mutableStateOf(false) }
    var showCorrectAnimation by remember { mutableStateOf(false) }
    var showWrongAnimation by remember { mutableStateOf(false) }
    var updateError by remember { mutableStateOf<String?>(null) }
    var showEncouragement by remember { mutableStateOf(false) }
    var encouragementMessage by remember { mutableStateOf("") }
    var streak by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()

    // Check if quiz is already completed (view-only mode)
    val isQuizCompleted = remember(quiz) {
        quiz?.getCompletionPercentage() == 100
    }
    val isViewOnly = remember { isQuizCompleted }

    LaunchedEffect(quiz) {
        if (quiz == null || questions.isEmpty()) {
            errorMessage = "Failed to load quiz data"
        } else {
            errorMessage = null
        }
    }

    LaunchedEffect(showCorrectAnimation) {
        if (showCorrectAnimation) {
            delay(1500)
            showCorrectAnimation = false
        }
    }

    LaunchedEffect(showWrongAnimation) {
        if (showWrongAnimation) {
            delay(1500)
            showWrongAnimation = false
        }
    }

    LaunchedEffect(showEncouragement) {
        if (showEncouragement) {
            delay(2000)
            showEncouragement = false
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
        DecorativeElementsQuizPlay()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = horizontalPadding, vertical = verticalPadding)
        ) {
            // Header
            Spacer(modifier = Modifier.height((screenHeight * 0.025f).coerceIn(12.dp, 40.dp)))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Exit button
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .shadow(4.dp, CircleShape)
                        .background(
                            color = Color.White.copy(alpha = 0.25f),
                            shape = CircleShape
                        )
                        .border(
                            width = 1.5.dp,
                            color = Color.White.copy(alpha = 0.4f),
                            shape = CircleShape
                        )
                        .clickable { onExit?.invoke() ?: navController?.popBackStack() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✕",
                        fontSize = 20.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Score badge
                Row(
                    modifier = Modifier
                        .shadow(6.dp, RoundedCornerShape(100.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFFFD700).copy(alpha = 0.3f),
                                    Color(0xFFFFA500).copy(alpha = 0.25f)
                                )
                            ),
                            shape = RoundedCornerShape(100.dp)
                        )
                        .border(
                            width = 2.dp,
                            color = Color(0xFFFFD700).copy(alpha = 0.5f),
                            shape = RoundedCornerShape(100.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "⭐", fontSize = 18.sp)
                    Text(
                        text = "$totalScore",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height((screenHeight * 0.02f).coerceIn(12.dp, 24.dp)))

            if (errorMessage != null) {
                // Error State
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "⚠️", fontSize = 40.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = errorMessage ?: "Something went wrong",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF2E2E2E),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { onExit?.invoke() ?: navController?.popBackStack() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFAF7EE7)
                            ),
                            shape = RoundedCornerShape(100.dp)
                        ) {
                            Text(
                                text = "GO BACK",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            } else {
                // Quiz Progress Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .shadow(4.dp, RoundedCornerShape(14.dp))
                                    .background(
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                quiz?.type?.getBackgroundColor()?.copy(alpha = 0.8f) ?: Color.Gray,
                                                quiz?.type?.getProgressColor() ?: Color.Gray
                                            )
                                        ),
                                        shape = RoundedCornerShape(14.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (quiz != null) {
                                    Image(
                                        painter = painterResource(id = quiz.type.getIconRes()),
                                        contentDescription = quiz.title,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = quiz?.title ?: "Quiz",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF2E2E2E),
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Q ${currentIndex + 1}/${questions.size}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF666666)
                                    )
                                    Text(text = "•", color = Color(0xFFCCCCCC), fontSize = 11.sp)
                                    Text(
                                        text = "$correctCount correct",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF4CAF50)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Progress bar
                        val animatedProgress by animateFloatAsState(
                            targetValue = (currentIndex + 1).toFloat() / questions.size.toFloat(),
                            animationSpec = tween(600, easing = FastOutSlowInEasing),
                            label = "progress"
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .background(
                                    color = Color(0xFFF0F0F0),
                                    shape = RoundedCornerShape(4.dp)
                                )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(animatedProgress)
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(
                                                quiz?.type?.getProgressColor()?.copy(alpha = 0.8f) ?: Color.Gray,
                                                quiz?.type?.getProgressColor() ?: Color.Gray
                                            )
                                        ),
                                        shape = RoundedCornerShape(4.dp)
                                    )
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "${((currentIndex + 1) * 100 / questions.size)}% Complete",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = quiz?.type?.getProgressColor() ?: Color.Gray
                        )
                    }
                }

                // View-Only Mode Banner
                if (isViewOnly) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF4CAF50).copy(alpha = 0.15f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(text = "✅", fontSize = 24.sp)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Quiz Completed!",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E2E2E)
                                )
                                Text(
                                    text = "You're viewing your results. Score: $totalScore points",
                                    fontSize = 13.sp,
                                    color = Color(0xFF666666)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Question Card with scrollable content
                AnimatedContent(
                    targetState = currentIndex,
                    transitionSpec = {
                        (slideInHorizontally(
                            initialOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(400, easing = FastOutSlowInEasing)
                        ) + fadeIn(animationSpec = tween(400))) togetherWith
                                (slideOutHorizontally(
                                    targetOffsetX = { fullWidth -> -fullWidth },
                                    animationSpec = tween(400, easing = FastOutSlowInEasing)
                                ) + fadeOut(animationSpec = tween(400)))
                    },
                    modifier = Modifier.weight(1f),
                    label = "question_animation"
                ) { idx ->
                    val question = questions[idx]

                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .shadow(10.dp, RoundedCornerShape(24.dp)),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp)
                        ) {
                            // Level badge
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .shadow(2.dp, RoundedCornerShape(8.dp))
                                        .background(
                                            color = when (question.level) {
                                                QuestionLevel.EASY -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                                                QuestionLevel.MEDIUM -> Color(0xFFFF9800).copy(alpha = 0.2f)
                                                QuestionLevel.HARD -> Color(0xFFFF5252).copy(alpha = 0.2f)
                                            },
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .border(
                                            width = 1.5.dp,
                                            color = when (question.level) {
                                                QuestionLevel.EASY -> Color(0xFF4CAF50).copy(alpha = 0.4f)
                                                QuestionLevel.MEDIUM -> Color(0xFFFF9800).copy(alpha = 0.4f)
                                                QuestionLevel.HARD -> Color(0xFFFF5252).copy(alpha = 0.4f)
                                            },
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = when (question.level) {
                                            QuestionLevel.EASY -> "⭐ Easy"
                                            QuestionLevel.MEDIUM -> "⭐⭐ Medium"
                                            QuestionLevel.HARD -> "⭐⭐⭐ Hard"
                                        },
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (question.level) {
                                            QuestionLevel.EASY -> Color(0xFF4CAF50)
                                            QuestionLevel.MEDIUM -> Color(0xFFFF9800)
                                            QuestionLevel.HARD -> Color(0xFFFF5252)
                                        }
                                    )
                                }

                                Text(
                                    text = "+${when (question.level) {
                                        QuestionLevel.EASY -> 5
                                        QuestionLevel.MEDIUM -> 10
                                        QuestionLevel.HARD -> 15
                                    }} pts",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF666666)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Question Text
                            Text(
                                text = question.questionText,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E2E2E),
                                lineHeight = 26.sp
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            // Options
                            Column(
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                question.options.forEachIndexed { optionIndex, optionText ->
                                    EnhancedOptionButton(
                                        text = optionText,
                                        index = optionIndex,
                                        isSelected = selectedOption == optionIndex,
                                        isAnswered = isAnswered || isViewOnly,
                                        isCorrect = optionIndex == question.correctAnswerIndex,
                                        quizColor = quiz?.type?.getProgressColor() ?: Color.Gray,
                                        onClick = {
                                            if (!isAnswered && !isViewOnly) {
                                                selectedOption = optionIndex
                                            }
                                        }
                                    )
                                }
                            }

                            // Explanation card
                            if (isAnswered && question.explanation != null) {
                                Spacer(modifier = Modifier.height(14.dp))

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .shadow(3.dp, RoundedCornerShape(14.dp)),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFFF8F9FA)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Text(text = "💡", fontSize = 18.sp)
                                        Text(
                                            text = question.explanation,
                                            fontSize = 13.sp,
                                            color = Color(0xFF555555),
                                            lineHeight = 18.sp,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // Action Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (currentIndex > 0) {
                                    OutlinedButton(
                                        onClick = {
                                            currentIndex -= 1
                                            selectedOption = perQuestionAnswer[currentIndex]
                                            isAnswered = perQuestionAnswer[currentIndex] != null || isViewOnly
                                        },
                                        shape = RoundedCornerShape(100.dp),
                                        border = androidx.compose.foundation.BorderStroke(
                                            2.dp,
                                            Color(0xFFE0E0E0)
                                        ),
                                        modifier = Modifier.height(46.dp)
                                    ) {
                                        Text(
                                            text = "← Prev",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF666666)
                                        )
                                    }
                                } else {
                                    Spacer(modifier = Modifier.width(1.dp))
                                }

                                if (isViewOnly) {
                                    // View-only mode: only show next/done buttons
                                    Button(
                                        onClick = {
                                            if (currentIndex < questions.lastIndex) {
                                                currentIndex += 1
                                                selectedOption = perQuestionAnswer[currentIndex]
                                                isAnswered = true
                                            } else {
                                                // Exit on last question in view-only mode
                                                onExit?.invoke() ?: navController?.popBackStack()
                                            }
                                        },
                                        shape = RoundedCornerShape(100.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF4CAF50)
                                        ),
                                        modifier = Modifier.height(46.dp)
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = if (currentIndex < questions.lastIndex) "Next" else "Done",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            Text(
                                                text = if (currentIndex < questions.lastIndex) "→" else "✓",
                                                fontSize = 15.sp,
                                                color = Color.White
                                            )
                                        }
                                    }
                                } else {
                                    // Normal quiz mode
                                    Button(
                                        onClick = {
                                            if (!isAnswered && selectedOption != null) {
                                                val correct = selectedOption == question.correctAnswerIndex
                                                perQuestionAnswer[currentIndex] = selectedOption
                                                perQuestionCorrect[currentIndex] = correct

                                                if (correct) {
                                                    val points = when (question.level) {
                                                        QuestionLevel.EASY -> 5
                                                        QuestionLevel.MEDIUM -> 10
                                                        QuestionLevel.HARD -> 15
                                                    }
                                                    totalScore += points
                                                    correctCount++
                                                    streak++
                                                    showCorrectAnimation = true

                                                    encouragementMessage = when {
                                                        streak >= 5 -> "🔥 On fire! $streak in a row!"
                                                        streak >= 3 -> "⭐ Amazing streak!"
                                                        question.level == QuestionLevel.HARD -> "🎯 Impressive!"
                                                        else -> listOf("🌟 Great!", "👏 Nice!", "✨ Perfect!").random()
                                                    }
                                                    showEncouragement = true
                                                } else {
                                                    streak = 0
                                                    showWrongAnimation = true

                                                    encouragementMessage = listOf(
                                                        "💪 Keep trying!",
                                                        "🌈 You're learning!",
                                                        "⭐ Nice effort!"
                                                    ).random()
                                                    showEncouragement = true
                                                }

                                                isAnswered = true

                                                // Submit quiz when all questions are answered
                                                if (currentIndex == questions.lastIndex && parentId != null && kidId != null && quiz?.id != null) {
                                                    android.util.Log.d("QuizPlayScreen", "Submitting quiz - currentIndex: $currentIndex, lastIndex: ${questions.lastIndex}, parentId: $parentId, kidId: $kidId, quizId: ${quiz.id}")
                                                    val answers = perQuestionAnswer.map { it ?: 0 }
                                                    android.util.Log.d("QuizPlayScreen", "Answers to submit: $answers")
                                                    scope.launch {
                                                        val submitResult = ApiClient.submitQuizAnswers(
                                                            parentId = parentId,
                                                            kidId = kidId,
                                                            quizId = quiz.id,
                                                            answers = answers
                                                        )
                                                        submitResult.fold(
                                                            onSuccess = {
                                                                android.util.Log.d("QuizPlayScreen", "Quiz submitted successfully!")
                                                                onQuizSubmitted?.invoke()
                                                                onExit?.invoke() ?: navController?.popBackStack()
                                                            },
                                                            onFailure = { e ->
                                                                android.util.Log.e("QuizPlayScreen", "Failed to submit quiz: ${e.message}", e)
                                                                updateError = e.message ?: "Failed to submit answers"
                                                            }
                                                        )
                                                    }
                                                } else {
                                                    android.util.Log.d("QuizPlayScreen", "NOT submitting - currentIndex: $currentIndex, lastIndex: ${questions.lastIndex}, parentId: $parentId, kidId: $kidId, quizId: ${quiz?.id}")
                                                }
                                            } else if (isAnswered) {
                                                if (currentIndex < questions.lastIndex) {
                                                    currentIndex += 1
                                                    selectedOption = perQuestionAnswer[currentIndex]
                                                    isAnswered = perQuestionAnswer[currentIndex] != null
                                                } else {
                                                    if (parentId != null && kidId != null && quiz?.id != null) {
                                                        android.util.Log.d("QuizPlayScreen", "Finish button - Submitting quiz")
                                                        val answers = perQuestionAnswer.map { it ?: 0 }
                                                        android.util.Log.d("QuizPlayScreen", "Finish button - Answers: $answers")
                                                        scope.launch {
                                                            val submitResult = ApiClient.submitQuizAnswers(
                                                                parentId = parentId,
                                                                kidId = kidId,
                                                                quizId = quiz.id!!,
                                                                answers = answers
                                                            )
                                                            submitResult.fold(
                                                                onSuccess = {
                                                                    android.util.Log.d("QuizPlayScreen", "Finish button - Quiz submitted successfully!")
                                                                    onQuizSubmitted?.invoke()
                                                                    onExit?.invoke() ?: navController?.popBackStack()
                                                                },
                                                                onFailure = { e ->
                                                                    android.util.Log.e("QuizPlayScreen", "Finish button - Failed to submit: ${e.message}", e)
                                                                    updateError = e.message ?: "Failed to submit answers"
                                                                }
                                                            )
                                                        }
                                                    } else {
                                                        android.util.Log.d("QuizPlayScreen", "Finish button - Missing params: parentId=$parentId, kidId=$kidId, quizId=${quiz?.id}")
                                                        showSummary = true
                                                    }
                                                }
                                            }
                                        },
                                        shape = RoundedCornerShape(100.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = quiz?.type?.getProgressColor() ?: Color.Gray,
                                            disabledContainerColor = Color(0xFFE0E0E0)
                                        ),
                                        enabled = !isAnswered || selectedOption != null,
                                        modifier = Modifier.height(46.dp)
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = when {
                                                    !isAnswered && selectedOption != null -> "Check"
                                                    currentIndex < questions.lastIndex -> "Next"
                                                    else -> "Finish"
                                                },
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            Text(
                                                text = if (!isAnswered && selectedOption != null) "✓" else "→",
                                                fontSize = 15.sp,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Overlays
        if (showCorrectAnimation) {
            EnhancedCelebrationOverlay()
        }

        if (showWrongAnimation) {
            EnhancedWrongAnswerOverlay()
        }

        if (showEncouragement) {
            EncouragementMessageOverlay(message = encouragementMessage)
        }

        if (showSummary) {
            QuizSummaryDialog(
                totalScore = totalScore,
                correctCount = correctCount,
                totalQuestions = questions.size,
                quizColor = quiz?.type?.getProgressColor() ?: Color.Gray,
                onDismiss = {
                    showSummary = false
                    onExit?.invoke() ?: navController?.popBackStack()
                }
            )
        }

        updateError?.let { err ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                LaunchedEffect(err) {
                    delay(4000)
                    updateError = null
                }
                Snackbar(
                    action = {
                        TextButton(onClick = { updateError = null }) {
                            Text("Dismiss", color = Color.White, fontSize = 12.sp)
                        }
                    },
                    containerColor = Color(0xFFD32F2F),
                    contentColor = Color.White
                ) {
                    Text(text = err, color = Color.White, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun EnhancedOptionButton(
    text: String,
    index: Int,
    isSelected: Boolean,
    isAnswered: Boolean,
    isCorrect: Boolean,
    quizColor: Color,
    onClick: () -> Unit
) {
    val letters = listOf("A", "B", "C", "D")

    // Unified state for cleaner logic
    val state = when {
        isAnswered && isCorrect && isSelected -> "correct_selected"
        isAnswered && !isCorrect && isSelected -> "wrong_selected"
        isAnswered && isCorrect -> "correct_other"
        isSelected -> "selected"
        else -> "default"
    }

    // --- COLORS (minimal, consistent, clean) ---
    val bg by animateColorAsState(
        targetValue = when (state) {
            "correct_selected" -> Color(0xFF4CAF50)
            "wrong_selected" -> Color(0xFFE53935)
            "correct_other" -> Color(0xFF4CAF50).copy(alpha = 0.12f)
            "selected" -> quizColor.copy(alpha = 0.15f)
            else -> Color(0xFFF6F6F6)
        },
        label = ""
    )

    val border by animateColorAsState(
        targetValue = when (state) {
            "correct_selected" -> Color(0xFF4CAF50)
            "wrong_selected" -> Color(0xFFE53935)
            "correct_other" -> Color(0xFF4CAF50).copy(alpha = 0.40f)
            "selected" -> quizColor
            else -> Color(0xFFE0E0E0)
        },
        label = ""
    )

    val textColor = when (state) {
        "correct_selected", "wrong_selected" -> Color.White
        else -> Color(0xFF1F1F1F)
    }

    val scale by animateFloatAsState(
        targetValue = if (isSelected && !isAnswered) 1.015f else 1f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 180f),
        label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .border(1.5.dp, border, RoundedCornerShape(14.dp))
            .background(bg, RoundedCornerShape(14.dp))
            .clickable(enabled = !isAnswered, onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // --- Letter Circle (clean + subtle) ---
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        color = if (isSelected || isAnswered) border.copy(alpha = 0.18f)
                        else Color.White,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = letters[index],
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = textColor
                )
            }

            // --- MAIN TEXT ---
            Text(
                text = text,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = textColor,
                modifier = Modifier.weight(1f)
            )

            // --- Correct / Wrong indicators (minimalistic) ---
            if (isAnswered) {
                val symbol = if (isCorrect) "✓" else "✕"
                val symbolColor = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFE53935)

                Text(
                    text = symbol,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = symbolColor
                )
            }
        }
    }
}




@Composable
fun QuizSummaryDialog(
    totalScore: Int,
    correctCount: Int,
    totalQuestions: Int,
    quizColor: Color,
    onDismiss: () -> Unit
) {
    val percentage = (correctCount * 100 / totalQuestions)

    val (emoji, title, message) = when {
        percentage == 100 -> Triple("🏆", "Perfect Score!", "You're a champion!")
        percentage >= 90 -> Triple("🌟", "Excellent!", "Outstanding work!")
        percentage >= 70 -> Triple("🎉", "Great Job!", "Well done!")
        percentage >= 50 -> Triple("😊", "Good Effort!", "Keep it up!")
        else -> Triple("📚", "Keep Practicing!", "You're learning!")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(24.dp),
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = emoji, fontSize = 64.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E2E2E),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = message,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF666666),
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Score card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(6.dp, RoundedCornerShape(18.dp)),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        quizColor.copy(alpha = 0.15f),
                                        quizColor.copy(alpha = 0.05f)
                                    )
                                )
                            )
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Your Score",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF666666)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = "⭐", fontSize = 28.sp)
                                Text(
                                    text = "$totalScore",
                                    fontSize = 40.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = quizColor
                                )
                            }
                        }
                    }
                }

                // Stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(
                        label = "Correct",
                        value = "$correctCount/$totalQuestions",
                        icon = "✅"
                    )
                    StatItem(
                        label = "Accuracy",
                        value = "$percentage%",
                        icon = "🎯"
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .shadow(4.dp, RoundedCornerShape(100.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = quizColor
                ),
                shape = RoundedCornerShape(100.dp)
            ) {
                Text(
                    text = "AWESOME! 🎉",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    )
}

@Composable
fun StatItem(label: String, value: String, icon: String?) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (icon != null) {
            Text(text = icon, fontSize = 22.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E2E2E)
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color(0xFF666666)
        )
    }
}

@Composable
fun BoxScope.DecorativeElementsQuizPlay() {
    Image(
        painter = painterResource(id = R.drawable.education_book),
        contentDescription = null,
        modifier = Modifier
            .size(80.dp)
            .offset(x = (-20).dp, y = 10.dp)
            .blur(1.dp),
        contentScale = ContentScale.Fit
    )
    Image(
        painter = painterResource(id = R.drawable.coins),
        contentDescription = null,
        modifier = Modifier
            .size(50.dp)
            .align(Alignment.TopEnd)
            .offset(x = (-10).dp, y = 30.dp),
        contentScale = ContentScale.Fit
    )
    Image(
        painter = painterResource(id = R.drawable.book_stacks),
        contentDescription = null,
        modifier = Modifier
            .size(70.dp)
            .align(Alignment.BottomEnd)
            .offset(x = (-20).dp, y = (-20).dp)
            .blur(2.dp),
        contentScale = ContentScale.Fit
    )
}

@Composable
fun EnhancedCelebrationOverlay() {
    val infiniteTransition = rememberInfiniteTransition(label = "celebration")

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4CAF50).copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "🎉",
                fontSize = 80.sp,
                modifier = Modifier
                    .scale(scale)
                    .rotate(rotation)
            )
            Card(
                shape = RoundedCornerShape(100.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                modifier = Modifier.shadow(6.dp, RoundedCornerShape(100.dp))
            ) {
                Text(
                    text = "Correct!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 10.dp)
                )
            }
        }
    }
}

@Composable
fun EnhancedWrongAnswerOverlay() {
    val shake by rememberInfiniteTransition(label = "shake").animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(100),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shake_offset"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFF5252).copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "😔",
                fontSize = 80.sp,
                modifier = Modifier.offset(x = shake.dp)
            )
            Card(
                shape = RoundedCornerShape(100.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                modifier = Modifier.shadow(6.dp, RoundedCornerShape(100.dp))
            ) {
                Text(
                    text = "Not quite!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF5252),
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 10.dp)
                )
            }
        }
    }
}

@Composable
fun EncouragementMessageOverlay(message: String) {
    val scale by rememberInfiniteTransition(label = "encouragement").animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Card(
            modifier = Modifier
                .padding(top = 100.dp)
                .shadow(8.dp, RoundedCornerShape(18.dp))
                .scale(scale),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = message,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E2E2E),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}