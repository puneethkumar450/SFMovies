package com.example.sfmovies.ui.favorites

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sfmovies.AppDatabase
import com.example.sfmovies.R
import com.example.sfmovies.data.model.SearchResults
import com.example.sfmovies.databinding.ActivityFavoritesBinding
import com.example.sfmovies.ui.adapter.FavoritesAdapter
import com.example.sfmovies.ui.moviedetail.MovieDetailScrollingActivity
import com.example.sfmovies.util.AppConstant
import com.example.sfmovies.util.RecyclerItemClickListener
import com.example.sfmovies.util.State
import com.example.sfmovies.util.hide
import com.example.sfmovies.util.show
import com.example.sfmovies.util.showToast
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class FavoritesActivity  : AppCompatActivity(), KodeinAware {
    override val kodein by kodein()
    private val mDb: AppDatabase by instance()
    private lateinit var mFavDataBind: ActivityFavoritesBinding
    private lateinit var mFavoritesAdapter: FavoritesAdapter
    private lateinit var mFavoritesViewModel: FavoritesViewModel
    private val mFavViewModelFactory: FavoritesViewModelFactory by instance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFavDataBind = DataBindingUtil.setContentView(this, R.layout.activity_favorites)
        mFavoritesViewModel = ViewModelProvider(this, mFavViewModelFactory).get(FavoritesViewModel::class.java)
        setupUI()
        setupDbCall()
        mFavoritesViewModel.getFavMovies()
    }

    private fun setupUI()
    {
        Log.w("HomeActivity", "Inside Setup UI")
        val lItemClick = object : RecyclerItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                if (mFavoritesAdapter.getData().isNotEmpty()) {
                    val searchItem = mFavoritesAdapter.getData()[position]
                    searchItem.let {
                        val intent =
                            Intent(
                                applicationContext,
                                MovieDetailScrollingActivity::class.java)
                        intent.putExtra(AppConstant.INTENT_SELECTED_MOVIE, searchItem)
                        startActivity(intent)
                    }
                }
            }

            override fun onFavClick(view: View, position: Int) {
                if (mFavoritesAdapter.getData().isNotEmpty())
                {
                    val lItem = mFavoritesAdapter.getData()[position]
                    mDb.FavMovieDao().delete(lItem.imdbID)
                    mFavoritesAdapter.getData().remove(lItem)
                    mFavoritesAdapter.notifyItemRemoved(position)
                }
            }

            override fun getIsSelected(adapterPosition: Int, movie: SearchResults.SearchItem) {
                //Log.d("HomeViewModel", " Inside get selected lvalue : ${movie.isSelected}")
            }
        }

        mFavoritesAdapter = FavoritesAdapter(lItemClick)

        mFavDataBind.recyclerViewFavMovies.apply {
            layoutManager = LinearLayoutManager(context)
            itemAnimator = DefaultItemAnimator()
            adapter = mFavoritesAdapter
        }
    }

    private fun setupDbCall()
    {
        Log.d("HomeActivity", "setupAPICall initializeObserver")
        mFavoritesViewModel.mMoviesLiveData.observe(this, Observer { state ->
            when (state) {
                is State.Loading -> {
                    mFavDataBind.recyclerViewFavMovies.hide()
                    mFavDataBind.progressBar.show()
                }
                is State.Success -> {
                    mFavDataBind.recyclerViewFavMovies.show()
                    mFavDataBind.progressBar.hide()
                    mFavoritesAdapter.setData(state.data)
                }
                is State.Error -> {
                    mFavDataBind.progressBar.hide()
                    showToast(state.message)
                }
            }
        })

    }
}