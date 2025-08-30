package com.example.lookify.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.lookify.data.database.*
import com.example.lookify.ui.LookifyRoute
import com.example.lookify.ui.LookifyState
import com.example.lookify.ui.LookifyViewModel
import com.example.lookify.ui.composables.TitleAppBar
import com.example.lookify.ui.screens.cinema.getCurrentUser

@Composable
fun AdminRequestDetailScreen(
    state: LookifyState,
    navController: NavController,
    viewModel: LookifyViewModel,
    requestType: String, // "film" o "serie"
    requestId: Int
) {
    val filmRequests = state.filmRequests
    val serieRequests = state.seriesRequests
    val allFilms = state.films
    val allSeries = state.series
    val allUsers = state.users
    val allActors = state.actors
    val allPlatforms = state.platforms

    var showConfirmDialog by remember { mutableStateOf(false) }
    var confirmAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    var confirmMessage by remember { mutableStateOf("") }

    val context = LocalContext.current

    // Trova i dati della richiesta
    val requestData = remember(filmRequests, serieRequests, allFilms, allSeries, allUsers, requestType, requestId) {
        if (requestType == "film") {
            val request = filmRequests.find { it.id_request == requestId }
            val film = request?.let { allFilms.find { f -> f.id_film == it.filmId } }
            val user = request?.let { allUsers.find { u -> u.id_user == it.richiedenteId } }
            if (request != null && film != null && user != null) {
                RequestDetailData.FilmData(request, film, user)
            } else null
        } else {
            val request = serieRequests.find { it.id_request == requestId }
            val serie = request?.let { allSeries.find { s -> s.id_serie == it.serieId } }
            val user = request?.let { allUsers.find { u -> u.id_user == it.richiedenteId } }
            if (request != null && serie != null && user != null) {
                RequestDetailData.SerieData(request, serie, user)
            } else null
        }
    }

    // Trova attori collegati
    var actors by remember(requestData) {
        mutableStateOf<List<Actors>>(emptyList())
    }

    LaunchedEffect(requestData) {
        when (requestData) {
            is RequestDetailData.FilmData -> {
                viewModel.getActorsByFilmId(requestData.film.id_film)
                    .collect { actorsList ->
                        actors = actorsList
                    }
            }
            is RequestDetailData.SerieData -> {
                viewModel.getActorsBySerieId(requestData.serie.id_serie)
                    .collect { actorsList ->
                        actors = actorsList
                    }
            }
            null -> Unit
        }
    }


    // Trova piattaforma
    val platform = remember(requestData, allPlatforms) {
        when (requestData) {
            is RequestDetailData.FilmData -> {
                // Trova la piattaforma collegata al film
                val link = state.filmPlatforms.find { it.filmId == requestData.film.id_film }
                link?.let { allPlatforms.find { p -> p.id == it.piattaformaId } }
            }
            is RequestDetailData.SerieData -> {
                // Trova la piattaforma collegata alla serie
                val link = state.seriePlatform.find { it.serieId == requestData.serie.id_serie }
                link?.let { allPlatforms.find { p -> p.id == it.piattaformaId } }
            }
            null -> null
        }
    }


    if (requestData == null) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Richiesta non trovata",
                color = Color.White,
                fontSize = 18.sp
            )
        }
        return
    }

    Scaffold(
        topBar = { TitleAppBar(navController) }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
                .background(Color.Black)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Dettaglio Richiesta",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }

                // Status Card
                item {
                    StatusCard(
                        isApproved = when (requestData) {
                            is RequestDetailData.FilmData -> requestData.request.approvato
                            is RequestDetailData.SerieData -> requestData.request.approvato
                        },
                        requesterName = requestData.user.username,
                        contentType = requestType
                    )
                }

                // Immagine copertina
                item {
                    val imageUri = when (requestData) {
                        is RequestDetailData.FilmData -> requestData.film.imageUri
                        is RequestDetailData.SerieData -> requestData.serie.imageUri
                    }

                    if (!imageUri.isNullOrEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(imageUri)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Copertina",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                // Titolo
                item {
                    DetailField(
                        label = "Titolo",
                        value = when (requestData) {
                            is RequestDetailData.FilmData -> requestData.film.titolo
                            is RequestDetailData.SerieData -> requestData.serie.titolo
                        }
                    )
                }

                // Piattaforma
                item {
                    DetailField(
                        label = "Piattaforma",
                        value = platform?.nome ?: "N/D"
                    )
                }

                // Numero Cast
                item {
                    DetailField(
                        label = "Numero Personale Cast",
                        value = when (requestData) {
                            is RequestDetailData.FilmData -> requestData.film.numero_Cast.toString()
                            is RequestDetailData.SerieData -> requestData.serie.numero_Cast.toString()
                        }
                    )
                }

                // Attori
                if (actors.isNotEmpty()) {
                    items(actors.size) { index ->
                        DetailField(
                            label = "Attore ${index + 1}",
                            value = "${actors[index].nome} ${actors[index].cognome}"
                        )
                    }
                }

                // Descrizione
                item {
                    DetailField(
                        label = "Descrizione",
                        value = when (requestData) {
                            is RequestDetailData.FilmData -> requestData.film.descrizione
                            is RequestDetailData.SerieData -> requestData.serie.descrizione
                        },
                        maxLines = 5
                    )
                }

                // Durata
                item {
                    DetailField(
                        label = "Durata",
                        value = "${when (requestData) {
                            is RequestDetailData.FilmData -> requestData.film.durata
                            is RequestDetailData.SerieData -> requestData.serie.durata
                        }} minuti"
                    )
                }

                // Categoria
                item {
                    DetailField(
                        label = "Categoria",
                        value = when (requestData) {
                            is RequestDetailData.FilmData -> requestData.film.categoria
                            is RequestDetailData.SerieData -> requestData.serie.categoria
                        }
                    )
                }

                item { Spacer(modifier = Modifier.height(100.dp)) }
            }

            // Bottoni azioni (solo se non ancora approvata)
            val isAlreadyApproved = when (requestData) {
                is RequestDetailData.FilmData -> requestData.request.approvato
                is RequestDetailData.SerieData -> requestData.request.approvato
            }

            if (!isAlreadyApproved) {
                ActionButtons(
                    onApprove = {
                        confirmMessage = "Sei sicuro di voler approvare questa richiesta?"
                        confirmAction = {
                            when (requestData) {
                                is RequestDetailData.FilmData -> {
                                    viewModel.approveFilmRequest(requestId, context)
                                }
                                is RequestDetailData.SerieData -> {
                                    viewModel.approveSerieRequest(requestId, context)
                                }
                            }
                            navController.navigate("${LookifyRoute.AdminUser}?userId=${state.currentUserId}")
                        }
                        showConfirmDialog = true
                    },
                    onReject = {
                        confirmMessage = "Sei sicuro di voler rifiutare questa richiesta? Il contenuto verrà eliminato definitivamente."
                        confirmAction = {
                            when (requestData) {
                                is RequestDetailData.FilmData -> {
                                    viewModel.rejectFilmRequest(context, requestId)
                                }
                                is RequestDetailData.SerieData -> {
                                    viewModel.rejectSerieRequest(context, requestId)
                                }
                            }
                            navController.navigate("${LookifyRoute.AdminUser}?userId=${state.currentUserId}")
                        }
                        showConfirmDialog = true
                    }
                )
            }
        }
    }

    // Dialog di conferma
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Conferma Azione") },
            text = { Text(confirmMessage) },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmAction?.invoke()
                        showConfirmDialog = false
                    }
                ) {
                    Text("Conferma", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showConfirmDialog = false }
                ) {
                    Text("Annulla")
                }
            },
            containerColor = Color(0xFF1E1E1E),
            titleContentColor = Color.White,
            textContentColor = Color.Gray
        )
    }
}

