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
    val parentId: String? = null
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
            parentId = this.parentId
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

