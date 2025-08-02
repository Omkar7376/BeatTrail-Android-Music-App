package com.example.beattrail.di

import android.app.Application
import androidx.room.Room
import com.example.beattrail.data.api.SongApiService
import com.example.beattrail.data.local.AppDatabase
import com.example.beattrail.data.repo.RecentSongRepo
import com.example.beattrail.data.repo.SavedSongRepository
import com.example.beattrail.data.repo.SongRepository
import com.example.beattrail.datastore.DataStoreManager
import com.example.beattrail.datastore.dataStore
import com.example.beattrail.ui.theme.ThemeViewModel
import com.example.beattrail.ui.theme.screen.home.HomeViewModel
import com.example.beattrail.ui.theme.screen.nowPlaying.PlayerViewModel
import com.example.beattrail.ui.theme.screen.recentSongs.RecentSongsViewModel
import com.example.beattrail.ui.theme.screen.savedSongs.SavedSongsViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    explicitNulls = false
                })
            }
        }
    }

    single {
        Room.databaseBuilder(
            get<Application>(),
            AppDatabase::class.java,
        "Song_db"
    ).fallbackToDestructiveMigration().build()
    }

    single { get<AppDatabase>().savedSongDao() }

    single { get<AppDatabase>().recentSongDao() }

    single { SongApiService(get()) }

    single { SongRepository(get()) }

    single { SavedSongRepository(get()) }

    single { RecentSongRepo(get()) }

    single { DataStoreManager(androidContext()) }

    viewModel { RecentSongsViewModel(get()) }

    viewModel { HomeViewModel(get()) }

    viewModel { PlayerViewModel(androidContext(),get(),get()) }

    viewModel { SavedSongsViewModel(get(),get()) }

    viewModel { ThemeViewModel(get()) }

}