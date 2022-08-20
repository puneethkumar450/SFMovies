package com.example.sfmovies.ui.moviedetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sfmovies.data.model.MovieDetail
import com.example.sfmovies.data.repositories.MovieDetailRepository
import com.example.sfmovies.util.ApiException
import com.example.sfmovies.util.AppConstant
import com.example.sfmovies.util.NoInternetException
import com.example.sfmovies.util.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovieDetailViewModel(
    private val aRepository: MovieDetailRepository
) : ViewModel() {
    private lateinit var aMovieDetailResponse: MovieDetail

    private val _movieDetailLiveData = MutableLiveData<State<MovieDetail>>()
    val aMovieDetailLiveData: LiveData<State<MovieDetail>>
        get() = _movieDetailLiveData

    fun getMovieDetail(movieTitle: String) {
        _movieDetailLiveData.postValue(State.loading())
        viewModelScope.launch(Dispatchers.IO) {
            try {
                aMovieDetailResponse = aRepository.getMovieDetail(AppConstant.API_KEY, movieTitle)
                withContext(Dispatchers.Main) {
                    _movieDetailLiveData.postValue(State.success(aMovieDetailResponse))
                }
            } catch (e: ApiException) {
                withContext(Dispatchers.Main) {
                    _movieDetailLiveData.postValue(State.error(e.message!!))
                }
            } catch (e: NoInternetException) {
                withContext(Dispatchers.Main) {
                    _movieDetailLiveData.postValue(State.error(e.message!!))
                }
            }
        }
    }

}