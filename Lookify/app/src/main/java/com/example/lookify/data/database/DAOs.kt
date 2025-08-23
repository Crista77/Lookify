package com.example.lookify.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface FilmsDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg film: Film)
    @Query("SELECT * FROM film")
    fun getAll(): Flow<List<Film>>
    @Upsert
    suspend fun upsert(film: Film)
    @Delete
    suspend fun delete(film: Film)
    @Query("DELETE FROM film")
    suspend fun deleteAll()
}

@Dao
interface Film_Watched_DAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg film_watched: FilmWatched)
    @Query("SELECT * FROM film_watched")
    fun getAll(): Flow<List<FilmWatched>>
    @Upsert
    suspend fun upsert(film_watched: FilmWatched)
    @Delete
    suspend fun delete(film_watched: FilmWatched)
}

@Dao
interface Film_Request_DAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg filmRequest: FilmRequest)

    @Query("SELECT * FROM film_request")
    fun getAll(): Flow<List<FilmRequest>>

    @Query("SELECT * FROM film_request WHERE richiedente_id = :userId")
    fun getByUser(userId: Int): Flow<List<FilmRequest>>

    @Query("SELECT * FROM film_request WHERE approvato = :approved")
    fun getByStatus(approved: Boolean): Flow<List<FilmRequest>>

    @Upsert
    suspend fun upsert(filmRequest: FilmRequest)

    @Delete
    suspend fun delete(filmRequest: FilmRequest)

    @Query("UPDATE film_request SET approvato = :approved WHERE id_request = :requestId")
    suspend fun updateApprovalStatus(requestId: Int, approved: Boolean)
}

@Dao
interface Users_DAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg users: Users)
    @Query("SELECT * FROM users")
    fun getAll(): Flow<List<Users>>
    @Upsert
    suspend fun upsert(users: Users)
    @Delete
    suspend fun delete(users: Users)
    @Query("DELETE FROM users")
    suspend fun deleteAll()
}

@Dao
interface SerieTV_DAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg serieTV: SerieTV)
    @Query("SELECT * FROM serie_tv")
    fun getAll(): Flow<List<SerieTV>>
    @Upsert
    suspend fun upsert(serieTV: SerieTV)
    @Delete
    suspend fun delete(serieTV: SerieTV)
    @Query("DELETE FROM serie_tv")
    suspend fun deleteAll()
}

@Dao
interface SerieTV_Watched_DAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg serietvWatched: SerieTV_Watched)
    @Query("SELECT * FROM serie_tv_watched")
    fun getAll(): Flow<List<SerieTV_Watched>>
    @Upsert
    suspend fun upsert(serietvWatched: SerieTV_Watched)
    @Delete
    suspend fun delete(serietvWatched: SerieTV_Watched)
}

@Dao
interface SerieTV_Request_DAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg serieRequest: SerieTV_Request)

    @Query("SELECT * FROM serie_tv_request")
    fun getAll(): Flow<List<SerieTV_Request>>

    @Query("SELECT * FROM serie_tv_request WHERE richiedente_id = :userId")
    fun getByUser(userId: Int): Flow<List<SerieTV_Request>>

    @Query("SELECT * FROM serie_tv_request WHERE approvato = :approved")
    fun getByStatus(approved: Boolean): Flow<List<SerieTV_Request>>

    @Upsert
    suspend fun upsert(serieRequest: SerieTV_Request)

    @Delete
    suspend fun delete(serieRequest: SerieTV_Request)

    @Query("UPDATE serie_tv_request SET approvato = :approved WHERE id_request = :requestId")
    suspend fun updateApprovalStatus(requestId: Int, approved: Boolean)
}

@Dao
interface Trophy_DAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg trophy: Trophy)
    @Query("SELECT * FROM trophy")
    fun getAll(): Flow<List<Trophy>>
    @Upsert
    suspend fun upsert(trophy: Trophy)
    @Delete
    suspend fun delete(trophy: Trophy)
    @Query("DELETE FROM trophy")
    suspend fun deleteAll()
}

@Dao
interface Achievements_Dao {
    @Query("SELECT * FROM achievements")
    fun getAll(): Flow<List<Achievements>>
    @Query("SELECT * FROM achievements WHERE id_user = :userId")
    suspend fun getAchievementsByUser(userId: Int): List<Achievements>
    @Query("SELECT * FROM achievements WHERE id_user = :userId AND trofeoId = :trophyId")
    suspend fun getSpecificAchievement(userId: Int, trophyId: Int): Achievements?
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAchievement(achievement: Achievements): Long
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAchievements(achievements: List<Achievements>)
    @Delete
    suspend fun deleteAchievement(achievement: Achievements)
    @Query("DELETE FROM achievements WHERE id_user = :userId")
    suspend fun deleteAllUserAchievements(userId: Int)
    @Query("SELECT EXISTS(SELECT 1 FROM achievements WHERE id_user = :userId AND trofeoId = :trophyId)")
    suspend fun isTrophyUnlocked(userId: Int, trophyId: Int): Boolean
    @Query("SELECT COUNT(*) FROM achievements WHERE id_user = :userId")
    suspend fun getUserTrophyCount(userId: Int): Int
    @Query("DELETE FROM achievements")
    suspend fun deleteAll()
}

