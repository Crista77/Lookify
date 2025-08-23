package com.example.lookify.ui.screens.cinema

import android.content.Intent
import android.net.Uri
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
import androidx.navigation.NavController
import com.example.lookify.data.database.Cinema
import com.example.lookify.data.database.Users
import com.example.lookify.ui.LookifyState
import com.example.lookify.ui.composables.TitleAppBar
import com.example.lookify.ui.composables.BottomBar

@Composable
fun CinemaScreen(state: LookifyState, navController: NavController) {
    val context = LocalContext.current

    val currentUserId = rememberSaveable { state.currentUserId!! }

    val currentUser = currentUserId?.let { id ->
        state.users.find { it.id_user == id }
    }

    val nearbyCinemas = remember(currentUser, state.cinemas) {
        currentUser?.residenza?.let { residenza ->
            getCinemasByResidenceOrRegion(residenza, state.cinemas)
        } ?: emptyList()
    }

    Scaffold(
        topBar = { TitleAppBar(navController) },
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

            if (currentUser?.residenza != null) {
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
                        text = "Vicino a ${currentUser.residenza}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }

            if (nearbyCinemas.isEmpty()) {
                NoCinemaPlaceholder(currentUser?.residenza == null)
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

// Mappa provincia -> regione
val provinceToRegion = mapOf(
    // Emilia-Romagna
    "bologna" to "Emilia-Romagna",
    "forlì-cesena" to "Emilia-Romagna",
    "forli" to "Emilia-Romagna",
    "cesena" to "Emilia-Romagna",
    "forlimpopoli" to "Emilia-Romagna",
    "ravenna" to "Emilia-Romagna",
    "faenza" to "Emilia-Romagna",
    "rimini" to "Emilia-Romagna",
    "modena" to "Emilia-Romagna",
    "reggio emilia" to "Emilia-Romagna",
    "parma" to "Emilia-Romagna",
    "piacenza" to "Emilia-Romagna",
    "ferrara" to "Emilia-Romagna",

    // Lombardia
    "milano" to "Lombardia",
    "bergamo" to "Lombardia",
    "brescia" to "Lombardia",
    "como" to "Lombardia",
    "cremona" to "Lombardia",
    "mantova" to "Lombardia",
    "pavia" to "Lombardia",
    "sondrio" to "Lombardia",
    "varese" to "Lombardia",
    "lecco" to "Lombardia",
    "lodi" to "Lombardia",
    "monza e brianza" to "Lombardia",

    // Veneto
    "venezia" to "Veneto",
    "verona" to "Veneto",
    "padova" to "Veneto",
    "vicenza" to "Veneto",
    "treviso" to "Veneto",
    "belluno" to "Veneto",
    "rovigo" to "Veneto",

    // Piemonte
    "torino" to "Piemonte",
    "alessandria" to "Piemonte",
    "asti" to "Piemonte",
    "biella" to "Piemonte",
    "cuneo" to "Piemonte",
    "novara" to "Piemonte",
    "verbania" to "Piemonte",
    "vercelli" to "Piemonte",

    // Liguria
    "genova" to "Liguria",
    "imperia" to "Liguria",
    "la spezia" to "Liguria",
    "savona" to "Liguria",

    // Toscana
    "firenze" to "Toscana",
    "arezzo" to "Toscana",
    "grosseto" to "Toscana",
    "livorno" to "Toscana",
    "lucca" to "Toscana",
    "massa carrara" to "Toscana",
    "pisa" to "Toscana",
    "pistoia" to "Toscana",
    "prato" to "Toscana",
    "siena" to "Toscana",

    // Lazio
    "roma" to "Lazio",
    "frosinone" to "Lazio",
    "latina" to "Lazio",
    "rieti" to "Lazio",
    "viterbo" to "Lazio",

    // Campania
    "napoli" to "Campania",
    "avellino" to "Campania",
    "benevento" to "Campania",
    "caserta" to "Campania",
    "salerno" to "Campania",

    // Sicilia
    "palermo" to "Sicilia",
    "catania" to "Sicilia",
    "messina" to "Sicilia",
    "agrigento" to "Sicilia",
    "caltanissetta" to "Sicilia",
    "enna" to "Sicilia",
    "ragusa" to "Sicilia",
    "siracusa" to "Sicilia",
    "trapani" to "Sicilia",

    // Puglia
    "bari" to "Puglia",
    "brindisi" to "Puglia",
    "foggia" to "Puglia",
    "lecce" to "Puglia",
    "taranto" to "Puglia",
    "barletta-andria-trani" to "Puglia",

    // Calabria
    "cosenza" to "Calabria",
    "catanzaro" to "Calabria",
    "reggio calabria" to "Calabria",
    "crotone" to "Calabria",
    "vibo valentia" to "Calabria",

    // Altre regioni principali
    "trieste" to "Friuli-Venezia Giulia",
    "udine" to "Friuli-Venezia Giulia",
    "pordenone" to "Friuli-Venezia Giulia",
    "gorizia" to "Friuli-Venezia Giulia",
    "trento" to "Trentino-Alto Adige",
    "bolzano" to "Trentino-Alto Adige",
    "aosta" to "Valle d'Aosta",
    "ancona" to "Marche",
    "perugia" to "Umbria",
    "terni" to "Umbria",
    "l'aquila" to "Abruzzo",
    "chieti" to "Abruzzo",
    "pescara" to "Abruzzo",
    "teramo" to "Abruzzo",
    "cagliari" to "Sardegna",
    "nuoro" to "Sardegna",
    "oristano" to "Sardegna",
    "sassari" to "Sardegna",
    "carbonia-iglesias" to "Sardegna"
)


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
    val gmmIntentUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=${Uri.encode(query)}")
    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
    context.startActivity(mapIntent)
}