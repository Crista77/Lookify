package com.example.lookify.ui.screens.search

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.TvOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.lookify.data.database.Film
import com.example.lookify.data.database.SerieTV
import com.example.lookify.data.database.Users
import com.example.lookify.ui.LookifyRoute
import com.example.lookify.ui.LookifyState
import com.example.lookify.ui.composables.*



enum class SearchFilter {
    FILM, SERIE_TV, UTENTI
}

@Composable
fun SearchScreen(state: LookifyState, navController: NavController, selectedNav: String) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(SearchFilter.FILM)}
    var showCategoryFilter by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    selectedFilter = when (selectedNav) {
        "Film" -> SearchFilter.FILM
        "TV" -> SearchFilter.SERIE_TV
        "Users" -> SearchFilter.UTENTI
        else -> SearchFilter.FILM
    }
    // Utenti dal database tramite state

    Scaffold(
        topBar = { TitleAppBar(navController) },
        bottomBar = { BottomBar(state, navController) }
    ) { contentPadding ->

        Column(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
                .background(Color.Black)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Filter Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterButton(
                    text = "Film",
                    isSelected = selectedFilter == SearchFilter.FILM,
                    onClick = { selectedFilter = SearchFilter.FILM },
                    modifier = Modifier.weight(1f)
                )
                FilterButton(
                    text = "Serie Tv",
                    isSelected = selectedFilter == SearchFilter.SERIE_TV,
                    onClick = { selectedFilter = SearchFilter.SERIE_TV },
                    modifier = Modifier.weight(1f)
                )
                FilterButton(
                    text = "Utenti",
                    isSelected = selectedFilter == SearchFilter.UTENTI,
                    onClick = { selectedFilter = SearchFilter.UTENTI },
                    modifier = Modifier.weight(1f)
                )
            }

            // Search Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    singleLine = true,
                    placeholder = {
                        Text(
                            when(selectedFilter) {
                                SearchFilter.FILM -> "Cerca Film..."
                                SearchFilter.SERIE_TV -> "Cerca Serie TV..."
                                SearchFilter.UTENTI -> "Cerca Utenti..."
                            },
                            color = Color.Gray
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Search,
                            contentDescription = "Cerca",
                            tint = Color.Gray
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Red,
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search,
                        keyboardType = KeyboardType.Text),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                )

                IconButton(
                    onClick = {
                        if (selectedFilter == SearchFilter.UTENTI) {
                            // Per utenti, mostra messaggio che filtri non sono disponibili
                        } else {
                            showCategoryFilter = true
                        }
                    }
                ) {
                    Icon(
                        Icons.Outlined.FilterList,
                        contentDescription = "Filtri",
                        tint = Color.White
                    )
                }
            }

            if (showCategoryFilter) {
                CategoryFilterDialog(
                    categories = when(selectedFilter) {
                        SearchFilter.FILM -> getUniqueFilmCategories(state.films)
                        SearchFilter.SERIE_TV -> getUniqueSeriesCategories(state.series)
                        SearchFilter.UTENTI -> emptyList()
                    },
                    selectedCategory = selectedCategory,
                    onCategorySelected = { category ->
                        selectedCategory = category
                        showCategoryFilter = false
                    },
                    onDismiss = { showCategoryFilter = false },
                    filterType = selectedFilter
                )
            }

            // Content based on selected filter
            when (selectedFilter) {
                SearchFilter.FILM -> {
                    FilmSearchResults(
                        state,
                        films = filterFilms(state.films, searchQuery, selectedCategory),
                        navController = navController
                    )
                }
                SearchFilter.SERIE_TV -> {
                    SeriesTvSearchResults(
                        series = filterSeries(state.series, searchQuery, selectedCategory),
                        navController = navController
                    )
                }
                SearchFilter.UTENTI -> {
                    UsersSearchResults(
                        state = state,
                        users = filterUsers(state.users, searchQuery), // Usa i dati dal database
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryFilterDialog(
    categories: List<String>,
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit,
    onDismiss: () -> Unit,
    filterType: SearchFilter
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = when(filterType) {
                    SearchFilter.FILM -> "Filtra per Categoria Film"
                    SearchFilter.SERIE_TV -> "Filtra per Categoria Serie TV"
                    SearchFilter.UTENTI -> "Filtri non disponibili"
                },
                color = Color.White
            )
        },
        text = {
            if (filterType == SearchFilter.UTENTI) {
                Text(
                    "I filtri non sono disponibili per questa categoria di ricerca.",
                    color = Color.Gray
                )
            } else {
                LazyColumn {
                    // Opzione "Tutte le categorie"
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onCategorySelected(null) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedCategory == null,
                                onClick = { onCategorySelected(null) },
                                colors = RadioButtonDefaults.colors(selectedColor = Color.Red)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Tutte le categorie",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    // Lista delle categorie
                    items(categories) { category ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onCategorySelected(category) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedCategory == category,
                                onClick = { onCategorySelected(category) },
                                colors = RadioButtonDefaults.colors(selectedColor = Color.Red)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                category,
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Chiudi", color = Color.Red)
            }
        },
        containerColor = Color.Black,
        tonalElevation = 8.dp
    )
}

@Composable
fun FilterButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color.Red else Color.Transparent,
            contentColor = if (isSelected) Color.White else Color.Gray
        ),
        border = if (!isSelected) ButtonDefaults.outlinedButtonBorder else null,
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
    ) {
        Text(text)
    }
}

