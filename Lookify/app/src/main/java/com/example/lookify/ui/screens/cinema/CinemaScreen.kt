package com.example.lookify.ui.screens.cinema

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.lookify.data.database.Cinema
import com.example.lookify.data.database.Users
import com.example.lookify.ui.LookifyState
import com.example.lookify.ui.composables.TitleAppBar
import com.example.lookify.ui.composables.BottomBar
import com.google.android.gms.location.LocationServices

@Composable
fun CinemaScreen(state: LookifyState, navController: NavController) {
    val context = LocalContext.current

    val currentUserId = rememberSaveable { state.currentUserId!! }
    val currentUser = currentUserId?.let { id -> state.users.find { it.id_user == id } }

    var userCity by remember { mutableStateOf<String?>(null) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                        location?.let {
                            Log.d("CinemaScreen", "Lat: ${it.latitude}, Lng: ${it.longitude}")
                            val closestCity = findClosestCity(it.latitude, it.longitude)
                            Log.d("CinemaScreen", "Closest city: $closestCity")
                            userCity = closestCity ?: currentUser?.residenza
                        } ?: run {
                            userCity = currentUser?.residenza
                        }
                    }
                } else {
                    userCity = currentUser?.residenza
                }
            } else {
                userCity = currentUser?.residenza
            }
        }
    )

    LaunchedEffect(Unit) {
        if (userCity == null) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    val nearbyCinemas = remember(userCity, state.cinemas) {
        userCity?.let { city ->
            getCinemasByResidenceOrRegion(city, state.cinemas)
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
                .padding(16.dp)
        ) {
            Text(
                text = "Cinema Nella Tua Zona",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (!userCity.isNullOrEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Icon(
                        Icons.Outlined.LocationOn,
                        contentDescription = "Posizione",
                        tint = Color.Red,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Vicino a $userCity",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }

            if (nearbyCinemas.isEmpty()) {
                NoCinemaPlaceholder(userCity.isNullOrEmpty())
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(nearbyCinemas) { cinema ->
                        CinemaCard(
                            cinema = cinema,
                            onClick = { openMapsForCinema(context, cinema) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CinemaCard(cinema: Cinema, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.size(80.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.Map,
                        contentDescription = "Apri Mappa",
                        tint = Color.Red,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = cinema.nome,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = cinema.indirizzo,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = cinema.provincia,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Red,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun NoCinemaPlaceholder(noResidence: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Icon(
            if (noResidence) Icons.Outlined.LocationOn else Icons.Outlined.Map,
            contentDescription = "Nessun Cinema",
            modifier = Modifier.size(64.dp),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (noResidence) {
                "Imposta la tua residenza per vedere i cinema vicini"
            } else {
                "Nessun cinema trovato nella tua zona"
            },
            style = MaterialTheme.typography.titleMedium,
            color = Color.Gray,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

// Funzioni helper

fun getCurrentUser(state: LookifyState): Users? {
    return state.currentUserId?.let { userId ->
        state.users.find { it.id_user == userId }
    }
}

// Mappa città -> coordinate
val cityCoordinates = mapOf(
    "cesena" to Pair(45.0703, 7.6869),
    "bologna" to Pair(44.4949, 11.3426),
    "rimini" to Pair(44.0678, 12.5695),
    "forli" to Pair(44.2225, 12.0407),
    "modena" to Pair(44.6471, 10.9252),
    "parma" to Pair(44.8015, 10.3279),
    "ferrara" to Pair(44.8354, 11.6198),
    "ravenna" to Pair(44.4184, 12.2035),
    "milano" to Pair(45.4642, 9.1900),
    "torino" to Pair(44.1403, 12.2432),
    "roma" to Pair(41.9028, 12.4964),
    "napoli" to Pair(40.8522, 14.2681),
    "palermo" to Pair(38.1157, 13.3615),
    "catania" to Pair(37.5079, 15.0830),
    "genova" to Pair(44.4056, 8.9463),
    "bari" to Pair(41.1173, 16.8719),
    "firenze" to Pair(43.7696, 11.2558),
    "venezia" to Pair(45.4408, 12.3155),
    "verona" to Pair(45.4384, 10.9916),
    "trieste" to Pair(45.6495, 13.7768)
)

// Haversine formula per distanza km
fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val R = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
            Math.sin(dLon / 2) * Math.sin(dLon / 2)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    return R * c
}

// Trova la città più vicina dato lat/lng
fun findClosestCity(lat: Double, lon: Double): String? {
    return cityCoordinates.minByOrNull { (_, coords) ->
        haversine(lat, lon, coords.first, coords.second)
    }?.key
}

// Mappa provincia -> regione
val provinceToRegion = mapOf(
    "cesena" to "Emilia-Romagna",
    "bologna" to "Emilia-Romagna",
    "rimini" to "Emilia-Romagna",
    "forli" to "Emilia-Romagna",
    "modena" to "Emilia-Romagna",
    "parma" to "Emilia-Romagna",
    "ferrara" to "Emilia-Romagna",
    "ravenna" to "Emilia-Romagna",
    "milano" to "Lombardia",
    "torino" to "Piemonte",
    "roma" to "Lazio",
    "napoli" to "Campania",
    "palermo" to "Sicilia",
    "catania" to "Sicilia",
    "genova" to "Liguria",
    "bari" to "Puglia",
    "firenze" to "Toscana",
    "venezia" to "Veneto",
    "verona" to "Veneto",
    "trieste" to "Friuli-Venezia Giulia"
)

// Filtra cinema per residenza o regione
fun getCinemasByResidenceOrRegion(residenza: String, allCinemas: List<Cinema>): List<Cinema> {
    val normalized = residenza.lowercase().trim()
    val userRegion = provinceToRegion[normalized] ?: return emptyList()
    return allCinemas.filter { cinema ->
        val cinemaProvince = cinema.provincia.lowercase().trim()
        provinceToRegion[cinemaProvince] == userRegion
    }.take(5)
}

fun openMapsForCinema(context: android.content.Context, cinema: Cinema) {
    val query = "${cinema.nome}, ${cinema.indirizzo}, ${cinema.provincia}"
    val gmmIntentUri =
        Uri.parse("https://www.google.com/maps/search/?api=1&query=${Uri.encode(query)}")
    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
    context.startActivity(mapIntent)
}
