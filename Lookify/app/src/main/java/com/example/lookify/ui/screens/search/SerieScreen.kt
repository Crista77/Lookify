package com.example.lookify.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.lookify.ui.LookifyRoute
import com.example.lookify.ui.LookifyState
import com.example.lookify.ui.LookifyViewModel
import com.example.lookify.ui.composables.BottomBar
import com.example.lookify.ui.composables.TitleAppBar

@Composable
fun SerieScreen(
    state: LookifyState,
    navController: NavController,
    lookifyViewModel: LookifyViewModel,
    serieId: Int,
    currentUserId: Int
) {
    val serie = state.series.find { it.id_serie == serieId }

    val context = LocalContext.current
    // Stato locale
    var isLoadingWatched by remember { mutableStateOf(false) }
    var isLoadingRating by remember { mutableStateOf(false) }
    var rating by remember(serie?.stelle) { mutableStateOf(serie?.stelle ?: 0) }

    // Calcola isWatched **da stato corrente**, non da snapshot
    val isWatched = remember(state.users) {
        derivedStateOf {
            state.users.find { it.id_user == currentUserId }?.serieViste?.contains(serieId) == true
        }
    }

    val serieActors = remember(state.actorsInSerie, state.actors) {
        val actorIds = state.actorsInSerie.filter { it.serieId == serieId }.map { it.attoreId }
        state.actors.filter { it.id in actorIds }
    }

    val seriePlatform = remember(state.seriePlatform, state.platforms) {
        val platformIds = state.seriePlatform.filter { it.serieId == serieId }.map { it.piattaformaId }
        state.platforms.filter { it.id in platformIds }
    }

    val friendsWhoWatched = remember(state.followers, state.users) {
        val followedIds = state.followers.filter { it.seguaceId == currentUserId }.map { it.seguitoId }
        state.users.filter { it.id_user in followedIds && it.serieViste.contains(serieId) }
    }

    Scaffold(
        topBar = { TitleAppBar(navController, state) },
        bottomBar = { BottomBar(state, navController) }
    ) { contentPadding ->

        if (serie == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text("Serie non trovata", color = Color.White, fontSize = 18.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(contentPadding)
                    .fillMaxSize()
                    .background(Color.Black)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {

                        AsyncImage(
                            model = serie.imageUri,
                            contentDescription = serie.titolo,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(280.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = serie.titolo,
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(serie.categoria, color = Color(0xFF4CAF50), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Text(" • ${serie.durata} min", color = Color(0xFFB0B0B0), fontSize = 14.sp)
                            if (serie.visualizzazioni > 0) {
                                Text(" • ${serie.visualizzazioni} visualizzazioni", color = Color(0xFFB0B0B0), fontSize = 14.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Descrizione", color = Color.Red, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(serie.descrizione, color = Color.White, fontSize = 14.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                isLoadingWatched = true
                                if (isWatched.value) {
                                    lookifyViewModel.removeWatchedSerie(currentUserId, serieId) {
                                        isLoadingWatched = false
                                    }
                                } else {
                                    lookifyViewModel.addWatchedSerie(context, currentUserId, serieId) {
                                        isLoadingWatched = false
                                    }
                                }
                            },
                            enabled = !isLoadingWatched,
                            colors = ButtonDefaults.buttonColors(containerColor = if (isWatched.value) Color(0xFFE53935) else Color(0xFF4CAF50)),
                            shape = RoundedCornerShape(25.dp),
                            modifier = Modifier.fillMaxWidth(0.8f).height(50.dp)
                        ) {
                            if (isLoadingWatched) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                Text(if (isWatched.value) "Visto" else "Non visto", color = Color.White, fontSize = 16.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { navController.navigate("${LookifyRoute.Users}?userId=$currentUserId") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                            shape = RoundedCornerShape(25.dp),
                            modifier = Modifier.fillMaxWidth(0.6f).height(45.dp)
                        ) {
                            Text("Vai al profilo", color = Color.White, fontSize = 14.sp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Valuta questa Serie", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                            (1..5).forEach { star ->
                                Icon(
                                    imageVector = if (star <= rating) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                    contentDescription = "Stella $star",
                                    tint = if (star <= rating) Color.Yellow else Color(0xFF666666),
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clickable(enabled = !isLoadingRating) {
                                            isLoadingRating = true
                                            val newRating = if (rating == star) 0 else star
                                            lookifyViewModel.rateSerie(currentUserId, serieId, newRating) {
                                                rating = newRating
                                                isLoadingRating = false
                                            }
                                        }
                                        .padding(4.dp)
                                )
                            }
                        }

                        if (isLoadingRating) {
                            Spacer(modifier = Modifier.height(8.dp))
                            CircularProgressIndicator(color = Color(0xFF4CAF50), modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        } else if (rating > 0) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Rating: $rating stelle", color = Color(0xFFB0B0B0), fontSize = 14.sp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (serieActors.isNotEmpty()) {
                            Text("Cast:", color = Color.White, fontWeight = FontWeight.Bold)
                            LazyRow { items(serieActors) { actor ->
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(end = 12.dp)) {
                                    Icon(Icons.Filled.Person, contentDescription = actor.cognome, tint = Color.White, modifier = Modifier.size(40.dp))
                                    Text(actor.cognome, color = Color.White, fontSize = 12.sp)
                                }
                            } }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (seriePlatform.isNotEmpty()) {
                            Text("Disponibile su:", color = Color.White, fontWeight = FontWeight.Bold)
                            LazyRow { items(seriePlatform) { platform ->
                                Box(
                                    modifier = Modifier
                                        .padding(end = 12.dp)
                                        .background(Color.Gray, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) { Text(platform.nome, color = Color.White, fontSize = 12.sp) }
                            } }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (friendsWhoWatched.isNotEmpty()) {
                            Text("Amici che hanno visto questa Serie:", color = Color.White, fontWeight = FontWeight.Bold)
                            LazyRow { items(friendsWhoWatched) { friend ->
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(end = 12.dp)) {
                                    Icon(Icons.Filled.Person, contentDescription = friend.username, tint = Color.White, modifier = Modifier.size(32.dp))
                                    Text(friend.username, color = Color.White, fontSize = 12.sp)
                                }
                            } }
                        }
                    }
                }
            }
        }
    }
}
