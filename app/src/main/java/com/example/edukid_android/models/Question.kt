package com.example.edukid_android.models


data class Question(
//    val id: Int,
    val questionText: String,
    val options: List<String>, // e.g. ["A", "B", "C", "D"]
    val correctAnswerIndex: Int, // index in the options list
    val explanation: String? = null, // optional
    val imageUrl: String? = null, // optional image
    val type: QuestionType = QuestionType.MULTIPLE_CHOICE,
    val level: QuestionLevel = QuestionLevel.MEDIUM,
    val userAnswerIndex: Int? = null // optional user's answer

)

enum class QuestionType {
    MULTIPLE_CHOICE,
    TRUE_FALSE
}

enum class QuestionLevel {
    EASY,
    MEDIUM,
    HARD
}
