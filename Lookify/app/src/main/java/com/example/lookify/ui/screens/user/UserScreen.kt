package com.example.lookify.ui.screens.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Weekend
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.ModeNight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.example.lookify.data.database.Users
import com.example.lookify.ui.LookifyRoute
import com.example.lookify.ui.LookifyState
import com.example.lookify.ui.composables.BottomBar
import com.example.lookify.ui.composables.TitleAppBar

// 🔹 Item visto dall'utente
data class WatchedItem(
    val id: Int,
    val title: String,
    val duration: Int,
    val category: String,
    val imageUri: String?,
    val type: ContentType,
    val visualizations: Int
)

enum class ContentType {
    FILM,
    SERIE_TV
}

// 🔹 Trofeo con icona Material
data class Trophy(
    val id: Int,
    val nome: String,
    val icon: ImageVector
)

data class TrophyItem(
    val trophy: Trophy,
    val isUnlocked: Boolean
)

// 🔹 Statistiche per categoria
data class CategoryStats(
    val totalViewed: Int,
    val totalDuration: Int
)

@Composable
fun UserScreen(state: LookifyState, navController: NavController) {
    // Leggiamo userId direttamente dagli arguments del BackStackEntry
    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val userIdFromNav = navBackStackEntry?.arguments?.getInt("userId")?.takeIf { it != -1 }

// Se lo state non ha currentUserId, aggiorniamolo con quello del percorso
    if (state.currentUserId == null && userIdFromNav != null) {
        state.currentUserId = userIdFromNav
    }

// Ora possiamo usare currentUserId in modo sicuro
    val currentUserId = state.currentUserId
    val currentUser = currentUserId?.let { id ->
        state.users.find { it.id_user == id }
    }

    var selectedCategory by remember { mutableStateOf("Film") }

    // 🔹 Contenuti guardati
    val watchedContent = remember(currentUserId, selectedCategory, state) {
        currentUserId?.let { userId ->
            when (selectedCategory) {
                "Film" -> {
                    val watchedFilms = state.watchedFilms.filter { it.utenteId == userId }
                    watchedFilms.mapNotNull { watched ->
                        state.films.find { it.id_film == watched.filmId }?.let { film ->
                            WatchedItem(
                                id = film.id_film,
                                title = film.titolo,
                                duration = film.durata,
                                category = film.categoria,
                                imageUri = film.imageUri,
                                type = ContentType.FILM,
                                visualizations = film.visualizzazioni
                            )
                        }
                    }
                }
                "Serie" -> {
                    val watchedSeries = state.watchedSeries.filter { it.id_user == userId }
                    watchedSeries.mapNotNull { watched ->
                        state.series.find { it.id_serie == watched.serieId }?.let { serie ->
                            WatchedItem(
                                id = serie.id_serie,
                                title = serie.titolo,
                                duration = serie.durata,
                                category = serie.categoria,
                                imageUri = serie.imageUri,
                                type = ContentType.SERIE_TV,
                                visualizations = serie.visualizzazioni
                            )
                        }
                    }
                }
                else -> emptyList()
            }
        } ?: emptyList()
    }

    // 🔹 Statistiche per la categoria selezionata
    val categoryStats = remember(watchedContent) {
        CategoryStats(
            totalViewed = watchedContent.size,
            totalDuration = watchedContent.sumOf { it.duration }
        )
    }

    // 🔹 Followers
    val followersCount = remember(currentUserId, state.followers) {
        currentUserId?.let { userId ->
            state.followers.count { it.seguitoId == userId }
        } ?: 0
    }

    // 🔹 Trofei
    val userTrophies = remember(currentUserId, state) {
        currentUserId?.let { userId ->
            val userAchievements = state.achievements.filter { it.id_user == userId }
            state.trophies.map { trophy ->
                TrophyItem(
                    trophy = Trophy(
                        id = trophy.id,
                        nome = trophy.nome,
                        icon = getTrophyIcon(trophy.nome) // Usa la funzione helper
                    ),
                    isUnlocked = userAchievements.any { it.trofeoId == trophy.id }
                )
            }
        } ?: emptyList()
    }

    Scaffold(
        topBar = { TitleAppBar(navController) },
        bottomBar = { BottomBar(state, navController) }
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
                .background(Color.Black)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                ProfileHeader(
                    user = currentUser,
                    followersCount = followersCount
                )
            }

            item {
                CategoryTabs(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it }
                )
            }

            item {
                CategoryStatsCard(
                    categoryStats = categoryStats,
                    categoryName = selectedCategory
                )
            }

            items(watchedContent.take(3)) { content ->
                WatchedContentCard(content = content)
            }

            item {
                RequestButton(onClick = {
                    navController.navigate(LookifyRoute.Request)
                })
            }

            item {
                TrophiesSection(trophies = userTrophies)
            }

            item {
                TrophyProgressBar(trophies = userTrophies, totalTrophiesCount = state.trophies.size)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ProfileHeader(
    user: Users?,
    followersCount: Int
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color(0xFF333333)),
            contentAlignment = Alignment.Center
        ) {
            if (!user?.immagine.isNullOrEmpty()) {
                AsyncImage(
                    model = user?.immagine,
                    contentDescription = "Profile picture",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = user?.nome?.take(2)?.uppercase() ?: "??",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Il Mio Profilo",
            color = Color.White,
            fontSize = 16.sp
        )

        Text(
            text = user?.username ?: "Guest",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "$followersCount Followers",
            color = Color.Red,
            fontSize = 14.sp
        )
    }
}

