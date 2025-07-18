package com.example.beattrail.ui.theme.screen.home

import com.example.beattrail.domain.model.SongModel

data class HomeUiState(
    val isLoading: Boolean = false,
    val songs: List<SongModel> = emptyList(),
    val searchQuery: String = "",
    val filteredSongs: List<SongModel> = emptyList(),
    val error: String? = null
)