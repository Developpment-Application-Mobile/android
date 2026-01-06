package com.example.edukid_android.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edukid_android.R
import com.example.edukid_android.models.Child
import com.example.edukid_android.models.Quiz
import com.example.edukid_android.models.getBackgroundColor
import com.example.edukid_android.models.getProgressColor

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChildResultsScreen(
    child: Child,
    onBackClick: () -> Unit = {}
) {
    val completedQuizzes = remember(child) { child.getCompletedQuizzes() }
    
    // Filter states
    var selectedTopic by remember { mutableStateOf<String?>(null) }
    var sortBy by remember { mutableStateOf("date_newest") } // date_newest, date_oldest, points_high, points_low
    var showFilterMenu by remember { mutableStateOf(false) }
    
    // Get unique topics
    val topics = remember(completedQuizzes) {
        completedQuizzes.map { it.type.name }.distinct().sorted()
    }
    
    // Apply filters and sorting
    val filteredQuizzes = remember(completedQuizzes, selectedTopic, sortBy) {
        var filtered = completedQuizzes
        
        // Filter by topic
        if (selectedTopic != null) {
            filtered = filtered.filter { it.type.name == selectedTopic }
        }
        
        // Sort
        when (sortBy) {
            "date_newest" -> filtered.sortedByDescending { it.id } // Assuming higher ID = newer
            "date_oldest" -> filtered.sortedBy { it.id }
            "points_high" -> filtered.sortedByDescending { it.score ?: 0 }
            "points_low" -> filtered.sortedBy { it.score ?: 0 }
            else -> filtered
        }
    }
    
    val averageScore = remember(filteredQuizzes) {
        if (filteredQuizzes.isNotEmpty()) {
            filteredQuizzes.mapNotNull { it.score }.average().toInt()
        } else {
            0
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // Decorative elements
            DecorativeElementsResults()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header with back button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = CircleShape
                            )
                    ) {
                        Text(
                            text = "←",
                            fontSize = 24.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = "${child.name}'s Results",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.4.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Statistics Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.95f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                Color(0xFFAF7EE7),
                                                Color(0xFF7E57C2)
                                            )
                                        ),
                                        shape = RoundedCornerShape(16.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "📊",
                                    fontSize = 28.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = "Performance Overview",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E2E2E)
                                )
                                Text(
                                    text = "Overall statistics and progress",
                                    fontSize = 13.sp,
                                    color = Color(0xFF666666)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Divider(color = Color(0xFFE0E0E0))

                        Spacer(modifier = Modifier.height(20.dp))

                        // Stats Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatBox(
                                icon = "✅",
                                value = "${completedQuizzes.size}",
                                label = "Completed",
                                color = Color(0xFF4CAF50)
                            )

                            VerticalDivider(
                                modifier = Modifier.height(80.dp),
                                color = Color(0xFFE0E0E0),
                                thickness = 1.dp
                            )

                            StatBox(
                                icon = "⭐",
                                value = "$averageScore%",
                                label = "Avg Score",
                                color = Color(0xFFFFB300)
                            )

                            VerticalDivider(
                                modifier = Modifier.height(80.dp),
                                color = Color(0xFFE0E0E0),
                                thickness = 1.dp
                            )

                            StatBox(
                                icon = "🏆",
                                value = "${child.progressionLevel}",
                                label = "Level",
                                color = Color(0xFFAF7EE7)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Divider(color = Color(0xFFE0E0E0))

                        Spacer(modifier = Modifier.height(20.dp))

                        // Points Section
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Available Points",
                                    fontSize = 14.sp,
                                    color = Color(0xFF666666)
                                )
                                Text(
                                    text = "${child.Score}",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFAF7EE7)
                                )
                            }

                            Column(
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = "Lifetime Score",
                                    fontSize = 14.sp,
                                    color = Color(0xFF666666)
                                )
                                Text(
                                    text = "${child.lifetimeScore}",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF7E57C2)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Level Progress Bar
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Progress to Level ${child.progressionLevel + 1}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF666666)
                                )
                                Text(
                                    text = "${(child.lifetimeScore % 1000)} / 1000",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFAF7EE7)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(12.dp)
                                    .background(
                                        color = Color(0xFFE0E0E0),
                                        shape = RoundedCornerShape(6.dp)
                                    )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth((child.lifetimeScore % 1000) / 1000f)
                                        .fillMaxHeight()
                                        .background(
                                            brush = Brush.horizontalGradient(
                                                colors = listOf(
                                                    Color(0xFFAF7EE7),
                                                    Color(0xFF7E57C2)
                                                )
                                            ),
                                            shape = RoundedCornerShape(6.dp)
                                        )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Filter Section Header with Premium Design
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.15f),
                                    Color.White.copy(alpha = 0.05f)
                                )
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFFAF7EE7),
                                            Color(0xFF7E57C2)
                                        )
                                    ),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "📚",
                                fontSize = 20.sp
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Column {
                            Text(
                                text = "Completed Quizzes",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )
                            if (selectedTopic != null || sortBy != "date_newest") {
                                Text(
                                    text = "${filteredQuizzes.size} of ${completedQuizzes.size} shown",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                    
                    // Filter Toggle Button
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                brush = if (showFilterMenu) {
                                    Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFFAF7EE7),
                                            Color(0xFF7E57C2)
                                        )
                                    )
                                } else {
                                    Brush.radialGradient(
                                        colors = listOf(
                                            Color.White.copy(alpha = 0.2f),
                                            Color.White.copy(alpha = 0.1f)
                                        )
                                    )
                                },
                                shape = CircleShape
                            )
                            .border(
                                width = 2.dp,
                                color = if (showFilterMenu) Color.White.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.2f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = { showFilterMenu = !showFilterMenu }
                        ) {
                            Text(
                                text = if (showFilterMenu) "✕" else "⚙️",
                                fontSize = 18.sp,
                                color = Color.White
                            )
                        }
                    }
                }
                
                // Premium Filter Menu with Animation
                androidx.compose.animation.AnimatedVisibility(
                    visible = showFilterMenu,
                    enter = androidx.compose.animation.expandVertically() + androidx.compose.animation.fadeIn(),
                    exit = androidx.compose.animation.shrinkVertically() + androidx.compose.animation.fadeOut()
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White.copy(alpha = 0.98f)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                Color(0xFFF8F5FF),
                                                Color.White
                                            )
                                        )
                                    )
                                    .padding(24.dp)
                            ) {
                                // Topic Filter Section
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(
                                                color = Color(0xFFAF7EE7).copy(alpha = 0.15f),
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = "🏷️", fontSize = 16.sp)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "Filter by Topic",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E2E2E)
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                // Topic Chips with Premium Design
                                androidx.compose.foundation.layout.FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // All Topics chip
                                    PremiumFilterChip(
                                        text = "All Topics",
                                        selected = selectedTopic == null,
                                        onClick = { selectedTopic = null },
                                        icon = "📚"
                                    )
                                    
                                    topics.forEach { topic ->
                                        PremiumFilterChip(
                                            text = topic,
                                            selected = selectedTopic == topic,
                                            onClick = { selectedTopic = if (selectedTopic == topic) null else topic },
                                            icon = getQuizTypeEmoji(topic)
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(24.dp))
                                
                                // Decorative Divider
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(
                                            brush = Brush.horizontalGradient(
                                                colors = listOf(
                                                    Color.Transparent,
                                                    Color(0xFFAF7EE7).copy(alpha = 0.3f),
                                                    Color.Transparent
                                                )
                                            )
                                        )
                                )
                                
                                Spacer(modifier = Modifier.height(24.dp))
                                
                                // Sort By Section
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(
                                                color = Color(0xFF7E57C2).copy(alpha = 0.15f),
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = "🔄", fontSize = 16.sp)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "Sort By",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E2E2E)
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                // Sort Options with Premium Design
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    PremiumSortOption(
                                        icon = "📅",
                                        text = "Newest First",
                                        selected = sortBy == "date_newest",
                                        onClick = { sortBy = "date_newest" }
                                    )
                                    PremiumSortOption(
                                        icon = "📅",
                                        text = "Oldest First",
                                        selected = sortBy == "date_oldest",
                                        onClick = { sortBy = "date_oldest" }
                                    )
                                    PremiumSortOption(
                                        icon = "⬆️",
                                        text = "Highest Points",
                                        selected = sortBy == "points_high",
                                        onClick = { sortBy = "points_high" }
                                    )
                                    PremiumSortOption(
                                        icon = "⬇️",
                                        text = "Lowest Points",
                                        selected = sortBy == "points_low",
                                        onClick = { sortBy = "points_low" }
                                    )
                                }
                                
                                // Clear Filters Button
                                if (selectedTopic != null || sortBy != "date_newest") {
                                    Spacer(modifier = Modifier.height(20.dp))
                                    
                                    Button(
                                        onClick = {
                                            selectedTopic = null
                                            sortBy = "date_newest"
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFFF6B6B).copy(alpha = 0.1f),
                                            contentColor = Color(0xFFFF6B6B)
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(
                                            text = "✕ Clear All Filters",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (filteredQuizzes.isEmpty()) {
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
                                text = "📝",
                                fontSize = 48.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (completedQuizzes.isEmpty()) "No Completed Quizzes Yet" else "No Quizzes Match Filter",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E2E2E),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (completedQuizzes.isEmpty()) 
                                    "${child.name} hasn't completed any quizzes yet. Start a quiz to see results here!" 
                                else 
                                    "Try adjusting your filters to see more results.",
                                fontSize = 14.sp,
                                color = Color(0xFF666666),
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp
                            )
                        }
                    }
                } else {
                    filteredQuizzes.forEach { quiz ->
                        QuizResultCard(quiz = quiz)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun PremiumFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    icon: String
) {
    val scale = remember { Animatable(1f) }
    
    LaunchedEffect(selected) {
        if (selected) {
            scale.animateTo(
                targetValue = 1.05f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        } else {
            scale.animateTo(1f)
        }
    }
    
    Box(
        modifier = Modifier
            .scale(scale.value)
            .background(
                brush = if (selected) {
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFAF7EE7),
                            Color(0xFF7E57C2)
                        )
                    )
                } else {
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFF5F5F5),
                            Color(0xFFEEEEEE)
                        )
                    )
                },
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) Color.White.copy(alpha = 0.5f) else Color(0xFFE0E0E0),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = if (selected) Color.White else Color(0xFF2E2E2E)
            ),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.height(IntrinsicSize.Min)
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
                    text = text,
                    fontSize = 14.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                )
                if (selected) {
                    Text(
                        text = "✓",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun PremiumSortOption(
    icon: String,
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = if (selected) {
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFAF7EE7).copy(alpha = 0.2f),
                            Color(0xFF7E57C2).copy(alpha = 0.1f)
                        )
                    )
                } else {
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Transparent
                        )
                    )
                },
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) Color(0xFFAF7EE7).copy(alpha = 0.5f) else Color(0xFFE0E0E0),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            color = if (selected) Color(0xFFAF7EE7).copy(alpha = 0.2f) else Color(0xFFF5F5F5),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = icon,
                        fontSize = 18.sp
                    )
                }
                
                Text(
                    text = text,
                    fontSize = 15.sp,
                    color = if (selected) Color(0xFF2E2E2E) else Color(0xFF666666),
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                )
            }
            
            RadioButton(
                selected = selected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color(0xFFAF7EE7),
                    unselectedColor = Color(0xFFCCCCCC)
                )
            )
        }
    }
}