@Composable
fun CategoryTabs(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf("Film", "Serie").forEach { category ->
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onCategorySelected(category) },
                colors = CardDefaults.cardColors(
                    containerColor = if (category == selectedCategory) Color.Red else Color(0xFF333333)
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = category,
                    color = Color.White,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 4.dp)
                )
            }
        }
    }
}

@Composable
fun CategoryStatsCard(
    categoryStats: CategoryStats,
    categoryName: String
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
            // 🔹 Numero totale visti
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${categoryStats.totalViewed}",
                    color = Color.Red,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$categoryName Visti",
                    color = Color.White,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }

            // 🔹 Divisore verticale
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(50.dp)
                    .background(Color.Gray)
            )

            // 🔹 Durata totale
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val totalHours = categoryStats.totalDuration / 60
                val totalMinutes = categoryStats.totalDuration % 60

                Text(
                    text = if (totalHours > 0) "${totalHours}h ${totalMinutes}m" else "${totalMinutes}m",
                    color = Color.Red,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Tempo Totale",
                    color = Color.White,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun WatchedContentCard(content: WatchedItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                modifier = Modifier.size(60.dp, 90.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF333333)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (!content.imageUri.isNullOrEmpty()) {
                        AsyncImage(
                            model = content.imageUri,
                            contentDescription = content.title,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = content.title.take(3),
                            color = Color.White,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = content.title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "Durata: ${content.duration} min",
                    color = Color.Gray,
                    fontSize = 12.sp
                )

                Text(
                    text = "Categoria: ${content.category}",
                    color = Color.Red,
                    fontSize = 12.sp
                )

                Text(
                    text = "Visualizzazioni: ${content.visualizations}",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = "Liked",
                    tint = Color.Red,
                    modifier = Modifier.size(20.dp)
                )
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.Red,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun RequestButton(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.Red),
        shape = RoundedCornerShape(25.dp)
    ) {
        Text(
            text = "Richiedi film o serie TV",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

@Composable
fun TrophiesSection(trophies: List<TrophyItem>) {
    Column {
        Text(
            text = "Trofei Raggiunti",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(trophies) { trophyItem ->
                Card(
                    modifier = Modifier.size(50.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (trophyItem.isUnlocked) Color(0xFF1E1E1E) else Color(0xFF0A0A0A)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            trophyItem.trophy.icon,
                            contentDescription = trophyItem.trophy.nome,
                            tint = if (trophyItem.isUnlocked) Color(0xFFFFD700) else Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TrophyProgressBar(trophies: List<TrophyItem>, totalTrophiesCount: Int) {
    val unlocked = trophies.count { it.isUnlocked }
    val total = totalTrophiesCount.coerceAtLeast(1) // evita divisione per 0
    val progress = unlocked.toFloat() / total.toFloat()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LinearProgressIndicator(
            progress = { progress },
            color = Color.Red,
            trackColor = Color.DarkGray,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(50))
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "$unlocked / $total Trofei",
            color = Color.White,
            fontSize = 12.sp
        )
    }
}

fun getTrophyIcon(trophyName: String): ImageVector {
    return when (trophyName.lowercase()) {
        "primo film" -> Icons.Filled.PlayArrow
        "cinefilo" -> Icons.Filled.Movie
        "maratoneta" -> Icons.Filled.Schedule
        "esploratore" -> Icons.Filled.Explore
        "fedele spettatore" -> Icons.Filled.Favorite
        "critico" -> Icons.Filled.Star
        "nottambulo" -> Icons.Outlined.ModeNight
        "weekend warrior" -> Icons.Filled.Weekend
        "collezionista" -> Icons.Filled.Collections
        "sociale" -> Icons.Filled.Group
        else -> Icons.Outlined.EmojiEvents // icona di default
    }
}