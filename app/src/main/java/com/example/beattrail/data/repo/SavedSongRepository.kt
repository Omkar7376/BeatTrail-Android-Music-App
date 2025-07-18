package com.example.beattrail.data.repo

import com.example.beattrail.data.local.Dao.SavedSongDao
import com.example.beattrail.data.local.entity.SavedSongEntity
import kotlinx.coroutines.flow.Flow

class SavedSongRepository(private val dao: SavedSongDao) {

    fun getAllSavedSongs(): Flow<List<SavedSongEntity>> = dao.getAllSongs()

    suspend fun saveSong(song: SavedSongEntity) {
        dao.insert(song)
    }

    suspend fun deleteById(id: Int) {
        dao.deleteById(id)
    }

    suspend fun getSongById(id: Int): SavedSongEntity? {
        return dao.getSongById(id)
    }
}