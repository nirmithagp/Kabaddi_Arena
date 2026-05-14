package com.example.my_arena.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class MatchStats(
    val opponentTeam: String,
    var raids: Int = 0,
    var touchPoints: Int = 0,
    var bonusPoints: Int = 0,
    var superRaids: Int = 0,
    var caught: Int = 0,
    var tacklePoints: Int = 0,
    var totalTackles: Int = 0,
    var successfulTackles: Int = 0,
    var actionsHistory: MutableList<Action> = mutableListOf()
) {
    fun addAction(action: String, points: Int) {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val currentTime = sdf.format(Date())
        // Adding at the end of the list for chronological order in chart, 
        // but we can reverse it for the UI display.
        actionsHistory.add(Action(playerName = "Current Player", points = points, title = action, timestamp = currentTime))
    }

    fun totalPoints(): Int {
        return touchPoints + bonusPoints + superRaids + tacklePoints
    }

    fun raidSuccessRate(): Double {
        return if (raids == 0) 0.0
        else ((touchPoints + bonusPoints + superRaids).toDouble() / raids) * 100
    }

    fun tackleSuccessRate(): Double {
        return if (totalTackles == 0) 0.0
        else (successfulTackles.toDouble() / totalTackles) * 100
    }

    fun performanceSummary(): String {
        val total = totalPoints()
        return when {
            total >= 15 -> "Outstanding performance! A true match winner."
            total >= 10 -> "Excellent performance! Very impactful."
            total >= 5 -> "Good contribution to the team."
            else -> "Average match. Focus on improving raid success rate."
        }
    }
}