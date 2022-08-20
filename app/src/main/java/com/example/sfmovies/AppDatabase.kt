package com.example.sfmovies

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.sfmovies.data.local.FavMovie
import com.example.sfmovies.data.local.FavMovieDao

@Database(entities = [FavMovie::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun FavMovieDao(): FavMovieDao


    object DatabaseBuilder {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase
        {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = buildRoomDB(context)
                }
            }
            return INSTANCE!!
        }

        private fun buildRoomDB(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "FavMovieDB"
            ).allowMainThreadQueries().build()
    }
}


