package com.example.lookify.ui

import TrophyChecker
import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lookify.data.database.*
import com.example.lookify.data.repositories.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.get
import java.io.File

// Data classes per gli stati
data class LookifyState(
    val films: List<Film> = emptyList(),
    val watchedFilms: List<FilmWatched> = emptyList(),
    val filmRequests: List<FilmRequest> = emptyList(),
    val series: List<SerieTV> = emptyList(),
    val watchedSeries: List<SerieTV_Watched> = emptyList(),
    val seriesRequests: List<SerieTV_Request> = emptyList(),
    val users: List<Users> = emptyList(),
    val trophies: List<Trophy> = emptyList(),
    val achievements: List<Achievements> = emptyList(),
    val notifications: List<Notify> = emptyList(),
    val reachedNotifications: List<Reached_Notify> = emptyList(),
    val followers: List<Followers> = emptyList(),
    val cinemas: List<Cinema> = emptyList(),
    var currentUserId: Int? = null,
    val nearbyCinemas: List<CinemaVicini> = emptyList(),
    val actors: List<Actors> = emptyList(),
    val actorsInFilms: List<Actors_In_Film> = emptyList(),
    val platforms: List<Platform> = emptyList(),
    val filmPlatforms: List<Film_Platform> = emptyList(),
    val actorsInSerie: List<Actors_In_Serie> = emptyList(),
    val seriePlatform: List<Serie_Platform> = emptyList()
)

