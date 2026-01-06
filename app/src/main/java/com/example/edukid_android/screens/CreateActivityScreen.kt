package com.example.edukid_android.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edukid_android.models.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateActivityScreen(
    child: Child,
    onDismiss: () -> Unit,
    onActivityCreated: (ScheduledActivity) -> Unit
) {
    var activityType by remember { mutableStateOf(ScheduledActivity.ActivityType.QUIZ) }
    var scheduledDate by remember { mutableStateOf(Calendar.getInstance().apply { add(Calendar.HOUR, 1) }) }
    var duration by remember { mutableStateOf(900000L) } // 15 minutes default

    var selectedQuiz by remember { mutableStateOf<AIQuizResponse?>(null) }
    var selectedGame by remember { mutableStateOf<ScheduledActivity.GameType?>(null) }
    var selectedPuzzle by remember { mutableStateOf<ScheduledActivity.PuzzleType?>(null) }

    var showQuizSelection by remember { mutableStateOf(false) }
    var showGameSelection by remember { mutableStateOf(false) }
    var showPuzzleSelection by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Schedule Activity") },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, "Close")
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
                        )
                    )
                )
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    Text(
                        "📅 Schedule Activity",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Activity Type Selection
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            "Activity Type",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            ScheduledActivity.ActivityType.values().forEach { type ->
                                ActivityTypeButton(
                                    type = type,
                                    isSelected = activityType == type,
                                    onClick = { activityType = type },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // Selection based on type
                when (activityType) {
                    ScheduledActivity.ActivityType.QUIZ -> {
                        item {
                            SelectionCard(
                                title = "Select Quiz",
                                selectedItem = selectedQuiz?.title,
                                onClick = { showQuizSelection = true }
                            )
                        }
                    }
                    ScheduledActivity.ActivityType.GAME -> {
                        item {
                            SelectionCard(
                                title = "Select Game",
                                selectedItem = selectedGame?.displayName,
                                onClick = { showGameSelection = true }
                            )
                        }
                    }
                    ScheduledActivity.ActivityType.PUZZLE -> {
                        item {
                            SelectionCard(
                                title = "Select Puzzle",
                                selectedItem = selectedPuzzle?.title,
                                onClick = { showPuzzleSelection = true }
                            )
                        }
                    }
                }

                // Date & Time Selection
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            "Schedule For",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Date Button
                            Button(
                                onClick = { showDatePicker = true },
                                modifier = Modifier.weight(1f).height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.CalendarToday, null, tint = Color(0xFF272052))
                                    Text(
                                        SimpleDateFormat("MMM dd", Locale.getDefault()).format(scheduledDate.time),
                                        color = Color(0xFF272052),
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            // Time Button
                            Button(
                                onClick = { showTimePicker = true },
                                modifier = Modifier.weight(1f).height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.AccessTime, null, tint = Color(0xFF272052))
                                    Text(
                                        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(scheduledDate.time),
                                        color = Color(0xFF272052),
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Duration Selection
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            "Activity Duration",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            DurationButton(
                                duration = 900000L,
                                label = "15 min",
                                selected = duration == 900000L,
                                onClick = { duration = 900000L },
                                modifier = Modifier.weight(1f)
                            )
                            DurationButton(
                                duration = 1800000L,
                                label = "30 min",
                                selected = duration == 1800000L,
                                onClick = { duration = 1800000L },
                                modifier = Modifier.weight(1f)
                            )
                            DurationButton(
                                duration = 3600000L,
                                label = "1 hour",
                                selected = duration == 3600000L,
                                onClick = { duration = 3600000L },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Create Button
                item {
                    val canCreate = when (activityType) {
                        ScheduledActivity.ActivityType.QUIZ -> selectedQuiz != null
                        ScheduledActivity.ActivityType.GAME -> selectedGame != null
                        ScheduledActivity.ActivityType.PUZZLE -> selectedPuzzle != null
                    }

                    Button(
                        onClick = {
                            val title = when (activityType) {
                                ScheduledActivity.ActivityType.QUIZ -> selectedQuiz?.title ?: ""
                                ScheduledActivity.ActivityType.GAME -> selectedGame?.displayName ?: ""
                                ScheduledActivity.ActivityType.PUZZLE -> selectedPuzzle?.title ?: ""
                            }

                            val description = when (activityType) {
                                ScheduledActivity.ActivityType.QUIZ -> "${selectedQuiz?.subject} - ${selectedQuiz?.difficulty}"
                                ScheduledActivity.ActivityType.GAME -> selectedGame?.description ?: ""
                                ScheduledActivity.ActivityType.PUZZLE -> selectedPuzzle?.description ?: ""
                            }

                            val activity = ScheduledActivity(
                                childId = child.id ?: "",
                                activityType = activityType,
                                title = title,
                                description = description,
                                scheduledTime = scheduledDate.timeInMillis,
                                duration = duration,
                                quizData = selectedQuiz,
                                gameType = selectedGame,
                                puzzleType = selectedPuzzle
                            )

                            onActivityCreated(activity)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        enabled = canCreate,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            disabledContainerColor = Color.White.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(30.dp)
                    ) {
                        Icon(Icons.Default.CalendarMonth, null, tint = Color(0xFF272052))
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Schedule Activity",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF272052)
                        )
                    }
                }
            }
        }
    }

    // Selection Sheets
    if (showQuizSelection) {
        QuizSelectionSheet(
            child = child,
            onDismiss = { showQuizSelection = false },
            onSelect = { quiz ->
                selectedQuiz = quiz
                showQuizSelection = false
            }
        )
    }

    if (showGameSelection) {
        GameSelectionSheet(
            onDismiss = { showGameSelection = false },
            onSelect = { game ->
                selectedGame = game
                showGameSelection = false
            }
        )
    }

    if (showPuzzleSelection) {
        PuzzleSelectionSheet(
            child = child,
            onDismiss = { showPuzzleSelection = false },
            onSelect = { puzzle ->
                selectedPuzzle = puzzle
                showPuzzleSelection = false
            }
        )
    }

    // Date/Time Pickers (using Material Dialogs or similar library)
    // TODO: Implement date/time pickers
}

@Composable
fun ActivityTypeButton(
    type: ScheduledActivity.ActivityType,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(80.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) type.color else Color.White
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = when (type) {
                    ScheduledActivity.ActivityType.QUIZ -> Icons.Default.Quiz
                    ScheduledActivity.ActivityType.GAME -> Icons.Default.SportsEsports
                    ScheduledActivity.ActivityType.PUZZLE -> Icons.Default.Extension
                },
                contentDescription = null,
                tint = if (isSelected) Color.White else type.color,
                modifier = Modifier.size(28.dp)
            )
            Text(
                type.value.capitalize(),
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color.White else type.color
            )
        }
    }
}

