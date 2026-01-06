package com.example.edukid_android.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edukid_android.models.*
import kotlinx.coroutines.delay
import com.example.edukid_android.repositories.ScheduledActivityRepository


// Repository helper - duplicating here to avoid complex DI refactor for now, but usually should be injected
@Composable
fun rememberChildRepository(): ScheduledActivityRepository {
    val context = androidx.compose.ui.platform.LocalContext.current
    return remember { ScheduledActivityRepository(context) }
}

@Composable
fun ChildScheduledActivitiesView(
    child: Child,
    onQuizCompleted: () -> Unit
) {
    val repository = rememberChildRepository()
    var scheduledActivities by remember { mutableStateOf<List<ScheduledActivity>>(emptyList()) }
    var selectedActivity by remember { mutableStateOf<ScheduledActivity?>(null) }
    var showQuizTaking by remember { mutableStateOf(false) }
    var showGameScreen by remember { mutableStateOf(false) }
    var showPuzzleScreen by remember { mutableStateOf(false) }
    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }

    // Update time every second
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            currentTime = System.currentTimeMillis()
        }
    }


    // Load activities
    LaunchedEffect(Unit) {
        // Poll for updates every few seconds
        while(true) {
            // Need parentId. Assuming child.parentId is available.
            scheduledActivities = repository.getActivities(child.parentId ?: "", child.id ?: "")
            delay(5000) 
        }
    }

    val availableActivities = scheduledActivities.filter { it.isAvailable && !it.isCompleted }
    val upcomingActivities = scheduledActivities.filter { !it.isAvailable && !it.isCompleted }
    val completedActivities = scheduledActivities.filter { it.isCompleted }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        when {
            scheduledActivities.isEmpty() -> {
                ChildEmptyState(
                    icon = "📅",
                    title = "No scheduled activities",
                    message = "Your parent hasn't scheduled any activities yet"
                )
            }
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Available Now Section
                    if (availableActivities.isNotEmpty()) {
                        item {
                            ChildSectionHeader(
                                title = "Available Now! 🎉",
                                icon = Icons.Default.PlayCircle
                            )
                        }

                        items(availableActivities) { activity ->
                            ChildActivityCard(
                                activity = activity,
                                isAvailable = true,
                                currentTime = currentTime,
                                onTap = {
                                    selectedActivity = activity
                                    when (activity.activityType) {
                                        ScheduledActivity.ActivityType.QUIZ -> showQuizTaking = true
                                        ScheduledActivity.ActivityType.GAME -> showGameScreen = true
                                        ScheduledActivity.ActivityType.PUZZLE -> showPuzzleScreen = true
                                    }
                                }
                            )
                        }
                    }

                    // Coming Soon Section
                    if (upcomingActivities.isNotEmpty()) {
                        item {
                            ChildSectionHeader(
                                title = "Coming Soon ⏰",
                                icon = Icons.Default.Schedule
                            )
                        }

                        items(upcomingActivities) { activity ->
                            ChildActivityCard(
                                activity = activity,
                                isAvailable = false,
                                currentTime = currentTime,
                                onTap = null
                            )
                        }
                    }

                    // Completed Section
                    if (completedActivities.isNotEmpty()) {
                        item {
                            ChildSectionHeader(
                                title = "Completed ✅",
                                icon = Icons.Default.CheckCircle
                            )
                        }

                        items(completedActivities.take(3)) { activity ->
                            ChildCompletedActivityCard(activity = activity)
                        }
                    }
                }
            }
        }
    }

    // TODO: Show quiz/game/puzzle screens based on selectedActivity
    // Logic to mark as completed when returned
//    if (showQuizTaking && selectedActivity != null) {
//        val scope = rememberCoroutineScope()
//        // Example integration
//        QuizTakingScreen(
//            quiz = selectedActivity!!.quizData!!,
//            onComplete = {
//                scope.launch {
//                     repository.markActivityCompleted(selectedActivity!!.id)
//                }
//                showQuizTaking = false
//                onQuizCompleted()
//            },
//            onClose = { showQuizTaking = false }
//        )
//    }
}

@Composable
fun ChildSectionHeader(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Text(
            title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun ChildActivityCard(
    activity: ScheduledActivity,
    isAvailable: Boolean,
    currentTime: Long,
    onTap: (() -> Unit)?
) {
    Card(
        onClick = { onTap?.invoke() },
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.25f)
        ),
        shape = RoundedCornerShape(20.dp),
        enabled = isAvailable
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .background(
                        activity.activityType.color.copy(alpha = 0.3f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (activity.activityType) {
                        ScheduledActivity.ActivityType.QUIZ -> Icons.Default.Quiz
                        ScheduledActivity.ActivityType.GAME -> Icons.Default.SportsEsports
                        ScheduledActivity.ActivityType.PUZZLE -> Icons.Default.Extension
                    },
                    contentDescription = null,
                    tint = activity.activityType.color,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    activity.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    activity.description,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    maxLines = 1
                )

                Spacer(Modifier.height(8.dp))

                // Status
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (isAvailable) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                "Available Now",
                                fontSize = 12.sp,
                                color = Color(0xFF4CAF50),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.Schedule,
                                contentDescription = null,
                                tint = Color(0xFFFF9800),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                formatTimeRemaining(activity.timeRemaining),
                                fontSize = 12.sp,
                                color = Color(0xFFFF9800),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Default.Timer,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            formatDuration(activity.duration),
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            if (isAvailable) {
                Icon(
                    Icons.Default.PlayCircle,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}

@Composable
fun ChildCompletedActivityCard(activity: ScheduledActivity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(24.dp)
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    activity.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2
                )
                Text(
                    activity.description,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    maxLines = 1
                )
            }

            Text(
                "⭐",
                fontSize = 20.sp
            )
        }
    }
}

@Composable
fun ChildEmptyState(
    icon: String,
    title: String,
    message: String
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                icon,
                fontSize = 50.sp
            )
            Text(
                title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.7f)
            )
            Text(
                message,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.6f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
