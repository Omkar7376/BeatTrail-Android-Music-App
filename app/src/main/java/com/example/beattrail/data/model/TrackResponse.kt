package com.example.beattrail.data.model

import kotlinx.serialization.Serializable

@Serializable
data class TrackResponse(
    val headers: Headers,
    val results: List<TrackDto>
)