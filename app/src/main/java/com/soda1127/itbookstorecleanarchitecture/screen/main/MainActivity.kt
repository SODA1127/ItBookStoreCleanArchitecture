package com.soda1127.itbookstorecleanarchitecture.screen.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.soda1127.itbookstorecleanarchitecture.R
import com.soda1127.itbookstorecleanarchitecture.screen.detail.BookDetailScreen
import com.soda1127.itbookstorecleanarchitecture.screen.main.bookmark.BookmarkScreen
import com.soda1127.itbookstorecleanarchitecture.screen.main.newtab.BookNewScreen
import com.soda1127.itbookstorecleanarchitecture.screen.main.search.SearchScreen
import com.soda1127.itbookstorecleanarchitecture.ui.theme.ItBookStoreTheme
import dagger.hilt.android.AndroidEntryPoint

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

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ItBookStoreTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val bottomNavItems = listOf(
        BottomNavItem.New,
        BottomNavItem.Search,
        BottomNavItem.Bookmark
    )

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            
            // Should show bottom bar only on top level destinations?
            // Usually yes, but depends on design. Assuming yes for now.
            val showBottomBar = bottomNavItems.any { it.route.route == currentDestination?.route }
            
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any { it.route == item.route.route } == true
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = ImageVector.vectorResource(id = item.iconResId),
                                    contentDescription = item.label,
                                    modifier = Modifier.size(24.dp),
                                )
                            },
                            label = { Text(item.label) },
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Route.New.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Route.New.route) {
                BookNewScreen(
                    onBookClick = { isbn13, title ->
                        navController.navigate(Route.Detail.createRoute(isbn13, title))
                    }
                )
            }
            composable(Route.Search.route) {
                SearchScreen(
                    onBookClick = { isbn13, title ->
                        navController.navigate(Route.Detail.createRoute(isbn13, title))
                    }
                )
            }
            composable(Route.Bookmark.route) {
                BookmarkScreen(
                    onBookClick = { isbn13, title ->
                        navController.navigate(Route.Detail.createRoute(isbn13, title))
                    }
                )
            }
            composable(
                route = Route.Detail.route,
                arguments = listOf(
                    navArgument("isbn13") { type = NavType.StringType },
                    navArgument("title") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val isbn13 = backStackEntry.arguments?.getString("isbn13") ?: ""
                val title = backStackEntry.arguments?.getString("title") ?: ""
                BookDetailScreen(
                    isbn13 = isbn13,
                    title = title,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
