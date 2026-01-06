@file:Suppress("VariableNeverRead", "VariableNeverRead")

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
import com.example.edukid_android.models.InventoryItem
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
    onChildUpdate: (Child) -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf<QuizType?>(null) }

    var childState by remember { mutableStateOf(child) }
    
    // Puzzle play state
    var selectedPuzzle by remember { mutableStateOf<com.example.edukid_android.models.PuzzleResponse?>(null) }
    var showPuzzlePlay by remember { mutableStateOf(false) }

    // Update local state when child prop changes
    LaunchedEffect(child) {
        childState = child
    }
    // Gifts state (Restored)
    var giftError by remember { mutableStateOf<String?>(null) }
    var giftSuccess by remember { mutableStateOf<String?>(null) }
    var isBuyingGift by remember { mutableStateOf<String?>(null) } // giftId being purchased
    var showGifts by remember { mutableStateOf(false) } // Toggle for gifts section
    val scope = rememberCoroutineScope()

    val ownedGifts = remember(childState) {
        childState?.inventory ?: emptyList()
    }

    val availableGifts = remember(childState) {
        val inventoryTitles = childState?.inventory?.map { it.title }?.toSet() ?: emptySet()
        childState?.shopCatalog?.filter { it.title !in inventoryTitles } ?: emptyList()
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
        filteredQuizzes.filter { it.getCompletionPercentage() in 1..<100 }
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
            // Main content - Now scrollable
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                item {
                    // Premium Header with Glassmorphism
                    Spacer(modifier = Modifier.height(40.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.15f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color.White.copy(alpha = 0.2f),
                                            Color.White.copy(alpha = 0.05f)
                                        )
                                    )
                                )
                                .border(
                                    width = 1.dp,
                                    color = Color.White.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(24.dp)
                                )
                                .padding(20.dp)
                        ) {
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
                                            InfoChip(iconResId = R.drawable.icon_birthday, text = "$it years old")
                                        }
