package edu.vt.cs3714.spring2023.retrofitrecyclerviewguide

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.*
import retrofit2.HttpException

class MainActivity : AppCompatActivity() {

    //emulating a repository; this is a list of movieDB movie IDs.
    private val nowPlaying = mutableListOf(
        424,
        11031,
        8005,
        154,
        11,
        238,
        426563,
        463684,
        505192,
        568709,
        491854,
        398173,
        445629
    )

    private val movies = ArrayList<MovieItem>()
    private lateinit var job: Job

    //TODO: add your own key. Hint: https://developers.themoviedb.org/3/getting-started/introduction
    //Make sure to put your API key in /res/values/strings.xml
    //Need this to be lazy, because the reference to R.string.api_key doesn't exist until AFTER
    //onCreate() is executed. By the time we need the apiKey, R.string.api_key exists.
    private val apiKey by lazy {
        resources.getString(R.string.api_key)
    }

    //Creates a handle to the Retrofit Service pointing to the MovieDB URL.
    private val retrofitService by lazy {
        RetrofitService.create(resources.getString(R.string.base_url))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Do some setup on the recyclerView. Get a handle to the RecyclerView,
        //create an instance of the adaptor that will provide the list of items
        //to the recyclerview, and then tell the view to use a linear layout for
        //the items.
        val recyclerView = findViewById<RecyclerView>(R.id.movie_list)
        val adapter = MovieListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        //Tell the adaptor about the movie IDs we want
        adapter.setMovies(movies)

        //Start a job on the IO thread, and fetch each of the movies in turn
        job = CoroutineScope(Dispatchers.IO).launch {

            //Iterate across the movie ID list
            for (i: Int in nowPlaying) {
                //Throtle the calls, so the API doesn't fuss we are making too many, too quickly
                delay(500)

                //Actually get the record
                fetchMovieById(i)

                //Notify the RecyclerView adapter that the data has changed. Since the RecyclerView
                //is on the main/UI thread, we have to set the context to that thread so the adapter
                //can be notified.
                withContext(Dispatchers.Main) {
                    adapter.notifyDataSetChanged()

                }
            }
        }
    }

    /**
     * Calls the retrofit service, and gets the record for the specified ID
     *
     * @param id MovieId to be fetched.
     */
    private suspend fun fetchMovieById(id: Int) {
        val request = retrofitService.getMovieForId(id, apiKey)
        try {
            val movie = request.await()

            movie.let { movies.add(it) }

            Log.d("retrofit_demo", "movie " + id.toString() + " " + movie.toString())
            // Do something with the response.body()
        } catch (e: HttpException) {
            Log.d("retrofit_demo", "HttpException:" + e.toString())
        } catch (e: Throwable) {
            Log.d("retrofit_demo", "ThrowableException:" + e.toString())
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

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
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
                // interact with the item
            }
        }

        inner class MovieViewHolder(val view: View) : RecyclerView.ViewHolder(view),
            View.OnClickListener {
            override fun onClick(view: View?) {
                Log.d("retrofit_demo", "list tap ")
                if (view != null) {
                    //Do some stuff here after the tap
                }
            }


        }

    }
}