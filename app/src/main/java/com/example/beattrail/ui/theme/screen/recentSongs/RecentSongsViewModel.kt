package com.example.beattrail.ui.theme.screen.recentSongs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beattrail.data.local.entity.RecentSongEntity
import com.example.beattrail.data.repo.RecentSongRepo
import com.example.beattrail.domain.model.SongModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RecentSongsViewModel(private val repo : RecentSongRepo) : ViewModel()  {

    val recentSongs = repo.getRecentSongs().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = emptyList()
    )

    fun addSong(song: SongModel) {
        viewModelScope.launch {
            repo.addToRecent(song)
        }
    }

    fun removeSong(song: RecentSongEntity) {
        viewModelScope.launch {
            repo.deleteById(song.id)
        }
    }
}