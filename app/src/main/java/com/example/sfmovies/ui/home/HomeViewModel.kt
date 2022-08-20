package com.example.sfmovies.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.sfmovies.AppDatabase
import com.example.sfmovies.data.model.SearchResults
import com.example.sfmovies.data.repositories.HomeRepository
import com.example.sfmovies.util.ApiException
import com.example.sfmovies.util.AppConstant
import com.example.sfmovies.util.NoInternetException
import com.example.sfmovies.util.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(private val aRepository: HomeRepository, application: Application)
    : AndroidViewModel(application)
{
    private var mTotalMovies = 0
    private var mPageIndex = 0
    private var mMovieList = ArrayList<SearchResults.SearchItem?>()
    private lateinit var mMovieResponse: SearchResults
    private var mDb: AppDatabase

    private val _isSelected = MutableLiveData<Boolean>()
    val mMovieIsSelected: LiveData<Boolean>
        get() = _isSelected


    private val _moviesLiveData = MutableLiveData<State<ArrayList<SearchResults.SearchItem?>>>()
    val mMoviesLiveData: LiveData<State<ArrayList<SearchResults.SearchItem?>>>
        get() = _moviesLiveData

    private val _movieNameLiveData = MutableLiveData<String>()
    val mMovieNameLiveData: LiveData<String>
        get() = _movieNameLiveData

    private val _loadMoreListLiveData = MutableLiveData<Boolean>()
    val mLoadMoreListLiveData: LiveData<Boolean>
        get() = _loadMoreListLiveData

    init {
        mDb = AppDatabase.DatabaseBuilder.getInstance(application)
        _loadMoreListLiveData.value = false
        _movieNameLiveData.value = ""
    }

    fun getMovies()
    {
        Log.d("HomeViewModel", " Inside getMovies called")
        if (mPageIndex == 1) {
            mMovieList.clear()
            _moviesLiveData.postValue(State.loading())
        } else {
            if (mMovieList.isNotEmpty() && mMovieList.last() == null) {
                mMovieList.removeAt(mMovieList.size - 1)
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            Log.d("HomeViewModel", " Inside getMovies launch")
            if (_movieNameLiveData.value != null && _movieNameLiveData.value!!.isNotEmpty())
            {
                Log.d("HomeViewModel", " Inside getMovies _movieNameLiveData != null")
                try {
                    mMovieResponse = aRepository.getMovies(
                        AppConstant.API_KEY,
                        _movieNameLiveData.value!!,
                        mPageIndex
                    )
                    withContext(Dispatchers.Main) {
                        if (mMovieResponse.response == AppConstant.SUCCESS) {
                            Log.d("HomeViewModel", " Inside getMovies mMovieResponse == success")
                            if (mMovieResponse.search.isNotEmpty()) {
                                mMovieResponse.search.forEach { _item ->
                                    _item!!.isSelected = mDb.FavMovieDao().getIsSelected(_item.imdbID)
                                }
                            }
                            mMovieList.addAll(mMovieResponse.search)
                            mTotalMovies = mMovieResponse.totalResults.toInt()
                            _moviesLiveData.postValue(State.success(mMovieList))
                            _loadMoreListLiveData.value = false
                        } else{
                            Log.d("HomeViewModel", " Inside getMovies mMovieResponse == failure")
                            _moviesLiveData.postValue(State.error(mMovieResponse.error))
                        }
                    }
                } catch (e: ApiException) {
                    withContext(Dispatchers.Main) {
                        _moviesLiveData.postValue(State.error(e.message!!))
                        _loadMoreListLiveData.value = false
                    }
                } catch (e: NoInternetException) {
                    withContext(Dispatchers.Main) {
                        _moviesLiveData.postValue(State.error(e.message!!))
                        _loadMoreListLiveData.value = false
                    }
                }
            }
        }
    }

    fun searchMovie(query: String) {
        Log.d("HomeViewModel", " Inside searchMovie query ")
        _movieNameLiveData.value = query
        mPageIndex = 1
        mTotalMovies = 0
        getMovies()
    }

    fun loadMore() {
        Log.d("HomeViewModel", " Inside loadMore query ")
        mPageIndex++
        getMovies()
    }

    fun checkForLoadMoreItems(
        aVisibleItemCount: Int,
        aFirstVisibleItemPosition: Int,
        aTotalItemCount: Int)
    {
        //Log.d("HomeViewModel", " Inside checkForLoadMoreItems ")
        if (!_loadMoreListLiveData.value!! && (aTotalItemCount < mTotalMovies)) {
            if (aVisibleItemCount + aFirstVisibleItemPosition >= aTotalItemCount && aFirstVisibleItemPosition >= 0) {
                _loadMoreListLiveData.value = true
            }
        }
    }

    fun getIsSelected(aitem: SearchResults.SearchItem) :Boolean{
       return mDb.FavMovieDao().getIsSelected(aitem.imdbID)
    }
}