package com.soda1127.itbookstorecleanarchitecture.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

@Composable
fun MainNavHost(
    navController: NavHostController,
    innerPadding: PaddingValues,
    snackState: SnackbarHostState
) {
    // Implementation goes here
    NavHost(navController = navController, startDestination = Route.New.route) {
        bookNewGraph(
            onBookClick = { isbn13, title ->
                navController.navigate(Route.Detail.createRoute(isbn13, title))
            },
            paddingValues = innerPadding
        )

        bookSearchGraph(
            onBookClick = { isbn13, title ->
                navController.navigate(Route.Detail.createRoute(isbn13, title))
            },
            paddingValues = innerPadding
        )

        bookmarkGraph(
            onBookClick = { isbn13, title ->
                navController.navigate(Route.Detail.createRoute(isbn13, title))
            },
            paddingValues = innerPadding
        )

        bookDetailGraph(
            onBackClick = { navController.popBackStack() },
            snackState = snackState
        )
    }
}
