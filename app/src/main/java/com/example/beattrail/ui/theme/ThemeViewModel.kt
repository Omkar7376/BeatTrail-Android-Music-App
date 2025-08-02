package com.example.beattrail.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beattrail.datastore.DataStoreManager
import com.example.beattrail.datastore.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ThemeViewModel(private val dataStoreManager: DataStoreManager) : ViewModel() {

    private val _appTheme = MutableStateFlow(ThemeMode.SYSTEM)
    val appTheme: StateFlow<ThemeMode> = _appTheme

    init {
        viewModelScope.launch {
            dataStoreManager.themeModeFlow.collect {
                _appTheme.value = it
            }

        }
    }

    fun setTheme(mode: ThemeMode) {
        viewModelScope.launch {
            dataStoreManager.saveThemeMode(mode)
        }
    }
}