@Composable
fun SelectionCard(
    title: String,
    selectedItem: String?,
    onClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Card(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    selectedItem ?: "Choose...",
                    fontSize = 16.sp,
                    color = if (selectedItem != null) Color(0xFF272052) else Color(0xFF272052).copy(alpha = 0.5f)
                )
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color(0xFF272052)
                )
            }
        }
    }
}

@Composable
fun DurationButton(
    duration: Long,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) Color(0xFF272052) else Color.White
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (selected) Color.White else Color(0xFF272052)
        )
    }
}

// Helper for repository
@Composable
fun rememberCreateRepository(): com.example.edukid_android.repositories.ScheduledActivityRepository {
    val context = androidx.compose.ui.platform.LocalContext.current
    return remember { com.example.edukid_android.repositories.ScheduledActivityRepository(context) }
}

// ... existing code ...

// Quiz Selection Sheet
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizSelectionSheet(
    child: Child,
    onDismiss: () -> Unit,
    onSelect: (AIQuizResponse) -> Unit
) {
    val repository = rememberCreateRepository()
    var quizzes by remember { mutableStateOf<List<AIQuizResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isLoading = true
        quizzes = repository.getQuizzes(child.id ?: "")
        isLoading = false
    }


    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF272052)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                "Select Quiz",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(Modifier.height(20.dp))

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
                quizzes.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("📚", fontSize = 60.sp)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "No Quizzes Available",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            "Create a quiz first to schedule it",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(quizzes) { quiz ->
                            Card(
                                onClick = { onSelect(quiz) },
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            quiz.title,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF272052)
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            "${quiz.subject} • ${quiz.questions.size} questions",
                                            fontSize = 14.sp,
                                            color = Color(0xFF272052).copy(alpha = 0.7f)
                                        )
                                    }
                                    Icon(
                                        Icons.Default.ChevronRight,
                                        contentDescription = null,
                                        tint = Color(0xFF272052)
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

// Game Selection Sheet
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameSelectionSheet(
    onDismiss: () -> Unit,
    onSelect: (ScheduledActivity.GameType) -> Unit
) {
    val allGames = ScheduledActivity.GameType.values()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF272052)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                "Select Game",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(Modifier.height(20.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(allGames.toList()) { game ->
                    Card(
                        onClick = { onSelect(game) },
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = when (game) {
                                    ScheduledActivity.GameType.MEMORY_MATCH -> Icons.Default.Psychology
                                    ScheduledActivity.GameType.COLOR_MATCH -> Icons.Default.Palette
                                    ScheduledActivity.GameType.SHAPE_MATCH -> Icons.Default.Category
                                    ScheduledActivity.GameType.NUMBER_SEQUENCE -> Icons.Default.FormatListNumbered
                                    ScheduledActivity.GameType.MATH_QUIZ -> Icons.Default.Calculate
                                    ScheduledActivity.GameType.EMOJI_MATCH -> Icons.Default.EmojiEmotions
                                },
                                contentDescription = null,
                                tint = Color(0xFF272052),
                                modifier = Modifier.size(40.dp)
                            )

                            Spacer(Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    game.displayName,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF272052)
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    game.description,
                                    fontSize = 14.sp,
                                    color = Color(0xFF272052).copy(alpha = 0.7f)
                                )
                            }

                            Icon(
                                Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = Color(0xFF272052)
                            )
                        }
                    }
                }
            }
        }
    }
}

