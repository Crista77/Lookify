package com.example.lookify.ui.screens.home

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.TvOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lookify.data.database.Film
import com.example.lookify.data.database.SerieTV
import com.example.lookify.ui.LookifyRoute
import com.example.lookify.ui.LookifyState
import com.example.lookify.ui.composables.*

@Composable
fun HomeScreen(state: LookifyState, navController: NavController, currentUserId: Int) {
    Scaffold(
        topBar = { TitleAppBar(navController, state) },
        bottomBar = { BottomBar(state, navController) }
    ) { contentPadding ->

        Column(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.films.isNotEmpty()) {
                Text(
                    text = "Film Appena Usciti",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier
                        .padding(start = 16.dp, top = 16.dp) // aumenta top per spostarlo più in basso
                        .fillMaxWidth(), // per occupare tutta la larghezza
                    textAlign = TextAlign.Start
                )
                FilmCarousel(
                    currentUserId,
                    state,
                    films = state.films
                        .filter { it.visibile }
                        .sortedByDescending { it.id_film }
                        .take(3),
                    navController = navController
                )
            } else {
                NoItemsPlaceholder()
            }

            if (state.films.isNotEmpty()) {
                Text(
                    text = "Film Più Visti",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier
                        .padding(start = 16.dp, top = 16.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
                FilmCarousel(
                    currentUserId,
                    state,
                    films = state.films
                        .filter { it.visibile }
                        .sortedByDescending { it.visualizzazioni }
                        .take(3),
                    navController = navController
                )
            } else {
                NoItemsPlaceholder()
            }

            if (state.series.isNotEmpty()) {
                Text(
                    text = "Serie TV Appena Uscite",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier
                        .padding(start = 16.dp, top = 16.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
                SerieTvCarousel(
                    currentUserId,
                    series = state.series
                        .filter { it.visibile }
                        .sortedByDescending { it.id_serie }
                        .take(3),
                    navController = navController
                )
            } else {
                NoItemsPlaceholder()
            }

            if (state.series.isNotEmpty()) {
                Text(
                    text = "Serie TV Più Viste",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier
                        .padding(start = 16.dp, top = 16.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
                SerieTvCarousel(
                    currentUserId,
                    series = state.series
                        .filter { it.visibile }
                        .sortedByDescending { it.visualizzazioni }
                        .take(3),
                    navController = navController
                )
            } else {
                NoItemsPlaceholder()
            }
        }
    }
}


@Composable
fun FilmCarousel(
    currentUserId: Int,
    state: LookifyState,
    films: List<Film>,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var currentIndex by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = { if (currentIndex > 0) currentIndex-- },
                enabled = currentIndex > 0
            ) {
                Icon(
                    imageVector = Icons.Outlined.ChevronLeft,
                    contentDescription = "Indietro"
                )
            }

            FilmItem(
                item = films[currentIndex],
                onClick = {
                    navController.navigate("${LookifyRoute.Film}?filmId=${films[currentIndex].id_film}&currentUserId=${currentUserId}")
                }
            )

            IconButton(
                onClick = { if (currentIndex < films.lastIndex) currentIndex++ },
                enabled = currentIndex < films.lastIndex
            ) {
                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = "Avanti"
                )
            }
        }
    }
}

@Composable
fun FilmItem(item: Film, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .width(220.dp)
            .height(300.dp)
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val imageUri = item.imageUri?.let { Uri.parse(it) }
            if (imageUri != null) {
                ImageWithPlaceholder(imageUri, Size.Lg)
            } else {
                Icon(
                    Icons.Outlined.TvOff,
                    contentDescription = "Nessuna Immagine",
                    modifier = Modifier.size(96.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = item.titolo,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun SerieTvCarousel(
    currentUserId: Int,
    series: List<SerieTV>,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var currentIndex by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = { if (currentIndex > 0) currentIndex-- },
                enabled = currentIndex > 0
            ) {
                Icon(
                    imageVector = Icons.Outlined.ChevronLeft,
                    contentDescription = "Indietro"
                )
            }

            SerieTVItem(
                item = series[currentIndex],
                onClick = {
                    navController.navigate("${LookifyRoute.Serie}?serieId=${series[currentIndex].id_serie}&currentUserId=${currentUserId}")
                }
            )

            IconButton(
                onClick = { if (currentIndex < series.lastIndex) currentIndex++ },
                enabled = currentIndex < series.lastIndex
            ) {
                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = "Avanti"
                )
            }
        }
    }
}

@Composable
fun SerieTVItem(item: SerieTV, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .width(220.dp)
            .height(300.dp)
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val imageUri = item.imageUri?.let { Uri.parse(it) }
            if (imageUri != null) {
                ImageWithPlaceholder(imageUri, Size.Lg)
            } else {
                Icon(
                    Icons.Outlined.TvOff,
                    contentDescription = "Nessuna Immagine",
                    modifier = Modifier.size(96.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = item.titolo,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun NoItemsPlaceholder(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Icon(
            Icons.Outlined.TvOff,
            contentDescription = "Nessun Film o Serie",
            modifier = Modifier.padding(bottom = 16.dp).size(48.dp),
            tint = MaterialTheme.colorScheme.secondary
        )
        Text(
            "Non ci sono Film o Serie",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            "Vai sul tuo account e richiedi nuovi Film e Serie.",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
