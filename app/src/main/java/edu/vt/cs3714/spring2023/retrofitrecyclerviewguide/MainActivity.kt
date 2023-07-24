package edu.vt.cs3714.spring2023.retrofitrecyclerviewguide

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.*
import retrofit2.HttpException

class MainActivity : AppCompatActivity() {

    private lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.movie_list)
        val adapter = MovieListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val model = ViewModelProvider(this)[MovieViewModel::class.java]

        model.allMovies.observe(
            this,
            Observer<List<MovieItem>>{ movies ->
                movies?.let{adapter.setMovies(it)}
            }
        )

        (findViewById<Button>(R.id.refresh)).setOnClickListener{
            model.refreshMovies(1)
        }
    }

    /**
     * Make sure to cancel the job when onDestroy() is called.
     */
    override fun onDestroy() {
        super.onDestroy()
        job.cancel()

    }

    /**
     * The adapter for the RecyclerView. Provides the list of items to be displayed there.
     */
    inner class MovieListAdapter :
        RecyclerView.Adapter<MovieListAdapter.MovieViewHolder>() {

        //a list of the movie items to load into the RecyclerView
        private var movies = emptyList<MovieItem>()

        internal fun setMovies(movies: List<MovieItem>) {
            this.movies = movies
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int {
            return movies.size
        }

        /**
         * Creates the view that will hold the content of an item in the recycler view
         *
         * @param parent
         * @param viewType
         * @return
         */
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
            Log.d("T04", "In onCreateViewHolder")
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.card_view, parent, false)
            return MovieViewHolder(v)
        }

        override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {

            Glide.with(this@MainActivity)
                .load(resources.getString(R.string.picture_base_url) + movies[position].poster_path)
                .apply(RequestOptions().override(128, 128))
                .into(holder.view.findViewById(R.id.poster))

            holder.view.findViewById<TextView>(R.id.title).text = movies[position].title

            holder.view.findViewById<TextView>(R.id.rating).text =
                movies[position].vote_average.toString()

            holder.itemView.setOnClickListener {
                Log.d("T04", "MovieViewHolder list tap " + holder.view.findViewById<TextView>(R.id.title).text)
            }
        }

        inner class MovieViewHolder(val view: View) : RecyclerView.ViewHolder(view),
            View.OnClickListener {

            override fun onClick(view: View?) {
                Log.d("T04", "list tap in onClick")
                if (view != null) {
                    //Do some stuff here after the tap
                }
            }




        }

    }
}