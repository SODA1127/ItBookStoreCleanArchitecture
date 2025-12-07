package com.soda1127.itbookstorecleanarchitecture.navigation

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.soda1127.itbookstorecleanarchitecture.screen.main.search.SearchScreen

fun NavGraphBuilder.bookSearchGraph(
    onBookClick: (String, String) -> Unit,
    paddingValues: PaddingValues,
    listState: LazyListState,

    ) {
    composable(
        route = Route.Search.route,
        enterTransition = {
            val initialIndex = getTabIndex(initialState.destination.route)
            val targetIndex = getTabIndex(targetState.destination.route)
            if (initialIndex < targetIndex) {
                slideIntoContainer(SlideDirection.Left, tween(300))
            } else {
                slideIntoContainer(SlideDirection.Right, tween(300))
            }
        },
        exitTransition = {
            val initialIndex = getTabIndex(initialState.destination.route)
            val targetIndex = getTabIndex(targetState.destination.route)
            if (initialIndex < targetIndex) {
                slideOutOfContainer(SlideDirection.Left, tween(300))
            } else {
                slideOutOfContainer(SlideDirection.Right, tween(300))
            }
        },
        popEnterTransition = {
            val initialIndex = getTabIndex(initialState.destination.route)
            val targetIndex = getTabIndex(targetState.destination.route)
            if (initialIndex < targetIndex) {
                slideIntoContainer(SlideDirection.Left, tween(300))
            } else {
                slideIntoContainer(SlideDirection.Right, tween(300))
            }
        },
        popExitTransition = {
            val initialIndex = getTabIndex(initialState.destination.route)
            val targetIndex = getTabIndex(targetState.destination.route)
            if (initialIndex < targetIndex) {
                slideOutOfContainer(SlideDirection.Left, tween(300))
            } else {
                slideOutOfContainer(SlideDirection.Right, tween(300))
            }
        }
    ) {
        SearchScreen(
            paddingValues = paddingValues,
            onBookClick = onBookClick,
            listState = listState,
        )
    }
}
