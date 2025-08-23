package com.example.lookify.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        Film::class,
        Users::class,
        FilmWatched::class,
        FilmRequest::class,
        SerieTV::class,
        SerieTV_Request::class,
        SerieTV_Watched::class,
        Trophy::class,
        Achievements::class,
        Notify::class,
        Reached_Notify::class,
        Followers::class,
        Cinema::class,
        CinemaVicini::class,
        Actors::class,
        Actors_In_Film::class,
        Platform::class,
        Film_Platform::class,
        Actors_In_Serie::class,
        Serie_Platform::class
    ],
    version = 14
)

abstract class LookifyDatabase : RoomDatabase() {

    abstract fun filmsDAO(): FilmsDAO
    abstract fun filmWatchedDAO(): Film_Watched_DAO
    abstract fun filmRequestDAO(): Film_Request_DAO
    abstract fun usersDAO(): Users_DAO
    abstract fun serieTvDAO(): SerieTV_DAO
    abstract fun serieTvWatchedDAO(): SerieTV_Watched_DAO
    abstract fun serieTvRequestDAO(): SerieTV_Request_DAO
    abstract fun trophyDAO(): Trophy_DAO
    abstract fun achievementsDAO(): Achievements_Dao
    abstract fun notifyDAO(): Notify_DAO
    abstract fun notifyReachedDAO(): Notify_Reached_DAO
    abstract fun followersDAO(): Followers_DAO
    abstract fun cinemaDAO(): Cinema_DAO
    abstract fun cinemaNearDAO(): Cinema_Near_DAO
    abstract fun actorsDAO(): Actors_DAO
    abstract fun actorsInFilmDAO(): Actors_In_Film_DAO
    abstract fun platformDAO(): Platform_DAO
    abstract fun platformFilmDAO(): Platform_Film_DAO
    abstract fun platformSerieDAO(): Platform_Serie_DAO
    abstract fun actorsInSerieDAO(): Actors_In_Serie_DAO

}

