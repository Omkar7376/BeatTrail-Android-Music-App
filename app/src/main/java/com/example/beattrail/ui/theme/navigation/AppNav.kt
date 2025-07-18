package com.example.beattrail.ui.theme.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.beattrail.ui.theme.screen.home.HomeScreen
import com.example.beattrail.ui.theme.screen.home.HomeViewModel
import com.example.beattrail.ui.theme.screen.home.currentRoute
import com.example.beattrail.ui.theme.screen.nowPlaying.NowPlayingScreen
import com.example.beattrail.ui.theme.screen.nowPlaying.PlayerViewModel
import com.example.beattrail.ui.theme.screen.savedSongs.SavedScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNav() {
    val navController = rememberNavController()
    val homeViewModel : HomeViewModel = koinViewModel()
    val playerViewModel : PlayerViewModel = koinViewModel()
    val currentRoute = currentRoute(navController)

    LaunchedEffect(Unit) {
        playerViewModel.navigationEvent.collect { songId ->
            navController.navigate("nowPlaying/$songId") {
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                viewModel = homeViewModel,
                playerViewModel = playerViewModel,
                currentRoute = currentRoute,
                onSongClick = { song ->
                    playerViewModel.play(song, homeViewModel.uiState.value.filteredSongs)
                    playerViewModel.emitNavigation(song.id)
                },
                onNavClick = { destination ->
                    navController.navigate(destination) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo("home") { inclusive = false }
                    }
                },
            )
        }

        composable("nowPlaying/{songId}") { backStackEntry ->
            val songId = backStackEntry.arguments?.getString("songId")
            NowPlayingScreen(
                songId = songId,
                viewModel = playerViewModel,
                onBack = { navController.popBackStack() })
        }

        composable("saved"){
            SavedScreen(
                playerViewModel = playerViewModel,
                onSongClick = { song ->
                    playerViewModel.play(song, listOf(song))
                    playerViewModel.emitNavigation(song.id)
                }
            )
        }
    }
}