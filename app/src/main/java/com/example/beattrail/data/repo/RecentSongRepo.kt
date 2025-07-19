package com.example.beattrail.data.repo

import com.example.beattrail.data.local.Dao.RecentSongDao
import com.example.beattrail.data.local.entity.RecentSongEntity
import com.example.beattrail.domain.mapper.toRecentSongEntity
import com.example.beattrail.domain.model.SongModel
import kotlinx.coroutines.flow.Flow

class RecentSongRepo(private val dao: RecentSongDao) {
    fun getRecentSongs(): Flow<List<RecentSongEntity>> = dao.getRecentSongs()
    suspend fun addToRecent(song: SongModel) {
        dao.insertSongs(song.toRecentSongEntity())
    }

    suspend fun deleteById(id: Int) {
        dao.deleteById(id)
    }
}