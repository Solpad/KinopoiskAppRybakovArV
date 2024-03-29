package com.example.kinopoiskapp.screens.popular

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.kinopoiskapp.MainActivity.Companion.SCREEN_MORE_INFO
import com.example.kinopoiskapp.R
import com.example.kinopoiskapp.screens.baseui.FilmsTopAppBar
import com.example.kinopoiskapp.screens.baseui.MoviesBottomAppBar

@Composable
internal fun PopularScreen(
    viewModel: PopularScreenViewModel,
    navController: NavHostController,
) {
    val popularsFilms by viewModel.popularsMoviesStateFlow.collectAsState()
    val needShowErrorScreen by viewModel.needShowErrorScreenStateFlow.collectAsState()

    val onPopularMovieClicked = remember {
        { movieId: String ->
            navController.navigate(
                "$SCREEN_MORE_INFO/$movieId"
            )
        }
    }

    PopularScreen(
        popularFilms = popularsFilms,
        topBar = { FilmsTopAppBar(stringResource(id = R.string.popular_title)) },
        bottomBar = { MoviesBottomAppBar(navController = navController) },
        needShowErrorScreen = needShowErrorScreen,
        onPopularMovieClick = onPopularMovieClicked,
        onRepeatErrorButton = viewModel::onRepeatErrorButton,
        onAddToFavoriteClick = viewModel::onAddToFavoriteClick,
    )
}

@Composable
private fun PopularScreen(
    popularFilms: List<PopularMovieUiModel>,
    topBar: @Composable () -> Unit,
    bottomBar: @Composable () -> Unit,
    needShowErrorScreen: Boolean,
    onRepeatErrorButton: () -> Unit,
    onPopularMovieClick: (String) -> Unit,
    onAddToFavoriteClick: (String) -> Unit,
) {
    Scaffold(
        topBar = topBar,
        bottomBar = bottomBar,
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
    ) { scaffoldPaddings ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = scaffoldPaddings.calculateTopPadding(),
                    bottom = scaffoldPaddings.calculateBottomPadding(),
                    start = scaffoldPaddings.calculateStartPadding(LocalLayoutDirection.current),
                    end = scaffoldPaddings.calculateEndPadding(LocalLayoutDirection.current),
                )
        ) {
            if (needShowErrorScreen) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.kinopoisk_no_internet_icon),
                            contentDescription = "",
                            tint = Color.Unspecified,
                            modifier = Modifier.padding(12.dp)
                        )
                        Text(
                            text = stringResource(id = R.string.error_title),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = Color(0xFF0094FF),
                            modifier = Modifier.padding(12.dp),
                        )
                        Button(
                            modifier = Modifier.padding(12.dp),
                            onClick = onRepeatErrorButton,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0094FF),
                                contentColor = Color.White
                            ),
                        ) {
                            Text(text = stringResource(id = R.string.repeat_title))
                        }
                    }
                }
            } else {
                LazyColumn() {
                    itemsIndexed(
                        popularFilms,
                        key = { _, film -> "popular_film_item_${film.id}" },
                    ) { _, film ->
                        PopularFilmCard(
                            film = film,
                            onPopularMovieClick = onPopularMovieClick,
                            onAddToFavoriteClick = onAddToFavoriteClick,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PopularFilmCard(
    film: PopularMovieUiModel,
    onPopularMovieClick: (String) -> Unit,
    onAddToFavoriteClick: (String) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .shadow(8.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.background)
            .clickable(
                role = Role.Button,
                onClick = { onPopularMovieClick(film.id) }
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onPopularMovieClick(film.id) },
                    onLongPress = { onAddToFavoriteClick(film.id) },
                )
            }

    ) {
        Row(modifier = Modifier.padding(10.dp)) {
            AsyncImage(
                model = film.posterSmall, contentDescription = "content",
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .size(100.dp)
            )
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = film.name,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    modifier = Modifier
                        .padding(8.dp)
                        .alpha(0.5f),
                    text = film.genre,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
            if (film.inFavorite) {
                Icon(
                    Icons.Rounded.Star, contentDescription = "",
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .align(Alignment.CenterVertically)
                )
            }
        }

    }
}