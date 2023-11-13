package com.example.mymovieapp.data.remote.dto


import com.example.mymovieapp.domain.model.MovieUiModel
import com.google.gson.annotations.SerializedName

data class MoviesDto(
    @SerializedName("Response")
    val response: String,
    @SerializedName("Search")
    val search: List<Search>,
    @SerializedName("totalResults")
    val totalResults: String
)


fun MoviesDto.toMovieList(): List<MovieUiModel> {
    return search.map {
        MovieUiModel(
            it.imdbID,
            it.poster,
            it.title,
            it.year
        )
    }
}