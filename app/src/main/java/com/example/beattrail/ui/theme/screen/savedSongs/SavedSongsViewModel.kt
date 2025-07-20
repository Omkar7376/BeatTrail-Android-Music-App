package com.example.beattrail.ui.theme.screen.savedSongs

import android.app.Application
import android.content.Context
import android.content.ContentValues
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.mutableStateOf
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
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.plugins.onDownload
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.request.head
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.contentLength
import io.ktor.http.isSuccess
import io.ktor.util.cio.writeChannel
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.copyTo
import io.ktor.utils.io.counted
import io.ktor.utils.io.readAvailable
import io.ktor.utils.io.totalBytesRead
import kotlinx.coroutines.channels.Channel
import java.io.File
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.nio.ByteBuffer
import java.nio.channels.Channels

class SavedSongsViewModel(
    application: Application,
    private val repository: SavedSongRepository
) : AndroidViewModel(application) {

    sealed class DownloadState {
        object Idle : DownloadState()
        data class Downloading(val songId: Int, val progress: Float) : DownloadState()
        data class Success(val songId: Int) : DownloadState()
        data class Error(val songId: Int, val message: String) : DownloadState()
    }
    private val _downloadState = MutableStateFlow<DownloadState>(DownloadState.Idle)
    val downloadState: StateFlow<DownloadState> = _downloadState.asStateFlow()

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
            val context = getApplication<Application>().applicationContext

            HttpClient(CIO) {
                engine {
                    requestTimeout = 120_000
                    endpoint {
                        connectTimeout = 120_000
                        socketTimeout = 60_000
                        keepAliveTime = 120_000
                    }
                }
                install(HttpTimeout) {
                    requestTimeoutMillis = 120_000
                    connectTimeoutMillis = 60_000
                    socketTimeoutMillis = 60_000
                }
                install(HttpRequestRetry) {
                    maxRetries = 5
                    retryOnExceptionIf { request, cause ->
                        cause is ConnectException ||
                                cause is SocketTimeoutException ||
                                cause is ConnectTimeoutException
                    }
                    delayMillis { retry ->
                        (retry * 2000L).coerceAtMost(10_000L)
                    }
                }
                expectSuccess = false

                install(HttpCache)
            }.use { client ->
                try {
                    val contentValues = ContentValues().apply {
                        put(MediaStore.Audio.Media.DISPLAY_NAME, "${song.title.sanitizeFileName()}.mp3")
                        put(MediaStore.Audio.Media.MIME_TYPE, "audio/mpeg")
                        put(MediaStore.Audio.Media.RELATIVE_PATH, Environment.DIRECTORY_MUSIC)
                        put(MediaStore.Audio.Media.IS_PENDING, 1)
                    }

                    val uri = context.contentResolver.insert(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        contentValues
                    ) ?: throw IOException("Failed to create file entry")

                    try {
                        val testResponse = client.head(song.downloadUrl)
                        if (!testResponse.status.isSuccess()) {
                            throw IOException("Server unavailable: ${testResponse.status}")
                        }
                    } catch (e: Exception) {
                        Log.w("Download", "Test check failed", e)
                    }

                    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                        val response = client.get(song.downloadUrl){
                            expectSuccess = false
                        }

                        if (!response.status.isSuccess()) {
                            throw IOException("Download failed: ${response.status}")
                        }

                        val bytes = response.body<ByteArray>()
                        outputStream.write(bytes)

                        /*val outputChannel = Channels.newChannel(outputStream)
                        val inputChannel = response.bodyAsChannel()
                        response.bodyAsChannel().copyTo(outputChannel as ByteWriteChannel)

                        try {
                            val buffer = ByteArray(8192)
                            val byteBuffer = ByteBuffer.wrap(buffer)

                            while (true) {
                                byteBuffer.clear()
                                val bytesRead = inputChannel.readAvailable(byteBuffer)
                                if (bytesRead == -1) break

                                byteBuffer.flip()
                                while (byteBuffer.hasRemaining()) {
                                    outputChannel.write(byteBuffer)
                                }

                                val progress = (inputChannel.counted().totalBytesRead.toFloat() /
                                        (response.contentLength()?.toFloat()!!)) * 100
                                _downloadState.value = DownloadState.Downloading(song.id, progress)
                            }
                        } finally {
                            outputChannel.close()
                        }*/
                    }

                    contentValues.put(MediaStore.Audio.Media.IS_PENDING, 0)
                    context.contentResolver.update(uri, contentValues, null, null)

                    repository.saveSong(
                        SavedSongEntity.from(
                            song.copy(url = uri.toString())
                        )
                    )

                    _downloadState.value = DownloadState.Success(song.id)

                    val filePath = uriToFilePath(context, uri)
                    if (filePath == null || !File(filePath).exists()) {
                        throw IOException("Downloaded file verification failed")
                    }

                    Log.d("###", "saveSongPath: $filePath ")

                    repository.saveSong(SavedSongEntity.from(song.copy(url = uri.toString())))
                } catch (e: Exception) {
                    _downloadState.value = DownloadState.Error(
                        song.id,
                        when (e) {
                            is SocketTimeoutException -> "Connection timed out"
                            is ConnectTimeoutException -> "Server not responding"
                            is IOException -> "File write error: ${e.message}"
                            else -> "Download failed: ${e.message}"
                        }
                    )
                    Log.e("Download", "Error saving song", e)
                }
            }
        }
    }

    private fun uriToFilePath(context: Context, uri: Uri): String? {
        val projection = arrayOf(MediaStore.Audio.Media.DATA)
        return context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
            } else null
        }
    }

    fun String.sanitizeFileName(): String {
        return replace("[^a-zA-Z0-9.\\- ]".toRegex(), "_")
    }

    /*fun saveSong(song: SongModel) {
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

            */
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

            repository.saveSong(SavedSongEntity.from(song))*//*
        }
    }*/

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
