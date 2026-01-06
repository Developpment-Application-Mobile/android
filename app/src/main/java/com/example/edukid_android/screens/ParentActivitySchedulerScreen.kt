package com.example.edukid_android.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edukid_android.models.Child
import com.example.edukid_android.models.ScheduledActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import java.text.SimpleDateFormat
import java.util.*
import com.example.edukid_android.repositories.ScheduledActivityRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable

// Repository helper
fun rememberRepository(): ScheduledActivityRepository {
    val context = androidx.compose.ui.platform.LocalContext.current
    return remember { ScheduledActivityRepository(context) }
}

// ... (Rest of the file remains similar, but using repository)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentActivitySchedulerScreen(
    child: Child,
    onBack: () -> Unit
) {
    val repository = rememberRepository()
    var scheduledActivities by remember { mutableStateOf<List<ScheduledActivity>>(emptyList()) }
    var showCreateActivity by remember { mutableStateOf(false) }
    var activityToDelete by remember { mutableStateOf<ScheduledActivity?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }

    // Update time every second
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            currentTime = System.currentTimeMillis()
        }
    }


    // Load activities on start
    LaunchedEffect(Unit) {
        isLoading = true
        // Assuming we have parentId available in child object or somewhere
        // Using child.parentId if available, else empty string (might need fixing if model differs)
        scheduledActivities = repository.getActivities(child.parentId ?: "", child.id ?: "")
        isLoading = false
    }

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Schedule Activities") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF272052)
                )
            )
        },
        containerColor = Color(0xFF272052)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFAF7EE7).copy(alpha = 0.6f),
                            Color(0xFF272052)
                        ),
                        center = androidx.compose.ui.geometry.Offset(300f, 300f),
                        radius = 800f
                    )
                )
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Spacer(Modifier.height(20.dp))

                // Create Activity Button
                Button(
                    onClick = { showCreateActivity = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(horizontal = 20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(30.dp)
                ) {
                    Icon(
                        Icons.Default.AddCircle,
                        contentDescription = null,
                        tint = Color(0xFF272052)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "Schedule New Activity",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF272052)
                    )
                }

                Spacer(Modifier.height(20.dp))

                // Activities List
                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    }
                    scheduledActivities.isEmpty() -> {
                        EmptyActivitiesView()
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(scheduledActivities.sortedBy { it.scheduledTime }) { activity ->
                                ScheduledActivityCard(
                                    activity = activity,
                                    currentTime = currentTime,
                                    onDelete = { activityToDelete = activity }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Create Activity Sheet
    if (showCreateActivity) {
        CreateActivityScreen(
            child = child,
            onDismiss = { showCreateActivity = false },
            onActivityCreated = { newActivity: ScheduledActivity ->
                scope.launch {
                    repository.saveActivity(child.parentId ?: "", newActivity)
                    scheduledActivities = repository.getActivities(child.parentId ?: "", child.id ?: "")
                    showCreateActivity = false
                }
            }
        )
    }

    // Delete Confirmation Dialog
    if (activityToDelete != null) {
        AlertDialog(
            onDismissRequest = { activityToDelete = null },
            title = { Text("Delete Activity") },
            text = { Text("Are you sure you want to delete this scheduled activity?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        activityToDelete?.let { activity ->
                            scope.launch {
                                repository.deleteActivity(activity.id)
                                scheduledActivities = repository.getActivities(child.parentId ?: "", child.id ?: "")
                            }
                        }
                        activityToDelete = null
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { activityToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ScheduledActivityCard(
    activity: ScheduledActivity,
    currentTime: Long,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.12f)
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Activity Icon
            Box(
                modifier = Modifier
                    .size(60.dp)
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
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            // Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = activity.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = activity.description,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    maxLines = 1
                )

                Spacer(Modifier.height(6.dp))

                // Status
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (activity.isAvailable) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            "Available Now",
                            fontSize = 12.sp,
                            color = Color(0xFF4CAF50)
                        )
                    } else {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            tint = Color(0xFFFF9800),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            formatTimeRemaining(activity.timeRemaining),
                            fontSize = 12.sp,
                            color = Color(0xFFFF9800)
                        )
                    }
                }
            }

            // Duration and Delete
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        formatDuration(activity.duration),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        "Duration",
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.Red.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Red.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyActivitiesView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "📅",
                fontSize = 60.sp
            )
            Text(
                "No Scheduled Activities",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            Text(
                "Create your first scheduled activity\nfor your child",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}

// Helper functions
fun formatTimeRemaining(millis: Long): String {
    val hours = millis / (1000 * 60 * 60)
    val minutes = (millis / (1000 * 60)) % 60

    return when {
        hours > 0 -> "in ${hours}h ${minutes}m"
        minutes > 0 -> "in ${minutes}m"
        else -> "Soon"
    }
}

fun formatDuration(millis: Long): String {
    val minutes = millis / (1000 * 60)
    return when {
        minutes < 60 -> "${minutes} min"
        else -> {
            val hours = minutes / 60
            val remainingMinutes = minutes % 60
            if (remainingMinutes > 0) "${hours}h ${remainingMinutes}m" else "${hours} hour"
        }
    }
}




