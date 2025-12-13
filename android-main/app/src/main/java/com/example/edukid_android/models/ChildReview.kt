package com.example.edukid_android.models

data class ChildReview(
    val childName: String,
    val childAge: Int,
    val childLevel: String,
    val progressionLevel: Int,
    val totalQuizzes: Int,
    val overallAverage: Double,
    val lifetimeScore: Int,
    val currentScore: Int,
    val performanceByTopic: List<PerformanceByTopic>,
    val strengths: String,
    val weaknesses: String,
    val recommendations: List<String>,
    val summary: String,
    val generatedAt: String,
    val pdfBase64: String? = null
)

data class PerformanceByTopic(
    val topic: String,
    val quizzesCompleted: Int,
    val averageScore: Double,
    val highestScore: Int,
    val lowestScore: Int
)
