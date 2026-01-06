package com.example.edukid_android.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.edukid_android.R
import com.example.edukid_android.models.Child
import com.example.edukid_android.models.Quiz
import com.example.edukid_android.models.PuzzleResponse
import com.example.edukid_android.models.getBackgroundColor
import com.example.edukid_android.models.getIconRes
import com.example.edukid_android.utils.ApiClient
import com.example.edukid_android.utils.SessionManager
import com.example.edukid_android.utils.getAvatarResource
import kotlinx.coroutines.launch

@Composable
fun ChildProfileQRScreen(
    child: Child,
    parentId: String? = null,
    onBackClick: () -> Unit = {},
    onViewResultsClick: () -> Unit = {},
    onQuizGenerated: (Quiz) -> Unit = {},
    onGiftManagementClick: () -> Unit = {},
    onViewReviewClick: () -> Unit = {}
) {
    var showQRDialog by remember { mutableStateOf(false) }
    var showPuzzleCreation by remember { mutableStateOf(false) }
    var showActivitiesDialog by remember { mutableStateOf(false) }
    var childState by remember { mutableStateOf(child) }
    var selectedSubject by remember { mutableStateOf("") }
    var selectedDifficulty by remember { mutableStateOf("") }
    var numberOfQuestions by remember { mutableStateOf("10") }
    var customTopic by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }
    var generateError by remember { mutableStateOf<String?>(null) }
    var generateSuccess by remember { mutableStateOf<String?>(null) }
    
    // QR Code state
    var qrCodeBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoadingQR by remember { mutableStateOf(false) }
    var qrError by remember { mutableStateOf<String?>(null) }
    
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val scope = rememberCoroutineScope()

    val subjects = listOf(
        R.drawable.math to "Math",
        R.drawable.ic_science to "Science",
        R.drawable.english to "English",
        R.drawable.ic_geography to "Geography",
        R.drawable.ic_history to "History",
        R.drawable.art to "Art"
    )

    val difficulties = listOf("Easy", "Medium", "Hard")

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
            DecorativeElementsProfilem()

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
                            .size(40.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = "${childState.name}'s Profile",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.4.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Child Profile Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.95f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Avatar
                        val context = LocalContext.current
                        val avatarResId = remember(childState.avatarEmoji) {
                            val res = getAvatarResource(context, childState.avatarEmoji)
                            if (res != 0) res else getAvatarResource(context, "avatar_3")
                        }

                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .background(
                                    color = Color(0xFFAF7EE7).copy(alpha = 0.2f),
                                    shape = CircleShape
                                )
                                .border(
                                    width = 4.dp,
                                    color = Color(0xFFAF7EE7),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (avatarResId != 0) {
                                Image(
                                    painter = painterResource(id = avatarResId),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(96.dp)
                                        .border(2.dp, Color(0xFFAF7EE7), CircleShape)
                                        .background(Color.White, CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.avatar_3),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(96.dp)
                                        .border(2.dp, Color(0xFFAF7EE7), CircleShape)
                                        .background(Color.White, CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Name
                        Text(
                            text = childState.name,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E2E2E)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Age and Level
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            InfoChip(
                                iconResId = R.drawable.icon_birthday, // Placeholder for age/cake
                                text = "${childState.age} years old"
                            )
                            InfoChip(
                                iconResId = R.drawable.icon_level, // Placeholder for level
                                text = "Level ${childState.level}"
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Divider(color = Color(0xFFE0E0E0))

                        Spacer(modifier = Modifier.height(20.dp))

                        // Stats
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatColumn(
                                iconResId = R.drawable.taskcompleted, // Placeholder
                                value = "${childState.getCompletedQuizzes().size}",
                                label = "Completed"
                            )

                            VerticalDivider(
                                modifier = Modifier.height(60.dp),
                                color = Color(0xFFE0E0E0),
                                thickness = 1.dp
                            )

                            StatColumn(
                                iconResId = R.drawable.coins, // Placeholder
                                value = "${childState.Score}",
                                label = "Score"
                            )

                            VerticalDivider(
                                modifier = Modifier.height(60.dp),
                                color = Color(0xFFE0E0E0),
                                thickness = 1.dp
                            )

                            StatColumn(
                                iconResId = R.drawable.education_book, // Placeholder
                                value = "${childState.quizzes.size}",
                                label = "Total"
                            )
                        }
                    }
                }

                // Quick Actions Section
                Text(
                    text = "Quick Actions",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 16.dp),
                    letterSpacing = 0.5.sp
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // First row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ProfileActionButton(
                            iconResId = R.drawable.qrcode, // Placeholder
                            label = "Show QR",
                            onClick = {
                                if (parentId != null && childState.id != null) {
                                    isLoadingQR = true
                                    qrError = null
                                    scope.launch {
                                        val result = ApiClient.getQRCode(parentId, childState.id!!)
                                        result.onSuccess { qrResponse ->
                                            val base64String = qrResponse.qr
                                            val base64Image = if (base64String.startsWith("data:image")) {
                                                base64String.substringAfter(",")
                                            } else {
                                                base64String
                                            }
                                            
                                            try {
                                                val imageBytes = Base64.decode(base64Image, Base64.DEFAULT)
                                                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                                                qrCodeBitmap = bitmap
                                                showQRDialog = true
                                                isLoadingQR = false
                                            } catch (e: Exception) {
                                                qrError = "Failed to decode QR code: ${e.message}"
                                                isLoadingQR = false
                                            }
                                        }.onFailure { exception ->
                                            qrError = exception.message ?: "Failed to load QR code"
                                            isLoadingQR = false
                                        }
                                    }
                                } else {
                                    qrError = "Missing parent or child ID"
                                }
                            },
                            isLoading = isLoadingQR,
                            enabled = parentId != null && child.id != null,
                            modifier = Modifier.weight(1f)
                        )

                        ProfileActionButton(
                            iconResId = R.drawable.taskcompleted, // Placeholder
                            label = "Results",
                            onClick = onViewResultsClick,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Second row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ProfileActionButton(
                            iconResId = R.drawable.icon_gift, // Placeholder
                            label = "Gifts",
                            onClick = onGiftManagementClick,
                            modifier = Modifier.weight(1f)
                        )

                        ProfileActionButton(
                            iconResId = R.drawable.icon_results, // Placeholder
                            label = "Review",
                            onClick = onViewReviewClick,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    // Third row - Puzzle Creation and Activities
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ProfileActionButton(
                            iconResId = R.drawable.icon_level,
                            label = "Create Puzzle",
                            onClick = { showPuzzleCreation = true },
                            modifier = Modifier.weight(1f)
                        )
                        
                        ProfileActionButton(
                            iconResId = R.drawable.icon_results,
                            label = "Show Activities",
                            onClick = { showActivitiesDialog = true },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // AI Quiz Generator Section
                Text(
                    text = "AI Quiz Generator",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 16.dp),
                    letterSpacing = 0.5.sp
                )

                // AI Badge with improved design
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.98f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFFAF7EE7).copy(alpha = 0.1f),
                                        Color(0xFF7E57C2).copy(alpha = 0.05f)
                                    )
                                )
                            )
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

