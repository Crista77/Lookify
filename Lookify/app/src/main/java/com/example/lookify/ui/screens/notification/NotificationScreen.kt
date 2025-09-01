package com.example.lookify.ui.screens.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.lookify.data.database.Notify
import com.example.lookify.ui.LookifyRoute
import com.example.lookify.ui.LookifyState
import com.example.lookify.ui.LookifyViewModel
import com.example.lookify.ui.composables.*

@Composable
fun NotificationsScreen(
    state: LookifyState,
    navController: NavController,
    lookifyViewModel: LookifyViewModel
) {
    // Recupera userId dagli arguments del NavController
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val userIdFromNav = navBackStackEntry?.arguments?.getInt("userId")?.takeIf { it != -1 }

    // Se lo state non ha ancora currentUserId, aggiornalo con quello di Nav
    LaunchedEffect(userIdFromNav) {
        if (state.currentUserId == null && userIdFromNav != null) {
            state.currentUserId = userIdFromNav
        }
    }

    val currentUserId = state.currentUserId

    // Lista delle notifiche dell’utente
    val userNotifications = remember(state.notifications, state.reachedNotifications, currentUserId) {
        currentUserId?.let { userId ->
            state.reachedNotifications
                .filter { it.id_user == userId }
                .mapNotNull { reached ->
                    val notification = state.notifications.find { it.id == reached.notificaId }
                    notification?.let { Triple(it, reached, !reached.letta) }
                }
                .sortedByDescending { it.first.id }
        } ?: emptyList()
    }

    Scaffold(
        topBar = { TitleAppBar(navController, state) },
        bottomBar = { BottomBar(state, navController) }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
                .background(Color.Black)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Titolo
            Text(
                text = "Notifiche",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 24.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            // Azioni globali
            if (userNotifications.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            currentUserId?.let { userId ->
                                lookifyViewModel.markAllNotificationsAsRead(userId)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                    ) {
                        Icon(Icons.Outlined.MarkEmailRead, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Segna tutte come lette")
                    }

                    Button(
                        onClick = {
                            currentUserId?.let { userId ->
                                lookifyViewModel.deleteAllNotifications(userId)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                    ) {
                        Icon(Icons.Outlined.Delete, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Elimina tutte")
                    }
                }
            }

            // Lista notifiche
            if (userNotifications.isNotEmpty()) {
                userNotifications.forEach { (notification, _, isUnread) ->
                    NotificationItem(
                        notification = notification,
                        isUnread = isUnread,
                        onClick = {
                            currentUserId?.let { userId ->
                                lookifyViewModel.markNotificationAsRead(userId, notification.id)
                            }
                            navController.navigate("${LookifyRoute.Notification}?userId=${state.currentUserId}")
                        },
                        onDelete = {
                            currentUserId?.let { userId ->
                                lookifyViewModel.deleteNotification(userId, notification.id)
                            }
                            navController.navigate("${LookifyRoute.Notification}?userId=${state.currentUserId}")
                        }
                    )
                }
            } else {
                // Placeholder quando non ci sono notifiche
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp)
                ) {
                    Icon(
                        Icons.Outlined.Email,
                        contentDescription = "Nessuna notifica",
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Nessuna notifica",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationItem(
    notification: Notify,
    isUnread: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isUnread) 2.dp else 1.dp,
            color = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Pallino rosso se non letta
            if (isUnread) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = Color.Red,
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                )
            } else {
                Spacer(modifier = Modifier.width(8.dp))
            }

            // Contenuto notifica
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.nome,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = if (isUnread) FontWeight.Bold else FontWeight.Normal
                    )
                )

                if (notification.contenuto.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = notification.contenuto,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    )
                }
            }

            // Icona in base al tipo
            val icon = when {
                notification.nome.contains("richiesta", ignoreCase = true) -> Icons.Outlined.Email
                notification.nome.contains("trofeo", ignoreCase = true) -> Icons.Outlined.Star
                notification.nome.contains("segui", ignoreCase = true) -> Icons.Outlined.Refresh
                else -> Icons.Outlined.Notifications
            }

            Icon(
                imageVector = icon,
                contentDescription = "Icona notifica",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Elimina notifica",
                    tint = Color.Red
                )
            }
        }
    }
}
