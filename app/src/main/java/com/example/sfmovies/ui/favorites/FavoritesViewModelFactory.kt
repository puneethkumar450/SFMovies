package com.example.sfmovies.ui.favorites

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sfmovies.data.repositories.FavoritesRepository

@Suppress("UNCHECKED_CAST")
class FavoritesViewModelFactory(
    private val aRepository: FavoritesRepository,
    private val application: Application
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavoritesViewModel(aRepository, application) as T
    }
}