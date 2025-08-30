package com.example.lookify.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.lookify.ui.LookifyState
import com.example.lookify.ui.LookifyViewModel
import com.example.lookify.ui.composables.BottomBar
import com.example.lookify.ui.composables.TitleAppBar

@Composable
fun FilmScreen(
    state: LookifyState,
    navController: NavController,
    lookifyViewModel: LookifyViewModel,
    filmId: Int,
    currentUserId: Int
) {
    val context = LocalContext.current
    val film = state.films.find { it.id_film == filmId }
    val currentUserWatched = state.watchedFilms.find {
        it.filmId == filmId && it.utenteId == currentUserId
    }

    var isWatched by remember { mutableStateOf(currentUserWatched != null) }
    var rating by remember { mutableStateOf(film?.stelle ?: 0) }
    var isLoading by remember { mutableStateOf(false) }

    val friendsWhoWatched = remember(state.followers, state.watchedFilms) {
        val myFollowed = state.followers.filter { it.seguaceId == currentUserId }.map { it.seguitoId }
        val watchedByFriends = state.watchedFilms.filter { it.filmId == filmId && it.utenteId in myFollowed }
        watchedByFriends.mapNotNull { watched -> state.users.find { it.id_user == watched.utenteId } }
    }

    Scaffold(
        topBar = { TitleAppBar(navController) },
        bottomBar = { BottomBar(state, navController) }
    ) { contentPadding ->
        if (film == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Film non trovato", color = Color.White)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(contentPadding)
                    .fillMaxSize()
                    .background(Color.Black)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        AsyncImage(
                            model = film.imageUri,
                            contentDescription = film.titolo,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(film.titolo, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text("${film.categoria} • ${film.durata} min", color = Color.Gray, fontSize = 14.sp)

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                isLoading = true
                                if (isWatched) {
                                    lookifyViewModel.removeWatchedFilm(currentUserId, filmId) {
                                        isWatched = false
                                        isLoading = false
                                    }
                                } else {
                                    lookifyViewModel.addWatchedFilm(currentUserId, filmId) {
                                        isWatched = true
                                        isLoading = false
                                    }
                                }
                            },
                            enabled = !isLoading,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isWatched) Color.Red else Color(0xFF4CAF50)
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.height(45.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = if (isWatched) "Già visto" else "Segna come visto",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // ⭐ SEZIONE VALUTAZIONE
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("La tua valutazione", color = Color.White, fontWeight = FontWeight.Bold)

                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            (1..5).forEach { star ->
                                Icon(
                                    imageVector = if (star <= rating) Icons.Filled.Star else Icons.Filled.StarBorder,
                                    contentDescription = "Stella $star",
                                    tint = Color.Yellow,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clickable {
                                            rating = star
                                            lookifyViewModel.rateFilm(
                                                userId = currentUserId,
                                                filmId = filmId,
                                                stars = star
                                            ) {}
                                        }
                                )
                            }
                        }
                    }
                }

                if (friendsWhoWatched.isNotEmpty()) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                "Amici che hanno visto questo film:",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                items(friendsWhoWatched) { friend ->
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Box(
                                            modifier = Modifier
                                                .size(50.dp)
                                                .clip(CircleShape)
                                                .background(Color.Gray),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (!friend.immagine.isNullOrEmpty()) {
                                                AsyncImage(
                                                    model = friend.immagine,
                                                    contentDescription = friend.username,
                                                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                                                    contentScale = ContentScale.Crop
                                                )
                                            } else {
                                                Text(
                                                    text = friend.nome.take(1).uppercase(),
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                        Text(
                                            text = friend.username,
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
