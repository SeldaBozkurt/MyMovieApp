package com.example.mymovieapp.data.remote

import com.example.mymovieapp.data.remote.dto.MovieDetailDto
import com.example.mymovieapp.data.remote.dto.MoviesDto
import com.example.mymovieapp.util.Constants.API_KEY
import retrofit2.http.GET
import retrofit2.http.Query


// http://www.omdbapi.com/?i=tt0372784&apikey=646ddece
// http://www.omdbapi.com/?s=batman&apikey=646ddece



interface MovieApi {
    @GET(".")
    suspend fun getMovies(
        @Query("s") search : String,
        @Query("apikey") apiKey : String = API_KEY
    ): MoviesDto

    @GET(".")
    suspend fun getMovieDetail(
        @Query("i") imdbId : String,
        @Query("apikey") apiKey : String = API_KEY
    ): MovieDetailDto
}