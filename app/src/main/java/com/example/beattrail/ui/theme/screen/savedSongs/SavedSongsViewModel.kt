package com.example.beattrail.ui.theme.screen.savedSongs

import android.app.Application
import android.content.Context
import android.app.DownloadManager
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.beattrail.data.local.entity.SavedSongEntity
import com.example.beattrail.data.repo.SavedSongRepository
import com.example.beattrail.domain.model.SongModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import java.io.File

class SavedSongsViewModel(
    application: Application,
    private val repository: SavedSongRepository
) : AndroidViewModel(application) {

    private val _savedSongs = MutableStateFlow<List<SongModel>>(emptyList())
    val savedSongs: StateFlow<List<SongModel>> = _savedSongs.asStateFlow()

    private val _isLoding = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoding.asStateFlow()

    init {
        observeSavedSongs()
    }

    private fun observeSavedSongs() {
        viewModelScope.launch {
            _isLoding.value = true
            try {
                repository.getAllSavedSongs()
                    .map { entities -> entities.map { it.toSongModel() } }
                    .collect { _savedSongs.value = it }
            } finally {
                _isLoding.value = false
            }
        }
    }

    fun saveSong(song: SongModel) {
        viewModelScope.launch {
            //val uri = song.downloadUrl.toUri()
            val context = getApplication<Application>().applicationContext
            val fileName = "${song.title}-${song.id}.mp3"
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), fileName)

            val fileUri = Uri.fromFile(file).toString()

            val songToSave = song.copy(url = fileUri)
            repository.saveSong(SavedSongEntity.from(songToSave))

            try {
                val client = HttpClient(CIO)
                val bytes = client.get(song.downloadUrl).body<ByteArray>()
                file.writeBytes(bytes)

                repository.saveSong(
                    SavedSongEntity(
                        id = song.id,
                        title = song.title,
                        artist = song.artist,
                        url = song.url,
                        image = song.image,
                        downloadUrl = song.downloadUrl,
                        duration = song.duration
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

            /*val request = DownloadManager.Request(uri)
                .setTitle(song.title)
                .setDescription("Downloading ${song.title}")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_MUSIC, "${song.title}-${song.id}.mp3")
                .setMimeType("audio/mpeg")
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
                .addRequestHeader("User-Agent", "Mozilla/5.0")

            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)

            repository.saveSong(SavedSongEntity.from(song))*/
        }
    }

    fun removeSong(song: SongModel) {
        viewModelScope.launch {
            repository.deleteById(song.id)
        }
    }

    fun isSongSaved(songId: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val song = repository.getSongById(songId)
            onResult(song != null)
        }
    }
}
