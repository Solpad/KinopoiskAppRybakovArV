package com.example.kinopoiskapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.kinopoiskapp.repository.MoviesRepositoryImpl
import com.example.kinopoiskapp.screens.favorites.FavoritesScreenViewModel
import com.example.kinopoiskapp.screens.mapper.MovieUiMapperImpl
import com.example.kinopoiskapp.screens.more.MoreScreenViewModel
import com.example.kinopoiskapp.screens.popular.PopularScreenViewModel
import com.example.kinopoiskapp.ui.theme.KinopoiskAppTheme
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    @Inject
    lateinit var moviesRepository: MoviesRepositoryImpl
    private var movieUiMapperImpl = MovieUiMapperImpl()

    private val viewModelFactory = viewModelFactory {
        initializer {
            PopularScreenViewModel(
                moviesRepository,
                movieUiMapperImpl
            )
        }
        initializer {
            FavoritesScreenViewModel(
                moviesRepository,
                movieUiMapperImpl
            )
        }
        initializer {
            val savedStateHandle = createSavedStateHandle()
            MoreScreenViewModel(
                savedStateHandle,
                moviesRepository,
                movieUiMapperImpl
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as App).mainComponent.inject(this)
        setContent {
            KinopoiskAppTheme {
                FilmsNavigationRoot(viewModelFactory = viewModelFactory)
            }
        }

    }

    internal companion object {
        internal const val SCREEN_POPULAR = "populars"
        internal const val SCREEN_FAVORITES = "favorites"
        internal const val SCREEN_MORE_INFO = "more_info"
    }
}
