package com.example.sfmovies

import android.app.Application
import com.example.sfmovies.data.network.ApiInterface
import com.example.sfmovies.data.network.NetworkConnectionInterceptor
import com.example.sfmovies.data.repositories.FavoritesRepository
import com.example.sfmovies.data.repositories.HomeRepository
import com.example.sfmovies.data.repositories.MovieDetailRepository
import com.example.sfmovies.ui.favorites.FavoritesViewModelFactory
import com.example.sfmovies.ui.home.HomeViewModelFactory
import com.example.sfmovies.ui.moviedetail.MovieDetailViewModelFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class SFApplication : Application(), KodeinAware {

    override val kodein = Kodein.lazy {
        import(androidXModule(this@SFApplication))

        bind() from singleton { NetworkConnectionInterceptor(instance()) }
        bind() from singleton { ApiInterface(instance()) }

        bind() from singleton { AppDatabase.DatabaseBuilder.getInstance(instance()) }

        bind() from provider { HomeViewModelFactory(instance(),this@SFApplication) }
        bind() from provider { MovieDetailViewModelFactory(instance()) }
        bind() from provider { FavoritesViewModelFactory(instance(), this@SFApplication) }

        bind() from singleton { HomeRepository(instance()) }
        bind() from singleton { MovieDetailRepository(instance()) }
        bind() from singleton { FavoritesRepository(instance()) }


    }
}