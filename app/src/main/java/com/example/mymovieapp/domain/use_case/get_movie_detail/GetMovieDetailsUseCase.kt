package com.example.mymovieapp.domain.use_case.get_movie_detail

import com.example.mymovieapp.data.remote.dto.toMovieDetail
import com.example.mymovieapp.domain.model.MovieDetailUiModel
import com.example.mymovieapp.domain.repository.MovieRepository
import com.example.mymovieapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOError
import javax.inject.Inject

class GetMovieDetailsUseCase @Inject constructor(private val repository: MovieRepository) {
    fun executeGetMovieDetails(imdbId: String): Flow<Resource<MovieDetailUiModel>> = flow {
        try {
            emit(Resource.Loading())
            val movieDetails = repository.getMovieDetail(imdbID = imdbId)
            if (movieDetails.response == "True") {
                emit(Resource.Success(movieDetails.toMovieDetail()))
            } else {
                emit(Resource.Error("No movie found!"))
            }
        } catch (e: IOError) {
            emit(Resource.Error("No internet connection"))
        }
    }
}