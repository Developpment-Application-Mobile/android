package com.example.edukid_android.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.edukid_android.R
import com.example.edukid_android.components.BottomNavigationBar
import com.example.edukid_android.models.Child
import com.example.edukid_android.models.Quiz
import com.example.edukid_android.models.QuizType
import com.example.edukid_android.models.getBackgroundColor
import com.example.edukid_android.models.getIconRes
import com.example.edukid_android.models.getProgressColor
import com.example.edukid_android.utils.getAvatarResource


@Composable
fun ImprovedChildHomeScreen(
    navController: NavController? = null,
    child: Child? = null,
    onQuizClick: (Quiz) -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf<QuizType?>(null) }

    val childState by remember(child) { mutableStateOf(child) }

    val allQuizzes = remember(childState) {
        childState?.quizzes ?: emptyList()
    }
    
    val filteredQuizzes = remember(selectedFilter, allQuizzes) {
        val quizzes = if (selectedFilter == null) {
            allQuizzes
        } else {
            allQuizzes.filter { it.type == selectedFilter }
        }
        quizzes.filter { !it.isAnswered }
    }

    val inProgressQuizzes = remember(filteredQuizzes) {
        filteredQuizzes.filter { it.getCompletionPercentage() > 0 && it.getCompletionPercentage() < 100 }
    }

    val notStartedQuizzes = remember(filteredQuizzes) {
        filteredQuizzes.filter { it.getCompletionPercentage() == 0 }
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
        // Decorative elements
        DecorativeElementsChildHome()
        
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Main content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(20.dp)
            ) {
                // Header
                Spacer(modifier = Modifier.height(40.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Hello,",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Text(
                            text = childState?.name?.takeIf { it.isNotBlank() } ?: "Kid Explorer!",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 0.4.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            childState?.age?.let {
                                InfoChip(icon = "🎂", text = "$it years old")
                            }
                            childState?.level?.takeIf { it.isNotBlank() }?.let {
                                InfoChip(icon = "📊", text = "Level $it")
                            }
                        }
                    }

                    val context = LocalContext.current
                    val avatarResId = remember(childState?.avatarEmoji) {
                        val name = childState?.avatarEmoji ?: "avatar_3"
                        val res = getAvatarResource(context, name)
                        if (res != 0) res else getAvatarResource(context, "avatar_3")
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .background(
                                    color = Color.White.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(100.dp)
                                )
                                .border(
                                    width = 2.dp,
                                    color = Color.White.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(100.dp)
                                )
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "⭐", fontSize = 20.sp)
                            Text(
                                text = "${childState?.Score ?: 0}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color.White.copy(alpha = 0.2f), CircleShape)
                                .border(2.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (avatarResId != 0) {
                                Image(
                                    painter = painterResource(id = avatarResId),
                                    contentDescription = childState?.name,
                                    modifier = Modifier.size(36.dp),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.avatar_3),
                                    contentDescription = childState?.name,
                                    modifier = Modifier.size(36.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Category Filter
                Text(
                    text = "Categories",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // All categories
                    item {
                        CategoryFilterChip(
                            label = "All",
                            icon = "🎯",
                            isSelected = selectedFilter == null,
                            onClick = { selectedFilter = null }
                        )
                    }
                    
                    // Individual categories
                    item {
                        CategoryFilterChip(
                            label = "Math",
                            icon = "🔢",
                            isSelected = selectedFilter == QuizType.MATH,
                            onClick = { selectedFilter = QuizType.MATH }
                        )
                    }
                    item {
                        CategoryFilterChip(
                            label = "Science",
                            icon = "🔬",
                            isSelected = selectedFilter == QuizType.SCIENCE,
                            onClick = { selectedFilter = QuizType.SCIENCE }
                        )
                    }
                    item {
                        CategoryFilterChip(
                            label = "History",
                            icon = "⏰",
                            isSelected = selectedFilter == QuizType.HISTORY,
                            onClick = { selectedFilter = QuizType.HISTORY }
                        )
                    }
                    item {
                        CategoryFilterChip(
                            label = "Geography",
                            icon = "🌍",
                            isSelected = selectedFilter == QuizType.GEOGRAPHY,
                            onClick = { selectedFilter = QuizType.GEOGRAPHY }
                        )
                    }
                    item {
                        CategoryFilterChip(
                            label = "Literature",
                            icon = "📖",
                            isSelected = selectedFilter == QuizType.LITERATURE,
                            onClick = { selectedFilter = QuizType.LITERATURE }
                        )
                    }
                    item {
                        CategoryFilterChip(
                            label = "General",
                            icon = "🎓",
                            isSelected = selectedFilter == QuizType.GENERAL,
                            onClick = { selectedFilter = QuizType.GENERAL }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Quizzes List
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    // In Progress Section
                    if (inProgressQuizzes.isNotEmpty()) {
                        item {
                            Text(
                                text = "Continue Playing 🎮",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        
                        items(inProgressQuizzes) { quiz ->
                            PremiumQuizCard(
                                quiz = quiz,
                                onClick = { onQuizClick(quiz) }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    // Available Quizzes Section
                    if (notStartedQuizzes.isNotEmpty()) {
                        item {
                            Text(
                                text = "Start New Adventure 🚀",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        items(notStartedQuizzes) { quiz ->
                            PremiumQuizCard(
                                quiz = quiz,
                                onClick = { onQuizClick(quiz) }
                            )
                        }
                    }
                    
                    // Empty state
                    if (filteredQuizzes.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White.copy(alpha = 0.95f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(40.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "🎯",
                                        fontSize = 48.sp
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = if (childState == null) "No child data available" else "No quizzes in this category",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF2E2E2E),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = if (childState == null) "Check your connection or scan QR again" else "Try selecting a different category!",
                                        fontSize = 14.sp,
                                        color = Color(0xFF666666),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Navigation
            BottomNavigationBar(
                currentRoute = "childHome",
                onNavigate = { route ->
                    navController?.navigate(route) {
                        popUpTo("childHome") { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

@Composable
fun CategoryFilterChip(
    label: String,
    icon: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(100.dp)
            )
            .border(
                width = if (isSelected) 0.dp else 1.dp,
                color = Color.White.copy(alpha = 0.3f),
                shape = RoundedCornerShape(100.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = icon,
                fontSize = 16.sp
            )
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color(0xFF2E2E2E) else Color.White
            )
        }
    }
}
@Composable
fun PremiumQuizCard(
    quiz: Quiz,
    onClick: () -> Unit
) {
    val progress = quiz.getCompletionPercentage()
    val isInProgress = progress in 1..99

    // Smooth animation for progress value
    val animatedProgress by animateFloatAsState(
        targetValue = progress / 100f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.85f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            quiz.type.getBackgroundColor().copy(alpha = 0.12f),
                            Color.White.copy(alpha = 0.9f)
                        )
                    )
                )
                .padding(18.dp)
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {

                // Icon with badge and shadow
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .shadow(6.dp, RoundedCornerShape(20.dp))
                        .background(
                            brush = Brush.linearGradient(
                                listOf(
                                    quiz.type.getBackgroundColor().copy(alpha = 0.25f),
                                    quiz.type.getBackgroundColor().copy(alpha = 0.45f)
                                )
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = quiz.type.getIconRes()),
                        contentDescription = quiz.title,
                        modifier = Modifier.size(42.dp)
                    )
                }

                Spacer(modifier = Modifier.width(18.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = quiz.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E1E1E)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "${quiz.questions.size} Questions • ${quiz.type.toString().uppercase()}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF6A6A6A)
                    )

                    if (isInProgress) {
                        Spacer(modifier = Modifier.height(12.dp))

                        // Premium progress bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .background(
                                    color = Color(0xFFEAEAEA),
                                    shape = RoundedCornerShape(4.dp)
                                )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(animatedProgress)
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            listOf(
                                                quiz.type.getProgressColor(),
                                                quiz.type.getProgressColor().copy(alpha = 0.5f)
                                            )
                                        ),
                                        shape = RoundedCornerShape(4.dp)
                                    )
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "$progress% Completed",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = quiz.type.getProgressColor()
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Animated CTA button
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .shadow(8.dp, CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                listOf(
                                    quiz.type.getProgressColor(),
                                    quiz.type.getProgressColor().copy(alpha = 0.75f)
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isInProgress) "▶" else "🚀",
                        fontSize = if (isInProgress) 20.sp else 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}


@Composable
fun BoxScope.DecorativeElementsChildHome() {
    Image(
        painter = painterResource(id = R.drawable.education_book),
        contentDescription = "Education Book",
        modifier = Modifier
            .size(100.dp)
            .offset(x = (-30).dp, y = 20.dp)
            .blur(1.dp),
        contentScale = ContentScale.Fit
    )

    Image(
        painter = painterResource(id = R.drawable.coins),
        contentDescription = "Coins",
        modifier = Modifier
            .size(70.dp)
            .offset(x = 290.dp, y = 40.dp),
        contentScale = ContentScale.Fit
    )

    Image(
        painter = painterResource(id = R.drawable.book_stacks),
        contentDescription = "Book Stacks",
        modifier = Modifier
            .size(90.dp)
            .offset(x = 260.dp, y = 700.dp)
            .blur(2.dp),
        contentScale = ContentScale.Fit
    )

    Image(
        painter = painterResource(id = R.drawable.coins),
        contentDescription = "Coins",
        modifier = Modifier
            .size(50.dp)
            .offset(x = 20.dp, y = 680.dp)
            .rotate(38.66f),
        contentScale = ContentScale.Fit
    )
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
fun ImprovedChildHomeScreenPreview() {
    val sampleChild = Child(
        id = "1",
        name = "Emma",
        age = 8,
        level = "3",
        avatarEmoji = "👧",
        Score = 250
    )
    
    ImprovedChildHomeScreen(child = sampleChild)
}