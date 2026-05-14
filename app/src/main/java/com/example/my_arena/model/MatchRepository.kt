package com.example.my_arena.model

import kotlinx.coroutines.flow.Flow

class MatchRepository(private val matchDao: MatchDao) {
    val allMatches: Flow<List<MatchEntity>> = matchDao.getAllMatches()

    suspend fun insert(match: MatchEntity) {
        matchDao.insertMatch(match)
    }

    suspend fun getMatchById(id: Int): MatchEntity? {
        return matchDao.getMatchById(id)
    }
}