//                                        childState?.level?.takeIf { it.isNotBlank() }?.let {
//                                            InfoChip(iconResId = R.drawable.icon_level, text = "Level $it")
//                                        }
                                    }
                                }

                                val context = LocalContext.current
                                val avatarResId = remember(childState?.avatarEmoji) {
                                    val name = childState?.avatarEmoji ?: "avatar_3"
                                    val res = getAvatarResource(context, name)
                                    if (res != 0) res else getAvatarResource(context, "avatar_3")
                                }

                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .background(
                                            brush = Brush.radialGradient(
                                                colors = listOf(
                                                    Color(0xFFFFD700),
                                                    Color(0xFFFFA500)
                                                )
                                            ),
                                            shape = CircleShape
                                        )
                                        .padding(3.dp)
                                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                                        .padding(2.dp),
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
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Points and Level Progress
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Points with Coin Icon
                                Row(
                                    modifier = Modifier
                                        .background(
                                            brush = Brush.horizontalGradient(
                                                colors = listOf(
                                                    Color(0xFFFFD700).copy(alpha = 0.3f),
                                                    Color(0xFFFFA500).copy(alpha = 0.2f)
                                                )
                                            ),
                                            shape = RoundedCornerShape(100.dp)
                                        )
                                        .border(
                                            width = 2.dp,
                                            color = Color(0xFFFFD700).copy(alpha = 0.5f),
                                            shape = RoundedCornerShape(100.dp)
                                        )
                                        .padding(horizontal = 16.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.coins),
                                        contentDescription = "Coins",
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = "${childState?.Score ?: 0}",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                // Level Badge
                                Row(
                                    modifier = Modifier
                                        .background(
                                            brush = Brush.horizontalGradient(
                                                colors = listOf(
                                                    Color(0xFFAF7EE7).copy(alpha = 0.3f),
                                                    Color(0xFF7E57C2).copy(alpha = 0.2f)
                                                )
                                            ),
                                            shape = RoundedCornerShape(100.dp)
                                        )
                                        .border(
                                            width = 2.dp,
                                            color = Color(0xFFAF7EE7).copy(alpha = 0.5f),
                                            shape = RoundedCornerShape(100.dp)
                                        )
                                        .padding(horizontal = 16.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(text = "🏆", fontSize = 20.sp)
                                    Text(
                                        text = "Level ${childState?.progressionLevel ?: 1}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Level Progress Bar
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Progress to Level ${(childState?.progressionLevel ?: 1) + 1}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                    Text(
                                        text = "${(childState?.lifetimeScore ?: 0) % 1000} / 1000",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFD700)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(10.dp)
                                        .background(
                                            color = Color.White.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(5.dp)
                                        )
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(((childState?.lifetimeScore ?: 0) % 1000) / 1000f)
                                            .fillMaxHeight()
                                            .background(
                                                brush = Brush.horizontalGradient(
                                                    colors = listOf(
                                                        Color(0xFFFFD700),
                                                        Color(0xFFFFA500)
                                                    )
                                                ),
                                                shape = RoundedCornerShape(5.dp)
                                            )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Collapsible Gifts Section
                    Button(
                        onClick = { showGifts = !showGifts },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.15f)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            brush = Brush.radialGradient(
                                                colors = listOf(
                                                    Color(0xFFFF6B9D),
                                                    Color(0xFFC239B3)
                                                )
                                            ),
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "🎁", fontSize = 20.sp)
                                }

                                Column {
                                    Text(
                                        text = "Rewards & Treasures",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "${availableGifts.size} available • ${ownedGifts.size} collected",
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                }
                            }

                            Text(
                                text = if (showGifts) "▲" else "▼",
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }
                    }

                    androidx.compose.animation.AnimatedVisibility(
                        visible = showGifts,
                        enter = androidx.compose.animation.expandVertically() + androidx.compose.animation.fadeIn(),
                        exit = androidx.compose.animation.shrinkVertically() + androidx.compose.animation.fadeOut()
                    ) {
                        Column {
                            // 1. Your Treasures (Owned Gifts)
                            if (ownedGifts.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "🏆 Your Treasures",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.9f),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(ownedGifts) { item ->
                                        // Using a simplified card for owned items
                                        OwnedGiftCard(item = item)
                                    }
                                }
                            }

                            // 2. Available Rewards (Shop Catalog)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "🛍️ Available Rewards",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            if (availableGifts.isEmpty()) {
                                Text(
                                    text = "Ask your parent to add more rewards!",
                                    fontSize = 13.sp,
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )
                            } else {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(availableGifts) { gift ->
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
                                                            giftId = gift.id
                                                        )
                                                        result.onSuccess { _ ->
                                                            // Simple approach: just subtract cost from local score
                                                            // Add gift to inventory locally
                                                            val newInventoryItem = InventoryItem(
                                                                title = gift.title,
                                                                cost = gift.cost,
                                                                purchasedAt = System.currentTimeMillis().toString()
                                                            )
                                                            
                                                            val updatedChild = childState?.copy(
                                                                Score = (childState?.Score ?: 0) - gift.cost,
                                                                inventory = (childState?.inventory ?: emptyList()) + newInventoryItem
                                                            )
                                                            
                                                            if (updatedChild != null) {
                                                                childState = updatedChild
                                                                onChildUpdate(updatedChild)
                                                            }
                                                            giftSuccess = "You bought ${gift.title}!"
                                                            isBuyingGift = null
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
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(bottom = 20.dp)
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

                    // Quizzes List - Now as Column items instead of LazyColumn
                    // In Progress Section
                    if (inProgressQuizzes.isNotEmpty()) {
                        Text(
                            text = "Continue Playing 🎮",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        inProgressQuizzes.forEach { quiz ->
                            PremiumQuizCard(
                                quiz = quiz,
                                onClick = { onQuizClick(quiz) }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Available Quizzes Section
                    if (notStartedQuizzes.isNotEmpty()) {
                        Text(
                            text = "Start New Adventure 🚀",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        notStartedQuizzes.forEach { quiz ->
                            PremiumQuizCard(
                                quiz = quiz,
                                onClick = { onQuizClick(quiz) }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    // Puzzles Section (from backend)
                    val backendPuzzles = remember(childState) {
                        childState?.puzzles ?: emptyList()
                    }
                    
                    if (backendPuzzles.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "My Puzzles 🧩",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        backendPuzzles.forEach { puzzle ->
                            ServerPuzzleCard(
                                puzzle = puzzle,
                                onClick = {
                                    selectedPuzzle = puzzle
                                    showPuzzlePlay = true
                                }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    // Empty state
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

                    // Bottom padding for navigation bar
                    Spacer(modifier = Modifier.height(80.dp))
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
            }
            Snackbar(
                action = {
                    TextButton(onClick = { }) {
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
            }
            Snackbar(
                containerColor = Color(0xFF43A047),
                contentColor = Color.White
            ) {
                Text(text = msg, color = Color.White)
            }
        }
    }
    
    // Puzzle Play Screen Overlay
    if (showPuzzlePlay && selectedPuzzle != null) {
        ImagePuzzlePlayScreen(
            puzzle = selectedPuzzle!!,
            onBack = {
                showPuzzlePlay = false
                selectedPuzzle = null
            },
            onComplete = { score, timeSpent ->
                // Submit puzzle completion to backend
                scope.launch {
                    try {
                        childState?.let { child ->
                            val result = ApiClient.submitPuzzle(
                                parentId = child.parentId ?: "",
                                kidId = child.id ?: "",
                                puzzleId = selectedPuzzle!!.id,
                                positions = selectedPuzzle!!.pieces.map { it.currentPosition },
                                timeSpent = timeSpent
                            )
                            
                            result.onSuccess { updatedChild ->
                                childState = updatedChild.toChild()
                                onChildUpdate(childState!!)
                            }
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("ChildHomeScreen", "Error submitting puzzle", e)
                    }
                }
                showPuzzlePlay = false
                selectedPuzzle = null
            }
        )
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
                    text = "${gift.cost} ⭐",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (canAfford) Color(0xFF43A047) else Color(0xFFAF7EE7)
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
fun OwnedGiftCard(item: InventoryItem) {
    Card(
        modifier = Modifier
            .width(110.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE8F5E9) // Light green for owned
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
                        color = Color(0xFF4CAF50).copy(alpha = 0.2f),
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
                text = item.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E2E2E),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 14.sp
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "Owned",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50)
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
    Card(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(100.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 6.dp else 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = if (isSelected) {
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.White,
                                Color(0xFFF5F5F5)
                            )
                        )
                    } else {
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.25f),
                                Color.White.copy(alpha = 0.15f)
                            )
                        )
                    },
                    shape = RoundedCornerShape(100.dp)
                )
                .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) Color(0xFFAF7EE7) else Color.White.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(100.dp)
                )
                .padding(horizontal = 18.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFFAF7EE7).copy(alpha = 0.3f),
                                        Color(0xFF7E57C2).copy(alpha = 0.2f)
                                    )
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = icon,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    Text(
                        text = icon,
                        fontSize = 16.sp
                    )
                }
                Text(
                    text = label,
                    fontSize = 15.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color(0xFF2E2E2E) else Color.White
                )
            }
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
fun DecorativeElementsChildHome() {
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


@Composable
fun ServerPuzzleCard(
    puzzle: com.example.edukid_android.models.PuzzleResponse,
    onClick: () -> Unit
) {
    val completionStatus = if (puzzle.isCompleted) "Completed ✓" else "Not Started"
    val statusColor = if (puzzle.isCompleted) Color(0xFF4CAF50) else Color(0xFFFF9800)

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
                            puzzle.puzzleType.color.copy(alpha = 0.12f),
                            Color.White.copy(alpha = 0.9f)
                        )
                    )
                )
                .padding(18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Puzzle Icon
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .shadow(6.dp, RoundedCornerShape(20.dp))
                        .background(
                            brush = Brush.linearGradient(
                                listOf(
                                    puzzle.puzzleType.color.copy(alpha = 0.25f),
                                    puzzle.puzzleType.color.copy(alpha = 0.45f)
                                )
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Show puzzle type icon
                    Text(
                        text = when (puzzle.puzzleType) {
                            com.example.edukid_android.models.PuzzleType.IMAGE -> "🖼️"
                            com.example.edukid_android.models.PuzzleType.WORD -> "🔤"
                            com.example.edukid_android.models.PuzzleType.NUMBER -> "🔢"
                            com.example.edukid_android.models.PuzzleType.SEQUENCE -> "🔄"
                            com.example.edukid_android.models.PuzzleType.PATTERN -> "🧩"
                        },
                        fontSize = 36.sp
                    )
                }

                Spacer(modifier = Modifier.width(18.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = puzzle.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E1E1E)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "${puzzle.gridSize}×${puzzle.gridSize}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF6A6A6A)
                        )
                        
                        Text(
                            text = "•",
                            fontSize = 13.sp,
                            color = Color(0xFF6A6A6A)
                        )
                        
                        Text(
                            text = puzzle.puzzleDifficulty.displayName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = puzzle.puzzleDifficulty.color
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Status badge
                    Box(
                        modifier = Modifier
                            .background(
                                color = statusColor.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = completionStatus,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    }
                }

                // Play button
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            brush = Brush.linearGradient(
                                listOf(
                                    puzzle.puzzleType.color,
                                    puzzle.puzzleType.color.copy(alpha = 0.8f)
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (puzzle.isCompleted) "↻" else "▶",
                        fontSize = 20.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Show score if completed
            if (puzzle.isCompleted && puzzle.score > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Score: ${puzzle.score} pts",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF4CAF50)
                    )
                    
                    if (puzzle.timeSpent > 0) {
                        Text(
                            text = "Time: ${puzzle.timeSpent}s",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF6A6A6A)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PuzzleCard(
    puzzle: com.example.edukid_android.models.LocalPuzzle,
    onClick: () -> Unit
) {
    val completionStatus = if (puzzle.isCompleted) "Completed ✓" else "Not Started"
    val statusColor = if (puzzle.isCompleted) Color(0xFF4CAF50) else Color(0xFFFF9800)

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
                            puzzle.type.color.copy(alpha = 0.12f),
                            Color.White.copy(alpha = 0.9f)
                        )
                    )
                )
                .padding(18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Puzzle Icon
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .shadow(6.dp, RoundedCornerShape(20.dp))
                        .background(
                            brush = Brush.linearGradient(
                                listOf(
                                    puzzle.type.color.copy(alpha = 0.25f),
                                    puzzle.type.color.copy(alpha = 0.45f)
                                )
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (puzzle.customImagePath != null) {
                        // Show custom image icon
                        Text(
                            text = "🖼️",
                            fontSize = 36.sp
                        )
                    } else {
                        // Show puzzle emoji
                        Text(
                            text = puzzle.puzzleImage.emoji,
                            fontSize = 36.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(18.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = puzzle.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E1E1E)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "${puzzle.gridSize}×${puzzle.gridSize}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF6A6A6A)
                        )
                        
                        Text(
                            text = "•",
                            fontSize = 13.sp,
                            color = Color(0xFF6A6A6A)
                        )
                        
                        Text(
                            text = puzzle.difficulty.displayName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = puzzle.difficulty.color
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Status badge
                    Box(
                        modifier = Modifier
                            .background(
                                color = statusColor.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = completionStatus,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    }
                }

                // Play button
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            brush = Brush.linearGradient(
                                listOf(
                                    puzzle.type.color,
                                    puzzle.type.color.copy(alpha = 0.8f)
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (puzzle.isCompleted) "↻" else "▶",
                        fontSize = 20.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Show score if completed
            if (puzzle.isCompleted && puzzle.score > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Best Score: ${puzzle.score} pts",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF4CAF50)
                    )
                    
                    Text(
                        text = "Time: ${puzzle.timeSpent}s",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF6A6A6A)
                    )
                }
            }
        }
    }
}