// Puzzle Selection Sheet
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PuzzleSelectionSheet(
    child: Child,
    onDismiss: () -> Unit,
    onSelect: (ScheduledActivity.PuzzleType) -> Unit
) {
    // Static Puzzles for now
    val puzzles = listOf(
        ScheduledActivity.PuzzleType("1", "Lion King", "Sliding puzzle with Lion King theme", true),
        ScheduledActivity.PuzzleType("2", "Magic Forest", "Mystical forest puzzle", true),
        ScheduledActivity.PuzzleType("3", "Space Explorer", "Space themed puzzle", true)
    )
    val isLoading = false

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF272052)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                "Select Puzzle",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(Modifier.height(20.dp))

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
                puzzles.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("🧩", fontSize = 60.sp)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "No Puzzles Available",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            "Create a puzzle first to schedule it",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(puzzles) { puzzle ->
                            Card(
                                onClick = { onSelect(puzzle) },
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Extension,
                                        contentDescription = null,
                                        tint = Color(0xFF272052),
                                        modifier = Modifier.size(40.dp)
                                    )

                                    Spacer(Modifier.width(16.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            puzzle.title,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF272052)
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            puzzle.description,
                                            fontSize = 14.sp,
                                            color = Color(0xFF272052).copy(alpha = 0.7f)
                                        )
                                    }

                                    Icon(
                                        Icons.Default.ChevronRight,
                                        contentDescription = null,
                                        tint = Color(0xFF272052)
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