fun getUniqueFilmCategories(films: List<Film>): List<String> {
    return films
        .filter { it.visibile }
        .map { it.categoria }  // Assumendo che Film abbia un campo categoria
        .distinct()
        .sorted()
}

fun getUniqueSeriesCategories(series: List<SerieTV>): List<String> {
    return series
        .filter { it.visibile }
        .map { it.categoria }  // Assumendo che SerieTV abbia un campo categoria
        .distinct()
        .sorted()
}

@Composable
fun FilmSearchResults(
    state: LookifyState,
    films: List<Film>,
    navController: NavController
) {
    if (films.isEmpty()) {
        NoResultsPlaceholder("Nessun film trovato")
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxHeight()
        ) {
            items(films) { film ->
                FilmSearchItem(
                    film = film,
                    onClick = {
                        navController.navigate("${LookifyRoute.Film}?filmId=${film.id_film}&currentUserId=${state.currentUserId}")
                    }
                )
            }
        }
    }
}

@Composable
fun SeriesTvSearchResults(
    series: List<SerieTV>,
    navController: NavController
) {
    if (series.isEmpty()) {
        NoResultsPlaceholder("Nessuna serie TV trovata")
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(series) { serie ->
                SerieSearchItem(
                    serie = serie,
                    onClick = {
                        //navController.navigate("${LookifyRoute.SerieDetail}/${serie.id_serie}")
                    }
                )
            }
        }
    }
}

@Composable
fun UsersSearchResults(
    state: LookifyState,
    users: List<Users>,
    navController: NavController
) {
    if (users.isEmpty()) {
        NoResultsPlaceholder("Nessun utente trovato")
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxHeight()
        ) {
            items(users) { user ->
                UserSearchItem(
                    user = user,
                    onClick = {
                        navController.navigate(
                            "${LookifyRoute.OtherUser}?userId=${user.id_user}&currentUserId=${state.currentUserId}"
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun UserSearchItem(user: Users, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Immagine profilo circolare
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.Gray.copy(alpha = 0.3f))
                    .border(2.dp, Color.White.copy(alpha = 0.2f), CircleShape)
            ) {
                if (user.immagine != null) {
                    val imageUri = Uri.parse(user.immagine)
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Profilo ${user.username}",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Placeholder con icona persona
                    Icon(
                        Icons.Outlined.Person,
                        contentDescription = "Nessuna Immagine Profilo",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Nome utente
            Text(
                text = user.username,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                textAlign = TextAlign.Center,
                maxLines = 1,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun FilmSearchItem(film: Film, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val imageUri = film.imageUri?.let { Uri.parse(it) }
            if (imageUri != null) {
                ImageWithPlaceholder(imageUri, Size.Lg)
            } else {
                Icon(
                    Icons.Outlined.TvOff,
                    contentDescription = "Nessuna Immagine",
                    modifier = Modifier.size(200.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = film.titolo,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

@Composable
fun SerieSearchItem(serie: SerieTV, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val imageUri = serie.imageUri?.let { Uri.parse(it) }
            if (imageUri != null) {
                ImageWithPlaceholder(imageUri, Size.Lg)
            } else {
                Icon(
                    Icons.Outlined.TvOff,
                    contentDescription = "Nessuna Immagine",
                    modifier = Modifier.size(200.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = serie.titolo,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

@Composable
fun NoResultsPlaceholder(message: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Icon(
            Icons.Outlined.Search,
            contentDescription = "Nessun Risultato",
            modifier = Modifier.size(64.dp),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            message,
            style = MaterialTheme.typography.titleMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

// Helper functions per filtrare i risultati
private fun filterFilms(films: List<Film>, query: String, category: String? = null): List<Film> {
    return films.filter { film ->
        film.visibile &&
                (query.isBlank() || film.titolo.contains(query, ignoreCase = true)) &&
                (category == null || film.categoria == category)
    }
}

private fun filterSeries(series: List<SerieTV>, query: String, category: String? = null): List<SerieTV> {
    return series.filter { serie ->
        serie.visibile &&
                (query.isBlank() || serie.titolo.contains(query, ignoreCase = true)) &&
                (category == null || serie.categoria == category)
    }
}

private fun filterUsers(users: List<Users>, query: String): List<Users> {
    return users.filter { user ->
        query.isBlank() || user.username.contains(query, ignoreCase = true)
    }
}