package com.example.lookify

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.lookify.data.database.Film
import com.example.lookify.data.database.Film_Platform
import com.example.lookify.data.database.LookifyDatabase
import com.example.lookify.ui.LookifyViewModel
import com.example.lookify.data.remote.OSMDataSource
import com.example.lookify.data.repositories.AchievementsRepository
import com.example.lookify.data.repositories.ActorsInFilmRepository
import com.example.lookify.data.repositories.ActorsInSerieRepository
import com.example.lookify.data.repositories.ActorsRepository
import com.example.lookify.data.repositories.CinemaNearRepository
import com.example.lookify.data.repositories.CinemaRepository
import com.example.lookify.data.repositories.FilmPlatformRepository
import com.example.lookify.data.repositories.FilmRequestRepository
import com.example.lookify.data.repositories.FilmWatchedRepository
import com.example.lookify.data.repositories.FilmsRepository
import com.example.lookify.data.repositories.FollowersRepository
import com.example.lookify.data.repositories.NotifyReachedRepository
import com.example.lookify.data.repositories.NotifyRepository
import com.example.lookify.data.repositories.PlatformRepository
import com.example.lookify.data.repositories.SeriePlatformRepository
import com.example.lookify.data.repositories.SerieTVRepository
import com.example.lookify.data.repositories.SerieTVRequestRepository
import com.example.lookify.data.repositories.SerieTVWatchedRepository
import com.example.lookify.data.repositories.SettingsRepository
import com.example.lookify.data.repositories.TrophyRepository
import com.example.lookify.data.repositories.UsersRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val Context.dataStore by preferencesDataStore("settings")

val appModule = module {
    // Preferences
    single { get<Context>().dataStore }

    // Database
    single {
        Room.databaseBuilder(
            get(),
            LookifyDatabase::class.java,
            "travel-diary"
        ).fallbackToDestructiveMigration().build()
    }

    // HTTP Client
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
    }

    single{ SettingsRepository(get())}

    single { OSMDataSource(get()) }

    single { get<LookifyDatabase>().filmsDAO() }
    single { get<LookifyDatabase>().filmWatchedDAO() }
    single { get<LookifyDatabase>().filmRequestDAO() }
    single { get<LookifyDatabase>().usersDAO() }
    single { get<LookifyDatabase>().serieTvDAO() }
    single { get<LookifyDatabase>().serieTvWatchedDAO() }
    single { get<LookifyDatabase>().serieTvRequestDAO() }
    single { get<LookifyDatabase>().trophyDAO() }
    single { get<LookifyDatabase>().achievementsDAO() }
    single { get<LookifyDatabase>().notifyDAO() }
    single { get<LookifyDatabase>().notifyReachedDAO() }
    single { get<LookifyDatabase>().followersDAO() }
    single { get<LookifyDatabase>().cinemaDAO() }
    single { get<LookifyDatabase>().cinemaNearDAO() }
    single { get<LookifyDatabase>().actorsDAO() }
    single { get<LookifyDatabase>().actorsInFilmDAO() }
    single { get<LookifyDatabase>().platformDAO() }
    single { get<LookifyDatabase>().platformFilmDAO() }
    single { get<LookifyDatabase>().platformSerieDAO() }
    single { get<LookifyDatabase>().actorsInSerieDAO() }

    single { FilmsRepository(get()) }
    single { FilmWatchedRepository(get()) }
    single { FilmRequestRepository(get()) }
    single { UsersRepository(get()) }
    single { SerieTVRepository(get()) }
    single { SerieTVWatchedRepository(get()) }
    single { SerieTVRequestRepository(get()) }
    single { TrophyRepository(get()) }
    single { AchievementsRepository(get()) }
    single { NotifyRepository(get()) }
    single { NotifyReachedRepository(get()) }
    single { FollowersRepository(get()) }
    single { CinemaRepository(get()) }
    single { CinemaNearRepository(get()) }
    single { ActorsRepository(get()) }
    single { ActorsInFilmRepository(get()) }
    single { PlatformRepository(get()) }
    single { FilmPlatformRepository(get()) }
    single { ActorsInSerieRepository(get()) }
    single { SeriePlatformRepository(get()) }

    // ViewModel
    viewModel {
        LookifyViewModel(
            filmsRepository = get(),
            filmWatchedRepository = get(),
            filmRequestRepository = get(),
            usersRepository = get(),
            serieTVRepository = get(),
            serieTVWatchedRepository = get(),
            serieTVRequestRepository = get(),
            trophyRepository = get(),
            achievementsRepository = get(),
            notifyRepository = get(),
            notifyReachedRepository = get(),
            followersRepository = get(),
            cinemaRepository = get(),
            cinemaNearRepository = get(),
            actorsRepository = get(),
            actorsInFilmRepository = get(),
            platformRepository = get(),
            filmPlatformRepository = get(),
            seriePlatformRepository = get(),
            actorsInSerieRepository = get()
        )
    }
}
