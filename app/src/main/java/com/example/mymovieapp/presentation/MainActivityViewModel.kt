package com.example.mymovieapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

object MainActivityView {


    data class MainActivityState(
        val message: String = "dummy"
    )


    sealed class Action {

        data class AirPlaneModeUpdate(val isOn: Boolean) : Action()
    }

    sealed class Event {

        data class ShowMessage(val message: String) : Event()
    }
}

@HiltViewModel
class MainActivityViewModel @Inject constructor(
) : ViewModel() {

    private val _state =
        MutableStateFlow(MainActivityView.MainActivityState())
    val state = _state.asStateFlow()

    private val _event = Channel<MainActivityView.Event>(Channel.BUFFERED)
    val event = _event.receiveAsFlow()

    fun onUiAction(action: MainActivityView.Action) {
        viewModelScope.launch {
            when (action) {

                is MainActivityView.Action.AirPlaneModeUpdate -> {
                    val message = "airplane mode is ${
                        when(action.isOn) {
                            true -> {
                                "on"
                            }

                            else -> {
                                "off"
                            }
                        }
                    }"
                    _state.update { it .copy(message = message) }
                    _event.send(
                        MainActivityView.Event.ShowMessage(
                            message = message
                        )
                    )
                }
            }
        }
    }
}