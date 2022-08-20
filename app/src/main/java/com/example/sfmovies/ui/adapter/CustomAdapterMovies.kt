package com.example.sfmovies.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.sfmovies.R
import com.example.sfmovies.data.model.SearchResults
import com.example.sfmovies.util.RecyclerItemClickListener
import com.example.sfmovies.util.show

class CustomAdapterMovies(val aItemClick: RecyclerItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    private var mMoviesList = ArrayList<SearchResults.SearchItem?>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == VIEW_TYPE_ITEM) {
            val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_movie, parent, false)
            MovieViewHolder(view)
        } else {
            val lItemView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_lazy_loading, parent, false)
            LoadingViewHolder(lItemView)
        }
    }

    override fun onBindViewHolder(aHolder: RecyclerView.ViewHolder, position: Int) {

        if (aHolder is LoadingViewHolder) {
            aHolder.showLoadingView()
        }else if (aHolder is MovieViewHolder) {
            if (mMoviesList[position] != null) {
                aHolder.bindItems(mMoviesList[position]!!,aItemClick)
            }
        }
    }

    override fun getItemViewType(aPosition: Int): Int {
        return if (mMoviesList[aPosition] == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    override fun getItemCount(): Int {
        return mMoviesList.size
    }

    fun setData(aNewMoviesList: ArrayList<SearchResults.SearchItem?>?)
    {
        if (aNewMoviesList != null) {
            if (!mMoviesList.isNullOrEmpty()) {
                mMoviesList.removeAt(mMoviesList.size - 1)
            }
            mMoviesList.clear()
            mMoviesList.addAll(aNewMoviesList)
        } else {
            mMoviesList.add(aNewMoviesList)
        }
        notifyDataSetChanged()
    }

    class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imagePoster: ImageView = itemView.findViewById(R.id.image_poster)
        private val textTitle: TextView = itemView.findViewById(R.id.text_title)
        private val textYear: TextView = itemView.findViewById(R.id.text_year)
        private val moviefav: ImageView = itemView.findViewById(R.id.movie_fav)
       // private val lDB = AppDatabase.DatabaseBuilder.getInstance(itemView.context.applicationContext)
        fun bindItems(movie: SearchResults.SearchItem, aItemClick: RecyclerItemClickListener) {
            textTitle.text = movie.title
            textYear.text = movie.year
            //movie.isSelected = lDB.FavMovieDao().getIsSelected(movie.imdbID)
            if(movie.isSelected){
                moviefav.setImageResource(R.drawable.ic_baseline_favorite_24_filled)
            }else{
                moviefav.setImageResource(R.drawable.ic_baseline_favorite_border_24)
            }

            moviefav.setOnClickListener {
                movie.isSelected = !movie.isSelected
                if(movie.isSelected){
                    moviefav.setImageResource(R.drawable.ic_baseline_favorite_24_filled)
                }else {
                    moviefav.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                }
                aItemClick.onFavClick(itemView, adapterPosition)
            }

            itemView.setOnClickListener {
                aItemClick.onItemClick(itemView, adapterPosition)
            }
            Glide.with(imagePoster.context).load(movie.poster)
                .centerCrop()
                .thumbnail(0.5f)
                .placeholder(R.drawable.ic_launcher_background)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imagePoster)
        }
    }

    fun getData() = mMoviesList




    class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)

        fun showLoadingView() {
            progressBar.show()
        }
    }


    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_LOADING = 1
    }
}