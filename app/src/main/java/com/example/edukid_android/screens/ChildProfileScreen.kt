package com.example.edukid_android.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.edukid_android.R
import com.example.edukid_android.components.BottomNavigationBar
import com.example.edukid_android.models.Child
import com.example.edukid_android.utils.getAvatarResource

@Composable
fun ChildProfileScreen(
    navController: NavController? = null,
    child: Child? = null,
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    
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
        DecorativeElementsProfile()

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Main content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                // Profile Header
                Text(
                    text = "My Profile",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 0.4.sp,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Profile Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.95f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFFAF7EE7).copy(alpha = 0.2f),
                                            Color(0xFFAF7EE7).copy(alpha = 0.1f)
                                        )
                                    ),
                                    shape = CircleShape
                                )
                                .border(4.dp, Color(0xFFAF7EE7), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (child != null) {
                                Image(
                                    painter = painterResource(id = getAvatarResource(context, child.avatarEmoji)),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(96.dp)
                                        .border(2.dp, Color(0xFFAF7EE7), CircleShape)
                                        .background(Color.White, CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Name
                        Text(
                            text = child?.name ?: "Unknown",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E2E2E)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Age and Level
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            InfoChipProfile(
                                icon = "🎂",
                                text = "${child?.age ?: 0} years old"
                            )
                            InfoChipProfile(
                                icon = "📊",
                                text = "Level ${child?.level ?: "1"}"
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Divider(color = Color(0xFFE0E0E0))

                        Spacer(modifier = Modifier.height(24.dp))

                        // Stats
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatColumnProfile(
                                icon = "📚",
                                value = "${child?.quizzes?.size ?: 0}",
                                label = "Total Quizzes"
                            )

                            VerticalDivider(
                                modifier = Modifier.height(60.dp),
                                color = Color(0xFFE0E0E0),
                                thickness = 1.dp
                            )

                            StatColumnProfile(
                                icon = "✅",
                                value = "${child?.getCompletedQuizzes()?.size ?: 0}",
                                label = "Completed"
                            )

                            VerticalDivider(
                                modifier = Modifier.height(60.dp),
                                color = Color(0xFFE0E0E0),
                                thickness = 1.dp
                            )

                            StatColumnProfile(
                                icon = "⭐",
                                value = "${child?.Score ?: 0}",
                                label = "Score"
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Divider(color = Color(0xFFE0E0E0))

                        Spacer(modifier = Modifier.height(24.dp))

                        // Puzzles Stats
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatColumnProfile(
                                icon = "🧩",
                                value = "${child?.puzzles?.size ?: 0}",
                                label = "Total Puzzles"
                            )

                            VerticalDivider(
                                modifier = Modifier.height(60.dp),
                                color = Color(0xFFE0E0E0),
                                thickness = 1.dp
                            )

                            StatColumnProfile(
                                icon = "🏆",
                                value = "${child?.puzzles?.count { it.isCompleted } ?: 0}",
                                label = "Completed"
                            )

                            VerticalDivider(
                                modifier = Modifier.height(60.dp),
                                color = Color(0xFFE0E0E0),
                                thickness = 1.dp
                            )

                            StatColumnProfile(
                                icon = "🚀",
                                value = "${child?.progressionLevel ?: 1}",
                                label = "Progress Lvl"
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Premium Logout Button
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFFFF5252),
                                        Color(0xFFD32F2F)
                                    )
                                ),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable { onLogout() },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(
                                        Color.White.copy(alpha = 0.2f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "→",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Text(
                                text = "Sign Out",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(100.dp)) // Space for bottom nav
            }

            // Bottom Navigation
            BottomNavigationBar(
                currentRoute = "childProfile",
                onNavigate = { route ->
                    when (route) {
                        "childHome" -> navController?.navigate("childHome")
                        "childGames" -> navController?.navigate("childGames")
                        "childProfile" -> {} // Already here
                    }
                }
            )
        }
    }
}

@Composable
fun InfoChipProfile(icon: String, text: String) {
    Row(
        modifier = Modifier
            .background(
                color = Color(0xFFF5F5F5),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(text = icon, fontSize = 16.sp)
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF666666)
        )
    }
}

@Composable
fun StatColumnProfile(icon: String, value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = icon, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E2E2E)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF666666),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun DecorativeElementsProfile() {
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