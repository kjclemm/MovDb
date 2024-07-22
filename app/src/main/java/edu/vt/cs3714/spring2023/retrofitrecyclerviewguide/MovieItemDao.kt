package edu.vt.cs3714.spring2023.retrofitrecyclerviewguide

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface MovieItemDao {

    @Query("SELECT * FROM movie_table ORDER BY release_date DESC")
    fun getAllMovies(): LiveData<List<MovieItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovie(movie: MovieItem)

    @Query("DELETE FROM movie_table")
    fun deleteAll()

}