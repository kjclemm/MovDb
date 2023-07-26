package edu.vt.cs3714.spring2023.retrofitrecyclerviewguide

import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class HomeFragment : Fragment() {
private val model: MovieViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.movie_list)
        val adapter = MovieListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this.context)

        model.allMovies.observe(
            this.viewLifecycleOwner,
            Observer<List<MovieItem>>{ movies ->
                movies.let{adapter.setMovies(it)}
            }
        )

        (view.findViewById<Button>(R.id.refresh)).setOnClickListener{
            model.refreshMovies(1)
        }
    }
    inner class MovieListAdapter :
        RecyclerView.Adapter<HomeFragment.MovieListAdapter.MovieViewHolder>() {

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

            Glide.with(this@HomeFragment)
                .load(resources.getString(R.string.picture_base_url) + movies[position].poster_path)
                .apply(RequestOptions().override(128, 128))
                .into(holder.view.findViewById(R.id.poster))

            holder.view.findViewById<TextView>(R.id.title).text = movies[position].title

            holder.view.findViewById<TextView>(R.id.rating).text =
                movies[position].vote_average.toString()

            holder.itemView.setOnClickListener {
                Log.d("T04", "MovieViewHolder list tap " + holder.view.findViewById<TextView>(R.id.title).text)
                view?.findNavController()?.navigate(R.id.action_homeFragment_to_detailFragment,
                bundleOf("img" to movies[position].poster_path, "content" to holder.view.findViewById<TextView>(R.id.title).text,
                "date" to movies[position].release_date.toString(), "details" to movies[position].overview,
                    "position" to position)
                )
            }
        }

        inner class MovieViewHolder(val view: View) : RecyclerView.ViewHolder(view),
            View.OnClickListener {

            override fun onClick(view: View?) {
                Log.d("T04", "list tap in onClick")
                if (view != null) {
                }
            }
        }
    }
}
