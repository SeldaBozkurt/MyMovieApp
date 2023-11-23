package com.example.mymovieapp.presentation.movie_detail.views

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.mymovieapp.presentation.movie_detail.DetailsView
import com.example.mymovieapp.presentation.movie_detail.MovieDetailViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun MovieDetailScreen(
    movieDetailViewModel: MovieDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state = movieDetailViewModel.state.value


    LaunchedEffect(key1 = movieDetailViewModel.event) {
        movieDetailViewModel.event.collect {
            withContext(Dispatchers.Main.immediate) {
                when (it) {
                    is DetailsView.Event.ShowMessage -> {
                        Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                    }

                    else -> {

                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray), contentAlignment = Center
    ) {
        state.movie?.let {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = CenterHorizontally
            ) {
                Button(
                    modifier = Modifier.align(End),
                    onClick = {
                        movieDetailViewModel.onUiAction(
                            DetailsView.Action.SaveButtonClick(
                                state.movie.imdbId
                            )
                        )
                    },
                ) {
                    Text(
                        text = if (state.isSaved == true) {
                            "Saved"
                        } else "Save"
                    )
                }
                Image(
                    painter = rememberAsyncImagePainter(model = it.poster),
                    contentDescription = it.title,
                    modifier = Modifier
                        .padding(16.dp)
                        .size(300.dp, 300.dp)
                        .clip(RectangleShape)
                        .align(CenterHorizontally)
                        .clickable {
                            movieDetailViewModel.onUiAction(DetailsView.Action.ImageClick(it.poster))
                        }

                )

                Text(
                    text = it.title,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(14.dp),
                    color = Color.White
                )

                Text(
                    text = it.year,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(14.dp),
                    color = Color.White
                )

                Text(
                    text = it.actors,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(14.dp),
                    color = Color.White
                )

                Text(
                    text = it.country,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(14.dp),
                    color = Color.White
                )

                Text(
                    text = "Director: ${it.director}",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(14.dp),
                    color = Color.White
                )

                Text(
                    text = "IMDB Rating: ${it.imdbRating}",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(14.dp),
                    color = Color.White
                )

            }
        }

        if (state.error.isNotBlank()) {
            Text(
                text = state.error,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
                    .align(Alignment.Center)
            )
        }

        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }


}

@Preview
@Composable
fun MovieDetailScreenPreview() {
    MovieDetailScreen(
    )
}