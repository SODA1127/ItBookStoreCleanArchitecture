package com.soda1127.itbookstorecleanarchitecture.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.soda1127.itbookstorecleanarchitecture.screen.detail.BookDetailScreen

fun NavGraphBuilder.bookDetailGraph(
    onBackClick: () -> Unit,
    snackState: SnackbarHostState
) {
    composable(
        route = Route.Detail.route,
        arguments = listOf(
            navArgument("isbn13") { type = NavType.StringType },
            navArgument("title") { type = NavType.StringType }
        ),
        enterTransition = {
            scaleIn(
                initialScale = 0.8f,
                animationSpec = tween(300)
            ) + fadeIn(
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            scaleOut(
                targetScale = 0.8f,
                animationSpec = tween(300)
            ) + fadeOut(
                animationSpec = tween(300)
            )
        },
        popEnterTransition = {
            scaleIn(
                initialScale = 0.8f,
                animationSpec = tween(300)
            ) + fadeIn(
                animationSpec = tween(300)
            )
        },
        popExitTransition = {
            scaleOut(
                targetScale = 0.8f,
                animationSpec = tween(300)
            ) + fadeOut(
                animationSpec = tween(300)
            )
        }
    ) { backStackEntry ->
        val isbn13 = backStackEntry.arguments?.getString("isbn13") ?: ""
        val title = backStackEntry.arguments?.getString("title") ?: ""
        BookDetailScreen(
            isbn13 = isbn13,
            title = title,
            onBackClick = onBackClick,
            snackState = snackState,
        )
    }
}
