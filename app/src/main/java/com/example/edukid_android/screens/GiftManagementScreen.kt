package com.example.edukid_android.screens

import androidx.compose.animation.core.*
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edukid_android.models.Child
import com.example.edukid_android.models.ShopItem
import com.example.edukid_android.utils.ApiClient
import kotlinx.coroutines.launch

@Composable
fun GiftManagementScreen(
    child: Child,
    parentId: String? = null,
    onBackClick: () -> Unit = {}
) {
    var gifts by remember { mutableStateOf<List<ShopItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf<String?>(null) }
    
    // Add gift form state
    var showAddForm by remember { mutableStateOf(false) }
    var giftTitle by remember { mutableStateOf("") }
    var giftCost by remember { mutableStateOf("") }
    var isCreating by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()

    // Load gifts on screen start
    LaunchedEffect(parentId, child.id) {
        if (parentId != null && child.id != null) {
            isLoading = true
            val result = ApiClient.getGifts(parentId, child.id!!)
            result.onSuccess { giftList ->
                gifts = giftList
                isLoading = false
            }.onFailure { e ->
                error = e.message ?: "Failed to load gifts"
                isLoading = false
            }
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
                        text = "🎁 Gift Management",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.4.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Add Gift Button
                Button(
                    onClick = { showAddForm = !showAddForm },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (showAddForm) "✕" else "➕",
                            fontSize = 20.sp
                        )
                        Text(
                            text = if (showAddForm) "CANCEL" else "ADD NEW GIFT",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E2E2E),
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Add Gift Form
                if (showAddForm) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
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
                            Text(
                                text = "Gift Title",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF2E2E2E),
                                modifier = Modifier.padding(bottom = 10.dp)
                            )

                            OutlinedTextField(
                                value = giftTitle,
                                onValueChange = { giftTitle = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp),
                                placeholder = {
                                    Text(
                                        text = "e.g., 'Lego Set', 'Toy Car'",
                                        color = Color(0xFF999999),
                                        fontSize = 14.sp
                                    )
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color(0xFF2E2E2E),
                                    unfocusedTextColor = Color(0xFF2E2E2E),
                                    focusedBorderColor = Color(0xFFAF7EE7),
                                    unfocusedBorderColor = Color(0xFFE0E0E0),
                                    cursorColor = Color(0xFFAF7EE7),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                ),
                                shape = RoundedCornerShape(14.dp),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Cost",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF2E2E2E),
                                modifier = Modifier.padding(bottom = 10.dp)
                            )

                            OutlinedTextField(
                                value = giftCost,
                                onValueChange = {
                                    if (it.isEmpty() || (it.toIntOrNull() != null && it.toInt() > 0)) {
                                        giftCost = it
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp),
                                placeholder = {
                                    Text(
                                        text = "e.g., 500",
                                        color = Color(0xFF999999),
                                        fontSize = 14.sp
                                    )
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color(0xFF2E2E2E),
                                    unfocusedTextColor = Color(0xFF2E2E2E),
                                    focusedBorderColor = Color(0xFFAF7EE7),
                                    unfocusedBorderColor = Color(0xFFE0E0E0),
                                    cursorColor = Color(0xFFAF7EE7),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                ),
                                shape = RoundedCornerShape(14.dp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = {
                                    val costInt = giftCost.toIntOrNull()
                                    if (giftTitle.isNotBlank() && costInt != null && costInt > 0 && parentId != null && child.id != null) {
                                        isCreating = true
                                        scope.launch {
                                            val result = ApiClient.createGift(
                                                parentId = parentId,
                                                kidId = child.id!!,
                                                title = giftTitle,
                                                cost = costInt
                                            )
                                            result.onSuccess {
                                                val createdTitle = giftTitle
                                                // Reload gifts
                                                val reloadResult = ApiClient.getGifts(parentId, child.id!!)
                                                reloadResult.onSuccess { giftList ->
                                                    gifts = giftList
                                                }
                                                giftTitle = ""
                                                giftCost = ""
                                                showAddForm = false
                                                success = "Gift '${createdTitle}' created successfully!"
                                                isCreating = false
                                            }.onFailure { e ->
                                                error = e.message ?: "Failed to create gift"
                                                isCreating = false
                                            }
                                        }
                                    } else if (parentId == null || child.id == null) {
                                        error = "Missing parent or child ID"
                                    } else if (giftTitle.isBlank()) {
                                        error = "Please enter a gift title"
                                    } else if (costInt == null || costInt <= 0) {
                                        error = "Please enter a valid positive cost"
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFAF7EE7)
                                ),
                                shape = RoundedCornerShape(14.dp),
                                enabled = !isCreating && giftTitle.isNotBlank() && giftCost.toIntOrNull() != null && giftCost.toIntOrNull()!! > 0
                            ) {
                                if (isCreating) {
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
                                            text = "CREATING...",
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
                                        Text(
                                            text = "🎁",
                                            fontSize = 18.sp
                                        )
                                        Text(
                                            text = "CREATE GIFT",
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
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Gifts List
                Text(
                    text = "Gifts (${gifts.size})",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 16.dp),
                    letterSpacing = 0.5.sp
                )

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 3.dp
                        )
                    }
                } else if (gifts.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.95f)
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No gifts yet.\nAdd your first gift!",
                                fontSize = 16.sp,
                                color = Color(0xFF666666),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    gifts.forEach { gift ->
                        GiftItem(
                            gift = gift,
                            onDelete = {
                                if (parentId != null && child.id != null && gift.id != null) {
                                    scope.launch {
                                        val result = ApiClient.deleteGift(
                                            parentId = parentId,
                                            kidId = child.id!!,
                                            giftId = gift.id!!
                                        )
                                        result.onSuccess {
                                            // Reload gifts
                                            val reloadResult = ApiClient.getGifts(parentId, child.id!!)
                                            reloadResult.onSuccess { giftList ->
                                                gifts = giftList
                                            }
                                            success = "Gift deleted successfully"
                                        }.onFailure { e ->
                                            error = e.message ?: "Failed to delete gift"
                                        }
                                    }
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Error Snackbar
    error?.let { err ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            LaunchedEffect(err) {
                kotlinx.coroutines.delay(4000)
                error = null
            }
            Snackbar(
                action = {
                    TextButton(onClick = { error = null }) {
                        Text("Dismiss", color = Color.White)
                    }
                },
                containerColor = Color(0xFFD32F2F),
                contentColor = Color.White
            ) {
                Text(text = err, color = Color.White)
            }
        }
    }

    // Success Snackbar
    success?.let { msg ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            LaunchedEffect(msg) {
                kotlinx.coroutines.delay(3000)
                success = null
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
fun GiftItem(
    gift: ShopItem,
    onDelete: () -> Unit
) {
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
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            color = Color(0xFFAF7EE7).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🎁",
                        fontSize = 28.sp
                    )
                }

                Column {
                    Text(
                        text = gift.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E2E2E)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${gift.cost} points",
                        fontSize = 14.sp,
                        color = Color(0xFF666666)
                    )
                }
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = Color(0xFFD32F2F).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Text(
                    text = "🗑️",
                    fontSize = 20.sp
                )
            }
        }
    }
}

