package com.soda1127.itbookstorecleanarchitecture.screen.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.soda1127.itbookstorecleanarchitecture.navigation.BottomNavigationBar
import com.soda1127.itbookstorecleanarchitecture.navigation.MainNavHost
import com.soda1127.itbookstorecleanarchitecture.ui.theme.ItBookStoreTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ItBookStoreTheme { MainScreen() } }
    }
}

@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    // State from ViewModel
    val bottomNavItems = viewModel.bottomNavItems

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            val showBottomBar =
                bottomNavItems.any { it.route.route == currentDestination?.route }

            if (showBottomBar) {
                BottomNavigationBar(
                    bottomNavItems = bottomNavItems,
                    navController = navController,
                    currentDestination = currentDestination,
                    onTabSelected = { index -> viewModel.onTabSelected(index) }
                )
            }
        }
    ) { innerPadding -> MainNavHost(navController = navController, innerPadding = innerPadding) }
}