class LookifyViewModel(
    private val filmsRepository: FilmsRepository,
    private val filmWatchedRepository: FilmWatchedRepository,
    private val filmRequestRepository: FilmRequestRepository,
    private val usersRepository: UsersRepository,
    private val serieTVRepository: SerieTVRepository,
    private val serieTVWatchedRepository: SerieTVWatchedRepository,
    private val serieTVRequestRepository: SerieTVRequestRepository,
    private val trophyRepository: TrophyRepository,
    private val achievementsRepository: AchievementsRepository,
    private val notifyRepository: NotifyRepository,
    private val notifyReachedRepository: NotifyReachedRepository,
    private val followersRepository: FollowersRepository,
    private val cinemaRepository: CinemaRepository,
    private val cinemaNearRepository: CinemaNearRepository,
    private val actorsRepository: ActorsRepository,
    private val actorsInFilmRepository: ActorsInFilmRepository,
    private val platformRepository: PlatformRepository,
    private val filmPlatformRepository: FilmPlatformRepository,
    private val seriePlatformRepository: SeriePlatformRepository,
    private val actorsInSerieRepository: ActorsInSerieRepository
) : ViewModel() {

    // Stato combinato dell'app
    val state = combine(
        filmsRepository.allFilms,
        filmWatchedRepository.allFilmWatched,
        filmRequestRepository.allFilmRequests,
        usersRepository.allUsers,
        serieTVRepository.allSerieTV,
        serieTVWatchedRepository.allSerieTVWatched,
        serieTVRequestRepository.allSerieRequests,
        trophyRepository.allTrophy,
        achievementsRepository.allAchievements,
        notifyRepository.allNotify,
        notifyReachedRepository.allNotifyReached,
        followersRepository.allFollowers,
        cinemaRepository.allCinema,
        cinemaNearRepository.allCinemaNear,
        actorsRepository.allActors,
        actorsInFilmRepository.allActorsInFilm,
        platformRepository.allPlatforms,
        filmPlatformRepository.allPlatformFilm,
        seriePlatformRepository.allPlatformSerie,
        actorsInSerieRepository.allActorsInSerie
    ) { values ->
        val films = values[0] as List<Film>
        val watchedFilms = values[1] as List<FilmWatched>
        val filmRequests = values[2] as List<FilmRequest>
        val users = values[3] as List<Users>
        val series = values[4] as List<SerieTV>
        val watchedSeries = values[5] as List<SerieTV_Watched>
        val seriesRequests = values[6] as List<SerieTV_Request>
        val trophies = values[7] as List<Trophy>
        val achievements = values[8] as List<Achievements>
        val notifications = values[9] as List<Notify>
        val reachedNotifications = values[10] as List<Reached_Notify>
        val followers = values[11] as List<Followers>
        val cinemas = values[12] as List<Cinema>
        val nearbyCinemas = values[13] as List<CinemaVicini>
        val actors = values[14] as List<Actors>
        val actorsInFilms = values[15] as List<Actors_In_Film>
        val platforms = values[16] as List<Platform>
        val filmPlatforms = values[17] as List<Film_Platform>

        LookifyState(
            films = films,
            watchedFilms = watchedFilms,
            filmRequests = filmRequests,
            users = users,
            series = series,
            watchedSeries = watchedSeries,
            seriesRequests = seriesRequests,
            trophies = trophies,
            achievements = achievements,
            notifications = notifications,
            reachedNotifications = reachedNotifications,
            followers = followers,
            cinemas = cinemas,
            nearbyCinemas = nearbyCinemas,
            actors = actors,
            actorsInFilms = actorsInFilms,
            platforms = platforms,
            filmPlatforms = filmPlatforms
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LookifyState()
    )

    private val trophyChecker = TrophyChecker()

    // ✅ TRIGGER 1: Quando l'utente guarda un film
    fun markFilmAsWatched(userId: Int, filmId: Int) {
        viewModelScope.launch {
            // Logica esistente per segnare come visto
            filmWatchedRepository.insert(
                FilmWatched(utenteId = userId, filmId = filmId)
            )

            // 🎯 TRIGGER: Controlla trofei dopo aver visto il film
            checkAndUnlockTrophies(userId)
        }
    }

    // ✅ TRIGGER 2: Quando l'utente guarda una serie
    fun markSeriesAsWatched(userId: Int, seriesId: Int) {
        viewModelScope.launch {
            // Logica esistente
            serieTVWatchedRepository.insert(
                SerieTV_Watched(id_user = userId, serieId = seriesId)
            )

            // 🎯 TRIGGER: Controlla trofei
            checkAndUnlockTrophies(userId)
        }
    }

    // ✅ TRIGGER 3: Quando ottieni un nuovo follower
    fun addFollower(followerId: Int, followedId: Int) {
        viewModelScope.launch {
            // Logica esistente
            followersRepository.insert(
                Followers(seguaceId = followerId, seguitoId = followedId)
            )

            // 🎯 TRIGGER: Controlla trofei per l'utente seguito
            checkAndUnlockTrophies(followedId.toInt())
        }
    }

    // ✅ TRIGGER 4: All'avvio dell'app/login
    fun onUserLogin(userId: Int) {
        viewModelScope.launch {
            // 🎯 TRIGGER: Controlla trofei all'avvio
            checkAndUnlockTrophies(userId)
        }
    }

    // Funzione principale che fa il controllo
    private suspend fun checkAndUnlockTrophies(userId: Int) {
        // Ottieni lo stato aggiornato
        val currentState = state.value

        // Controlla nuovi trofei
        val newTrophies = trophyChecker.checkTrophies(userId, currentState)

        if (newTrophies.isNotEmpty()) {
            // Sblocca i trofei nel database
            achievementsRepository.unlockTrophies(userId, newTrophies)

            // Mostra notifica (opzionale)
            showTrophyNotifications(newTrophies, currentState)
        }
    }

    private fun showTrophyNotifications(trophyIds: List<Int>, state: LookifyState) {
        trophyIds.forEach { id ->
            val trophy = state.trophies.find { it.id == id }
            trophy?.let {
                // TODO: Implementa toast/snackbar
                println("🏆 Nuovo trofeo sbloccato: ${it.nome}!")
            }
        }
    }

    fun insertFilm(
        context: Context,
        film: Film,
        platformName: String,
        actors: List<Actors>,
        imageUri: String?,
        imageName: String?
    ) {
        viewModelScope.launch {
            try {
                val finalImagePath = imageUri?.let { uri ->
                    imageName?.let { name ->
                        copyImageToInternalStorage(context, uri, name)
                    }
                }

                val updatedFilm = film.copy(imageUri = finalImagePath)
                filmsRepository.insert(updatedFilm)

                val filmId = filmsRepository.allFilms.first().last().id_film

                // Piattaforma
                val platform = platformRepository.allPlatforms
                    .first()
                    .find { it.nome.equals(platformName, ignoreCase = true) }
                    ?: Platform(nome = platformName).also { platformRepository.upsert(it) }

                filmPlatformRepository.upsert(
                    Film_Platform(
                        filmId = filmId,
                        piattaformaId = platform.id
                    )
                )

                // Attori
                actors.forEach { actor ->
                    actorsRepository.upsert(actor)
                    val actorId = actorsRepository.allActors.first()
                        .last { it.nome == actor.nome && it.cognome == actor.cognome }
                        .id
                    actorsInFilmRepository.upsert(
                        Actors_In_Film(
                            filmId = filmId,
                            attoreId = actorId
                        )
                    )
                }

            } catch (e: Exception) {
                Log.e("InsertFilm", "Errore nell'inserimento del film", e)
            }
        }
    }

    fun insertSerie(
        context: Context,
        serie: SerieTV,
        platformName: String,
        actors: List<Actors>,
        imageUri: String?,
        imageName: String?
    ) {
        viewModelScope.launch {
            try {
                // 🔹 Copia immagine nella storage interna
                val finalImagePath = imageUri?.let { uri ->
                    imageName?.let { name ->
                        copyImageToInternalStorage(context, uri, name)
                    }
                }

                // 🔹 Inserimento serie con percorso immagine aggiornato
                val updatedSerie = serie.copy(imageUri = finalImagePath)
                serieTVRepository.insert(updatedSerie)

                // 🔹 Recupero id serie appena inserita
                val serieId = serieTVRepository.allSerieTV.first().last().id_serie

                // 🔹 Gestione piattaforma
                val platform = platformRepository.allPlatforms
                    .first()
                    .find { it.nome.equals(platformName, ignoreCase = true) }
                    ?: Platform(nome = platformName).also { platformRepository.upsert(it) }

                // 🔹 Collega serie alla piattaforma
                seriePlatformRepository.upsert(
                    Serie_Platform(
                        serieId = serieId,
                        piattaformaId = platform.id
                    )
                )

                // 🔹 Inserimento attori
                actors.forEach { actor ->
                    actorsRepository.upsert(actor)
                    val actorId = actorsRepository.allActors.first()
                        .last { it.nome == actor.nome && it.cognome == actor.cognome }
                        .id

                    // 🔹 Collega attore alla serie
                    actorsInSerieRepository.upsert(
                        Actors_In_Serie(
                            serieId = serieId,
                            attoreId = actorId
                        )
                    )
                }

            } catch (e: Exception) {
                Log.e("InsertSerie", "Errore nell'inserimento della serie", e)
            }
        }
    }




    private suspend fun copyImageToInternalStorage(context: Context, uriString: String, imageName: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val uri = Uri.parse(uriString)
                val inputStream = context.contentResolver.openInputStream(uri) ?: return@withContext null
                val file = File(context.filesDir, "$imageName.jpg")
                inputStream.use { input -> file.outputStream().use { output -> input.copyTo(output) } }
                file.absolutePath
            } catch (e: Exception) {
                Log.e("LookifyViewModel", "Errore nel copiare l'immagine", e)
                null
            }
        }
    }


    fun insertFilmRequest(
        context: Context,
        film: Film,
        platformName: String,
        actors: List<Actors>,
        imageUri: String?,
        imageName: String?,
        userId: Int
    ) {
        viewModelScope.launch {
            try {
                // 🔹 Copia immagine nella storage interna
                val finalImagePath = imageUri?.let { uri ->
                    imageName?.let { name ->
                        copyImageToInternalStorage(context, uri, name)
                    }
                }

                // 🔹 Inserimento film con percorso immagine aggiornato e visibile = false
                val updatedFilm = film.copy(imageUri = finalImagePath, visibile = false)
                filmsRepository.insert(updatedFilm)

                // 🔹 Recupero id film appena inserito
                val filmId = filmsRepository.allFilms.first().last().id_film

                // 🔹 Gestione piattaforma
                val platform = platformRepository.allPlatforms
                    .first()
                    .find { it.nome.equals(platformName, ignoreCase = true) }
                    ?: Platform(nome = platformName).also { platformRepository.upsert(it) }

                // 🔹 Collega film alla piattaforma
                filmPlatformRepository.upsert(
                    Film_Platform(
                        filmId = filmId,
                        piattaformaId = platform.id
                    )
                )

                // 🔹 Inserimento attori
                actors.forEach { actor ->
                    actorsRepository.upsert(actor)
                    val actorId = actorsRepository.allActors.first()
                        .last { it.nome == actor.nome && it.cognome == actor.cognome }
                        .id

                    // 🔹 Collega attore al film
                    actorsInFilmRepository.upsert(
                        Actors_In_Film(
                            filmId = filmId,
                            attoreId = actorId
                        )
                    )
                }

                // 🔹 Crea la richiesta di film
                val filmRequest = FilmRequest(
                    filmId = filmId,
                    richiedenteId = userId,
                    approvatoreId = 112,
                    approvato = false,
                )
                filmRequestRepository.insert(filmRequest)

                Log.d("InsertFilmRequest", "Richiesta film inserita con successo")

            } catch (e: Exception) {
                Log.e("InsertFilmRequest", "Errore nell'inserimento della richiesta film", e)
            }
        }
    }

    // Metodo per inserire una richiesta di serie TV
    fun insertSerieRequest(
        context: Context,
        serie: SerieTV,
        platformName: String,
        actors: List<Actors>,
        imageUri: String?,
        imageName: String?,
        userId: Int
    ) {
        viewModelScope.launch {
            try {
                // 🔹 Copia immagine nella storage interna
                val finalImagePath = imageUri?.let { uri ->
                    imageName?.let { name ->
                        copyImageToInternalStorage(context, uri, name)
                    }
                }

                // 🔹 Inserimento serie con percorso immagine aggiornato e visibile = false
                val updatedSerie = serie.copy(imageUri = finalImagePath, visibile = false)
                serieTVRepository.insert(updatedSerie)

                // 🔹 Recupero id serie appena inserita
                val serieId = serieTVRepository.allSerieTV.first().last().id_serie

                // 🔹 Gestione piattaforma
                val platform = platformRepository.allPlatforms
                    .first()
                    .find { it.nome.equals(platformName, ignoreCase = true) }
                    ?: Platform(nome = platformName).also { platformRepository.upsert(it) }

                // 🔹 Collega serie alla piattaforma
                seriePlatformRepository.upsert(
                    Serie_Platform(
                        serieId = serieId,
                        piattaformaId = platform.id
                    )
                )

                // 🔹 Inserimento attori
                actors.forEach { actor ->
                    actorsRepository.upsert(actor)
                    val actorId = actorsRepository.allActors.first()
                        .last { it.nome == actor.nome && it.cognome == actor.cognome }
                        .id

                    // 🔹 Collega attore alla serie
                    actorsInSerieRepository.upsert(
                        Actors_In_Serie(
                            serieId = serieId,
                            attoreId = actorId
                        )
                    )
                }

                // 🔹 Crea la richiesta di serie
                val serieRequest = SerieTV_Request(
                    serieId = serieId,
                    richiedenteId = userId,
                    approvatoreId = 112,
                    approvato = false,
                )
                serieTVRequestRepository.insert(serieRequest)

                Log.d("InsertSerieRequest", "Richiesta serie TV inserita con successo")

            } catch (e: Exception) {
                Log.e("InsertSerieRequest", "Errore nell'inserimento della richiesta serie", e)
            }
        }
    }

    fun approveFilmRequest(requestId: Int) {
        viewModelScope.launch {
            try {
                // Ottieni la richiesta per trovare il film
                val requests = filmRequestRepository.allFilmRequests.first()
                val request = requests.find { it.id_request == requestId }

                if (request != null) {
                    // Approva la richiesta
                    filmRequestRepository.updateApprovalStatus(requestId, true)

                    // Rendi il film visibile
                    val films = filmsRepository.allFilms.first()
                    val film = films.find { it.id_film == request.filmId }

                    if (film != null) {
                        val updatedFilm = film.copy(visibile = true)
                        filmsRepository.upsert(updatedFilm)
                    }
                }

                Log.d("ApproveFilmRequest", "Richiesta film approvata con successo")
            } catch (e: Exception) {
                Log.e("ApproveFilmRequest", "Errore nell'approvazione della richiesta film", e)
            }
        }
    }

    fun approveSerieRequest(requestId: Int) {
        viewModelScope.launch {
            try {
                // Ottieni la richiesta per trovare la serie
                val requests = serieTVRequestRepository.allSerieRequests.first()
                val request = requests.find { it.id_request == requestId }

                if (request != null) {
                    // Approva la richiesta
                    serieTVRequestRepository.updateApprovalStatus(requestId, true)

                    // Rendi la serie visibile
                    val series = serieTVRepository.allSerieTV.first()
                    val serie = series.find { it.id_serie == request.serieId }

                    if (serie != null) {
                        val updatedSerie = serie.copy(visibile = true)
                        serieTVRepository.upsert(updatedSerie)
                    }
                }

                Log.d("ApproveSerieRequest", "Richiesta serie TV approvata con successo")
            } catch (e: Exception) {
                Log.e("ApproveSerieRequest", "Errore nell'approvazione della richiesta serie", e)
            }
        }
    }

    fun getFilmRequestsByStatus(approved: Boolean): Flow<List<FilmRequest>> {
        return filmRequestRepository.getFilmRequestsByStatus(approved)
    }

    fun getSerieRequestsByStatus(approved: Boolean): Flow<List<SerieTV_Request>> {
        return serieTVRequestRepository.getSerieRequestsByStatus(approved)
    }

    fun getFilmRequestsByUser(userId: Int): Flow<List<FilmRequest>> {
        return filmRequestRepository.getFilmRequestsByUser(userId)
    }

    fun getSerieRequestsByUser(userId: Int): Flow<List<SerieTV_Request>> {
        return serieTVRequestRepository.getSerieRequestsByUser(userId)
    }


    fun getActorsByFilmId(filmId: Int): Flow<List<Actors>> {
        return actorsInFilmRepository.getByFilmId(filmId).map { actorsInFilm ->
            val allActors = actorsRepository.allActors.first()
            actorsInFilm.mapNotNull { actorInFilm ->
                allActors.find { it.id == actorInFilm.attoreId }
            }
        }
    }

    fun getActorsBySerieId(serieId: Int): Flow<List<Actors>> {
        return actorsInSerieRepository.getBySerieId(serieId).map { actorsInSerie ->
            val allActors = actorsRepository.allActors.first()
            actorsInSerie.mapNotNull { actorInSerie ->
                allActors.find { it.id == actorInSerie.attoreId }
            }
        }
    }

    fun getPlatformByFilmId(filmId: Int): Flow<Platform?> {
        return filmPlatformRepository.getByFilmId(filmId).map { filmPlatforms ->
            if (filmPlatforms.isNotEmpty()) {
                val allPlatforms = platformRepository.allPlatforms.first()
                allPlatforms.find { it.id == filmPlatforms.first().piattaformaId }
            } else null
        }
    }

    fun getPlatformBySerieId(serieId: Int): Flow<Platform?> {
        return seriePlatformRepository.getBySerieId(serieId).map { seriePlatforms ->
            if (seriePlatforms.isNotEmpty()) {
                val allPlatforms = platformRepository.allPlatforms.first()
                allPlatforms.find { it.id == seriePlatforms.first().piattaformaId }
            } else null
        }
    }

    fun rejectFilmRequest(requestId: Int) {
        viewModelScope.launch {
            try {
                // Ottieni la richiesta per trovare il film
                val requests = filmRequestRepository.allFilmRequests.first()
                val request = requests.find { it.id_request == requestId }

                if (request != null) {
                    // Elimina la richiesta
                    filmRequestRepository.delete(request)

                    // Elimina il film associato
                    val films = filmsRepository.allFilms.first()
                    val film = films.find { it.id_film == request.filmId }

                    if (film != null) {
                        // Elimina anche le associazioni attori-film
                        val actorsInFilm = actorsInFilmRepository.getByFilmId(request.filmId).first()
                        actorsInFilm.forEach { actorInFilm ->
                            actorsInFilmRepository.delete(actorInFilm)
                        }

                        // Elimina le associazioni film-piattaforma
                        val filmPlatforms = filmPlatformRepository.getByFilmId(request.filmId).first()
                        filmPlatforms.forEach { filmPlatform ->
                            filmPlatformRepository.delete(filmPlatform)
                        }

                        // Infine elimina il film
                        filmsRepository.delete(film)
                    }
                }

                Log.d("RejectFilmRequest", "Richiesta film rifiutata e contenuto eliminato")
            } catch (e: Exception) {
                Log.e("RejectFilmRequest", "Errore nel rifiuto della richiesta film", e)
            }
        }
    }

    fun rejectSerieRequest(requestId: Int) {
        viewModelScope.launch {
            try {
                // Ottieni la richiesta per trovare la serie
                val requests = serieTVRequestRepository.allSerieRequests.first()
                val request = requests.find { it.id_request == requestId }

                if (request != null) {
                    // Elimina la richiesta
                    serieTVRequestRepository.delete(request)

                    // Elimina la serie associata
                    val series = serieTVRepository.allSerieTV.first()
                    val serie = series.find { it.id_serie == request.serieId }

                    if (serie != null) {
                        // Elimina anche le associazioni attori-serie
                        val actorsInSerie = actorsInSerieRepository.getBySerieId(request.serieId).first()
                        actorsInSerie.forEach { actorInSerie ->
                            actorsInSerieRepository.delete(actorInSerie)
                        }

                        // Elimina le associazioni serie-piattaforma
                        val seriePlatforms = seriePlatformRepository.getBySerieId(request.serieId).first()
                        seriePlatforms.forEach { seriePlatform ->
                            seriePlatformRepository.delete(seriePlatform)
                        }

                        // Infine elimina la serie
                        serieTVRepository.delete(serie)
                    }
                }

                Log.d("RejectSerieRequest", "Richiesta serie TV rifiutata e contenuto eliminato")
            } catch (e: Exception) {
                Log.e("RejectSerieRequest", "Errore nel rifiuto della richiesta serie", e)
            }
        }
    }

}
