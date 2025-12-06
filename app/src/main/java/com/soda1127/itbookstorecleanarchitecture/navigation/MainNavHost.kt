package com.soda1127.itbookstorecleanarchitecture.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import kotlinx.coroutines.launch

@Composable
fun MainNavHost(
    navController: NavHostController,
    innerPadding: PaddingValues,
    snackState: SnackbarHostState,
    listState: LazyListState
) {
    val scope = rememberCoroutineScope()
    // Implementation goes here
    NavHost(navController = navController, startDestination = Route.New.route) {
        bookNewGraph(
            onBookClick = { isbn13, title ->
                navController.navigate(Route.Detail.createRoute(isbn13, title))
            },
            paddingValues = innerPadding,
            listState = listState,
        )

        bookSearchGraph(
            onBookClick = { isbn13, title ->
                navController.navigate(Route.Detail.createRoute(isbn13, title))
            },
            paddingValues = innerPadding,
            listState = listState,
        )

        bookmarkGraph(
            onBookClick = { isbn13, title ->
                navController.navigate(Route.Detail.createRoute(isbn13, title))
            },
            paddingValues = innerPadding,
            listState = listState,
        )

        bookDetailGraph(
            onBackClick = { navController.popBackStack() },
            onShowSnackbar = { message ->
                scope.launch {
                    snackState.showSnackbar(message, duration = SnackbarDuration.Short)
                }
            }
        )
    }
}