@Composable
fun StatusCard(
    isApproved: Boolean,
    requesterName: String,
    contentType: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isApproved) Color.Green.copy(alpha = 0.1f) else Color.Yellow.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Richiesta ${contentType.uppercase()}",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Richiesta da: $requesterName",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isApproved) Color.Green else Color.Yellow
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (isApproved) "APPROVATA" else "IN ATTESA",
                    color = Color.Black,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun DetailField(
    label: String,
    value: String,
    maxLines: Int = 1
) {
    Column {
        Text(
            text = label,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = value,
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.padding(16.dp),
                maxLines = maxLines
            )
        }
    }
}

@Composable
fun ActionButtons(
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Bottone Rifiuta
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onReject() },
                colors = CardDefaults.cardColors(containerColor = Color.Gray),
                shape = RoundedCornerShape(25.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Rifiuta",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Rifiuta",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Bottone Approva
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onApprove() },
                colors = CardDefaults.cardColors(containerColor = Color.Green),
                shape = RoundedCornerShape(25.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Approva",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Approva",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// Sealed class per gestire i dati della richiesta
sealed class RequestDetailData {
    abstract val user: Users

    data class FilmData(
        val request: FilmRequest,
        val film: Film,
        override val user: Users
    ) : RequestDetailData()

    data class SerieData(
        val request: SerieTV_Request,
        val serie: SerieTV,
        override val user: Users
    ) : RequestDetailData()
}