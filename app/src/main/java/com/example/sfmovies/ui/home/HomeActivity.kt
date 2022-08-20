package com.example.sfmovies.ui.home

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sfmovies.AppDatabase
import com.example.sfmovies.R
import com.example.sfmovies.data.local.FavMovie
import com.example.sfmovies.data.model.SearchResults
import com.example.sfmovies.databinding.ActivityHomeBinding
import com.example.sfmovies.ui.adapter.CustomAdapterMovies
import com.example.sfmovies.ui.favorites.FavoritesActivity
import com.example.sfmovies.ui.moviedetail.MovieDetailScrollingActivity
import com.example.sfmovies.util.AppConstant
import com.example.sfmovies.util.NetworkUtils
import com.example.sfmovies.util.RecyclerItemClickListener
import com.example.sfmovies.util.State
import com.example.sfmovies.util.dismissKeyboard
import com.example.sfmovies.util.getColorRes
import com.example.sfmovies.util.hide
import com.example.sfmovies.util.show
import com.example.sfmovies.util.showToast
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class HomeActivity : AppCompatActivity(), KodeinAware {

    companion object {
        const val ANIMATION_DURATION = 1000.toLong()
    }
    override val kodein by kodein()

    private lateinit var mHomeDataBind: ActivityHomeBinding
    private lateinit var mHomeViewModel: HomeViewModel
    private val mHomeViewModelFactory: HomeViewModelFactory by instance()
    private val mDb: AppDatabase by instance()
    private lateinit var mCustomAdapterMovies: CustomAdapterMovies
    private lateinit var mSearchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mHomeDataBind = DataBindingUtil.setContentView(this, R.layout.activity_home)
        mHomeViewModel = ViewModelProvider(this, mHomeViewModelFactory).get(HomeViewModel::class.java)
        setupUI()
        initializeObserver()
        handleNetworkChanges()
        setupAPICall()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search, menu)
        val lFavItem =  menu.findItem(R.id.item_fav) as MenuItem
        lFavItem.setOnMenuItemClickListener {
            moveToFav()
            true
        }
        mSearchView = menu.findItem(R.id.search).actionView as SearchView
        mSearchView.apply {
            queryHint = "Search"
            isSubmitButtonEnabled = true
            onActionViewExpanded()
        }
        search()
        return true
    }

    private fun moveToFav() {
        val intent = Intent(applicationContext, FavoritesActivity::class.java)
        startActivity(intent)
    }

    private fun setupUI()
    {
        Log.w("HomeActivity", "Inside Setup UI")
        val lItemClick = object : RecyclerItemClickListener {
                override fun onItemClick(view: View, position: Int) {
                    if (mCustomAdapterMovies.getData().isNotEmpty()) {
                        val searchItem = mCustomAdapterMovies.getData()[position]
                        searchItem?.let {
                            val intent =
                                Intent(
                                    applicationContext,
                                    MovieDetailScrollingActivity::class.java)
                            val lItem = FavMovie(
                                imdbID = it.imdbID,
                                title = it.title,
                                year = it.year,
                                type = it.type,
                                poster = it.poster,
                                selected = true
                            )
                            intent.putExtra(AppConstant.INTENT_SELECTED_MOVIE, lItem)
                            startActivity(intent)
                        }

                    }
                }

                override fun onFavClick(view: View, position: Int) {
                    if (mCustomAdapterMovies.getData().isNotEmpty())
                    {
                        val searchItem = mCustomAdapterMovies.getData()[position]
                        if(searchItem!!.isSelected){
                            lifecycleScope.launch(IO) {
                                mDb.FavMovieDao().insert(FavMovie(
                                    imdbID = searchItem!!.imdbID,
                                    title = searchItem.title,
                                    year = searchItem.year,
                                    type = searchItem.type,
                                    poster = searchItem.poster,
                                    selected = searchItem.isSelected
                                ))
                            }
                        }else{
                            lifecycleScope.launch(IO) {
                                mDb.FavMovieDao().delete(searchItem!!.imdbID)
                            }
                        }
                    }
                }

            override fun getIsSelected(adapterPosition: Int, movie: SearchResults.SearchItem) {
                //Log.d("HomeViewModel", " Inside get selected lvalue : ${movie.isSelected}")
            }
        }

        mHomeViewModel.mMovieIsSelected.observe(this, Observer {

        })

        mCustomAdapterMovies = CustomAdapterMovies(lItemClick)

        mHomeDataBind.recyclerViewMovies.apply {
            layoutManager = LinearLayoutManager(context)
            itemAnimator = DefaultItemAnimator()
            adapter = mCustomAdapterMovies
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
                    val visibleItemCount = layoutManager!!.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                    mHomeViewModel.checkForLoadMoreItems(
                        visibleItemCount,
                        firstVisibleItemPosition,
                        totalItemCount)
                }
            })
        }
    }

    private fun setupAPICall()
    {
        Log.d("HomeActivity", "setupAPICall initializeObserver")
        mHomeViewModel.mMoviesLiveData.observe(this, Observer { state ->
            when (state) {
                is State.Loading -> {
                    mHomeDataBind.recyclerViewMovies.hide()
                    mHomeDataBind.linearLayoutSearch.hide()
                    mHomeDataBind.progressBar.show()
                }
                is State.Success -> {
                    mHomeDataBind.recyclerViewMovies.show()
                    mHomeDataBind.linearLayoutSearch.hide()
                    mHomeDataBind.progressBar.hide()
                    mCustomAdapterMovies.setData(state.data)
                }
                is State.Error -> {
                    mHomeDataBind.progressBar.hide()
                    showToast(state.message)
                }
            }
        })

    }

    private fun initializeObserver()
    {
        Log.d("HomeActivity", "Inside initializeObserver")
        mHomeViewModel.mMovieNameLiveData.observe(this, Observer {
        })
        mHomeViewModel.mLoadMoreListLiveData.observe(this, Observer {
            if (it) {
                mCustomAdapterMovies.setData(null)
                lifecycleScope.launch(){
                    delay(2000L)
                    mHomeViewModel.loadMore()
                }
            }
        })
    }

    private fun handleNetworkChanges()
    {
        Log.d("HomeActivity", "Inside handleNetworkChanges")
        NetworkUtils.getNetworkLiveData(applicationContext).observe(this, Observer { isConnected ->
            if (!isConnected) {
                mHomeDataBind.textViewNetworkStatus.text = getString(R.string.text_no_connectivity)
                mHomeDataBind.networkStatusLayout.apply {
                    show()
                    setBackgroundColor(getColorRes(R.color.colorStatusNotConnected))
                }
            } else {
                if (mHomeViewModel.mMoviesLiveData.value is State.Error || mCustomAdapterMovies.itemCount == 0) {
                    mHomeViewModel.getMovies()
                }
                mHomeDataBind.textViewNetworkStatus.text = getString(R.string.text_connectivity)
                mHomeDataBind.networkStatusLayout.apply {
                    setBackgroundColor(getColorRes(R.color.colorStatusConnected))
                    animate()
                        .alpha(1f)
                        .setStartDelay(ANIMATION_DURATION)
                        .setDuration(ANIMATION_DURATION)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                hide()
                            }
                        })
                }
            }
        })
    }

    private fun search()
    {
        Log.d("HomeActivity", "Inside search")
        mSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                dismissKeyboard(mSearchView)
                mSearchView.clearFocus()
                mHomeViewModel.searchMovie(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
    }
}