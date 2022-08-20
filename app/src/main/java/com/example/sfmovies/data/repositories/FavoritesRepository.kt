package com.example.sfmovies.data.repositories

import com.example.sfmovies.AppDatabase
import com.example.sfmovies.data.local.FavMovie

class FavoritesRepository(private val aDb: AppDatabase)
{
    suspend fun getMovies(): List<FavMovie> {
        return aDb.FavMovieDao().getAll()
    }
}