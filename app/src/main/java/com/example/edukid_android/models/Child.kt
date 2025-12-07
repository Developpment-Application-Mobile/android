package com.example.edukid_android.models





data class Child(
    val id: String?,
    val name: String,
    val age: Int,
    val level: String,
    val avatarEmoji: String,
    val quizzes: List<Quiz> = emptyList(),
    val Score: Int = 0,
    val lifetimeScore: Int = 0,
    val progressionLevel: Int = 1,
    val inventory: List<InventoryItem> = emptyList(),
    val shopCatalog: List<ShopItem> = emptyList(),
    val parentId: String? = null
) {
    // ✅ Get only completed quizzes
    fun getCompletedQuizzes(): List<Quiz> {
        return quizzes.filter { it.getCompletionPercentage() == 100 }
    }

    // 🕓 Get only in-progress quizzes
    fun getInProgressQuizzes(): List<Quiz> {
        return quizzes.filter { it.getCompletionPercentage() < 100 }
    }

    // 🧩 (optional) For quick overview
    fun getQuizProgressSummary(): Pair<Int, Int> {
        val completed = getCompletedQuizzes().size
        val inProgress = getInProgressQuizzes().size
        return completed to inProgress
    }
}

data class InventoryItem(
    val title: String,
    val cost: Int,
    val purchasedAt: String // Date as String for simplicity in Android
)

data class ShopItem(
    val id: String? = null,
    val title: String,
    val cost: Int
)

//data class Gift(
//    val id: String? = null,
//    val title: String,
//    val cost: Int
//)