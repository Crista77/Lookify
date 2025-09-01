package com.example.lookify.ui.screens.user

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.lookify.data.database.Users
import com.example.lookify.ui.LookifyRoute
import com.example.lookify.ui.LookifyState
import com.example.lookify.ui.LookifyViewModel
import com.example.lookify.ui.composables.BottomBar
import com.example.lookify.ui.composables.TitleAppBar

@Composable
fun OtherUserScreen(
    state: LookifyState,
    navController: NavController,
    lookifyViewModel: LookifyViewModel,
    otherUserId: Int,
    currentUserId: Int
) {
    val context = LocalContext.current
    val otherUser = state.users.find { it.id_user == otherUserId }

    var selectedCategory by remember { mutableStateOf("Film") }
    var isFollowLoading by remember { mutableStateOf(false) }
    var refreshTrigger by remember { mutableStateOf(0) }

    val watchedContent = remember(otherUserId, selectedCategory, state) {
        when (selectedCategory) {
            "Film" -> {
                val user = state.users.filter { it.id_user == otherUserId }
                val watchedFilms = user.first().filmVisti
                    watchedFilms.mapNotNull { watched ->
                        state.films.find { it.id_film == watched }?.let { film ->
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
            "Serie" -> state.watchedSeries.filter { it.id_user == otherUserId }
                .mapNotNull { watched ->
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
            else -> emptyList()
        }
    }

    val categoryStats = remember(watchedContent) {
        CategoryStats(
            totalViewed = watchedContent.size,
            totalDuration = watchedContent.sumOf { it.duration }
        )
    }

    val followersCount = remember(otherUserId, state.followers) {
        state.followers.count { it.seguitoId == otherUserId }
    }

    val userTrophies = remember(otherUserId, state) {
        val userAchievements = state.achievements.filter { it.id_user == otherUserId }
        state.trophies.map { trophy ->
            TrophyItem(
                trophy = Trophy(
                    id = trophy.id,
                    nome = trophy.nome,
                    icon = getTrophyIcon(trophy.nome)
                ),
                isUnlocked = userAchievements.any { it.trofeoId == trophy.id }
            )
        }
    }

    val isFollowing by remember(state.followers, currentUserId, otherUserId, refreshTrigger) {
        derivedStateOf {
            state.followers.any { it.seguaceId == currentUserId && it.seguitoId == otherUserId }
        }
    }

    Scaffold(
        topBar = { TitleAppBar(navController, state) },
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
                OtherProfileHeader(
                    user = otherUser,
                    followersCount = followersCount,
                    isFollowing = isFollowing,
                    isLoading = isFollowLoading,
                    onFollowClick = {
                        isFollowLoading = true

                        if (isFollowing) {
                            lookifyViewModel.unfollowUser(currentUserId, otherUserId) {
                                isFollowLoading = false
                                refreshTrigger++

                                navController.navigate(
                                    "${LookifyRoute.OtherUser}?userId=$otherUserId&currentUserId=$currentUserId"
                                ) {
                                    popUpTo("${LookifyRoute.OtherUser}?userId=$otherUserId&currentUserId=$currentUserId") {
                                        inclusive = true
                                    }
                                }
                            }
                        } else {
                            lookifyViewModel.addFollower(context, currentUserId, otherUserId) {
                                isFollowLoading = false
                                refreshTrigger++

                                navController.navigate(
                                    "${LookifyRoute.OtherUser}?userId=$otherUserId&currentUserId=$currentUserId"
                                ) {
                                    popUpTo("${LookifyRoute.OtherUser}?userId=$otherUserId&currentUserId=$currentUserId") {
                                        inclusive = true
                                    }
                                }
                            }
                        }
                    }
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
                WatchedContentCard(
                    content = content,
                    navController = navController,
                    currentUserId = state.currentUserId
                )
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
fun OtherProfileHeader(
    user: Users?,
    followersCount: Int,
    isFollowing: Boolean,
    isLoading: Boolean = false,
    onFollowClick: () -> Unit
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
                    modifier = Modifier.fillMaxSize().clip(CircleShape),
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

        Text(text = "Profilo Utente", color = Color.White, fontSize = 16.sp)
        Text(text = user?.username ?: "Guest", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(text = "$followersCount Followers", color = Color.Red, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { onFollowClick() },
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = when {
                    isLoading -> Color.Gray
                    isFollowing -> Color.Red
                    else -> Color(0xFF4CAF50)
                },
                disabledContainerColor = Color.DarkGray
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.height(40.dp)
        ) {
            if (isLoading) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Attendere...", color = Color.White, fontSize = 12.sp)
                }
            } else {
                Text(
                    text = if (isFollowing) "Seguito" else "Segui",
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
        }
    }
}
