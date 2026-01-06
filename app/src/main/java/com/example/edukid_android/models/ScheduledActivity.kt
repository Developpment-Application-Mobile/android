package com.example.edukid_android.models

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable
import java.util.*

// MARK: - Scheduled Activity Model
@Serializable
data class ScheduledActivity(
    val id: String = UUID.randomUUID().toString(),
    val childId: String,
    val activityType: ActivityType,
    val title: String,
    val description: String,
    val scheduledTime: Long, // Unix timestamp in milliseconds
    val duration: Long, // Duration in milliseconds
    var isCompleted: Boolean = false,
    var quizData: AIQuizResponse? = null,
    var gameType: GameType? = null,
    var puzzleType: PuzzleType? = null
) {
    val isAvailable: Boolean
        get() = System.currentTimeMillis() >= scheduledTime

    val timeRemaining: Long
        get() = maxOf(0, scheduledTime - System.currentTimeMillis())

    enum class ActivityType(val value: String) {
        QUIZ("quiz"),
        GAME("game"),
        PUZZLE("puzzle");

        val icon: String
            get() = when (this) {
                QUIZ -> "quiz"
                GAME -> "sports_esports"
                PUZZLE -> "extension"
            }

        val color: Color
            get() = when (this) {
                QUIZ -> Color(0xFFAF7EE7) // Purple
                GAME -> Color(0xFF4CAF50) // Green
                PUZZLE -> Color(0xFFFF9800) // Orange
            }
    }

    enum class GameType(val displayName: String) {
        MEMORY_MATCH("Memory Match"),
        COLOR_MATCH("Color Match"),
        SHAPE_MATCH("Shape Match"),
        NUMBER_SEQUENCE("Number Sequence"),
        MATH_QUIZ("Math Quiz"),
        EMOJI_MATCH("Emoji Match");

        val icon: String
            get() = when (this) {
                MEMORY_MATCH -> "psychology"
                COLOR_MATCH -> "palette"
                SHAPE_MATCH -> "category"
                NUMBER_SEQUENCE -> "format_list_numbered"
                MATH_QUIZ -> "calculate"
                EMOJI_MATCH -> "emoji_emotions"
            }

        val description: String
            get() = when (this) {
                MEMORY_MATCH -> "Match pairs of emojis"
                COLOR_MATCH -> "Find the correct color"
                SHAPE_MATCH -> "Match the shapes"
                NUMBER_SEQUENCE -> "Put numbers in order"
                MATH_QUIZ -> "Solve math problems"
                EMOJI_MATCH -> "Match emoji names"
            }
    }

    @Serializable
    data class PuzzleType(
        val id: String,
        val title: String,
        val description: String,
        val isLocal: Boolean
    )
}

// Sample AIQuizResponse for reference
@Serializable
data class AIQuizResponse(
    val id: String,
    val title: String,
    val subject: String,
    val difficulty: String,
    val questions: List<AIQuestion>,
    var isAnswered: Boolean = false,
    var score: Int = 0
) {
    val meaningfulTitle: String get() = title
}

@Serializable
data class AIQuestion(
    val id: String,
    val question: String,
    val options: List<String>,
    val correctAnswer: String
)
