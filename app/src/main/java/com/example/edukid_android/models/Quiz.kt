package com.example.edukid_android.models

import androidx.compose.ui.graphics.Color
import com.example.edukid_android.R

data class Quiz(
    val id: String? = null,
    val title: String,
    val type: QuizType,
    val isAnswered: Boolean = false,
    val score: Int? = null,
    val questions: List<Question>,
    val isScheduled: Boolean = false,
    val scheduledTime: Long = 0L,
    val isAvailable: Boolean = true
) {

    fun getCompletionPercentage(): Int {

        if (isAnswered) return 100
        else
        return 0
    }
}

enum class QuizType {
    MATH, SCIENCE, HISTORY, GEOGRAPHY, LITERATURE, GENERAL
}

// Extension function to get UI properties based on quiz type
fun QuizType.getIconRes(): Int {
    return when (this) {
        QuizType.MATH -> R.drawable.ic_math
        QuizType.SCIENCE -> R.drawable.ic_science
        QuizType.HISTORY -> R.drawable.ic_history
        QuizType.GEOGRAPHY -> R.drawable.ic_geography
        QuizType.LITERATURE -> R.drawable.ic_literature
        QuizType.GENERAL -> R.drawable.ic_general
    }
}

fun QuizType.getBackgroundColor(): Color {
    return when (this) {
        QuizType.MATH -> Color(0xFFE3F2FD)
        QuizType.SCIENCE -> Color(0xFFE8F5E9)
        QuizType.HISTORY -> Color(0xFFFFF3E0)
        QuizType.GEOGRAPHY -> Color(0xFFF3E5F5)
        QuizType.LITERATURE -> Color(0xFFFCE4EC)
        QuizType.GENERAL -> Color(0xFFE0F2F1)
    }
}

fun QuizType.getProgressColor(): Color {
    return when (this) {
        QuizType.MATH -> Color(0xFF2196F3)
        QuizType.SCIENCE -> Color(0xFF4CAF50)
        QuizType.HISTORY -> Color(0xFFFF9800)
        QuizType.GEOGRAPHY -> Color(0xFF9C27B0)
        QuizType.LITERATURE -> Color(0xFFE91E63)
        QuizType.GENERAL -> Color(0xFF009688)
    }
}
