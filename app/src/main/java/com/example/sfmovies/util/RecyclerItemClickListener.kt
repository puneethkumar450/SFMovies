package com.example.sfmovies.util

import android.view.View
import com.example.sfmovies.data.model.SearchResults

interface RecyclerItemClickListener {
    fun onItemClick(view: View, position: Int)
    fun onFavClick(view: View, position: Int)
    fun getIsSelected(adapterPosition: Int, movie: SearchResults.SearchItem)
}