package com.example.lookify.ui

import TrophyChecker
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lookify.data.database.*
import com.example.lookify.data.repositories.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    fun markFilmAsWatched(context: Context, userId: Int, filmId: Int) {
        viewModelScope.launch {
            // Logica esistente per segnare come visto
            filmWatchedRepository.insert(
                FilmWatched(utenteId = userId, filmId = filmId)
            )

            checkAndUnlockTrophies(context, userId)
        }
    }

    fun markSeriesAsWatched(context: Context, userId: Int, seriesId: Int) {
        viewModelScope.launch {
            // Logica esistente
            serieTVWatchedRepository.insert(
                SerieTV_Watched(id_user = userId, serieId = seriesId)
            )

            checkAndUnlockTrophies(context, userId)
        }
    }

    fun findUserById(userId: Int): Users? {
        return state.value.users.find { it.id_user == userId }
    }

    fun addFollower(context: Context, followerId: Int, followedId: Int, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val newFollower = Followers(seguaceId = followerId, seguitoId = followedId)

                followersRepository.insert(newFollower)

                checkAndUnlockTrophies(context, followedId)
                onComplete()
                createNotification(
                    context,
                    userId = followedId,
                    title = "Hai un nuovo follower!",
                    message = "L'utente ${findUserById(followerId)?.username} ti ha iniziato a seguire!"
                )

            } catch (e: Exception) {
                Log.e("AddFollower", "Errore nell'aggiunta del follower", e)
                onComplete()
            }
        }
    }

    fun unfollowUser(followerId: Int, followedId: Int, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                followersRepository.deleteFollower(followerId, followedId)
                onComplete()
            } catch (e: Exception) {
                Log.e("UnfollowUser", "Errore nell'unfollow dell'utente", e)
                onComplete()
            }
        }
    }

    fun addWatchedFilm(context: Context, userId: Int, filmId: Int, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val user = state.value.users.find { it.id_user == userId } ?: return@launch

                if (!user.filmVisti .contains(filmId)) {
                    val updatedUser = user.copy(filmVisti = user.filmVisti + filmId)
                    usersRepository.upsert(updatedUser) // aggiorna nel DB

                    // Incrementa visualizzazioni del film
                    val film = state.value.films.find { it.id_film == filmId }
                    film?.let {
                        val filmToUpdate = film.copy(visualizzazioni = film.visualizzazioni + 1)
                        filmsRepository.upsert(filmToUpdate)
                        checkAndUnlockTrophies(context, userId)
                    }
                }
                onComplete()
            } catch (e: Exception) {
                onComplete()
            }
        }
    }

    fun removeWatchedFilm(userId: Int, filmId: Int, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val user = state.value.users.find { it.id_user == userId } ?: return@launch

                if (user.filmVisti.contains(filmId)) {
                    val updatedUser = user.copy(filmVisti = user.filmVisti - filmId)
                    usersRepository.upsert(updatedUser) // aggiorna nel DB

                    // Decrementa visualizzazioni del film (opzionale)
                    val film = state.value.films.find { it.id_film == filmId }
                    film?.let {
                        val filmToUpdate = film.copy(visualizzazioni = (film.visualizzazioni - 1).coerceAtLeast(0))
                        filmsRepository.upsert(filmToUpdate)
                    }
                }
                onComplete()
            } catch (e: Exception) {
                onComplete()
            }
        }
    }



    fun rateFilm(userId: Int, filmId: Int, stars: Int, onComplete: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val film = state.value.films.find { it.id_film == filmId }
                film?.let {
                    val filmToAdd = film
                    filmToAdd.stelle = stars
                    filmsRepository.delete(film)
                    filmsRepository.insert(filmToAdd)
                }

                withContext(Dispatchers.Main) { onComplete() }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onComplete() }
            }
        }
    }

    fun addWatchedSerie(context: Context, userId: Int, serieId: Int, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val user = state.value.users.find { it.id_user == userId } ?: return@launch

                if (!user.serieViste .contains(serieId)) {
                    val updatedUser = user.copy(serieViste = user.serieViste + serieId)
                    usersRepository.upsert(updatedUser) // aggiorna nel DB

                    // Incrementa visualizzazioni del film
                    val serie = state.value.series.find { it.id_serie == serieId }
                    serie?.let {
                        val serieToUpdate = serie.copy(visualizzazioni = serie.visualizzazioni + 1)
                        serieTVRepository.upsert(serieToUpdate)
                        checkAndUnlockTrophies(context, userId)
                    }
                }
                onComplete()
            } catch (e: Exception) {
                onComplete()
            }
        }
    }

    fun removeWatchedSerie(userId: Int, serieId: Int, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val user = state.value.users.find { it.id_user == userId } ?: return@launch

                if (user.serieViste.contains(serieId)) {
                    val updatedUser = user.copy(filmVisti = user.serieViste - serieId)
                    usersRepository.upsert(updatedUser) // aggiorna nel DB

                    // Decrementa visualizzazioni del film (opzionale)
                    val serie = state.value.series.find { it.id_serie == serieId }
                    serie?.let {
                        val serieToUpdate = serie.copy(visualizzazioni = (serie.visualizzazioni - 1).coerceAtLeast(0))
                        serieTVRepository.upsert(serieToUpdate)
                    }
                }
                onComplete()
            } catch (e: Exception) {
                onComplete()
            }
        }
    }



    fun rateSerie(userId: Int, serieId: Int, stars: Int, onComplete: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val serie = state.value.series.find { it.id_serie == serieId }
                serie?.let {
                    val serieToAdd = serie
                    serieToAdd.stelle = stars
                    serieTVRepository.delete(serie)
                    serieTVRepository.insert(serieToAdd)
                }

                withContext(Dispatchers.Main) { onComplete() }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onComplete() }
            }
        }
    }

    // Funzione principale che fa il controllo
    private suspend fun checkAndUnlockTrophies(context: Context, userId: Int) {
        val currentState = state.value

        val newTrophies = trophyChecker.checkTrophies(userId, currentState)

        if (newTrophies.isNotEmpty()) {
            achievementsRepository.unlockTrophies(userId, newTrophies)

            showTrophyNotifications(context, userId, newTrophies, currentState)
        }
    }

    private fun showTrophyNotifications(context: Context, userId: Int, trophyIds: List<Int>, state: LookifyState) {
        trophyIds.forEach { id ->
            val trophy = state.trophies.find { it.id == id }
            trophy?.let {
                createNotification(context, userId, "Trofeo Sbloccato", "Complimenti hai sbloccato il trofeo ${it.nome}!")
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
                // Copia immagine nella storage interna
                val finalImagePath = imageUri?.let { uri ->
                    imageName?.let { name ->
                        copyImageToInternalStorage(context, uri, name)
                    }
                }

                // Inserimento serie con percorso immagine aggiornato
                val updatedSerie = serie.copy(imageUri = finalImagePath)
                serieTVRepository.insert(updatedSerie)

                // Recupero id serie appena inserita
                val serieId = serieTVRepository.allSerieTV.first().last().id_serie

                val platform = platformRepository.allPlatforms
                    .first()
                    .find { it.nome.equals(platformName, ignoreCase = true) }
                    ?: Platform(nome = platformName).also { platformRepository.upsert(it) }

                seriePlatformRepository.upsert(
                    Serie_Platform(
                        serieId = serieId,
                        piattaformaId = platform.id
                    )
                )

                actors.forEach { actor ->
                    actorsRepository.upsert(actor)
                    val actorId = actorsRepository.allActors.first()
                        .last { it.nome == actor.nome && it.cognome == actor.cognome }
                        .id

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
                val finalImagePath = imageUri?.let { uri ->
                    imageName?.let { name ->
                        copyImageToInternalStorage(context, uri, name)
                    }
                }

                val updatedFilm = film.copy(imageUri = finalImagePath, visibile = false)
                filmsRepository.insert(updatedFilm)

                val filmId = filmsRepository.allFilms.first().last().id_film

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
                val finalImagePath = imageUri?.let { uri ->
                    imageName?.let { name ->
                        copyImageToInternalStorage(context, uri, name)
                    }
                }

                val updatedSerie = serie.copy(imageUri = finalImagePath, visibile = false)
                serieTVRepository.insert(updatedSerie)

                val serieId = serieTVRepository.allSerieTV.first().last().id_serie

                val platform = platformRepository.allPlatforms
                    .first()
                    .find { it.nome.equals(platformName, ignoreCase = true) }
                    ?: Platform(nome = platformName).also { platformRepository.upsert(it) }

                seriePlatformRepository.upsert(
                    Serie_Platform(
                        serieId = serieId,
                        piattaformaId = platform.id
                    )
                )

                actors.forEach { actor ->
                    actorsRepository.upsert(actor)
                    val actorId = actorsRepository.allActors.first()
                        .last { it.nome == actor.nome && it.cognome == actor.cognome }
                        .id

                    actorsInSerieRepository.upsert(
                        Actors_In_Serie(
                            serieId = serieId,
                            attoreId = actorId
                        )
                    )
                }

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

    fun approveFilmRequest(requestId: Int, context: Context) {
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
                        createNotification(
                            context,
                            title = "Richiesta Film Approvata",
                            message = "La tua richiesta per il Film ${film.titolo} è stata approvata",
                            userId = request.richiedenteId)
                    }
                }

                Log.d("ApproveFilmRequest", "Richiesta film approvata con successo")
            } catch (e: Exception) {
                Log.e("ApproveFilmRequest", "Errore nell'approvazione della richiesta film", e)
            }
        }
    }

    fun approveSerieRequest(requestId: Int, context: Context) {
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
                        createNotification(
                            context,
                            title = "Richiesta Serie TV Approvata",
                            message = "La tua richiesta per la Serie ${serie.titolo} è stata approvata",
                            userId = request.richiedenteId)
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

    fun rejectFilmRequest(context: Context, requestId: Int) {
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

                        createNotification(
                            context,
                            title = "Richiesta Film Rifiutata",
                            message = "La tua richiesta per il Film ${film.titolo} è stata rifiutata",
                            userId = request.richiedenteId)

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

    fun rejectSerieRequest(context: Context, requestId: Int) {
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

                        createNotification(
                            context,
                            title = "Richiesta Serie TV Approvata",
                            message = "La tua richiesta per la Serie ${serie.titolo} è stata approvata",
                            userId = request.richiedenteId)

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

    // Funzione per creare una notifica
    fun createNotification(context: Context, userId: Int, title: String, message: String, type: String = "info") {
        viewModelScope.launch {
            try {
                val notification = Notify(
                    nome = title,
                    contenuto = message,
                )
                notifyRepository.insert(notification)

                // Ottieni l'ID della notifica appena inserita
                val notificationId = notifyRepository.allNotify.first().last().id

                // Crea il collegamento reached_notify
                val reachedNotify = Reached_Notify(
                    id_user = userId,
                    notificaId = notificationId,
                    letta = false
                )
                notifyReachedRepository.insert(reachedNotify)
                showSystemNotification(context, title, message)

                Log.d("CreateNotification", "Notifica creata per utente $userId")
            } catch (e: Exception) {
                Log.e("CreateNotification", "Errore nella creazione della notifica", e)
            }
        }
    }

    // Funzione per segnare una notifica come letta
    fun markNotificationAsRead(userId: Int, notificationId: Int) {
        viewModelScope.launch {
            try {
                notifyReachedRepository.markAsRead(userId, notificationId)
                Log.d("MarkNotification", "Notifica $notificationId segnata come letta per utente $userId")
            } catch (e: Exception) {
                Log.e("MarkNotification", "Errore nel segnare la notifica come letta", e)
            }
        }
    }

    // Funzione per segnare tutte le notifiche come lette per un utente
    fun markAllNotificationsAsRead(userId: Int) {
        viewModelScope.launch {
            try {
                val unreadNotifications = notifyReachedRepository.allNotifyReached.first()
                    .filter { it.id_user == userId && !it.letta }

                unreadNotifications.forEach { reachedNotify ->
                    notifyReachedRepository.markAsRead(userId, reachedNotify.notificaId)
                }

                Log.d("MarkAllNotifications", "Tutte le notifiche segnate come lette per utente $userId")
            } catch (e: Exception) {
                Log.e("MarkAllNotifications", "Errore nel segnare tutte le notifiche come lette", e)
            }
        }
    }

    // Funzione per ottenere le notifiche non lette di un utente
    fun getUnreadNotificationsForUser(userId: Int): Flow<List<Pair<Notify, Reached_Notify>>> {
        return combine(
            notifyRepository.allNotify,
            notifyReachedRepository.allNotifyReached
        ) { notifications, reachedNotifications ->
            val userUnreadReached = reachedNotifications.filter {
                it.id_user == userId && !it.letta
            }

            userUnreadReached.mapNotNull { reached ->
                val notification = notifications.find { it.id == reached.notificaId }
                notification?.let { it to reached }
            }
        }
    }

    private fun showSystemNotification(context: Context, title: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "lookify_channel"
        val channelName = "Lookify Notifiche"


        var channel = notificationManager.getNotificationChannel(channelId)
        if (channel == null) {
            channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.enableLights(true)
            channel.enableVibration(true)
            channel.description = "Notifiche Lookify"
            notificationManager.createNotificationChannel(channel)
        }

        // Costruzione notifica
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // icona valida
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // compatibilità pre-Oreo
            .setDefaults(NotificationCompat.DEFAULT_ALL) // suono + vibrazione + LED
            .setAutoCancel(true)
            .build()

        // Mostra notifica
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    // Funzione per eliminare tutte le notifiche di un utente
    fun deleteAllNotifications(userId: Int) {
        viewModelScope.launch {
            try {
                val userNotifications = notifyReachedRepository.allNotifyReached.first()
                    .filter { it.id_user == userId }

                userNotifications.forEach { reachedNotify ->
                    notifyReachedRepository.deleteNotification(userId, reachedNotify.notificaId)
                }

                Log.d("DeleteAllNotifications", "Tutte le notifiche eliminate per utente $userId")
            } catch (e: Exception) {
                Log.e("DeleteAllNotifications", "Errore nell'eliminare tutte le notifiche", e)
            }
        }
    }

    // Funzione per eliminare una singola notifica
    fun deleteNotification(userId: Int, notificationId: Int) {
        viewModelScope.launch {
            try {
                notifyReachedRepository.deleteNotification(userId, notificationId)
                Log.d("DeleteNotification", "Notifica $notificationId eliminata per utente $userId")
            } catch (e: Exception) {
                Log.e("DeleteNotification", "Errore nell'eliminare notifica $notificationId", e)
            }
        }
    }


}
