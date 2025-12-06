package com.soda1127.itbookstorecleanarchitecture.navigation

import com.soda1127.itbookstorecleanarchitecture.R

sealed class Route(val route: String) {
    object New : Route("new")
    object Search : Route("search")
    object Bookmark : Route("bookmark")
    object Detail : Route("detail/{isbn13}/{title}") {
        fun createRoute(isbn13: String, title: String) = "detail/$isbn13/$title"
    }
}

sealed class BottomNavItem(val route: Route, val iconResId: Int, val label: String) {
    object New : BottomNavItem(Route.New, R.drawable.ic_home, "New")
    object Search : BottomNavItem(Route.Search, R.drawable.ic_tab_search, "Search")
    object Bookmark : BottomNavItem(Route.Bookmark, R.drawable.ic_tab_bookmark, "Bookmark")
}

fun getTabIndex(route: String?): Int {
    return when (route) {
        Route.New.route -> 0
        Route.Search.route -> 1
        Route.Bookmark.route -> 2
        else -> if (route?.startsWith("detail") == true) 99 else -1
    }
}
