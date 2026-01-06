package com.example.edukid_android.repositories

import android.content.Context
import android.util.Log
import com.example.edukid_android.models.ScheduledActivity
import com.example.edukid_android.models.CreateScheduleRequest
import com.example.edukid_android.models.QuizDataDto
import com.example.edukid_android.models.PuzzleDataDto
import com.example.edukid_android.models.AIQuizResponse
import com.example.edukid_android.utils.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant

class ScheduledActivityRepository(context: Context) {
    // We'll use ApiClient instance. In a real DI app, this would be injected.
    private val apiService = ApiClient.apiService

    suspend fun getActivities(parentId: String, childId: String): List<ScheduledActivity> {
        return withContext(Dispatchers.IO) {
            try {
                if (parentId.isEmpty() || childId.isEmpty()) return@withContext emptyList()
                
                val response = apiService.getSchedulesForKid(parentId, childId)
                if (response.isSuccessful) {
                    response.body()?.map { it.toScheduledActivity() } ?: emptyList()
                } else {
                    Log.e("Repo", "Failed to fetch schedules: ${response.code()}")
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e("Repo", "Error fetching schedules", e)
                emptyList()
            }
        }
    }

    suspend fun saveActivity(parentId: String, activity: ScheduledActivity): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Map Domain -> DTO
                val req = CreateScheduleRequest(
                    parentId = parentId,
                    kidId = activity.childId,
                    activityType = activity.activityType.value,
                    title = activity.title,
                    description = activity.description,
                    scheduledTime = Instant.ofEpochMilli(activity.scheduledTime).toString(),
                    duration = activity.duration / 1000, // ms to seconds
                    quizData = activity.quizData?.let {
                        QuizDataDto(it.id, it.title, it.subject, it.difficulty, null)
                    },
                    gameType = activity.gameType?.let { 
                        // Map Enum -> backend string 
                        // Backend expects: "memoryMatch" etc. 
                        // Our enum: MEMORY_MATCH
                        // Camel case converter or manual map
                        when(it) {
                            ScheduledActivity.GameType.MEMORY_MATCH -> "memoryMatch"
                            ScheduledActivity.GameType.COLOR_MATCH -> "colorMatch"
                            ScheduledActivity.GameType.SHAPE_MATCH -> "shapeMatch"
                            ScheduledActivity.GameType.NUMBER_SEQUENCE -> "numberSequence"
                            ScheduledActivity.GameType.MATH_QUIZ -> "mathQuiz"
                            ScheduledActivity.GameType.EMOJI_MATCH -> "emojiMatch"
                        }
                    },
                    puzzleData = activity.puzzleType?.let {
                        PuzzleDataDto(it.id, it.title, it.description, it.isLocal)
                    }
                )

                val response = apiService.createSchedule(req)
                response.isSuccessful
            } catch (e: Exception) {
                Log.e("Repo", "Error saving schedule", e)
                false
            }
        }
    }

    suspend fun markActivityCompleted(activityId: String, score: Int = 0, timeSpent: Int = 0) {
        withContext(Dispatchers.IO) {
            try {
                // timeSpent in seconds for backend?
                apiService.markScheduleCompleted(activityId, mapOf("score" to score, "timeSpent" to timeSpent))
            } catch (e: Exception) {
                Log.e("Repo", "Error completing schedule", e)
            }
        }
    }

    suspend fun deleteActivity(activityId: String) {
        withContext(Dispatchers.IO) {
            try {
                apiService.deleteSchedule(activityId)
            } catch (e: Exception) {
                Log.e("Repo", "Error deleting schedule", e)
            }
        }
    }
    
    // Helper to fetch quizzes for selection
    suspend fun getQuizzes(childId: String): List<AIQuizResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getChildById(childId)
                if (response.isSuccessful) {
                    val child = response.body()?.toChild()
                    // Map Quiz -> AIQuizResponse
                    // Child model has 'Quiz' (local), but screens use 'AIQuizResponse'
                    // We need to map them if possible, or just treat them compatibly.
                    // Actually CreateActivityScreen expects AIQuizResponse.
                    // The Child object has 'quizzes: List<Quiz>'.
                    // We need to convert.
                    child?.quizzes?.map { q ->
                        AIQuizResponse(
                            id = q.id ?: "",
                            title = q.title,
                            subject = q.type.name, // Enum name as subject
                            difficulty = "Normal", // Default
                            questions = emptyList(), // Child model Quiz questions might be different type
                            isAnswered = q.isAnswered,
                            score = q.score ?: 0
                        )
                    } ?: emptyList()
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e("Repo", "Error fetching quizzes", e)
                emptyList()
            }
        }
    }
}

