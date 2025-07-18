package com.example.beattrail.ui.theme.screen.nowPlaying

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState

@Composable
fun NowPlayingScreen(
    songId: String?,
    viewModel: PlayerViewModel,
    onBack: () -> Unit = {},
) {
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val position by viewModel.currentPosition.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val currentOnBack by rememberUpdatedState(onBack)

    val displaySong = remember(songId, currentSong) {
        songId?.toIntOrNull()?.let { id ->
            viewModel.getSongById(id.toString()) ?: currentSong
        } ?: currentSong
    }


    if (isLoading || displaySong == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.Black)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        AsyncImage(
            model = currentSong?.image,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.height(16.dp))

        currentSong?.let { Text(it.title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold) }
        currentSong?.let { Text(it.artist, color = Color.Gray, fontSize = 16.sp) }

        Spacer(Modifier.height(16.dp))

        val safeDuration = if (duration > 0) duration else 1L
        Slider(
            value = position.coerceIn(0f, safeDuration.toFloat()),
            onValueChange = viewModel::seekTo,
            valueRange = 0f..safeDuration.toFloat(),
            colors = SliderDefaults.colors(
                thumbColor = Color.Green,
                activeTrackColor = Color.Green
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(viewModel.formatTime(position.toLong()), color = Color.White)
            Text(viewModel.formatTime(duration), color = Color.White)
        }

        Spacer(Modifier.height(16.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                viewModel.skipPrevious()
            }) {
                Icon(Icons.Default.SkipPrevious, contentDescription = null, tint = Color.White)
            }
            IconButton(onClick = { viewModel.togglePlayPause() }) {
                Icon(
                    if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Color.White
                )
            }
            IconButton(onClick = {
                viewModel.skipNext()
            }) {
                Icon(Icons.Default.SkipNext, contentDescription = null, tint = Color.White)
            }
        }
    }
    BackHandler(enabled = true) { currentOnBack() }
}



