package com.example.moviedex

import android.annotation.SuppressLint
import android.icu.text.CaseMap.Title
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.FlowRowScopeInstance.weight
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.materialIcon
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.moviedex.ViewModel.FavoritesRepository
import com.example.moviedex.ViewModel.MovieViewModel
import com.example.moviedex.ViewModel.Repository
import com.example.moviedex.models.Movie
import com.example.moviedex.ui.theme.MovieDexTheme
import com.example.moviedex.util.ApiService
import java.util.logging.Logger.global

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: MovieViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // Crear instancias manualmente
        val apiService = ApiService.create()
        val repository = Repository(apiService)
        val favoritesRepository = FavoritesRepository(this)
        val factory = MovieViewModelFactory(repository, favoritesRepository)
        viewModel = ViewModelProvider(this, factory).get(MovieViewModel::class.java)

        setContent {
            MovieApp(viewModel = viewModel)
        }




    }
}


@Composable
fun MovieScreen(viewModel: MovieViewModel, navController: NavHostController) {

    var query by remember { mutableStateOf("") }
    val movieState by viewModel.movieState.collectAsState()
    var type: String

    Box(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            BasicTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                decorationBox = { innerTextField ->
                    Box(modifier = Modifier.padding(8.dp)) {
                        if (query.isEmpty()) Text("Enter movie title...")
                        innerTextField()
                    }
                }
            )

            Button(
                onClick = { viewModel.searchAllMovies("91f9c951", query) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("Search")
            }

            Spacer(modifier = Modifier.height(16.dp))

            movieState?.let { result ->
                when {
                    result.isSuccess -> {
                        val movies = result.getOrNull() ?: emptyList()
                        MovieList(movies = movies, navController = navController)
                    }
                    result.isFailure -> {
                        Text("Failed to fetch movies: ${result.exceptionOrNull()?.message}")
                    }
                }
            }
        }

        // Button to navigate to the Favorites screen
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    type = "movies"
                    navController.navigate("favorites")
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text("View Favorite")
            }

            /*Button(
                onClick = {
                    type = "series"
                    navController.navigate("favorites")
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text("Favorite Series")
            }*/
        }
    }
}

@Composable
fun MovieList(movies: List<Movie>, navController: NavHostController) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(movies) { movie ->
            movie.Title?.let {
                Text(
                    text = it,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            navController.navigate("movieDetail/${movie.imdbID}")
                        }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun MovieDetailScreen(movie: Movie?, viewModel: MovieViewModel) {
    //val movie = Title?.let { viewModel.getFavoriteById(it) }

    movie?.let { selectedMovie ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {
            Text(text = "Title: ${selectedMovie.Title}")
            Text(text = "Year: ${selectedMovie.Year}")
            Text(text = "Type: ${selectedMovie.Type}")
            Text(text = "Genre: ${selectedMovie.Genre}")
            Text(text = "Plot: ${selectedMovie.Plot}")

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.addToFavorites(selectedMovie)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("Add to Favorites")
            }
        }
    } ?: run {
        Text("Movie not found")
    }
}

@Composable
fun MovieFavoriteDetailScreen(movieId: String?, viewModel: MovieViewModel) {
    val movie = movieId?.let { viewModel.getFavoriteById(it) }

    movie?.let { selectedMovie ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {
            Text(text = "Title: ${selectedMovie.Title}")
            Text(text = "Year: ${selectedMovie.Year}")
            Text(text = "Type: ${selectedMovie.Type}")
            Text(text = "Genre: ${selectedMovie.Genre}")
            Text(text = "Plot: ${selectedMovie.Plot}")

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.addToFavorites(selectedMovie)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("Add to Favorites")
            }
        }
    } ?: run {
        Text("Movie not found")
    }
}

@Composable
fun FavoritesMoviesScreen(viewModel: MovieViewModel, navController: NavHostController) {
    val favoriteMovies by viewModel.favoriteMovies.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(favoriteMovies) { movie ->

                movie.Title?.let {
                    Text(
                        text = it,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                navController.navigate("movieDetail/${movie.imdbID}")
                            }
                    )
                }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun MovieApp(viewModel: MovieViewModel) {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "movieList") {
        composable("movieList") {
            MovieScreen(viewModel = viewModel, navController = navController)
        }
        composable("movieDetail/{movieId}") { backStackEntry ->
            val movieId = backStackEntry.arguments?.getString("movieId")
            val movie = viewModel.movieState.value?.getOrNull()?.find { it.imdbID == movieId }
            MovieDetailScreen(movie = movie, viewModel = viewModel)
            MovieFavoriteDetailScreen(movieId = movieId, viewModel = viewModel)
        }
        composable("favorites") {
            FavoritesMoviesScreen(viewModel = viewModel, navController = navController)
        }
    }
}
