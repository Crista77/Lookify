package com.example.lookify.ui.screens.admin

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.example.lookify.data.database.Users
import com.example.lookify.ui.LookifyRoute
import com.example.lookify.ui.LookifyState
import com.example.lookify.ui.composables.BottomBar
import com.example.lookify.ui.composables.TitleAppBar

@Composable
fun AdminUserScreen(
    state: LookifyState,
    navController: NavController,
    onRichiesteClick: () -> Unit = {}
) {
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

    // Controlla se ci sono notifiche non lette per l'utente corrente
    val hasUnreadNotifications = remember(state.reachedNotifications, state.currentUserId) {
        state.reachedNotifications.any { notification ->
            notification.id_user == state.currentUserId && !notification.letta
        }
    }

    Scaffold(
        topBar = { TitleAppBar(navController) },
        bottomBar = { BottomBar(state, navController)}
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
                .background(Color.Black)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 🔹 Header Profilo Admin
            AdminProfileHeader(
                user = currentUser,
                hasUnreadNotifications = hasUnreadNotifications,
                onNotificationClick = {
                    navController.navigate("${LookifyRoute.Notification}?userId=${state.currentUserId}")
                }
            )

            Spacer(modifier = Modifier.height(60.dp))

            Log.d("AdminUserScreen", "currentUserId: $currentUserId")
            // 🔹 Bottone Inserisci
            AdminButton(
                text = "Inserisci",
                onClick = {navController.navigate(LookifyRoute.Insert)}
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Bottone Richieste
            AdminButton(
                text = "Richieste",
                onClick = {navController.navigate(LookifyRoute.AdminRequestPrev)}
            )
        }
    }
}

@Composable
fun AdminProfileHeader(
    user: Users?,
    hasUnreadNotifications: Boolean = false,
    onNotificationClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Header con titolo e campanella notifiche
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Profilo Gestione App",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            // Pulsante campanella notifiche
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clickable { onNotificationClick() }
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifiche",
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.Center)
                )

                // Pallino rosso per notifiche non lette
                if (hasUnreadNotifications) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(Color.Red, CircleShape)
                            .align(Alignment.TopEnd)
                            .zIndex(1f)
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .size(100.dp)
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

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = user?.username ?: "Guest",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AdminButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(200.dp)
            .height(50.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.Red),
        shape = RoundedCornerShape(25.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}