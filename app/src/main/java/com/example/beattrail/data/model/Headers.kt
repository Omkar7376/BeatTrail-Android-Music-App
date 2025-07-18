package com.example.beattrail.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Headers(
    val code: Int,
    val error_message: String,
    val next: String,
    val results_count: Int,
    val status: String,
    val warnings: String
)