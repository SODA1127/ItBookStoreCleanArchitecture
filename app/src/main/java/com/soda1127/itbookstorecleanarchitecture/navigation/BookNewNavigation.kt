package com.soda1127.itbookstorecleanarchitecture.navigation

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.tween
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.soda1127.itbookstorecleanarchitecture.screen.main.newtab.BookNewScreen

fun NavGraphBuilder.bookNewGraph(onBookClick: (String, String) -> Unit) {
    composable(
        route = Route.New.route,
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
    ) { BookNewScreen(onBookClick = onBookClick) }
}
