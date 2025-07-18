package com.example.beattrail.data.api

import com.example.beattrail.data.model.TrackResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class SongApiService(private val client: HttpClient) {
    suspend fun getTracks(): TrackResponse {
        return client.get("https://api.jamendo.com/v3.0/tracks/") {
            parameter("client_id", "29779bfd")
            parameter("limit", "200")
        }.body()
    }
}


