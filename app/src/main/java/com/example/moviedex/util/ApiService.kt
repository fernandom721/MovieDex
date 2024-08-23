package com.example.moviedex.util

import com.example.moviedex.models.Movie
import com.example.moviedex.models.MovieResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    //APIKEY: 91f9c951
    @GET("/")
    suspend fun searchMovies(
        @Query("apikey") apiKey: String,
        @Query("s") query: String,
        @Query("page") page: Int
    ): MovieResponse

    companion object {
        private const val BASE_URL = "https://www.omdbapi.com/"

        fun create(): ApiService{
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(ApiService::class.java)

        }
    }

}

