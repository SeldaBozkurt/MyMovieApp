package com.example.mymovieapp.data.repository

import com.example.mymovieapp.data.remote.MovieApi
import com.example.mymovieapp.data.remote.dto.MovieDetailDto
import com.example.mymovieapp.data.remote.dto.MoviesDto
import com.example.mymovieapp.domain.repository.MovieRepository
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(private val api: MovieApi) : MovieRepository {
    override suspend fun getMovie(search: String): MoviesDto {
        return api.getMovies(search)
    }

    override suspend fun getMovieDetail(imdbID: String): MovieDetailDto {
        return api.getMovieDetail(imdbID)
    }
}