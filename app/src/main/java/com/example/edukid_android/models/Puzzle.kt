package com.example.edukid_android.models

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable

// MARK: - Puzzle Piece
@Serializable
data class PuzzlePiece(
    val id: Int,
    val correctPosition: Int,
    var currentPosition: Int,
    val content: String,
    val imageUrl: String? = null
) {
    val isEmoji: Boolean get() = content.isSingleEmoji()
    val isImage: Boolean get() = !imageUrl.isNullOrEmpty()
    val displayText: String get() = content.trim()
}

// MARK: - Puzzle Type
enum class PuzzleType(val value: String) {
    IMAGE("image"),
    WORD("word"),
    NUMBER("number"),
    SEQUENCE("sequence"),
    PATTERN("pattern");

    val displayName: String
        get() = when (this) {
            IMAGE -> "Image"
            WORD -> "Word"
            NUMBER -> "Number"
            SEQUENCE -> "Sequence"
            PATTERN -> "Pattern"
        }

    val icon: String
        get() = when (this) {
            IMAGE -> "photo"
            WORD -> "text_fields"
            NUMBER -> "numbers"
            SEQUENCE -> "swap_horiz"
            PATTERN -> "grid_on"
        }

    val color: Color
        get() = when (this) {
            IMAGE -> Color(0xFF9C27B0) // Purple
            WORD -> Color(0xFF2196F3) // Blue
            NUMBER -> Color(0xFF4CAF50) // Green
            SEQUENCE -> Color(0xFFFF9800) // Orange
            PATTERN -> Color(0xFFE91E63) // Pink
        }

    companion object {
        fun fromString(value: String): PuzzleType {
            return values().find { it.value == value } ?: WORD
        }
    }
}

// MARK: - Puzzle Difficulty
enum class PuzzleDifficulty(val value: String) {
    EASY("easy"),
    MEDIUM("medium"),
    HARD("hard");

    val displayName: String get() = value.capitalize()

    val gridSize: Int
        get() = when (this) {
            EASY -> 2
            MEDIUM -> 3
            HARD -> 4
        }

    val color: Color
        get() = when (this) {
            EASY -> Color(0xFF4CAF50) // Green
            MEDIUM -> Color(0xFFFF9800) // Orange
            HARD -> Color(0xFFF44336) // Red
        }

    companion object {
        fun fromString(value: String): PuzzleDifficulty {
            return values().find { it.value == value } ?: EASY
        }
    }
}

// MARK: - Puzzle Image Presets
enum class PuzzleImage(val value: String, val emoji: String) {
    LION("puzzle_lion", "🦁"),
    TURTLE("puzzle_turtle", "🐢"),
    ELEPHANT("puzzle_elephant", "🐘"),
    RABBIT("puzzle_rabbit", "🐰"),
    CAT("puzzle_cat", "🐱"),
    DOG("puzzle_dog", "🐶"),
    BEAR("puzzle_bear", "🐻"),
    PANDA("puzzle_panda", "🐼");

    val displayName: String
        get() = value.replace("puzzle_", "").capitalize()

    val backgroundColor: Color
        get() = when (this) {
            LION -> Color(0xFF80CCFF)
            TURTLE -> Color(0xFF99E6F2)
            ELEPHANT -> Color(0xFFB3D9F2)
            RABBIT -> Color(0xFFF2D9E6)
            CAT -> Color(0xFFFFE6CC)
            DOG -> Color(0xFFE6D9BF)
            BEAR -> Color(0xFFD9BFA6)
            PANDA -> Color(0xFFE6F2E6)
        }

    companion object {
        fun random(): PuzzleImage = values().random()
    }
}

// MARK: - Server Puzzle Response
@Serializable
data class PuzzleResponse(
    val _id: String,
    val title: String,
    val type: String,
    val difficulty: String,
    val gridSize: Int,
    val pieces: List<PuzzlePiece>,
    val hint: String? = null,
    val solution: String? = null,
    val imageUrl: String? = null,
    var isCompleted: Boolean = false,
    var attempts: Int = 0,
    var timeSpent: Int = 0,
    var score: Int = 0,
    val completedAt: String? = null,
    val createdAt: String? = null
) {
    val id: String get() = _id
    val puzzleType: PuzzleType get() = PuzzleType.fromString(type)
    val puzzleDifficulty: PuzzleDifficulty get() = PuzzleDifficulty.fromString(difficulty)
    val isSolved: Boolean get() = pieces.all { it.currentPosition == it.correctPosition }
}

// MARK: - Local Puzzle Model
@Serializable
data class LocalPuzzle(
    val id: String,
    val childId: String,
    val title: String,
    val type: PuzzleType,
    val difficulty: PuzzleDifficulty,
    val gridSize: Int,
    var pieces: List<LocalPuzzlePiece>,
    val hint: String,
    val solution: String,
    val puzzleImage: PuzzleImage,
    val customImagePath: String? = null,
    var isCompleted: Boolean = false,
    var attempts: Int = 0,
    var timeSpent: Int = 0,
    var score: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    var completedAt: Long? = null
) {
    val puzzleType: PuzzleType get() = type
    val puzzleDifficulty: PuzzleDifficulty get() = difficulty
}

@Serializable
data class LocalPuzzlePiece(
    val id: Int,
    val correctPosition: Int,
    var currentPosition: Int,
    val content: String,
    val emoji: String? = null,
    val imageUrl: String? = null
)

// MARK: - API Request Models
@Serializable
data class GeneratePuzzleRequest(
    val type: String? = null,
    val difficulty: String? = null,
    val gridSize: Int? = null,
    val topic: String? = null
)

@Serializable
data class SubmitPuzzleRequest(
    val positions: List<Int>,
    val timeSpent: Int
)

// MARK: - Extensions
fun String.isSingleEmoji(): Boolean {
    return this.length == 1 && this[0].isEmoji()
}

fun Char.isEmoji(): Boolean {
    val codePoint = this.code
    return codePoint in 0x1F600..0x1F64F ||
            codePoint in 0x1F300..0x1F5FF ||
            codePoint in 0x1F680..0x1F6FF ||
            codePoint in 0x1F1E6..0x1F1FF ||
            codePoint in 0x2600..0x26FF ||
            codePoint in 0x2700..0x27BF ||
            codePoint in 0xFE00..0xFE0F ||
            codePoint in 0x1F900..0x1F9FF
}
