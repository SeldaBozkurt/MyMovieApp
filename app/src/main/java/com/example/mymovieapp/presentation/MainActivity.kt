package com.example.mymovieapp.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mymovieapp.domain.use_case.AirPlaneModeReceiver
import com.example.mymovieapp.presentation.movie_detail.views.MovieDetailScreen
import com.example.mymovieapp.presentation.movies.views.MovieScreen
import com.example.mymovieapp.presentation.ui.theme.MyMovieAppTheme
import com.example.mymovieapp.util.Constants.IMDB_ID
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var receiver: AirPlaneModeReceiver? = null

    private val viewModel by viewModels<MainActivityViewModel>()

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            MyMovieAppTheme {

                val context = LocalContext.current

                val state = viewModel.state.collectAsState().value

                LaunchedEffect(viewModel.event) {
                    viewModel.event.collect {

                        when (it) {
                            is MainActivityView.Event.ShowMessage -> {
                                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                            }
                        }

                    }

                }

                val navController = rememberNavController()
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(text = state.message)
                    NavHost(
                        modifier = Modifier.weight(1f),
                        navController = navController,
                        startDestination = Screen.MovieScreen.route
                    ) {
                        composable(route = Screen.MovieScreen.route) {
                            MovieScreen(navController = navController)
                        }
                        composable(route = Screen.MovieDetailScreen.route + "/{${IMDB_ID}}") {
                            MovieDetailScreen()
                        }
                    }
                }
            }
        }
    }

    private fun initializeReceiver() {
        receiver = AirPlaneModeReceiver {
            viewModel.onUiAction(MainActivityView.Action.AirPlaneModeUpdate(it))
        }
    }

    override fun onResume() {
        super.onResume()
        if (receiver == null) {
            initializeReceiver()
        }
        registerReceiver(
            receiver,
            IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}
