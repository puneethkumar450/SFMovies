package com.example.sfmovies.ui.moviedetail

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.sfmovies.R
import com.example.sfmovies.data.local.FavMovie
import com.example.sfmovies.databinding.ActivityMovieDetailScrollingBinding
import com.example.sfmovies.ui.home.HomeActivity
import com.example.sfmovies.util.AppConstant
import com.example.sfmovies.util.NetworkUtils
import com.example.sfmovies.util.State
import com.example.sfmovies.util.getColorRes
import com.example.sfmovies.util.hide
import com.example.sfmovies.util.show
import com.example.sfmovies.util.showToast
import kotlinx.android.synthetic.main.activity_movie_detail_scrolling.toolbar
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class MovieDetailScrollingActivity : AppCompatActivity(), KodeinAware {

    override val kodein by kodein()
    private lateinit var mMovieDetailsViewModel: MovieDetailViewModel
    private lateinit var mDetailsDataBinding: ActivityMovieDetailScrollingBinding
    private val mMovieDetailsFactory: MovieDetailViewModelFactory by instance()
    private lateinit var mFavMovie :FavMovie

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDetailsDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail_scrolling)
        mMovieDetailsViewModel = ViewModelProvider(this, mMovieDetailsFactory).get(
            MovieDetailViewModel::class.java)
        setSupportActionBar(toolbar)
        setupUI()
        handleNetworkChanges()
        setupAPICall()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun setupUI()
    {
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        if (intent.getParcelableExtra<FavMovie>(AppConstant.INTENT_SELECTED_MOVIE) != null){
            mFavMovie = intent.getParcelableExtra(AppConstant.INTENT_SELECTED_MOVIE)!!
        }
        mDetailsDataBinding.toolbar.title = mFavMovie.title
        mDetailsDataBinding.title.text = mFavMovie.title
        if(mFavMovie.selected){
            mDetailsDataBinding.favMovie.setImageResource(R.drawable.ic_baseline_favorite_24_filled)
        }else{
            mDetailsDataBinding.favMovie.setImageResource(R.drawable.ic_baseline_favorite_border_24)
        }

        Glide.with(this).load(mFavMovie.poster)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(mDetailsDataBinding.imagePoster)
    }

    private fun setupAPICall()
    {
        mMovieDetailsViewModel.aMovieDetailLiveData.observe(this, Observer { state ->
            when (state) {
                is State.Loading -> {
                    mDetailsDataBinding.progressBar.show()
                    mDetailsDataBinding.cardViewMovieDetail.hide()
                }
                is State.Success -> {
                    mDetailsDataBinding.progressBar.hide()
                    mDetailsDataBinding.cardViewMovieDetail.show()
                    state.data.let {
                        mDetailsDataBinding.textYear.text = "Year: ${it.year}"
                        mDetailsDataBinding.textDirector.text = "Director: ${it.director}"
                        mDetailsDataBinding.textWriter.text = "Writer: ${it.writer}"
                        mDetailsDataBinding.textPlot.text = it.plot
                        if (it.ratings.isNotEmpty())
                            mDetailsDataBinding.textImd.text =
                                "Internet Movie Database: ${it.ratings[0].value}"
                        mDetailsDataBinding.textMetascore.text = "Metascore: ${it.metascore}"
                        mDetailsDataBinding.textImdbRating.text = "IMBD Rating: ${it.imdbrating}"
                    }
                }
                is State.Error -> {
                    mDetailsDataBinding.progressBar.hide()
                    mDetailsDataBinding.cardViewMovieDetail.hide()
                    showToast(state.message)
                }
            }
        })
        getMoviesDetail(mFavMovie.title.toString())
    }

    private fun handleNetworkChanges()
    {
        NetworkUtils.getNetworkLiveData(applicationContext).observe(this, Observer { isConnected ->
            if (!isConnected) {
                mDetailsDataBinding.textViewNetworkStatus.text = getString(R.string.text_no_connectivity)
                mDetailsDataBinding.networkStatusLayout.apply {
                    show()
                    setBackgroundColor(getColorRes(R.color.colorStatusNotConnected))
                }
            } else {
                if (mMovieDetailsViewModel.aMovieDetailLiveData.value is State.Error) {
                    getMoviesDetail(mFavMovie.title.toString())
                }
                mDetailsDataBinding.textViewNetworkStatus.text = getString(R.string.text_connectivity)
                mDetailsDataBinding.networkStatusLayout.apply {
                    setBackgroundColor(getColorRes(R.color.colorStatusConnected))

                    animate()
                        .alpha(1f)
                        .setStartDelay(HomeActivity.ANIMATION_DURATION)
                        .setDuration(HomeActivity.ANIMATION_DURATION)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                hide()
                            }
                        })
                }
            }
        })
    }

    private fun getMoviesDetail(movieTitle: String) {
        mMovieDetailsViewModel.getMovieDetail(movieTitle)
    }
}
