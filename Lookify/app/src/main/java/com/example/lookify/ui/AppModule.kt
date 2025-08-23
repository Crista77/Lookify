package com.example.lookify.ui

import com.example.lookify.data.database.LookifyDatabase
import org.koin.dsl.module

class AppModule {
    val appModule = module {

        // DAO bindings
        single { get<LookifyDatabase>().filmsDAO() }
        single { get<LookifyDatabase>().filmWatchedDAO() }
        single { get<LookifyDatabase>().filmRequestDAO() }
        single { get<LookifyDatabase>().usersDAO() }
        single { get<LookifyDatabase>().serieTvDAO() }
        single { get<LookifyDatabase>().serieTvWatchedDAO() }
        single { get<LookifyDatabase>().serieTvRequestDAO() }
        single { get<LookifyDatabase>().trophyDAO() }
        single { get<LookifyDatabase>().achivementsDAO() }
        single { get<LookifyDatabase>().notifyDAO() }
        single { get<LookifyDatabase>().notifyReachedDAO() }
        single { get<LookifyDatabase>().followersDAO() }
        single { get<LookifyDatabase>().cinemaDAO() }
        single { get<LookifyDatabase>().cinemaNearDAO() }
        single { get<LookifyDatabase>().actorsDAO() }
        single { get<LookifyDatabase>().actorsInFilmDAO() }
        single { get<LookifyDatabase>().platformDAO() }
        single { get<LookifyDatabase>().platformFilmDAO() }

    }
}
