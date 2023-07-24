package edu.vt.cs3714.spring2023.retrofitrecyclerviewguide

import com.google.gson.GsonBuilder
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * The RetrofitService handles the API requests
 *
 */
interface  RetrofitService {

    //this annotation maps an API call to the corresponding Kotlin process.
    @GET("movie/now_playing?language=en-US")
    fun getNowPlaying(@Query("api_key") api_key: String, @Query("page") page: Int ): Observable<Movies>

    companion object {
        fun create(baseUrl: String): RetrofitService {

            val retrofit = Retrofit.Builder().addCallAdapterFactory(RxJava2CallAdapterFactory.create()).addConverterFactory(
                GsonConverterFactory.create(GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create()))
                .baseUrl(baseUrl)
                .build()

            return retrofit.create(RetrofitService::class.java)
        }
    }
}