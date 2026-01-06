package com.example.edukid_android.screens

//ParentPuzzleCreationScreen.kt
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edukid_android.R
import com.example.edukid_android.models.*
import com.example.edukid_android.utils.ApiClient
import com.example.edukid_android.utils.getAvatarResource
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentPuzzleCreationScreen(
    child: Child,
    onDismiss: () -> Unit,
    onCreated: (Child?) -> Unit
) {
    // Fixed to IMAGE type and 4x4 grid (HARD difficulty)
    val selectedType = PuzzleType.IMAGE
    val selectedDifficulty = PuzzleDifficulty.HARD // 4x4 grid
    var isCreating by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Puzzle") },
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
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Child Info Card
                ChildInfoCard(child = child)

                // Info Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            Icons.Default.Image,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = Color(0xFFAF7EE7)
                        )
                        
                        Text(
                            text = "AI-Generated Puzzle",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        
                        Text(
                            text = "Our AI will create a unique 4×4 image puzzle for ${child.name}",
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }

                // Error Message
                if (errorMessage != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF44336).copy(alpha = 0.2f)
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
                                Icons.Default.Error,
                                contentDescription = null,
                                tint = Color(0xFFF44336)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = errorMessage ?: "",
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // Generate Button
                Button(
                    onClick = {
                        isCreating = true
                        errorMessage = null
                        
                        scope.launch {
                            try {
                                val parentId = child.parentId ?: ""
                                val result = ApiClient.generatePuzzle(
                                    parentId = parentId,
                                    kidId = child.id,
                                    type = selectedType.value,
                                    difficulty = selectedDifficulty.value,
                                    gridSize = selectedDifficulty.gridSize,
                                    topic = null
                                )
                                
                                result.onSuccess { childResponse ->
                                    isCreating = false
                                    val updatedChild = childResponse.toChild()
                                    onCreated(updatedChild)
                                }.onFailure { error ->
                                    isCreating = false
                                    errorMessage = error.message ?: "Failed to create puzzle"
                                }
                            } catch (e: Exception) {
                                isCreating = false
                                errorMessage = e.message ?: "An error occurred"
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFAF7EE7)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !isCreating
                ) {
                    if (isCreating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                        Spacer(Modifier.width(12.dp))
                        Text("Generating...", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(28.dp))
                        Spacer(Modifier.width(12.dp))
                        Text("Generate Puzzle", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ChildInfoCard(child: Child) {
    val context = LocalContext.current
    val avatarResId = remember(child.avatarEmoji) {
        val res = getAvatarResource(context, child.avatarEmoji)
        if (res != 0) res else getAvatarResource(context, "avatar_3")
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(Color.White.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (avatarResId != 0) {
                    Image(
                        painter = painterResource(id = avatarResId),
                        contentDescription = child.name,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.avatar_3),
                        contentDescription = child.name,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Column {
                Text(
                    text = "Creating puzzle for:",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Text(
                    text = child.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

// Helper function to generate puzzle pieces
private fun generatePuzzlePieces(gridSize: Int): List<LocalPuzzlePiece> {
    val totalPieces = gridSize * gridSize
    val pieces = mutableListOf<LocalPuzzlePiece>()
    
    for (i in 0 until totalPieces) {
        pieces.add(
            LocalPuzzlePiece(
                id = i,
                correctPosition = i,
                currentPosition = i,
                content = (i + 1).toString()
            )
        )
    }
    
    return pieces
}
