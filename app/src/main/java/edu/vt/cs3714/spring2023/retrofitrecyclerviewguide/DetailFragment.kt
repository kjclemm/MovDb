package edu.vt.cs3714.spring2023.retrofitrecyclerviewguide

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import org.w3c.dom.Text

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


/**
 * A simple [Fragment] subclass.
 * Use the [DetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetailFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var img: String? = null
    private var movieInfo: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_detail, container, false)
        img = this.arguments?.getString("img")
        movieInfo =  this.arguments?.getString("content")
        Log.d("TEXT", movieInfo.toString())
        v.findViewById<TextView>(R.id.textView)?.text  = movieInfo
        Glide.with(this@DetailFragment)
            .load(resources.getString(R.string.picture_base_url) + img)
            .apply(RequestOptions())
            .into(v.findViewById(R.id.imageView))
        return v
    }

}