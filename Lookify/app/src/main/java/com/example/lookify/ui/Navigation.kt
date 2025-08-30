package com.example.lookify.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.toRoute
import com.example.lookify.data.repositories.UsersRepository
import com.example.lookify.ui.LookifyViewModel
import com.example.lookify.ui.screens.admin.AdminRequestDetailScreen
import com.example.lookify.ui.screens.admin.AdminRequestsPreviewScreen
import com.example.lookify.ui.screens.admin.AdminUserScreen
import com.example.lookify.ui.screens.admin.InsertFilmScreen
import com.example.lookify.ui.screens.cinema.CinemaScreen
import com.example.lookify.ui.screens.home.HomeScreen
import com.example.lookify.ui.screens.login.LoginScreen
import com.example.lookify.ui.screens.notification.NotificationsScreen
import com.example.lookify.ui.screens.registration.RegistrationScreen
import com.example.lookify.ui.screens.registration.TestCameraScreen
import com.example.lookify.ui.screens.search.FilmScreen
import com.example.lookify.ui.screens.search.SearchScreen
import com.example.lookify.ui.screens.user.OtherUserScreen
import com.example.lookify.ui.screens.user.RequestScreen
import com.example.lookify.ui.screens.user.UserScreen
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import java.io.Serial

sealed interface LookifyRoute {
    @Serializable data object Home : LookifyRoute
    @Serializable data object SearchFilm : LookifyRoute
    @Serializable data object Login : LookifyRoute
    @Serializable data object Registration : LookifyRoute
    @Serializable data object Cinema : LookifyRoute
    @Serializable data object SearchSerie : LookifyRoute
    @Serializable data object SearchUser : LookifyRoute
    @Serializable data object Users : LookifyRoute
    @Serializable data object AdminUser : LookifyRoute
    @Serializable data object Insert : LookifyRoute
    @Serializable data object Request : LookifyRoute
    @Serializable data object AdminRequestPrev : LookifyRoute
    @Serializable data object AdminRequestDetailScreen : LookifyRoute
    @Serializable data object Notification : LookifyRoute
    @Serializable data object OtherUser: LookifyRoute
    @Serializable data object Film: LookifyRoute

}

@Composable
fun LookifyNavGraph(navController: NavHostController, userRepo: UsersRepository) {
    val lookifyVm = koinViewModel<LookifyViewModel>()
    val lookifyState by lookifyVm.state.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = LookifyRoute.Login
    ) {
        composable<LookifyRoute.Home> {
            HomeScreen(lookifyState, navController)
        }

        composable<LookifyRoute.SearchFilm> {
            SearchScreen(lookifyState, navController, "Film")
        }

        composable<LookifyRoute.SearchSerie> {
            SearchScreen(lookifyState, navController, "TV")
        }

        composable<LookifyRoute.SearchUser> {
            SearchScreen(lookifyState, navController, "Users")
        }

        composable<LookifyRoute.Login> {
            LoginScreen(
                state = lookifyState,
                navController = navController
            )
        }

        composable<LookifyRoute.Registration> {
            //TestCameraScreen()
            val coroutineScope = rememberCoroutineScope()
            RegistrationScreen(lookifyState, navController,
                onRegister = { user ->
                coroutineScope.launch { userRepo.insert(user) }
            })
        }

        composable<LookifyRoute.Cinema> {
            CinemaScreen(lookifyState, navController)
        }

        composable(
            route = "${LookifyRoute.Users}?userId={userId}",
            arguments = listOf(
                navArgument("userId") {
                    type = NavType.IntType
                    defaultValue = -1 // se non passato, userId = -1
                }
            )
        ) { backStackEntry ->
            val userIdFromNav = backStackEntry.arguments?.getInt("userId")?.takeIf { it != -1 }

            LaunchedEffect(userIdFromNav) {
                if (lookifyState.currentUserId == null && userIdFromNav != null) {
                    lookifyState.currentUserId = userIdFromNav
                }
            }

            UserScreen(
                state = lookifyState,
                navController = navController
            )
        }

        composable(
            route = "${LookifyRoute.AdminUser}?userId={userId}",
            arguments = listOf(
                navArgument("userId") {
                    type = NavType.IntType
                    defaultValue = -1 // se non passato, userId = -1
                }
            )
        ) { backStackEntry ->
            val userIdFromNav = backStackEntry.arguments?.getInt("userId")?.takeIf { it != -1 }

            LaunchedEffect(userIdFromNav) {
                if (lookifyState.currentUserId == null && userIdFromNav != null) {
                    lookifyState.currentUserId = userIdFromNav
                }
            }

            AdminUserScreen(
                state = lookifyState,
                navController = navController
            )
        }


        composable<LookifyRoute.Insert> {
            InsertFilmScreen(lookifyState, navController, lookifyVm)
        }

        composable<LookifyRoute.Request> {
            RequestScreen(lookifyState, navController, lookifyVm)
        }

        composable<LookifyRoute.AdminRequestPrev> {
            AdminRequestsPreviewScreen(lookifyState, navController, lookifyVm)
        }

        composable(
            route = "${LookifyRoute.AdminRequestDetailScreen}/{requestType}/{requestId}",
            arguments = listOf(
                navArgument("requestType") { type = NavType.StringType },
                navArgument("requestId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val requestType = backStackEntry.arguments?.getString("requestType") ?: "film"
            val requestId = backStackEntry.arguments?.getInt("requestId") ?: 0

            AdminRequestDetailScreen(
                state = lookifyState,
                navController = navController,
                viewModel = lookifyVm,
                requestType = requestType,
                requestId = requestId
            )
        }

        composable(
            route = "${LookifyRoute.Notification}?userId={userId}",
        arguments = listOf(
            navArgument("userId") {
                type = NavType.IntType
                defaultValue = -1
            }
        )
        ) { backStackEntry ->
            val userIdFromNav = backStackEntry.arguments?.getInt("userId")?.takeIf { it != -1 }
            LaunchedEffect(userIdFromNav) {
                if (lookifyState.currentUserId == null && userIdFromNav != null) {
                    lookifyState.currentUserId = userIdFromNav
                }
            }

            NotificationsScreen(lookifyState, navController, lookifyVm)
        }

        composable(
            route = "${LookifyRoute.OtherUser}?userId={userId}&currentUserId={currentUserId}",
            arguments = listOf(
                navArgument("userId") {
                    type = NavType.IntType
                    defaultValue = -1
                },
                navArgument("currentUserId") {   // 👈 nuovo argomento
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val otherUserId = backStackEntry.arguments?.getInt("userId") ?: -1
            val currentUserId = backStackEntry.arguments?.getInt("currentUserId") ?: -1

            currentUserId.takeIf { it != -1 }?.let {
                OtherUserScreen(
                    state = lookifyState,
                    navController = navController,
                    lookifyViewModel = lookifyVm,
                    otherUserId = otherUserId,
                    currentUserId = it // 👈 evita -1
                )
            }
        }

        composable(
            route = "${LookifyRoute.Film}?filmId={filmId}&currentUserId={currentUserId}",
            arguments = listOf(
                navArgument("filmId") {
                    type = NavType.IntType
                    defaultValue = -1
                },
                navArgument("currentUserId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val filmId = backStackEntry.arguments?.getInt("filmId") ?: -1
            val currentUserId = backStackEntry.arguments?.getInt("currentUserId") ?: -1

            FilmScreen(
                state = lookifyState,
                navController = navController,
                lookifyViewModel = lookifyVm,
                filmId = filmId,
                currentUserId = currentUserId
            )
        }


    }
}