@Dao
interface Notify_DAO {
    @Query("SELECT * FROM notify")
    fun getAll(): Flow<List<Notify>>
    @Upsert
    suspend fun upsert(notify: Notify)
    @Delete
    suspend fun delete(notify: Notify)
}

@Dao
interface Notify_Reached_DAO {
    @Query("SELECT * FROM reached_notify")
    fun getAll(): Flow<List<Reached_Notify>>
    @Upsert
    suspend fun upsert(reachedNotify: Reached_Notify)
    @Delete
    suspend fun delete(reachedNotify: Reached_Notify)
}

@Dao
interface Followers_DAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg followers: Followers)
    @Query("SELECT * FROM followers")
    fun getAll(): Flow<List<Followers>>
    @Upsert
    suspend fun upsert(followers: Followers)
    @Delete
    suspend fun delete(followers: Followers)
}

@Dao
interface Cinema_DAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg cinema: Cinema)
    @Query("SELECT * FROM cinema")
    fun getAll(): Flow<List<Cinema>>
    @Upsert
    suspend fun upsert(cinema: Cinema)
    @Delete
    suspend fun delete(cinema: Cinema)
    @Query("DELETE FROM cinema")
    suspend fun deleteAll()
}

@Dao
interface Cinema_Near_DAO {
    @Query("SELECT * FROM near_cinema")
    fun getAll(): Flow<List<CinemaVicini>>
    @Upsert
    suspend fun upsert(cinemaVicini: CinemaVicini)
    @Delete
    suspend fun delete(cinemaVicini: CinemaVicini)
}

@Dao
interface Actors_DAO {
    @Query("SELECT * FROM actors")
    fun getAll(): Flow<List<Actors>>
    @Upsert
    suspend fun upsert(actors: Actors)
    @Delete
    suspend fun delete(actors: Actors)
}

@Dao
interface Actors_In_Film_DAO {
    @Query("SELECT * FROM actors_in_film")
    fun getAll(): Flow<List<Actors_In_Film>>
    @Upsert
    suspend fun upsert(actorsInFilm: Actors_In_Film)
    @Delete
    suspend fun delete(actorsInFilm: Actors_In_Film)
    @Query("SELECT * FROM actors_in_film WHERE film_id = :filmId")
    fun getByFilmId(filmId: Int): Flow<List<Actors_In_Film>>
}

@Dao
interface Actors_In_Serie_DAO {
    @Query("SELECT * FROM actors_in_serie")
    fun getAll(): Flow<List<Actors_In_Serie>>
    @Upsert
    suspend fun upsert(actorsInSerie: Actors_In_Serie)
    @Delete
    suspend fun delete(actorsInSerie: Actors_In_Serie)
    @Query("SELECT * FROM actors_in_serie WHERE serie_id = :serieId")
    fun getBySerieId(serieId: Int): Flow<List<Actors_In_Serie>>
}

@Dao
interface Platform_DAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg platform: Platform)
    @Query("SELECT * FROM platform")
    fun getAll(): Flow<List<Platform>>
    @Upsert
    suspend fun upsert(platform: Platform)
    @Delete
    suspend fun delete(platform: Platform)
}

@Dao
interface Platform_Film_DAO {
    @Query("SELECT * FROM film_platform")
    fun getAll(): Flow<List<Film_Platform>>
    @Upsert
    suspend fun upsert(filmPlatform: Film_Platform)
    @Delete
    suspend fun delete(filmPlatform: Film_Platform)
    @Query("SELECT * FROM film_platform WHERE film_id = :filmId")
    fun getByFilmId(filmId: Int): Flow<List<Film_Platform>>
}

@Dao
interface Platform_Serie_DAO {
    @Query("SELECT * FROM serie_platform")
    fun getAll(): Flow<List<Serie_Platform>>
    @Upsert
    suspend fun upsert(seriePlatform: Serie_Platform)
    @Delete
    suspend fun delete(seriePlatform: Serie_Platform)
    @Query("SELECT * FROM serie_platform WHERE serie_id = :serieId")
    fun getBySerieId(serieId: Int): Flow<List<Serie_Platform>>
}