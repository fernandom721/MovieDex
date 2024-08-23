package com.example.moviedex.ViewModel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviedex.models.Movie
import com.example.moviedex.models.MovieResponse
import com.example.moviedex.util.RetrofitInstance
import com.google.android.gms.common.api.internal.ApiKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.http.Query

class MovieViewModel(private val repository: Repository, private val favoritesRepository: FavoritesRepository) : ViewModel() {

    private val _movieState = MutableStateFlow<Result<List<Movie>>?>(null)
    val movieState: StateFlow<Result<List<Movie>>?> = _movieState

    private val _favoriteMovies = MutableStateFlow<List<Movie>>(favoritesRepository.getFavorites())
    val favoriteMovies: StateFlow<List<Movie>> = _favoriteMovies

    //Estado de favoritos
    private val _favoritesState = mutableStateOf<List<Movie>>(emptyList())
    val favoritesState: State<List<Movie>> = _favoritesState

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            _favoritesState.value = favoritesRepository.getFavorites()
        }
    }

    fun getFavoriteById(imdbID: String): Movie? {
        return _favoritesState.value.find { it.imdbID == imdbID }
    }

    fun searchAllMovies(apiKey: String, query: String) {
        viewModelScope.launch {
            val result = repository.searchAllMovies(apiKey, query)
            _movieState.value = result
        }
    }

    fun addToFavorites(movie: Movie) {
        viewModelScope.launch {
            favoritesRepository.addFavorite(movie)
            _favoriteMovies.value = favoritesRepository.getFavorites()
        }
    }

    fun removeFromFavorites(movie: Movie) {
        viewModelScope.launch {
            favoritesRepository.removeFavorite(movie)
            _favoriteMovies.value = favoritesRepository.getFavorites()
        }
    }
}