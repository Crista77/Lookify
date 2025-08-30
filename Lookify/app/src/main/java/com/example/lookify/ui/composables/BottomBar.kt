package com.example.lookify.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Computer
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lookify.ui.LookifyRoute
import com.example.lookify.ui.LookifyState
import com.example.lookify.ui.screens.cinema.getCurrentUser

@Composable
fun BottomBar(state: LookifyState, navController: NavController) {
    Row(
        modifier = Modifier
            .height(72.dp)
            .fillMaxWidth()
            .background(Color.Red),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(onClick = { navController.navigate(LookifyRoute.Cinema) }) {
            Icon(Icons.Outlined.LocationOn, contentDescription = "Cinema", tint = Color.White)
        }
        IconButton(onClick = { navController.navigate(LookifyRoute.SearchSerie) }) {
            Icon(Icons.Outlined.Computer, contentDescription = "TV Series", tint = Color.White)
        }
        IconButton(onClick = { navController.navigate(LookifyRoute.SearchUser) }) {
            Icon(Icons.Outlined.Search, contentDescription = "Search", tint = Color.White)
        }
        IconButton(onClick = { navController.navigate(LookifyRoute.SearchFilm) }) {
            Icon(Icons.Outlined.Movie, contentDescription = "Film", tint = Color.White)
        }
        if (getCurrentUser(state)?.admin == true){
            IconButton(onClick = { navController.navigate("${LookifyRoute.AdminUser}?userId=${state.currentUserId}") }) {
                Icon(Icons.Outlined.Person, contentDescription = "AdminUser", tint = Color.White)
            }
        } else {
            IconButton(onClick = { navController.navigate("${LookifyRoute.Users}?userId=${state.currentUserId}") }) {
                Icon(Icons.Outlined.Person, contentDescription = "Users", tint = Color.White)
            }
        }
    }
}
