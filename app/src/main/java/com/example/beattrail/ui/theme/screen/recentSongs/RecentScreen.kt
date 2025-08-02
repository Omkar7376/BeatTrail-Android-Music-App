package com.example.beattrail.ui.theme.screen.recentSongs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.beattrail.data.local.entity.RecentSongEntity
import com.example.beattrail.domain.mapper.toRecentSongEntity
import com.example.beattrail.domain.mapper.toSongModel
import com.example.beattrail.domain.model.SongModel
import com.example.beattrail.ui.theme.screen.nowPlaying.PlayerViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun RecentScreen(
    recentSongsViewModel: RecentSongsViewModel = koinViewModel(),
    playerViewModel: PlayerViewModel = koinViewModel(),
    onSongClick: (SongModel, List<SongModel>) -> Unit = { song, recentSongs ->
        playerViewModel.play(song)
    }
) {
    val recentSongs by recentSongsViewModel.recentSongs.collectAsState()
    val navController = rememberNavController()
    val mappedRecentSongs = remember(recentSongs) {
        recentSongs.map { it.toSongModel() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recent Songs", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                backgroundColor = Color.Gray,
                contentColor = Color.White,
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (mappedRecentSongs.isEmpty()){
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No Recent Songs")
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(mappedRecentSongs, key = { it.id }) {song ->
                    SongItemRecent(
                        song = song,
                        onClick = { onSongClick(song, mappedRecentSongs) },
                        onRemoveClick = {
                            recentSongsViewModel.removeSong(song.toRecentSongEntity())
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SongItemRecent(
    song: SongModel,
    onClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = song.image,
            contentDescription = null,
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.width(12.dp))

        Column(Modifier.weight(1f)) {
            Text(text = song.title, fontWeight = FontWeight.Bold)
            Text(
                text = song.artist,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Box(Modifier.wrapContentWidth(Alignment.End)) {
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "More Options")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Remove from Recent",color = MaterialTheme.colorScheme.onSurface) },
                    onClick = {
                        expanded = false
                        onRemoveClick()
                    }
                )
            }
        }
    }
}
