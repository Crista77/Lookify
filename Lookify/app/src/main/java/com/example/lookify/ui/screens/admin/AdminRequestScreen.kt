package com.example.lookify.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lookify.data.database.*
import com.example.lookify.data.repositories.FilmsRepository
import com.example.lookify.ui.LookifyRoute
import com.example.lookify.ui.LookifyState
import com.example.lookify.ui.LookifyViewModel
import com.example.lookify.ui.composables.TitleAppBar
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AdminRequestsPreviewScreen(
    state: LookifyState,
    navController: NavController,
    viewModel: LookifyViewModel
) {
    val filmRequests = state.filmRequests
    val serieRequests = state.seriesRequests
    val allFilms = state.films
    val allSeries = state.series
    val allUsers = state.users

    var selectedFilter by remember { mutableStateOf("Tutte") }

    // Combina le richieste in una lista unificata
    val combinedRequests = remember(filmRequests, serieRequests, allFilms, allSeries, allUsers) {
        val filmRequestsWithData = filmRequests.mapNotNull { request ->
            val film = allFilms.find { it.id_film == request.filmId }
            val user = allUsers.find { it.id_user == request.richiedenteId }
            if (film != null && user != null) {
                RequestItem.FilmRequestItem(request, film, user)
            } else null
        }

        val serieRequestsWithData = serieRequests.mapNotNull { request ->
            val serie = allSeries.find { it.id_serie == request.serieId }
            val user = allUsers.find { it.id_user == request.richiedenteId }
            if (serie != null && user != null) {
                RequestItem.SerieRequestItem(request, serie, user)
            } else null
        }

        filmRequestsWithData + serieRequestsWithData
    }

    // Filtra le richieste
    val filteredRequests = remember(combinedRequests, selectedFilter) {
        when (selectedFilter) {
            "Film" -> combinedRequests.filterIsInstance<RequestItem.FilmRequestItem>()
            "Serie TV" -> combinedRequests.filterIsInstance<RequestItem.SerieRequestItem>()
            "Da Approvare" -> combinedRequests.filter {
                when (it) {
                    is RequestItem.FilmRequestItem -> !it.request.approvato
                    is RequestItem.SerieRequestItem -> !it.request.approvato
                }
            }
            "Approvate" -> combinedRequests.filter {
                when (it) {
                    is RequestItem.FilmRequestItem -> it.request.approvato
                    is RequestItem.SerieRequestItem -> it.request.approvato
                }
            }
            else -> combinedRequests
        }
    }

    Scaffold(
        topBar = { TitleAppBar(navController, state) }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
                .background(Color.Black)
                .padding(horizontal = 16.dp)
        ) {
            // Header
            Text(
                text = "Gestione Richieste",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Statistiche
            RequestsStatsCard(
                totalRequests = combinedRequests.size,
                pendingRequests = combinedRequests.count {
                    when (it) {
                        is RequestItem.FilmRequestItem -> !it.request.approvato
                        is RequestItem.SerieRequestItem -> !it.request.approvato
                    }
                },
                approvedRequests = combinedRequests.count {
                    when (it) {
                        is RequestItem.FilmRequestItem -> it.request.approvato
                        is RequestItem.SerieRequestItem -> it.request.approvato
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Filtri
            FilterRow(
                selectedFilter = selectedFilter,
                onFilterChange = { selectedFilter = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Lista richieste
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredRequests) { requestItem ->
                    RequestCard(
                        requestItem = requestItem,
                        onClick = {
                            when (requestItem) {
                                is RequestItem.FilmRequestItem -> {
                                    navController.navigate("${LookifyRoute.AdminRequestDetailScreen}/film/${requestItem.request.id_request}")
                                }
                                is RequestItem.SerieRequestItem -> {
                                    navController.navigate("${LookifyRoute.AdminRequestDetailScreen}/serie/${requestItem.request.id_request}")
                                }
                            }
                        }
                    )
                }

                if (filteredRequests.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Nessuna richiesta trovata",
                                    color = Color.Gray,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
}

@Composable
fun RequestsStatsCard(
    totalRequests: Int,
    pendingRequests: Int,
    approvedRequests: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                icon = Icons.Default.AccessTime,
                value = totalRequests.toString(),
                label = "Totali",
                color = Color.White
            )
            StatItem(
                icon = Icons.Default.Pending,
                value = pendingRequests.toString(),
                label = "In Attesa",
                color = Color.Yellow
            )
            StatItem(
                icon = Icons.Default.CheckCircle,
                value = approvedRequests.toString(),
                label = "Approvate",
                color = Color.Green
            )
        }
    }
}

@Composable
fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            color = color,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 12.sp
        )
    }
}

@Composable
fun FilterRow(
    selectedFilter: String,
    onFilterChange: (String) -> Unit
) {
    val filters = listOf("Tutte", "Film", "Serie TV", "Da Approvare", "Approvate")

    LazyRow (
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters) { filter ->
            FilterChip(
                text = filter,
                isSelected = selectedFilter == filter,
                onClick = { onFilterChange(filter) }
            )
        }
    }
}

@Composable
fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color.Red else Color(0xFF2E2E2E)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun RequestCard(
    requestItem: RequestItem,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = when (requestItem) {
                            is RequestItem.FilmRequestItem -> Icons.Default.Movie
                            is RequestItem.SerieRequestItem -> Icons.Default.Tv
                        },
                        contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = when (requestItem) {
                                is RequestItem.FilmRequestItem -> requestItem.film.titolo
                                is RequestItem.SerieRequestItem -> requestItem.serie.titolo
                            },
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = when (requestItem) {
                                    is RequestItem.FilmRequestItem -> requestItem.user.username
                                    is RequestItem.SerieRequestItem -> requestItem.user.username
                                },
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = when (requestItem) {
                                is RequestItem.FilmRequestItem -> {
                                    if (requestItem.request.approvato) Color.Green else Color.Yellow
                                }
                                is RequestItem.SerieRequestItem -> {
                                    if (requestItem.request.approvato) Color.Green else Color.Yellow
                                }
                            }
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = when (requestItem) {
                                is RequestItem.FilmRequestItem -> {
                                    if (requestItem.request.approvato) "Approvata" else "In Attesa"
                                }
                                is RequestItem.SerieRequestItem -> {
                                    if (requestItem.request.approvato) "Approvata" else "In Attesa"
                                }
                            },
                            color = Color.Black,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = when (requestItem) {
                    is RequestItem.FilmRequestItem -> requestItem.film.descrizione
                    is RequestItem.SerieRequestItem -> requestItem.serie.descrizione
                },
                color = Color.Gray,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// Sealed class per gestire i diversi tipi di richieste
sealed class RequestItem {
    data class FilmRequestItem(
        val request: FilmRequest,
        val film: Film,
        val user: Users
    ) : RequestItem()

    data class SerieRequestItem(
        val request: SerieTV_Request,
        val serie: SerieTV,
        val user: Users
    ) : RequestItem()
}