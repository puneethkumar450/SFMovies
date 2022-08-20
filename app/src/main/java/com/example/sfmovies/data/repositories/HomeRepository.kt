package com.example.sfmovies.data.repositories

import com.example.sfmovies.data.model.SearchResults
import com.example.sfmovies.data.network.ApiInterface
import com.example.sfmovies.data.network.SafeApiRequest

class HomeRepository(
    private val aApi: ApiInterface
) : SafeApiRequest() {

    suspend fun getMovies(
        aApiKey: String,
        aSearchTitle: String,
        aPageIndex: Int
    ): SearchResults {
        return apiRequest { aApi.getSearchResultData(aSearchTitle, aApiKey, aPageIndex) }
    }
}