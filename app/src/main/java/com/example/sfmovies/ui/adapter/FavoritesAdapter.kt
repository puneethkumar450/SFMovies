package com.example.sfmovies.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.sfmovies.R
import com.example.sfmovies.data.local.FavMovie
import com.example.sfmovies.util.RecyclerItemClickListener

class FavoritesAdapter(val aItemClick: RecyclerItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    private var mMoviesList = ArrayList<FavMovie>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(aHolder: RecyclerView.ViewHolder, position: Int) {
        if (aHolder is MovieViewHolder) {
            aHolder.bindItems(mMoviesList[position]!!,aItemClick)
        }
    }

    override fun getItemViewType(aPosition: Int): Int {
        return VIEW_TYPE_ITEM
    }

    override fun getItemCount(): Int {
        return mMoviesList.size
    }

    fun setData(aNewMoviesList: List<FavMovie>?)
    {
        if (aNewMoviesList != null) {
            mMoviesList.clear()
            mMoviesList.addAll(aNewMoviesList)
        } else {
            mMoviesList.addAll(ArrayList())
        }
        notifyDataSetChanged()
    }

    class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imagePoster: ImageView = itemView.findViewById(R.id.image_poster)
        private val textTitle: TextView = itemView.findViewById(R.id.text_title)
        private val textYear: TextView = itemView.findViewById(R.id.text_year)
        private val moviefav: ImageView = itemView.findViewById(R.id.movie_fav)

        fun bindItems(movie: FavMovie, aItemClick: RecyclerItemClickListener) {
            textTitle.text = movie.title
            textYear.text = movie.year
            moviefav.visibility = View.GONE

            itemView.setOnClickListener {
                aItemClick.onItemClick(itemView, adapterPosition)
            }

            Glide.with(imagePoster.context).load(movie.poster)
                .centerCrop()
                .thumbnail(0.1f)
                .placeholder(R.drawable.ic_baseline_local_movies_24)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imagePoster)
        }
    }

    fun getData() = mMoviesList

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_LOADING = 1
    }
}