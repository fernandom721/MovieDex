package com.example.moviedex.models

data class MovieResponse(
    val Search: List<Movie>,
    val totalResults: String,
    val Response: String
)