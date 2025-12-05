package com.example.edukid_android.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edukid_android.R
import com.example.edukid_android.models.Quest
import com.example.edukid_android.models.Child
import com.example.edukid_android.utils.ApiService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun QuestsScreen(
    parentId: String,
    kidId: String,
    apiService: ApiService,
    onChildUpdate: ((Child) -> Unit)? = null
) {
    var quests by remember { mutableStateOf<List<Quest>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var claimedQuestId by remember { mutableStateOf<String?>(null) }
    var showCelebration by remember { mutableStateOf(false) }
    var earnedPoints by remember { mutableStateOf(0) }
    
    val scope = rememberCoroutineScope()

    fun loadQuests() {
        scope.launch {
            try {
                loading = true
                val response = apiService.getQuests(parentId, kidId)
                if (response.isSuccessful) {
                    quests = response.body()?.map { it.toQuest() } ?: emptyList()
                }
            } catch (e: Exception) {
                // handle error
            } finally {
                loading = false
            }
        }
    }

    LaunchedEffect(true) {
        loadQuests()
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
        // Decorative background elements
        DecorativeQuestBackground()

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header with Title
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Daily Quests",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Complete quests to earn stars!",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            if (loading && quests.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    contentPadding = PaddingValues(bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(quests) { quest ->
                        PremiumQuestCard(
                            quest = quest,
                            onClaim = {
                                if (quest.progress >= quest.target && !quest.isClaimed) {
                                    scope.launch {
                                        try {
                                            val response = apiService.claimQuest(parentId, kidId, quest.id)
                                            if (response.isSuccessful) {
                                                // Show celebration
                                                claimedQuestId = quest.id
                                                earnedPoints = quest.reward
                                                showCelebration = true
                                                
                                                // Update local child state from response
                                                response.body()?.let { childResponse ->
                                                    onChildUpdate?.invoke(childResponse.toChild())
                                                }
                                                
                                                // Reload to update state
                                                loadQuests()
                                                
                                                delay(2500)
                                                showCelebration = false
                                                claimedQuestId = null
                                            }
                                        } catch (e: Exception) {
                                            // Handle error
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }

        // Celebration Overlay
        if (showCelebration) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f))
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🎉 quest Complete!",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "+$earnedPoints ⭐",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFFFD700)
                    )
                }
            }
        }
    }
}

@Composable
fun PremiumQuestCard(
    quest: Quest,
    onClaim: () -> Unit
) {
    val progress = ((quest.progress.toFloat() / quest.target.toFloat()) * 100).coerceIn(0f, 100f)
    val isCompleted = quest.progress >= quest.target
    val isClaimed = quest.status == "CLAIMED"
    
    val animatedProgress by animateFloatAsState(
        targetValue = progress / 100f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "progress"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isCompleted && !isClaimed) 12.dp else 4.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = if (isCompleted && !isClaimed) Color(0xFFFFD700).copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.2f)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Icon Box
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = when {
                                    isClaimed -> listOf(Color.Gray, Color.LightGray)
                                    isCompleted -> listOf(Color(0xFFFFD700), Color(0xFFFFA500))
                                    else -> listOf(Color(0xFFE0E0E0), Color(0xFFF5F5F5))
                                }
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when {
                            isClaimed -> "✓"
                            isCompleted -> "🎁"
                            else -> "⚡"
                        },
                        fontSize = 24.sp
                    )
                }

                // Text Info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = quest.title ?: "Quest",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E2E2E)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${quest.reward} Stars Reward",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFFFB300)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFF0F0F0))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedProgress)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFFFFD700), Color(0xFFFFA500))
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${quest.progress} / ${quest.target}",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${progress.toInt()}%",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Button
            Button(
                onClick = onClaim,
                enabled = isCompleted && !isClaimed,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50),
                    disabledContainerColor = if (isClaimed) Color(0xFFCCCCCC) else Color(0xFFE0E0E0)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = when {
                        isClaimed -> "Claimed"
                        isCompleted -> "Claim Reward"
                        else -> "In Progress"
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isClaimed) Color.Gray else Color.White
                )
            }
        }
    }
}

@Composable
fun BoxScope.DecorativeQuestBackground() {
    Canvas(
        modifier = Modifier
            .size(300.dp)
            .align(Alignment.TopEnd)
            .offset(x = 100.dp, y = (-50).dp)
            .blur(60.dp)
    ) {
        drawCircle(color = Color(0xFF9C27B0).copy(alpha = 0.3f))
    }
    
    Canvas(
        modifier = Modifier
            .size(200.dp)
            .align(Alignment.BottomStart)
            .offset(x = (-50).dp, y = 50.dp)
            .blur(40.dp)
    ) {
        drawCircle(color = Color(0xFF3F51B5).copy(alpha = 0.3f))
    }
}
