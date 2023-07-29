package edu.vt.cs3714.spring2023.retrofitrecyclerviewguide

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Filter
import android.widget.Filterable
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class HomeFragment : Fragment(), AdapterView.OnItemSelectedListener {
private val model: MovieViewModel by activityViewModels()
    var adapterRecycle = MovieListAdapter()

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
        recyclerView.adapter = adapterRecycle
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        val spinner: Spinner = view.findViewById(R.id.sortSelector)
        spinner.onItemSelectedListener = this
        ArrayAdapter.createFromResource(this.requireContext(), R.array.sorters, android.R.layout.simple_spinner_item)
            .also{adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
                spinner.adapter = adapter
            }
        model.allMovies.observe(
            this.viewLifecycleOwner,
            Observer<List<MovieItem>>{ movies ->
                movies.let{adapterRecycle.setMovies(it)}
            }
        )
        (view.findViewById<Button>(R.id.refresh)).setOnClickListener{
            model.refreshMovies(1)
            spinner.setSelection(0)
            model.movieTitles.clear()
            model.filterOn = false
        }

        val filterButton: Button = view.findViewById<Button>(R.id.filterButton)
        if(!model.filterOn){
            filterButton.text="Filter Liked"
        }else{
            filterButton.text="Turn Off Filter"
        }
        filterButton.setOnClickListener{
            model.allMovies.value?.let{adapterRecycle.setMovies(it)}
            if(!model.movieTitles.isNullOrEmpty() && !model.filterOn) {
                model.filterOn = true
                adapterRecycle.filter.filter("")
                filterButton.text = "Turn Off Filter"
            }else if(!model.movieTitles.isNullOrEmpty() &&model.filterOn){
                model.filterOn = false
                filterButton.text = "Filter Liked"
            }else{
                Toast.makeText(requireView().context, "No Liked Movies to Filter", Toast.LENGTH_SHORT).show()
            }
        }
    }

    inner class MovieListAdapter :
        RecyclerView.Adapter<HomeFragment.MovieListAdapter.MovieViewHolder>(), Filterable {

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

        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(str: CharSequence?): FilterResults {
                    val search = str.toString() // takes the given characters and makes them into a string
                    val movieFilterList: ArrayList<MovieItem> = if (model.movieTitles.isNullOrEmpty()) { //empty search box
                        movies as ArrayList<MovieItem> // when empty filter does not change
                    } else {
                        val resultList = ArrayList<MovieItem>()
                        for (i in movies) { // loops through all movies
                            if (model.movieTitles.contains(i.title)) {
                                resultList.add(i)
                            }
                        }
                        resultList
                    }
                    val filterResults = FilterResults() //establishes final filter
                    filterResults.values = movieFilterList
                    return filterResults // returns the filtered list
                }

                /**
                 * updates local temporary list for recycler view and changes the recycler view
                 */
                override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                    if(model.filterOn) {
                        movies = (results?.values as List<MovieItem>?)!!
                        notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        var sort: String = p0?.getItemAtPosition(p2).toString()
            if (sort == "Sort By Score") {
                var tempList = model.allMovies.value?.sortedByDescending { it.vote_average }
//                Log.d("SORTED", tempList?.get(0)?.vote_average.toString())
                adapterRecycle.setMovies(tempList!!)
            } else if (sort == "Sort By Title") {
                var tempList = model.allMovies.value?.sortedBy { it.title }
         //       Log.d("SORTED", tempList?.get(0)?.vote_average.toString())
                adapterRecycle.setMovies(tempList!!)
            } else {
                var tempList = model.allMovies.value?.sortedByDescending { it.release_date }
                //    Log.d("SORTED", tempList?.get(0)?.vote_average.toString())
                adapterRecycle.setMovies(tempList!!)
            }
        if(model.filterOn && !model.movieTitles.isNullOrEmpty()){
            adapterRecycle.filter.filter("")
        }else if(model.filterOn && model.movieTitles.isNullOrEmpty()){
            model.filterOn = false
            requireView().findViewById<Button>(R.id.filterButton).text = "Filter Liked"
            Toast.makeText(requireView().context, "No Liked Movies to Filter", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }
}
