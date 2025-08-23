package com.example.lookify.data.repository

import com.example.lookify.data.database.*
import kotlinx.coroutines.flow.Flow

class FilmsRepository(private val filmsDAO: FilmsDAO) {
    val allFilms: Flow<List<Film>> = filmsDAO.getAll()
    suspend fun upsert(film: Film) = filmsDAO.upsert(film)
    suspend fun delete(film: Film) = filmsDAO.delete(film)
}

class FilmWatchedRepository(private val filmWatchedDao: Film_Watched_DAO) {
    val allFilmWatched: Flow<List<FilmWatched>> = filmWatchedDao.getAll()
    suspend fun upsert(fw: FilmWatched) = filmWatchedDao.upsert(fw)
    suspend fun delete(fw: FilmWatched) = filmWatchedDao.delete(fw)
}

class FilmRequestRepository(private val filmRequestDao: Film_Request_DAO) {
    val allFilmRequest: Flow<List<FilmRequest>> = filmRequestDao.getAll()
    suspend fun upsert(fr: FilmRequest) = filmRequestDao.upsert(fr)
    suspend fun delete(fr: FilmRequest) = filmRequestDao.delete(fr)
}

class UsersRepository(private val usersDao: Users_DAO) {
    val allUsers: Flow<List<Users>> = usersDao.getAll()
    suspend fun upsert(user: Users) = usersDao.upsert(user)
    suspend fun delete(user: Users) = usersDao.delete(user)
}

class SerieTVRepository(private val serietvDao: SerieTV_DAO) {
    val allSerieTV: Flow<List<SerieTV>> = serietvDao.getAll()
    suspend fun upsert(tv: SerieTV) = serietvDao.upsert(tv)
    suspend fun delete(tv: SerieTV) = serietvDao.delete(tv)
}

class SerieTVWatchedRepository(private val serietvWatchedDao: SerieTV_Watched_DAO) {
    val allSerieTVWatched: Flow<List<SerieTV_Watched>> = serietvWatchedDao.getAll()
    suspend fun upsert(sw: SerieTV_Watched) = serietvWatchedDao.upsert(sw)
    suspend fun delete(sw: SerieTV_Watched) = serietvWatchedDao.delete(sw)
}

class SerieTVRequestRepository(private val serietvRequestDao: SerieTV_Request_DAO) {
    val allSerieTVRequest: Flow<List<SerieTV_Request>> = serietvRequestDao.getAll()
    suspend fun upsert(req: SerieTV_Request) = serietvRequestDao.upsert(req)
    suspend fun delete(req: SerieTV_Request) = serietvRequestDao.delete(req)
}

class TrophyRepository(private val trophyDao: Trophy_DAO) {
    val allTrophy: Flow<List<Trophy>> = trophyDao.getAll()
    suspend fun upsert(trophy: Trophy) = trophyDao.upsert(trophy)
    suspend fun delete(trophy: Trophy) = trophyDao.delete(trophy)
}

class AchievementsRepository(private val achivementsDao: Achivements_DAO) {
    val allAchivements: Flow<List<Achivements>> = achivementsDao.getAll()
    suspend fun upsert(a: Achivements) = achivementsDao.upsert(a)
    suspend fun delete(a: Achivements) = achivementsDao.delete(a)
}

class NotifyRepository(private val notifyDao: Notify_DAO) {
    val allNotify: Flow<List<Notify>> = notifyDao.getAll()
    suspend fun upsert(n: Notify) = notifyDao.upsert(n)
    suspend fun delete(n: Notify) = notifyDao.delete(n)
}

class NotifyReachedRepository(private val notifyReachedDao: Notify_Reached_DAO) {
    val allNotifyReached: Flow<List<Reached_Notify>> = notifyReachedDao.getAll()
    suspend fun upsert(nr: Reached_Notify) = notifyReachedDao.upsert(nr)
    suspend fun delete(nr: Reached_Notify) = notifyReachedDao.delete(nr)
}

class FollowersRepository(private val followersDao: Followers_DAO) {
    val allFollowers: Flow<List<Followers>> = followersDao.getAll()
    suspend fun upsert(f: Followers) = followersDao.upsert(f)
    suspend fun delete(f: Followers) = followersDao.delete(f)
}

class CinemaRepository(private val cinemaDao: Cinema_DAO) {
    val allCinema: Flow<List<Cinema>> = cinemaDao.getAll()
    suspend fun upsert(cinema: Cinema) = cinemaDao.upsert(cinema)
    suspend fun delete(cinema: Cinema) = cinemaDao.delete(cinema)
}

class CinemaNearRepository(private val cinemaNearDao: Cinema_Near_DAO) {
    val allCinemaNear: Flow<List<CinemaVicini>> = cinemaNearDao.getAll()
    suspend fun upsert(c: CinemaVicini) = cinemaNearDao.upsert(c)
    suspend fun delete(c: CinemaVicini) = cinemaNearDao.delete(c)
}

class ActorsRepository(private val actorsDao: Actors_DAO) {
    val allActors: Flow<List<Actors>> = actorsDao.getAll()
    suspend fun upsert(a: Actors) = actorsDao.upsert(a)
    suspend fun delete(a: Actors) = actorsDao.delete(a)
}

class ActorsInFilmRepository(private val actorsInFilmDao: Actors_In_Film_DAO) {
    val allActorsInFilm: Flow<List<Actors_In_Film>> = actorsInFilmDao.getAll()
    suspend fun upsert(aif: Actors_In_Film) = actorsInFilmDao.upsert(aif)
    suspend fun delete(aif: Actors_In_Film) = actorsInFilmDao.delete(aif)
}

class PlatformRepository(private val platformDao: Platform_DAO) {
    val allPlatforms: Flow<List<Platform>> = platformDao.getAll()
    suspend fun upsert(p: Platform) = platformDao.upsert(p)
    suspend fun delete(p: Platform) = platformDao.delete(p)
}

class FilmPlatformRepository(private val platformFilmDao: Platform_Film_DAO) {
    val allPlatformFilm: Flow<List<Film_Platform>> = platformFilmDao.getAll()
    suspend fun upsert(fp: Film_Platform) = platformFilmDao.upsert(fp)
    suspend fun delete(fp: Film_Platform) = platformFilmDao.delete(fp)
}
