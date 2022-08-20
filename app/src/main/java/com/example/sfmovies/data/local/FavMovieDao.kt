package com.example.sfmovies.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavMovieDao {

    @Query("SELECT * FROM FavMovie")
    suspend fun getAll(): List<FavMovie>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert( FavMovie: FavMovie)

    @Query("SELECT selected FROM FavMovie WHERE imdbID =:id")
    fun getIsSelected(id: String): Boolean

    @Query("DELETE FROM FavMovie WHERE imdbID = :id")
    fun delete(id: String): Int
}