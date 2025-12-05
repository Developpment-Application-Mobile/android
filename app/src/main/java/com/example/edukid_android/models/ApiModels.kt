package com.example.edukid_android.models

import com.google.gson.annotations.SerializedName

// Request model for sign up
data class SignUpRequest(
    val name: String,
    val email: String,
    val password: String
)

// Request model for login
data class LoginRequest(
    val email: String,
    val password: String
)

// Request model for forgot password
data class ForgotPasswordRequest(
    val email: String
)

// Request model for reset password
data class ResetPasswordRequest(
    val token: String,
    val newPassword: String
)

data class UpdateParentRequest(
    val name: String,
    val email: String,
    val password: String
)

// Request model for adding a child
data class AddChildRequest(
    val name: String,
    val age: Int,
    val level: String,
    val avatarEmoji : String?
)

// Request model for generating a quiz for a child
data class GenerateQuizRequest(
    val subject: String,
    val difficulty: String,
    val nbrQuestions: Int,
    val topic: String? = null
)

//data class UpdateQuizRequest(
//    val answered: Int,
//    val score: Int
//)

data class SubmitAnswersRequest(
    val answers: List<Int>
)

data class CreateGiftRequest(
    val title: String,
    val cost: Int
)

// Response model for sign up
data class SignUpResponse(
    @SerializedName("access_token")
    val accessToken: String,
    val parent: ParentResponse
)

// Child response model from API
data class ChildResponse(
    val name: String?,
    val age: Int?,
    val level: String?,
    @SerializedName("avatarEmoji")
    val avatarEmoji: String? = null,
    val quizzes: List<QuizResponse>? = emptyList(),
    @SerializedName("Score")
    val score: Int? = 0,
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("parentId")
    val parentId: String? = null,
    @SerializedName("lifetimeScore")
    val lifetimeScore: Int? = 0,
    @SerializedName("progressionLevel")
    val progressionLevel: Int? = 1,
    @SerializedName("inventory")
    val inventory: List<InventoryItemResponse>? = emptyList(),
    @SerializedName("shopCatalog")
    val shopCatalog: List<ShopItemResponse>? = emptyList()
) {
    fun toChild(): Child {
        return Child(
            name = this.name ?: "",
            age = this.age ?: 0,
            level = this.level ?: "",
            avatarEmoji = this.avatarEmoji ?: "avatar_3",
            quizzes = this.quizzes?.map { it.toQuiz() } ?: emptyList(),
            Score = this.score ?: 0,
            id = this.id,
            parentId = this.parentId,
            lifetimeScore = this.lifetimeScore ?: 0,
            progressionLevel = this.progressionLevel ?: 1,
            inventory = this.inventory?.map { it.toInventoryItem() } ?: emptyList(),
            shopCatalog = this.shopCatalog?.map { it.toShopItem() } ?: emptyList()
        )
    }
}

data class InventoryItemResponse(
    val title: String? = null,
    val cost: Int? = 0,
    val purchasedAt: String? = null
) {
    fun toInventoryItem(): InventoryItem {
        return InventoryItem(
            title = this.title ?: "",
            cost = this.cost ?: 0,
            purchasedAt = this.purchasedAt ?: ""
        )
    }
}

data class ShopItemResponse(
    @SerializedName("_id")
    val id: String? = null,
    val title: String? = null,
    val cost: Int? = 0
) {
    fun toShopItem(): ShopItem {
        return ShopItem(
            id = this.id,
            title = this.title ?: "",
            cost = this.cost ?: 0
        )
    }
}

data class GiftResponse(
    @SerializedName("_id")
    val id: String? = null,
    val title: String? = null,
    val cost: Int? = 0
) {
    fun toGift(): ShopItem {
        return ShopItem(
            id = this.id,
            title = this.title ?: "",
            cost = this.cost ?: 0
        )
    }
}

// Parent response model from API
data class ParentResponse(
    val name: String,
    val email: String,
    val children: List<ChildResponse> = emptyList(),
    @SerializedName("totalScore")
    val totalScore: Int = 0,
    @SerializedName("isActive")
    val isActive: Boolean = true,
    @SerializedName("_id")
    val id: String,
    @SerializedName("createdAt")
    val createdAt: String? = null,
    @SerializedName("updatedAt")
    val updatedAt: String? = null,
    @SerializedName("__v")
    val version: Int = 0
    ) {
        // Convert ParentResponse to Parent model
        fun toParent(): Parent {
            return Parent(
                name = this.name,
                email = this.email,
                phoneNumber = "", // Not provided in API response
                profileImageUrl = "", // Not provided in API response
                children = this.children.map { it.toChild() },
                totalScore = this.totalScore,
                isActive = this.isActive,
                id = this.id
            )
        }
    }

