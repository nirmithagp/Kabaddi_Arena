package com.example.my_arena.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "matches")
data class MatchEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val opponentName: String,
    val totalScore: Int,
    val raidSuccess: Double,
    val tackleSuccess: Double,
    val actionHistoryJson: String, // Actions stored as JSON string
    val matchDate: String
)
