package com.example.moviedex.ViewModel

import android.icu.text.CaseMap.Title
import android.telecom.Call.Details
import com.example.moviedex.models.Movie
import com.example.moviedex.models.MovieResponse
import com.example.moviedex.util.ApiService
import com.example.moviedex.util.RetrofitInstance
//import com.example.moviedex.util.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import retrofit2.Response
import retrofit2.http.Query

class Repository (private val apiService: ApiService){

    suspend fun searchAllMovies(apiKey: String, query: String): Result<List<Movie>> {
        return withContext(Dispatchers.IO) {
            try {
                val allMovies = mutableListOf<Movie>()
                var currentPage = 1
                var totalResults: Int
                do {
                    val response = apiService.searchMovies(apiKey, query, currentPage)
                    if (response.Response == "True") {
                        allMovies.addAll(response.Search)
                        totalResults = response.totalResults.toInt()
                        currentPage++
                    } else {
                        totalResults = allMovies.size
                    }
                } while (allMovies.size < totalResults)

                Result.success(allMovies)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }



}