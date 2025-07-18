package com.example.beattrail.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.beattrail.data.local.Dao.SavedSongDao
import com.example.beattrail.data.local.entity.SavedSongEntity

@Database(
    entities = [SavedSongEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun savedSongDao(): SavedSongDao

    companion object {
        fun getDatabase(context: Context) : AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "Song_db")
                .fallbackToDestructiveMigration()
                .build()
    }
}
