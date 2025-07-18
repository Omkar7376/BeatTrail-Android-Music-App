package com.example.beattrail

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.beattrail.ui.theme.BeatTrailTheme
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import com.example.beattrail.di.appModule
import com.example.beattrail.ui.theme.navigation.AppNav

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            BeatTrailTheme {
                AppNav()
            }
        }
    }
}

