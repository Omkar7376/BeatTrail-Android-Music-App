package com.example.beattrail.domain.model

data class SongModel(
    val id: Int,
    val title: String,
    val artist: String,
    val url: String,
    val image: String,
    val downloadUrl : String,
    val duration: Int
)