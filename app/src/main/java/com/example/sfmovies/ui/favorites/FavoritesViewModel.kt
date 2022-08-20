package com.example.sfmovies.ui.favorites

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.sfmovies.AppDatabase
import com.example.sfmovies.data.local.FavMovie
import com.example.sfmovies.data.repositories.FavoritesRepository
import com.example.sfmovies.util.ApiException
import com.example.sfmovies.util.NoInternetException
import com.example.sfmovies.util.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoritesViewModel(private val aRepository: FavoritesRepository,
                         application: Application) : AndroidViewModel(application)
{
    private var mDb: AppDatabase
    private lateinit var mMovieResponse: List<FavMovie>

    init {
        mDb = AppDatabase.DatabaseBuilder.getInstance(application)
    }

    private val _moviesLiveData = MutableLiveData<State<List<FavMovie>>>()
    val mMoviesLiveData: LiveData<State<List<FavMovie>>>
        get() = _moviesLiveData

    fun getFavMovies()
    {
        Log.d("HomeViewModel", " Inside getFavMovies called")
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("HomeViewModel", " Inside getMovies launch")
            try {
                mMovieResponse = aRepository.getMovies()
                withContext(Dispatchers.Main) {
                    if (!mMovieResponse.isNullOrEmpty()) {
                        Log.d("HomeViewModel", " Inside getMovies mMovieResponse == success")
                        _moviesLiveData.postValue(State.success(mMovieResponse))
                    } else{
                        Log.d("HomeViewModel", " Inside getMovies mMovieResponse == failure")
                        _moviesLiveData.postValue(State.error("null"))
                    }
                }
            } catch (e: ApiException) {
                withContext(Dispatchers.Main) {
                    _moviesLiveData.postValue(State.error(e.message!!))
                }
            } catch (e: NoInternetException) {
                withContext(Dispatchers.Main) {
                    _moviesLiveData.postValue(State.error(e.message!!))
                }
            }
        }
    }
}