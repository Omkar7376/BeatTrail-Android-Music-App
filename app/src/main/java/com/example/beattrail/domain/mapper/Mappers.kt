package com.example.beattrail.domain.mapper

import android.util.Log
import com.example.beattrail.data.local.entity.RecentSongEntity
import com.example.beattrail.data.model.TrackDto
import com.example.beattrail.domain.model.SongModel

fun TrackDto.toSongModel(): SongModel {
    Log.d("###", "toSongModel: ${this.name}")
    return SongModel(
        id = id.toIntOrNull() ?: 0,
        title = name,
        artist = artist_name,
        url = audio,
        image = image,
        downloadUrl = audiodownload,
        duration = duration
    )
}

fun SongModel.toRecentSongEntity(): RecentSongEntity {
    return RecentSongEntity(
        id = id,
        title = title,
        artist = artist,
        url = url,
        image = image,
        downloadUrl = downloadUrl,
        duration = duration,
        timestamp = System.currentTimeMillis()
    )
}

fun RecentSongEntity.toSongModel(): SongModel {
    return SongModel(
        id = id,
        title = title,
        artist = artist,
        url = url,
        image = image,
        downloadUrl = downloadUrl,
        duration = duration
    )
}