package com.example.edukid_android.screens

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.edukid_android.R
import com.example.edukid_android.models.Child
import com.example.edukid_android.models.ChildReview
import com.example.edukid_android.models.PerformanceByTopic
import com.example.edukid_android.utils.ApiClient
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import kotlin.onFailure

@Composable
fun ChildReviewScreen(
    child: Child,
    parentId: String?,
    onBackClick: () -> Unit = {}
) {
    var reviewData by remember { mutableStateOf<ChildReview?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isDownloadingPdf by remember { mutableStateOf(false) }
    var downloadSuccess by remember { mutableStateOf<String?>(null) }
    var downloadError by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Load review data on screen open
    LaunchedEffect(child.id, parentId) {
        if (parentId != null && child.id != null) {
            android.util.Log.d("ChildReviewScreen", "Loading review for parentId: $parentId, childId: ${child.id}")
            isLoading = true
            errorMessage = null
            val result = ApiClient.getChildReview(parentId, child.id)
            result.onSuccess { response ->
                android.util.Log.d("ChildReviewScreen", "Review loaded successfully")
                android.util.Log.d("ChildReviewScreen", "Response pdfBase64 present: ${response.pdfBase64 != null}, length: ${response.pdfBase64?.length ?: 0}")
                val mappedReview = response.toChildReview()
                android.util.Log.d("ChildReviewScreen", "Mapped review pdfBase64 present: ${mappedReview.pdfBase64 != null}, length: ${mappedReview.pdfBase64?.length ?: 0}")
                reviewData = mappedReview
                isLoading = false
            }.onFailure { e ->
                android.util.Log.e("ChildReviewScreen", "Failed to load review: ${e.message}", e)
                errorMessage = e.message ?: "Failed to load review"
                isLoading = false
            }
        } else {
            android.util.Log.e("ChildReviewScreen", "Missing IDs - parentId: $parentId, childId: ${child.id}")
            errorMessage = "Missing parent or child ID"
            isLoading = false
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
        // Decorative elements
        DecorativeElementsReview()

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
                    text = "Performance Review",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 0.4.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            when {
                isLoading -> {
                    // Loading state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 3.dp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Loading review...",
                                fontSize = 16.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
                errorMessage != null -> {
                    // Error state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White.copy(alpha = 0.95f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "⚠️",
                                    fontSize = 48.sp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Error Loading Review",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E2E2E)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = errorMessage ?: "Unknown error",
                                    fontSize = 14.sp,
                                    color = Color(0xFF666666),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
                reviewData != null -> {
                    // Success state - show review
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        ReviewContent(
                            review = reviewData!!,
                            onDownloadPdf = {
                                android.util.Log.d("ChildReviewScreen", "Download PDF clicked - pdfBase64 present: ${reviewData!!.pdfBase64 != null}")
                                if (reviewData!!.pdfBase64 != null && reviewData!!.pdfBase64!!.isNotBlank()) {
                                    isDownloadingPdf = true
                                    downloadError = null
                                    downloadSuccess = null
                                    scope.launch {
                                        try {
                                            // Decode base64 PDF data
                                            val base64String = reviewData!!.pdfBase64!!
                                            android.util.Log.d("ChildReviewScreen", "PDF base64 length: ${base64String.length}")
                                            val base64Pdf = if (base64String.startsWith("data:application/pdf")) {
                                                base64String.substringAfter(",")
                                            } else {
                                                base64String
                                            }
                                            
                                            val pdfBytes = android.util.Base64.decode(base64Pdf, android.util.Base64.DEFAULT)
                                            android.util.Log.d("ChildReviewScreen", "PDF decoded, size: ${pdfBytes.size} bytes")
                                            
                                            // Save PDF to cache and open
                                            val fileName = "review_${child.name}_${System.currentTimeMillis()}.pdf"
                                            val file = File(context.cacheDir, fileName)
                                            file.writeBytes(pdfBytes)
                                            
                                            val uri = FileProvider.getUriForFile(
                                                context,
                                                "${context.packageName}.provider",
                                                file
                                            )
                                            
                                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                                setDataAndType(uri, "application/pdf")
                                                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                                            }
                                            context.startActivity(intent)
                                            downloadSuccess = "PDF downloaded successfully"
                                        } catch (e: Exception) {
                                            android.util.Log.e("ChildReviewScreen", "PDF download error: ${e.message}", e)
                                            downloadError = "Failed to open PDF: ${e.message}"
                                        }
                                        isDownloadingPdf = false
                                    }
                                } else {
                                    android.util.Log.w("ChildReviewScreen", "PDF data not available in response")
                                    downloadError = "PDF export not available. The backend needs to include the 'pdfBase64' field in the review response."
                                }
                            },
                            isDownloadingPdf = isDownloadingPdf
                        )
                    }
                }
            }
        }

        // Download success/error snackbars
        downloadSuccess?.let { msg ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                LaunchedEffect(msg) {
                    kotlinx.coroutines.delay(3000)
                    downloadSuccess = null
                }
                Snackbar(
                    containerColor = Color(0xFF43A047),
                    contentColor = Color.White
                ) {
                    Text(text = msg, color = Color.White)
                }
            }
        }

        downloadError?.let { error ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                LaunchedEffect(error) {
                    kotlinx.coroutines.delay(4000)
                    downloadError = null
                }
                Snackbar(
                    action = {
                        TextButton(onClick = { downloadError = null }) {
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
fun ReviewContent(
    review: ChildReview,
    onDownloadPdf: () -> Unit,
    isDownloadingPdf: Boolean
) {
    // Child Info Header
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
            Text(
                text = "📋",
                fontSize = 48.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "${review.childName}'s Review",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E2E2E)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoChipReview(icon = "🎂", text = "${review.childAge} years")
                InfoChipReview(icon = "📊", text = "Level ${review.childLevel}")
                InfoChipReview(icon = "⭐", text = "Prog. ${review.progressionLevel}")
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Generated: ${formatDate(review.generatedAt)}",
                fontSize = 12.sp,
                color = Color(0xFF999999)
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Overall Statistics
    Text(
        text = "📊 Overall Statistics",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = Modifier.padding(bottom = 12.dp)
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
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
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatColumnReview(
                    icon = "📚",
                    value = "${review.totalQuizzes}",
                    label = "Total Quizzes"
                )
                VerticalDivider(
                    modifier = Modifier.height(60.dp),
                    color = Color(0xFFE0E0E0),
                    thickness = 1.dp
                )
                StatColumnReview(
                    icon = "⭐",
                    value = String.format("%.1f%%", review.overallAverage),
                    label = "Average"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color(0xFFE0E0E0))
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatColumnReview(
                    icon = "🏆",
                    value = "${review.lifetimeScore}",
                    label = "Lifetime Score"
                )
                VerticalDivider(
                    modifier = Modifier.height(60.dp),
                    color = Color(0xFFE0E0E0),
                    thickness = 1.dp
                )
                StatColumnReview(
                    icon = "💎",
                    value = "${review.currentScore}",
                    label = "Current Score"
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Performance by Topic
    if (review.performanceByTopic.isNotEmpty()) {
        Text(
            text = "📈 Performance by Topic",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        review.performanceByTopic.forEach { topic ->
            TopicPerformanceCard(topic)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }

    // Strengths
    if (review.strengths.isNotBlank()) {
        Text(
            text = "💪 Strengths",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF43A047).copy(alpha = 0.15f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = review.strengths,
                    fontSize = 14.sp,
                    color = Color.White,
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    // Weaknesses
    if (review.weaknesses.isNotBlank()) {
        Text(
            text = "🎯 Areas for Improvement",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFF9800).copy(alpha = 0.15f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = review.weaknesses,
                    fontSize = 14.sp,
                    color = Color.White,
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    // Recommendations
    if (review.recommendations.isNotEmpty()) {
        Text(
            text = "💡 AI Recommendations",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
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
                review.recommendations.forEachIndexed { index, recommendation ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment  = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(
                                    color = Color(0xFFAF7EE7),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = recommendation,
                            fontSize = 14.sp,
                            color = Color(0xFF2E2E2E),
                            lineHeight = 20.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (index < review.recommendations.size - 1) {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    // Summary
    if (review.summary.isNotBlank()) {
        Text(
            text = "📝 Summary",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
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
                Text(
                    text = review.summary,
                    fontSize = 14.sp,
                    color = Color(0xFF2E2E2E),
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    // Download PDF Button
    Button(
        onClick = onDownloadPdf,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(100.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
        enabled = !isDownloadingPdf
    ) {
        if (isDownloadingPdf) {
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
                    text = "DOWNLOADING...",
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
                Text(
                    text = "📥",
                    fontSize = 20.sp
                )
                Text(
                    text = "DOWNLOAD PDF REPORT",
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

@SuppressLint("DefaultLocale")
@Composable
fun TopicPerformanceCard(topic: PerformanceByTopic) {
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
            Text(
                text = topic.topic,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E2E2E)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${topic.quizzesCompleted}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFAF7EE7)
                    )
                    Text(
                        text = "Completed",
                        fontSize = 11.sp,
                        color = Color(0xFF666666)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = String.format("%.1f%%", topic.averageScore),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFAF7EE7)
                    )
                    Text(
                        text = "Average",
                        fontSize = 11.sp,
                        color = Color(0xFF666666)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${topic.highestScore}%",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF43A047)
                    )
                    Text(
                        text = "Highest",
                        fontSize = 11.sp,
                        color = Color(0xFF666666)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${topic.lowestScore}%",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF5722)
                    )
                    Text(
                        text = "Lowest",
                        fontSize = 11.sp,
                        color = Color(0xFF666666)
                    )
                }
            }
        }
    }
}

@Composable
fun InfoChipReview(icon: String, text: String) {
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
        Text(text = icon, fontSize = 14.sp)
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF666666)
        )
    }
}

@Composable
fun StatColumnReview(icon: String, value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = icon, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(4.dp))
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
fun DecorativeElementsReview() {
    Image(
        painter = painterResource(id = R.drawable.education_book),
        contentDescription = "Education Book",
        modifier = Modifier
            .size(100.dp)
            .offset(x = (-30).dp, y = 10.dp)
            .blur(1.dp)
    )

    Image(
        painter = painterResource(id = R.drawable.coins),
        contentDescription = "Coins",
        modifier = Modifier
            .size(60.dp)
            .offset(x = 300.dp, y = 30.dp)
    )

    Image(
        painter = painterResource(id = R.drawable.coins),
        contentDescription = "Coins",
        modifier = Modifier
            .size(50.dp)
            .offset(x = 20.dp, y = 700.dp)
            .rotate(38.66f)
    )
}

fun formatDate(isoDate: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = inputFormat.parse(isoDate)
        val outputFormat = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
        date?.let { outputFormat.format(it) } ?: isoDate
    } catch (_: Exception) {
        isoDate
    }
}
