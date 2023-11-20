package com.example.mymovieapp.presentation.movies

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymovieapp.domain.model.MovieUiModel
import com.example.mymovieapp.domain.use_case.get_movies.GetMovieUseCase
import com.example.mymovieapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


data class MoviesState(
    val isLoading: Boolean = false,
    val movies: List<MovieUiModel> = emptyList(),
    val error: String = "",
    val search: String = ""
)

sealed class MoviesEvent {
    data class Search(val searchString: String) : MoviesEvent()
}

@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val getMoviesUseCase: GetMovieUseCase
) : ViewModel() {

    private val _state = mutableStateOf<MoviesState>(MoviesState())
    val state: State<MoviesState> = _state

    private var job: Job? = null

    init {
        getMovies(_state.value.search)
    }

    private fun getMovies(search: String) {
        job?.cancel()
        job = getMoviesUseCase.executeGetMovies(search).onEach {
            when (it) {
                is Resource.Success -> {
                    _state.value = MoviesState(movies = it.data ?: emptyList())
                }

                is Resource.Error -> {
                    _state.value = MoviesState(error = it.message ?: "Error!")
                }

                is Resource.Loading -> {
                    _state.value = MoviesState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun onEvent(event: MoviesEvent) {
        when (event) {
            is MoviesEvent.Search -> {
                getMovies(event.searchString)
            }

            else -> {}
        }
    }
}