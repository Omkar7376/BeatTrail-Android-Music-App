package com.example.beattrail.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_songs")
data class RecentSongEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val title: String,
    val artist: String,
    val image: String,
    val url: String,
    val downloadUrl: String,
    val duration: Int,
    val timestamp: Long = System.currentTimeMillis()
)