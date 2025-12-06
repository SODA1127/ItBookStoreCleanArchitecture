package com.soda1127.itbookstorecleanarchitecture.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun BottomNavigationBar(
    bottomNavItems: List<BottomNavItem>,
    navController: NavHostController,
    currentDestination: NavDestination?,
) {
    NavigationBar {
        bottomNavItems.forEachIndexed { index, item ->
            val selected =
                currentDestination?.hierarchy?.any { it.route == item.route.route } == true
            NavigationTabItem(
                selected = selected,
                item = item,
                navController = navController,
            )
        }
    }
}

@Composable
fun RowScope.NavigationTabItem(
    selected: Boolean,
    item: BottomNavItem,
    navController: NavHostController,
) {
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
            if (!selected) {
                navController.navigate(item.route.route) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationBarPreview() {
    val bottomNavItems = listOf(BottomNavItem.New, BottomNavItem.Search, BottomNavItem.Bookmark)
    BottomNavigationBar(
        bottomNavItems = bottomNavItems,
        navController = rememberNavController(), // Keep navController for navigation logic in preview
        currentDestination = null, // Set currentDestination to null for a generic preview without selection
    )
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationBarItemPreview() {
    val bottomNavItem = BottomNavItem.New
    Row(modifier = Modifier.wrapContentSize()) {
        NavigationTabItem(
            selected = true,
            item = bottomNavItem,
            navController = rememberNavController(), // Keep navController for navigation logic in
        )
    }
}
