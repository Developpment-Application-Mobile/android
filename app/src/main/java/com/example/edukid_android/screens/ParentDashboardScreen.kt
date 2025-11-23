package com.example.edukid_android.screens

import android.content.Context
import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edukid_android.R
import com.example.edukid_android.models.Child
import com.example.edukid_android.models.Parent
import com.example.edukid_android.utils.getAvatarResource


@Composable
fun ParentDashboardScreen(
    parent: Parent?,
    onAddChildClick: () -> Unit = {},
    onChildClick: (Child) -> Unit = {},
    onEditProfileClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFAF7EE7).copy(alpha = 0.6f), Color(0xFF272052)
                    ), center = Offset(200f, 200f), radius = 400f
                )
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // Decorative elements
            DecorativeElementsDashboard()

            // Main content
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Parent Dashboard",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 0.4.sp
                        )
                        Text(
                            text = "Manage your children's learning",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }

                    // Edit Profile button
                    IconButton(
                        onClick = onEditProfileClick, modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.2f), shape = CircleShape
                            )
                    ) {
                        Text(
                            text = "⚙️", fontSize = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Add Child Button
                Button(
                    onClick = onAddChildClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(100.dp)
                ) {
                    Text(
                        text = "➕ ADD NEW CHILD",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E2E2E),
                        letterSpacing = 0.4.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Children List
                val children = parent?.children ?: emptyList()
                if (children.isEmpty()) {
                    // Empty state
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "📚", fontSize = 60.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No children added yet",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Tap 'Add New Child' to get started",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.8f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    // Children cards
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(items = children, key = { it.id ?: it.name }) { child ->
                            ChildCard(
                                child = child, onClick = { onChildClick(child) },
                                context = LocalContext.current
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChildCard(
    context : Context,
    child: Child, onClick: () -> Unit
) {

    // Resolve avatar resource id: try existing helper first, then fallback to resolving drawable by name
    val avatarResId = remember(child.avatarEmoji) {
        // Try existing helper (keeps backward compatibility if helper already handles emojis)
        val resFromHelper = getAvatarResource(context, child.avatarEmoji)
        if (resFromHelper != 0) {
            resFromHelper
        } else {
            // Fallback: treat avatarEmoji as a drawable resource name (e.g. "avatar_27")
            // strip extension if present
            val resourceName = child.avatarEmoji.substringBeforeLast('.')
            val fallback = context.resources.getIdentifier(resourceName, "drawable", context.packageName)
            fallback
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(
                                color = Color(0xFFAF7EE7).copy(alpha = 0.2f), shape = CircleShape
                            ), contentAlignment = Alignment.Center
                    ) {
                        if (avatarResId != 0) {
                            Image(
                                painter = painterResource(id = avatarResId),
                                contentDescription = "Child Avatar",
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, Color(0xFFAF7EE7), CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            // fallback if avatar not found
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(Color.Gray.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("?", color = Color.White)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = child.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E2E2E)
                        )
                        Text(
                            text = "Age ${child.age} • Level ${child.level}",
                            fontSize = 14.sp,
                            color = Color(0xFF666666)
                        )
                    }
                }

                // Arrow icon
                Text(
                    text = "▶", fontSize = 20.sp, color = Color(0xFFAF7EE7)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Quiz Progress",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF666666)
                    )
                    Text(
                        text = "${child.getCompletedQuizzes().size}/${child.quizzes.size}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E2E2E)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = if (child.quizzes.isNotEmpty()) child.getCompletedQuizzes().size.toFloat() / child.quizzes.size.toFloat()
                    else 0f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFFAF7EE7),
                    trackColor = Color(0xFFE0E0E0)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                score(
                    label = "Score", value = "${child.Score}"
                )

                Divider(
                    modifier = Modifier
                        .width(1.dp)
                        .height(40.dp), color = Color(0xFFE0E0E0)
                )

                score(
                    label = "Completed", value = "${child.getCompletedQuizzes().size}",

                )
            }
        }
    }
}

@Composable
fun score(
    label: String, value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.coins),
                contentDescription = "Coins",
                modifier = Modifier
                    .size(30.dp)
//                    .offset(x = 290.dp, y = 10.dp)
                    ,
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E2E2E)
            )
        }
        Text(
            text = label, fontSize = 12.sp, color = Color(0xFF666666)
        )
    }
}

@Composable
fun BoxScope.DecorativeElementsDashboard() {
    // Education Book - Top Left (smaller)
    Image(
        painter = painterResource(id = R.drawable.education_book),
        contentDescription = "Education Book",
        modifier = Modifier
            .size(120.dp)
            .offset(x = (-40).dp, y = (-10).dp)
            .blur(1.dp),
        contentScale = ContentScale.Fit
    )

    // Coins - Top Right
    Image(
        painter = painterResource(id = R.drawable.coins),
        contentDescription = "Coins",
        modifier = Modifier
            .size(70.dp)
            .offset(x = 290.dp, y = 10.dp),
        contentScale = ContentScale.Fit
    )

    // Coins - Bottom Left
    Image(
        painter = painterResource(id = R.drawable.coins),
        contentDescription = "Coins",
        modifier = Modifier
            .size(50.dp)
            .offset(x = 20.dp, y = 720.dp)
            .rotate(38.66f),
        contentScale = ContentScale.Fit
    )
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
fun ParentDashboardScreenPreview() {
    val sampleChildren = listOf(
        Child(

            name = "Emma", age = 8, level = "3", avatarEmoji = "👧",

            Score = 85,
            id = "ffzf",
            quizzes = emptyList()
        ), Child(

            name = "Lucas",
            age = 6,
            level = "1",
            avatarEmoji = "👦",
            Score = 200,
            id = "efdc",
            quizzes = emptyList()
        )
    )
    val parent = Parent(
        name = "John Doe",
        email = "john.doe@example.com",
        children = sampleChildren,
        totalScore = 0,
        isActive = true
    )
    ParentDashboardScreen(parent = parent)
}