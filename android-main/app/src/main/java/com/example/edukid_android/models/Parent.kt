package com.example.edukid_android.models

data class Parent(
    val name: String,
    val email: String,
    val phoneNumber: String = "",
    val profileImageUrl: String = "",
    val children: List<Child> = emptyList(),
    val totalScore: Int = 0,
    val isActive: Boolean = true,
    val id: String = "" // <- Must match _id from Mongo
)

object ParentJsonParser {

    fun fromJson(obj: org.json.JSONObject): Parent {
        // ✅ Fix: extract MongoDB ID correctly
        val id = obj.optString("_id", "")

        val name = obj.optString("name", "")
        val email = obj.optString("email", "")
        val phoneNumber = obj.optString("phoneNumber", "")
        val profileImageUrl = obj.optString("profileImageUrl", "")
        val totalScore = obj.optInt("totalScore", 0)
        val isActive = obj.optBoolean("isActive", true)

        val childrenArray = obj.optJSONArray("children")
        val children = mutableListOf<Child>()
        if (childrenArray != null) {
            for (i in 0 until childrenArray.length()) {
                val childObj = childrenArray.optJSONObject(i)
                if (childObj != null) children.add(parseChild(childObj))
            }
        }

        return Parent(
            id = id, // ✅ Now stored properly
            name = name,
            email = email,
            phoneNumber = phoneNumber,
            profileImageUrl = profileImageUrl,
            children = children,
            totalScore = totalScore,
            isActive = isActive
        )
    }

    private fun parseChild(obj: org.json.JSONObject): Child {
        // ✅ Fix: use MongoDB _id field
        val id = obj.optString("_id", "")

        val name = obj.optString("name", "")
        val age = obj.optInt("age", 0)
        val level = obj.optString("level", "")
        val avatarEmoji = obj.optString("avatarEmoji", "")

        // ✅ Fix: handle Score (capital S) fallback safely
        val scoreValue = obj.optInt("Score", obj.optInt("score", 0))

        val quizzesArray = obj.optJSONArray("quizzes")
        val quizzes = mutableListOf<Quiz>()
        if (quizzesArray != null) {
            for (i in 0 until quizzesArray.length()) {
                val quizObj = quizzesArray.optJSONObject(i)
                if (quizObj != null) quizzes.add(parseQuizItem(quizObj))
            }
        }

        return Child(
            id = id, // ✅ Now stored correctly
            name = name,
            age = age,
            level = level,
            avatarEmoji = avatarEmoji,
            quizzes = quizzes,
            Score = scoreValue
        )
    }

    private fun parseQuizItem(obj: org.json.JSONObject): Quiz {
        val title = obj.optString("title", "")
        val isAnswered = obj.optBoolean("isAnswered", false)
        val score = obj.optInt("score", 0)

        val typeStr = obj.optString("type", "GENERAL")
        val type = try { QuizType.valueOf(typeStr.uppercase()) } catch (_: Exception) { QuizType.GENERAL }

        val questionsArray = obj.optJSONArray("questions")
        val questions = mutableListOf<Question>()
        if (questionsArray != null) {
            for (i in 0 until questionsArray.length()) {
                val qObj = questionsArray.optJSONObject(i)
                if (qObj != null) questions.add(parseQuestion(qObj))
            }
        }

        return Quiz(
            title = title,
            questions = questions,
            isAnswered = isAnswered,
            type = type,
            score = score
        )
    }

    private fun parseQuestion(obj: org.json.JSONObject): Question {
        val questionText = obj.optString("questionText", "")

        val optionsArray = obj.optJSONArray("options")
        val options = mutableListOf<String>()
        if (optionsArray != null) {
            for (i in 0 until optionsArray.length()) {
                options.add(optionsArray.optString(i, ""))
            }
        }

        val correctAnswerIndex = obj.optInt("correctAnswerIndex", 0)
        val explanation = obj.optString("explanation", null)
        val imageUrl = obj.optString("imageUrl", null)
        val userAnswerIndex = obj.optInt("userAnswerIndex", 0)


        val type = try { QuestionType.valueOf(obj.optString("type").uppercase()) } catch (_: Exception) { QuestionType.MULTIPLE_CHOICE }
        val level = try { QuestionLevel.valueOf(obj.optString("level").uppercase()) } catch (_: Exception) { QuestionLevel.MEDIUM }

        return Question(
            questionText = questionText,
            options = options,
            correctAnswerIndex = correctAnswerIndex,
            explanation = explanation,
            imageUrl = imageUrl,
            type = type,
            level = level,
            userAnswerIndex = userAnswerIndex
        )
    }
}
