package com.example.beattrail.data.repo

import android.util.Log
import com.example.beattrail.data.api.SongApiService
import com.example.beattrail.domain.model.SongModel
import com.example.beattrail.domain.mapper.toSongModel

class SongRepository(private val apiService: SongApiService) {
    private val cachedSongs = mutableListOf<SongModel>()

    suspend fun getSongs(): List<SongModel> {
        return if (cachedSongs.isEmpty()) {
            try {
                val freshSongs = apiService.getTracks().results?.map { it.toSongModel() } ?: emptyList()
                Log.d("###", "getSongs: ${freshSongs.size}")

                cachedSongs.addAll(freshSongs)
                freshSongs
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            cachedSongs
        }
    }
}
