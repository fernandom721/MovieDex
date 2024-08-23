package com.example.moviedex.ViewModel

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.moviedex.models.Movie
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class FavoritesRepository(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("favorites_prefs", Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }

    fun getFavorites(): List<Movie> {
        val favoritesJson = sharedPreferences.getString("favorites", "[]")
        return json.decodeFromString(favoritesJson ?: "[]")
    }

    fun addFavorite(movie: Movie) {
        val favorites = getFavorites().toMutableList()
        if (movie !in favorites) {
            favorites.add(movie)
            saveFavorites(favorites)
        }
    }

    fun removeFavorite(movie: Movie) {
        val favorites = getFavorites().toMutableList()
        if (movie in favorites) {
            favorites.remove(movie)
            saveFavorites(favorites)
        }
    }

    private fun saveFavorites(favorites: List<Movie>) {
        val favoritesJson = json.encodeToString(favorites)
        sharedPreferences.edit {
            putString("favorites", favoritesJson)
        }
    }
}