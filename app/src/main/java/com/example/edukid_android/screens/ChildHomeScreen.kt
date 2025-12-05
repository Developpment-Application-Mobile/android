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
import com.example.edukid_android.models.ShopItem
import com.example.edukid_android.models.getBackgroundColor
import com.example.edukid_android.models.getIconRes
import com.example.edukid_android.models.getProgressColor
import com.example.edukid_android.utils.ApiClient
import com.example.edukid_android.utils.getAvatarResource
import kotlinx.coroutines.launch


@Composable
fun ImprovedChildHomeScreen(
    navController: NavController? = null,
    child: Child? = null,
    onQuizClick: (Quiz) -> Unit = {},
    onChildUpdate: ((Child) -> Unit)? = null
) {
    var selectedFilter by remember { mutableStateOf<QuizType?>(null) }

    var childState by remember { mutableStateOf(child) }
    
    // Update local state when child prop changes
    LaunchedEffect(child) {
        childState = child
    }
    
    // Gifts state
    var gifts by remember { mutableStateOf<List<ShopItem>>(emptyList()) }
    var isLoadingGifts by remember { mutableStateOf(false) }
    var giftError by remember { mutableStateOf<String?>(null) }
    var giftSuccess by remember { mutableStateOf<String?>(null) }
    var isBuyingGift by remember { mutableStateOf<String?>(null) } // giftId being purchased
    val scope = rememberCoroutineScope()
    
    // Load gifts on screen start
    LaunchedEffect(childState?.parentId, childState?.id) {
        if (childState?.parentId != null && childState?.id != null) {
            isLoadingGifts = true
            val result = ApiClient.getGifts(childState!!.parentId!!, childState!!.id!!)
            result.onSuccess { giftList ->
                gifts = giftList
                isLoadingGifts = false
            }.onFailure { e ->
                giftError = e.message ?: "Failed to load gifts"
                isLoadingGifts = false
            }
        }
    }

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
                // Premium View Quests Button
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                        .clickable { navController?.navigate("questsScreen") },
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFFFFA726),
                                        Color(0xFFFF7043)
                                    )
                                )
                            )
                            .padding(horizontal = 20.dp, vertical = 16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = "Daily Quests",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Earn stars & rewards!",
                                    fontSize = 14.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                            Text(
                                text = "🚀",
                                fontSize = 32.sp
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.weight(1f)
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
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Level Progress Section
                        val currentScore = childState?.Score ?: 0
                        val level = childState?.level?.filter { it.isDigit() }?.toIntOrNull() ?: 1
                        
                        // Visual calculation for progress bar (assuming 500 pts per visual level step for now)
                        // This doesn't affect the backend level, just visualizes progress to "next" milestone
                        val scorePerLevel = 500
                        val progressInLevel = currentScore % scorePerLevel
                        val progressFloat = (progressInLevel.toFloat() / scorePerLevel.toFloat()).coerceIn(0f, 1f)
                        
                        Column(
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                InfoChip(icon = "📊", text = "Level ${childState?.level ?: "1"}")
                                Text(
                                    text = "$progressInLevel / $scorePerLevel XP",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(6.dp))
                            
                            // XP Progress Bar
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .background(
                                        color = Color.White.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(100.dp)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = Color.White.copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(100.dp)
                                    )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(progressFloat)
                                        .background(
                                            brush = Brush.horizontalGradient(
                                                colors = listOf(
                                                    Color(0xFF4CAF50),
                                                    Color(0xFF8BC34A)
                                                )
                                            ),
                                            shape = RoundedCornerShape(100.dp)
                                        )
                                )
                            }
                        }
                    }

                    val context = LocalContext.current
                    val avatarResId = remember(childState?.avatarEmoji) {
                        val name = childState?.avatarEmoji ?: "avatar_3"
                        val res = getAvatarResource(context, name)
                        if (res != 0) res else getAvatarResource(context, "avatar_3")
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp) // Slightly larger avatar
                                .background(Color.White.copy(alpha = 0.2f), CircleShape)
                                .border(2.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (avatarResId != 0) {
                                Image(
                                    painter = painterResource(id = avatarResId),
                                    contentDescription = childState?.name,
                                    modifier = Modifier.size(48.dp),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.avatar_3),
                                    contentDescription = childState?.name,
                                    modifier = Modifier.size(48.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                        
                        Row(
                            modifier = Modifier
                                .background(
                                    color = Color(0xFFFFD700).copy(alpha = 0.2f), // Gold tint
                                    shape = RoundedCornerShape(100.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFFFFD700).copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(100.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = "⭐", fontSize = 14.sp)
                            Text(
                                text = "${childState?.Score ?: 0}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Gifts Section - Compact
                if (gifts.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "🎁 Gifts",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Text(
                            text = "${gifts.size} available",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                    
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        items(gifts) { gift ->
                            CompactGiftCard(
                                gift = gift,
                                currentScore = childState?.Score ?: 0,
                                isBuying = isBuyingGift == gift.id,
                                onBuyClick = {
                                    if (childState?.parentId != null && childState?.id != null && gift.id != null) {
                                        isBuyingGift = gift.id
                                        scope.launch {
                                            val result = ApiClient.buyGift(
                                                parentId = childState!!.parentId!!,
                                                kidId = childState!!.id!!,
                                                giftId = gift.id!!
                                            )
                                            result.onSuccess {
                                                // Simply subtract the gift cost from current score
                                                childState = childState?.copy(Score = (childState?.Score ?: 0) - gift.cost)
                                                giftSuccess = "You bought ${gift.title}!"
                                                isBuyingGift = null
                                                // Reload gifts to remove the purchased one
                                                val reloadResult = ApiClient.getGifts(childState!!.parentId!!, childState!!.id!!)
                                                reloadResult.onSuccess { giftList ->
                                                    gifts = giftList
                                                }
                                            }.onFailure { e ->
                                                giftError = e.message ?: "Failed to buy gift"
                                                isBuyingGift = null
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Category Filter
                Text(
                    text = "📚 Quiz Categories",
                    fontSize = 18.sp,
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
                
                Spacer(modifier = Modifier.height(20.dp))
                
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
    
    // Gift error snackbar
    giftError?.let { error ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            LaunchedEffect(error) {
                kotlinx.coroutines.delay(4000)
                giftError = null
            }
            Snackbar(
                action = {
                    TextButton(onClick = { giftError = null }) {
                        Text("Dismiss", color = Color.White)
                    }
                },
                containerColor = Color(0xFFD32F2F),
                contentColor = Color.White
            ) {
                Text(text = error, color = Color.White)
            }
        }
    }
    
    // Gift success snackbar
    giftSuccess?.let { msg ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            LaunchedEffect(msg) {
                kotlinx.coroutines.delay(3000)
                giftSuccess = null
            }
            Snackbar(
                containerColor = Color(0xFF43A047),
                contentColor = Color.White
            ) {
                Text(text = msg, color = Color.White)
            }
        }
    }
}

@Composable
fun CompactGiftCard(
    gift: ShopItem,
    currentScore: Int,
    isBuying: Boolean,
    onBuyClick: () -> Unit
) {
    val canAfford = currentScore >= gift.cost
    
    Card(
        modifier = Modifier
            .width(110.dp)
            .clickable(enabled = canAfford && !isBuying, onClick = onBuyClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (canAfford) Color.White.copy(alpha = 0.95f) else Color.White.copy(alpha = 0.6f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = Color(0xFFAF7EE7).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🎁",
                    fontSize = 20.sp
                )
            }
            
            Spacer(modifier = Modifier.height(6.dp))
            
            Text(
                text = gift.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E2E2E),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 14.sp
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "⭐",
                    fontSize = 12.sp
                )
                Text(
                    text = "${gift.cost}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (canAfford) Color(0xFF2E2E2E) else Color(0xFF999999)
                )
            }
            
            Spacer(modifier = Modifier.height(6.dp))
            
            if (isBuying) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = Color(0xFFAF7EE7),
                    strokeWidth = 2.dp
                )
            } else {
                Button(
                    onClick = onBuyClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (canAfford) Color(0xFFAF7EE7) else Color(0xFFCCCCCC)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    enabled = canAfford,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (canAfford) "Buy" else "${gift.cost - currentScore}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
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