@Composable
fun StatBox(
    icon: String,
    value: String,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = color.copy(alpha = 0.15f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = icon,
                fontSize = 24.sp
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E2E2E)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF666666)
        )
    }
}

@Composable
fun QuizResultCard(quiz: Quiz) {
    val backgroundColor = quiz.type.getBackgroundColor()
    val progressColor = quiz.type.getProgressColor()
    
    val scale = remember { Animatable(0.95f) }
    
    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale.value),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            backgroundColor.copy(alpha = 0.3f),
                            Color.White
                        )
                    )
                )
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Quiz Type Icon
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        color = progressColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .border(
                        width = 2.dp,
                        color = progressColor.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getQuizTypeEmoji(quiz.type.name),
                    fontSize = 32.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Quiz Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = quiz.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E2E2E),
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${quiz.type.name} • ${quiz.questions.size} questions",
                    fontSize = 12.sp,
                    color = Color(0xFF666666)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Score Badge
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                progressColor.copy(alpha = 0.9f),
                                progressColor
                            )
                        ),
                        shape = CircleShape
                    )
                    .border(
                        width = 3.dp,
                        color = Color.White,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${quiz.score ?: 0}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "pts",
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}

@Composable
fun DecorativeElementsResults() {
    Image(
        painter = painterResource(id = R.drawable.education_book),
        contentDescription = "Education Book",
        modifier = Modifier
            .size(100.dp)
            .offset(x = (-30).dp, y = 10.dp)
            .blur(1.dp),
        contentScale = ContentScale.Fit
    )

    Image(
        painter = painterResource(id = R.drawable.coins),
        contentDescription = "Coins",
        modifier = Modifier
            .size(60.dp)
            .offset(x = 300.dp, y = 30.dp),
        contentScale = ContentScale.Fit
    )

    Image(
        painter = painterResource(id = R.drawable.coins),
        contentDescription = "Coins",
        modifier = Modifier
            .size(50.dp)
            .offset(x = 20.dp, y = 700.dp)
            .rotate(38.66f),
        contentScale = ContentScale.Fit
    )
}

fun getQuizTypeEmoji(type: String): String {
    return when (type.uppercase()) {
        "MATH" -> "🔢"
        "SCIENCE" -> "🔬"
        "HISTORY" -> "⏰"
        "GEOGRAPHY" -> "🌍"
        "LITERATURE" -> "📖"
        else -> "📚"
    }
}
