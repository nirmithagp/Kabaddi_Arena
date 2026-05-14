package com.example.my_arena.model

import android.content.Context

object AppState {
    var currentPlayer: Player? = null
    var currentMatch: MatchStats? = null
    
    private var database: MatchDatabase? = null
    var repository: MatchRepository? = null

    fun initialize(context: Context) {
        if (database == null) {
            database = MatchDatabase.getDatabase(context)
            repository = MatchRepository(database!!.matchDao())
        }
    }
}
