package com.example.beattrail.data.local.Dao

import androidx.room.*
import com.example.beattrail.data.local.entity.SavedSongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedSongDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(song: SavedSongEntity)

    @Delete
    suspend fun delete(song: SavedSongEntity)

    @Query("SELECT * FROM saved_songs")
    fun getAllSongs(): Flow<List<SavedSongEntity>>

    @Query("SELECT * FROM saved_songs WHERE id = :id")
    suspend fun getSongById(id: Int): SavedSongEntity?

    @Query("DELETE FROM saved_songs WHERE id = :id")
    suspend fun deleteById(id: Int)
}
