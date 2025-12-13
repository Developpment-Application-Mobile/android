package com.example.edukid_android.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
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
import com.example.edukid_android.R
import com.example.edukid_android.models.Child
import com.example.edukid_android.utils.ApiClient
import com.example.edukid_android.utils.QRScannerComposable
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

@Composable
fun ChildQRLoginScreen(
    onQRScanned: (Child) -> Unit = {},
    onBackClick: () -> Unit = {}

) {
    var showScanner by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            showScanner = true
        } else {
            // Permission denied handling if needed
        }
    }
    
    fun handleQRScanned(qrCode: String) {
        isLoading = true
        errorMessage = null
        scope.launch {
            val result = ApiClient.getChildById(qrCode)
            result.onSuccess { childResponse ->
                val child = childResponse.toChild()
                isLoading = false
                onQRScanned(child)
            }.onFailure { exception ->
                isLoading = false
                errorMessage = exception.message ?: "Failed to load child data"
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
            // Decorative elements
            DecorativeElementsChildLogin()

            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title
                Text(
                    text = "Welcome,\nKid Explorer!",
                    fontSize = 36.sp,
                    lineHeight = 42.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 0.4.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Scan your QR code to start learning",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(50.dp))

                // QR Code Scanner Frame
                Box(
                    modifier = Modifier
                        .size(280.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .border(
                            width = 4.dp,
                            color = Color.White,
                            shape = RoundedCornerShape(24.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Scanner corners decoration
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Top-left corner
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(12.dp)
                                .size(40.dp)
                                .border(
                                    width = 4.dp,
                                    color = Color(0xFFFFD700),
                                    shape = RoundedCornerShape(
                                        topStart = 8.dp,
                                        topEnd = 0.dp,
                                        bottomStart = 0.dp,
                                        bottomEnd = 0.dp
                                    )
                                )
                        )

                        // Top-right corner
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp)
                                .size(40.dp)
                                .border(
                                    width = 4.dp,
                                    color = Color(0xFFFFD700),
                                    shape = RoundedCornerShape(
                                        topStart = 0.dp,
                                        topEnd = 8.dp,
                                        bottomStart = 0.dp,
                                        bottomEnd = 0.dp
                                    )
                                )
                        )

                        // Bottom-left corner
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(12.dp)
                                .size(40.dp)
                                .border(
                                    width = 4.dp,
                                    color = Color(0xFFFFD700),
                                    shape = RoundedCornerShape(
                                        topStart = 0.dp,
                                        topEnd = 0.dp,
                                        bottomStart = 8.dp,
                                        bottomEnd = 0.dp
                                    )
                                )
                        )

                        // Bottom-right corner
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(12.dp)
                                .size(40.dp)
                                .border(
                                    width = 4.dp,
                                    color = Color(0xFFFFD700),
                                    shape = RoundedCornerShape(
                                        topStart = 0.dp,
                                        topEnd = 0.dp,
                                        bottomStart = 0.dp,
                                        bottomEnd = 8.dp
                                    )
                                )
                        )
                    }

                    if (showScanner) {
                        QRScannerComposable(
                            showScanner = true,
                            onResult = { qrCode ->
                                qrCode?.let { 
                                    handleQRScanned(it)
                                    showScanner = false
                                }
                            },
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(24.dp))
                        )
                    } else {
                        // Camera preview placeholder
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "📷",
                                fontSize = 60.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Position QR code\nwithin frame",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Instruction text
                Text(
                    text = "Ask your parent for the QR code",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center
                )
                
                // Error message
                errorMessage?.let { error ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = error,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFFF6B6B),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Scan button
                Button(
                    onClick = { /* Open camera scanner */
                        // Request permission and show scanner
                        val hasPermission = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                        if (hasPermission) {
                            showScanner = true
                        } else {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(100.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color(0xFFAF7EE7),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "OPEN SCANNER",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E2E2E),
                            letterSpacing = 0.4.sp
                        )
                    }
                }
                

                Spacer(modifier = Modifier.height(16.dp))

                // Back button
                OutlinedButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        2.dp,
                        Color.White
                    ),
                    shape = RoundedCornerShape(100.dp)
                ) {
                    Text(
                        text = "BACK TO HOME",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.4.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun BoxScope.DecorativeElementsChildLogin() {
    // Book and Globe - Top Center
    Image(
        painter = painterResource(id = R.drawable.book_and_globe),
        contentDescription = "Book and Globe",
        modifier = Modifier
            .size(180.dp)
            .align(Alignment.TopCenter)
            .offset(x = 0.dp, y = (-30).dp)
            .blur(1.dp),
        contentScale = ContentScale.Fit
    )

    // Education Book - Top Left
    Image(
        painter = painterResource(id = R.drawable.education_book),
        contentDescription = "Education Book",
        modifier = Modifier
            .size(140.dp)
            .offset(x = (-40).dp, y = 0.dp)
            .blur(1.dp),
        contentScale = ContentScale.Fit
    )

    // Coins - Top Right
    Image(
        painter = painterResource(id = R.drawable.coins),
        contentDescription = "Coins",
        modifier = Modifier
            .size(90.dp)
            .offset(x = 280.dp, y = 20.dp),
        contentScale = ContentScale.Fit
    )

    // Coins - Top Left
    Image(
        painter = painterResource(id = R.drawable.coins),
        contentDescription = "Coins",
        modifier = Modifier
            .size(60.dp)
            .offset(x = 30.dp, y = 80.dp)
            .rotate(15f)
            .graphicsLayer(scaleX = -1f),
        contentScale = ContentScale.Fit
    )

    // Book Stacks - Bottom Right
    Image(
        painter = painterResource(id = R.drawable.book_stacks),
        contentDescription = "Book Stacks",
        modifier = Modifier
            .size(110.dp)
            .offset(x = 250.dp, y = 680.dp)
            .blur(2.dp),
        contentScale = ContentScale.Fit
    )

    // Coins - Bottom Left
    Image(
        painter = painterResource(id = R.drawable.coins),
        contentDescription = "Coins",
        modifier = Modifier
            .size(50.dp)
            .offset(x = 20.dp, y = 700.dp)
            .rotate(38.66f),
        contentScale = ContentScale.Fit
    )

    // Coins - Middle Right
    Image(
        painter = painterResource(id = R.drawable.coins),
        contentDescription = "Coins",
        modifier = Modifier
            .size(45.dp)
            .offset(x = 300.dp, y = 400.dp)
            .rotate(28.68f),
        contentScale = ContentScale.Fit
    )
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
fun ChildQRLoginScreenPreview() {
//    ChildQRLoginScreen(null)
}

