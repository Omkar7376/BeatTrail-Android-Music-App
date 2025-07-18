package com.example.beattrail.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.beattrail.domain.model.SongModel

@Entity(tableName = "saved_songs")
data class SavedSongEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val artist: String,
    val url: String,
    val image: String,
    val downloadUrl: String,
    val duration: Int
) {
    fun toSongModel() = SongModel(id, title, artist, url, image, downloadUrl, duration)

    companion object {
        fun from(song: SongModel) = SavedSongEntity(
            id = song.id,
            title = song.title,
            artist = song.artist,
            url = song.url,
            image = song.image,
            downloadUrl = song.downloadUrl,
            duration = song.duration
        )
    }
}

