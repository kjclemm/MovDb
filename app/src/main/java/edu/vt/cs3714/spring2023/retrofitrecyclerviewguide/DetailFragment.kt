package edu.vt.cs3714.spring2023.retrofitrecyclerviewguide

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import org.w3c.dom.Text

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

class DetailFragment : Fragment() {
    private val model: MovieViewModel by activityViewModels()
    private var img: String? = null
    private var movieTitle: String? = null
    private var movieDate: String? = null
    private var movieDetails: String? = null
    private var position: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_detail, container, false)
        img = this.arguments?.getString("img")
        movieTitle =  this.arguments?.getString("content")
        movieDate = this.arguments?.getString("date")
        movieDetails = this.arguments?.getString("details")
        position = this.arguments?.getInt("position")
        val likeButton: Button = v.findViewById<Button>(R.id.likeButton)
        if(model.allMovies.value!![position!!].liked) {
            likeButton.text = "Unlike"
        }
        likeButton.setOnClickListener(){
            var isLiked: Boolean = model.allMovies.value!![position!!].liked
            if(isLiked){
                model.allMovies.value!![position!!].liked = false
                likeButton.text = "Like"
                Log.d("UNLIKED", model.allMovies.value!![position!!].liked.toString())
            }
            else{
                model.allMovies.value!![position!!].liked = true
                likeButton.text = "Unlike"
                Log.d("LIKED", model.allMovies.value!![position!!].liked.toString())
            }
        }
//        position?.let { model.allMovies.value?.get(it)?.liked } ?: true
        Log.d("LIKED", position.toString())
        v.findViewById<TextView>(R.id.details).text = movieDetails
        v.findViewById<TextView>(R.id.yearOut).text = movieDate
        v.findViewById<TextView>(R.id.movieTitle)?.text  = movieTitle
        Glide.with(this@DetailFragment)
            .load(resources.getString(R.string.picture_base_url) + img)
            .apply(RequestOptions())
            .into(v.findViewById(R.id.imageView))
        return v
    }

}