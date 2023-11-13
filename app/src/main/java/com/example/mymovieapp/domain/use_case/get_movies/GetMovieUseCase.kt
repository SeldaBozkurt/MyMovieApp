package com.example.mymovieapp.domain.use_case.get_movies

import com.example.mymovieapp.data.remote.dto.toMovieList
import com.example.mymovieapp.domain.model.MovieUiModel
import com.example.mymovieapp.domain.repository.MovieRepository
import com.example.mymovieapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOError
import javax.inject.Inject

class GetMovieUseCase @Inject constructor(private val repository: MovieRepository) {
    fun executeGetMovies(search: String): Flow<Resource<List<MovieUiModel>>> = flow {
        try {
            emit(Resource.Loading())
            val movieList = repository.getMovie(search = search)
            if (movieList.response == "True") {
                emit(Resource.Success(movieList.toMovieList()))
            } else {
                emit(Resource.Error("No movie found!"))
            }
        } catch (e: IOError) {
            emit(Resource.Error("No internet connection"))
        }
    }
}