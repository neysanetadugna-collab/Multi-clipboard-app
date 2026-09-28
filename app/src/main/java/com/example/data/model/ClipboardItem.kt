package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clipboard_items")
data class ClipboardItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isStarred: Boolean = false,
    val isNote: Boolean = false,
    val isTrash: Boolean = false,
    val tag: String = "",
    val colorHex: String = "",
    val characterCount: Int = content.length,
    val wordCount: Int = if (content.isBlank()) 0 else content.trim().split("\\s+".toRegex()).size,
    val hasUrl: Boolean = content.contains("http://", ignoreCase = true) || content.contains("https://", ignoreCase = true) || content.contains("www.", ignoreCase = true),
    val hasEmail: Boolean = content.contains("@") && content.contains("."),
    val hasPhone: Boolean = content.any { it.isDigit() } && content.filter { it.isDigit() }.length in 7..15
)
