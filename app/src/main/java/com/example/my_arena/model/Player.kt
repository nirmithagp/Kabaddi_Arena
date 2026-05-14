package com.example.my_arena.model

data class Player(
    val name: String,
    val skillLevel: String,

    var totalMatches: Int = 0,
    var totalRaids: Int = 0,
    var totalTouchPoints: Int = 0,
    var totalBonusPoints: Int = 0,
    var totalSuperRaids: Int = 0,
    var totalCaught: Int = 0,
    var totalTacklePoints: Int = 0,
    var totalTackles: Int = 0
) {

    // Calculate total raid points
    fun totalRaidPoints(): Int {
        return totalTouchPoints + totalBonusPoints + totalSuperRaids
    }

    // Calculate total points (Raid + Tackle)
    fun totalCareerPoints(): Int {
        return totalRaidPoints() + totalTacklePoints
    }

    // Raid success rate
    fun successRate(): Double {
        return if (totalRaids == 0) 0.0
        else (totalRaidPoints().toDouble() / totalRaids) * 100
    }

    // Tackle success rate
    fun tackleSuccessRate(): Double {
        return if (totalTackles == 0) 0.0
        else (totalTacklePoints.toDouble() / totalTackles) * 100
    }
}
