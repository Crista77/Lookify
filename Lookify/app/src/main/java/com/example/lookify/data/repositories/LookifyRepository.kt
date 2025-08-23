package com.example.lookify.data.repositories

import com.example.lookify.data.database.*
import kotlinx.coroutines.flow.Flow

class FilmsRepository(private val filmsDAO: FilmsDAO) {
    val allFilms: Flow<List<Film>> = filmsDAO.getAll()
    suspend fun upsert(film: Film) = filmsDAO.upsert(film)
    suspend fun delete(film: Film) = filmsDAO.delete(film)
    suspend fun deleteAll() = filmsDAO.deleteAll()
    suspend fun insert(film: Film) = filmsDAO.insert(film)
}

class FilmWatchedRepository(private val filmWatchedDao: Film_Watched_DAO) {
    val allFilmWatched: Flow<List<FilmWatched>> = filmWatchedDao.getAll()
    suspend fun upsert(fw: FilmWatched) = filmWatchedDao.upsert(fw)
    suspend fun delete(fw: FilmWatched) = filmWatchedDao.delete(fw)
    suspend fun insert(fw: FilmWatched) = filmWatchedDao.insert(fw)
}

class FilmRequestRepository(private val filmRequestDao: Film_Request_DAO) {
    val allFilmRequests: Flow<List<FilmRequest>> = filmRequestDao.getAll()

    fun getFilmRequestsByUser(userId: Int): Flow<List<FilmRequest>> =
        filmRequestDao.getByUser(userId)

    fun getFilmRequestsByStatus(approved: Boolean): Flow<List<FilmRequest>> =
        filmRequestDao.getByStatus(approved)

    suspend fun insert(vararg filmRequest: FilmRequest) {
        filmRequestDao.insert(*filmRequest)
    }

    suspend fun upsert(filmRequest: FilmRequest) {
        filmRequestDao.upsert(filmRequest)
    }

    suspend fun delete(filmRequest: FilmRequest) {
        filmRequestDao.delete(filmRequest)
    }

    suspend fun updateApprovalStatus(requestId: Int, approved: Boolean) {
        filmRequestDao.updateApprovalStatus(requestId, approved)
    }
}

class UsersRepository(private val usersDao: Users_DAO) {
    val allUsers: Flow<List<Users>> = usersDao.getAll()
    suspend fun upsert(user: Users) = usersDao.upsert(user)
    suspend fun delete(user: Users) = usersDao.delete(user)
    suspend fun deleteAll() = usersDao.deleteAll()
    suspend fun insert(user: Users) = usersDao.insert(user)
}

class SerieTVRepository(private val serietvDao: SerieTV_DAO) {
    val allSerieTV: Flow<List<SerieTV>> = serietvDao.getAll()
    suspend fun upsert(tv: SerieTV) = serietvDao.upsert(tv)
    suspend fun delete(tv: SerieTV) = serietvDao.delete(tv)
    suspend fun deleteAll() = serietvDao.deleteAll()
    suspend fun insert(serieTV: SerieTV) = serietvDao.insert(serieTV)

}

class SerieTVWatchedRepository(private val serietvWatchedDao: SerieTV_Watched_DAO) {
    val allSerieTVWatched: Flow<List<SerieTV_Watched>> = serietvWatchedDao.getAll()
    suspend fun upsert(sw: SerieTV_Watched) = serietvWatchedDao.upsert(sw)
    suspend fun delete(sw: SerieTV_Watched) = serietvWatchedDao.delete(sw)
    suspend fun insert(sw: SerieTV_Watched) = serietvWatchedDao.insert(sw)
}

class SerieTVRequestRepository(private val serietvRequestDao: SerieTV_Request_DAO) {
    val allSerieRequests: Flow<List<SerieTV_Request>> = serietvRequestDao.getAll()

    fun getSerieRequestsByUser(userId: Int): Flow<List<SerieTV_Request>> =
        serietvRequestDao.getByUser(userId)

    fun getSerieRequestsByStatus(approved: Boolean): Flow<List<SerieTV_Request>> =
        serietvRequestDao.getByStatus(approved)

    suspend fun insert(vararg serieRequest: SerieTV_Request) {
        serietvRequestDao.insert(*serieRequest)
    }

    suspend fun upsert(serieRequest: SerieTV_Request) {
        serietvRequestDao.upsert(serieRequest)
    }

    suspend fun delete(serieRequest: SerieTV_Request) {
        serietvRequestDao.delete(serieRequest)
    }

