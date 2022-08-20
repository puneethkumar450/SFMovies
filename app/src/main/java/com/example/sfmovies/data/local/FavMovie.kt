package com.example.sfmovies.data.local

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class FavMovie(
    @PrimaryKey val imdbID: String,
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "year") val year: String?,
    @ColumnInfo(name = "type") val type: String?,
    @ColumnInfo(name = "poster") val poster: String?,
    @ColumnInfo(name = "selected") var selected: Boolean
):Parcelable