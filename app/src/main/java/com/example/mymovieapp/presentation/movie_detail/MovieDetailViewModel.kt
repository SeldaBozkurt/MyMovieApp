package com.example.mymovieapp.presentation.movie_detail

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymovieapp.domain.model.MovieDetailUiModel
import com.example.mymovieapp.domain.use_case.get_movie_detail.GetMovieDetailsUseCase
import com.example.mymovieapp.util.Constants.IMDB_ID
import com.example.mymovieapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

object DetailsView {

    data class MovieDetailState(
        val isLoading: Boolean = false,
        val movie: MovieDetailUiModel? = null,
        val error: String = "",
        val isSaved: Boolean? = false
    )

    sealed class Action {
        data class SaveButtonClick(val movieId: String) : Action()
        data class ImageClick(val imageUrl: String) : Action()
    }

    sealed class Event {
        data class ShowMessage(val message: String) : Event()
    }
}


@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val _state =
        mutableStateOf(DetailsView.MovieDetailState())
    val state: State<DetailsView.MovieDetailState> = _state

    private val _event = Channel<DetailsView.Event?>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    init {
        savedStateHandle.get<String>(IMDB_ID)?.let {
            getMovieDetail(it)
        }
    }

    fun onUiAction(action: DetailsView.Action) {
        when (action) {
            is DetailsView.Action.SaveButtonClick -> {
                viewModelScope.launch {

                    dataStore.edit { preference ->
                        val movies = preference[SAVED_MOVIES_PREF] ?: emptySet()
                        if (movies.contains(action.movieId)) {
                            preference[SAVED_MOVIES_PREF] =
                                movies.toMutableSet().apply { remove(action.movieId) }

                            _state.value = DetailsView.MovieDetailState(
                                movie = _state.value.movie,
                                isSaved = false
                            )
                            _event.send(DetailsView.Event.ShowMessage("${_state.value.movie?.title} has been removed!"))
                        } else {
                            preference[SAVED_MOVIES_PREF] = movies.toMutableSet().apply {
                                add(action.movieId)
                            }

                            _state.value = DetailsView.MovieDetailState(
                                movie = _state.value.movie,
                                isSaved = true
                            )
                            _event.send(DetailsView.Event.ShowMessage("${_state.value.movie?.title} has been saved!"))
                        }

                    }


                }
            }

            is DetailsView.Action.ImageClick -> {
                println(action.imageUrl)
            }
        }
    }


    private fun getMovieDetail(imdbId: String) {
        getMovieDetailsUseCase.executeGetMovieDetails(imdbId = imdbId).onEach { uiModelResource ->
            when (uiModelResource) {
                is Resource.Success -> {
                    val uiModel = uiModelResource.data

                    val savedMoviesFlow = dataStore.data
                        .map { preferences ->
                            // No type safety.
                            preferences[SAVED_MOVIES_PREF] ?: emptySet()
                        }

                    savedMoviesFlow.collect {
                        _state.value = DetailsView.MovieDetailState(
                            movie = uiModel,
                            isSaved = it.contains(uiModel?.imdbId)
                        )
                    }
                }

                is Resource.Error -> {
                    _state.value =
                        DetailsView.MovieDetailState(error = uiModelResource.message ?: "Error!")

                }

                is Resource.Loading -> {
                    _state.value = DetailsView.MovieDetailState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    companion object {
        private val SAVED_MOVIES_PREF = stringSetPreferencesKey("saved_movies")
    }
}