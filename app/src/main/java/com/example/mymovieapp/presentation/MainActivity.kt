package com.example.mymovieapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mymovieapp.presentation.movie_detail.views.MovieDetailScreen
import com.example.mymovieapp.presentation.ui.theme.MyMovieAppTheme
import com.example.mymovieapp.util.Constants.IMDB_ID
import dagger.hilt.android.AndroidEntryPoint
import com.example.mymovieapp.presentation.movies.views.MovieScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyMovieAppTheme {
                val navController = rememberNavController()
                NavHost(navController = navController,
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