    suspend fun updateApprovalStatus(requestId: Int, approved: Boolean) {
        serietvRequestDao.updateApprovalStatus(requestId, approved)
    }
}

class TrophyRepository(private val trophyDao: Trophy_DAO) {
    val allTrophy: Flow<List<Trophy>> = trophyDao.getAll()
    suspend fun upsert(trophy: Trophy) = trophyDao.upsert(trophy)
    suspend fun delete(trophy: Trophy) = trophyDao.delete(trophy)
    suspend fun deleteAll() = trophyDao.deleteAll()
    suspend fun insert(trophy: Trophy) = trophyDao.insert(trophy)
}

class AchievementsRepository(private val achievementsDao: Achievements_Dao) {

    val allAchievements: Flow<List<Achievements>> = achievementsDao.getAll()

    suspend fun deleteAll() = achievementsDao.deleteAll()

    suspend fun getUserAchievements(userId: Int): List<Achievements> =
        achievementsDao.getAchievementsByUser(userId)

    suspend fun insertAchievement(achievement: Achievements): Long =
        achievementsDao.insertAchievement(achievement)

    suspend fun insertMultipleAchievements(achievements: List<Achievements>) =
        achievementsDao.insertAchievements(achievements)

    suspend fun isTrophyUnlocked(userId: Int, trophyId: Int): Boolean =
        achievementsDao.isTrophyUnlocked(userId, trophyId)

    suspend fun getUserTrophyCount(userId: Int): Int =
        achievementsDao.getUserTrophyCount(userId)

    suspend fun unlockTrophies(userId: Int, trophyIds: List<Int>) {
        val achievements = trophyIds.map { trophyId ->
            Achievements(
                id_user = userId,
                trofeoId = trophyId,
                dataConseguimento = System.currentTimeMillis()
            )
        }
        insertMultipleAchievements(achievements)
    }
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
    suspend fun insert(f: Followers) = followersDao.insert(f)
}

class CinemaRepository(private val cinemaDao: Cinema_DAO) {
    val allCinema: Flow<List<Cinema>> = cinemaDao.getAll()
    suspend fun upsert(cinema: Cinema) = cinemaDao.upsert(cinema)
    suspend fun delete(cinema: Cinema) = cinemaDao.delete(cinema)
    suspend fun deleteAll() = cinemaDao.deleteAll()
    suspend fun insert(cinema: Cinema) = cinemaDao.insert(cinema)
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
    fun getByFilmId(filmId: Int): Flow<List<Actors_In_Film>> = actorsInFilmDao.getByFilmId(filmId)
}

class ActorsInSerieRepository(private val actorsInSerieDao: Actors_In_Serie_DAO) {
    val allActorsInSerie: Flow<List<Actors_In_Serie>> = actorsInSerieDao.getAll()
    suspend fun upsert(ais: Actors_In_Serie) = actorsInSerieDao.upsert(ais)
    suspend fun delete(ais: Actors_In_Serie) = actorsInSerieDao.delete(ais)
    fun getBySerieId(serieId: Int): Flow<List<Actors_In_Serie>> = actorsInSerieDao.getBySerieId(serieId)
}

class PlatformRepository(private val platformDao: Platform_DAO) {
    val allPlatforms: Flow<List<Platform>> = platformDao.getAll()
    suspend fun upsert(p: Platform) = platformDao.upsert(p)
    suspend fun delete(p: Platform) = platformDao.delete(p)
    suspend fun insert(platform: Platform) = platformDao.insert(platform)
}

class FilmPlatformRepository(private val platformFilmDao: Platform_Film_DAO) {
    val allPlatformFilm: Flow<List<Film_Platform>> = platformFilmDao.getAll()
    suspend fun upsert(fp: Film_Platform) = platformFilmDao.upsert(fp)
    suspend fun delete(fp: Film_Platform) = platformFilmDao.delete(fp)
    fun getByFilmId(filmId: Int): Flow<List<Film_Platform>> = platformFilmDao.getByFilmId(filmId)
}

class SeriePlatformRepository(private val platformSerieDao: Platform_Serie_DAO) {
    val allPlatformSerie: Flow<List<Serie_Platform>> = platformSerieDao.getAll()
    suspend fun upsert(sp: Serie_Platform) = platformSerieDao.upsert(sp)
    suspend fun delete(sp: Serie_Platform) = platformSerieDao.delete(sp)
    fun getBySerieId(serieId: Int): Flow<List<Serie_Platform>> = platformSerieDao.getBySerieId(serieId)
}

