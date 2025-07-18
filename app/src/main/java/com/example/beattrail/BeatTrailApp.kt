package com.example.beattrail

import android.app.Application
import com.example.beattrail.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class BeatTrailApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@BeatTrailApp)
            modules(appModule)
        }
    }
}