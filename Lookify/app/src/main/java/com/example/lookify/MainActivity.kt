package com.example.lookify

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.lookify.data.database.Achievements
import com.example.lookify.data.database.Cinema
import com.example.lookify.data.database.Film
import com.example.lookify.data.database.FilmWatched
import com.example.lookify.data.database.Followers
import com.example.lookify.data.database.Platform
import com.example.lookify.data.database.SerieTV
import com.example.lookify.data.database.SerieTV_Watched
import com.example.lookify.data.database.Trophy
import com.example.lookify.data.database.Users
import com.example.lookify.data.repositories.*
import com.example.lookify.ui.LookifyNavGraph
import com.example.lookify.ui.LookifyState
import com.example.lookify.ui.screens.cinema.getCurrentUser
import com.example.lookify.ui.theme.TravelDiaryTheme
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val userRepo: UsersRepository = get()

        lifecycleScope.launch {
            val usersRepo: UsersRepository = get()
            val film3 = Users(
                nome = "Admin",
                cognome = "Admin",
                username = "admin",
                password = "tac",
                residenza = "Forlimpopoli",
                admin = true
            )
            userRepo.insert(film3)
            //usersRepo.deleteAll()
            val film1 = Users(
                id_user = 110,
                nome = "Simone",
                cognome = "Cristarelli",
                username = "simo.cris",
                password = "Simo004",
                residenza = "Forlimpopoli",
                admin = false
            )
            usersRepo.insert(film1)
            val film2 = Users(
                id_user = 111,
                nome = "Simone",
                cognome = "Cristarelli",
                username = "simon.cris",
                password = "Simo004",
                residenza = "Forlimpopoli",
                admin = false
            )
            usersRepo.insert(film2)

            val filmRepo: FilmsRepository = get()
            val film = Film(
                id_film = 1,
                titolo = "Non Ci Resta Che Piangere",
                numero_Cast = 2,
                descrizione = "Film incredibile di Massimo Troisi",
                durata = 100,
                categoria = "Comico",
                visibile = true,
                visualizzazioni = 1,
                imageUri = "android.resource://com.example.lookify/${R.drawable.non_ci_resta_che_piangere}"
            )
            filmRepo.insert(film)

            val serieRepo: SerieTVRepository = get()
            val serie = SerieTV(
                id_serie = 1,
                titolo = "Mercoledì",
                numero_Cast = 8,
                descrizione = "Serie di tot episodi su Mercoledì Addams",
                durata = 1200,
                visibile = true,
                categoria = "Fantasy",
                visualizzazioni = 1,
                imageUri = "android.resource://com.example.lookify/${R.drawable.mercoledi}"
            )

            serieRepo.insert(serie)

            val platRepo: PlatformRepository = get()
            val netflix = Platform(
                nome = "Netflix"
            )
            platRepo.insert(netflix)

            val primeVideo = Platform(
                nome = "Amazon Prime Video"
            )
            platRepo.insert(primeVideo)

            val disneyPlus = Platform(
                nome = "Disney+"
            )
            platRepo.insert(disneyPlus)

            val hboMax = Platform(
                nome = "HBO Max"
            )
            platRepo.insert(hboMax)

            val appleTv = Platform(
                nome = "Apple TV+"
            )
            platRepo.insert(appleTv)

            val paramount = Platform(
                nome = "Paramount+"
            )
            platRepo.insert(paramount)

            val discovery = Platform(
                nome = "Discovery+"
            )
            platRepo.insert(discovery)


            val filmWRepo: FilmWatchedRepository = get()
            val fw = FilmWatched(
                utenteId = 110,
                filmId = 1
            )
            filmWRepo.insert(fw)

            val serieWRepo: SerieTVWatchedRepository = get()
            val sw = SerieTV_Watched(
                id_user = 110,
                serieId = 1
            )
            serieWRepo.insert(sw)

            val followerRepo: FollowersRepository = get()
            val follower = Followers(
                seguaceId = 111,
                seguitoId = 110
            )

            followerRepo.insert(follower)


            val trofei: TrophyRepository = get()
            trofei.deleteAll()
            val trophies1 = Trophy(nome = "Primo Film", id = 1)
            val trophies2  =  Trophy(nome = "Cinefilo", id = 2)
            val trophies3  =  Trophy(nome = "Maratoneta", id = 3)
            val trophies4  =  Trophy(nome = "Esploratore", id = 4)
            val trophies5  = Trophy(nome = "Fedele Spettatore", id = 5)
            val trophies6 =   Trophy(nome = "Critico", id = 6)
            val trophies7 =  Trophy(nome = "Nottambulo", id = 7)
            val trophies8 =   Trophy(nome = "Weekend Warrior", id = 8)
            val trophies9 =  Trophy(nome = "Collezionista", id = 9)
            val trophies10  =  Trophy(nome = "Sociale", id = 10)

            val trophies = listOf(
                trophies10, trophies4, trophies5, trophies2, trophies3,
                trophies6, trophies1, trophies9, trophies7, trophies8
            )

            trophies.forEach { trofeo ->
                trofei.insert(trofeo)
            }

            val achievementsRepo: AchievementsRepository = get()
            achievementsRepo.deleteAll()
// Prendiamo alcuni trofei già creati prima (ad es. i primi 3)
            val achievements = listOf(
                Achievements(id_user = 110 , trofeoId = 1), // Primo Film
                Achievements(id_user = 110, trofeoId = 2), // Cinefilo
                Achievements(id_user = 110, trofeoId = 3),  // Fedele Spettatore
                Achievements(id_user = 110, trofeoId = 4),
                Achievements(id_user = 110, trofeoId = 5),
                Achievements(id_user = 110, trofeoId = 6),
                Achievements(id_user = 110, trofeoId = 7),
                Achievements(id_user = 110, trofeoId = 8),
                Achievements(id_user = 110, trofeoId = 9),
                Achievements(id_user = 110, trofeoId = 10),
                Achievements(id_user = 111, trofeoId = 1)
            )

// Inserisco solo questi (utente 1 ha sbloccato 3 achievements)
            achievements.forEach { achievement ->
                achievementsRepo.insertAchievement(achievement)
            }


            val repository: CinemaRepository = get() // Assumi che hai un CinemaRepository
            repository.deleteAll()

            // EMILIA-ROMAGNA
            val cinema1 = Cinema(nome = "Multiplex Cineflash", indirizzo = "Via Emilia per Forlì, 160", provincia = "Forlì-Cesena")
            val cinema2 = Cinema(nome = "Cinema Saffi", indirizzo = "Viale Risorgimento, 180", provincia = "Forlì-Cesena")
            val cinema3 = Cinema(nome = "Cinema Eliseo Multisala", indirizzo = "Vico Gesù Crocifisso, 7", provincia = "Forlì-Cesena")
            val cinema4 = Cinema(nome = "Cinema Multisala Astoria", indirizzo = "Viale Risorgimento, 315", provincia = "Forlì-Cesena")
            val cinema5 = Cinema(nome = "UCI Castel Maggiore", indirizzo = "Via Villanova, 1", provincia = "Bologna")
            val cinema6 = Cinema(nome = "The Space Cinema Bologna", indirizzo = "Via Sebastiano Serlio, 25/2", provincia = "Bologna")
            val cinema7 = Cinema(nome = "Cinema Lumière", indirizzo = "Via Azzurrite, 33", provincia = "Bologna")
            val cinema8 = Cinema(nome = "Cinema Arlecchino", indirizzo = "Via Lame, 57", provincia = "Bologna")
            val cinema9 = Cinema(nome = "Cinema Adriano", indirizzo = "Via San Felice, 52", provincia = "Bologna")
            val cinema10 = Cinema(nome = "Multiplex Savoia", indirizzo = "Via del Savoia, 17a", provincia = "Bologna")

            // LOMBARDIA
            val cinema11 = Cinema(nome = "UCI Cinemas Bicocca", indirizzo = "Viale Sarca, 336", provincia = "Milano")
            val cinema12 = Cinema(nome = "The Space Cinema Odeon", indirizzo = "Via Santa Radegonda, 8", provincia = "Milano")
            val cinema13 = Cinema(nome = "Anteo Palazzo del Cinema", indirizzo = "Via Milazzo, 9", provincia = "Milano")
            val cinema14 = Cinema(nome = "Cinema Beltrade", indirizzo = "Via Filodrammatici, 2", provincia = "Milano")
            val cinema15 = Cinema(nome = "Multiplex Gloria", indirizzo = "Corso Buenos Aires, 83", provincia = "Milano")
            val cinema16 = Cinema(nome = "UCI Orio", indirizzo = "Via Portico, 71", provincia = "Bergamo")
            val cinema17 = Cinema(nome = "Nuovo Eden", indirizzo = "Via Bartolomeo Colleoni, 17", provincia = "Bergamo")
            val cinema18 = Cinema(nome = "The Space Cinema Brescia", indirizzo = "Via Orzinuovi, 1", provincia = "Brescia")
            val cinema19 = Cinema(nome = "Multiplex Oz", indirizzo = "Via Fratelli Bandiera, 2", provincia = "Brescia")
            val cinema20 = Cinema(nome = "Cinema Como", indirizzo = "Via Mentana, 19", provincia = "Como")

            // VENETO
            val cinema21 = Cinema(nome = "Multisala Giorgione Movie d'Essai", indirizzo = "Via Giorgione, 10", provincia = "Venezia")
            val cinema22 = Cinema(nome = "UCI Luxe Marcon", indirizzo = "Via Mattei, 12", provincia = "Venezia")
            val cinema23 = Cinema(nome = "The Space Cinema Torri Bianche", indirizzo = "Via Torri Bianche, 16", provincia = "Venezia")
            val cinema24 = Cinema(nome = "AMC Multicine", indirizzo = "Via Gatta, 20", provincia = "Verona")
            val cinema25 = Cinema(nome = "UCI Verona", indirizzo = "Via Unità d'Italia, 23", provincia = "Verona")
            val cinema26 = Cinema(nome = "The Space Cinema Padova", indirizzo = "Via Gattamelata, 5", provincia = "Padova")
            val cinema27 = Cinema(nome = "Multiplex Pio X", indirizzo = "Via Santa Lucia, 34", provincia = "Padova")
            val cinema28 = Cinema(nome = "UCI Vicenza", indirizzo = "Strada Statale Pasubio, 154", provincia = "Vicenza")
            val cinema29 = Cinema(nome = "Multisala Treviglio", indirizzo = "Via Roma, 45", provincia = "Treviso")
            val cinema30 = Cinema(nome = "Cinema Dolomitico", indirizzo = "Via Mezzaterra, 2", provincia = "Belluno")

            // PIEMONTE
            val cinema31 = Cinema(nome = "The Space Cinema Torino", indirizzo = "Via Nizza, 262", provincia = "Torino")
            val cinema32 = Cinema(nome = "UCI Cinemas Lingotto", indirizzo = "Via Nizza, 230", provincia = "Torino")
            val cinema33 = Cinema(nome = "Cinema Massimo", indirizzo = "Via Verdi, 18", provincia = "Torino")
            val cinema34 = Cinema(nome = "Multisala Eliseo", indirizzo = "Via Po, 21", provincia = "Torino")
            val cinema35 = Cinema(nome = "Cinema Romano", indirizzo = "Galleria Subalpina", provincia = "Torino")
            val cinema36 = Cinema(nome = "Multisala Alessandria", indirizzo = "Via Cavour, 15", provincia = "Alessandria")
            val cinema37 = Cinema(nome = "Cinema Asti", indirizzo = "Via Aliberti, 24", provincia = "Asti")
            val cinema38 = Cinema(nome = "Multisala Cuneo", indirizzo = "Via Roma, 8", provincia = "Cuneo")

            // LIGURIA
            val cinema39 = Cinema(nome = "The Space Cinema Genova", indirizzo = "Via del Campo, 14", provincia = "Genova")
            val cinema40 = Cinema(nome = "UCI Fiumara", indirizzo = "Via Fiumara, 15", provincia = "Genova")
            val cinema41 = Cinema(nome = "Cinema Sivori", indirizzo = "Via Bartolomeo Bosco, 52", provincia = "Genova")
            val cinema42 = Cinema(nome = "Multisala America", indirizzo = "Galleria Mazzini, 2", provincia = "Genova")
            val cinema43 = Cinema(nome = "Cinema Imperia", indirizzo = "Via Matteotti, 143", provincia = "Imperia")
            val cinema44 = Cinema(nome = "Cinema La Spezia", indirizzo = "Via del Prione, 15", provincia = "La Spezia")

            // TOSCANA
            val cinema45 = Cinema(nome = "UCI Firenze", indirizzo = "Via di Novoli, 2", provincia = "Firenze")
            val cinema46 = Cinema(nome = "The Space Cinema Firenze", indirizzo = "Via Generale dalla Chiesa, 3", provincia = "Firenze")
            val cinema47 = Cinema(nome = "Cinema Odeon", indirizzo = "Via dei Sassetti, 1", provincia = "Firenze")
            val cinema48 = Cinema(nome = "Multisala Pisa", indirizzo = "Borgo Stretto, 35", provincia = "Pisa")
            val cinema49 = Cinema(nome = "Cinema Livorno", indirizzo = "Via Grande, 178", provincia = "Livorno")
            val cinema50 = Cinema(nome = "Multisala Siena", indirizzo = "Via di Città, 5", provincia = "Siena")

            // LAZIO
            val cinema51 = Cinema(nome = "Warner Village Parco de' Medici", indirizzo = "Viale dei Romagnoli, 717", provincia = "Roma")
            val cinema52 = Cinema(nome = "The Space Cinema Moderno", indirizzo = "Piazza della Repubblica, 45", provincia = "Roma")
            val cinema53 = Cinema(nome = "Cinema Barberini", indirizzo = "Piazza Barberini, 24", provincia = "Roma")
            val cinema54 = Cinema(nome = "Multisala Adriano", indirizzo = "Piazza Cavour, 51", provincia = "Roma")
            val cinema55 = Cinema(nome = "UCI Porta di Roma", indirizzo = "Via Alberto Lionello, 201", provincia = "Roma")
            val cinema56 = Cinema(nome = "Cinema Frosinone", indirizzo = "Corso della Repubblica, 15", provincia = "Frosinone")
            val cinema57 = Cinema(nome = "Multisala Latina", indirizzo = "Corso Matteotti, 37", provincia = "Latina")

            // CAMPANIA
            val cinema58 = Cinema(nome = "UCI Casoria", indirizzo = "Via Atellana, 3", provincia = "Napoli")
            val cinema59 = Cinema(nome = "The Space Cinema Napoli", indirizzo = "Via Chiaia, 149", provincia = "Napoli")
            val cinema60 = Cinema(nome = "Multisala Duel Village", indirizzo = "Via Benedetto Croce, 19", provincia = "Napoli")
            val cinema61 = Cinema(nome = "Cinema Metropolitan", indirizzo = "Via Chiaia, 149", provincia = "Napoli")
            val cinema62 = Cinema(nome = "Multisala Salerno", indirizzo = "Corso Vittorio Emanuele, 56", provincia = "Salerno")
            val cinema63 = Cinema(nome = "Cinema Caserta", indirizzo = "Corso Trieste, 98", provincia = "Caserta")

            // SICILIA
            val cinema64 = Cinema(nome = "UCI Palermo", indirizzo = "Viale Regione Siciliana, 2991", provincia = "Palermo")
            val cinema65 = Cinema(nome = "The Space Cinema Palermo", indirizzo = "Via Mariano Stabile, 233", provincia = "Palermo")
            val cinema66 = Cinema(nome = "Multisala Catania", indirizzo = "Via Etnea, 229", provincia = "Catania")
            val cinema67 = Cinema(nome = "UCI Catania", indirizzo = "Via Giuseppe Verga, 34", provincia = "Catania")
            val cinema68 = Cinema(nome = "Cinema Messina", indirizzo = "Via Garibaldi, 95", provincia = "Messina")
            val cinema69 = Cinema(nome = "Multisala Agrigento", indirizzo = "Via Atenea, 315", provincia = "Agrigento")

            // PUGLIA
            val cinema70 = Cinema(nome = "UCI Bari", indirizzo = "Via Amendola, 168", provincia = "Bari")
            val cinema71 = Cinema(nome = "The Space Cinema Bari", indirizzo = "Strada Statale 16, km 792", provincia = "Bari")
            val cinema72 = Cinema(nome = "Multisala Lecce", indirizzo = "Via Trinchese, 37", provincia = "Lecce")
            val cinema73 = Cinema(nome = "Cinema Taranto", indirizzo = "Corso Umberto I, 8", provincia = "Taranto")
            val cinema74 = Cinema(nome = "Multisala Brindisi", indirizzo = "Corso Garibaldi, 1", provincia = "Brindisi")
            val cinema75 = Cinema(nome = "Cinema Foggia", indirizzo = "Via Arpi, 155", provincia = "Foggia")

            val cinemas = listOf(
                cinema1, cinema2, cinema3, cinema4, cinema5, cinema6, cinema7, cinema8, cinema9, cinema10,
                cinema11, cinema12, cinema13, cinema14, cinema15, cinema16, cinema17, cinema18, cinema19, cinema20,
                cinema21, cinema22, cinema23, cinema24, cinema25, cinema26, cinema27, cinema28, cinema29, cinema30,
                cinema31, cinema32, cinema33, cinema34, cinema35, cinema36, cinema37, cinema38, cinema39, cinema40,
                cinema41, cinema42, cinema43, cinema44, cinema45, cinema46, cinema47, cinema48, cinema49, cinema50,
                cinema51, cinema52, cinema53, cinema54, cinema55, cinema56, cinema57, cinema58, cinema59, cinema60,
                cinema61, cinema62, cinema63, cinema64, cinema65, cinema66, cinema67, cinema68, cinema69, cinema70,
                cinema71, cinema72, cinema73, cinema74, cinema75
            )

            cinemas.forEach { cinema ->
                repository.insert(cinema)
            }
        }

        setContent {
            TravelDiaryTheme {
                val navController = rememberNavController()
                LookifyNavGraph(navController, userRepo)
            }
        }
    }

}