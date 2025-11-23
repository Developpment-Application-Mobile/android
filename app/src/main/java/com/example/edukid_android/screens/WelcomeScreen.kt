package com.example.edukid_android.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.edukid_android.R


@Composable
fun WelcomeScreen(
    navController: NavController

) {



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

        // Main content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // Decorative elements
            DecorativeElements()

            // Bottom content
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
            ) {
                // Title
                Text(
                    text = "Welcome\nto EduKid Academy!",
                    fontSize = 32.sp,
                    lineHeight = 38.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    letterSpacing = 0.4.sp,
                    modifier = Modifier.width(323.dp)
                )

                Spacer(modifier = Modifier.height(15.dp))

                // Subtitle
                Text(
                    text = "Play, Learn, and Explore with Exciting Quizzes!",
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White,
                    modifier = Modifier.width(269.dp)
                )

                Spacer(modifier = Modifier.height(34.dp))

                // Get Started Button
                Button(
                    onClick = {
                        navController.navigate("parentLogin")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(100.dp)
                ) {
                    Text(
                        text = "GET STARTED",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E2E2E),
                        letterSpacing = 0.4.sp
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { navController.navigate("childLogin") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(100.dp)
                ) {
                    Text(
                        text = "LOG IN AS CHILD",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E2E2E),
                        letterSpacing = 0.4.sp,

                        )
                }

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun BoxScope.DecorativeElements() {
    // Book and Globe - Center
    Image(
        painter = painterResource(id = R.drawable.book_and_globe),
        contentDescription = "Book and Globe",
        modifier = Modifier
            .size(426.dp)
            .align(Alignment.TopCenter)
            .offset(x = 0.5.dp, y = 79.dp),
        contentScale = ContentScale.Fit
    )

    // Education Book - Top Left
    Image(
        painter = painterResource(id = R.drawable.education_book),
        contentDescription = "Education Book",
        modifier = Modifier
            .size(224.dp)
            .offset(x = (-80).dp, y = (-18).dp),
        contentScale = ContentScale.Fit
    )

    // Book Stacks - Bottom Right (with blur)
    Image(
        painter = painterResource(id = R.drawable.book_stacks),
        contentDescription = "Book Stacks",
        modifier = Modifier
            .size(116.dp)
            .offset(x = 230.dp, y = 439.dp)
            .blur(2.dp),
        contentScale = ContentScale.Fit
    )

    // Coins 1 - Top Right
    Image(
        painter = painterResource(id = R.drawable.coins),
        contentDescription = "Coins",
        modifier = Modifier
            .size(123.dp)
            .offset(x = 267.dp, y = (-18).dp),
        contentScale = ContentScale.Fit
    )

    // Coins 2 - Top Center (flipped horizontally)
    Image(
        painter = painterResource(id = R.drawable.coins),
        contentDescription = "Coins",
        modifier = Modifier
            .size(53.dp)
            .offset(x = 154.dp, y = 52.dp)
            .graphicsLayer(scaleX = -1f),
        contentScale = ContentScale.Fit
    )

    // Coins 3 - Middle Right (rotated)
    Image(
        painter = painterResource(id = R.drawable.coins),
        contentDescription = "Coins",
        modifier = Modifier
            .size(36.dp)
            .offset(x = 286.57.dp, y = 206.57.dp)
            .rotate(28.68f),
        contentScale = ContentScale.Fit
    )

    // Coins 4 - Bottom Left (rotated)
    Image(
        painter = painterResource(id = R.drawable.coins),
        contentDescription = "Coins",
        modifier = Modifier
            .size(42.31.dp)
            .offset(x = 41.dp, y = 439.dp)
            .rotate(38.66f),
        contentScale = ContentScale.Fit
    )
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
fun WelcomeScreenPreview() {
//    WelcomeScreen()
}