package com.example.lookify.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface FilmsDAO {
    @Query("SELECT * FROM film")
    fun getAll(): Flow<List<Film>>
    @Upsert
    suspend fun upsert(film: Film)
    @Delete
    suspend fun delete(film: Film)
}

@Dao
interface Film_Watched_DAO {
    @Query("SELECT * FROM film_watched")
    fun getAll(): Flow<List<FilmWatched>>
    @Upsert
    suspend fun upsert(film_watched: FilmWatched)
    @Delete
    suspend fun delete(film_watched: FilmWatched)
}

@Dao
interface Film_Request_DAO {
    @Query("SELECT * FROM film_request")
    fun getAll(): Flow<List<FilmRequest>>
    @Upsert
    suspend fun upsert(film_request: FilmRequest)
    @Delete
    suspend fun delete(film_request: FilmRequest)
}

@Dao
interface Users_DAO {
    @Query("SELECT * FROM users")
    fun getAll(): Flow<List<Users>>
    @Upsert
    suspend fun upsert(users: Users)
    @Delete
    suspend fun delete(users: Users)
}

@Dao
interface SerieTV_DAO {
    @Query("SELECT * FROM serie_tv")
    fun getAll(): Flow<List<SerieTV>>
    @Upsert
    suspend fun upsert(serieTV: SerieTV)
    @Delete
    suspend fun delete(serieTV: SerieTV)
}

@Dao
interface SerieTV_Watched_DAO {
    @Query("SELECT * FROM serie_tv_watched")
    fun getAll(): Flow<List<SerieTV_Watched>>
    @Upsert
    suspend fun upsert(serietvWatched: SerieTV_Watched)
    @Delete
    suspend fun delete(serietvWatched: SerieTV_Watched)
}

@Dao
interface SerieTV_Request_DAO {
    @Query("SELECT * FROM serie_tv_request")
    fun getAll(): Flow<List<SerieTV_Request>>
    @Upsert
    suspend fun upsert(serie_tv_request: SerieTV_Request)
    @Delete
    suspend fun delete(serie_tv_request: SerieTV_Request)
}

@Dao
interface Trophy_DAO {
    @Query("SELECT * FROM trophy")
    fun getAll(): Flow<List<Trophy>>
    @Upsert
    suspend fun upsert(trophy: Trophy)
    @Delete
    suspend fun delete(trophy: Trophy)
}

@Dao
interface Achivements_DAO {
    @Query("SELECT * FROM achivements")
    fun getAll(): Flow<List<Achivements>>
    @Upsert
    suspend fun upsert(achivements: Achivements)
    @Delete
    suspend fun delete(achivements: Achivements)
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
    @Query("SELECT * FROM followers")
    fun getAll(): Flow<List<Followers>>
    @Upsert
    suspend fun upsert(followers: Followers)
    @Delete
    suspend fun delete(followers: Followers)
}

@Dao
interface Cinema_DAO {
    @Query("SELECT * FROM cinema")
    fun getAll(): Flow<List<Cinema>>
    @Upsert
    suspend fun upsert(cinema: Cinema)
    @Delete
    suspend fun delete(cinema: Cinema)
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
}

@Dao
interface Platform_DAO {
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
}