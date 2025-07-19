package com.example.beattrail.data.local.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.beattrail.data.local.entity.RecentSongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentSongDao {
    @Query("SELECT * FROM recent_songs ORDER BY id DESC LIMIT 50")
    fun getRecentSongs(): Flow<List<RecentSongEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(song: RecentSongEntity)

    @Query("DELETE FROM recent_songs WHERE id = :id")
    suspend fun deleteById(id: Int)
}