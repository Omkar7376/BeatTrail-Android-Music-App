package com.example.beattrail.ui.theme.screen.nowPlaying

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beattrail.domain.model.SongModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.example.beattrail.data.repo.SongRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.io.File

class PlayerViewModel(private val context: Context, private val repository: SongRepository) : ViewModel() {

    private val _currentSong = MutableStateFlow<SongModel?>(null)
    val currentSong: StateFlow<SongModel?> = _currentSong.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0f)
    val currentPosition: StateFlow<Float> = _currentPosition.asStateFlow()

    private val _isPlayerVisible = MutableStateFlow(false)
    val isPlayerVisible = _isPlayerVisible.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _navigationEvent = MutableSharedFlow<Int>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val player: ExoPlayer = ExoPlayer.Builder(context).build().apply {
        addListener(PlayerEventListener())
    }

    init {
        viewModelScope.launch {
            while (true) {
                _currentPosition.value = player.currentPosition.toFloat()
                delay(1000)
            }
        }
    }

    private val songList = mutableListOf<SongModel>()
    private var currentIndex = -1

    fun play(song: SongModel, songs: List<SongModel> = emptyList()) {
        viewModelScope.launch {
            if (_currentSong.value?.id != song.id) {
                _isLoading.value = true
                songList.clear()
                songList.addAll(songs)
                currentIndex = songList.indexOfFirst { it.id == song.id }

                _currentSong.value = song
                _duration.value = 0L
                _currentPosition.value = 0f

                player.stop()
                player.clearMediaItems()

                /*val localFile = File(
                    context.getExternalFilesDir(Environment.DIRECTORY_MUSIC),
                    "${song.title}-${song.id}.mp3"
                )*/

                val uriToPlay = if (song.url.startsWith("http")) {
                    song.url.toUri()
                } else {
                    Uri.fromFile(File(song.url))
                }

                player.setMediaItem(MediaItem.fromUri(uriToPlay))
                player.prepare()
                player.play()

                _isLoading.value = false
                _isPlayerVisible.value = true
                _isPlaying.value = true
            }
        }
    }

    fun emitNavigation(songId: Int) {
        viewModelScope.launch {
            _navigationEvent.emit(songId)
        }
    }

    fun skipPrevious(): SongModel? {
        if (songList.isEmpty()) return null

        val newIndex = if (currentIndex - 1 >= 0) currentIndex - 1 else songList.size - 1
        val newSong = songList[newIndex]
        play(newSong, songList)
        currentIndex = newIndex

        viewModelScope.launch {
            play(newSong, songList)
            _navigationEvent.emit(newSong.id)
        }
        return newSong
    }

    fun skipNext(): SongModel? {
        if (songList.isEmpty()) return null

        val newIndex = if (currentIndex + 1 < songList.size) currentIndex + 1 else 0
        val newSong = songList[newIndex]
        play(newSong, songList)
        currentIndex = newIndex

        viewModelScope.launch {
            play(newSong, songList)
            _navigationEvent.emit(newSong.id)
        }
        return newSong
    }

    fun pause() {
        player.pause()
        _isPlaying.value = false
    }

    fun resume() {
        player.play()
        _isPlaying.value = true
    }

    fun togglePlayPause() {
        if (player.isPlaying) {
            pause()
        } else {
            resume()
        }
    }

    fun seekTo(position: Float) {
        player.seekTo(position.toLong())
    }

    fun getSongById(id: String?): SongModel? {
        val parsedId = id?.toIntOrNull() ?: return null
        return songList.find { it.id == parsedId }
    }

    fun formatTime(millis: Long): String {
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }

    private inner class PlayerEventListener : Player.Listener {
        override fun onPlaybackStateChanged(state: Int) {
            _currentPosition.value = player.currentPosition.toFloat()
            _duration.value = if (player.duration > 0) player.duration else 0L
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            _duration.value = if (player.duration > 0) player.duration else 0L
        }
    }
}
