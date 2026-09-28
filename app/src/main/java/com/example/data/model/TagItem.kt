package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tag_items")
data class TagItem(
    @PrimaryKey
    val name: String,
    val colorHex: String,
    val iconName: String = "Label"
)
