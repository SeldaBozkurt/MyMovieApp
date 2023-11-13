package com.example.mymovieapp.domain.repository

import com.example.mymovieapp.data.remote.dto.MovieDetailDto
import com.example.mymovieapp.data.remote.dto.MoviesDto

interface MovieRepository {
    suspend fun getMovie(search: String): MoviesDto
    suspend fun getMovieDetail(imdbID: String): MovieDetailDto
}