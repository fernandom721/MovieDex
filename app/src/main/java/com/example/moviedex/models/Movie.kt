package com.example.moviedex.models

import kotlinx.serialization.Serializable

@Serializable
data class Movie(
    val Title: String?,
    val Year: String?,
    val Rated: String?,
    val Released: String?,
    val Runtime: String?,
    val Genre: String?,
    val Director: String?,
    val Writer: String?,
    val Actors: String?,
    val Plot: String?,
    val Language: String?,
    val Country: String?,
    val Awards: String?,
    val Poster: String?,
    val Metascore: String?,
    val imdbRating: String?,
    val imdbVotes: String?,
    val imdbID: String?,
    val Type: String?,
    val Dvd: String?,
    val BoxOffice: String?,
    val Production: String?,
    val Website: String?,
    val Response: String?
)


