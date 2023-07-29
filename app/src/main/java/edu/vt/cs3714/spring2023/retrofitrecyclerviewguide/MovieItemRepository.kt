package edu.vt.cs3714.spring2023.retrofitrecyclerviewguide

import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

class MovieItemRepository(private val movieDao: MovieItemDao) {

    val allMovies: LiveData<List<MovieItem>> = movieDao.getAllMovies()

    @WorkerThread
    fun moviesByTitle(){
        movieDao.deleteAll()
    }
    @WorkerThread
    fun insert(movie: MovieItem) {


        movieDao.insertMovie(movie)
    }

    @WorkerThread
    fun deleteAll() {
        movieDao.deleteAll()
    }
}