package com.example.beattrail.ui.theme.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.beattrail.data.repo.SongRepository

class HomeViewModelFactory(private val repository: SongRepository) :  ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repository) as T
    }
}
