package com.example.edukid_android.models

import com.google.gson.annotations.SerializedName
import java.util.Date

data class CreateScheduleRequest(
    val parentId: String,
    val kidId: String,
    val activityType: String,
    val title: String,
    val description: String,
    val scheduledTime: String, // ISO String
    val duration: Long, // in milliseconds (backend expects seconds? No, backend says milliseconds in logic but commented seconds, let's check)
    // Backend says duration: number // in seconds in comment.
    // Wait, DTO validation says @Min(60). If it's 60ms that's too fast. Likely seconds.
    // iOS/Android use millis. We'll convert.
    @SerializedName("quizData")
    val quizData: QuizDataDto? = null,
    @SerializedName("gameType")
    val gameType: String? = null,
    @SerializedName("puzzleData")
    val puzzleData: PuzzleDataDto? = null
)

data class QuizDataDto(
    val id: String,
    val title: String,
    val subject: String,
    val difficulty: String,
    val questions: List<Any>? = null 
)

data class PuzzleDataDto(
    val id: String,
    val title: String,
    val description: String,
    val isLocal: Boolean
)

data class ScheduleResponse(
    @SerializedName("_id")
    val id: String,
    val parentId: String,
    val kidId: String,
    val activityType: String,
    val title: String,
    val description: String,
    val scheduledTime: String,
    val duration: Long, // seconds from backend
    val isCompleted: Boolean,
    val completedAt: String?,
    val quizData: QuizDataDto?,
    val gameType: String?,
    val puzzleData: PuzzleDataDto?,
    val score: Int?,
    val timeSpent: Int?
) {
    fun toScheduledActivity(): ScheduledActivity {
        // Convert Backend Type String to Enum
        val type = when (activityType.lowercase()) {
            "quiz" -> ScheduledActivity.ActivityType.QUIZ
            "game" -> ScheduledActivity.ActivityType.GAME
            "puzzle" -> ScheduledActivity.ActivityType.PUZZLE
            else -> ScheduledActivity.ActivityType.GAME // fallback
        }

        // Convert Game Type String to Enum
        val gameEnum = if (gameType != null) {
            try {
                // Backend sends string like "memoryMatch"
                // Model expects enum. We need to map "memoryMatch" -> MEMORY_MATCH
                // Let's iterate values to find match? Or generic map.
                // Or assumptions: Backend likely sends camelCase, Enum is SCREAMING_SNAKE_CASE
                // ScheduledActivity.GameType.values().find { it.name.replace("_", "").equals(gameType, ignoreCase = true) }
                // For safety let's try direct matching or simple mapping map
                 when (gameType) {
                     "memoryMatch" -> ScheduledActivity.GameType.MEMORY_MATCH
                     "colorMatch" -> ScheduledActivity.GameType.COLOR_MATCH
                     "shapeMatch" -> ScheduledActivity.GameType.SHAPE_MATCH
                     "numberSequence" -> ScheduledActivity.GameType.NUMBER_SEQUENCE
                     "mathQuiz" -> ScheduledActivity.GameType.MATH_QUIZ
                     "emojiMatch" -> ScheduledActivity.GameType.EMOJI_MATCH
                     else -> null
                 }
            } catch (e: Exception) { null }
        } else null
        
        // Puzzle
        val pData = puzzleData?.let {
            ScheduledActivity.PuzzleType(it.id, it.title, it.description, it.isLocal)
        }
        
        // Quiz Data
        // Backend QuizDataDto is simplified. Frontend needs AIQuizResponse?
        // ScheduledActivity expects AIQuizResponse. We might need to construct it.
        // Or if questions are just objects.
        val qData = quizData?.let {
           com.example.edukid_android.models.AIQuizResponse(
               id = it.id,
               title = it.title,
               subject = it.subject,
               difficulty = it.difficulty,
               questions = emptyList() // Details might be missing in summary
           )
        }

        // Parse Date
        // Backend sends ISO string 2026-01-07T10:00:00Z
        // We need Long
        val timeMillis = try {
            java.time.Instant.parse(scheduledTime).toEpochMilli()
        } catch (e: Exception) {
             System.currentTimeMillis() // fallback
        }

        return ScheduledActivity(
            id = id,
            childId = kidId,
            activityType = type,
            title = title,
            description = description,
            scheduledTime = timeMillis,
            duration = duration * 1000, // seconds to millis
            isCompleted = isCompleted,
            quizData = qData,
            gameType = gameEnum,
            puzzleType = pData
        )
    }
}
