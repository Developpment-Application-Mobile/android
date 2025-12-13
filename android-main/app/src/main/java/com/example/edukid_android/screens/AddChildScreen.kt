package com.example.edukid_android.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.edukid_android.R
import com.example.edukid_android.models.Parent
import com.example.edukid_android.utils.ApiClient
import kotlinx.coroutines.launch

@Composable
fun AddChildScreen(
    parentId: String? = null,
    onAddChildSuccess: (Parent) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var selectedAvatarIndex by remember { mutableStateOf(0) } // store index instead of emoji
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // 🔹 Load avatar drawable resource IDs dynamically
    val avatarResIds = remember {
        (1..42).mapNotNull { index ->
            val resName = "avatar_$index"
            val resId = try {
                R.drawable::class.java.getField(resName).getInt(null)
            } catch (e: Exception) {
                null
            }
            resId
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
                    center = androidx.compose.ui.geometry.Offset(200f, 200f),
                    radius = 400f
                )
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            DecorativeElementsAddChild()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(100.dp))

                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
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

                    Column {
                        Text(
                            text = "Add New Child",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 0.4.sp
                        )
                        Text(
                            text = "Create a profile for your child",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 🔹 Avatar selection section
                Text(
                    text = "Choose an Avatar",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.95f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Show selected avatar
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .align(Alignment.CenterHorizontally)
                                .background(
                                    color = Color(0xFFAF7EE7).copy(alpha = 0.2f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = avatarResIds[selectedAvatarIndex]),
                                contentDescription = "Selected Avatar",
                                modifier = Modifier.size(80.dp),
                                contentScale = ContentScale.Fit
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Display avatars in grid (rows of 6)
                        val chunkedAvatars = avatarResIds.chunked(6)
                        chunkedAvatars.forEach { row ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                row.forEachIndexed { _, resId ->
                                    AvatarOptionImage(
                                        resId = resId,
                                        isSelected = avatarResIds[selectedAvatarIndex] == resId,
                                        onClick = {
                                            selectedAvatarIndex = avatarResIds.indexOf(resId)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Name input
                Text(
                    text = "Child's Name",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    placeholder = {
                        Text(
                            text = "Enter child's name",
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        cursorColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Age input
                Text(
                    text = "Age",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = age,
                    onValueChange = {
                        if (it.isEmpty() || (it.toIntOrNull() != null && it.toInt() in 1..18)) {
                            age = it
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    placeholder = {
                        Text(
                            text = "Enter age (1–18)",
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        cursorColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Error message
                errorMessage?.let { error ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFF5252).copy(alpha = 0.9f)
                        )
                    ) {
                        Text(
                            text = error,
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Submit button
                Button(
                    onClick = {
                        val ageInt = age.toIntOrNull() ?: 0
                        if (name.isNotBlank() && ageInt > 0 && parentId != null) {
                            errorMessage = null
                            isLoading = true

                            // 🔹 Get the selected avatar resource name reliably from the resId
                            val avatarResName = try {
                                context.resources.getResourceEntryName(avatarResIds[selectedAvatarIndex])
                            } catch (e: Exception) {
                                "avatar_3"
                            }

                            scope.launch {
                                val result = ApiClient.addChild(
                                    parentId = parentId,
                                    name = name,
                                    age = ageInt,
                                    avatarEmoji = avatarResName // ✅ send avatar name to backend
                                )

                                isLoading = false

                                result.onSuccess { parentResponse ->
                                    val updatedParent = parentResponse.toParent()
                                    onAddChildSuccess(updatedParent)
                                }.onFailure { exception ->
                                    errorMessage = exception.message ?: "Failed to add child. Please try again."
                                }
                            }
                        } else if (parentId == null) {
                            errorMessage = "Parent ID is missing. Please log in again."
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(100.dp),
                    enabled = !isLoading && name.isNotBlank() && age.isNotBlank() && parentId != null
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color(0xFF2E2E2E),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "ADD CHILD",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E2E2E),
                            letterSpacing = 0.4.sp
                        )
                    }
                }


                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}


@Composable
fun BoxScope.DecorativeElementsAddChild() {
    // Education Book - Top Left (smaller)
    Image(
        painter = painterResource(id = R.drawable.education_book),
        contentDescription = "Education Book",
        modifier = Modifier
            .size(150.dp)
            .offset(x = (-50).dp, y = (-10).dp)
            .blur(1.dp),
        contentScale = ContentScale.Fit
    )

    // Coins - Top Right
    Image(
        painter = painterResource(id = R.drawable.coins),
        contentDescription = "Coins",
        modifier = Modifier
            .size(80.dp)
            .offset(x = 280.dp, y = 20.dp),
        contentScale = ContentScale.Fit
    )

    // Book Stacks - Bottom Right
    Image(
        painter = painterResource(id = R.drawable.book_stacks),
        contentDescription = "Book Stacks",
        modifier = Modifier
            .size(100.dp)
            .offset(x = 250.dp, y = 650.dp)
            .blur(2.dp),
        contentScale = ContentScale.Fit
    )

    // Coins - Bottom Left
    Image(
        painter = painterResource(id = R.drawable.coins),
        contentDescription = "Coins",
        modifier = Modifier
            .size(40.dp)
            .offset(x = 30.dp, y = 700.dp)
            .rotate(38.66f),
        contentScale = ContentScale.Fit
    )
}
// 🔹 New composable: avatar selection using drawable images
@Composable
fun AvatarOptionImage(
    resId: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(
                color = if (isSelected) Color(0xFFAF7EE7).copy(alpha = 0.2f) else Color.Transparent
            )
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) Color(0xFFAF7EE7) else Color(0xFFE0E0E0),
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = resId),
            contentDescription = "Avatar",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
fun AddChildScreenPreview() {
    AddChildScreen()
}
