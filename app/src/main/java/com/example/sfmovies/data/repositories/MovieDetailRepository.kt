package com.example.sfmovies.data.repositories

import com.example.sfmovies.data.model.MovieDetail
import com.example.sfmovies.data.network.ApiInterface
import com.example.sfmovies.data.network.SafeApiRequest

class MovieDetailRepository(
    private val api: ApiInterface
) : SafeApiRequest() {

    suspend fun getMovieDetail(
        aApiKey: String,
        aTitle: String,
    ): MovieDetail {

        return apiRequest { api.getMovieDetailData(aTitle, aApiKey) }
    }
}