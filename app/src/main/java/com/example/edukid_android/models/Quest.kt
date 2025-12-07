package com.example.edukid_android.models

data class Quest(
    val id: String,
    val title: String?,
    val description: String,
    val progress: Int,
    val target: Int,
    val reward: Int,
    val status: String,
    val progressionLevel: Int
) {
    val isCompleted: Boolean
        get() = status == "COMPLETED"

    val isClaimed: Boolean
        get() = status == "CLAIMED"
}

