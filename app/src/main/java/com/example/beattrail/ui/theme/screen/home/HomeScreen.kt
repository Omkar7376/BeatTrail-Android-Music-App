package com.example.beattrail.ui.theme.screen.home

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.OutlinedButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.example.beattrail.domain.model.SongModel
import com.example.beattrail.ui.theme.screen.nowPlaying.PlayerViewModel
import com.example.beattrail.ui.theme.screen.savedSongs.SavedSongsViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    playerViewModel: PlayerViewModel = koinViewModel(),
    currentRoute: String?,
    onSongClick: (SongModel) -> Unit,
    onNavClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentSong by playerViewModel.currentSong.collectAsState()
    val isPlayerVisible by playerViewModel.isPlayerVisible.collectAsState()
    val isPlaying by playerViewModel.isPlaying.collectAsState()
    val savedSongsViewModel: SavedSongsViewModel = koinViewModel()
    var isScreenVisible by remember { mutableStateOf(false) }
    val sortOrder = remember { mutableStateOf("Title (A-Z)") }
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()


    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.isLoading,
        onRefresh = { viewModel.loadSongs() }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("BeatTrail", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.White) },
                backgroundColor = Color.Gray,
                contentColor = Color.White
            )
        },
        bottomBar = {
            Column {
                if (isPlayerVisible && currentSong != null) {
                    MiniPlayer(
                        song = currentSong!!,
                        isPlaying = isPlaying,
                        onClick = { onSongClick(currentSong!!) },
                        onPlayPause = { playerViewModel.togglePlayPause() },
                    )
                    Log.d("###", "Current song: ${isPlayerVisible} ${currentSong?.title}")
                }
                BottomNavigation(modifier = Modifier.height(70.dp), backgroundColor = MaterialTheme.colorScheme.surface) {
                    BottomNavigationItem(
                        selected = currentRoute == "home",
                        onClick = { onNavClick("Home") },
                        icon = { Icon(Icons.Default.Home, null) },
                        label = { Text("Home", fontSize = 16.sp) }
                    )
                    BottomNavigationItem(
                        selected = currentRoute == "playlist",
                        onClick = { onNavClick("playlist") },
                        icon = { Icon(Icons.Default.List, null) },
                        label = { Text("Playlist",fontSize = 16.sp) }
                    )
                    BottomNavigationItem(
                        selected = currentRoute == "saved",
                        onClick = { onNavClick("saved") },
                        icon = { Icon(Icons.Default.Download, null) },
                        label = { Text("Saved",fontSize = 16.sp) }
                    )
                    BottomNavigationItem(
                        selected = currentRoute == "recent",
                        onClick = { onNavClick("recent") },
                        icon = { Icon(Icons.Default.History, null) },
                        label = { Text("Recent",fontSize = 16.sp) }
                    )
                    BottomNavigationItem(
                        selected = currentRoute == "settings",
                        onClick = { onNavClick("settings") },
                        icon = { Icon(Icons.Default.Settings, null) },
                        label = { Text("Settings",fontSize = 16.sp) }
                    )
                }
            }
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .pullRefresh(pullRefreshState)
        ) {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(16.dp))    {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it)},
                    label = { Text("Search Songs") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Sort By: ", modifier = Modifier.padding(end = 8.dp))
                    DropdownMenuSort(
                        selectedSortOrder = sortOrder.value,
                        onSortOrderSelected = {
                            sortOrder.value = it
                            viewModel.sortSongs(it)
                        }
                    )
                }

                Spacer(Modifier.height(6.dp))

                if (uiState.filteredSongs.isEmpty()) {
                    Text("No songs found")
                } else {
                    LazyColumn {
                        items(uiState.filteredSongs, key = { it.id }) { song ->
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn(animationSpec = tween(500)) + slideInVertically(),
                            ) {
                                SongItemHome(
                                    song = song,
                                    onClick = { onSongClick(song) },
                                    onDownloadClick = {
                                        savedSongsViewModel.saveSong(song)
                                    }
                                )
                            }
                        }
                    }
                }
            }
            PullRefreshIndicator(
                refreshing = uiState.isLoading,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
fun DropdownMenuSort(
    selectedSortOrder: String,
    onSortOrderSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val sortOptions = listOf("Title (A-Z)", "Title (Z-A)", "Artist (A-Z)", "Artist (Z-A)")

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(selectedSortOrder)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            sortOptions.forEach { option ->
                DropdownMenuItem(onClick = {
                    onSortOrderSelected(option)
                    expanded = false
                }) {
                    Text(option)
                }
            }
        }
    }
}


@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

@Composable
fun SongItemHome(song: SongModel, onClick: () -> Unit, onDownloadClick: () -> Unit) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp)
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
        Column {
            Text(text = song.title, fontWeight = FontWeight.Bold)
            Text(text = song.artist, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.weight(1f))

        Box {
            var expanded by remember { mutableStateOf(false) }
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "More Options")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Download") },
                    onClick = {
                        expanded = false
                        onDownloadClick()
                    }
                )
            }
        }
    }
}

@Composable
fun DropdownMenuBox(
    selectedOption: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(selectedOption)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun MiniPlayer(song: SongModel,isPlaying: Boolean, onClick: () -> Unit, onPlayPause: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp)
            .background(Color.Gray)
    ) {
        AsyncImage(
            model = song.image,
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .padding(2.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)) {
            Text(song.title, color = Color.White)
            Text(song.artist, color = Color.White, fontSize = 12.sp)
        }
        IconButton(onClick = onPlayPause) {
            Icon(
                if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}




