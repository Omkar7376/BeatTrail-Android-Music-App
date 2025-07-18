package com.example.beattrail.ui.theme.screen.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beattrail.data.repo.SongRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: SongRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        fetchSongs()
    }

    private fun fetchSongs(){
        viewModelScope.launch {
            try {
                val songs = repository.getSongs()
                _uiState.update {
                    it.copy(songs = songs, isLoading = false, filteredSongs = songs)
                }
                Log.d("###", "fetchSongs: $songs")
            }catch (e: Exception) {
                _uiState.update {
                    it.copy(error = e.message, isLoading = false)
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { state ->
            val filteredSongs = if (query.isBlank()) state.songs else {
                state.songs.filter {
                    it.title.contains(query, ignoreCase = true) || it.artist.contains(query, ignoreCase = true)
                }
            }
            state.copy(searchQuery = query, filteredSongs = filteredSongs)
        }
    }

    fun sortSongs(option: String) {
        val sorted = when (option) {
            "Title (A-Z)" -> _uiState.value.songs.sortedBy { it.title.lowercase() }
            "Title (Z-A)" -> _uiState.value.songs.sortedByDescending { it.title.lowercase() }
            "Artist (A-Z)" -> _uiState.value.songs.sortedBy { it.artist.lowercase() }
            "Artist (Z-A)" -> _uiState.value.songs.sortedByDescending { it.artist.lowercase() }
            else -> _uiState.value.songs
        }

        _uiState.update { it.copy(filteredSongs = sorted) }
    }

}