// Nested quiz response models parsed by Gson
data class QuizResponse(
    val title: String? = null,
    val questions: List<QuestionResponse>? = emptyList(),
    val isAnswered: Boolean? = false,
    val score: Int? = null,
    val type: String? = null,
    @SerializedName("_id")
    val id: String? = null
) {
    fun toQuiz(): Quiz {
        val quizType = sanitizeQuizType(type)
        return Quiz(
            id = id,
            title = title ?: "",
            questions = questions?.map { it.toQuestion() } ?: emptyList(),
            isAnswered = isAnswered ?: false,
            type = quizType,
            score = score ?: 0
        )
    }

    private fun sanitizeQuizType(raw: String?): QuizType {
        val normalized = raw?.replace('-', '_')?.replace(' ', '_')?.uppercase()
        return try {
            if (normalized != null) QuizType.valueOf(normalized) else QuizType.GENERAL
        } catch (_: Exception) {
            QuizType.GENERAL
        }
    }
}

data class QuestionResponse(
    @SerializedName("questionText") val questionText: String? = null,
    val options: List<String>? = emptyList(),
    @SerializedName("correctAnswerIndex") val correctAnswerIndex: Int? = 0,
    val explanation: String? = null,
    val imageUrl: String? = null,
    val type: String? = null,
    val level: String? = null
) {
    fun toQuestion(): Question {
        return Question(
            questionText = questionText ?: "",
            options = options ?: emptyList(),
            correctAnswerIndex = correctAnswerIndex ?: 0,
            explanation = explanation?.ifBlank { null },
            imageUrl = imageUrl?.ifBlank { null },
            type = mapQuestionType(type),
            level = mapQuestionLevel(level)
        )
    }

    private fun mapQuestionType(raw: String?): QuestionType {
        val normalized = raw?.replace('-', '_')?.replace(' ', '_')?.uppercase()
        return when (normalized) {
            "MULTIPLE_CHOICE" -> QuestionType.MULTIPLE_CHOICE
            "TRUE_FALSE" -> QuestionType.TRUE_FALSE
            else -> QuestionType.MULTIPLE_CHOICE
        }
    }

    private fun mapQuestionLevel(raw: String?): QuestionLevel {
        return when (raw?.lowercase()) {
            "beginner", "easy" -> QuestionLevel.EASY
            "intermediate", "medium" -> QuestionLevel.MEDIUM
            "advanced", "hard" -> QuestionLevel.HARD
            else -> QuestionLevel.MEDIUM
        }
    }
}

// QR Code response model
data class QRCodeResponse(
    val child: QRChildInfo,
    val qr: String // Base64 encoded image string
)

data class QRChildInfo(
    val name: String,
    val id: String
)

// Child Review response models
data class PerformanceByTopicResponse(
    val topic: String? = null,
    val quizzesCompleted: Int? = 0,
    val averageScore: Double? = 0.0,
    val highestScore: Int? = 0,
    val lowestScore: Int? = 0
) {
    fun toPerformanceByTopic(): PerformanceByTopic {
        return PerformanceByTopic(
            topic = this.topic ?: "",
            quizzesCompleted = this.quizzesCompleted ?: 0,
            averageScore = this.averageScore ?: 0.0,
            highestScore = this.highestScore ?: 0,
            lowestScore = this.lowestScore ?: 0
        )
    }
}

data class ChildReviewResponse(
    val childName: String? = null,
    val childAge: Int? = 0,
    val childLevel: String? = null,
    val progressionLevel: Int? = 1,
    val totalQuizzes: Int? = 0,
    val overallAverage: Double? = 0.0,
    val lifetimeScore: Int? = 0,
    val currentScore: Int? = 0,
    val performanceByTopic: List<PerformanceByTopicResponse>? = emptyList(),
    val strengths: String? = null,
    val weaknesses: String? = null,
    val recommendations: String? = null,  // Changed from List<String> to String
    val summary: String? = null,
    val generatedAt: String? = null,
    val pdfBase64: String? = null
) {
    fun toChildReview(): ChildReview {
        // Split recommendations string by newlines and filter out empty lines
        val recommendationsList = this.recommendations
            ?.split("\n")
            ?.map { it.trim() }
            ?.filter { it.isNotBlank() }
            ?.map { 
                // Remove leading numbers and dots/dashes if present (e.g., "1. " or "- ")
                it.replaceFirst(Regex("^\\d+\\.\\s*"), "")
                  .replaceFirst(Regex("^-\\s*"), "")
                  .trim()
            }
            ?: emptyList()
        
        return ChildReview(
            childName = this.childName ?: "",
            childAge = this.childAge ?: 0,
            childLevel = this.childLevel ?: "",
            progressionLevel = this.progressionLevel ?: 1,
            totalQuizzes = this.totalQuizzes ?: 0,
            overallAverage = this.overallAverage ?: 0.0,
            lifetimeScore = this.lifetimeScore ?: 0,
            currentScore = this.currentScore ?: 0,
            performanceByTopic = this.performanceByTopic?.map { it.toPerformanceByTopic() } ?: emptyList(),
            strengths = this.strengths ?: "",
            weaknesses = this.weaknesses ?: "",
            recommendations = recommendationsList,
            summary = this.summary ?: "",
            generatedAt = this.generatedAt ?: "",
            pdfBase64 = this.pdfBase64
        )
    }
}