//                        Spacer(modifier = Modifier.width(16.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "✨ Powered by AI",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E2E2E)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Personalized questions tailored for ${child.name}",
                                fontSize = 13.sp,
                                color = Color(0xFF666666),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Quick Generate Button - Based on Kid's Needs
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.98f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                Color(0xFFAF7EE7),
                                                Color(0xFF7E57C2)
                                            )
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.icon_generator), // Placeholder for target
                                    contentDescription = null,
                                    modifier = Modifier.size(28.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Smart Quiz",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E2E2E)
                                )
                                Text(
                                    text = "AI analyzes ${child.name}'s needs automatically",
                                    fontSize = 12.sp,
                                    color = Color(0xFF666666),
                                    lineHeight = 16.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (parentId != null && childState.id != null) {
                                    isGenerating = true
                                    generateError = null
                                    generateSuccess = null
                                    scope.launch {
                                        val result = ApiClient.generateQuizBasedOnNeeds(
                                            parentId = parentId,
                                            kidId = childState.id!!
                                        )
                                        result.onSuccess { updatedChildResponse ->
                                            val updatedChild = updatedChildResponse.toChild()
                                            childState = childState.copy(
                                                quizzes = childState.quizzes + updatedChild.quizzes
                                            )
                                            val newQuiz = updatedChild.quizzes.lastOrNull()
                                            if (newQuiz != null) {
                                                onQuizGenerated(newQuiz)
                                            }
                                            generateSuccess = "Smart quiz generated: ${updatedChild.quizzes.lastOrNull()?.title ?: "New Quiz"}"
                                            isGenerating = false
                                        }.onFailure { e ->
                                            generateError = e.message ?: "Failed to generate smart quiz"
                                            isGenerating = false
                                        }
                                    }
                                } else {
                                    generateError = "Missing parent or child ID"
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFAF7EE7)
                            ),
                            shape = RoundedCornerShape(14.dp),
                            enabled = !isGenerating
                        ) {
                            if (isGenerating) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                    Text(
                                        text = "GENERATING...",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.taskcompleted), // Placeholder for rocket
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "GENERATE SMART QUIZ",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Divider with text
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Divider(
                        modifier = Modifier.weight(1f),
                        color = Color.White.copy(alpha = 0.3f),
                        thickness = 1.dp
                    )
                    Text(
                        text = "  OR CUSTOMIZE  ",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.7f),
                        letterSpacing = 1.sp
                    )
                    Divider(
                        modifier = Modifier.weight(1f),
                        color = Color.White.copy(alpha = 0.3f),
                        thickness = 1.dp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Custom Quiz Section
                Text(
                    text = "Custom Quiz Settings",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Select Subject
                Text(
                    text = "Select Subject",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.98f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            subjects.take(3).forEach { (iconResId, subject) ->
                                SubjectOption(
                                    iconResId = iconResId,
                                    subject = subject,
                                    isSelected = selectedSubject == subject,
                                    onClick = { selectedSubject = subject }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            subjects.drop(3).forEach { (iconResId, subject) ->
                                SubjectOption(
                                    iconResId = iconResId,
                                    subject = subject,
                                    isSelected = selectedSubject == subject,
                                    onClick = { selectedSubject = subject }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Custom Topic
                Text(
                    text = "Custom Topic (Optional)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                OutlinedTextField(
                    value = customTopic,
                    onValueChange = { customTopic = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    placeholder = {
                        Text(
                            text = "e.g., 'Dinosaurs', 'Solar System'",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 14.sp
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        cursorColor = Color.White,
                        focusedContainerColor = Color.White.copy(alpha = 0.1f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.05f)
                    ),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Difficulty and Questions Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Difficulty
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Difficulty",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White.copy(alpha = 0.98f)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                difficulties.forEach { difficulty ->
                                    DifficultyOptionCompact(
                                        difficulty = difficulty,
                                        isSelected = selectedDifficulty == difficulty,
                                        onClick = { selectedDifficulty = difficulty }
                                    )
                                }
                            }
                        }
                    }

                    // Number of Questions
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Questions",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )

                        OutlinedTextField(
                            value = numberOfQuestions,
                            onValueChange = {
                                if (it.isEmpty() || (it.toIntOrNull() != null && it.toInt() in 1..30)) {
                                    numberOfQuestions = it
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            placeholder = {
                                Text(
                                    text = "1-30",
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 14.sp
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                                cursorColor = Color.White,
                                focusedContainerColor = Color.White.copy(alpha = 0.1f),
                                unfocusedContainerColor = Color.White.copy(alpha = 0.05f)
                            ),
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Generate Custom Quiz Button
                Button(
                    onClick = {
                        val numQuestions = numberOfQuestions.toIntOrNull() ?: 10
                        if (selectedSubject.isNotBlank() && selectedDifficulty.isNotBlank() && parentId != null && childState.id != null) {
                            isGenerating = true
                            generateError = null
                            generateSuccess = null
                            scope.launch {
                                val result = ApiClient.generateQuiz(
                                    parentId = parentId,
                                    kidId = childState.id!!,
                                    subject = selectedSubject,
                                    difficulty = selectedDifficulty,
                                    nbrQuestions = numQuestions,
                                    topic = customTopic.takeIf { it.isNotBlank() }
                                )
                                result.onSuccess { updatedChildResponse ->
                                    val updatedChild = updatedChildResponse.toChild()
                                    childState = childState.copy(
                                        quizzes = childState.quizzes + updatedChild.quizzes
                                    )
                                    val newQuiz = updatedChild.quizzes.lastOrNull()
                                    if (newQuiz != null) {
                                        onQuizGenerated(newQuiz)
                                    }
                                    generateSuccess = "Quiz generated: ${updatedChild.quizzes.lastOrNull()?.title ?: "New Quiz"}"
                                    isGenerating = false
                                }.onFailure { e ->
                                    generateError = e.message ?: "Failed to generate quiz"
                                    isGenerating = false
                                }
                            }
                        } else if (parentId == null || childState.id == null) {
                            generateError = "Missing parent or child ID"
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(100.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                    enabled = selectedSubject.isNotBlank() &&
                            selectedDifficulty.isNotBlank() &&
                            !isGenerating
                ) {
                    if (isGenerating) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color(0xFFAF7EE7),
                                strokeWidth = 2.dp
                            )
                            Text(
                                text = "GENERATING...",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E2E2E),
                                letterSpacing = 0.5.sp
                            )
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.taskcompleted), // Placeholder for sparkle
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "GENERATE CUSTOM QUIZ",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E2E2E),
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // QR Code Dialog
    if (showQRDialog) {
        QRCodeDialog(
            child = childState,
            qrBitmap = qrCodeBitmap,
            onDismiss = { 
                showQRDialog = false
                qrError = null
            }
        )
    }

    if (showPuzzleCreation) {
        ParentPuzzleCreationScreen(
            child = childState,
            onDismiss = { showPuzzleCreation = false },
            onCreated = { updatedChild ->
                showPuzzleCreation = false
                // Update the child state with the new puzzle data
                if (updatedChild != null) {
                    childState = updatedChild
                    // Save to session manager
                    sessionManager.updateChildSession(updatedChild)
                }
                // Optional: Show success snackbar
                generateSuccess = "Puzzle created successfully!"
            }
        )
    }

    if (showActivitiesDialog) {
        ActivitiesDialog(
            child = childState,
            parentId = parentId,
            onDismiss = { showActivitiesDialog = false },
            onActivityDeleted = { activityType, activityId ->
                // Update child state by removing the deleted activity
                childState = when (activityType) {
                    "quiz" -> childState.copy(
                        quizzes = childState.quizzes.filter { it.id != activityId }
                    )
                    "puzzle" -> childState.copy(
                        puzzles = childState.puzzles.filter { it.id != activityId }
                    )
                    else -> childState
                }
                // Save to session manager
                sessionManager.updateChildSession(childState)
                generateSuccess = "Activity deleted successfully!"
            }
        )
    }

    if (isGenerating) {
        AlertDialog(
            onDismissRequest = {},
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp),
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Generating Quiz",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E2E2E)
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFFAF7EE7),
                        strokeWidth = 3.dp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "This may take up to 2 minutes...",
                        fontSize = 14.sp,
                        color = Color(0xFF666666),
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {}
        )
    }

    // Error Snackbar
    qrError?.let { error ->
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
                Text(
                    text = error,
                    color = Color.White
                )
            }
        }
    }

    // Generate result snackbars
    generateError?.let { error ->
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

    generateSuccess?.let { msg ->
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
}


@Composable
fun InfoChip(
    iconResId: Int,
    text: String
) {
    Row(
        modifier = Modifier
            .background(
                color = Color(0xFFF5F5F5),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            contentScale = ContentScale.Fit
        )
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF666666)
        )
    }
}

@Composable
fun StatColumn(
    iconResId: Int,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = Color(0xFFAF7EE7).copy(alpha = 0.1f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                contentScale = ContentScale.Fit
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            fontSize = 20.sp,
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
fun SubjectOption(
    iconResId: Int,
    subject: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(
                    color = if (isSelected) Color(0xFFAF7EE7) else Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(12.dp)
                )
                .border(
                    width = if (isSelected) 2.dp else 0.dp,
                    color = if (isSelected) Color(0xFFAF7EE7) else Color.Transparent,
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                contentScale = ContentScale.Fit
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = subject,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color(0xFF2E2E2E) else Color(0xFF666666)
        )
    }
}

@Composable
fun DifficultyOptionCompact(
    difficulty: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .background(
                color = if (isSelected) Color(0xFFAF7EE7) else Color(0xFFF5F5F5),
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = difficulty,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else Color(0xFF666666)
        )
    }
}

@Composable
fun QRCodeDialog(
    child: Child,
    qrBitmap: Bitmap?,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(24.dp),
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${child.name}'s Login QR",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E2E2E),
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Scan this code on the child login screen",
                    fontSize = 14.sp,
                    color = Color(0xFF666666),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .border(
                            width = 2.dp,
                            color = Color(0xFFE0E0E0),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (qrBitmap != null) {
                        Image(
                            bitmap = qrBitmap.asImageBitmap(),
                            contentDescription = "QR Code",
                            modifier = Modifier
                                .size(200.dp)
                                .padding(8.dp),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(180.dp)
                                .background(
                                    color = Color(0xFFF5F5F5),
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFFAF7EE7),
                                strokeWidth = 3.dp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Child ID: ${child.id}",
                    fontSize = 12.sp,
                    color = Color(0xFF999999),
                    fontWeight = FontWeight.Medium
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFAF7EE7)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Close",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    )
}

@Composable
fun DecorativeElementsProfilem() {
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

@Composable
fun ProfileActionButton(
    iconResId: Int,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(90.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White.copy(alpha = 0.95f),
            disabledContainerColor = Color.White.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(22.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 0.dp
        ),
        enabled = enabled && !isLoading,
        contentPadding = PaddingValues(0.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color(0xFFAF7EE7),
                    strokeWidth = 2.dp
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = Color(0xFFAF7EE7).copy(alpha = 0.1f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = iconResId),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        contentScale = ContentScale.Fit
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = label,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E2E2E)
                )
            }
        }
    }
}

@Composable
fun ActivitiesDialog(
    child: Child,
    parentId: String?,
    onDismiss: () -> Unit,
    onActivityDeleted: (String, String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    var isDeleting by remember { mutableStateOf(false) }
    var deleteError by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

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
        DecorativeElementsProfilem()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // Header with back button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDismiss,
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
                    text = "${child.name}'s Activities",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 0.4.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tab Row
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.95f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    TabButton(
                        text = "Quizzes (${child.quizzes.size})",
                        isSelected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        modifier = Modifier.weight(1f)
                    )
                    TabButton(
                        text = "Puzzles (${child.puzzles.size})",
                        isSelected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Content based on selected tab
            when (selectedTab) {
                0 -> {
                    // Quizzes Tab
                    if (child.quizzes.isNotEmpty()) {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(child.quizzes) { quiz ->
                                QuizActivityCard(
                                    quiz = quiz,
                                    onDelete = {
                                        if (parentId != null && child.id != null) {
                                            isDeleting = true
                                            deleteError = null
                                            scope.launch {
                                                val result = ApiClient.deleteQuiz(parentId, child.id!!, quiz.id ?: "")
                                                result.onSuccess {
                                                    onActivityDeleted("quiz", quiz.id ?: "")
                                                    isDeleting = false
                                                }.onFailure { exception ->
                                                    deleteError = exception.message ?: "Failed to delete quiz"
                                                    isDeleting = false
                                                }
                                            }
                                        }
                                    },
                                    isDeleting = isDeleting
                                )
                            }
                        }
                    } else {
                        EmptyStateCard(
                            icon = "📚",
                            title = "No Quizzes Yet",
                            subtitle = "Create quizzes to see them here"
                        )
                    }
                }
                1 -> {
                    // Puzzles Tab
                    if (child.puzzles.isNotEmpty()) {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(child.puzzles) { puzzle ->
                                PuzzleActivityCard(
                                    puzzle = puzzle,
                                    onDelete = {
                                        if (parentId != null && child.id != null) {
                                            isDeleting = true
                                            deleteError = null
                                            scope.launch {
                                                val result = ApiClient.deletePuzzle(parentId, child.id!!, puzzle.id)
                                                result.onSuccess {
                                                    onActivityDeleted("puzzle", puzzle.id)
                                                    isDeleting = false
                                                }.onFailure { exception ->
                                                    deleteError = exception.message ?: "Failed to delete puzzle"
                                                    isDeleting = false
                                                }
                                            }
                                        }
                                    },
                                    isDeleting = isDeleting
                                )
                            }
                        }
                    } else {
                        EmptyStateCard(
                            icon = "🧩",
                            title = "No Puzzles Yet",
                            subtitle = "Create puzzles to see them here"
                        )
                    }
                }
            }
        }

        // Error Snackbar
        deleteError?.let { error ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                LaunchedEffect(error) {
                    kotlinx.coroutines.delay(4000)
                    deleteError = null
                }
                Snackbar(
                    action = {
                        TextButton(onClick = { deleteError = null }) {
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
    }
}

@Composable
fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFFAF7EE7) else Color.Transparent,
            contentColor = if (isSelected) Color.White else Color(0xFF666666)
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (isSelected) 2.dp else 0.dp
        )
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun QuizActivityCard(
    quiz: Quiz,
    onDelete: () -> Unit,
    isDeleting: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Quiz Icon
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        color = quiz.type.getBackgroundColor(),
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = quiz.type.getIconRes()),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    contentScale = ContentScale.Fit
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
                    color = Color(0xFF2E2E2E)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${quiz.questions.size} questions • ${quiz.type.name}",
                    fontSize = 13.sp,
                    color = Color(0xFF666666)
                )
                Spacer(modifier = Modifier.height(4.dp))
                
                // Status
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (quiz.isAnswered) Color(0xFF4CAF50).copy(alpha = 0.15f) else Color(0xFFFF9800).copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (quiz.isAnswered) "✅ Completed" else "⏳ Pending",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (quiz.isAnswered) Color(0xFF4CAF50) else Color(0xFFFF9800)
                        )
                    }
                    
                    if (quiz.score != null && quiz.score!! > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Score: ${quiz.score}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFAF7EE7)
                        )
                    }
                }
            }
            
            // Delete Button
            IconButton(
                onClick = onDelete,
                enabled = !isDeleting,
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = Color(0xFFD32F2F).copy(alpha = 0.1f),
                        shape = CircleShape
                    )
            ) {
                if (isDeleting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color(0xFFD32F2F),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFFD32F2F),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PuzzleActivityCard(
    puzzle: PuzzleResponse,
    onDelete: () -> Unit,
    isDeleting: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Puzzle Icon
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        color = puzzle.puzzleType.color.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🧩",
                    fontSize = 28.sp
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Puzzle Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = puzzle.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E2E2E)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${puzzle.puzzleDifficulty.displayName} • ${puzzle.pieces.size} pieces",
                    fontSize = 13.sp,
                    color = Color(0xFF666666)
                )
                Spacer(modifier = Modifier.height(4.dp))
                
                // Status
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (puzzle.isCompleted) Color(0xFF4CAF50).copy(alpha = 0.15f) else Color(0xFFFF9800).copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (puzzle.isCompleted) "✅ Completed" else "⏳ Pending",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (puzzle.isCompleted) Color(0xFF4CAF50) else Color(0xFFFF9800)
                        )
                    }
                    
                    if (puzzle.score > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Score: ${puzzle.score}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFAF7EE7)
                        )
                    }
                }
            }
            
            // Delete Button
            IconButton(
                onClick = onDelete,
                enabled = !isDeleting,
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = Color(0xFFD32F2F).copy(alpha = 0.1f),
                        shape = CircleShape
                    )
            ) {
                if (isDeleting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color(0xFFD32F2F),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFFD32F2F),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyStateCard(
    icon: String,
    title: String,
    subtitle: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
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
                text = icon,
                fontSize = 64.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E2E2E),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = Color(0xFF666666),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
fun ChildProfileQRScreenPreview() {
    val sampleChild = Child(
        id = "12345",
        name = "Emma",
        age = 8,
        level = "3",
        avatarEmoji = "👧",
        Score = 200,
    )

    ChildProfileQRScreen(
        child = sampleChild,
        parentId = "parent123"
    )
}