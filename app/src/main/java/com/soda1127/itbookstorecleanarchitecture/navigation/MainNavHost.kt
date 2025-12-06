package com.soda1127.itbookstorecleanarchitecture.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

@Composable
fun MainNavHost(navController: NavHostController, innerPadding: PaddingValues) {
    // Implementation goes here
    NavHost(
        navController = navController,
        startDestination = Route.New.route,
        modifier = Modifier.padding(innerPadding)
    ) {
        bookNewGraph(
            onBookClick = { isbn13, title ->
                navController.navigate(Route.Detail.createRoute(isbn13, title))
            }
        )

        bookSearchGraph(
            onBookClick = { isbn13, title ->
                navController.navigate(Route.Detail.createRoute(isbn13, title))
            }
        )

        bookmarkGraph(
            onBookClick = { isbn13, title ->
                navController.navigate(Route.Detail.createRoute(isbn13, title))
            }
        )

        bookDetailGraph(onBackClick = { navController.popBackStack() })
    }
}
