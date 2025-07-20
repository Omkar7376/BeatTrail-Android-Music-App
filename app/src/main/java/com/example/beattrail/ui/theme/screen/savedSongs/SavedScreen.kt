package com.example.beattrail.ui.theme.screen.savedSongs

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
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.beattrail.domain.model.SongModel
import com.example.beattrail.ui.theme.screen.home.SongItemHome
import com.example.beattrail.ui.theme.screen.nowPlaying.PlayerViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SavedScreen(
    savedSongsViewModel: SavedSongsViewModel = koinViewModel(),
    playerViewModel: PlayerViewModel = koinViewModel(),
    onSongClick: (SongModel) -> Unit = { song ->
        playerViewModel.play(song)
    }
) {
    val savedSongs by savedSongsViewModel.savedSongs.collectAsState()
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saved Songs")},
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
        if (savedSongs.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No Saved Songs yet")
            }
        } else {
            LazyColumn {
                items(savedSongs, key = { it.id }) { song ->
                    SongItemSaved(
                        song = song,
                        onClick = { onSongClick(song) },
                        onRemoveClick = {
                            savedSongsViewModel.removeSong(song)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SongItemSaved(
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
            Text(text = song.artist, style = MaterialTheme.typography.bodySmall)
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
                    text = { Text("Remove from Saved") },
                    onClick = {
                        expanded = false
                        onRemoveClick()
                    }
                )
            }
        }
    }
}
