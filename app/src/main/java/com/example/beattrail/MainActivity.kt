package com.example.beattrail

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.beattrail.datastore.ThemeMode
import com.example.beattrail.ui.theme.BeatTrailTheme
import com.example.beattrail.ui.theme.ThemeViewModel
import com.example.beattrail.ui.theme.navigation.AppNav
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val themeViewModel: ThemeViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val themeMode by themeViewModel.appTheme.collectAsState()

            val isDarkTheme = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }
            BeatTrailTheme(darkTheme = isDarkTheme) {
                AppNav(themeViewModel = themeViewModel)
            }
        }
    }
}

