package com.example.mymovieapp.presentation.movies

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymovieapp.domain.model.MovieUiModel
import com.example.mymovieapp.domain.use_case.get_movies.GetMovieUseCase
import com.example.mymovieapp.presentation.movie_detail.DetailsView
import com.example.mymovieapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

object MovieView{
    data class MoviesState(
        val isLoading: Boolean = false,
        val movies: List<MovieUiModel> = emptyList(),
        val error: String = "",
        val search: String = "Cat"
    )

    sealed class MoviesUiAction {
        data class MovieItemClicked(val imdbId: String) : MoviesUiAction()
        data class SearchClicked(val searchString: String) : MoviesUiAction()

    }

    sealed class Event{
        sealed class Navigation : Event() {
            data class GoMovieDetail(val imdbId: String) : Navigation()
        }
    }
}


@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val getMoviesUseCase: GetMovieUseCase
) : ViewModel() {

    private val _state = mutableStateOf(MovieView.MoviesState())
    val state: State<MovieView.MoviesState> = _state
    private val _event = Channel<MovieView.Event?>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    private var job: Job? = null

    init {
        getMovies(_state.value.search)
    }

    private fun getMovies(search: String) {
        job?.cancel()
        job = getMoviesUseCase.executeGetMovies(search).onEach {
            when (it) {
                is Resource.Success -> {
                    _state.value = MovieView.MoviesState(movies = it.data ?: emptyList())
                }

                is Resource.Error -> {
                    _state.value = MovieView.MoviesState(error = it.message ?: "Error!")
                }

                is Resource.Loading -> {
                    _state.value = MovieView.MoviesState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun onUiAction(action: MovieView.MoviesUiAction) {
        viewModelScope.launch {
            when (action) {
                is MovieView.MoviesUiAction.SearchClicked -> {
                    getMovies(action.searchString)
                }
                is MovieView.MoviesUiAction.MovieItemClicked -> {
                    // Launching a coroutine to send the navigation event
                        _event.send(MovieView.Event.Navigation.GoMovieDetail(action.imdbId))
                }
                else -> {}
            }
        }
    }
}